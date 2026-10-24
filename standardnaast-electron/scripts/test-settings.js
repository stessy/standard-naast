const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { EventEmitter } = require('node:events');
const vm = require('node:vm');
const { createSettingsStore, createSettingsWindow } = require('../settings');

function directory(t) {
  const result = fs.mkdtempSync(path.join(os.tmpdir(), 'naast-settings-'));
  t.after(() => fs.rmSync(result, { recursive: true, force: true }));
  return result;
}

test('defaults, validation, persistence and preservation of old files', t => {
  const root = directory(t);
  const store = createSettingsStore(root);
  assert.deepEqual(store.get(), { backupsDir: path.join(root, 'backups'), logsDir: path.join(root, 'logs') });
  fs.mkdirSync(store.get().backupsDir);
  const oldBackup = path.join(store.get().backupsDir, 'backup_old.sql');
  fs.writeFileSync(oldBackup, 'old backup');
  const next = { backupsDir: path.join(root, 'sauvegardes été'), logsDir: path.join(root, 'logs " spéciaux $') };
  store.save(next);
  assert.deepEqual(createSettingsStore(root).get(), next);
  assert.equal(fs.readFileSync(oldBackup, 'utf8'), 'old backup');
  assert.ok(fs.statSync(next.logsDir).isDirectory());
  const copy = store.get();
  copy.logsDir = 'changed';
  assert.deepEqual(store.get(), next);
  assert.throws(() => store.save({ ...next, logsDir: 'relative' }), /absolus/);
  assert.throws(() => store.save({ ...next, logsDir: oldBackup }));
  assert.deepEqual(store.get(), next);
  assert.deepEqual(createSettingsStore(root).get(), next);
});

test('corrupt settings are surfaced rather than silently reset', t => {
  const root = directory(t);
  fs.writeFileSync(path.join(root, 'settings.json'), 'not JSON');
  assert.throws(() => createSettingsStore(root), SyntaxError);
});

test('backup and retention use the current chosen folder without shell interpolation', t => {
  const root = directory(t);
  const store = createSettingsStore(root);
  const chosen = path.join(root, 'backup " été $');
  store.save({ ...store.get(), backupsDir: chosen });
  const source = fs.readFileSync(path.join(__dirname, '..', 'main.js'), 'utf8');
  let invocation;
  const context = vm.createContext({
    fs, path, settingsStore: store, PG_PORT: 5432,
    pgDumpExecutable: path.join(root, 'pg_dump'),
    console: { log() {}, warn() {}, error() {} },
    getPostgresEnv: () => ({}),
    execFileSync(command, args) {
      invocation = { command, args };
      fs.writeFileSync(args.at(-1), 'SQL backup');
    }
  });
  vm.runInContext(source.slice(source.indexOf('function backupDatabase()'),
    source.indexOf('// 6. Arrêt des services')), context);
  assert.equal(context.backupDatabase(), true);
  assert.equal(invocation.command, 'pg_dump');
  assert.equal(path.dirname(invocation.args.at(-1)), chosen);
  assert.equal(fs.readFileSync(invocation.args.at(-1), 'utf8'), 'SQL backup');
  const second = path.join(root, 'second');
  store.save({ ...store.get(), backupsDir: second });
  for (let i = 0; i < 17; i++) fs.writeFileSync(path.join(second, `backup_old_${i}.sql`), '');
  context.cleanOldBackups(15);
  assert.equal(fs.readdirSync(second).length, 15);
  assert.equal(fs.readdirSync(chosen).length, 1);
  context.execFileSync = () => { throw new Error('Permission denied'); };
  assert.equal(context.backupDatabase(), false);
});

test('window is isolated, single instance, checks IPC origin and cleans up handlers', async t => {
  const store = createSettingsStore(directory(t));
  const windows = [];
  class Window extends EventEmitter {
    constructor(options) {
      super();
      this.options = options;
      this.webContents = new EventEmitter();
      this.webContents.mainFrame = {};
      this.webContents.setWindowOpenHandler = handler => { this.openHandler = handler; };
      windows.push(this);
    }
    focus() { this.focused = true; }
    loadFile(file) { this.file = file; return Promise.resolve(); }
  }
  const handlers = new Map();
  const ipcMain = {
    handle: (key, handler) => handlers.set(key, handler),
    removeHandler: key => handlers.delete(key)
  };
  let canceled = true;
  const dialog = { showOpenDialog: async () => ({ canceled, filePaths: [store.get().backupsDir] }) };
  const show = createSettingsWindow({ BrowserWindow: Window, ipcMain, dialog, store });
  show();
  show();
  assert.equal(windows.length, 1);
  const window = windows[0];
  assert.ok(window.focused);
  assert.equal(window.options.webPreferences.nodeIntegration, false);
  assert.equal(window.options.webPreferences.contextIsolation, true);
  assert.equal(window.options.webPreferences.sandbox, true);
  assert.deepEqual(window.openHandler(), { action: 'deny' });
  const event = { sender: window.webContents, senderFrame: window.webContents.mainFrame };
  await assert.rejects(handlers.get('settings:get')({ ...event, sender: {} }), /autorisée/);
  await assert.rejects(handlers.get('settings:save')({ ...event, senderFrame: {} }), /autorisée/);
  assert.deepEqual((await handlers.get('settings:get')(event)).value, store.get());
  assert.equal((await handlers.get('settings:choose')(event, 'backupsDir')).value, null);
  canceled = false;
  assert.equal((await handlers.get('settings:choose')(event, 'backupsDir')).value, store.get().backupsDir);
  assert.equal((await handlers.get('settings:save')(event, store.get())).success, true);
  window.emit('closed');
  assert.equal(handlers.size, 0);
  show();
  assert.equal(windows.length, 2);
});
