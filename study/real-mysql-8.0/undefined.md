# 설정

교재와 달리로 컨테이너로 진행



### 컨테이너 생성.

`docker-compose up -d`

{% code title="docker-compose.yml" overflow="wrap" lineNumbers="true" %}
```yaml
version: '3'
services:
  junny-db:
    image: mysql:8.0
    container_name: junny-container
    restart: always
    ports:
      - 3336:3306
    environment:
      MYSQL_ROOT_PASSWORD: 1234
      TZ: Asia/Seoul
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    volumes:
      - ./init:/docker-entrypoint-initdb.d
    platform: linux/x86_64
```
{% endcode %}

{% code title="./init/default.sql" overflow="wrap" lineNumbers="true" %}
```sql

# 기본 설정사항 작성
```
{% endcode %}

#### &#x20;설정 정보

```
host : IP:3336
USER : ROOT
PASSWORD : 1234
```

#### 설정 확인

```
mysql -uroot -p1234

> SELECT CURRENT_USER();
+----------------+
| CURRENT_USER() |
+----------------+
| root@localhost |
+----------------+
1 row in set (0.00 sec)

```



### 버전 업그레이드

#### In-place upgrade

서버의 데이터 파일을 전부 둔채 업그레이드\
마이너 버전의 업그레이든 데이터 파일의 변경없이 진행되며 여러 버전을 건너뛰고 업그레이드 할 수 있다.\
메이저 버전의 업그레이드는 한 단계의 메이저 버전씩 가능하다

#### Logical upgrade

데이터를 덤프한 후 새 버전에 덤프된 데이터를 적재

```bash
mysqld -Uroot -p 1234 --all-databases --check-upgrade
```



### 설정 파일

설정은 my.cnf 파일을 확인한다.

{% hint style="info" %}
/etc/my.cnf , /etc/mysql/my.cnf , /usr/etc/my.cnf 등  다양하다.
{% endhint %}

#### 글로벌 변수

SQL 서버 전체에 영향을 끼치는 시스템 변수를 의미한다.

#### 세션 변수

MYSQL 클라이언트가 서버에 접속할 때 기본적으로 부여하는 옵션의 기본값을 제어하는데 사용한다.

