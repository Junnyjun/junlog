# HikariCP의 예외 처리

고성능 데이터베이스 커넥션 풀은 단순히 빠른 커넥션 관리만으로는 충분하지 않습니다. 실제 운영 환경에서는 다양한 예외 상황을 적절히 처리하는 능력이 안정성과 가용성을 결정짓는 중요한 요소가 됩니다. 이번 챕터에서는 HikariCP의 예외 처리 메커니즘, 특히 SQLExceptionOverride 인터페이스와 관련 구현체를 중심으로 살펴보겠습니다.

### 데이터베이스 연결에서의 예외 처리 중요성

데이터베이스 연결 과정에서 발생하는 예외는 단순히 개발자에게 오류를 알리는 것 이상의 의미를 갖습니다. 이러한 예외는 다음과 같은 중요한 역할을 합니다:

* **커넥션 상태 판단**: 커넥션이 여전히 유효한지 또는 버려야 하는지 결정
* **재시도 전략 수립**: 특정 예외는 즉시 재시도가 가능한 반면, 다른 예외는 백오프 전략이 필요
* **리소스 관리**: 오류 발생 시 관련 리소스를 적절히 정리
* **풀 상태 관리**: 예외 패턴에 따라 풀의 크기나 타임아웃 설정 조정

### SQLExceptionOverride 인터페이스

HikariCP는 SQLExceptionOverride 인터페이스를 통해 JDBC의 SQLException 처리를 위한 유연한 프레임워크를 제공합니다.&#x20;

```java
public interface SQLExceptionOverride {
   /**
    * 주어진 SQLException을 검사하여 이 예외가 커넥션을 폐기해야 하는지 여부를 결정합니다.
    *
    * @param sqlException 검사할 SQLException
    * @return 결정 상태 (CONTINUE_EVICT, CONTINUE_EVICT_AND_RETRY, 또는 DO_NOT_EVICT)
    */
   SQLExceptionOverrideState adjudicate(SQLException sqlException);
}
```

이 간단한 인터페이스는 SQLException을 평가(adjudicate)하고 세 가지 가능한 결과 중 하나를 반환합니다

* **CONTINUE\_EVICT**: 커넥션을 풀에서 제거
* **CONTINUE\_EVICT\_AND\_RETRY**: 커넥션을 풀에서 제거하고 작업 재시도
* **DO\_NOT\_EVICT**: 커넥션을 유지하고 예외 전파

### SQLExceptionOverrideFactory

HikariCP는 SQLExceptionOverrideFactory를 통해 다양한 데이터베이스 벤더별 예외 처리 전략을 생성합니다

```java
public class SQLExceptionOverrideFactory {
   public static SQLExceptionOverride createOverride(Properties properties) {
      String exceptionOverrideClassName = properties.getProperty("exceptionOverrideClassName");
      if (exceptionOverrideClassName == null) {
         // 벤더별 기본 구현 반환
         return new VendorSpecificSQLExceptionOverride();
      }
      
      // 사용자 정의 구현 인스턴스화
      return UtilityElf.createInstance(exceptionOverrideClassName, SQLExceptionOverride.class);
   }
}
```

이 팩토리는 다음 두 가지 방식으로 동작합니다

1. 사용자가 명시적으로 구현 클래스를 지정한 경우, 해당 클래스의 인스턴스를 생성
2. 그렇지 않은 경우, 데이터베이스 벤더에 맞는 기본 구현을 반환

### 벤더별 예외 처리: VendorSpecificSQLExceptionOverride

서로 다른 데이터베이스 시스템은 다양한 예외 코드와 메시지를 사용합니다. \
HikariCP는 VendorSpecificSQLExceptionOverride 클래스를 통해 주요 데이터베이스 벤더별 예외 패턴을 처리합니다

```java
public class VendorSpecificSQLExceptionOverride implements SQLExceptionOverride {
   @Override
   public SQLExceptionOverrideState adjudicate(SQLException sqlException) {
      // Oracle 예외 처리
      if (sqlException.getErrorCode() == 17002 || sqlException.getErrorCode() == 17008) {
         return CONTINUE_EVICT;
      }
      
      // MySQL 예외 처리
      if (sqlException.getErrorCode() == 1317 || sqlException.getErrorCode() == 1547) {
         return CONTINUE_EVICT_AND_RETRY;
      }
      
      // PostgreSQL 예외 처리
      if ("57P01".equals(sqlException.getSQLState())) {
         return CONTINUE_EVICT;
      }
      
      // 기타 일반적인 연결 오류 패턴 처리
      String message = sqlException.getMessage().toLowerCase();
      if (message.contains("connection is closed") || message.contains("socket closed")) {
         return CONTINUE_EVICT;
      }
      
      return DO_NOT_EVICT;
   }
}
```

이 클래스는 다음과 같은 주요 예외 패턴을 처리합니다:

#### Oracle 데이터베이스 예외

* **17002**: IO 예외 (네트워크 연결 끊김)
* **17008**: 닫힌 연결

#### MySQL 예외

* **1317**: 쿼리 실행 중단됨
* **1547**: 세션이 종료됨

#### PostgreSQL 예외

* **57P01**: 관리자에 의해 종료됨
* **08006**: 연결 실패

#### 일반 연결 오류

* "connection is closed" 포함 메시지
* "socket closed" 포함 메시지
* "connection reset" 포함 메시지

### 사용자 정의 예외 처리 구현

HikariCP의 강점 중 하나는 사용자가 자신의 환경에 맞는 예외 처리 전략을 구현할 수 있다는 점입니다.&#x20;

```java
public class CustomSQLExceptionOverride implements SQLExceptionOverride {
   @Override
   public SQLExceptionOverrideState adjudicate(SQLException sqlException) {
      // 특정 기업 환경에 맞는 예외 처리 로직
      if (sqlException.getErrorCode() == 12345) {
         // 기업 내부 데이터베이스 클러스터 페일오버 처리
         return CONTINUE_EVICT_AND_RETRY;
      }
      
      // 특정 애플리케이션 패턴에 맞는 처리
      if (sqlException.getMessage().contains("application specific error")) {
         logApplicationError(sqlException);
         return DO_NOT_EVICT;
      }
      
      // 기본 처리 로직으로 폴백
      return defaultOverride.adjudicate(sqlException);
   }
}
```

### 예외 처리와 커넥션 풀의 연동

HikariCP의 SQLExceptionOverride는 HikariPool 클래스와 긴밀하게 통합되어 있습니다. \
커넥션을 사용하는 과정에서 예외가 발생하면 다음과 같은 흐름으로 처리됩니다:

```java
try {
   connection.prepareStatement(sql);
   // ... SQL 작업 수행
} catch (SQLException e) {
   SQLExceptionOverrideState state = exceptionOverride.adjudicate(e);
   switch (state) {
      case CONTINUE_EVICT:
         evictConnection(connection);
         throw e;
      case CONTINUE_EVICT_AND_RETRY:
         evictConnection(connection);
         // 새 커넥션을 얻어 작업 재시도
         return getConnection().prepareStatement(sql);
      case DO_NOT_EVICT:
         // 커넥션은 유지하고 예외만 전파
         throw e;
   }
}
```

이 메커니즘을 통해 HikariCP는 다음과 같은 이점을 얻습니다:

1. **풀 안정성 향상**: 문제가 있는 커넥션을 적극적으로 제거하여 풀의 건강 상태 유지
2. **자동 복구**: 일시적인 오류 발생 시 자동 재시도로 애플리케이션 가용성 향상
3. **리소스 낭비 방지**: 불필요하게 커넥션을 폐기하지 않음
4. **맞춤형 오류 처리**: 다양한 데이터베이스 환경에 맞춘 예외 처리 전략

### 예외 모니터링과 메트릭

HikariCP는 예외 발생 패턴을 모니터링하고 이에 대한 메트릭을 제공합니다. 이는 애플리케이션 운영 중 문제를 조기에 발견하고 대응하는 데 매우 중요합니다:

```java
// 커넥션 획득 실패 수 추적
private final AtomicLong connectionTimeout = new AtomicLong();

// 커넥션 제거 수 추적
private final AtomicLong connectionEvictions = new AtomicLong();

// 메트릭 수집
public void recordConnectionTimeout() {
   connectionTimeout.incrementAndGet();
   metricsTracker.recordConnectionTimeout();
}

public void recordConnectionEviction() {
   connectionEvictions.incrementAndGet();
   metricsTracker.recordConnectionEviction();
}
```

이러한 메트릭은 다음과 같은 목적으로 활용됩니다:

* **풀 크기 최적화**: 타임아웃이 자주 발생하면 풀 크기 증가 고려
* **데이터베이스 문제 조기 감지**: 갑작스러운 커넥션 폐기 증가는 데이터베이스 문제 신호일 수 있음
* **성능 튜닝**: 예외 패턴 분석을 통한 애플리케이션 설정 최적화

### 고급 예외 처리 기법

HikariCP는 단순히 예외를 분류하는 것 이상의 고급 예외 처리 기법을 사용합니다:

#### 예외 히스토그램 분석

특정 예외 패턴의 빈도와 분포를 분석하여 간헐적 오류와 체계적 오류를 구분합니다.

#### 자동 복구 메커니즘

특정 유형의 예외 발생 후 커넥션 검증과 재검증을 통해 풀의 자가 복구를 시도합니다.

#### 예외 기반 백오프 전략

연속적인 예외 발생 시 지수적 백오프 전략을 사용하여 시스템에 과부하가 걸리지 않도록 합니다.

#### 컨텍스트 인식 예외 처리

트랜잭션 상태, SQL 문 유형, 이전 작업 기록 등 컨텍스트 정보를 활용한 더 정교한 예외 판단을 수행합니다.

### 안정성과 성능의 균형

SQLExceptionOverride와 관련 예외 처리 메커니즘은 HikariCP가 단순히 빠른 커넥션 풀을 넘어 안정적이고 견고한 데이터 액세스 계층을 제공하는 핵심 요소입니다. 이러한 예외 처리 시스템을 통해 HikariCP는 다음과 같은 가치를 제공합니다:

* **빠른 실패와 자가 복구**: 문제가 있는 커넥션을 빠르게 감지하고 필요한 경우 자동으로 재시도하여 애플리케이션의 가용성을 유지합니다.
* **효율적인 리소스 관리**: 불필요한 커넥션 폐기를 방지하여 리소스 낭비를 최소화합니다.
* **벤더 중립적 추상화**: 다양한 데이터베이스 시스템의 예외 패턴을 표준화된 방식으로 처리합니다.
* **확장성과 맞춤성**: 사용자 정의 예외 처리 전략을 통해 특수한 환경이나 요구사항에 대응할 수 있습니다.

HikariCP의 SQLExceptionOverride는 고성능 애플리케이션 개발에서 안정성과 성능이 상충 관계가 아닌 상호 보완적인 목표가 될 수 있음을 보여주는 훌륭한 사례입니다. 예외 처리를 단순한 오류 보고 메커니즘이 아닌 시스템 안정성과 자가 복구의 핵심 요소로 활용함으로써, HikariCP는 극한의 성능과 견고한 안정성을 동시에 제공합니다.
