import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import path from 'node:path';
import { test } from 'node:test';

const script = path.resolve('.github/scripts/publish-release.sh');
const mock = `#!/usr/bin/env bash
printf '%s\\n' "$*" >> "$MOCK_LOG"
if [[ "$1" == api && "$2" == */commits/* ]]; then
  if [[ "$MOCK_CASE" == forbidden ]]; then
    echo 'gh: Forbidden (HTTP 403)' >&2
    exit 1
  fi
  if [[ "$MOCK_CASE" == new && ! -f "$MOCK_CREATED" ]]; then
    echo 'gh: Not Found (HTTP 404)' >&2
    exit 1
  fi
  if [[ "$MOCK_CASE" == mismatch ]]; then
    echo other-commit
  else
    echo built-commit
  fi
elif [[ "$1" == api && "$2" == */releases/tags/* ]]; then
  if [[ "$MOCK_CASE" == existing ]]; then
    echo v1.1.0
  else
    echo 'gh: Not Found (HTTP 404)' >&2
    exit 1
  fi
elif [[ "$1" == release && "$2" == create ]]; then
  touch "$MOCK_CREATED"
elif [[ "$1" != release || "$2" != upload ]]; then
  echo "Unexpected command: $*" >&2
  exit 1
fi
`;

function runPublish(scenario, tag = 'v1.1.0') {
  const dir = mkdtempSync(path.join(tmpdir(), 'publish-release-test-'));
  try {
    const asset = path.join(dir, 'Standard Naast-1.1.0.dmg');
    const log = path.join(dir, 'calls');
    writeFileSync(path.join(dir, 'gh'), mock, { mode: 0o755 });
    writeFileSync(asset, 'installer');
    writeFileSync(log, '');
    const result = spawnSync('bash', [script, asset], {
      encoding: 'utf8',
      env: {
        ...process.env,
        PATH: `${dir}${path.delimiter}${process.env.PATH}`,
        RELEASE_TAG: tag,
        GITHUB_REPOSITORY: 'owner/repo',
        GITHUB_SHA: 'built-commit',
        MOCK_CASE: scenario,
        MOCK_LOG: log,
        MOCK_CREATED: path.join(dir, 'created')
      }
    });
    assert.ifError(result.error);
    return { ...result, calls: readFileSync(log, 'utf8') };
  } finally {
    rmSync(dir, { recursive: true, force: true });
  }
}

test('creates a release at the built commit and uploads the installer', () => {
  const result = runPublish('new');
  assert.equal(result.status, 0, result.stderr);
  assert.match(result.calls, /release create v1\.1\.0 --target built-commit/);
  assert.match(result.calls, /release upload v1\.1\.0 .*Standard Naast-1\.1\.0\.dmg --clobber/);
});

test('creates a release for an existing tag at the built commit', () => {
  const result = runPublish('tag-only');
  assert.equal(result.status, 0, result.stderr);
  assert.match(result.calls, /release create v1\.1\.0 --verify-tag/);
});

test('uploads to an existing release without recreating it', () => {
  const result = runPublish('existing');
  assert.equal(result.status, 0, result.stderr);
  assert.doesNotMatch(result.calls, /release create/);
  assert.match(result.calls, /release upload/);
});

test('rejects a tag pointing to a different commit', () => {
  const result = runPublish('mismatch');
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /not the built commit/);
  assert.doesNotMatch(result.calls, /release (create|upload)/);
});

test('does not treat an API permission error as a missing tag', () => {
  const result = runPublish('forbidden');
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /HTTP 403/);
  assert.doesNotMatch(result.calls, /release (create|upload)/);
});

test('rejects invalid tags before contacting GitHub', () => {
  const result = runPublish('new', 'not-a-version');
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /form v1\.2\.3/);
  assert.equal(result.calls, '');
});
