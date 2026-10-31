# Application desktop JVM / SQLite

Cette distribution remplace Electron et le serveur PostgreSQL local par Spring
Boot, Angular servi depuis le backend et SQLite. Le runtime Java est inclus par
`jpackage` : aucun Java, Node.js, Electron ou PostgreSQL ne doit être installé chez
l'utilisateur.

## Construire

Sur la machine de build : JDK 25 avec `jpackage`, Maven, Node.js et pnpm.
Le script utilise aussi le Node local de `standardnaast-ng/node` s'il existe.
Les installateurs doivent être construits sur leur OS cible.

```sh
# macOS : image application, puis installateur DMG ou PKG
bash ./build-desktop.sh app-image
bash ./build-desktop.sh dmg

# Linux : image application ou installateur (outils système de packaging requis)
bash ./build-desktop.sh deb
```

```bat
REM Windows : image application, EXE ou MSI
build-desktop.bat app-image
build-desktop.bat exe
```

Les installateurs Windows exigent aussi une version des outils WiX compatible
avec le JDK utilisé. Les distributions sont générées dans `dist-desktop`.

### Build Windows x64 avec GitHub Actions

Le workflow [Build Windows x64 installer](.github/workflows/build-windows-x64.yml)
permet de générer l'installateur depuis un Mac, sans machine Windows locale :

1. Pousser le workflow sur la branche par défaut du dépôt.
2. Ouvrir **Actions → Build Windows x64 installer → Run workflow**.
3. Sélectionner la branche à construire, puis lancer le workflow.
4. Après réussite, télécharger l'artifact **standard-naast-windows-x64-exe**
   dans la page de l'exécution et décompresser le ZIP pour récupérer
   **Standard Naast-1.0.0.exe**.

Le runner `windows-2022` x64 fournit Maven et WiX 3. Le workflow configure
Temurin JDK 25 x64, Node.js 22 et pnpm, installe les dépendances Angular avec
`npm ci` à partir du `package-lock.json` suivi dans Git, puis appelle
`build-desktop.bat exe`. pnpm sert ensuite à exécuter le script de build existant ;
aucun lockfile pnpm local non suivi n'est nécessaire.
Le script compile Angular, exécute les tests Java et inclut le runtime Java
Windows dans l'installateur. Les rapports de tests sont également disponibles
en artifact, y compris en cas d'échec du build.

Les artifacts sont conservés 14 jours. Ce workflow est manuel : il ne publie
pas de GitHub Release et ne signe pas l'exécutable. Un avertissement SmartScreen
reste donc possible. Un test d'installation sur Windows reste nécessaire.

### Reconstruction locale

Pour `app-image`, l'ancienne image de l'OS cible est supprimée automatiquement
juste avant le packaging, après la réussite des builds Angular et Java.
Le script vérifie la présence de la configuration du lanceur avant de supprimer
l'image ; un dossier inattendu est refusé. Les autres fichiers de `dist-desktop`,
les installateurs et les données utilisateur sont conservés. Si le packaging
échoue après cette suppression, il faut le relancer pour recréer l'image.
Une image est conservée lors de la construction d'un installateur.

Sur macOS, le build DMG garantit la présence d'un raccourci **Applications** dans
le volume, même si la personnalisation Finder de `jpackage` n'a pas été exécutée.
Après ouverture du DMG, glisser **Standard Naast.app** sur **Applications**, puis
éjecter le volume et lancer l'application depuis le dossier Applications.
Le DMG n'installe pas automatiquement l'application.

Le build compile Angular en production, exécute les tests Java et copie
`dist/standardnaast-ng/browser` dans `target/classes/static` via le profil Maven
`desktop`. Aucun fichier Angular généré n'est ajouté aux sources Java.
`jpackage` fabrique ensuite le runtime Java et l'intègre à la distribution.
Le profil Maven `desktop` suppose que le build Angular a déjà réussi : utiliser
les scripts pour une distribution complète.

Les anciennes sources Electron et les profils H2/PostgreSQL sont conservés pour
la transition, mais les nouveaux scripts desktop ne les utilisent plus.

### Tests de migration

`LiquibaseMigrationIntegrationTest` utilise une base H2 de test isolée et vérifie
l'exécution du schéma Liquibase, l'absence d'import historique, l'accès aux tables
via les repositories et l'insertion avec les séquences. Les écritures de test
sont annulées après chaque méthode. Le build standard exécute ce test sans
exclusion et sans dépendre des données utilisateur.

L'import `099-initial-data` reste désactivé dans le changelog serveur ; ne pas le
réactiver pour alimenter les tests.

## Utilisation

Lancer **Standard Naast** :

1. Un verrou empêche deux instances d'utiliser le même dossier de données.
2. Si la base SQLite n'existe pas, le premier lancement propose d'importer une
   sauvegarde H2 ou de démarrer avec une base vierge. Après l'import (ou le choix
   d'une base vierge), une boîte de dialogue demande la création du premier
   administrateur uniquement si aucun compte avec le rôle `ROLE_ADMIN` n'a été
   importé. Les autres comptes importés restent inchangés. Aucun mot de passe par
   défaut n'est créé.
3. Le backend démarre sur `127.0.0.1:8080`, avec un port fixe ; le formulaire de
   création d'administrateur s'affiche si nécessaire.
4. Lorsque l'application est prête, le navigateur par défaut s'ouvre.

Si aucune saison n'existe, l'API `/api/seasons/current` retourne un 404 avec
le message « Aucune saison configurée » et l'indication d'ouvrir **Saisons →
Nouvelle Saison**, affichés par la notification Angular.

L'adresse **http://127.0.0.1:8080/** peut être ajoutée aux favoris du navigateur.
L'application doit être démarrée pour que le favori fonctionne. Si le port 8080
est occupé, le lancement échoue explicitement sans choisir un autre port :
fermer le programme concerné, puis relancer l'application. Les tests du lanceur
utilisent également 8080 ; quitter l'application avant de les exécuter.

L'écusson de droite de `img/banniere_club.png` est utilisé comme icône de
l'application (ICNS macOS, ICO Windows, PNG Linux), de notification et comme
favicon du navigateur. Les formats de packaging sont dans `scripts/icons/` ;
le PNG est également embarqué dans les ressources du backend pour la notification.

Un deuxième lancement rouvre l'interface de l'instance existante. Si le premier
lancement n'est pas encore prêt, un message d'erreur l'indique au lieu de démarrer
un deuxième backend.

Fermer l'onglet du navigateur **ne quitte pas** l'application. Utiliser le menu
**Quitter Standard Naast** de l'icône de notification quand le système la prend en
charge, ou le lanceur **Quitter Standard Naast** fourni dans la distribution
(sur macOS, dans `Standard Naast.app/Contents/MacOS`).

Sans icône de notification, le lanceur d'arrêt reste disponible. La commande
d'arrêt exige un jeton aléatoire local, elle n'est pas un endpoint public sans
protection. Les API métier conservent leur authentification JWT.

## Données et mises à jour

| OS | Dossier |
|---|---|
| macOS | `~/Library/Application Support/Standard Naast` |
| Windows | `%LOCALAPPDATA%\Standard Naast` |
| Linux | `~/.local/share/standardnaast` |

Ce dossier contient `standardnaast.sqlite`, `jwt.secret`, les journaux dans
`logs/` et les fichiers de coordination de l'instance. Il est indépendant du
dossier d'installation et doit être conservé lors des mises à jour.
Le secret JWT est propre à l'installation et persiste entre les lancements.

SQLite dispose d'un changelog Liquibase distinct. Les identifiants utilisent
`INTEGER PRIMARY KEY AUTOINCREMENT` avec des overrides ORM limités au profil
desktop ; les mappings de séquences des profils serveur restent inchangés.
Les clés étrangères sont activées à chaque connexion, le pool est limité à une
connexion et un délai d'attente SQLite est configuré.

Au premier lancement interactif, l'import de sauvegarde H2 vers SQLite est
disponible avant le provisioning administrateur. Le flux SQL restaure uniquement
dans une base H2 temporaire locale ; les commandes qui créent des alias Java,
exécutent des scripts/appels ou ouvrent des ressources externes sont refusées.
La base SQLite est créée en staging avec Liquibase, puis publiée atomiquement
seulement après validation des tables, colonnes, identifiants, volumes et clés
étrangères. Une structure historique inconnue ou incohérente échoue explicitement
sans publier de destination.

Une base SQLite déjà présente n'est jamais proposée à l'import et aucune donnée
existante n'est écrasée. La sauvegarde SQL H2 (avec ou sans extension `.sql`)
est uniquement lue et restaurée dans un dossier temporaire ; elle n'est jamais
modifiée ni supprimée. Les fichiers `.mv.db` ne sont pas acceptés dans ce parcours.
Annuler le choix ferme le démarrage, sans bascule silencieuse vers une base vide. Pour
choisir une base vierge, utilisez explicitement cette option dans le dialogue.
Après un import sans rôle `ROLE_ADMIN`, le formulaire ajoute un compte
administrateur sans supprimer ni modifier les comptes importés.

Les tables historiques connues (`COMMANDE_PLACE`, `PRIX_PLACE`, `TYPE_COMPETITION`,
`TYPE_MATCH`, `TYPE_PERSONNE`) sont conservées, même si l'interface actuelle ne
les utilise pas. Les valeurs copiées sont comparées à la source avant publication.
Ces archives reprennent le schéma historique sans ajouter de clés étrangères :
une ancienne référence sans ligne correspondante est conservée et signalée
dans les logs, sans création de données fictives. Une clé étrangère explicitement
déclarée dans la sauvegarde reste vérifiée lors de l'import ; les contraintes
des tables utilisées par l'application restent actives.
À l'import, les cotisations et déplacements (`PERSONNES_COTISATIONS`,
`PERSONNE_MATCH`) qui référencent une personne absente sont conservés, avec
leurs identifiants et toutes leurs valeurs, dans les tables dédiées
`PERSONNES_COTISATIONS_IMPORT_ARCHIVE` et `PERSONNE_MATCH_IMPORT_ARCHIVE`.
La raison `PERSONNE_ABSENTE` et les volumes archivés sont signalés dans les logs.
Ces lignes ne sont pas affichées dans les écrans métier ; les lignes valides
sont importées normalement et les autres violations de contraintes restent
bloquantes. Les archives sont incluses dans la base SQLite et sa sauvegarde.
Une perte de précision monétaire ou temporelle provoque un refus explicite.
Les anciennes colonnes numériques `PRICES.RANG` et `PRICES.PLACE` sont converties
explicitement en texte sans perte de valeur (par exemple `12.00` devient `12`).
Les positions déjà textuelles et leurs zéros initiaux restent inchangés.
Cette compatibilité ne permet pas de convertir arbitrairement les autres colonnes.

Pour une sauvegarde manuelle, quitter complètement l'application puis copier le
dossier de données. Garder les sauvegardes hors du dossier d'installation.
Ne pas placer la base active dans un dossier réseau ou synchronisé, et ne pas la
partager entre plusieurs ordinateurs.

Les distributions ne sont pas encore signées/notarisées. Avant diffusion, prévoir
signature Windows, signature/notarisation macOS et validation sur chaque OS cible.
Les correctifs Java sont livrés en reconstruisant la distribution avec un JDK
maintenu, pas en utilisant un Java installé chez l'utilisateur.

## Validation reproductible

```sh
mvn -f standardnaast-backend/pom.xml \
  -Dtest=SqliteDesktopIntegrationTest,DesktopApplicationTest,InitialAdminSetupTest,H2ToSqliteMigratorTest test

# Build desktop effectué au préalable ; Node 24+ et Java 25 pour le test
node --test scripts/test-desktop.mjs

# Exercer le véritable lanceur avec son runtime inclus (macOS)
DESKTOP_LAUNCHER="$PWD/dist-desktop/Standard Naast.app/Contents/MacOS/Standard Naast" \
  node --test scripts/test-desktop.mjs
```

Ces tests utilisent un dossier temporaire, créent leur propre administrateur et
vérifient : ressources Angular, routes profondes, erreurs 404 distinctes des pages
SPA, authentification, CRUD membre, saison et génération PDF, instance unique, arrêt et persistance après
redémarrage. Le runtime de l'application provient de l'image ; Java installé sur
la machine de test sert uniquement à provisionner le compte de test via le JAR.
Les tests ne touchent pas aux bases utilisateur.

Pour un diagnostic manuel isolé :

```sh
java -jar standardnaast-backend/target/standardnaast-backend-1.0.0-SNAPSHOT.jar \
  --desktop --desktop-data-dir=/chemin/vers/donnees-de-test
```

L'option `--desktop-no-browser` désactive le navigateur et les dialogues pour les
tests automatisés ; elle ne crée pas de compte administrateur.

## Import initial d'une sauvegarde H2

Au premier lancement interactif, avant toute création de compte administrateur,
Standard Naast propose soit d'importer une sauvegarde SQL H2 (avec ou sans
extension `.sql`), soit de démarrer avec une base vierge. Annuler la
question ou le choix du fichier ferme le parcours de démarrage ; aucune base vide
n'est créée en remplacement. Après un import, un administrateur présent dans la
sauvegarde est conservé et la création du premier compte est ignorée. Si aucun
compte administrateur n'a été importé, l'étape habituelle de création d'administrateur suit
l'import.

L'import restaure les sauvegardes SQL dans une base H2 temporaire locale, puis
construit une base SQLite de staging avec le changelog Liquibase du profil
desktop. Les tables, colonnes, clés, nombres de lignes, identifiants, intégrité
SQLite et références sont vérifiés avant publication atomique. Une table, colonne
ou relation historique inconnue provoque un échec explicite : aucune donnée source
n'est ignorée silencieusement et la base cible n'est pas publiée. Les instructions
SQL permettant l'accès aux fichiers/ressources externes, les alias Java, appels,
scripts et opérations similaires sont rejetées avant restauration.

Une base SQLite déjà présente n'est jamais proposée à l'import ni remplacée.
L'import est limité à une destination neuve ; en cas d'erreur, les fichiers de
staging sont supprimés et l'application affiche l'échec au lieu de continuer
silencieusement avec une base vierge. Le fichier source n'est jamais exécuté sur
son emplacement d'origine, modifié ou supprimé. Gardez une copie indépendante de
la sauvegarde avant toute opération de maintenance.

`--desktop-no-browser` saute uniquement le dialogue interactif pour les tests ;
le moteur d'import est couvert séparément par des sauvegardes synthétiques.
Les tests ne lisent pas les sauvegardes privées de l'utilisateur et ne touchent
pas à une base utilisateur.

GraalVM Native Image reste une évolution ultérieure : le packaging actuel
embarque bien une JVM et ne prétend pas être une image native.
