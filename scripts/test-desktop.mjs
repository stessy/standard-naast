import assert from 'node:assert/strict';
import { spawn } from 'node:child_process';
import { once } from 'node:events';
import { mkdtemp, readFile, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import path from 'node:path';
import { setTimeout as delay } from 'node:timers/promises';
import { after, before, test } from 'node:test';

const jar = path.resolve(process.argv[2] ?? 'standardnaast-backend/target/standardnaast-backend-1.0.0-SNAPSHOT.jar');
const launcher = process.env.DESKTOP_LAUNCHER;
const java = process.env.JAVA ?? 'java';
let directory;
let application;
let baseUrl;
let token;
let memberId;
let output = '';

function startProcess(command, args, options = {}) {
  const child = spawn(command, args, { stdio: ['pipe', 'pipe', 'pipe'], ...options });
  child.stdout.on('data', data => { output += data; });
  child.stderr.on('data', data => { output += data; });
  child.on('error', error => { output += error.stack; });
  return child;
}

async function waitForExit(child, timeout = 15000) {
  if (child.exitCode !== null) {
    assert.equal(child.exitCode, 0, output);
    return;
  }
  const [code] = await once(child, 'exit', { signal: AbortSignal.timeout(timeout) });
  assert.equal(code, 0, output);
}

function desktop(args) {
  return launcher
    ? startProcess(launcher, args)
    : startProcess(java, ['-jar', jar, ...args]);
}

async function start() {
  output = '';
  application = desktop(['--desktop', '--desktop-no-browser', `--desktop-data-dir=${directory}`]);
  for (let attempt = 0; attempt < 240; attempt++) {
    assert.equal(application.exitCode, null, output);
    const ready = output.match(/DESKTOP_READY (http:\/\/127\.0\.0\.1:\d+\/)/);
    if (ready) {
      baseUrl = ready[1];
      const health = await fetch(`${baseUrl}actuator/health`);
      assert.equal(health.status, 200);
      assert.equal((await health.json()).status, 'UP');
      return;
    }
    await delay(250);
  }
  assert.fail(`Desktop startup timed out:\n${output}`);
}

async function stop() {
  const child = desktop(['--desktop-stop', `--desktop-data-dir=${directory}`, '--desktop-no-browser']);
  await waitForExit(child);
  await waitForExit(application);
}

async function login() {
  const response = await fetch(`${baseUrl}api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'desktop-test', password: 'local-test-password' })
  });
  assert.equal(response.status, 200, await response.clone().text());
  token = (await response.json()).accessToken;
  assert.ok(token);
}

before(async () => {
  directory = await mkdtemp(path.join(tmpdir(), 'standardnaast-desktop-test-'));
  await start();
  const setup = startProcess(java, ['-jar', jar, '--create-initial-admin'], {
    env: {
      ...process.env,
      LOG_DIR: path.join(directory, 'logs'),
      SPRING_DATASOURCE_URL: `jdbc:sqlite:${path.join(directory, 'standardnaast.sqlite')}`,
      SPRING_DATASOURCE_USERNAME: '',
      SPRING_DATASOURCE_PASSWORD: ''
    }
  });
  setup.stdin.end('desktop-test\nlocal-test-password\n');
  await waitForExit(setup);
});

after(async () => {
  if (application && application.exitCode === null) {
    application.kill('SIGTERM');
    await once(application, 'exit', { signal: AbortSignal.timeout(15000) });
  }
  if (directory) {
    await rm(directory, { recursive: true });
  }
});

test('embedded Angular, deep links and API security remain distinct', async () => {
  for (const route of ['', 'login', 'members/123', 'seasons']) {
    const response = await fetch(`${baseUrl}${route}`);
    assert.equal(response.status, 200, route);
    assert.match(response.headers.get('content-type'), /text\/html/);
    assert.match(await response.text(), /<app-root/);
  }
  const index = await (await fetch(baseUrl)).text();
  const script = index.match(/src="([^"]+\.js)"/)[1];
  const javascript = await fetch(`${baseUrl}${script}`);
  assert.equal(javascript.status, 200);
  assert.match(javascript.headers.get('content-type'), /javascript/);
  const missingAsset = await fetch(`${baseUrl}missing-file.js`);
  assert.equal(missingAsset.status, 404);
  assert.doesNotMatch(await missingAsset.text(), /<app-root/);
  assert.equal((await fetch(`${baseUrl}api/members`)).status, 401);
  assert.equal((await fetch(`${baseUrl}api/desktop/stop`, { method: 'POST' })).status, 403);
  const invalid = await fetch(`${baseUrl}api/auth/login`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'desktop-test', password: 'wrong-password' })
  });
  assert.equal(invalid.status, 401);
  await login();
  const missingApi = await fetch(`${baseUrl}api/missing-endpoint`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  assert.equal(missingApi.status, 404);
  assert.doesNotMatch(await missingApi.text(), /<app-root/);
});

test('member creation and updates round-trip through the real SQLite database', async () => {
  await login();
  const headers = { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' };
  const data = { name: 'Desktop test', firstname: 'SQLite', birthdate: '1980-02-29', student: false, redCard: true };
  const created = await fetch(`${baseUrl}api/members`, { method: 'POST', headers, body: JSON.stringify(data) });
  assert.equal(created.status, 201, await created.clone().text());
  const member = await created.json();
  memberId = member.id;
  assert.ok(memberId > 0);
  const loaded = await (await fetch(`${baseUrl}api/members/${memberId}`, { headers })).json();
  assert.equal(loaded.birthdate, data.birthdate);
  assert.equal(loaded.student, false);
  assert.equal(loaded.redCard, true);
  const updated = await fetch(`${baseUrl}api/members/${memberId}`, {
    method: 'PUT', headers, body: JSON.stringify({ ...data, name: 'Desktop persisted' })
  });
  assert.equal(updated.status, 200);
  assert.equal((await updated.json()).name, 'Desktop persisted');
});

test('season amounts and PDF generation work with the packaged runtime', async () => {
  const headers = { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' };
  const season = await fetch(`${baseUrl}api/seasons`, {
    method: 'POST', headers,
    body: JSON.stringify({
      id: '2026-2027', dateStart: '2026-07-01', dateEnd: '2027-06-30',
      european: false, montantCotisation: 15.50
    })
  });
  assert.equal(season.status, 201, await season.clone().text());
  const saved = await (await fetch(`${baseUrl}api/seasons/2026-2027`, { headers })).json();
  assert.equal(saved.montantCotisation, 15.50);
  const pdfPath = `${baseUrl}api/member-cotisations/pdf?seasonId=2026-2027`;
  assert.equal((await fetch(pdfPath)).status, 401);
  const report = await fetch(pdfPath, { headers });
  assert.equal(report.status, 200, await report.clone().text());
  assert.match(report.headers.get('content-type'), /application\/pdf/);
  const bytes = Buffer.from(await report.arrayBuffer());
  assert.equal(bytes.subarray(0, 5).toString(), '%PDF-');
  assert.ok(bytes.length > 1000);
});

test('single instance, clean stop and restart preserve data and credentials', async () => {
  const original = await readFile(path.join(directory, 'desktop.properties'), 'utf8');
  const second = desktop(['--desktop', '--desktop-no-browser', `--desktop-data-dir=${directory}`]);
  await waitForExit(second);
  assert.equal(await readFile(path.join(directory, 'desktop.properties'), 'utf8'), original);
  await stop();
  await start();
  await login();
  const headers = { Authorization: `Bearer ${token}` };
  const response = await fetch(`${baseUrl}api/members/${memberId}`, { headers });
  assert.equal(response.status, 200);
  assert.equal((await response.json()).name, 'Desktop persisted');
  const deleted = await fetch(`${baseUrl}api/members/${memberId}`, { method: 'DELETE', headers });
  assert.equal(deleted.status, 204);
  assert.equal((await fetch(`${baseUrl}api/seasons/2026-2027`, { headers })).status, 200);
  assert.equal((await fetch(`${baseUrl}api/seasons/2026-2027`, { method: 'DELETE', headers })).status, 204);
  await stop();
});
