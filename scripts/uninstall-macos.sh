#!/usr/bin/env bash

set -euo pipefail

usage() {
    echo "Usage: bash scripts/uninstall-macos.sh [--dry-run]"
    echo "Supprime Standard de Naast, ses donnees locales et ses sauvegardes."
    echo "--dry-run : affiche les chemins presents sans rien supprimer."
}

DRY_RUN=false
case "${1:-}" in
    --dry-run) DRY_RUN=true ;;
    -h|--help) usage; exit 0 ;;
    "") ;;
    *) usage >&2; exit 1 ;;
esac
if [ "$#" -gt 1 ]; then
    usage >&2
    exit 1
fi

if [ "$(uname -s)" != "Darwin" ]; then
    echo "Erreur : ce script est reserve a macOS." >&2
    exit 1
fi
if [ "$EUID" -eq 0 ]; then
    echo "Erreur : lancez ce script sans sudo pour cibler votre compte utilisateur." >&2
    exit 1
fi
if [ -z "${HOME:-}" ] || [ "$HOME" = "/" ] || [ ! -d "$HOME" ]; then
    echo "Erreur : dossier utilisateur invalide." >&2
    exit 1
fi

USER_HOME="$(cd "$HOME" && pwd -P)"
TARGETS=(
    "/Applications/Standard de Naast.app"
    "$USER_HOME/Applications/Standard de Naast.app"
    "$USER_HOME/.standardnaast"
)
for name in "standardnaast-desktop" "be.standardnaast.desktop" "Standard de Naast"; do
    TARGETS+=(
        "$USER_HOME/Library/Application Support/$name"
        "$USER_HOME/Library/Caches/$name"
        "$USER_HOME/Library/Caches/$name.ShipIt"
        "$USER_HOME/Library/Preferences/$name.plist"
        "$USER_HOME/Library/Saved Application State/$name.savedState"
        "$USER_HOME/Library/Logs/$name"
        "$USER_HOME/Library/WebKit/$name"
        "$USER_HOME/Library/HTTPStorages/$name"
        "$USER_HOME/Library/HTTPStorages/$name.binarycookies"
    )
done

PRESENT=()
for target in "${TARGETS[@]}"; do
    if [ -e "$target" ] || [ -L "$target" ]; then
        parent="$(dirname "$target")"
        if [ "$(cd "$parent" && pwd -P)" != "$parent" ]; then
            echo "Erreur : un dossier parent est un lien symbolique : $target" >&2
            exit 1
        fi
        PRESENT+=("$target")
    fi
done

if [ "${#PRESENT[@]}" -eq 0 ]; then
    echo "Aucune installation ni donnee trouvee aux emplacements connus."
    exit 0
fi

echo "Emplacements qui seront supprimes :"
printf '  %s\n' "${PRESENT[@]}"
echo
echo "ATTENTION : bases PostgreSQL, sauvegardes et donnees de migration incluses."
echo "Les fichiers du projet, les DMG et les bases H2 dans Documents sont preserves."
if [ "$DRY_RUN" = true ]; then
    echo "Apercu uniquement : aucune suppression."
    exit 0
fi

for tool in lsof pgrep; do
    if ! command -v "$tool" >/dev/null 2>&1; then
        echo "Erreur : $tool est requis pour verifier les processus et fichiers ouverts." >&2
        exit 1
    fi
done
echo "L'application sera arretee automatiquement apres confirmation."
echo "Pour garantir une derniere sauvegarde, quittez-la normalement avant de continuer."
printf 'Tapez SUPPRIMER pour confirmer la suppression definitive : '
if ! IFS= read -r confirmation || [ "$confirmation" != "SUPPRIMER" ]; then
    echo "Nettoyage annule."
    exit 1
fi

if app_pids="$(pgrep -u "$EUID" -x "Standard de Naast")"; then
    while IFS= read -r pid; do
        if [[ ! "$pid" =~ ^[0-9]+$ ]]; then
            echo "Erreur : PID invalide : $pid." >&2
            exit 1
        fi
        echo "Arret de Standard de Naast (PID $pid)..."
        if ! kill -TERM "$pid"; then
            if kill -0 "$pid" 2>/dev/null; then
                echo "Erreur : impossible d'arreter le PID $pid." >&2
                exit 1
            fi
        fi
        for ((attempt = 0; attempt < 30; attempt++)); do
            if ! kill -0 "$pid" 2>/dev/null; then
                break
            fi
            sleep 1
        done
        if kill -0 "$pid" 2>/dev/null; then
            echo "Erreur : le PID $pid est toujours actif apres 30 secondes. Aucune suppression." >&2
            exit 1
        fi
    done <<< "$app_pids"
else
    status=$?
    if [ "$status" -ne 1 ]; then
        echo "Erreur : recherche des processus impossible (code $status)." >&2
        exit 1
    fi
fi

# Verifier tous les emplacements avant la premiere suppression.
for target in "${PRESENT[@]}"; do
    if [ -L "$target" ]; then
        continue
    fi
    if [ -d "$target" ]; then
        lsof_args=(-nP +D "$target")
    else
        lsof_args=(-nP "$target")
    fi
    if lsof "${lsof_args[@]}"; then
        echo "Erreur : fichiers ouverts dans $target. Fermez l'application et ses processus avant de reessayer." >&2
        exit 1
    else
        status=$?
        if [ "$status" -ne 1 ]; then
            echo "Erreur : verification lsof impossible pour $target (code $status)." >&2
            exit 1
        fi
    fi
done

for target in "${PRESENT[@]}"; do
    echo "Suppression : $target"
    if [ "$target" = "/Applications/Standard de Naast.app" ]; then
        sudo /bin/rm -rf -- "$target"
    else
        /bin/rm -rf -- "$target"
    fi
    if [ -e "$target" ] || [ -L "$target" ]; then
        echo "Erreur : suppression incomplete pour $target." >&2
        exit 1
    fi
done
echo "Nettoyage termine pour les emplacements listes."
