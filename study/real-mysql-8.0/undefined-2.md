# 아키텍처

MySQL 엔진과 스토리지 엔진으로 구분할 수 있다\
스토리지 엔진은 핸들러 API를 만족하면 누구든지 스토리지 엔진을 구현해서,\
MYSQL 서버에 추가할 수 있다

#### Mysql Engine

<img src="../../.gitbook/assets/file.drawing (1).svg" alt="" class="gitbook-drawing">

Mysql은 대부분의 프로그래밍 언어로 부터 접근 드라이버를 모두 지원한다\
클라이언트로부터의 접속 및 쿼리 요청을 처리하는 Connection Handler,SQL Parser,전 처리기, 쿼리 최적화된 실행을 위한 옵티마이저가 중심을 이룬다

#### Storage Engine

디스크 스토리지로부터 데이터를 읽어오는 부분을 담당한다.\
Mysql서버는 하나지만 Storage서버는 여러개를 동시에 사용할 수 있다.

#### Handler API

Mysql 엔진의 쿼리 실행기에서 데이터를 쓰거나 읽어야 할 때는\
스토리지 엔진에 쓰기&읽기 요청하는데 이 과정을 Handler 요청이라고 한다.

{% code title="레코드 작업량 확인" %}
```sql
> SHOW GLOBAL STATUS LIKE 'Handler%';
```
{% endcode %}

