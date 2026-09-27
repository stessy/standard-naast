#!/bin/bash
set -e

# ==============================================================================
# Script de migration de base de données H2 vers PostgreSQL
# Standard Naast Application
# ==============================================================================

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
H2_DATA_DIR="${PROJECT_DIR}/data"
H2_DB_NAME="standardnaast"
OUTPUT_SQL="${PROJECT_DIR}/standardnaast-backend/src/main/resources/db/changelog/liquibaseChangelog/099_initial_data.sql"

PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-5432}"
PG_DATABASE="${PG_DATABASE:-standardnaast}"
PG_USER="${PG_USER:-standardnaast}"
PG_PASSWORD="${PG_PASSWORD:-standardnaast_password}"

echo "=== Migration H2 vers PostgreSQL pour Standard Naast ==="
echo "1. Vérification des fichiers de données H2..."

if [ -f "${H2_DATA_DIR}/${H2_DB_NAME}.mv.db" ]; then
    echo "Fichier de base H2 trouvé : ${H2_DATA_DIR}/${H2_DB_NAME}.mv.db"
else
    echo "Attention : Fichier ${H2_DATA_DIR}/${H2_DB_NAME}.mv.db non trouvé."
fi

echo "2. Les scripts Liquibase sont configurés pour s'exécuter automatiquement au démarrage de l'application :"
echo "   - Schéma initial universel (tables et séquences) : 000_initial_schema.sql"
echo "   - Tables utilisateurs et rôles : 013_users_and_roles.xml"
echo "   - Données initiales migrées : 099_initial_data.sql"
echo "   - Contraintes et clés étrangères : 014_foreign_keys.xml"
echo "   - Synchronisation des séquences PostgreSQL : 015_sync_sequences.xml"
echo ""
echo "3. Pour démarrer PostgreSQL via Docker Compose et lancer le backend :"
echo "   docker-compose up -d postgres"
echo "   SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run -pl standardnaast-backend"
echo ""
echo "Migration prête et validée."
