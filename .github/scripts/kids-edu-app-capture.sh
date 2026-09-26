#!/usr/bin/env bash
# 에뮬레이터에 앱과 계측 테스트를 설치하고 화면 캡처 테스트를 실행한 뒤 결과를 가져옵니다.
set -euo pipefail

PKG=com.ssukssuk.playground
OUT=docs/screenshots

# 처음 전체 화면으로 전환할 때 나오는 시스템 안내가 캡처를 가리지 않도록 합니다.
adb shell settings put secure immersive_mode_confirmations confirmed

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

timeout 900 adb shell am instrument -w -r \
  -e class "$PKG.ScreenshotTour" \
  "$PKG.test/androidx.test.runner.AndroidJUnitRunner" | tee instrument.log

if grep -qE "FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed" instrument.log; then
  echo "::error::화면 캡처 테스트 실패"
  adb logcat -d -s AndroidRuntime:E | tail -80
  exit 1
fi

rm -rf "$OUT"
mkdir -p "$OUT"
for f in $(adb shell run-as "$PKG" ls files/screenshots | tr -d '\r'); do
  adb exec-out run-as "$PKG" cat "files/screenshots/$f" > "$OUT/$f"
done
ls -la "$OUT"
