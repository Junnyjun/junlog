# 커넥션 풀링의 필요성과 기본 개념

**초기 웹 애플리케이션의 한계**

* 2000년대 초반, Java EE(당시 J2EE) 기반 웹 애플리케이션이 보편화되면서 수천, 수만 명의 동시 접속이 일상화
* 매 HTTP 요청마다 `DriverManager.getConnection()`을 호출해 데이터베이스와 통신 → TCP 핸드셰이크·인증·드라이버 초기화 비용이 과도

**성능 병목과 자원 고갈**

* 짧은 수명(single-use) 커넥션이 폭증하며 DB 서버 스레드가 빠르게 소진
* OS 레벨에서 소켓 파일 디스크립터가 고갈되어 장애 발생

**풀링 솔루션의 출현**

* **C3P0** (2002년경): 최초의 오픈소스 JDBC 커넥션 풀
* **Apache DBCP** (2005년): Jakarta Commons 에서 제공, 엔터프라이즈 향 최적화
* **HikariCP** (2013년): “가볍고 빠른” 풀링을 지향하며 벤치마크에서 압도적 성능

***

### 커넥션 풀링이 필요한 이유

**매 요청마다 커넥션 생성·폐기의 오버헤드 제거**

* TCP 핸드셰이크·인증·드라이버 초기화 → 요청당 수십\~수백 ms 지연

**DB 서버 자원 효율화**

* 짧은 커넥션이 반복 생성되면 서버 메모리·스레드가 낭비

**트래픽 급증 대응**

*   풀링 도입 전후 동일 부하(100 QPS)에서의 응답 시간 비교:

    | 항목           | 커넥션 풀 미사용 | 커넥션 풀 사용 (HikariCP) |
    | ------------ | --------- | ------------------- |
    | 평균 응답 시간     | 120 ms    | 35 ms               |
    | 95퍼센타일 응답 시간 | 250 ms    | 60 ms               |
    | 초당 처리량 (TPS) | 85        | 95                  |

***

### 커넥션 풀 추가 전·후 코드 비교

**풀 미사용 시**

```java
// 매 요청마다 새로운 커넥션 생성
try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id=?");
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // 처리 로직…
}
```

**HikariCP 도입 후**

```java
// 애플리케이션 시작 시 한 번만 초기화
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/appdb");
config.setUsername("app");
config.setPassword("secret");
DataSource ds = new HikariDataSource(config);

// 매 요청 시 풀에서 재사용
try (Connection conn = ds.getConnection()) {
    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id=?");
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();
    // 처리 로직…
}
```

***

### 커넥션 풀 동작 흐름

```mermaid
flowchart LR
  subgraph 앱
    A[스레드 요청]
  end

  subgraph "커넥션 풀 (DataSource)"
    P{유휴 존재?}
    I[유휴 커넥션]
    C[신규 커넥션 생성]
    W[대기(타임아웃)]
    R[커넥션 정리]
  end

  subgraph DB
    D[(데이터베이스)]
  end

  A -->|getConnection()| P
  P -->|Yes| I
  P -->|No\n(풀 < 최대)| C
  P -->|No\n(풀 == 최대)| W
  I --> A
  C --> A

  A -->|쿼리 실행| D
  D -->|결과 반환| A

  A -->|close() 호출| I
  I -.->|idleTimeout 경과| R

```

```mermaid
```

***

### 대표 풀 라이브러리 비교

| 라이브러리       | 초기 릴리스 | 주요 특징                               | 벤치마크 성능\*     |
| ----------- | ------ | ----------------------------------- | ------------- |
| C3P0        | 2002   | 설정 유연, 기능 풍부                        | 보통            |
| Apache DBCP | 2005   | Jakarta Commons, 성능 개선판             | C3P0 대비 1.5×  |
| HikariCP    | 2013   | 최소 지연·경량 설계, 경쟁 기반 락 제거(락 프리 프로그래밍) | DBCP 대비 2\~3× |

\* 벤치마크는 커넥션 획득·반납 시나리오 기준으로 측정

***



* 과거 직접 커넥션 방식은 트래픽 증가에 취약했고, 자원 낭비와 지연을 야기
* 풀링 도입으로 “한 번 생성 → 여러 번 재사용” 패턴을 실현
* 다양한 라이브러리 중 HikariCP는 경량·고성능으로 현대 애플리케이션에 최적
