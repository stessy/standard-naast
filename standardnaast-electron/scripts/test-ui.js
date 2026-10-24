const { app, BrowserWindow } = require('electron');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const http = require('node:http');
const os = require('node:os');
const path = require('node:path');

const uiDir = path.resolve(__dirname, '../../standardnaast-ng/dist/standardnaast-ng/browser');
const webUiDir = path.resolve(__dirname, '../../standardnaast-ng/dist/standardnaast-ng-web/browser');
const userData = fs.mkdtempSync(path.join(os.tmpdir(), 'naast-ui-test-'));
app.setPath('userData', userData);

async function waitForLogin(window) {
  const deadline = Date.now() + 15000;
  while (Date.now() < deadline) {
    if (await window.webContents.executeJavaScript(
      `Boolean(document.querySelector("app-login #username")
        && document.querySelector("app-login button[type=submit]")
        && (location.protocol === 'file:' ? location.hash.split('?')[0] === '#/login' : location.pathname === '/login'))`
    )) return;
    await new Promise(resolve => setTimeout(resolve, 100));
  }
  throw new Error(`Login form did not render: ${window.webContents.getURL()}`);
}

async function reload(window) {
  const loaded = new Promise(resolve => window.webContents.once('did-finish-load', resolve));
  window.reload();
  await loaded;
  await waitForLogin(window);
}

async function test() {
  assert.ok(fs.existsSync(path.join(uiDir, 'index.html')), 'Run the Angular build:electron script first');
  assert.ok(fs.existsSync(path.join(webUiDir, 'index.html')),
    'Build the web frontend with --output-path dist/standardnaast-ng-web first');
  const window = new BrowserWindow({
    show: false,
    webPreferences: { nodeIntegration: false, contextIsolation: true }
  });
  const errors = [];
  window.webContents.on('console-message', details => {
    if (details.level === 'error') errors.push(details.message);
  });
  window.webContents.on('did-fail-load', (_event, code, description, url) => {
    errors.push(`${code}: ${description}: ${url}`);
  });
  let server;
  try {
    await window.loadFile(path.join(uiDir, 'index.html'));
    await waitForLogin(window);
    assert.equal(new URL(window.webContents.getURL()).hash.split('?')[0], '#/login');
    await reload(window);
    assert.equal(new URL(window.webContents.getURL()).pathname,
      new URL(`file://${path.join(uiDir, 'index.html')}`).pathname);
    await window.webContents.executeJavaScript('location.hash = "/members"');
    await waitForLogin(window);
    assert.equal(new URL(window.webContents.getURL()).hash.split('?')[0], '#/login',
      'Protected route must redirect to login');
    assert.deepEqual(errors, [], 'File renderer errors');
    console.log('PASS: file assets, lazy login, protected route and reload');

    server = http.createServer((request, response) => {
      const pathname = new URL(request.url, 'http://localhost').pathname;
      const file = path.join(webUiDir, pathname);
      const resource = fs.existsSync(file) && fs.statSync(file).isFile()
        ? file : path.join(webUiDir, 'index.html');
      const types = { '.js': 'text/javascript', '.css': 'text/css', '.html': 'text/html' };
      response.setHeader('Content-Type', types[path.extname(resource)] || 'application/octet-stream');
      fs.createReadStream(resource).pipe(response);
    });
    await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
    await window.loadURL(`http://127.0.0.1:${server.address().port}`);
    await waitForLogin(window);
    assert.equal(new URL(window.webContents.getURL()).pathname, '/login');
    assert.equal(new URL(window.webContents.getURL()).hash, '');
    await reload(window);
    assert.deepEqual(errors, [], 'HTTP renderer errors');
    console.log('PASS: HTTP path routing and reload remain functional');
  } finally {
    window.destroy();
    if (server) await new Promise(resolve => server.close(resolve));
  }
}

function finish(code) {
  fs.rmSync(userData, { recursive: true, force: true });
  app.exit(code);
}

app.whenReady().then(test).then(() => finish(0), error => {
  console.error(error);
  finish(1);
});
