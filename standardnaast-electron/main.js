const { app, BrowserWindow, dialog, ipcMain } = require('electron');
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

// 2. Démarrage du Backend Spring Boot
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

// 3. Création des fenêtres
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

// 4. Sauvegarde de la base de données
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

// 5. Arrêt des services
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
