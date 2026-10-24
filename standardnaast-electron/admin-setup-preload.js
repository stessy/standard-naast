const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('adminSetup', {
  create: credentials => ipcRenderer.invoke('initial-admin:create', credentials)
});
