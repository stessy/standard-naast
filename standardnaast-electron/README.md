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
    "version": "25",
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
  pnpm run download:binaries
  ```
- **Pour toutes les plateformes (macOS ARM64, macOS x64, Windows x64) :**
  ```bash
  pnpm run download:binaries:all
  ```
- **Pour Windows uniquement :**
  ```bash
  pnpm run download:binaries:win
  ```
- **Pour forcer un re-téléchargement :**
  ```bash
  node scripts/download-binaries.js --force
  ```

*(Note : La commande `pnpm run build:prepare` vérifie et télécharge également automatiquement les binaires manquants).*

---

## 3. Commandes de Build et Génération automatisée

Les builds nécessitent **Node.js**, **pnpm** et **Maven**. Les scripts utilisent pnpm pour installer les dépendances et exécuter les commandes frontend/Electron.

Le backend natif enregistre explicitement `db/changelog/**` via les Runtime Hints Spring afin d'embarquer le changelog Liquibase et ses fichiers XML/SQL inclus. Il autorise aussi les appels reflectifs aux types de changements et preconditions utilises par le changelog actif, y compris leurs setters herites. Lors de l'ajout d'un nouveau type de changement Liquibase, adaptez `LiquibaseRuntimeHints` et verifiez le demarrage du binaire natif sur une base jetable.

Le plugin Maven `org.hibernate.orm:hibernate-maven-plugin`, aligne sur la version
Hibernate geree par Spring Boot, enrichit les entites a la phase `process-classes`,
avant le traitement AOT. Cet enhancement permet de charger les associations
`LAZY` sans generer de proxies Hibernate a l'execution dans le binaire natif.
Ne le remplacez pas par un changement de BytecodeProvider ou un passage global en
`EAGER`. Les tests PostgreSQL verifient ces lectures avec le provider `none` ;
avec `MIGRATION_NATIVE_BINARY`, ils testent aussi la connexion d'un utilisateur lie
a une personne et la lecture d'un abonnement via HTTP sur deux demarrages natifs.

Dans VS Code, le build Java automatique est désactivé dans `.vscode/settings.json` pour éviter que le compilateur de l'IDE ne remplace les classes produites par Maven pendant le build, notamment les mappers MapStruct lors du traitement AOT. Utilisez Maven pour compiler le backend. Après un changement de ce réglage, rechargez la fenêtre VS Code avant de relancer un build propre.

Le rapport PDF des cotisations enregistre ses ressources natives via
`PdfRuntimeHints` : polices et banniere `pdf/**`, metriques et encodages iText
`com/itextpdf/text/pdf/fonts/**`, ainsi que les messages d'erreur iText.
Ces ressources doivent rester embarquees pour conserver la police Comic Sans Bold,
la banniere et le repli Helvetica-Bold dans le binaire natif.
Le profil Maven `native` inclut les jeux de caracteres (`-H:+AddAllCharsets`),
notamment Windows-1252 utilise par iText. Les hints autorisent aussi la copie
reflective des images PNG par iText lors de la construction du tableau.
Le test HTTP active
avec `MIGRATION_NATIVE_BINARY` verifie aussi la generation du PDF des cotisations.
Apres une modification de ces hints, reconstruisez le backend natif et le package
Electron ; un binaire deja installe ne beneficie pas de ces changements.

### Option 1 : Script tout-en-un à la racine du projet (Recommandé)

L'ajout d'un abonnement depuis la gestion des membres ne demande pas de donnees
de paiement : l'API initialise `reduction` a `0` et `paye` a `false` lorsque
ces champs sont absents ou `null`. Les valeurs explicites restent conservees ;
la saison et le membre restent obligatoires.

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
pnpm run dist:mac

# Pour Windows (.exe) :
pnpm run dist:win

# Pour toutes les plateformes :
pnpm run dist:all
```

> Les installeurs finaux (`.dmg` ou `.exe`) sont générés dans le dossier `standardnaast-electron/dist-package/`.

### Verification de l'interface Electron

La preparation utilise `pnpm run build:electron` dans le frontend : la configuration
`production,electron` genere une base relative (`./index.html`) pour charger les scripts et
styles depuis `file://`. Dans ce contexte uniquement, Angular utilise les routes
`#/login`. La base HTML conserve explicitement `index.html` lors d'un
rechargement. La version web conserve ses routes habituelles (`/login`).

Pour verifier le rendu reel sans demarrer le backend ni acceder aux donnees
de l'application installee :

```bash
cd standardnaast-ng
pnpm run build --output-path dist/standardnaast-ng-web
pnpm run build:electron
cd ../standardnaast-electron
pnpm run test:ui
```

Ce test ouvre une fenetre Electron invisible avec un profil temporaire. Il verifie
le formulaire de connexion charge a la demande, la redirection des routes protegees
et le rechargement en `file://`, puis le routage HTTP classique et son rechargement.
Les erreurs du renderer font echouer le test. Les echecs de chargement et les
erreurs console du frontend sont aussi journalises par l'application principale.

---

## 4. Persistance des données et Mises à jour

Lors de la migration H2 vers PostgreSQL, les colonnes `NUMERIC`/`DECIMAL` dont la precision H2 depasse 1000 (ou n'est pas renseignee) sont creees en `NUMERIC` sans precision explicite. Cela evite la limite PostgreSQL sur les declarations de precision sans imposer un arrondi a deux decimales. Les valeurs restent soumises aux limites de stockage numerique de PostgreSQL.

Si une cle etrangere relie des types entiers et decimaux differents, les colonnes concernees sont creees en `NUMERIC` sans precision, y compris dans les chaines de references, pour conserver les valeurs sans arrondi ni debordement. Les noms de sequences conservent leur casse. Chaque ajout de cle etrangere et chaque synchronisation de sequence dispose d'un point de sauvegarde transactionnel : un echec est signale dans les avertissements et annule localement sans bloquer les operations suivantes.

Ces conversions de types concernent la creation des tables : une cible contenant deja un ancien schema n'est pas automatiquement restructuree. Avant de retenter une migration sur une cible existante, sauvegardez-la et utilisez une base de test vide ou une recreation explicitement choisie. Une migration terminee avec des avertissements de contraintes n'assure pas l'integrite referentielle complete.

Les tests PostgreSQL du migrateur utilisent `MIGRATION_TEST_PG_URL` et le compte systeme courant sans mot de passe. Cette URL doit designer une **base jetable vide**, jamais la base de l'application. Sans cette variable, ces tests sont ignores ; les tests H2 et de generation SQL restent executables normalement.

### Passage de l'import H2 a Liquibase

L'historique Liquibase de H2 n'est pas copie : il ne correspond pas au changelog PostgreSQL courant. Avant le schema initial, le changeset `017-normalize-imported-identifiers` renomme en minuscules les tables, colonnes et sequences connues de l'application dans `public`. PostgreSQL conserve les donnees, les contraintes et les compteurs lors de ces renommages. Les objets etrangers a l'application ne sont pas cibles.

Le changeset initial existant s'execute ensuite avec ses instructions `IF NOT EXISTS` : les tables importees ne sont pas recreees, les objets absents sont crees et cette execution est inscrite normalement dans `databasechangelog`. Les autres evolutions, dont `ROBOTICKET_ID`, restent actives. Aucun `changelogSync` global et aucune desactivation de Liquibase ne sont utilises.

Cette reprise fonctionne aussi pour une base importee avec l'ancienne version. **Sauvegardez-la avant la mise a jour.** Si deux tables, sequences ou colonnes ne different que par la casse, le changeset echoue et ses renommages sont annules : il faut reconcilier explicitement ces objets, pas supprimer automatiquement l'un des deux. La normalisation des noms ne corrige pas des colonnes manquantes ou des types incompatibles dans un schema endommage ; la validation Hibernate reste active.

Le changeset suivant `018-restore-imported-integer-types` convertit les colonnes `NUMERIC` connues comme entieres dans le schema applicatif en `BIGINT` ou `INTEGER`, y compris les references comme `abonnement.personne_id`. Il verifie les valeurs avant conversion (aucune fraction, aucun depassement 64 ou 32 bits, aucune valeur non finie), bloque les ecritures pendant l'operation et restaure les cles etrangeres avec leur definition existante. Toute erreur annule le changeset sans enregistrer son execution ni arrondir les donnees. Les colonnes monetaires decimales ne sont pas ciblees. Ce nouveau changeset s'applique aussi si les changesets precedents sont deja enregistres.

Le changeset `019-align-sequence-increments` aligne les 12 sequences JPA sur les `allocationSize` du modele : pas de 1 pour `abonnement_seq`, `accounting_seq`, `benevolat_seq`, `personne_match_seq`, `prices_seq`, `travel_price_seq`, `user_seq` ; pas de 50 pour `match_seq`, `person_seq`, `personne_cotisation_seq`, `team_season_seq`, `team_seq`. Il utilise uniquement `ALTER SEQUENCE ... INCREMENT BY`, sans `RESTART` ni `setval`, pour conserver les compteurs existants. Il s'applique aussi aux bases deja importees.

`ImportedDatabaseLiquibaseTest` couvre une installation vide, un import H2 suivi de deux passages Liquibase, le refus transactionnel des collisions et la correction des 12 increments avec verification de la prochaine valeur generee. Le schema H2 de test comporte volontairement des colonnes entieres `NUMERIC` et des increments contraires au modele JPA. Avec `MIGRATION_NATIVE_BINARY` (chemin absolu du backend natif reconstruit), il verifie aussi deux demarrages natifs apres import, la validation JPA et la conservation des donnees et sequences. Ces tests creent et suppriment leurs propres bases : le compte de test doit avoir le droit `CREATEDB`.

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

---

## 6. Desinstallation complete sur macOS

Le script [uninstall-macos.sh](../scripts/uninstall-macos.sh) supprime l'application dans `/Applications` et `~/Applications`, ainsi que les donnees utilisateur, caches, preferences, etats de fenetres et logs des identifiants connus (`standardnaast-desktop`, `be.standardnaast.desktop`, `Standard de Naast`) et `~/.standardnaast`.

**Attention : les bases PostgreSQL et toutes les sauvegardes de ces dossiers sont definitivement supprimees.** Copiez les sauvegardes a conserver ailleurs avant de continuer. Les fichiers du projet, les installateurs DMG et les bases H2 dans Documents ne sont pas supprimes. Les copies de l'application et exports places ailleurs ne sont pas recherches.

Depuis la racine du projet :

```bash
# Afficher les chemins presents sans rien supprimer :
bash scripts/uninstall-macos.sh --dry-run

# Arreter automatiquement l'application puis nettoyer, apres confirmation :
bash scripts/uninstall-macos.sh
```

Le script demande de saisir `SUPPRIMER`, recherche les PID du processus exact `Standard de Naast` appartenant au compte courant, puis leur envoie `SIGTERM`. Il attend leur arret pendant 30 secondes maximum par processus, sans arret force, et refuse de continuer si un processus reste actif ou si des fichiers sont encore ouverts. Pour garantir une derniere sauvegarde, quittez normalement l'application avant de lancer le script : un signal d'arret ne garantit pas l'execution des sauvegardes Electron. Le mode `--dry-run` n'arrete aucun processus.

Le script ne touche pas aux installations PostgreSQL independantes. Lancez-le **sans sudo** : une elevation est demandee uniquement pour supprimer l'application dans `/Applications`. Le nettoyage concerne uniquement le compte utilisateur courant, pas les autres comptes ni les sauvegardes systeme.
