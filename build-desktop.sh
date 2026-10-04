#!/usr/bin/env bash

# ==============================================================================
# Script de build et packaging complet de l'application Desktop Standard Naast
# (Spring Boot + Angular + PostgreSQL + Electron)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ELECTRON_DIR="${SCRIPT_DIR}/standardnaast-electron"
NG_DIR="${SCRIPT_DIR}/standardnaast-ng"
BACKEND_DIR="${SCRIPT_DIR}/standardnaast-backend"

TARGET="${1:-auto}"

echo "================================================================="
echo "  🚀 Build & Packaging Desktop - Standard de Naast"
echo "================================================================="

# 1. Détection de Maven dans le PATH ou chemins connus
if ! command -v mvn >/dev/null 2>&1; then
    if [ -x "/opt/homebrew/bin/mvn" ]; then
        export PATH="/opt/homebrew/bin:${PATH}"
    elif [ -x "/usr/local/bin/mvn" ]; then
        export PATH="/usr/local/bin:${PATH}"
    elif [ -x "/Applications/IntelliJ IDEA.app/Contents/plugins/maven-plugin/lib/maven3/bin/mvn" ]; then
        export PATH="/Applications/IntelliJ IDEA.app/Contents/plugins/maven-plugin/lib/maven3/bin:${PATH}"
    fi
fi

if ! command -v mvn >/dev/null 2>&1; then
    echo "❌ Erreur : Maven ('mvn') est introuvable. Veuillez installer Maven ou vérifier votre PATH."
    exit 1
fi

# 2. Détection de Node et npm
if ! command -v node >/dev/null 2>&1 || ! command -v npm >/dev/null 2>&1; then
    echo "❌ Erreur : Node.js et npm sont requis mais introuvables."
    exit 1
fi

echo "✅ Node.js : $(node -v)"
echo "✅ npm     : $(npm -v)"
echo "✅ Maven   : $(mvn -v | head -n 1)"
echo "-----------------------------------------------------------------"

# 3. Installation des dépendances si nécessaire
if [ ! -d "${ELECTRON_DIR}/node_modules" ]; then
    echo "📦 Installation des dépendances Electron..."
    cd "${ELECTRON_DIR}" && npm install
fi

if [ ! -d "${NG_DIR}/node_modules" ]; then
    echo "📦 Installation des dépendances Angular..."
    cd "${NG_DIR}" && npm install
fi

# 4. Exécution selon la cible demandée
cd "${ELECTRON_DIR}"

case "${TARGET}" in
    mac|darwin|dmg)
        echo "🍎 Packaging pour macOS (ARM64 Apple Silicon)..."
        npm run dist:mac
        ;;
    mac-universal|universal)
        echo "🍎 Packaging pour macOS (Universel ARM64 + x64)..."
        npm run dist:mac:universal
        ;;
    win|windows|exe)
        echo "🪟 Packaging pour Windows (x64 NSIS)..."
        npm run dist:win
        ;;
    all)
        echo "🌍 Packaging pour toutes les plateformes (macOS + Windows)..."
        npm run dist:all
        ;;
    auto)
        if [[ "$OSTYPE" == "darwin"* ]]; then
            echo "🍎 Détection macOS : Packaging DMG (ARM64)..."
            npm run dist:mac
        elif [[ "$OSTYPE" == "msys"* ]] || [[ "$OSTYPE" == "win32"* ]]; then
            echo "🪟 Détection Windows : Packaging NSIS (x64)..."
            npm run dist:win
        else
            echo "🐧 Détection Linux / Autre : Lancement de electron-builder..."
            npm run dist
        fi
        ;;
    *)
        echo "❌ Cible inconnue : ${TARGET}"
        echo "Usage: ./build-desktop.sh [mac | mac-universal | win | all]"
        exit 1
        ;;
esac

echo ""
echo "================================================================="
echo "🎉 Build terminé avec succès !"
echo "📁 Les fichiers d'installation sont disponibles dans :"
echo "   ${ELECTRON_DIR}/dist-package/"
echo "================================================================="
