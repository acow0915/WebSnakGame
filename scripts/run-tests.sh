#!/bin/bash

# 測試腳本：執行 Maven 的測試階段。
set -euo pipefail
cd "$(dirname "$0")/.."
echo "Running full Maven test suite..."
mvn test
