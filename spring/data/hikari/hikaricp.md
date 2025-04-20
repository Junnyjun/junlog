# HikariCP 소개 및 아키텍처

HikariCP 탄생 배경 및 필요성

2000년대 초 Java EE 기반 웹 애플리케이션이 보편화되면서, 매 요청마다 JDBC 커넥션을 생성·해제하는 방식은 다음과 같은 한계를 드러냈습니다:

* **지연 시간 증가**: TCP 핸드셰이크와 드라이버 초기화로 인해 요청당 수십\~수백 밀리초 지연 발생
* **DB 자원 낭비**: 짧은 수명 커넥션이 폭증하면 데이터베이스 스레드·메모리·소켓 디스크립터 고갈 우려
* **확장성 한계**: 동시 사용자 수가 증가할수록 커넥션 생성 경쟁(lock)으로 처리량 정체 발생

이러한 문제를 해결하고자, **가볍고 빠른** 풀링 라이브러리가 필요했습니다. 기존 C3P0와 Apache DBCP는 기능은 풍부하지만 락(lock) 경쟁과 복잡한 설정으로 오버헤드가 컸습니다. 이에 2013년 Brett Wooldridge가 **최소 코드**·**락프리 동시성**·**단일 하우스키핑 스레드**라는 설계 철학을 바탕으로 HikariCP를 개발했습니다.

간단한 아키텍처 개요

먼저 HikariCP의 전체 구성 요소와 데이터 흐름을 단순화된 다이어그램으로 나타냅니다.

```mermaid
flowchart LR
  subgraph 애플리케이션
    A[Application Thread]
  end
  subgraph HikariCP
    DS[HikariDataSource]
    Pool[HikariPool]
    Bag[ConcurrentBag]
    HK[Housekeeping Thread]
  end
  subgraph 데이터베이스
    DB[(Database)]
  end

  A -->|1. getConnection() 호출| DS
  DS -->|2. borrow()| Bag
  Bag -->|3. ConnectionProxy 반환| DS
  DS -->|4. 클라이언트로 반환| A
  A -->|5. 쿼리 실행| DB
  DB -->|6. 결과 전달| A
  A -->|7. close() 호출| DS
  DS -->|8. requite()| Bag
  HK -.->|주기적 누수 탐지 / 유휴 정리| Bag
```

### 아키텍처 설계 배경 및 이유

1. **성능 극대화**: 애플리케이션이 빈번하게 데이터베이스 커넥션을 생성·해제하면 TCP 핸드셰이크 및 드라이버 로딩으로 인한 수십\~수백 밀리초 지연이 발생합니다. 이를 방지하기 위해 미리 연결을 생성해두고 재사용하는 풀링 구조를 선택했습니다.
2. **안정성 확보**: 커넥션 수를 제한(`maximumPoolSize`)해 데이터베이스 과부하를 방지하고, 누수 탐지(`leakDetectionThreshold`) 및 유휴 정리(`idleTimeout`)를 통해 장시간 사용되지 않는 커넥션을 자동으로 청소합니다.
3. **단순화된 유지보수**: 복잡한 기능을 제거해 코드베이스를 경량화하고, 핵심 동작만 집중적으로 검증할 수 있는 구조로 설계했습니다.

### 설계 원칙 상세 설명

#### 락프리 CAS 기반 동시성 제어

* **원칙**: 락을 최소화하고 원자 연산(CAS)을 통해 경쟁 상태를 해결함으로써 지연을 줄이고 확장성을 보장합니다.
* **어려운 점**: CAS 방식은 ABA 문제, 무한 스핀(spin) 가능성, 공정성(fairness) 보장 미흡 등의 단점을 가지고 있습니다.
  * _ABA 문제_: 메모리 위치 값이 A→B→A로 변경되면 단순 비교로 변경을 인식하지 못할 수 있습니다. 이를 해결하기 위해 상태에 버전(타임스탬프)을 추가하거나, 비교 대상 객체 자체를 교체해 ABA 현상을 방지합니다.
  * _스핀 비용_: 경쟁이 심할 때 스레드가 반복 시도하며 CPU 사이클을 소모합니다. HikariCP는 짧은 스핀과 `Thread.onSpinWait()`을 활용해 캐시 효율을 높이고, 과도한 스핀을 방지하기 위해 일정 횟수 이상 재시도 시 스레드를 잠시 양보할 수 있는 설계가 필요합니다.
  * _공정성 문제가 발생할 수 있음_: CAS는 선착순(fifo)을 보장하지 않아 일부 스레드가 지속적으로 자원을 획득 못할 수 있습니다. HikariCP는 슬롯 배열 순회를 통해 어느 정도 접근 기회를 분산하지만, 아주 큰 경쟁 환경에서는 추가 대책이 필요합니다.

#### 단일 하우스키핑 스레드

* **원칙**: 풀 유지보수를 위해 최소한의 백그라운드 스레드만 사용해 오버헤드를 통제합니다.
* **어려운 점**:
  * _장애 복원력_: 단일 스레드가 예외로 중단되면 전체 하우스키핑이 중지될 수 있습니다. 이를 방지하기 위해 예외를 내부에서 캐치하고, 스케줄된 작업이 반복적으로 실행되도록 `scheduleWithFixedDelay`를 사용합니다.
  * _타이밍 정확도_: JVM GC나 시스템 부하에 따라 주기(scheduling delay)가 들쭉날쭉할 수 있습니다. 누수 탐지와 유휴 정리 시점이 지연되면 메모리 누수, 커넥션 고갈 위험이 있으므로, 메트릭 모니터링을 통해 실제 실행 간격을 모니터링해야 합니다.

#### 최소 코드 베이스 (KISS & YAGNI 원칙)

* **원칙**: Keep It Simple, Stupid(KISS)와 You Aren't Gonna Need It(YAGNI)를 준수해 필요한 기능만 구현합니다.
* **어려운 점**:
  * _기능 축소의 경계_: 너무 많은 기능을 제거하면 사용자가 필요한 커스터마이징이 어려워집니다. HikariCP는 Statement 캐싱과 JMX 자동 등록을 제거했으나, 이 기능이 필요한 사용자에게는 외부 통합 포인트(jmxRegistryHandler 등)를 제공해 확장을 지원합니다.
  * _코드 복잡도 관리_: 핵심 로직이 길어지면 오히려 가독성과 유지보수성이 떨어질 수 있습니다. 메서드 분리와 주석, 문서화를 통해 코드 이해성을 높이고, JIT 최적화 경로(핫스팟)만 집중적으로 단일 메서드로 유지합니다.

### 각 컴포넌트 동작 원리 예시

#### CAS 기반 슬롯 획득 예시

```java
int maxSlots = config.getMaximumPoolSize();
for (int i = 0, attempts = 0; ; i = (i+1) % maxSlots, attempts++) {
    ConnectionWrapper cw = slots.get(i);
    if (cw.state == FREE) {
        if (cw.stateUpdater.compareAndSet(cw, FREE, RESERVED)) {
            cw.usageStart = System.nanoTime();
            return cw; // 슬롯 획득 성공
        }
    }
    if (attempts == maxSlots) {
        // 모든 슬롯 시도 후 스핀 대기
        Thread.onSpinWait();
        attempts = 0;
    }
}
```

#### 하우스키핑 스레드 예외 복원 예시

```java
exec.scheduleWithFixedDelay(() -> {
    try {
        housekeeping();
    } catch (Throwable t) {
        log.error("Housekeeping thread failed, recovering", t);
        // 예외 발생 시에도 스케줄러가 중단되지 않도록 처리
    }
}, period, period, TimeUnit.MILLISECONDS);
```

위와 같이 아키텍처 설계 이유부터, 어려운 원칙과 대응 전략, 그리고 실제 작동 예시를 모두 포함해 HikariCP의 설계 개념을 깊이 이해할 수 있도록 구성했습니다. 추가로 구체적인 사례나 코드 부분이 필요하시면 알려주세요!
