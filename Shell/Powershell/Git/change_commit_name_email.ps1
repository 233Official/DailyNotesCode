# 遍历仓库中的所有提交, 将指定的提交者的名字和邮箱修改为新的名字和邮箱
git filter-branch --env-filter '
if [ "$GIT_COMMITTER_EMAIL" = "origin_email" ]; then
    export GIT_COMMITTER_EMAIL="new_email
fi
if [ "$GIT_AUTHOR_EMAIL" = "origin_email" ]; then
    export GIT_AUTHOR_EMAIL="new_email"
fi
if [ "$GIT_COMMITTER_NAME" = "origin_name" ]; then
    export GIT_COMMITTER_NAME="new_name"
fi
if [ "$GIT_AUTHOR_NAME" = "origin_name" ]; then
    export GIT_AUTHOR_NAME="new_name"
fi
' --tag-name-filter cat -- --branches --tags
