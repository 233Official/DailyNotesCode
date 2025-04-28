#!/bin/bash

# 设置当前工作目录的 .prefect 目录为 PREFECT_HOME
export PREFECT_HOME="$(pwd)/.prefect"

# 确保目录存在
mkdir -p "$PREFECT_HOME"

# 显示配置信息
echo "已设置 PREFECT_HOME=$PREFECT_HOME"

# 检测是否在Windows的Git Bash环境下
if [[ "$(uname -s)" == MINGW* || "$(uname -s)" == CYGWIN* ]]; then
    # Windows下的Git Bash或Cygwin
    ACTIVATE_FILE=".venv/Scripts/activate"
else
    # 普通Unix环境
    ACTIVATE_FILE=".venv/bin/activate"
fi

if [ -f "$ACTIVATE_FILE" ]; then
    # 检查是否已经添加了我们的配置
    if ! grep -q "# 自动设置 PREFECT_HOME" "$ACTIVATE_FILE"; then
        echo "修改 $ACTIVATE_FILE 以在激活环境时自动设置 PREFECT_HOME..."
        
        # 向激活脚本添加设置 PREFECT_HOME 的命令
        cat << 'EOF' >> "$ACTIVATE_FILE"

# 自动设置 PREFECT_HOME
export PREFECT_HOME="$(dirname "$VIRTUAL_ENV")/.prefect"
mkdir -p "$PREFECT_HOME"
echo "已自动设置 PREFECT_HOME=$PREFECT_HOME"
EOF
        
        echo "配置已添加到激活脚本中。今后每次激活环境时将自动设置 PREFECT_HOME。"
    else
        echo "激活脚本已包含 PREFECT_HOME 设置，无需修改。"
    fi
else
    echo "警告：找不到虚拟环境激活脚本 ($ACTIVATE_FILE)。请确保已创建 Poetry 虚拟环境。"
fi