#!/bin/bash

set -euo pipefail

# 批量生成样本脚本
# 用法:
#   ./batch_build.sh 100
#   ./batch_build.sh 50 15MB

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_SCRIPT="$SCRIPT_DIR/build.sh"
OUTPUT_DIR="$SCRIPT_DIR/bin"
BATCH_DIR="$OUTPUT_DIR/batch"

usage() {
    cat <<'EOF'
Usage: ./batch_build.sh <count> [target_size]

Arguments:
  count        要生成的样本组数（每组包含 win64 + win32 两个文件）
  target_size  可选，传递给 build.sh 的目标文件大小（如 15MB）

Examples:
  ./batch_build.sh 100
  ./batch_build.sh 50 15MB
  ./batch_build.sh 10 5M
EOF
}

if (( $# < 1 || $# > 2 )); then
    usage
    exit 1
fi

COUNT="$1"
SIZE_ARG="${2:-}"

if ! [[ "$COUNT" =~ ^[0-9]+$ ]] || (( COUNT < 1 )); then
    echo "[-] count 必须是正整数"
    usage
    exit 1
fi

mkdir -p "$BATCH_DIR"

echo "============================================"
echo "[*] 批量构建: 共 $COUNT 组样本"
if [[ -n "$SIZE_ARG" ]]; then
    echo "[*] 目标大小: $SIZE_ARG"
fi
echo "[*] 输出目录: $BATCH_DIR"
echo "============================================"
echo ""

SUCCESS=0
FAIL=0

for i in $(seq 1 "$COUNT"); do
    printf "[%d/%d] 正在构建第 %d 组样本...\n" "$i" "$COUNT" "$i"

    # 调用 build.sh
    if bash "$BUILD_SCRIPT" $SIZE_ARG; then
        # 用零填充序号，方便排序
        NUM=$(printf "%04d" "$i")

        mv "$OUTPUT_DIR/reverse_shell_win64.exe" "$BATCH_DIR/reverse_shell_win64_${NUM}.exe"
        mv "$OUTPUT_DIR/reverse_shell_win32.exe" "$BATCH_DIR/reverse_shell_win32_${NUM}.exe"

        SUCCESS=$((SUCCESS + 1))
    else
        echo "[-] 第 $i 组构建失败，跳过"
        FAIL=$((FAIL + 1))
    fi

    echo ""
done

echo "============================================"
echo "[*] 批量构建完成"
echo "[+] 成功: $SUCCESS 组"
if (( FAIL > 0 )); then
    echo "[-] 失败: $FAIL 组"
fi
echo "[*] 样本目录: $BATCH_DIR"
echo "[*] 文件总数: $(ls -1 "$BATCH_DIR"/*.exe 2>/dev/null | wc -l | tr -d ' ')"
echo "============================================"

# 输出所有样本的 SHA256
echo ""
echo "[*] 所有样本 SHA256:"
if command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "$BATCH_DIR"/*.exe
else
    sha256sum "$BATCH_DIR"/*.exe
fi
