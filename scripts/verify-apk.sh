#!/usr/bin/env bash
set -euo pipefail

apk="${1:?usage: verify-apk.sh path/to.apk}"
sdk_root="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
apkanalyzer_bin="${APKANALYZER:-${sdk_root:+$sdk_root/cmdline-tools/latest/bin/apkanalyzer}}"
if [[ -z "$apkanalyzer_bin" ]]; then apkanalyzer_bin=apkanalyzer; fi
apksigner_bin="${APKSIGNER:-${sdk_root:+$sdk_root/build-tools/36.0.0/apksigner}}"
if [[ -z "$apksigner_bin" ]]; then apksigner_bin=apksigner; fi

"$apkanalyzer_bin" files list "$apk" | grep -q '^lib/arm64-v8a/libvanguard_sp.so$'
"$apkanalyzer_bin" files list "$apk" | grep -q '^lib/arm64-v8a/libvanguard_mp.so$'
if "$apkanalyzer_bin" files list "$apk" | grep -E '^lib/(armeabi|armeabi-v7a|x86|x86_64)/'; then
  echo "Unexpected non-arm64 ABI found."
  exit 1
fi
"$apksigner_bin" verify --verbose "$apk"
echo "APK ABI and signature checks passed."
