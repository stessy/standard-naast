/**
 * Script de téléchargement et d'extraction automatique des binaires JRE et PostgreSQL.
 * Lit la configuration depuis binaries-config.json.
 *
 * Usage :
 *   node scripts/download-binaries.js                 # Télécharge pour l'OS/Arch courant
 *   node scripts/download-binaries.js --platform=...  # Télécharge pour une plateforme spécifique
 *   node scripts/download-binaries.js --all           # Télécharge pour toutes les plateformes configurées
 *   node scripts/download-binaries.js --force         # Force le re-téléchargement même si présent
 *   node scripts/download-binaries.js --jre-only      # Télécharge uniquement le JRE
 *   node scripts/download-binaries.js --postgres-only # Télécharge uniquement PostgreSQL
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const electronDir = path.resolve(__dirname, '..');
const configFile = path.join(electronDir, 'binaries-config.json');
const tmpDir = path.join(electronDir, '.tmp-binaries');

function log(msg) {
  console.log(`\n\x1b[36m=== ${msg} ===\x1b[0m`);
}

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function cleanDir(dir) {
  if (fs.existsSync(dir)) {
    fs.rmSync(dir, { recursive: true, force: true });
  }
}

function parseArgs() {
  const args = process.argv.slice(2);
  const options = {
    all: args.includes('--all'),
    force: args.includes('--force'),
    jreOnly: args.includes('--jre-only'),
    postgresOnly: args.includes('--postgres-only'),
    platform: null
  };

  const platformArg = args.find((a) => a.startsWith('--platform='));
  if (platformArg) {
    options.platform = platformArg.split('=')[1].trim();
  } else if (!options.all) {
    options.platform = `${process.platform}-${process.arch}`;
  }

  return options;
}

function downloadFile(url, destPath) {
  console.log(`Téléchargement de ${url} ...`);
  ensureDir(path.dirname(destPath));
  // Utilisation de curl avec suivi des redirections (-L), affichage de la progression (-#)
  execSync(`curl -L -# -f "${url}" -o "${destPath}"`, { stdio: 'inherit' });
}

function extractArchive(archivePath, extractDir, format) {
  ensureDir(extractDir);
  console.log(`Extraction de ${path.basename(archivePath)} ...`);

  if (format === 'tar.gz') {
    execSync(`tar -xzf "${archivePath}" -C "${extractDir}"`, { stdio: 'inherit' });
  } else if (format === 'zip') {
    if (process.platform === 'win32') {
      execSync(`tar.exe -xf "${archivePath}" -C "${extractDir}"`, { stdio: 'inherit' });
    } else {
      execSync(`unzip -q -o "${archivePath}" -d "${extractDir}"`, { stdio: 'inherit' });
    }
  } else {
    throw new Error(`Format non supporté : ${format}`);
  }
}

function findDirectoryWithSubpath(baseDir, expectedSubpath) {
  // Cherche récursivement un dossier contenant expectedSubpath (ex: 'bin/java' ou 'bin')
  if (fs.existsSync(path.join(baseDir, expectedSubpath))) {
    return baseDir;
  }
  const entries = fs.readdirSync(baseDir, { withFileTypes: true });
  for (const entry of entries) {
    if (entry.isDirectory()) {
      const subDir = path.join(baseDir, entry.name);
      const found = findDirectoryWithSubpath(subDir, expectedSubpath);
      if (found) return found;
    }
  }
  return null;
}

function fixPermissions(dir) {
  if (process.platform === 'win32') return;
  const binDir = path.join(dir, 'bin');
  if (fs.existsSync(binDir)) {
    try {
      execSync(`chmod +x "${binDir}"/*`, { stdio: 'ignore' });
    } catch (_) {}
  }
}

async function processJRE(target, config, force) {
  const destDir = path.join(electronDir, 'resources', 'jre', target);
  const javaBinaryName = target.startsWith('win32') ? 'java.exe' : 'java';
  const javaBinary = path.join(destDir, 'bin', javaBinaryName);

  if (fs.existsSync(javaBinary) && !force) {
    console.log(`[JRE] Déjà présent pour ${target} (${javaBinary}). Utilisez --force pour réinstaller.`);
    return;
  }

  log(`Téléchargement du JRE pour ${target}`);
  const ext = config.format === 'zip' ? 'zip' : 'tar.gz';
  const downloadDest = path.join(tmpDir, `jre-${target}.${ext}`);
  const extractTmp = path.join(tmpDir, `jre-${target}-extracted`);

  cleanDir(extractTmp);
  downloadFile(config.url, downloadDest);
  extractArchive(downloadDest, extractTmp, config.format);

  let sourceDir = extractTmp;
  if (config.subDir) {
    const candidate = findDirectoryWithSubpath(extractTmp, config.subDir);
    if (candidate) {
      sourceDir = path.join(candidate, config.subDir);
    }
  } else {
    const candidate = findDirectoryWithSubpath(extractTmp, 'bin');
    if (candidate) {
      sourceDir = candidate;
    }
  }

  cleanDir(destDir);
  ensureDir(destDir);
  console.log(`Copie du JRE vers ${destDir} ...`);
  fs.cpSync(sourceDir, destDir, { recursive: true });
  fixPermissions(destDir);
  console.log(`[JRE] Installé avec succès pour ${target} !`);
}

async function processPostgres(target, config, force) {
  const destDir = path.join(electronDir, 'resources', 'postgres', target);
  const initdbBinaryName = target.startsWith('win32') ? 'initdb.exe' : 'initdb';
  const initdbBinary = path.join(destDir, 'bin', initdbBinaryName);

  if (fs.existsSync(initdbBinary) && !force) {
    console.log(`[PostgreSQL] Déjà présent pour ${target} (${initdbBinary}). Utilisez --force pour réinstaller.`);
    return;
  }

  log(`Téléchargement de PostgreSQL pour ${target}`);

  if (config.format === 'dmg') {
    if (process.platform !== 'darwin') {
      console.warn(`[PostgreSQL] L'extraction d'un fichier .dmg nécessite macOS. Ignore ${target}.`);
      return;
    }
    const downloadDest = path.join(tmpDir, `postgres-${target}.dmg`);
    downloadFile(config.url, downloadDest);

    console.log(`Montage du DMG ${downloadDest} ...`);
    const mountOutput = execSync(`hdiutil attach -nobrowse -readonly "${downloadDest}"`).toString();
    const mountMatch = mountOutput.match(/\/Volumes\/[^\n\r]+/);
    if (!mountMatch) {
      throw new Error(`Impossible de trouver le point de montage pour ${downloadDest}`);
    }
    const mountPoint = mountMatch[0].trim();
    console.log(`Monté sur : ${mountPoint}`);

    try {
      const appSource = path.join(mountPoint, config.dmgAppPath || 'Postgres.app/Contents/Versions/17');
      cleanDir(destDir);
      ensureDir(destDir);
      console.log(`Copie de PostgreSQL depuis ${appSource} vers ${destDir} ...`);
      fs.cpSync(appSource, destDir, { recursive: true });
      fixPermissions(destDir);
      console.log(`[PostgreSQL] Installé avec succès pour ${target} !`);
    } finally {
      console.log(`Démontage de ${mountPoint} ...`);
      execSync(`hdiutil detach "${mountPoint}" -force`, { stdio: 'ignore' });
    }
  } else {
    const ext = config.format === 'zip' ? 'zip' : 'tar.gz';
    const downloadDest = path.join(tmpDir, `postgres-${target}.${ext}`);
    const extractTmp = path.join(tmpDir, `postgres-${target}-extracted`);

    cleanDir(extractTmp);
    downloadFile(config.url, downloadDest);
    extractArchive(downloadDest, extractTmp, config.format);

    let sourceDir = extractTmp;
    if (config.subDir) {
      const candidate = findDirectoryWithSubpath(extractTmp, config.subDir);
      if (candidate) {
        sourceDir = path.join(candidate, config.subDir);
      }
    } else {
      const candidate = findDirectoryWithSubpath(extractTmp, 'bin');
      if (candidate) {
        sourceDir = candidate;
      }
    }

    cleanDir(destDir);
    ensureDir(destDir);
    console.log(`Copie de PostgreSQL vers ${destDir} ...`);
    fs.cpSync(sourceDir, destDir, { recursive: true });
    fixPermissions(destDir);
    console.log(`[PostgreSQL] Installé avec succès pour ${target} !`);
  }
}

async function main() {
  if (!fs.existsSync(configFile)) {
    console.error(`Fichier de configuration introuvable : ${configFile}`);
    process.exit(1);
  }

  const config = JSON.parse(fs.readFileSync(configFile, 'utf-8'));
  const options = parseArgs();

  ensureDir(tmpDir);

  const jreTargets = options.all
    ? Object.keys(config.jre.targets)
    : [options.platform].filter((p) => config.jre.targets[p]);

  const postgresTargets = options.all
    ? Object.keys(config.postgres.targets)
    : [options.platform].filter((p) => config.postgres.targets[p]);

  if (jreTargets.length === 0 && postgresTargets.length === 0) {
    console.error(`Aucune cible trouvée pour la plateforme "${options.platform}". Cibles valides : ${Object.keys(config.jre.targets).join(', ')}`);
    process.exit(1);
  }

  try {
    if (!options.postgresOnly) {
      for (const target of jreTargets) {
        await processJRE(target, config.jre.targets[target], options.force);
      }
    }

    if (!options.jreOnly) {
      for (const target of postgresTargets) {
        await processPostgres(target, config.postgres.targets[target], options.force);
      }
    }

    log('Nettoyage des fichiers temporaires');
    cleanDir(tmpDir);

    log('Tous les binaires nécessaires ont été installés avec succès !');
  } catch (err) {
    console.error(`\n\x1b[31mErreur :\x1b[0m`, err);
    process.exit(1);
  }
}

main();
