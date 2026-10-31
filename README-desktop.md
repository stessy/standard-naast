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

### État des tests historiques

Le test `LiquibaseMigrationIntegrationTest.testAllRepositoriesAfterLiquibaseMigration`
exige des milliers de lignes historiques, alors que l'import `099-initial-data`
est actuellement désactivé dans le changelog serveur. Sur une base de test neuve,
ce test échoue indépendamment de SQLite. Le build standard signale cet échec.
Pour construire en excluant explicitement cette seule méthode :

```sh
MAVEN_OPTS='-Dtest=*,!LiquibaseMigrationIntegrationTest#testAllRepositoriesAfterLiquibaseMigration' \
  bash ./build-desktop.sh app-image
```

Ne pas réactiver l'import historique pour contourner ce test.

## Utilisation

Lancer **Standard Naast** :

1. Un verrou empêche deux instances d'utiliser le même dossier de données.
2. Le backend démarre sur `127.0.0.1`, avec un port attribué par le système.
3. Au premier lancement, une boîte de dialogue demande la création du premier
   administrateur. Aucun mot de passe par défaut n'est créé.
4. Lorsque l'application est prête, le navigateur par défaut s'ouvre.

Si aucune saison n'existe, l'API `/api/seasons/current` retourne un 404 avec
le message « Aucune saison configurée » et l'indication d'ouvrir **Saisons →
Nouvelle Saison**, affichés par la notification Angular.

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

**Cette première étape ne transfère pas les données H2/PostgreSQL existantes.**
La nouvelle distribution crée une base SQLite vide et ne lit, ne modifie, ni ne
supprime les anciennes bases. L'import contrôlé de l'historique reste une étape
distincte à réaliser avant de basculer les données réelles. Ne pas utiliser une
base SQLite vide comme remplacement de l'historique sans cet import.

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
  -Dtest=SqliteDesktopIntegrationTest,DesktopApplicationTest,InitialAdminSetupTest test

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

GraalVM Native Image reste une évolution ultérieure : le packaging actuel
embarque bien une JVM et ne prétend pas être une image native.
