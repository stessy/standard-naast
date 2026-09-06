#!/usr/bin/env bash

# ==============================================================================
# Script de lancement du frontend Angular (standardnaast-ng)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NG_DIR="${SCRIPT_DIR}/standardnaast-ng"

cd "${NG_DIR}"

echo "=================================================="
echo " Démarrage du frontend Angular (Standard Naast)"
echo "=================================================="

# 1. Vérification et configuration de Node / npm
# Utilisation du Node/npm local installé par frontend-maven-plugin s'il existe
if [ -d "${NG_DIR}/node" ]; then
    export PATH="${NG_DIR}/node:${PATH}"
fi

# Vérification si node et npm sont accessibles
if ! command -v node >/dev/null 2>&1 || ! command -v npm >/dev/null 2>&1; then
    echo "⚠️ Node.js / npm n'ont pas été trouvés dans le PATH."
    echo "📦 Tentative d'installation automatique via Maven (frontend-maven-plugin)..."
    
    # Chercher mvn (PATH ou IntelliJ)
    if ! command -v mvn >/dev/null 2>&1; then
        if [ -x "/Applications/IntelliJ IDEA.app/Contents/plugins/maven-plugin/lib/maven3/bin/mvn" ]; then
            export PATH="/Applications/IntelliJ IDEA.app/Contents/plugins/maven-plugin/lib/maven3/bin:${PATH}"
        fi
    fi

    if command -v mvn >/dev/null 2>&1; then
        mvn -pl standardnaast-ng generate-resources
        if [ -d "${NG_DIR}/node" ]; then
            export PATH="${NG_DIR}/node:${PATH}"
        fi
    else
        echo "❌ Erreur : ni Node.js/npm ni Maven ne sont installés ou trouvés dans le PATH."
        exit 1
    fi
fi

echo " Node.js version : $(node -v)"
echo " npm version     : $(npm -v)"

# 2. Vérification des dépendances (node_modules)
if [ ! -d "${NG_DIR}/node_modules" ]; then
    echo "📦 Installation des dépendances npm..."
    npm install
fi

# 3. Lancement du serveur de développement Angular
PORT="${PORT:-4200}"
HOST="${HOST:-localhost}"

echo "🚀 Lancement de l'application Angular sur http://${HOST}:${PORT} ..."
npm start -- --host "${HOST}" --port "${PORT}" "$@"
