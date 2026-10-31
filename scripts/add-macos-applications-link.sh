#!/usr/bin/env bash
set -euo pipefail

if [ "$(uname -s)" != Darwin ] || [ "$#" -ne 1 ] || [ ! -f "$1" ]; then
    echo "Usage: bash $0 /path/to/application.dmg (macOS only)" >&2
    exit 1
fi

DMG="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
WORK="$(mktemp -d "${TMPDIR:-/tmp}/standardnaast-dmg.XXXXXX")"
MOUNT="${WORK}/volume"
MOUNTED=false

cleanup() {
    if [ "${MOUNTED}" = true ]; then
        if ! hdiutil detach "${MOUNT}"; then
            echo "Could not detach ${MOUNT}; temporary files retained in ${WORK}." >&2
            return 1
        fi
    fi
    rm -rf -- "${WORK}"
}
trap cleanup EXIT

mkdir "${MOUNT}"
hdiutil convert "${DMG}" -format UDRW -o "${WORK}/writable.dmg"
hdiutil attach "${WORK}/writable.dmg" -nobrowse -mountpoint "${MOUNT}"
MOUNTED=true

if [ -L "${MOUNT}/Applications" ]; then
    if [ "$(readlink "${MOUNT}/Applications")" != /Applications ]; then
        echo "Unexpected Applications link in ${DMG}; original image left unchanged." >&2
        exit 1
    fi
elif [ -e "${MOUNT}/Applications" ]; then
    echo "Applications exists but is not a symlink; original image left unchanged." >&2
    exit 1
else
    ln -s /Applications "${MOUNT}/Applications"
fi

hdiutil detach "${MOUNT}"
MOUNTED=false
hdiutil convert "${WORK}/writable.dmg" -format UDZO -o "${WORK}/complete.dmg"
hdiutil verify "${WORK}/complete.dmg"
mv -f "${WORK}/complete.dmg" "${DMG}"
echo "Applications shortcut added to ${DMG}."
