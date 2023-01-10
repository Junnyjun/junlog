# 권한

## 사용자

MYSQL에서의 권한은 접속하는 IP까지도 확인 하는 차이점이 있다\
8.0 버전 부터는 묶어서 관리하는 ROLE 개념도 추가되어, 미리 준비된 권한을 부여하는 것도 가능하다.\
사용자 계정이 유사한(junny@'127.0.0.1' , junny@'%' ) 경우 범위가 가장 작은것(127.0.0.1)을 우선 선택한다.

#### 시스템 계정

MYSQL 8.0 부터 SYSTEM\_USER 권한에 따라 시스템 계정과 일반 계정이 구분된다.\
서버 관리자를 위한 계정이며 계쩡관리 세션, 쿼리 강제종료 등 시스템 권한이 주어진다.

### 계정 생성

아래는 계정 생성을 위한 명령어 유형이다.

SSL/TLS 설정 및 비밀번호 유효기간, 재사용 금지 등 옵션을 선택할 수 있다.

{% code title="CREATE USER" %}
```sql
CREATE USER [IF NOT EXISTS]
    user [auth_option] [, user [auth_option]] ...
    DEFAULT ROLE role [, role ] ...
    [REQUIRE {NONE | tls_option [[AND] tls_option] ...}]
    [WITH resource_option [resource_option] ...]
    [password_option | lock_option] ...
    [COMMENT 'comment_string' | ATTRIBUTE 'json_object']

user:
    (see Section 6.2.4, “Specifying Account Names”)

auth_option: {
    IDENTIFIED BY 'auth_string' [AND 2fa_auth_option]
  | IDENTIFIED BY RANDOM PASSWORD [AND 2fa_auth_option]
  | IDENTIFIED WITH auth_plugin [AND 2fa_auth_option]
  | IDENTIFIED WITH auth_plugin BY 'auth_string' [AND 2fa_auth_option]
  | IDENTIFIED WITH auth_plugin BY RANDOM PASSWORD [AND 2fa_auth_option]
  | IDENTIFIED WITH auth_plugin AS 'auth_string' [AND 2fa_auth_option]
  | IDENTIFIED WITH auth_plugin [initial_auth_option]
}

2fa_auth_option: {
    IDENTIFIED BY 'auth_string' [AND 3fa_auth_option]
  | IDENTIFIED BY RANDOM PASSWORD [AND 3fa_auth_option]
  | IDENTIFIED WITH auth_plugin [AND 3fa_auth_option]
  | IDENTIFIED WITH auth_plugin BY 'auth_string' [AND 3fa_auth_option]
  | IDENTIFIED WITH auth_plugin BY RANDOM PASSWORD [AND 3fa_auth_option]
  | IDENTIFIED WITH auth_plugin AS 'auth_string' [AND 3fa_auth_option]
}

3fa_auth_option: {
    IDENTIFIED BY 'auth_string'
  | IDENTIFIED BY RANDOM PASSWORD
  | IDENTIFIED WITH auth_plugin
  | IDENTIFIED WITH auth_plugin BY 'auth_string'
  | IDENTIFIED WITH auth_plugin BY RANDOM PASSWORD
  | IDENTIFIED WITH auth_plugin AS 'auth_string'
}

initial_auth_option: {
    INITIAL AUTHENTICATION IDENTIFIED BY {RANDOM PASSWORD | 'auth_string'}
  | INITIAL AUTHENTICATION IDENTIFIED WITH auth_plugin AS 'auth_string'
}

tls_option: {
   SSL
 | X509
 | CIPHER 'cipher'
 | ISSUER 'issuer'
 | SUBJECT 'subject'
}

resource_option: {
    MAX_QUERIES_PER_HOUR count
  | MAX_UPDATES_PER_HOUR count
  | MAX_CONNECTIONS_PER_HOUR count
  | MAX_USER_CONNECTIONS count
}

password_option: {
    PASSWORD EXPIRE [DEFAULT | NEVER | INTERVAL N DAY]
  | PASSWORD HISTORY {DEFAULT | N}
  | PASSWORD REUSE INTERVAL {DEFAULT | N DAY}
  | PASSWORD REQUIRE CURRENT [DEFAULT | OPTIONAL]
  | FAILED_LOGIN_ATTEMPTS N
  | PASSWORD_LOCK_TIME {N | UNBOUNDED}
}

lock_option: {
    ACCOUNT LOCK
  | ACCOUNT UNLOCK
}
```
{% endcode %}

#### 이중 비밀번호

새로 설정한 비밀번호는 PRIMARY, 이전 비밀번호는 Secondary로 구분한다.\
새 비밀번호 설정이 완료되면 `DISCARD OLD PASSWORD`로 secondary 비밀번호를 비활성화 시킬 수 있다

### 권한

테이블 이외에 객체에 적용되는 권한은 글로벌 권한, \
데이터베이스와 테이블을 제어하는 권한은 객체 권한 이라고한다\
\
8.0버전 부터는 정적 권한과 동적 권한으로 나뉘어 지는데,\
정적권한은 서버 소스코드에 명시되어있는 권한이며, 동적 권한은 MYSQL서버가 시작되며 실행되는 권한이다.

#### 권한

권한 부여는 GRANT를 사용한다. \
`GRANT {authority} ON {target} TO {USER}`의 형태를 가진다

#### 역할

권한을 묶어 역할을 사용한다.

{% code title="MAKE ROLE" %}
```sql
> CREATE ROLE
 role_emp_read
 role_emp_write;
 
> GRANT {AUTHORITY} ON {DATABASE} TO {USER};
```
{% endcode %}

