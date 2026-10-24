const { app, BrowserWindow, ipcMain } = require('electron');
const assert = require('node:assert/strict');
const { EventEmitter } = require('node:events');
const { PassThrough } = require('node:stream');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const vm = require('node:vm');

const directory = path.resolve(__dirname, '..');
const userData = fs.mkdtempSync(path.join(os.tmpdir(), 'naast-admin-ui-'));
app.setPath('userData', userData);

async function test() {
  let hasUsers = true;
  let latestWindow;
  let creations = 0;
  const source = fs.readFileSync(path.join(directory, 'main.js'), 'utf8');
  const context = vm.createContext({
    BrowserWindow: function (options) {
      latestWindow = new BrowserWindow({ ...options, show: false, parent: undefined });
      return latestWindow;
    },
    ipcMain, path, fs, process, Buffer, setTimeout, clearTimeout,
    app,
    __dirname: directory,
    nativeExecutable: path.join(directory, 'missing-test-native'),
    javaExecutable: path.join(directory, 'missing-test-java'),
    jarFile: '/test/backend.jar',
    PG_PORT: 5432,
    mainWindow: null,
    splashWindow: null,
    updateSplashStatus() {},
    spawn(command, args) {
      assert.equal(command, 'java');
      assert.ok(!args.some(value => value.includes('12345678')));
      const child = new EventEmitter();
      child.stdin = new PassThrough();
      child.stdout = new PassThrough();
      child.stderr = new PassThrough();
      child.kill = () => {};
      let input = '';
      child.stdin.on('data', data => { input += data.toString(); });
      child.stdin.on('finish', () => {
        setImmediate(() => {
          if (args.includes('--create-initial-admin')) {
            assert.equal(input, 'andré\n12345678\n');
            creations++;
            hasUsers = true;
            child.stdout.emit('data', Buffer.from('ADMIN_SETUP:CREATED\n'));
          } else {
            assert.equal(input, '');
            child.stdout.emit('data', Buffer.from(hasUsers ? 'ADMIN_SETUP:EXISTS\n' : 'ADMIN_SETUP:REQUIRED\n'));
          }
          child.emit('close', 0);
        });
      });
      return child;
    }
  });
  vm.runInContext(source.slice(source.indexOf('function runInitialAdminCommand('),
    source.indexOf('// 4. Création des fenêtres')), context);
  await context.ensureInitialAdmin();
  assert.equal(latestWindow, undefined, 'Existing users must skip the dialog');

  hasUsers = false;
  const setup = context.ensureInitialAdmin();
  const deadline = Date.now() + 10000;
  while (!latestWindow || !await latestWindow.webContents.executeJavaScript(
    'Boolean(document.getElementById("setup") && window.adminSetup)'
  )) {
    assert.ok(Date.now() < deadline, 'Setup form did not load');
    await new Promise(resolve => setTimeout(resolve, 50));
  }
  await latestWindow.webContents.executeJavaScript(`
    document.getElementById('username').value = 'andré';
    document.getElementById('password').value = '12345678';
    document.getElementById('confirmation').value = 'different-password';
    document.getElementById('setup').requestSubmit();
  `);
  assert.match(await latestWindow.webContents.executeJavaScript(
    'document.getElementById("error").textContent'), /ne correspondent pas/);
  assert.equal(creations, 0);
  await latestWindow.webContents.executeJavaScript(`
    document.getElementById('confirmation').value = '12345678';
    document.getElementById('setup').requestSubmit();
  `);
  await setup;
  assert.equal(creations, 1);
  await context.ensureInitialAdmin();
  assert.equal(creations, 1, 'Restart must not create another account');
  console.log('PASS: existing account, isolated setup form, confirmation, UTF-8 credentials over stdin, restart');
}

app.whenReady().then(test).then(() => {
  fs.rmSync(userData, { recursive: true, force: true });
  app.exit(0);
}, error => {
  console.error(error);
  for (const window of BrowserWindow.getAllWindows()) window.destroy();
  fs.rmSync(userData, { recursive: true, force: true });
  app.exit(1);
});
