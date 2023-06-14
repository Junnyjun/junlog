# Author 변경

### 전체 변경

```bash
> git filter-branch --env-filter '
WRONG_EMAIL="{잘못된 이메일 주소}"
NEW_NAME="{바꿀 이름}"
NEW_EMAIL="{바꿀 이메일 주소}"

if [ "$GIT_COMMITTER_EMAIL" = "$WRONG_EMAIL" ]
then
    export GIT_COMMITTER_NAME="$NEW_NAME"
    export GIT_COMMITTER_EMAIL="$NEW_EMAIL"
fi
if [ "$GIT_AUTHOR_EMAIL" = "$WRONG_EMAIL" ]
then
    export GIT_AUTHOR_NAME="$NEW_NAME"
    export GIT_AUTHOR_EMAIL="$NEW_EMAIL"
fi
' --tag-name-filter cat -- --branches --tags
```

### 개별 변경

#### 범위 지정

```bash
> git rebase -i HEAD~{개수}
```

