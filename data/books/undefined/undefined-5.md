# 레플리케이션

**레디스 레플리케이션**은 하나의 마스터 노드에서 발생하는 모든 데이터를 다수의 레플리카 노드로 실시간으로 복제하여 **데이터의 가용성을 높이고, 읽기 성능을 향상**시키는 기술이다. 이는 다음과 같은 상황에서 활용된다:

1. **읽기 성능 개선**:
   * 읽기 요청을 여러 레플리카로 분산하여 마스터의 부하를 줄인다.
   * 고성능이 필요한 애플리케이션에서 읽기 전용 레플리카 활용.
2. **장애 복구**:
   * 마스터 장애 시 레플리카를 새로운 마스터로 승격하여 데이터 손실 및 다운타임 최소화.
   * 페일오버 전략을 통한 자동 복구.
3. **데이터 중복성**:
   * 동일 데이터를 여러 노드에 저장하여 데이터 유실 가능성 감소.
   * 장애나 서버 오류에도 데이터의 지속성을 보장.

***

#### 레디스 레플리케이션의 구조

**마스터-레플리카 모델**

* **마스터**: 쓰기 작업의 중심 역할을 담당하며, 모든 데이터 변경 사항을 관리.
* **레플리카**: 읽기 요청을 처리하며, 마스터의 모든 데이터를 복제.

> 기본적으로 레플리카는 읽기 전용으로 설정되어 있으나, 특정 요구사항에 따라 쓰기를 활성화할 수 있음.

***

#### 레플리케이션 설정

**설정 파일 구성**

**마스터 설정 파일 예제 (`redis-master.conf`)**

```plaintext
bind 0.0.0.0
port 6379
requirepass yourpassword
```

**레플리카 설정 파일 예제 (`redis-replica.conf`)**

```plaintext
replicaof <master-ip> 6379
masterauth yourpassword
replica-read-only yes
```

***

#### 레플리케이션 메커니즘

**초기 동기화 (Full Synchronization)**

마스터와 레플리카가 처음 연결될 때 수행되는 과정:

1. **PSYNC 요청**:
   * 레플리카는 PSYNC 명령어를 통해 마스터에 동기화를 요청.
   * 이때 레플리카는 이전에 처리된 레플리케이션 ID와 오프셋 정보를 전송.
2. **RDB 파일 생성**:
   * 마스터는 메모리의 데이터를 RDB 파일로 스냅샷 생성.
   * 생성된 RDB 파일은 레플리카로 전송.
3. **쓰기 버퍼 전송**:
   * RDB 전송 이후, 마스터는 쓰기 작업 내용을 레플리카로 전달.
   * 레플리카는 RDB 파일을 메모리에 로드하고 이후 실시간 복제를 시작.

> 초기 동기화는 네트워크 부하와 디스크 I/O가 크기 때문에 시스템 성능에 영향을 미칠 수 있다.

**부분 동기화 (Partial Synchronization)**

* 기존 연결이 끊어졌다가 재연결된 경우:
  * 마스터는 **레플리케이션 백로그**를 참조하여 누락된 데이터만 레플리카로 전송.
  * 네트워크 부하를 줄이고 빠르게 동기화 가능.

> 레플리케이션 백로그 크기(`repl-backlog-size`)와 TTL 설정(`repl-backlog-ttl`)은 성능에 중요한 영향을 미친다.

***

#### 비동기 레플리케이션과 동기화 보장

**기본 동작: 비동기 처리**

* 마스터에서 데이터가 레플리카로 복제되는 동안 네트워크 딜레이 발생 가능.
* 장애 발생 시 데이터 손실 가능성 존재.

**동기식 레플리케이션 강제**

`WAIT` 명령어를 통해 동기적 동작을 보장:

```plaintext
WAIT <number_of_replicas> <timeout>
```

예시: 최소 2개의 레플리카가 데이터를 저장할 때까지 대기.

```plaintext
WAIT 2 5000
```

***

#### 디스크 없는 레플리케이션

**디스크 없는 RDB 전송**

RDB 파일을 디스크에 저장하지 않고 소켓을 통해 직접 전송:

1.  설정 활성화:

    ```plaintext
    repl-diskless-sync yes
    repl-diskless-sync-delay 5
    ```
2. **장점**:
   * 디스크 I/O를 줄여 동기화 속도 향상.
   * 네트워크 대역폭이 충분할 경우 특히 유리.
3. **단점**:
   * 메모리 사용량 증가.

***

#### 페일오버와 고가용성

**수동 페일오버**

마스터가 장애를 일으킨 경우, 수동으로 레플리카를 승격:

```plaintext
REPLICAOF NO ONE
```

**Redis Sentinel**

자동 페일오버 기능 제공:

1. 장애를 감지하여 새로운 마스터를 선택.
2. 클라이언트가 새로운 마스터를 참조하도록 재구성.

센티널 설정 예시:

```plaintext
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 15000
```

***

#### 실습: Docker를 활용한 레플리케이션 구성

**Docker Compose 설정**

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

**실행**

1.  Docker 컨테이너 시작:

    ```bash
    docker-compose up
    ```
2. 레플리케이션 상태 확인:
   *   마스터:

       ```bash
       redis-cli -p 6379 INFO replication
       ```
   *   레플리카:

       ```bash
       redis-cli -p 6380 INFO replication
       ```

***

#### 트러블슈팅 및 최적화

**문제 해결**

1. **레플리카 연결 실패**: IP 및 인증 설정 확인.
2. **전체 동기화 반복**: `repl-backlog-size`와 `repl-backlog-ttl` 조정.
3. **데이터 손실**: `WAIT` 명령어로 동기화 보장.

**성능 최적화**

1.  **출력 버퍼 제한**:

    ```plaintext
    client-output-buffer-limit replica 256mb 64mb 60
    ```
2.  **TCP\_NODELAY 비활성화**:

    ```plaintext
    repl-disable-tcp-nodelay yes
    ```
