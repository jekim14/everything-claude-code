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

rm -rf "$OUT" raw-screens
mkdir -p "$OUT"
adb pull /data/local/tmp/ssukssuk-screens raw-screens
count=$(ls raw-screens/*.png | wc -l)
echo "캡처 $count 장"
if [ "$count" -lt 22 ]; then
  echo "::error::캡처가 부족합니다 ($count/22)"
  exit 1
fi

# 저장소 용량을 줄이기 위해 가로 1200px JPEG로 변환합니다.
python3 -m venv /tmp/capture-venv
/tmp/capture-venv/bin/pip install --quiet pillow
/tmp/capture-venv/bin/python - "$OUT" <<'PY'
import glob, os, sys
from PIL import Image
out = sys.argv[1]
for path in sorted(glob.glob("raw-screens/*.png")):
    image = Image.open(path).convert("RGB")
    width = 1200
    height = round(image.height * width / image.width)
    name = os.path.splitext(os.path.basename(path))[0] + ".jpg"
    image.resize((width, height), Image.LANCZOS).save(os.path.join(out, name), quality=82)
PY
ls -la "$OUT"
