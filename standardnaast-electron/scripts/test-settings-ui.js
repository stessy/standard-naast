const { app, BrowserWindow, ipcMain } = require('electron');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const os = require('node:os');
const { createSettingsStore, createSettingsWindow } = require('../settings');

const root = fs.mkdtempSync(path.join(os.tmpdir(), 'naast-settings-ui-'));
app.setPath('userData', root);
let testWindow;
const timer = setTimeout(() => finish(new Error('Settings UI timeout')), 15000);

function finish(error) {
  clearTimeout(timer);
  if (error) console.error(error);
  for (const window of BrowserWindow.getAllWindows()) window.destroy();
  fs.rmSync(root, { recursive: true, force: true });
  app.exit(error ? 1 : 0);
}

app.whenReady().then(async () => {
  const store = createSettingsStore(root);
  const chosen = path.join(root, 'sauvegardes été');
  const chosenLogs = path.join(root, 'journaux applicatifs');
  const dialog = {
    showOpenDialog: async (_window, options) => ({
      canceled: false,
      filePaths: [options.title.includes('logs') ? chosenLogs : chosen]
    }),
    showErrorBox: (_title, message) => { throw new Error(message); }
  };
  let rejectSave = true;
  const show = createSettingsWindow({
    BrowserWindow: function (options) {
      testWindow = new BrowserWindow({ ...options, show: false });
      return testWindow;
    },
    ipcMain, dialog,
    store: {
      get: () => store.get(),
      save: settings => {
        if (rejectSave) throw new Error('Dossier inaccessible pour le test.');
        return store.save(settings);
      }
    }
  });
  show();
  while (!await testWindow.webContents.executeJavaScript(
    'Boolean(document.getElementById("save") && !document.getElementById("save").disabled)'
  )) await new Promise(resolve => setTimeout(resolve, 50));
  assert.equal(await testWindow.webContents.executeJavaScript('document.getElementById("logsDir").value'), store.get().logsDir);
  await testWindow.webContents.executeJavaScript('document.querySelector("[data-folder=backupsDir]").click()');
  while (await testWindow.webContents.executeJavaScript('document.getElementById("backupsDir").value') !== chosen) {
    await new Promise(resolve => setTimeout(resolve, 50));
  }
  assert.notEqual(store.get().backupsDir, chosen, 'Selection must not save until confirmed');
  await testWindow.webContents.executeJavaScript('document.querySelector("[data-folder=logsDir]").click()');
  while (await testWindow.webContents.executeJavaScript('document.getElementById("logsDir").value') !== chosenLogs) {
    await new Promise(resolve => setTimeout(resolve, 50));
  }
  await testWindow.webContents.executeJavaScript('document.getElementById("settings-form").requestSubmit()');
  while (!await testWindow.webContents.executeJavaScript(
    'document.getElementById("message").textContent.includes("Dossier inaccessible") && !document.getElementById("save").disabled'
  )) await new Promise(resolve => setTimeout(resolve, 50));
  assert.equal(testWindow.isDestroyed(), false, 'Failed save must keep the window open');
  assert.notEqual(store.get().backupsDir, chosen);
  rejectSave = false;
  const closed = new Promise(resolve => testWindow.once('closed', resolve));
  await testWindow.webContents.executeJavaScript('document.getElementById("settings-form").requestSubmit()');
  await closed;
  assert.equal(testWindow.isDestroyed(), true, 'Successful save must close the window');
  assert.equal(createSettingsStore(root).get().backupsDir, chosen);
  assert.equal(createSettingsStore(root).get().logsDir, chosenLogs);
  const saved = store.get();
  for (const action of ['cancel', 'escape']) {
    show();
    while (!await testWindow.webContents.executeJavaScript(
      'Boolean(document.getElementById("save") && !document.getElementById("save").disabled)'
    )) await new Promise(resolve => setTimeout(resolve, 50));
    await testWindow.webContents.executeJavaScript(`
      document.getElementById('backupsDir').value = ${JSON.stringify(path.join(root, 'unsaved'))};
    `);
    const dismissed = new Promise(resolve => testWindow.once('closed', resolve));
    if (action === 'cancel') {
      assert.equal(await testWindow.webContents.executeJavaScript(`
        (() => {
          const button = document.getElementById('cancel');
          const rect = button.getBoundingClientRect();
          return rect.width > 0 && rect.height > 0 && rect.bottom <= window.innerHeight;
        })()
      `), true, 'Cancel button must be visible within the window');
      await testWindow.webContents.executeJavaScript('document.getElementById("cancel").click()');
    } else {
      testWindow.webContents.sendInputEvent({ type: 'keyDown', keyCode: 'Escape' });
    }
    await dismissed;
    assert.equal(testWindow.isDestroyed(), true);
    assert.deepEqual(createSettingsStore(root).get(), saved, 'Dismissal must not save changes');
  }
  console.log('PASS: settings save, error handling, visible Cancel button and Escape dismiss without saving');
}).then(() => finish(), finish);
