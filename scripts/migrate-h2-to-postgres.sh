#!/bin/bash
set -e

# ==============================================================================
# Script de migration dynamique de base de données H2 vers PostgreSQL
# Standard Naast Application
# ==============================================================================

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
H2_FILE="${1:-${PROJECT_DIR}/data/standardnaast.mv.db}"

PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-5432}"
PG_DATABASE="${PG_DATABASE:-standardnaast}"
PG_USER="${PG_USER:-standardnaast}"
PG_PASSWORD="${PG_PASSWORD:-standardnaast_password}"
PG_URL="jdbc:postgresql://${PG_HOST}:${PG_PORT}/${PG_DATABASE}"

echo "=== Migration dynamique H2 vers PostgreSQL ==="
echo "Base H2 source : ${H2_FILE}"
echo "PostgreSQL cible : ${PG_URL}"

if [ ! -f "${H2_FILE}" ] && [ ! -f "${H2_FILE}.mv.db" ]; then
    echo "Erreur : Fichier H2 introuvable : ${H2_FILE}"
    echo "Usage : $0 [chemin_vers_base_h2.mv.db]"
    exit 1
fi

BACKEND_JAR="${PROJECT_DIR}/standardnaast-backend/target/standardnaast-backend-1.0.0-SNAPSHOT.jar"

if [ ! -f "${BACKEND_JAR}" ]; then
    echo "Compilation du backend..."
    (cd "${PROJECT_DIR}/standardnaast-backend" && mvn clean package -DskipTests)
fi

echo "Exécution de la migration..."
java -jar "${BACKEND_JAR}" \
  "--migrate-from-h2=${H2_FILE}" \
  "--pg-url=${PG_URL}" \
  "--pg-user=${PG_USER}" \
  "--pg-password=${PG_PASSWORD}"

echo "=== Migration terminée avec succès ==="
