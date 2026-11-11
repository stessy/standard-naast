#!/usr/bin/env bash
set -euo pipefail

tag="${RELEASE_TAG:?RELEASE_TAG is required}"
if [[ ! "$tag" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Release tag must have the form v1.2.3." >&2
    exit 1
fi

if [[ "${1:-}" == "--validate-tag" ]]; then
    exit 0
fi

asset="${1:?An installer path is required}"
test -s "$asset"
: "${GITHUB_REPOSITORY:?GITHUB_REPOSITORY is required}"
: "${GITHUB_SHA:?GITHUB_SHA is required}"

error_file="$(mktemp)"
trap 'rm -f "$error_file"' EXIT

tag_exists=false
if tag_sha="$(gh api "repos/$GITHUB_REPOSITORY/commits/$tag" --jq .sha 2>"$error_file")"; then
    tag_exists=true
    if [[ "$tag_sha" != "$GITHUB_SHA" ]]; then
        echo "Tag $tag points to $tag_sha, not the built commit $GITHUB_SHA." >&2
        exit 1
    fi
elif ! grep -q 'HTTP 404' "$error_file"; then
    cat "$error_file" >&2
    exit 1
fi

if gh api "repos/$GITHUB_REPOSITORY/releases/tags/$tag" --jq .tag_name > /dev/null 2>"$error_file"; then
    if [[ "$tag_exists" != true ]]; then
        echo "Release $tag exists but its tag could not be resolved." >&2
        exit 1
    fi
else
    if ! grep -q 'HTTP 404' "$error_file"; then
        cat "$error_file" >&2
        exit 1
    fi
    if [[ "$tag_exists" == true ]]; then
        gh release create "$tag" --verify-tag --title "$tag" --notes "Installateurs Standard Naast."
    else
        gh release create "$tag" --target "$GITHUB_SHA" --title "$tag" --notes "Installateurs Standard Naast."
    fi
fi

tag_sha="$(gh api "repos/$GITHUB_REPOSITORY/commits/$tag" --jq .sha)"
if [[ "$tag_sha" != "$GITHUB_SHA" ]]; then
    echo "Tag $tag points to $tag_sha, not the built commit $GITHUB_SHA." >&2
    exit 1
fi

gh release upload "$tag" "$asset" --clobber
