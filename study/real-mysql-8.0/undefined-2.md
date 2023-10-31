# 아키텍처

MySQL 엔진과 스토리지 엔진으로 구분할 수 있다\
스토리지 엔진은 핸들러 API를 만족하면 누구든지 스토리지 엔진을 구현해서,\
MYSQL 서버에 추가할 수 있다

#### Mysql Engine

<img src="../../.gitbook/assets/file.drawing (1) (4).svg" alt="" class="gitbook-drawing">

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

### MYSQL THREADING

MYSQL SERVER는 Thread기반이며 ForeGround, BackGround로 나눌 수 있다.

{% code title="동작중인 Thread 확인" %}
```sql
select * from performance_schema.threads ORDER BY TYPE, THREAD_IDs.
```
{% endcode %}

총 44개가 실행 중이며 41개는 백그라운드 3개는 포그라운드 스레드이다.\
`thread/sql/one_connection`만이 사용자 요청을 처리하는 포그라운드 스레드이다.

<img src="../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

`ForeGround Thread`\
접속한 클라이언트 수만큼 존새하며, 각 클라이언트가 요청하는 쿼리를 처리한다.\
클라이언트가 작업을 마치고 커넥션을 종료하면 쓰레드는 캐시 스레드로 전환되어 종료된다

`BackGround Thread`\
가장 중요한 것은 로그 스레드와 쓰기 스레드 이다. \
innodb\_write\_io & innodb\_read\_threads로 스레드 개수를 설정할 수 있다.

#### 메모리 할당& 사용 구조

글로벌 메모리 영역과 로컬 메모리 영역으로 구분할 수 있다.

글로벌 영역은 기본적으로 선점되는 메모리 영역이다.\
모든 스레드에 의해 공유되는 부분이며 시스템 설정과 관련되어 있어 모든 스레드가 접근할 수 있다.

로컬 메모리 영역은 클라이언트 스레드가 쿼리를 처리하는데 사용하는 메모리 영역이다.\
버퍼&소트를 담당하는 부분이라고도 할 수 있다.\
클라이언트 영역을 담당해 세션 영역이라고도 표현한다. \
각 스레드별로 독립적으로 할당되며, 절대 공유되어 사용되지 않는다.

#### 플러그인 스토리지 엔진 모델

MYSQL의 쿼리는 대부분 Mysql엔진에서 처리되고, 데이터 읽기&쓰기만 스토리지 엔진에 의해 처리된다.

<img src="../../.gitbook/assets/file.drawing (1).svg" alt=" " class="gitbook-drawing">

#### 실행 구조

Query Parser : 사용자의 요청으로 들어온 쿼리 문장을 토큰으로 분리해 트리 형태의 구조로 만들어 낸다.\
Preprocessor : Parser에서 만들어진 트리 기반으로 쿼리 문장에 구조적인 문제점을 확인한다.\
Optimizer : 가장 저렴한 비용으로 빠르게 처리하는 방식을 결정하는 역할이다.\
Executor Engine : 핸들러들을 이어주는 역할을 한다.\
Handler :  실행 요청에 따라 데이터를 처리하는 역할을 담당한다.\
QueryCache :  쿼리 실행 결과를 메모리에 캐시하고 동일 SQL을 즉시 반환해준다.\
Metadata : 테이블 구조와 저장 프로그램을 지칭한다.

## INNODB

유일하게 레코드 기반의 잠금을 제공하여, 동시성 처리가 가능하다

#### Primary Key

InnoDB의 모든 테이블은 PK를 기준으로 클러스터링 되어 저장된다.\
PK값 순서대로 저장된다는 뜻을 의미하며, PK를 기반으로한 조회처리는 상당히 빨리 처리될 수 있다

#### MVCC

record를 지원하는 DBMS가 제공하는 기능이며, 잠금을 사용하지 않는 일관된 읽기를 지향한다\
격리 수준에 따라 다른 쿼리 결과를 내는 방식이다, 레코드를 여러 버전으로 관리한다

MVCC기술로 잠금을 걸지 않고 읽기 작업을 수행할 수 있다.\
다른 트랜잭션의 잠금을 기다리지 않고 읽을 수 있으며 UNDO LOG를 사용한다.

#### 데드락 감지

내부적으로 교착상태에 빠지지 않았는지 확인하기 위해 그래프 형태로 관리한다.\
레코드를 가장 적게 가진 쪽이 강제 종료 대상이되며 innodb\_lock\_wait\_timeout으로 on\&off할수 있다.

### Buffer Pool

스토리지 엔진에 핵심 부분으로, 디스크파일과 인덱스 정보를 메모리에 캐시해 두는 공간이다.\
쓰기 작업을 지연시켜 일괄작업으로 처리할 수 있도록 해야한다

전체 메모리의 80%정도를 설정하라는 내용도 있지만, 조금더 고려하여 설정해야 한다.\
버퍼풀의 사이즈는 `innodb_buffer_pool_size`로 크기를 설정할 수 있으며, 동적으로 버퍼풀의 크기를 확장할 수 있다.

버퍼풀은 기본적으로 메모리의 페이징 기법과 유사하다.\
LRU, FLUSH, FREE 유형인 3개의 자료구조로 관리한다

LRU는 한번 읽어온 페이지를 오랫동안 유지하여 디스크 읽기를 줄이는 방식으로, [메모리의 LRU Cache](../books/undefined-4/undefined-7.md)와 유사하다.

Flush는 동기화 되지 않은 데이터를 가진 페이지의 변경 시점 기준으로 페이지 목록을 관리한다.\
데이터 변경이 없다면 플러시 목록에서 제외되지만 변경이 되면 디스크에 기록하는 작업을 거쳐야 한다.

<img src="../../.gitbook/assets/file.drawing (9).svg" alt="" class="gitbook-drawing">

들어온 데이터가 자주 사용되면 사용 될 수록 NEW에 가까워져 오래동안 메모리에 상주하며, OLD쪽 데이터들은 밀려나며 점차 제거된다.

### REDO LOG

SELECT로 이루어진 CLEAN PAGE와 INSERT UPDATE DELETE로 이루어진 DIRTYPAGE중\
DIRTY PAGE에 있는 데이터는 디스크에 기록되어야 하는데, 이 REDO LOG를 디스크에 반영하고 지우며 디스크와 데이터를 반영한다.\
DIRTY PAGE가 쓰기 폭발을 발생시킬 수 있는데, innodb\_max\_dirty\_pages\_lwm으로 디스크에 쓰는 비율을 설정할 수 있다

### UNDO LOG

트랜잭션과 격리 수준을 보장하기 위해 이전의 데이터를 별도로 백업하는 것을 지칭한다.\
트랜잭션이 롤백되면 UNDO LOG에 백업해둔 이전 버전의 데이터를 이용해 복구한다.\
트랜잭션중인 데이터를 UNDO LOG나 RECORD에서 선택하여 읽어올 수 있다.

#### Change Buffer

데이터가 변경될 때는, 인덱스도 업데이트 해야되는데 이때 사용하는 임시 메모리 결과이다.\
Innodb buffer pool의 25%까지 사용할 수 있게 설정 되어있다
