/**
 * Script CLI pour exécuter la migration H2 -> PostgreSQL
 * Usage: node scripts/migrate-h2.js [chemin_h2_file]
 */

const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');

const rootDir = path.resolve(__dirname, '..', '..');
const electronDir = path.resolve(__dirname, '..');

const customH2 = process.argv[2] || path.join(rootDir, 'data', 'standardnaast.mv.db');
const h2Path = path.resolve(customH2);

if (!fs.existsSync(h2Path) && !fs.existsSync(`${h2Path}.mv.db`)) {
  console.error(`\x1b[31mErreur : Fichier H2 introuvable : ${h2Path}\x1b[0m`);
  console.log(`Usage : node scripts/migrate-h2.js [chemin_vers_base_h2.mv.db]`);
  process.exit(1);
}

const jarPath = path.join(rootDir, 'standardnaast-backend', 'target', 'standardnaast-backend-1.0.0-SNAPSHOT.jar');

if (!fs.existsSync(jarPath)) {
  console.error(`\x1b[31mErreur : Le JAR backend n'existe pas : ${jarPath}\x1b[0m`);
  console.log('Veuillez compiler le backend avec Maven d\'abord.');
  process.exit(1);
}

console.log(`\x1b[36m=== Lancement de la migration H2 -> PostgreSQL ===\x1b[0m`);
console.log(`Source H2  : ${h2Path}`);
console.log(`Cible PG   : jdbc:postgresql://localhost:5432/standardnaast\n`);

const args = [
  '-jar',
  jarPath,
  `--migrate-from-h2=${h2Path}`,
  `--pg-url=jdbc:postgresql://localhost:5432/standardnaast`,
  `--pg-user=standardnaast`,
  `--pg-password=standardnaast_password`
];

const proc = spawn('java', args, { stdio: 'inherit' });

proc.on('close', (code) => {
  if (code === 0) {
    console.log(`\n\x1b[32m=== Migration réussie avec succès ! ===\x1b[0m`);
  } else {
    console.error(`\n\x1b[31m=== Échec de la migration (code ${code}) ===\x1b[0m`);
    process.exit(code);
  }
});
