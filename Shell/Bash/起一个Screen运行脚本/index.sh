#!/bin/bash

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
echo "当前脚本所在目录: $SCRIPT_DIR"

# 若当前不存在名为 TargetScreen 的 screen, 则 新建一个 screen, 名为 TargetScreen, 并在其中运行 python FileServer/TargetScreen.py 然后 detach
is_TargetScreen_screen_exist=$(screen -ls | grep TargetScreen)
if [ -n "$is_TargetScreen_screen_exist" ]; then
    echo "TargetScreen screen 已存在"
else
    echo "TargetScreen screen 不存在, 进入创建流程......"
    screen -dmS TargetScreen bash -c "cd $SCRIPT_DIR;source .pyfs/bin/activate;python fileserver.py"
    echo "TargetScreen screen 已启动"
fi
