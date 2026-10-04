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

function run(cmd, cwd, env = process.env) {
  console.log(`Exécution : ${cmd} (dans ${cwd})`);
  execSync(cmd, { cwd, stdio: 'inherit', env });
}

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function findMavenCommand() {
  const isWindows = process.platform === 'win32';
  const defaultMvn = isWindows ? 'mvn.cmd' : 'mvn';

  // 1. Tester la commande directe dans PATH
  try {
    execSync(`${defaultMvn} -v`, { stdio: 'ignore' });
    return defaultMvn;
  } catch (_) {}

  // 2. Chercher dans les chemins standards
  const candidates = [
    '/opt/homebrew/bin/mvn',
    '/usr/local/bin/mvn',
    '/usr/bin/mvn',
    '/Applications/IntelliJ IDEA.app/Contents/plugins/maven-plugin/lib/maven3/bin/mvn',
    path.join(process.env.M2_HOME || '', 'bin', defaultMvn),
    path.join(process.env.MAVEN_HOME || '', 'bin', defaultMvn)
  ];

  for (const candidate of candidates) {
    if (candidate && fs.existsSync(candidate)) {
      return candidate;
    }
  }

  return defaultMvn;
}

async function main() {
  const args = process.argv.slice(2);
  const extraArgsStr = args.length > 0 ? ` ${args.join(' ')}` : '';

  // 1. Installation des dépendances Frontend si nécessaire
  log('1. Vérification et build du Frontend Angular');
  const ngNodeModules = path.join(ngDir, 'node_modules');
  if (!fs.existsSync(ngNodeModules)) {
    console.log('Installation des dépendances npm dans standardnaast-ng...');
    run('npm install', ngDir);
  }

  try {
    run('npm run build', ngDir);
  } catch (e) {
    console.error('❌ Erreur lors du build Angular.');
    throw e;
  }

  // 2. Copie des fichiers Angular dans Electron ui/
  log('2. Copie des fichiers Angular dans standardnaast-electron/ui');
  const ngDist = path.join(ngDir, 'dist', 'standardnaast-ng');
  const electronUi = path.join(electronDir, 'ui');
  ensureDir(electronUi);
  if (fs.existsSync(ngDist)) {
    fs.cpSync(ngDist, electronUi, { recursive: true });
    console.log(`Angular copié vers ${electronUi}`);
  } else {
    throw new Error(`❌ Le dossier de distribution Angular ${ngDist} n'existe pas.`);
  }

  // 3. Build Spring Boot
  log('3. Compilation du Backend Spring Boot');
  const mvnCmd = findMavenCommand();
  try {
    run(`"${mvnCmd}" clean package -DskipTests`, backendDir);
  } catch (e) {
    console.error('❌ Erreur lors du build Maven backend');
    throw e;
  }

  // 4. Copie du JAR dans resources/backend
  log('4. Copie du JAR Spring Boot vers resources/backend');
  const backendTargetJar = path.join(backendDir, 'target', 'standardnaast-backend-1.0.0-SNAPSHOT.jar');
  const electronBackendDir = path.join(electronDir, 'resources', 'backend');
  ensureDir(electronBackendDir);

  if (fs.existsSync(backendTargetJar)) {
    const destJar = path.join(electronBackendDir, 'standardnaast-backend.jar');
    fs.copyFileSync(backendTargetJar, destJar);
    console.log(`✅ JAR copié avec succès vers ${destJar}`);
  } else {
    throw new Error(`❌ Le JAR généré ${backendTargetJar} n'a pas été trouvé.`);
  }

  // 5. Téléchargement et vérification des binaires JRE et PostgreSQL
  log('5. Vérification et téléchargement automatique des binaires JRE et PostgreSQL');
  try {
    const downloadScript = path.join(__dirname, 'download-binaries.js');
    run(`node "${downloadScript}"${extraArgsStr}`, electronDir);
  } catch (e) {
    console.error('❌ Erreur lors du téléchargement des binaires :', e.message);
    throw e;
  }

  console.log('\n✅ Préparation terminée avec succès ! Prêt pour le packaging.');
  console.log('Structure des répertoires resources/ :');
  console.log(' - resources/backend/standardnaast-backend.jar');
  console.log(' - resources/jre/<plateforme>/ (ex: darwin-arm64, win32-x64)');
  console.log(' - resources/postgres/<plateforme>/ (binaires pg_ctl, initdb, postgres, pg_dump, createdb)');
}

main().catch((err) => {
  console.error('\n❌ Échec de la préparation des ressources :', err.message || err);
  process.exit(1);
});
