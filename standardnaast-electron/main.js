const { app, BrowserWindow, dialog, ipcMain, Menu } = require('electron');
const path = require('path');
const { spawn, execSync } = require('child_process');
const http = require('http');
const net = require('net');
const fs = require('fs');

let splashWindow = null;
let mainWindow = null;
let springProcess = null;
let isQuitting = false;

// Ports de communication
const BACKEND_PORT = 8080;
const PG_PORT = 5432;

// Détermination des répertoires de ressources
const isPackaged = app.isPackaged;
const resourcesDir = isPackaged
  ? process.resourcesPath
  : path.join(__dirname, 'resources');

// Chemins du JRE et du JAR
const platformDir = `${process.platform}-${process.arch}`;
const jreBinDir = isPackaged
  ? path.join(resourcesDir, 'jre', 'bin')
  : path.join(resourcesDir, 'jre', platformDir, 'bin');

const javaExecutable = process.platform === 'win32'
  ? path.join(jreBinDir, 'java.exe')
  : path.join(jreBinDir, 'java');

const jarFile = isPackaged
  ? path.join(resourcesDir, 'backend', 'standardnaast-backend.jar')
  : path.join(__dirname, '..', 'standardnaast-backend', 'target', 'standardnaast-backend-1.0.0-SNAPSHOT.jar');

// Chemins PostgreSQL
const pgBinDir = isPackaged
  ? path.join(resourcesDir, 'postgres', 'bin')
  : path.join(resourcesDir, 'postgres', platformDir, 'bin');

const pgDataDir = path.join(app.getPath('userData'), 'postgres_data');
const backupsDir = path.join(app.getPath('userData'), 'backups');
const migrationStatusFile = path.join(app.getPath('userData'), 'migration_h2_status.json');

const initdbExecutable = process.platform === 'win32' ? path.join(pgBinDir, 'initdb.exe') : path.join(pgBinDir, 'initdb');
const pgCtlExecutable = process.platform === 'win32' ? path.join(pgBinDir, 'pg_ctl.exe') : path.join(pgBinDir, 'pg_ctl');
const createdbExecutable = process.platform === 'win32' ? path.join(pgBinDir, 'createdb.exe') : path.join(pgBinDir, 'createdb');
const pgDumpExecutable = process.platform === 'win32' ? path.join(pgBinDir, 'pg_dump.exe') : path.join(pgBinDir, 'pg_dump');

// Fonction pour envoyer un message à la fenêtre splash
function updateSplashStatus(message) {
  console.log(`[Status]: ${message}`);
  if (splashWindow && !splashWindow.isDestroyed()) {
    splashWindow.webContents.send('splash-status', message);
  }
}

// Vérifier si un port est ouvert
function checkPortInUse(port) {
  return new Promise((resolve) => {
    const server = net.createServer();
    server.once('error', (err) => {
      if (err.code === 'EADDRINUSE') {
        resolve(true);
      } else {
        resolve(false);
      }
    });
    server.once('listening', () => {
      server.close();
      resolve(false);
    });
    server.listen(port, '127.0.0.1');
  });
}

// 1. Démarrage de PostgreSQL
async function startPostgreSQL() {
  updateSplashStatus('Vérification de la base de données PostgreSQL...');

  const isPortUsed = await checkPortInUse(PG_PORT);
  if (isPortUsed) {
    console.log(`Le port ${PG_PORT} est déjà utilisé. On suppose que PostgreSQL est déjà actif.`);
    return;
  }

  // Vérification de la présence des binaires
  const hasEmbeddedPostgres = fs.existsSync(pgCtlExecutable);
  const pgCtlCmd = hasEmbeddedPostgres ? pgCtlExecutable : 'pg_ctl';
  const initdbCmd = hasEmbeddedPostgres ? initdbExecutable : 'initdb';

  if (!fs.existsSync(pgDataDir)) {
    updateSplashStatus('Initialisation de la base de données...');
    fs.mkdirSync(pgDataDir, { recursive: true });
    try {
      execSync(`"${initdbCmd}" -D "${pgDataDir}" -U standardnaast -A trust --encoding=UTF8`, {
        windowsHide: true
      });
      console.log('Cluster PostgreSQL initialisé avec succès.');
    } catch (e) {
      console.error('Erreur lors de initdb:', e);
    }
  }

  updateSplashStatus('Démarrage du moteur PostgreSQL...');
  const logFile = path.join(app.getPath('userData'), 'postgres.log');

  try {
    execSync(`"${pgCtlCmd}" -D "${pgDataDir}" -l "${logFile}" -o "-p ${PG_PORT}" start`, {
      windowsHide: true
    });
    console.log('PostgreSQL démarré.');
  } catch (e) {
    console.warn('Tentative de démarrage pg_ctl :', e.message);
  }

  // Attente que Postgres écoute sur le port
  let attempts = 0;
  while (attempts < 20) {
    const listening = await checkPortInUse(PG_PORT);
    if (listening) break;
    await new Promise((r) => setTimeout(r, 500));
    attempts++;
  }

  // Création de la base de données 'standardnaast' si inexistante
  try {
    const createdbCmd = hasEmbeddedPostgres ? createdbExecutable : 'createdb';
    execSync(`"${createdbCmd}" -p ${PG_PORT} -U standardnaast standardnaast`, {
      windowsHide: true,
      stdio: 'ignore'
    });
    console.log("Base 'standardnaast' créée ou déjà existante.");
  } catch (e) {
    // Erreur normale si la base existe déjà
  }
}

// 2. Exécution de la Migration H2 -> PostgreSQL
function executeH2Migration(h2FilePath) {
  return new Promise((resolve, reject) => {
    updateSplashStatus(`Migration des données H2 vers PostgreSQL...`);
    const javaCmd = fs.existsSync(javaExecutable) ? javaExecutable : 'java';

    if (!fs.existsSync(jarFile)) {
      return reject(new Error(`Fichier JAR backend introuvable : ${jarFile}`));
    }

    console.log(`[Migration H2]: Lancement de la migration pour ${h2FilePath}`);
    const args = [
      '-jar',
      jarFile,
      `--migrate-from-h2=${h2FilePath}`,
      `--pg-url=jdbc:postgresql://localhost:${PG_PORT}/standardnaast`,
      '--pg-user=standardnaast',
      '--pg-password=standardnaast_password'
    ];

    const proc = spawn(javaCmd, args, { windowsHide: true });

    proc.stdout.on('data', (data) => {
      const text = data.toString();
      console.log(`[Migration Output]: ${text}`);
      const lines = text.split('\n').map(l => l.trim()).filter(l => l.length > 0);
      for (const line of lines) {
        if (line.includes('[MIGRATION]')) {
          updateSplashStatus(line.replace('[MIGRATION]', '').trim());
        }
      }
    });

    proc.stderr.on('data', (data) => {
      console.error(`[Migration ERROR]: ${data.toString()}`);
    });

    proc.on('close', (code) => {
      if (code === 0) {
        console.log('[Migration H2]: Migration terminée avec succès.');
        try {
          fs.writeFileSync(migrationStatusFile, JSON.stringify({
            migrated: true,
            date: new Date().toISOString(),
            sourceH2File: h2FilePath
          }, null, 2));
        } catch (err) {
          console.warn('Impossible de sauvegarder le fichier de statut de migration:', err);
        }
        resolve();
      } else {
        reject(new Error(`Échec de la migration H2 (code d'erreur ${code})`));
      }
    });
  });
}

// Vérification et proposition de migration lors du premier démarrage
async function checkAndPerformInitialH2Migration() {
  // Vérifier si un argument en ligne de commande force la migration
  const migrateArg = process.argv.find(arg => arg.startsWith('--migrate-from-h2=') || arg.startsWith('--h2='));
  if (migrateArg) {
    const customH2Path = migrateArg.split('=')[1];
    if (customH2Path && fs.existsSync(customH2Path)) {
      await executeH2Migration(customH2Path);
      return;
    }
  }

  // Si la migration a déjà été effectuée ou refusée, ignorer
  if (fs.existsSync(migrationStatusFile)) {
    return;
  }

  // Recherche d'une base H2 existante dans les répertoires usuels
  const potentialH2Paths = [
    path.join(__dirname, '..', 'data', 'standardnaast.mv.db'),
    path.join(process.cwd(), 'data', 'standardnaast.mv.db'),
    path.join(app.getPath('userData'), 'standardnaast.mv.db'),
    path.join(app.getPath('home'), '.standardnaast', 'standardnaast.mv.db'),
    path.join(app.getPath('documents'), 'standardnaast.mv.db')
  ];

  let detectedH2File = null;
  for (const candidate of potentialH2Paths) {
    if (fs.existsSync(candidate)) {
      detectedH2File = candidate;
      break;
    }
  }

  // Proposer à l'utilisateur de migrer les données
  let buttons = ['Parcourir pour sélectionner une base H2...', 'Démarrer avec une base vierge'];
  let defaultId = 0;
  let message = "C'est la première fois que vous lancez Standard de Naast.\n\nSouhaitez-vous migrer vos données depuis une ancienne base de données H2 (.mv.db) vers PostgreSQL ?";

  if (detectedH2File) {
    buttons = ['Migrer la base H2 détectée', 'Choisir un autre fichier...', 'Base vierge'];
    message = `Une ancienne base H2 a été détectée :\n${detectedH2File}\n\nSouhaitez-vous migrer ces données vers PostgreSQL ?`;
  }

  const choice = dialog.showMessageBoxSync({
    type: 'question',
    buttons: buttons,
    defaultId: defaultId,
    cancelId: buttons.length - 1,
    title: 'Migration des données - Premier lancement',
    message: 'Initialisation de la base de données',
    detail: message
  });

  let selectedH2File = null;

  if (detectedH2File && choice === 0) {
    selectedH2File = detectedH2File;
  } else if ((detectedH2File && choice === 1) || (!detectedH2File && choice === 0)) {
    const fileSelection = dialog.showOpenDialogSync({
      title: 'Sélectionner la base de données H2 (.mv.db)',
      properties: ['openFile'],
      filters: [
        { name: 'Base de données H2 (*.mv.db, *.db)', extensions: ['mv.db', 'db'] },
        { name: 'Tous les fichiers', extensions: ['*'] }
      ]
    });

    if (fileSelection && fileSelection.length > 0) {
      selectedH2File = fileSelection[0];
    }
  }

  if (selectedH2File) {
    try {
      await executeH2Migration(selectedH2File);
      dialog.showMessageBoxSync({
        type: 'info',
        title: 'Migration réussie',
        message: 'Migration terminée avec succès',
        detail: 'Toutes les données de la base H2 ont été importées dans PostgreSQL avec succès.'
      });
    } catch (err) {
      dialog.showErrorBox('Erreur de migration', `La migration a échoué :\n${err.message}\nL'application continuera avec le schéma actuel.`);
    }
  } else {
    // L'utilisateur a choisi une base vierge ou a annulé
    try {
      fs.writeFileSync(migrationStatusFile, JSON.stringify({
        migrated: false,
        skipped: true,
        date: new Date().toISOString()
      }, null, 2));
    } catch (e) {}
  }
}

// 3. Démarrage du Backend Spring Boot
function startSpringBoot() {
  return new Promise((resolve, reject) => {
    updateSplashStatus('Démarrage du serveur backend Spring Boot...');

    const javaCmd = fs.existsSync(javaExecutable) ? javaExecutable : 'java';
    console.log(`Utilisation de Java : ${javaCmd}`);
    console.log(`Utilisation du JAR : ${jarFile}`);

    if (!fs.existsSync(jarFile)) {
      return reject(new Error(`Fichier backend introuvable : ${jarFile}`));
    }

    const env = Object.assign({}, process.env, {
      SPRING_PROFILES_ACTIVE: 'prod',
      SPRING_DATASOURCE_URL: `jdbc:postgresql://localhost:${PG_PORT}/standardnaast`,
      SPRING_DATASOURCE_USERNAME: 'standardnaast',
      SPRING_DATASOURCE_PASSWORD: 'standardnaast_password',
      SERVER_PORT: `${BACKEND_PORT}`,
      APP_JWT_SECRET: 'yourSuperSecretKeyForProductionMustBeAtLeast256BitsLongAndSecure'
    });

    springProcess = spawn(javaCmd, ['-jar', jarFile], {
      env,
      windowsHide: true
    });

    springProcess.stdout.on('data', (data) => {
      console.log(`[Spring Boot]: ${data}`);
    });

    springProcess.stderr.on('data', (data) => {
      console.error(`[Spring Boot ERROR]: ${data}`);
    });

    springProcess.on('exit', (code) => {
      console.log(`Processus Spring Boot terminé avec le code ${code}`);
      if (!isQuitting && code !== 0) {
        dialog.showErrorBox(
          'Erreur Serveur',
          `Le serveur backend s'est arrêté de manière inattendue (code ${code}).`
        );
      }
    });

    // Sondage du health-check de Spring Boot
    let pollCount = 0;
    const maxPolls = 60; // 60 secondes max

    const checkHealth = () => {
      pollCount++;
      updateSplashStatus(`Attente de l'initialisation du serveur (${pollCount}s)...`);

      http.get(`http://localhost:${BACKEND_PORT}/actuator/health`, (res) => {
        if (res.statusCode === 200) {
          console.log('Spring Boot est prêt !');
          resolve();
        } else {
          scheduleNextPoll();
        }
      }).on('error', () => {
        scheduleNextPoll();
      });
    };

    const scheduleNextPoll = () => {
      if (pollCount >= maxPolls) {
        reject(new Error("Délai d'attente dépassé pour le démarrage de Spring Boot."));
      } else {
        setTimeout(checkHealth, 1000);
      }
    };

    setTimeout(checkHealth, 2000);
  });
}

// 4. Création des fenêtres et Menus
function buildApplicationMenu() {
  const isMac = process.platform === 'darwin';
  const template = [
    ...(isMac ? [{
      label: app.name,
      submenu: [
        { role: 'about', label: 'À propos de Standard de Naast' },
        { type: 'separator' },
        { role: 'services' },
        { type: 'separator' },
        { role: 'hide', label: 'Masquer Standard de Naast' },
        { role: 'hideOthers', label: 'Masquer les autres' },
        { role: 'unhide', label: 'Tout afficher' },
        { type: 'separator' },
        { role: 'quit', label: 'Quitter Standard de Naast' }
      ]
    }] : []),
    {
      label: 'Fichier',
      submenu: [
        isMac ? { role: 'close', label: 'Fermer la fenêtre' } : { role: 'quit', label: 'Quitter' }
      ]
    },
    {
      label: 'Édition',
      submenu: [
        { role: 'undo', label: 'Annuler' },
        { role: 'redo', label: 'Rétablir' },
        { type: 'separator' },
        { role: 'cut', label: 'Couper' },
        { role: 'copy', label: 'Copier' },
        { role: 'paste', label: 'Coller' },
        { role: 'selectAll', label: 'Tout sélectionner' }
      ]
    },
    {
      label: 'Outils',
      submenu: [
        {
          label: 'Effectuer une sauvegarde manuelle...',
          click: () => {
            backupDatabase();
            dialog.showMessageBoxSync({
              type: 'info',
              title: 'Sauvegarde',
              message: 'Sauvegarde effectuée avec succès',
              detail: `Le fichier de sauvegarde a été enregistré dans le dossier : ${backupsDir}`
            });
          }
        },
        {
          label: 'Migrer des données depuis une base H2...',
          click: async () => {
            const fileSelection = dialog.showOpenDialogSync({
              title: 'Sélectionner le fichier H2 (.mv.db)',
              properties: ['openFile'],
              filters: [{ name: 'Base H2 (*.mv.db, *.db)', extensions: ['mv.db', 'db'] }]
            });
            if (fileSelection && fileSelection.length > 0) {
              try {
                await executeH2Migration(fileSelection[0]);
                dialog.showMessageBoxSync({
                  type: 'info',
                  title: 'Migration réussie',
                  message: 'Migration terminée',
                  detail: 'Les données H2 ont été migrées vers PostgreSQL. Veuillez redémarrer pour recharger les données.'
                });
              } catch (e) {
                dialog.showErrorBox('Erreur de migration', e.message);
              }
            }
          }
        }
      ]
    },
    {
      label: 'Affichage',
      submenu: [
        { role: 'reload', label: 'Actualiser' },
        { role: 'forceReload', label: 'Actualisation forcée' },
        { role: 'toggleDevTools', label: 'Outils de développement' },
        { type: 'separator' },
        { role: 'resetZoom', label: 'Taille réelle' },
        { role: 'zoomIn', label: 'Zoom avant' },
        { role: 'zoomOut', label: 'Zoom arrière' },
        { type: 'separator' },
        { role: 'togglefullscreen', label: 'Plein écran' }
      ]
    }
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

function createSplashWindow() {
  splashWindow = new BrowserWindow({
    width: 480,
    height: 320,
    frame: false,
    transparent: true,
    alwaysOnTop: true,
    center: true,
    resizable: false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  splashWindow.loadFile(path.join(__dirname, 'splash.html'));
}

function createMainWindow() {
  mainWindow = new BrowserWindow({
    width: 1366,
    height: 868,
    minWidth: 1024,
    minHeight: 700,
    title: 'Standard de Naast',
    show: false,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true
    }
  });

  buildApplicationMenu();

  // Chargement de l'application (servie par Spring Boot ou packagée dans ui/)
  const uiLocalPath = path.join(__dirname, 'ui', 'browser', 'index.html');
  if (fs.existsSync(uiLocalPath)) {
    // Si UI embarquée directement dans Electron
    mainWindow.loadFile(uiLocalPath);
  } else {
    // Si servie via le serveur web Spring Boot
    mainWindow.loadURL(`http://localhost:${BACKEND_PORT}`);
  }

  mainWindow.once('ready-to-show', () => {
    if (splashWindow && !splashWindow.isDestroyed()) {
      splashWindow.close();
    }
    mainWindow.show();
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// 5. Sauvegarde de la base de données
function backupDatabase() {
  try {
    if (!fs.existsSync(backupsDir)) {
      fs.mkdirSync(backupsDir, { recursive: true });
    }

    const hasEmbeddedPostgres = fs.existsSync(pgDumpExecutable);
    const pgDumpCmd = hasEmbeddedPostgres ? pgDumpExecutable : 'pg_dump';

    const now = new Date();
    const timestamp = now.toISOString().replace(/[:T]/g, '-').split('.')[0];
    const backupFile = path.join(backupsDir, `backup_${timestamp}.sql`);

    console.log(`[Backup]: Début de la sauvegarde PostgreSQL vers ${backupFile}...`);

    execSync(`"${pgDumpCmd}" -p ${PG_PORT} -U standardnaast -d standardnaast -F p -f "${backupFile}"`, {
      windowsHide: true,
      stdio: 'pipe'
    });

    console.log(`[Backup]: Sauvegarde effectuée avec succès : ${backupFile}`);
    cleanOldBackups(15);
  } catch (error) {
    console.error('[Backup ERROR]: Erreur lors de la sauvegarde de PostgreSQL :', error.message);
  }
}

function cleanOldBackups(maxKeep = 15) {
  try {
    if (!fs.existsSync(backupsDir)) return;

    const files = fs.readdirSync(backupsDir)
      .filter(f => f.startsWith('backup_') && f.endsWith('.sql'))
      .map(f => ({
        name: f,
        path: path.join(backupsDir, f),
        time: fs.statSync(path.join(backupsDir, f)).mtime.getTime()
      }))
      .sort((a, b) => b.time - a.time);

    if (files.length > maxKeep) {
      const toDelete = files.slice(maxKeep);
      toDelete.forEach(file => {
        try {
          fs.unlinkSync(file.path);
          console.log(`[Backup]: Ancienne sauvegarde purgée : ${file.name}`);
        } catch (err) {
          console.warn(`[Backup]: Impossible de supprimer ${file.name} :`, err.message);
        }
      });
    }
  } catch (e) {
    console.warn('[Backup]: Erreur lors du nettoyage des anciens backups :', e.message);
  }
}

// 6. Arrêt des services
function stopServices() {
  isQuitting = true;
  console.log('Arrêt des sous-processus...');

  if (springProcess) {
    try {
      console.log('Arrêt de Spring Boot...');
      springProcess.kill('SIGTERM');
      springProcess = null;
    } catch (e) {
      console.error("Erreur lors de l'arrêt de Spring Boot :", e);
    }
  }

  // Sauvegarde de la base de données pendant que PostgreSQL est encore en cours d'exécution
  backupDatabase();

  const hasEmbeddedPostgres = fs.existsSync(pgCtlExecutable);
  const pgCtlCmd = hasEmbeddedPostgres ? pgCtlExecutable : 'pg_ctl';

  if (fs.existsSync(pgDataDir)) {
    try {
      console.log('Arrêt de PostgreSQL...');
      execSync(`"${pgCtlCmd}" -D "${pgDataDir}" stop -m fast`, {
        windowsHide: true,
        stdio: 'ignore'
      });
    } catch (e) {
      // Ignorer si déjà arrêté
    }
  }
}

// Cycle de vie Electron
app.on('ready', async () => {
  createSplashWindow();

  try {
    await startPostgreSQL();
    await checkAndPerformInitialH2Migration();
    await startSpringBoot();
    updateSplashStatus('Ouverture de Standard de Naast...');
    createMainWindow();
  } catch (err) {
    console.error('Erreur au lancement :', err);
    if (splashWindow && !splashWindow.isDestroyed()) {
      splashWindow.close();
    }
    dialog.showErrorBox(
      'Erreur de démarrage',
      `Impossible de démarrer l'application :\n${err.message}`
    );
    stopServices();
    app.quit();
  }
});

app.on('window-all-closed', () => {
  stopServices();
  app.quit();
});

app.on('before-quit', () => {
  stopServices();
});
