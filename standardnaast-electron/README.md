# Standard de Naast - Application Desktop (Electron)

Cette application Desktop autonome intègre :
- **Frontend :** Angular 19 (`standardnaast-ng`)
- **Backend :** Spring Boot 4 (`standardnaast-backend`)
- **Base de données :** PostgreSQL embarqué / portable
- **Orchestration :** Electron & Electron Builder

L'utilisateur final (particulier) n'a qu'à installer le fichier `.dmg` (sur macOS) ou `.exe` (sur Windows). Il n'a aucun prérequis à installer (ni Java, ni Node, ni Docker, ni PostgreSQL).

---

## 1. Arborescence du module

```text
standardnaast-electron/
├── package.json               # Configuration Electron et Electron-Builder
├── main.js                    # Orchestrateur (Cycle de vie PostgreSQL + Spring + UI)
├── splash.html                # Écran de chargement avec statut
├── scripts/
│   └── prepare-resources.js   # Script d'automatisation des builds et copies
├── resources/                 # Ressources binaires embarquées
│   ├── backend/
│   │   └── standardnaast-backend.jar
│   ├── jre/
│   │   ├── darwin-arm64/      # JRE Java 25 (macOS Apple Silicon M1/M2/M3/M4)
│   │   ├── darwin-x64/        # JRE Java 25 (macOS Intel)
│   │   └── win32-x64/         # JRE Java 25 (Windows 64-bit)
│   └── postgres/
│       ├── darwin-arm64/      # Binaires PostgreSQL (bin/, share/, lib/)
│       ├── darwin-x64/
│       └── win32-x64/
└── ui/                        # Fichiers statiques du build Angular
```

---

## 2. Téléchargement automatique & configuration des binaires

Les binaires (JRE Java et PostgreSQL) sont configurés dans `binaries-config.json` et peuvent être téléchargés automatiquement sans intervention manuelle.

### A. Fichier de configuration (`binaries-config.json`)
Vous pouvez personnaliser les versions et URLs sources pour chaque OS/architecture :
```json
{
  "jre": {
    "version": "21",
    "targets": { ... }
  },
  "postgres": {
    "version": "17",
    "targets": { ... }
  }
}
```

### B. Commandes de téléchargement
- **Pour votre OS actuel (ex: macOS Apple Silicon) :**
  ```bash
  npm run download:binaries
  ```
- **Pour toutes les plateformes (macOS ARM64, macOS x64, Windows x64) :**
  ```bash
  npm run download:binaries:all
  ```
- **Pour Windows uniquement :**
  ```bash
  npm run download:binaries:win
  ```
- **Pour forcer un re-téléchargement :**
  ```bash
  node scripts/download-binaries.js --force
  ```

*(Note : La commande `npm run build:prepare` vérifie et télécharge également automatiquement les binaires manquants).*

---

## 3. Commandes de Build et Génération automatisée

### Option 1 : Script tout-en-un à la racine du projet (Recommandé)

Un script d'automatisation complet compile le backend Spring Boot (Maven), le frontend Angular, télécharge les binaires (JRE + PostgreSQL) et assemble le package Electron :

* **Sur macOS / Linux :**
  ```bash
  # Build automatique pour votre machine courante :
  ./build-desktop.sh

  # Ou pour une cible spécifique :
  ./build-desktop.sh mac             # DMG macOS Apple Silicon (ARM64)
  ./build-desktop.sh mac-universal   # DMG macOS Universel (ARM64 + Intel x64)
  ./build-desktop.sh win             # EXE Windows (NSIS x64)
  ./build-desktop.sh all             # Tous les installateurs (macOS + Windows)
  ```

* **Sur Windows :**
  ```cmd
  build-desktop.bat win
  ```

---

### Option 2 : Commandes directes dans `standardnaast-electron`

Chaque commande `dist` ou `build` intègre désormais automatiquement toutes les étapes préalables de compilation et de packaging :

```bash
cd standardnaast-electron

# Pour macOS (.dmg) :
npm run dist:mac

# Pour Windows (.exe) :
npm run dist:win

# Pour toutes les plateformes :
npm run dist:all
```

> Les installeurs finaux (`.dmg` ou `.exe`) sont générés dans le dossier `standardnaast-electron/dist-package/`.

---

## 4. Persistance des données et Mises à jour

* Les données de PostgreSQL sont enregistrées dans le répertoire utilisateur du système (`app.getPath('userData')`) :
  - **macOS :** `~/Library/Application Support/be.standardnaast.desktop/postgres_data`
  - **Windows :** `%APPDATA%\be.standardnaast.desktop\postgres_data`
* **Mises à jour :** Lorsque le client installe une nouvelle version de l'application (.dmg ou .exe), ses données PostgreSQL ne sont **jamais perdues ni écrasées**, car le dossier de données est indépendant du dossier d'installation du programme.

---

## 5. Sauvegardes automatiques (Backups)

* À chaque arrêt ou fermeture de l'application, un dump complet de la base de données PostgreSQL est automatiquement généré via `pg_dump`.
* **Emplacement des sauvegardes :**
  - **macOS :** `~/Library/Application Support/be.standardnaast.desktop/backups/backup_YYYY-MM-DD-HH-mm-ss.sql`
  - **Windows :** `%APPDATA%\be.standardnaast.desktop\backups\backup_YYYY-MM-DD-HH-mm-ss.sql`
* **Rotation automatique :** Seules les **15 sauvegardes les plus récentes** sont conservées pour éviter de saturer le disque dur du client.
* **Restauration :** En cas de besoin, une sauvegarde peut être restaurée en ligne de commande avec :
  ```bash
  psql -U standardnaast -d standardnaast -f backup_XXX.sql
  ```
