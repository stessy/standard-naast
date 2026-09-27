/**
 * Script de préparation des ressources pour Electron
 * Compile Angular, compile Spring Boot et prépare l'arborescence resources/
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const rootDir = path.resolve(__dirname, '..', '..');
const electronDir = path.resolve(__dirname, '..');
const ngDir = path.join(rootDir, 'standardnaast-ng');
const backendDir = path.join(rootDir, 'standardnaast-backend');

function log(msg) {
  console.log(`\n\x1b[36m=== ${msg} ===\x1b[0m`);
}

function run(cmd, cwd) {
  console.log(`Exécution : ${cmd} (dans ${cwd})`);
  execSync(cmd, { cwd, stdio: 'inherit' });
}

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

async function main() {
  log('1. Compilation du Frontend Angular');
  try {
    run('npm run build', ngDir);
  } catch (e) {
    console.error('Erreur lors du build Angular. Assurez-vous que npm install a été exécuté dans standardnaast-ng.');
  }

  // Copie des fichiers Angular dans Electron ui/
  log('2. Copie des fichiers Angular dans standardnaast-electron/ui');
  const ngDist = path.join(ngDir, 'dist', 'standardnaast-ng');
  const electronUi = path.join(electronDir, 'ui');
  ensureDir(electronUi);
  if (fs.existsSync(ngDist)) {
    fs.cpSync(ngDist, electronUi, { recursive: true });
    console.log(`Angular copié vers ${electronUi}`);
  }

  // Build Spring Boot
  log('3. Compilation du Backend Spring Boot');
  try {
    const isWindows = process.platform === 'win32';
    const mvnCmd = isWindows ? 'mvn.cmd' : 'mvn';
    run(`${mvnCmd} clean package -DskipTests`, backendDir);
  } catch (e) {
    console.error('Erreur lors du build Maven backend');
  }

  // Copie du JAR dans resources/backend
  log('4. Copie du JAR Spring Boot vers resources/backend');
  const backendTargetJar = path.join(backendDir, 'target', 'standardnaast-backend-1.0.0-SNAPSHOT.jar');
  const electronBackendDir = path.join(electronDir, 'resources', 'backend');
  ensureDir(electronBackendDir);

  if (fs.existsSync(backendTargetJar)) {
    const destJar = path.join(electronBackendDir, 'standardnaast-backend.jar');
    fs.copyFileSync(backendTargetJar, destJar);
    console.log(`JAR copié vers ${destJar}`);
  } else {
    console.warn(`Attention : Le JAR ${backendTargetJar} n'a pas été trouvé.`);
  }

  // Structure des dossiers JRE et PostgreSQL
  log('5. Vérification des répertoires JRE et PostgreSQL');
  const targets = ['darwin-arm64', 'darwin-x64', 'win32-x64'];
  targets.forEach((target) => {
    ensureDir(path.join(electronDir, 'resources', 'jre', target));
    ensureDir(path.join(electronDir, 'resources', 'postgres', target));
  });

  console.log('\nPrêt pour le packaging !');
  console.log('Structure des répertoires resources/ :');
  console.log(' - resources/backend/standardnaast-backend.jar');
  console.log(' - resources/jre/<plateforme>/ (ex: darwin-arm64, win32-x64)');
  console.log(' - resources/postgres/<plateforme>/ (binaires pg_ctl, initdb, postgres, pg_dump, createdb)');
}

main().catch(console.error);
