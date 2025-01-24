# 레플리케이션

확실히 더 세부적인 내용을 담아 확장해서 작성해볼게. 이 버전에서는 기술적 동작, 설정 및 레플리케이션의 상세 구현 방식, 장애 처리, 그리고 특수 상황에서의 고려 사항을 **모두** 추가해볼게. 레디스 레플리케이션에 관한 모든 것을 하나의 문서로 정리해 보자.

***

### **레디스 레플리케이션: 초고도 디테일 가이드**

***

#### **1. 레플리케이션의 개요**

**1.1. 레플리케이션이란?**

* 레플리케이션은 **데이터 복제**를 통해 마스터-레플리카 구조를 구축하는 과정입니다.
* **목적**:
  * **데이터 고가용성 확보**: 장애 발생 시 데이터 손실 최소화.
  * **읽기 성능 향상**: 읽기 요청을 레플리카로 분산.
  * **데이터 백업**: 레플리카를 활용한 데이터 스냅샷 가능.
  * **서버 마이그레이션**: 마이그레이션 과정에서 다운타임 최소화.

**1.2. 마스터와 레플리카의 역할**

* **마스터**:
  * 쓰기 작업을 처리하며, 발생하는 데이터를 모든 레플리카로 복제.
  * 실시간으로 레플리카에 데이터를 전송하여 동기화 유지.
* **레플리카**:
  * 읽기 전용(기본값).
  * `replica-read-only`를 통해 쓰기도 허용 가능.

***

#### **2. 레플리케이션의 동작 과정**

**2.1. 초기 동기화 (Full Resynchronization)**

* 초기 연결 시 레플리카는 항상 전체 동기화 과정을 수행.
* 마스터는 `BGSAVE` 명령을 실행하여 RDB 스냅샷 생성 후, 해당 데이터를 레플리카로 전송.
* 동작 순서:
  1. 레플리카가 마스터에 `PSYNC` 명령 전송.
  2. 마스터는 현재 상태를 판단 후 **전체 동기화**를 수행.
  3. 마스터가 생성한 RDB 파일을 레플리카로 전송.
  4. 레플리카가 RDB 파일을 로드하며, 이후 마스터로부터 발생한 추가 데이터를 동기화.

**2.2. 부분 동기화 (Partial Resynchronization)**

* 레플리카와 마스터 간 연결이 일시적으로 끊어졌을 경우, 이전 상태를 기반으로 부분 동기화 진행.
* 마스터는 \*\*레플리케이션 백로그(Replication Backlog)\*\*를 참조하여 누락된 데이터 전송.
* 주요 동작:
  * 레플리카가 **마지막 처리 오프셋**을 마스터에게 제공.
  * 마스터는 오프셋을 기반으로 백로그에서 필요한 데이터 전송.

**2.3. 실시간 동기화**

* 동기화 완료 후, 마스터에서 발생하는 모든 쓰기 작업은 레플리카로 실시간 전송.
* 데이터 전송 방식:
  * 명령어 단위로 전송.
  * 네트워크 상태에 따라 비동기 처리.

**2.4. 동기화 실패 시 처리**

* 마스터와 레플리카의 상태가 다를 경우, **전체 동기화**가 다시 수행.
* 레플리카가 데이터를 초기화한 뒤 동기화 재개.

***

#### **3. 레플리케이션 설정**

**3.1. 마스터 설정**

* `redis.conf` 파일에서 다음과 같이 설정:

```plaintext
bind 127.0.0.1         # 마스터가 수신할 IP 주소
requirepass mypassword # 마스터 인증 비밀번호
```

**3.2. 레플리카 설정**

* 레플리카에서 마스터 연결 설정:

```plaintext
replicaof 192.168.1.100 6379   # 마스터의 IP 주소 및 포트
masterauth mypassword          # 마스터 인증 비밀번호
replica-read-only yes          # 읽기 전용 상태 유지
```

**3.3. 디스크 없는 레플리케이션**

* 디스크 사용을 줄이기 위한 설정:

```plaintext
repl-diskless-sync yes          # 디스크 없는 동기화 활성화
repl-diskless-sync-delay 5      # 대기 시간 설정 (5초)
```

***

#### **4. 레플리케이션 성능 최적화**

**4.1. 클라이언트 출력 버퍼 제한**

* 레플리카의 출력 버퍼 크기 초과 방지:

```plaintext
client-output-buffer-limit replica 256mb 64mb 60
```

* **256MB**: 하드 리미트(즉시 연결 끊김).
* **64MB/60초**: 소프트 리미트(연결 유지 시간 초과 시 연결 끊김).

**4.2. 백로그 크기 조정**

* 레플리케이션 백로그는 부분 동기화를 위한 데이터 저장소:

```plaintext
repl-backlog-size 10mb  # 기본값: 1MB
repl-backlog-ttl 600    # 백로그 유지 시간(초)
```

**4.3. TCP\_NODELAY 비활성화**

* 네트워크 대역폭을 절약하지만, 지연 시간이 증가할 수 있음:

```plaintext
repl-disable-tcp-nodelay yes
```

**4.4. TTL 키 관리**

* 마스터에서 TTL 키 만료 시, DEL 명령을 통해 레플리카로 전파.
* 레플리카는 TTL 키를 자체적으로 만료시키지 않음.

***

#### **5. 장애 처리 및 페일오버**

**5.1. 수동 페일오버**

* 마스터 장애 발생 시, 레플리카를 수동으로 마스터로 승격:

```plaintext
REPLICAOF NO ONE
```

**5.2. 자동 페일오버 (Redis Sentinel)**

* 센티널을 사용하여 자동 페일오버 구현:

```plaintext
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 15000
```

***

#### **6. 트러블슈팅**

**6.1. 레플리카가 연결을 잃는 경우**

* 원인:
  * 출력 버퍼 초과.
  * 네트워크 장애.
  * 마스터 리소스 부족.
* 해결:
  *   출력 버퍼 크기 증가:

      ```plaintext
      client-output-buffer-limit replica 512mb 128mb 120
      ```

**6.2. 전체 동기화 반복**

* 원인:
  * 레플리케이션 백로그 크기 부족.
* 해결:
  *   백로그 크기 증가:

      ```plaintext
      repl-backlog-size 20mb
      ```

**6.3. 성능 문제**

* 마스터-레플리카 간 네트워크 대역폭 부족.
* 해결:
  *   디스크 없는 동기화 활성화:

      ```plaintext
      repl-diskless-sync yes
      ```

***

#### **7. 실습: Docker로 레플리케이션 환경 구축**

**7.1. Docker Compose 파일**

```yaml
version: '3'
services:
  master:
    image: redis:latest
    ports:
      - "6379:6379"
    volumes:
      - ./redis-master.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf

  replica:
    image: redis:latest
    ports:
      - "6380:6379"
    volumes:
      - ./redis-replica.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf
```

**7.2. 마스터 설정 파일 (`redis-master.conf`)**

```plaintext
bind 0.0.0.0
port 6379
requirepass yourpassword
```

**7.3. 레플리카 설정 파일 (`redis-replica.conf`)**

```plaintext
replicaof master 6379
masterauth yourpassword
```
