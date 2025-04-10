#!/bin/bash

# 显示帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -oe, --old-email EMAIL    原始邮箱"
    echo "  -ne, --new-email EMAIL    新邮箱"
    echo "  -on, --old-name NAME      原始名称"
    echo "  -nn, --new-name NAME      新名称"
    echo "  -f,  --force              强制覆盖已有备份（默认开启）"
    echo "  -h,  --help               显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  仅修改邮箱: $0 -oe old@example.com -ne new@example.com"
    echo "  仅修改名称: $0 -on 'Old Name' -nn 'New Name'"
    echo "  同时修改两者: $0 -oe old@example.com -ne new@example.com -on 'Old Name' -nn 'New Name'"
    exit 0
}

# 初始化变量
OLD_EMAIL=""
NEW_EMAIL=""
OLD_NAME=""
NEW_NAME=""
FORCE="-f"  # 默认启用强制覆盖

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case "$1" in
        -oe|--old-email)
            OLD_EMAIL="$2"
            shift 2
            ;;
        -ne|--new-email)
            NEW_EMAIL="$2"
            shift 2
            ;;
        -on|--old-name)
            OLD_NAME="$2"
            shift 2
            ;;
        -nn|--new-name)
            NEW_NAME="$2"
            shift 2
            ;;
        -f|--force)
            FORCE="-f"
            shift
            ;;
        --no-force)
            FORCE=""
            shift
            ;;
        -h|--help)
            show_help
            ;;
        *)
            echo "错误: 未知选项 $1"
            show_help
            ;;
    esac
done

# 验证参数
if [[ -z "$OLD_EMAIL" && -z "$OLD_NAME" ]]; then
    echo "错误: 至少需要指定原始邮箱或原始名称"
    show_help
fi

if [[ -n "$OLD_EMAIL" && -z "$NEW_EMAIL" ]]; then
    echo "错误: 指定了原始邮箱，但未指定新邮箱"
    exit 1
fi

if [[ -n "$OLD_NAME" && -z "$NEW_NAME" ]]; then
    echo "错误: 指定了原始名称，但未指定新名称"
    exit 1
fi

# 构建确认信息
CONFIRM_MSG="将修改:"
if [[ -n "$OLD_EMAIL" ]]; then
    CONFIRM_MSG="$CONFIRM_MSG\n- 邮箱: '$OLD_EMAIL' -> '$NEW_EMAIL'"
fi
if [[ -n "$OLD_NAME" ]]; then
    CONFIRM_MSG="$CONFIRM_MSG\n- 名称: '$OLD_NAME' -> '$NEW_NAME'"
fi

echo -e "$CONFIRM_MSG"
echo "按 Ctrl+C 取消或按 Enter 继续..."
read

# 构建 git filter-branch 命令的环境过滤器
ENV_FILTER=""
if [[ -n "$OLD_EMAIL" ]]; then
    ENV_FILTER+="
if [ \"\$GIT_COMMITTER_EMAIL\" = \"$OLD_EMAIL\" ]; then
    export GIT_COMMITTER_EMAIL=\"$NEW_EMAIL\"
fi
if [ \"\$GIT_AUTHOR_EMAIL\" = \"$OLD_EMAIL\" ]; then
    export GIT_AUTHOR_EMAIL=\"$NEW_EMAIL\"
fi"
fi

if [[ -n "$OLD_NAME" ]]; then
    ENV_FILTER+="
if [ \"\$GIT_COMMITTER_NAME\" = \"$OLD_NAME\" ]; then
    export GIT_COMMITTER_NAME=\"$NEW_NAME\"
fi
if [ \"\$GIT_AUTHOR_NAME\" = \"$OLD_NAME\" ]; then
    export GIT_AUTHOR_NAME=\"$NEW_NAME\"
fi"
fi

# 运行 git filter-branch 命令
git filter-branch $FORCE --env-filter "$ENV_FILTER" --tag-name-filter cat -- --branches --tags

echo "完成！请检查结果，如果满意，可能需要使用 git push --force 推送更改"
echo "注意：请确保你了解 git push --force 的风险"