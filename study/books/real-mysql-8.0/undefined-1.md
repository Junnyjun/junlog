# 트랜잭션과 잠금

* 이번 장에서는 잠금과 트랜잭션, 트랜잭션의 격리 수준에 대해 살펴본다.
* 트랜잭션이란? 작업의 완전성을 보장해주는 것이다. => All or Nothing
* 잠금이란 동시성을 제어하는 기능이고 트랜잭션은 데이터의 정합성을 보장하기 위한 기능이다.
* 격리수준이라는 것은 하나의 트랜잭션 내에서 또는 여러 트랜잭션 간의 작업 내용을 어디까지 공유하고 차단할 것인지를 결정하는 레벨을 의미한다.

### 5.1 트랜잭션

* 데이터의 정합성 보장해주는 기능이다.
* 논리적인 작업의 Set이 100% 적용되거나 아무것도 적용되지 않아야 함을 보장해주는 것

#### 5.1.1 MySQL에서의 트랜잭션

* InnoDB 스토리지 엔진에서는 트랜잭션을 지원하지만 MyISAM,MEMORY 스토리지 엔진에서는 트랜잭션을 지원하지 않는다.
* 트랜잭션을 지원하지 않으면 개발자가 이를 구현해야 함을 의미한다.
  * 의도치 않은 Partial Update 가 발생하면 이를 Rollback 하는 데에 어려움이 생길 수 있다.

#### 5.1.2 주의사항

* 코드에서 데이터베이스 커넥션을 가지고 있는 범위와 트랜잭션의 범위를 최소화하라
* 네트워크 작업이 있는 경우에는 반드시 트랜잭션에서 배제해야 한다.

### 5.2 MySQL 엔진의 잠금

* MySQL에서 사용되는 잠금은 크게 스토리지 엔진 레벨과 MySQL엔진 레벨로 나눌 수 있다.
* MySQL 엔진 레벨 잠금은 스토리지 엔진에 영향을 미치지만, 스토리지 엔진 레벨의 장금은 상호 영향을 미치지 않는다.
* 종류
  * 테이블 락 : 테이블 데이터 동기화를 위한 락
  * 메타데이터 락 : 테이블의 구조를 잠금
  * 네임드 락 : 사용자 커스텀 락

#### 5.2.1 글로벌 락

* FLUSH TABLES WITH READ LOCK 명령으로 획득 할 수 있다. MySQL에서 제공하는 잠금 중에 가장 범위가 크다.
* 일단 한 세션에서 글로벌 락을 획득하면 다른 세션에서 SELECT를 제외한 대부분의 DDL, DML을 실행하면 글로벌 락이 해제될 때까지 대기 상태로 남는다.
  * 여러 데이터베이스에 존재하는 MyISAM, MEMORY 테이블에 대해 mysqldump로 일관된 백업을 받을 때 글로벌 락을 사용해야 한다.
* InnoDB 스토리지 엔진을 사용하면서 보다 가벼운 글로벌 락의 필요성이 생겼고 MySQL 8.0 부터는 Xtrabackup 이나 Enterprise Backup 과 같은 백업 툴들의 안정적인 실행을 위해 백업 락이 도입되었다.
  * LOCK INSTANCE FOR BACKUP;
  * UNLOCK INSTANCE;

#### 5.2.2 테이블 락

* 개별 테이블 단위로 설정되는 잠금
* 명시적 또는 묵시적으로 특정 테이블 락을 획득할 수 있다.
* 명시적 테이블 락은 아래와 같은 명령으로 사용할 수 있으며 특별한 상황이 아니면 잘 사용하지 않는다.

```
LOCK TABLES table_name [READ | WRITE]
UNLOCK TABLES

```

* 묵시적 테이블 락은 MyISAM, MEMORY 스토리지 엔진에서 쿼리가 실행되는 동안 자동으로 획득했다가 쿼리가 완료된 후 자동 해제된다.
* InnoDB 테이블의 경우 스토리지 엔진 차원에서 레코드 기반의 잠금을 제공하기 때문에 단순 데이터 변경 쿼리로 테이블 락이 설정되지는 않는다.
  * 스키마를 변경하는 DDL의 경우에만 설정한다.

#### 5.2.3 네임드 락

* GET\_LOCK() 함수를 이용해 임의의 문자열에 대해 잠금을 설정할 수 있다.
* 데이터베이스 1대에 여러대의 웹서버가 접속하는 경우 동기화를 위해 네임드 락을 사용해서 해결하기도 한다.
* 하지만 잘 사용하지 않는다. (다른 방식으로 해결책을 구하는게 더 나은 경우가 많음)

#### 5.2.4 메타데이터 락

* 데이터베이스 객체(테이블이나 뷰 등)의 이름이나 구조를 변경하는 경우에 획득하는 잠금
* 명시적으로 획득하거나 해제할 수 없다.

```
//아래의 쿼리의 경우 Table not found 'rank' 같은 상황을 발생시키지 않음
RENAME TABLE rank TO rank_backup, rank_new TO rank;

```

```
//아래의 쿼리의 경우 Table not found 'rank' 발생 할 수 있음
RENAME TABLE rank TO rank_backup;
//이 순간 rank 조회 하면 오류
RENAME TABLE rank_new TO rank;

```

### 5.3 InnoDB 스토리지 엔진 잠금

* InnoDB 스토리지 엔진은 MySQL에서 제공하는 잠금과는 별개로 스토리지 엔진 내부에서 레코드 기반의 잠금 방식을 탑재하고 있다.
  * 이 방식 때문에 MyISAM보다 훨씬 뛰어난 동시성 처리를 제공할 수 있다.
* information\_schema 데이터베이스의 INNODB\_TRX, INNODB\_LOCKS, INNODB\_LOCK\_WAITS 테이블을 조인해서 조회하면 현재 어떤 트랜잭션이 어떤 잠금을 대기하고 있고, 해당 잠금을 어느 트랜잭션이 가지고 있는지 확인할 수 있다.
* InnoDB 잠금에 대한 모니터링도 더 강화되면서 Performance Schema를 이용해서 내부 잠금(세마포어)에 대한 모니터링 방법도 추가되었다.

#### 5.3.1 InnoDB 스토리지 엔진의 잠금

#### 5.3.1.1 레코드 락

* 레코드자체 만을 잠그는 것
* 다른 사용 DBMS의 레코드 락과 동일한 역할을 한다.
* InnoDB 스토리지 엔진은 레코드 자체가 아니라 인덱스의 레코드를 잠근다.
  * 인덱스가 하나도 없는 테이블이라도 내부적으로 자동 생성된 클러스터 인덱스를 이용해 잠금을 설정한다.
* 프라이머리 키 또는 유니크 인덱스에 의한 변경 작업에서 사용
* 보조 인덱스를 이용한 변경작업은 갭 락, 넥스트 키 락을 사용

#### 5.3.1.2 갭 락

* 레코드 자체가 아니라 레코드와 인접한 레코드 사이의 간격만을 잠그는 것

#### 5.3.1.3 넥스트 키 락

* 레코드 락과 갭 락을 합쳐 놓은 형태의 잠금
* STATEMENT 포맷의 바이너리 로그를 사용하는 MySQL 서버에서는 REPEATABLE READ 격리 수준을 사용해야 한다.
  * 넥스트 키 락과 갭 락으로 데드락이 발생하거나 다른 트랜잭션을 기다리게 하는 일이 자주 발생한다.
  * 가능하면 ROW 형태의 바이너리 로그를 사용해서 넥스트 키 락이나 갭 락을 줄이는게 좋다.
* MySQL 8.0 에서는 ROW 포맷의 바이너리 로그가 기본 설정으로 변경되었다.

#### 5.3.1.4 자동증가 락

* MySQL에서는 자동 증가하는 숫자 값을 채번하기 위해 AUTO\_INCREMENT라는 컬럼 속성을 제공한다.
* 해당 컬럼 속성을 사용하는 테이블에 인서트할 때 InnoDB 스토리지 엔진에서는 내부적으로 AUTO\_INCREMENT 락이라는 테이블 수준의 잠금을 사용한다.
* AUTO\_INCREMENT 락은 INSERT와 REPLACE 쿼리 문장과 같이 새로운 레코드를 저장하는 쿼리에서만 필요하며, UPDATE, DELETE 등의 쿼리에서는 걸리지 않는다.
  * AUTO\_INCREMENT 락을 명시적으로 획득하고 해제하는 방법은 없다.
* MySQL 5.1 이상부터는 innodb\_autoinc\_lock\_mode라는 시스템 변수를 이용해서 자동 증가 락의 작동 방식을 변경할 수 있다.
* innodb\_autoinc\_lock\_mode=0
  * MySQL 5.0과 동일한 방식
* innodb\_autoinc\_lock\_mode=1
  * 단순히 한 건 또는 여러 건의 레코드를 INSERT 하는 SQL 중에서 레코드 건수를 정확히 예측 할 수 있을 때는 자동 증가 락을 사용하지 않고, 훨씬 빠른 래치(뮤텍스)를 이용해 처리한다.
  * INSERT... SELECT와 같이 서버가 건수를 예측할 수 없을 때, 대량의 INSERT를 수행할 때에는 여러 개의 자동 증가 값을 할당 받아서 사용한다. 이 때 남는 자동 증가 값은 폐기되므로 누락된 값이 발생할 수 있다. 이 설정에서는 최소한 하나의 INSERT 문장으로 INSERT 되는 레코드는 연속된 자동 증가 값을 가지게 된다.
* innodb\_autoinc\_lock\_mode=2
  * 절대 자동증가 락을 걸지 않고 경량화된 래치(뮤텍스)를 사용한다.
  * 연속된 자동 증가 값을 보장하지 않는다.
  * 이 설정에서는 자동 증가 시 유니크한 값이 생성된다는 것만 보장한다.

#### 5.3.2 인덱스와 잠금

* InnoDB의 잠금은 레코드를 잠그는 것이 아니라 인덱스를 잠그는 방식으로 처리된다. 즉변경해야 할 레코드를 찾기 위해 검색한 인덱스의 레코드를 모두 락을 걸어야 한다.
* 만약 테이블의 인덱스가 하나도 없다면 어떻게 될까? 테이블을 풀 스캔하면서 모든 레코드를 잠그게 된다.

#### 5.3.3 레코드 수준의 잠금 확인 및 해제

* MySQL 5.1부터는 레코드 잠금과 잠금 대기에 대한 조회가 가능하므로 쿼리 하나만 실행해 보면 잠금과 잠금 대기를 바로 확인할 수 있다.
* MySQL 5.1부터는 information\_schema DB의 INNODB\_TRX라는 테이블과 INNODB\_LOCKS, INNODB\_LOCKS\_WAITS라는 테이블을 통해 확인이 가능했다.
* MySQL 8.0부터는 performance\_schema의 data\_locks와 data\_lock\_waits 에서 확인할 수 있다.

### 5.4 MySQL의 격리 수준

* 트랜잭션의 격리 수준(Isolation Level)이란 여러 트랜잭션이 동시에 처리될 때 특정 트랜잭션이 다른 트랜잭션에서 변경하거나 조회하는 데이터를 볼 수 있게 허용할지 말지를 결정하는 것이다.
* 종류
  * READ UNCOMMITTED
  * READ COMMITTED
  * REPEATABLE READ
  * SERIALIZABLE
* READ UNCOMMITTED는 DIRTY READ가 발생하고 SERIALIZABLE 은 동시성이 거의 보장되지 않기 때문에 잘 사용되지 않는다.
* ORACLE에서는 READ COMMITTED, MySQL에서는 REPEATABLE READ를 주로 사용한다.
* 데이터 베이스의 격리 수준을 이야기할 때 항상 언급되는 세 가지 부정합 문제점이 있다.
  * DIRTY READ - READ UNCOMMITTED 에서 발생
  * NON-REPEATABLE READ - READ COMMITTED이하 에서 발생
  * PHANTOM READ - REPEATABLE READ 이하에서 발생, 그러나 InnoDB에서는 없음

#### 5.4.1 READ UNCOMMITTED

* 각 트랜잭션에서의 변경 내용이 Commit이나 Rollback 여부에 상관없이 다른 트랜잭션에서 보인다.
  * 사용자 A가 특정 레코드를 인서트를 하고 커밋을 하기 전이라고 가정하자.
  * 사용자 B는 해당 레코드를 조회해서 가져올 수 있다. 가져와서 다른 비즈니스로직을 수행한다.
  * 사용자 A는 커밋 하기 전에 문제가 생겨서 롤백을 했다.
  * 그럼에도 사용자 B는 자신의 로직을 수행할 것이다.
* 이처럼 어떤 트랜잭션에서 처리한 작업이 완료되지 않았는데도 다른 트랜잭션에서 볼 수 있는 현상을 DIRTY READ라 한다.

#### 5.4.2 READ COMMITTED

* 오라클 DBMS에서 기본적으로 사용되는 격리 수준이다.
* 어떤 트랜잭션에서 변경한 내용이 커밋되기 전까지는 다른 트랜잭션에서 그러한 변경내역을 조회할 수 없다.
* NON-REPEATABLE READ 부정합 문제
  * 사용자 B가 트랜잭션을 시작하고 특정 데이터의 레코드를 조회를 시도했다고 가정하자 (select \* from employee where name = '성훈')
  * TABLE에 데이터가 없어서 조회 결과가 없음을 결과로 받았다.
  * 사용자 A가 다른 트랜잭션에서 해당 데이터를 인서트를 하고 커밋을 했다.
  * 그 후 사용자 B가 기존의 트랜잭션 안에서 다시 한번 해당 레코드 조회를 시도하면 어떨까? (select \* from employee where name = '성훈')
  * 커밋된 데이터이기 때문에 조회가 정상적으로 될 것이다.
  * 사용자 B가 하나의 트랜잭션 안에서 똑같은 SELECT 쿼리를 실행했을 때는 항상 같은 결과를 가져와야 한다는 REPEATABLE READ 정합성에 어긋나는 것이다.

#### 5.4.3 REPEATABLE READ

* REPEATABLE READ 격리 수준에서는 기본적으로 SELECT 쿼리 문장도 트랜잭션 범위 내에서만 작동한다.
* 트랜잭션 안에서는 온종일 동일한 쿼리를 반복해서 실행하면 동일한 결과를 보게 된다.
* InnoDB 스토리지 엔진에서 기본적으로 사용되는 격리 수준이다.
* 언두 영역을 사용해서 REPEATABLE READ를 보장할 수 있다.
  * 사용자 B가 트랜잭션을 시작하고 특정 데이터의 레코드를 조회를 시도했다고 가정하자 (select \* from employee where name = '성훈')
  * TABLE에 데이터가 없어서 조회 결과가 없음을 결과로 받았다.
  * 사용자 A가 다른 트랜잭션에서 해당 데이터를 인서트를 하고 커밋을 했다.
  * 그 후 사용자 B가 기존의 트랜잭션 안에서 다시 한번 해당 레코드 조회를 시도하면 어떨까? (select \* from employee where name = '성훈')
  * Undo log를 읽기 때문에 조회 결과 없음을 유지할 수 있다.
* PHANTOM READ 발생, InnoDB에서는 No

#### 5.4.4 SERIALIZABLE

* 가장 단순한 격리 수준
* 그만큼 동시 처리 성능도 다른 트랜잭션 격리 수준보다 떨어진다.