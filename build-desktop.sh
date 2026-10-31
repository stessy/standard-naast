#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TYPE="${1:-app-image}"
NG_DIR="${ROOT}/standardnaast-ng"
BACKEND_DIR="${ROOT}/standardnaast-backend"
OUTPUT="${ROOT}/dist-desktop"
INPUT="${BACKEND_DIR}/target/jpackage-input"

case "${TYPE}" in
    app-image|dmg|pkg|deb|rpm) ;;
    mac) TYPE=dmg ;;
    *) echo "Usage: $0 [app-image|dmg|pkg|deb|rpm] (build on the target OS)" >&2; exit 1 ;;
esac

if [ -d "${NG_DIR}/node" ]; then
    export PATH="${NG_DIR}/node:${PATH}"
fi
if ! command -v mvn >/dev/null 2>&1; then
    MAVEN_IDEA="/Applications/IntelliJ IDEA.app/Contents/plugins/maven-plugin/lib/maven3/bin"
    export PATH="/opt/homebrew/bin:/usr/local/bin:${MAVEN_IDEA}:${PATH}"
fi
for tool in java jpackage mvn node pnpm; do
    if ! command -v "${tool}" >/dev/null 2>&1; then
        echo "Missing build tool: ${tool}. Use JDK 25 (including jpackage), Maven, Node and pnpm." >&2
        exit 1
    fi
done
if [ "${TYPE}" = app-image ] && { [ -e "${OUTPUT}/Standard Naast.app" ] || [ -e "${OUTPUT}/Standard Naast" ]; }; then
    echo "An app image already exists in ${OUTPUT}. Move or remove that image before rebuilding." >&2
    exit 1
fi

cd "${NG_DIR}"
if [ ! -d node_modules ]; then
    pnpm install
fi
pnpm run build
test -f dist/standardnaast-ng/browser/index.html

cd "${ROOT}"
mvn -f "${BACKEND_DIR}/pom.xml" -Pdesktop clean package
mkdir -p "${INPUT}" "${OUTPUT}"
cp "${BACKEND_DIR}/target/standardnaast-backend-1.0.0-SNAPSHOT.jar" "${INPUT}/standardnaast.jar"
printf 'arguments=--desktop-stop\n' > "${BACKEND_DIR}/target/stop-launcher.properties"

PACKAGE_OPTIONS=()
if [ "$(uname -s)" = Darwin ]; then
    PACKAGE_OPTIONS+=(--mac-package-identifier be.standardnaast.desktop)
fi
jpackage \
    --type "${TYPE}" \
    --name "Standard Naast" \
    --app-version 1.0.0 \
    --vendor "Standard de Naast" \
    --dest "${OUTPUT}" \
    --input "${INPUT}" \
    --main-jar standardnaast.jar \
    --arguments "--desktop" \
    --java-options "-Dfile.encoding=UTF-8" \
    --add-modules java.se,jdk.unsupported,jdk.crypto.ec,jdk.charsets,jdk.management,jdk.zipfs \
    --add-launcher "Quitter Standard Naast=${BACKEND_DIR}/target/stop-launcher.properties" \
    "${PACKAGE_OPTIONS[@]}"

if [ "$(uname -s)" = Darwin ] && [ "${TYPE}" = dmg ]; then
    bash "${ROOT}/scripts/add-macos-applications-link.sh" "${OUTPUT}/Standard Naast-1.0.0.dmg"
fi

echo "Package created in ${OUTPUT}. Java is included; no user-side Java installation is needed."
