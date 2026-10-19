#!/usr/bin/env bash
set -euo pipefail

root="${1:-.}"
blocked='\.(apk|aab|obb|iwd|pak|ff|bik|exe|dll|so|zip|7z|rar|jks|keystore)$'

violations="$(git -C "$root" ls-files | grep -E -i "$blocked" || true)"
if [[ -n "$violations" ]]; then
  echo "Proprietary or binary payload extensions are not allowed in Git:"
  echo "$violations"
  exit 1
fi

oversized="$(git -C "$root" ls-files -z | while IFS= read -r -d '' file; do
  size=$(git -C "$root" cat-file -s ":$file")
  if (( size > 10485760 )); then printf '%s (%s bytes)\n' "$file" "$size"; fi
done)"
if [[ -n "$oversized" ]]; then
  echo "Unexpected tracked files larger than 10 MiB:"
  echo "$oversized"
  exit 1
fi

echo "Proprietary payload check passed."
