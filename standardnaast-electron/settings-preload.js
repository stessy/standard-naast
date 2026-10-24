const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('settings', {
  get: () => ipcRenderer.invoke('settings:get'),
  choose: key => ipcRenderer.invoke('settings:choose', key),
  save: settings => ipcRenderer.invoke('settings:save', settings)
});
