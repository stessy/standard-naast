const fs = require('node:fs');
const path = require('node:path');

function createSettingsStore(userData) {
  const settingsFile = path.join(userData, 'settings.json');
  const defaults = {
    backupsDir: path.join(userData, 'backups'),
    logsDir: path.join(userData, 'logs')
  };

  function validate(settings) {
    if (!settings || typeof settings !== 'object' || Array.isArray(settings)) {
      throw new Error('Paramètres invalides.');
    }
    for (const key of Object.keys(defaults)) {
      if (typeof settings[key] !== 'string' || !path.isAbsolute(settings[key]) || settings[key].includes('\0')) {
        throw new Error('Les dossiers doivent être des chemins absolus valides.');
      }
    }
    return { backupsDir: settings.backupsDir, logsDir: settings.logsDir };
  }

  let current = defaults;
  if (fs.existsSync(settingsFile)) {
    current = validate(JSON.parse(fs.readFileSync(settingsFile, 'utf8')));
  }

  return {
    get: () => ({ ...current }),
    save(settings) {
      const next = validate(settings);
      for (const directory of Object.values(next)) {
        fs.mkdirSync(directory, { recursive: true });
        if (!fs.statSync(directory).isDirectory()) throw new Error(`Ce chemin n'est pas un dossier : ${directory}`);
        fs.accessSync(directory, fs.constants.W_OK | fs.constants.X_OK);
      }
      fs.mkdirSync(userData, { recursive: true });
      const temporaryFile = `${settingsFile}.tmp`;
      try {
        fs.writeFileSync(temporaryFile, JSON.stringify(next, null, 2), { mode: 0o600 });
        fs.renameSync(temporaryFile, settingsFile);
      } finally {
        fs.rmSync(temporaryFile, { force: true });
      }
      current = next;
      return { ...current };
    }
  };
}

function createSettingsWindow({ BrowserWindow, dialog, ipcMain, store }) {
  let window = null;
  return function showSettings(parent) {
    if (window) {
      window.focus();
      return;
    }
    window = new BrowserWindow({
      width: 740,
      height: 530,
      resizable: false,
      parent,
      modal: true,
      autoHideMenuBar: true,
      title: 'Paramètres',
      webPreferences: {
        preload: path.join(__dirname, 'settings-preload.js'),
        nodeIntegration: false,
        contextIsolation: true,
        sandbox: true
      }
    });
    const channels = ['settings:get', 'settings:choose', 'settings:save'];
    function handle(channel, action) {
      ipcMain.handle(channel, async (event, value) => {
        if (!window || event.sender !== window.webContents || event.senderFrame !== window.webContents.mainFrame) {
          throw new Error('Requête non autorisée.');
        }
        try {
          return { success: true, value: await action(value) };
        } catch (error) {
          console.error('[Paramètres]:', error);
          return { success: false, error: error.message };
        }
      });
    }
    handle('settings:get', () => store.get());
    handle('settings:choose', async key => {
      if (key !== 'backupsDir' && key !== 'logsDir') throw new Error('Paramètre inconnu.');
      const selection = await dialog.showOpenDialog(window, {
        title: key === 'backupsDir' ? 'Dossier des sauvegardes PostgreSQL' : 'Dossier des logs applicatifs',
        defaultPath: store.get()[key],
        properties: ['openDirectory', 'createDirectory']
      });
      return selection.canceled ? null : selection.filePaths[0];
    });
    handle('settings:save', value => store.save(value));
    window.webContents.setWindowOpenHandler(() => ({ action: 'deny' }));
    window.webContents.on('will-navigate', event => event.preventDefault());
    window.on('closed', () => {
      for (const channel of channels) ipcMain.removeHandler(channel);
      window = null;
    });
    window.loadFile(path.join(__dirname, 'settings.html')).catch(error => {
      console.error('[Paramètres]: Impossible de charger la fenêtre.', error);
      dialog.showErrorBox('Erreur de paramètres', error.message);
      if (window) window.destroy();
    });
  };
}

module.exports = { createSettingsStore, createSettingsWindow };
