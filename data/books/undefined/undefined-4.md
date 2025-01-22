# 트러블 슈팅

### Redis 트러블슈팅 개요

Redis는 뛰어난 성능을 제공하는 비관계형 데이터 저장소지만, 운영 중 다양한 성능 문제나 장애가 발생할 수 있습니다. 이를 해결하기 위해 Redis에서 제공하는 다양한 명령어와 도구를 활용하여 문제를 진단하고 해결해야 합니다. 주요 문제는 **지연 시간 증가**, **메모리 부족** 또는 **단편화**, **레플리케이션 불안정성** 등이 포함됩니다.

***

### INFO 명령어 활용

`INFO` 명령어는 Redis 서버의 상태를 전반적으로 파악할 수 있는 도구입니다. 실행 시 여러 섹션으로 구성된 정보를 반환하며, 특정 섹션만 필터링해서 볼 수 있습니다.

#### **기본 사용법**

```bash
127.0.0.1:6379> INFO
127.0.0.1:6379> INFO ALL         # 모든 섹션 출력
127.0.0.1:6379> INFO MEMORY      # 특정 섹션만 필터링
127.0.0.1:6379> INFO DEFAULT     # 기본 섹션 출력
127.0.0.1:6379> INFO EVERYTHING  # 모든 섹션 포함, 모듈 생성 섹션까지 포함
```

#### **주요 섹션 설명**

1. **Server**: Redis 버전, 실행 시간, 운영 모드 등을 포함.
   * 예: `redis_version:7.0.4`, `uptime_in_seconds: 3600`
   * **활용**: 버전 충돌 문제 또는 의도치 않은 재시작 여부 확인.
2. **Clients**: 연결된 클라이언트 수와 상태 정보.
   * 예: `connected_clients: 100`
   * **활용**: 클라이언트 과부하 문제 진단.
3. **Memory**: 메모리 사용량, 단편화 비율, 메모리 설정 값 확인.
   * 예: `used_memory_human: 1.5G`, `mem_fragmentation_ratio: 1.6`
   * **활용**: 메모리 과사용, 단편화 문제 파악.
4. **Replication**: 마스터-레플리카 간 상태, 레플리케이션 지연 확인.
   * 예: `role: master`, `connected_slaves: 1`
   * **활용**: 레플리케이션 불안정성 진단.
5. **Keyspace**: 데이터베이스별 키 수, 만료 키 상태.
   * 예: `db0: keys=10000, expires=500, avg_ttl=3600000`
   * **활용**: TTL 설정 확인, 만료되지 않은 키의 영향 분석.

***

### 지연 문제 해결

Redis에서 지연 시간이 늘어날 때는 명령어 실행 시간, 네트워크 상태, 또는 키 관리 방식을 분석해야 합니다.

#### **1. 슬로우 로그 사용**

슬로우 로그는 오래 걸린 명령어를 기록하는 도구입니다.

*   **설정**:

    ```bash
    CONFIG SET slowlog-log-slower-than 10000  # 10ms 이상의 쿼리 기록
    CONFIG SET slowlog-max-len 128           # 최대 128개의 기록 저장
    ```
*   **조회**:

    ```bash
    SLOWLOG GET 10       # 최근 10개 조회
    SLOWLOG RESET        # 슬로우 로그 초기화
    SLOWLOG LEN          # 현재 슬로우 로그 항목 수 확인
    ```

**활용**: 특정 명령어(`SORT`, `SCAN`)가 과도한 시간을 소모한다면 애플리케이션 로직을 최적화하거나 Redis 설정을 변경합니다.

***

#### **2. 지연 시간 모니터링**

**LATENCY 명령어 활용**

지연 시간이 발생하는 주요 이벤트를 탐지하고 시계열로 분석합니다.

*   **활성화**:

    ```bash
    CONFIG SET latency-monitor-threshold 100  # 100ms 이상의 지연을 탐지
    ```
*   **주요 명령어**:

    ```bash
    LATENCY LATEST         # 가장 최근 이벤트
    LATENCY HISTORY event  # 특정 이벤트의 시계열 데이터
    LATENCY GRAPH event    # ASCII 기반 그래프 출력
    LATENCY RESET          # 모든 이벤트 초기화
    LATENCY DOCTOR         # 진단 보고서 제공
    ```

**활용 사례**:

* 특정 명령어 실행 시 지연이 발생하는 경우(`LATENCY GRAPH command`).
* 스냅샷 생성(`BGSAVE`)이 지연의 원인이라면 `LATENCY DOCTOR`에서 원인과 해결 방법을 제시.

***

### 메모리 문제 해결

Redis는 메모리 기반 엔진으로 메모리 사용량이 성능에 큰 영향을 미칩니다. `MEMORY` 명령어를 통해 세부적인 정보를 확인하고 최적화할 수 있습니다.

#### **1. MEMORY STATS**

```bash
127.0.0.1:6379> MEMORY STATS
```

* 주요 정보:
  * `peak.allocated`: 최대 메모리 사용량.
  * `total.allocated`: 현재 메모리 사용량.
  * `mem_fragmentation_ratio`: 메모리 단편화 비율.

**활용 사례**:

* `mem_fragmentation_ratio > 1.5`일 경우 메모리 단편화가 심각.
* `MEMORY PURGE`로 더티 페이지 정리.

***

#### **2. MEMORY USAGE**

특정 키의 메모리 사용량 확인:

```bash
MEMORY USAGE myKey SAMPLES 0  # 샘플링 없이 정확한 크기 확인
```

**활용 사례**:

* 대형 키(`Set`, `List`)의 메모리 사용량이 비정상적으로 클 경우 키 분할.

***

#### **3. 메모리 단편화 문제**

Redis의 단편화 문제는 `jemalloc` 메모리 할당자를 통해 관리됩니다.

*   **진단**:

    ```bash
    MEMORY DOCTOR
    ```
* **해결**:
  * 단편화 비율이 높은 경우 `MEMORY PURGE`로 메모리를 정리.
  * `maxmemory` 정책을 설정해 불필요한 데이터 제거.

***

### 레플리케이션 문제 해결

Redis의 레플리케이션 문제는 지연 시간 증가와 데이터 불일치를 유발할 수 있습니다.

#### **INFO Replication**

```bash
INFO Replication
```

* 주요 정보:
  * `role`: 마스터/레플리카 역할.
  * `connected_slaves`: 연결된 레플리카 수.
  * `master_link_status`: 마스터-레플리카 연결 상태.

#### **레플리케이션 지연 분석**

마스터와 레플리카 간의 데이터 오프셋 차이 확인:

```bash
LATENCY GRAPH replication
```

**활용 사례**:

* 지연 발생 시 `master_sync_in_progress` 확인 후 동기화 문제 해결.
* 네트워크 대역폭 부족 시 레플리카 동기화를 줄이는 설정(`replica-priority`).

***

### 최적화 및 설정

#### 1. **메모리 최적화**

* `maxmemory`: Redis에서 사용할 최대 메모리 설정.
* `maxmemory-policy`: 메모리 초과 시 데이터 제거 정책.
  * `volatile-lru`: 만료 키 중 가장 오래된 데이터 제거.

#### 2. **명령어 최적화**

* `KEYS` 대신 `SCAN` 사용: 대량의 키 조회 성능 문제 해결.
* 슬로우 로그 분석으로 복잡도가 높은 명령어 제거.

