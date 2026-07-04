#!/usr/bin/env bash
set -euo pipefail

REPLACEMENTS="$(mktemp)"
trap 'rm -f "$REPLACEMENTS"' EXIT

cat >"$REPLACEMENTS" <<'EOF'
literal:e14cfacdd442a953343ebd8529138680==>REDACTED_JENKINS_BUILD_MARKER
EOF

command -v git-filter-repo >/dev/null || exit 127

git filter-repo --force --replace-text "$REPLACEMENTS"
