# Setting

## SET UP

### 1. application 띄우기

```bash
> wget https://raw.githubusercontent.com/hasura/graphql-engine/stable/install-manifests/docker-compose/docker-compose.yaml
> docker compose up -d
```

### 2. database connect

<figure><img src="../../../.gitbook/assets/image (5).png" alt=""><figcaption></figcaption></figure>

옵션을 선택 `Environment Variable`하고 `PG_DATABASE_URL`환경 변수 이름으로 입력

<figure><img src="../../../.gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>

### 3. setup demmy

테이블 생성

<figure><img src="../../../.gitbook/assets/image (8).png" alt=""><figcaption></figcaption></figure>

query 사용

```graphql
query {
  profiles {
    id
    name
  }
}
```

<figure><img src="../../../.gitbook/assets/image (7).png" alt=""><figcaption></figcaption></figure>
