# 커넥션 테스트와 유효성 검사

데이터베이스 커넥션 풀에서 가장 중요한 책임 중 하나는 애플리케이션에 항상 유효한 커넥션을 제공하는 것입니다. 네트워크 문제, 데이터베이스 재시작, 자원 제한 등 다양한 이유로 커넥션이 무효화될 수 있는 환경에서 이는 결코 쉬운 일이 아닙니다.&#x20;

### 커넥션 유효성 검사의 중요성

유효하지 않은 커넥션이 애플리케이션에 전달되면 다음과 같은 심각한 문제가 발생할 수 있습니다:

#### 런타임 예외

애플리케이션이 연결이 끊긴 커넥션을 사용하려 할 때 예외가 발생하여 사용자 작업이 실패하고 오류 메시지가 표시됩니다.

#### 타임아웃 지연

일부 경우에는 연결이 끊긴 커넥션을 사용할 때 즉시 예외가 발생하지 않고 네트워크 타임아웃까지 대기하게 되어 애플리케이션이 장시간 응답하지 않을 수 있습니다.

#### 연쇄적 오류

한 커넥션의 문제가 여러 사용자 요청에 영향을 미칠 수 있으며, 이는 시스템 전체의 성능 저하로 이어질 수 있습니다.

### HikariCP의 커넥션 검증 전략

HikariCP는 다양한 상황에서 커넥션 상태를 검증하기 위해 여러 전략을 제공합니다:

#### 커넥션 테스트 시점

1. **대여 전 검증 (Connection Validation on Borrow)**
2. **유휴 시간 검증 (Idle Connection Validation)**
3. **주기적 검증 (Background Validation)**
4. **외부 트리거 검증 (Externally Triggered Validation)**

이러한 각 검증 시점은 HikariCP 설정을 통해 구성할 수 있습니다.

### 구성 옵션

HikariCP는 커넥션 유효성 검사와 관련된 다양한 설정 옵션을 제공합니다:

```java
public class HikariConfig {
   // 커넥션 대여 시 검증 활성화 여부
   private boolean connectionTestOnBorrow = false;
   
   // 커넥션 반환 시 검증 활성화 여부
   private boolean connectionTestOnReturn = false;
   
   // 유휴 커넥션 검증 활성화 여부 
   private boolean connectionTestWhileIdle = true;
   
   // 유휴 커넥션 검증 주기 (밀리초)
   private long validationInterval = TimeUnit.SECONDS.toMillis(30);
   
   // 커넥션 테스트 쿼리
   private String connectionTestQuery;
   
   // 커넥션 상태 검사 방법 (JDBC4, JDBC42, QUERY)
   private String connectionValidationMethod = "JDBC4";
   
   // 검증 타임아웃 (밀리초)
   private long validationTimeout = TimeUnit.SECONDS.toMillis(5);
   
   // 유휴 커넥션 제거 활성화 여부
   private boolean removeAbandoned = false;
   
   // 유휴 커넥션으로 간주되기 전 시간 (초)
   private long removeAbandonedTimeout = TimeUnit.SECONDS.toSeconds(60);
}
```

### 검증 메서드

HikariCP는 커넥션 상태를 검사하기 위한 세 가지 주요 방법을 지원합니다:

#### 1. JDBC4 isValid() 메서드

JDBC 4.0 이상에서는 Connection 인터페이스에 `isValid(int timeout)` 메서드가 추가되었습니다. 이 메서드는 드라이버 수준에서 커넥션의 유효성을 가장 효율적으로 검사할 수 있습니다:

```java
private boolean isConnectionAlive(final Connection connection) {
   try {
      if (connectionValidationMethod.equalsIgnoreCase("JDBC4")) {
         return connection.isValid((int) TimeUnit.MILLISECONDS.toSeconds(validationTimeout));
      }
      // 다른 검증 방법 처리
   }
   catch (SQLException e) {
      return false;
   }
}
```

#### 2. 커스텀 테스트 쿼리

데이터베이스 드라이버가 JDBC4 `isValid()` 메서드를 올바르게 구현하지 않은 경우, 사용자 정의 테스트 쿼리를 사용하여 커넥션을 검증할 수 있습니다:

<pre class="language-java"><code class="lang-java"><strong>private boolean isConnectionAlive(final Connection connection) {
</strong>   try {
      if (connectionValidationMethod.equalsIgnoreCase("QUERY") &#x26;&#x26; connectionTestQuery != null) {
         try (Statement statement = connection.createStatement()) {
            if (statement.execute(connectionTestQuery)) {
               try (ResultSet rs = statement.getResultSet()) {
                  return rs.next(); // 결과 집합이 있으면 커넥션이 활성 상태
               }
            }
         }
      }
      // 다른 검증 방법 처리
   }
   catch (SQLException e) {
      return false;
   }
}
</code></pre>

#### 3. 메타데이터 메서드 호출

일부 데이터베이스 드라이버에서는 메타데이터 메서드를 호출하여 커넥션 상태를 확인할 수 있습니다:

```java
private boolean isConnectionAlive(final Connection connection) {
   try {
      if (connectionValidationMethod.equalsIgnoreCase("METADATA")) {
         connection.getMetaData(); // 메타데이터 액세스가 가능하면 커넥션이 활성 상태
         return true;
      }
      // 다른 검증 방법 처리
   }
   catch (SQLException e) {
      return false;
   }
}
```

### 커넥션 대여 시 검증

HikariCP는 애플리케이션에 커넥션을 제공하기 전에 해당 커넥션이 여전히 유효한지 확인할 수 있습니다:

```java
public Connection getConnection() throws SQLException {
   final PoolEntry poolEntry = connectionBag.borrow(connectionTimeout, MILLISECONDS);
   if (poolEntry == null) {
      throw new SQLTransientConnectionException("Connection is not available, timeout after " + connectionTimeout + "ms");
   }
   
   final Connection connection = poolEntry.connection;
   
   // 대여 전 검증이 활성화된 경우 수행
   if (connectionTestOnBorrow) {
      if (!isConnectionAlive(connection)) {
         closeConnection(poolEntry, "Connection failed validation check on borrow");
         // 재귀적으로 다른 커넥션 요청
         return getConnection();
      }
   }
   
   return connection;
}
```

### 백그라운드 커넥션 검증

HikariCP는 별도의 스레드를 사용하여 백그라운드에서 유휴 커넥션을 주기적으로 검사할 수 있습니다:

```java
private class HouseKeeper implements Runnable {
   private volatile long lastValidationTime;
   
   @Override
   public void run() {
      try {
         // 마지막 검증 이후 충분한 시간이 지났는지 확인
         final long now = currentTime();
         if (now - lastValidationTime > validationInterval) {
            lastValidationTime = now;
            
            // 유휴 커넥션 검증
            validateIdleConnections();
         }
      }
      catch (Exception e) {
         logger.error("Unexpected exception in housekeeping task", e);
      }
   }
   
   /**
    * 유휴 커넥션을 검증하고 필요시 교체합니다.
    */
   private void validateIdleConnections() {
      final List<PoolEntry> idleEntries = connectionBag.getIdleEntries();
      
      for (PoolEntry entry : idleEntries) {
         if (!isConnectionAlive(entry.connection)) {
            // 문제가 있는 커넥션 폐기
            closeConnection(entry, "Connection failed validation check during idle check");
            // 필요시 새 커넥션으로 교체
            if (getTotalConnections() < getMinimumIdle()) {
               addConnection();
            }
         }
      }
   }
}
```

### 커넥션 누수 감지와 회수

애플리케이션이 커넥션을 명시적으로 닫지 않는 경우, HikariCP는 이를 감지하고 필요한 경우 회수할 수 있습니다:

```

private class LeakDetector implements Runnable {
   @Override
   public void run() {
      final long now = currentTime();
      final long leakThreshold = removeAbandonedTimeout * 1000L;
      
      if (removeAbandoned) {
         final List<PoolEntry> inUseEntries = connectionBag.getInUseEntries();
         
         for (PoolEntry entry : inUseEntries) {
            if (entry.lastBorrowTime > 0 && now - entry.lastBorrowTime > leakThreshold) {
               // 누수로 간주되는 커넥션 기록
               logger.warn("Connection has been in use for {} ms (threshold: {}), considered leaked",
                     now - entry.lastBorrowTime, leakThreshold);
               
               if (entry.leakTask != null) {
                  // 누수 스택 트레이스 로깅
                  entry.leakTask.cancel();
                  logger.warn("Stack trace where the connection was borrowed:", entry.leakTask.getStackTrace());
               }
               
               // 강제로 커넥션 회수
               closeConnection(entry, "Connection leaked and reclaimed");
            }
         }
      }
   }
}
```

### 데이터베이스 리셋 감지 및 복구

네트워크 중단이나 데이터베이스 재시작 같은 이벤트는 풀의 모든 커넥션을 한 번에 무효화할 수 있습니다. HikariCP는 이러한 상황을 감지하고 풀을 효율적으로 복구하는 메커니즘을 갖추고 있습니다:

```

private void checkFailFast() {
   if (failFastEnabled) {
      final long startTime = currentTime();
      try {
         // 새 커넥션 생성 시도
         final Connection connection = dataSource.getConnection();
         connection.close();
      }
      catch (SQLException e) {
         // 모든 커넥션 무효화
         logger.warn("Database connectivity failure detected, marking all connections as broken");
         evictAllConnections("Database appears to be down");
         
         // 백오프 정책에 따라 재시도 일정 설정
         scheduleHouseKeepingWithBackoff();
         
         throw new SQLTransientConnectionException("Database connectivity failure detected", e);
      }
   }
}

private void evictAllConnections(String reason) {
   // 모든 커넥션을 무효화하고 풀 상태 리셋
   connectionBag.close();
   softEvictConnections(connectionBag.values(), reason);
   
   // 새 커넥션백 생성
   connectionBag = new ConcurrentBag<>(this);
}
```

### 알림 메커니즘

HikariCP는 커넥션 문제 발생 시 관리자에게 알리기 위한 다양한 메커니즘을 제공합니다:

#### 로깅

문제가 있는 커넥션은 자세한 정보와 함께 로그에 기록됩니다:

```

private void handleBrokenConnection(PoolEntry poolEntry, String reason, Throwable cause) {
   final String connectionId = poolEntry.connection.toString();
   logger.warn("Connection {} marked as broken: {}", connectionId, reason, cause);
   
   metrics.incrementBrokenConnectionCount();
   closeConnection(poolEntry, reason);
}
```

#### JMX 모니터링

HikariCP는 JMX를 통해 풀 상태와 관련된 다양한 메트릭을 노출합니다:

```

public class HikariPool implements HikariPoolMXBean {
   @Override
   public int getActiveConnections() {
      return connectionBag.getActiveCount();
   }
   
   @Override
   public int getIdleConnections() {
      return connectionBag.getIdleCount();
   }
   
   @Override
   public int getTotalConnections() {
      return connectionBag.getTotalCount();
   }
   
   @Override
   public int getThreadsAwaitingConnection() {
      return connectionBag.getWaitingThreadCount();
   }
   
   @Override
   public int getConnectionTimeoutsPerSecond() {
      return (int) metrics.getConnectionTimeoutRate();
   }
}
```

#### 헬스체크 인터페이스

HikariCP는 풀 상태를 외부 모니터링 시스템에 노출하기 위한 헬스체크 인터페이스를 제공할 수 있습니다:

```

public class HikariPoolHealthCheck implements HealthCheck {
   private final HikariDataSource dataSource;
   
   @Override
   public Result check() {
      final int active = dataSource.getHikariPoolMXBean().getActiveConnections();
      final int max = dataSource.getMaximumPoolSize();
      
      // 풀이 거의 포화 상태인 경우 경고
      if (active > max * 0.9) {
         return Result.unhealthy("Connection pool near capacity: %d of %d connections in use", active, max);
      }
      
      // 대기 스레드가 많은 경우 경고
      final int waiting = dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
      if (waiting > 10) {
         return Result.unhealthy("%d threads waiting for connections", waiting);
      }
      
      return Result.healthy("Pool state OK: %d active, %d idle, %d waiting", 
            active, dataSource.getHikariPoolMXBean().getIdleConnections(), waiting);
   }
}
```

### 벤더별 최적화

HikariCP는 다양한 데이터베이스 벤더에 맞게 커넥션 검증 전략을 최적화합니다:

#### MySQL 최적화

```

private void configureValidationForMySql(HikariConfig config) {
   // MySQL은 JDBC4 isValid()를 효율적으로 구현함
   config.setConnectionValidationMethod("JDBC4");
   
   // 기본 검증 쿼리 설정 (isValid()가 작동하지 않는 경우)
   if (config.getConnectionTestQuery() == null) {
      config.setConnectionTestQuery("SELECT 1");
   }
   
   // MySQL 서버 장애 감지를 위한 설정
   if (config.getDataSourceProperties().getProperty("socketTimeout") == null) {
      config.addDataSourceProperty("socketTimeout", String.valueOf(validationTimeout));
   }
}
```

#### PostgreSQL 최적화

```

private void configureValidationForPostgreSQL(HikariConfig config) {
   // PostgreSQL은 JDBC4 isValid()를 효율적으로 구현함
   config.setConnectionValidationMethod("JDBC4");
   
   // 기본 검증 쿼리 설정
   if (config.getConnectionTestQuery() == null) {
      config.setConnectionTestQuery("SELECT 1");
   }
   
   // Statement 타임아웃 설정
   if (config.getDataSourceProperties().getProperty("socketTimeout") == null) {
      config.addDataSourceProperty("socketTimeout", String.valueOf(validationTimeout));
   }
}
```

#### Oracle 최적화

```

private void configureValidationForOracle(HikariConfig config) {
   // Oracle 드라이버는 isValid()가 느릴 수 있으므로 간단한 쿼리 사용
   config.setConnectionValidationMethod("QUERY");
   
   // 기본 검증 쿼리 설정
   if (config.getConnectionTestQuery() == null) {
      config.setConnectionTestQuery("SELECT 1 FROM DUAL");
   }
   
   // Fast Connection Failover 활성화
   if (config.getDataSourceProperties().getProperty("oracle.net.CONNECT_TIMEOUT") == null) {
      config.addDataSourceProperty("oracle.net.CONNECT_TIMEOUT", String.valueOf(validationTimeout));
   }
}
```

### 고급 검증 기법

HikariCP는 일반적인 검증 이외에도 몇 가지 고급 검증 기법을 제공합니다:

#### 풀 자가 건전성 검사

HikariCP는 자체적으로 풀의 건전성을 주기적으로 진단하고 필요한 조치를 취합니다:

```

private void performPoolHealthCheck() {
   final int totalConnections = getTotalConnections();
   final int idleConnections = getIdleConnections();
   final int activeConnections = getActiveConnections();
   
   // 연결 수가 예상과 다른 경우 조사
   if (totalConnections != activeConnections + idleConnections) {
      logger.warn("Inconsistent pool state detected: total={}, active={}, idle={}",
            totalConnections, activeConnections, idleConnections);
      
      // 복구 조치 수행
      recoverPoolState();
   }
   
   // 최소 유휴 커넥션 유지
   if (idleConnections < config.getMinimumIdle() && totalConnections < config.getMaximumPoolSize()) {
      addConnections(Math.min(config.getMinimumIdle() - idleConnections, 
                             config.getMaximumPoolSize() - totalConnections));
   }
}
```

#### 커넥션 수명 주기 관리

HikariCP는 커넥션의 수명 주기를 관리하여 오래된 커넥션을 자동으로 교체합니다:

```

private void manageConnectionLifecycle() {
   final long now = currentTime();
   final List<PoolEntry> allEntries = connectionBag.values();
   
   for (PoolEntry entry : allEntries) {
      final long connectionAge = now - entry.createTime;
      
      // 최대 수명 초과 여부 확인
      if (connectionAge > config.getMaxLifetime()) {
         // 커넥션이 사용 중이 아닐 때만 교체
         if (connectionBag.reserve(entry)) {
            closeConnection(entry, "Connection reached maximum lifetime");
            if (getTotalConnections() < config.getMinimumIdle()) {
               addConnection();
            }
         }
      }
   }
}
```

#### 점진적 커넥션 교체

데이터베이스 재시작 같은 이벤트 이후 모든 커넥션을 한 번에 재생성하면 성능에 영향을 줄 수 있습니다. HikariCP는 커넥션을 점진적으로 교체하여 이러한 문제를 방지합니다:

```

private void softEvictConnections(Collection<PoolEntry> connections, String reason) {
   for (PoolEntry poolEntry : connections) {
      // 소프트 제거 플래그 설정
      poolEntry.markEvicted();
      
      // 비활성 커넥션은 즉시 제거
      if (connectionBag.reserve(poolEntry)) {
         closeConnection(poolEntry, reason);
      }
   }
}
```

### 결론: 신뢰성과 성능의 균형

HikariCP의 커넥션 테스트와 유효성 검사 메커니즘은 고성능 커넥션 풀링의 또 다른 중요한 측면을 보여줍니다. 단순히 빠른 커넥션 관리를 넘어 신뢰성과 안정성이 실제 운영 환경에서 얼마나 중요한지 강조합니다. 이러한 메커니즘은 다음과 같은 이점을 제공합니다:

#### 사용자 경험 향상

끊긴 커넥션이나 무효화된 커넥션으로 인한 오류를 최소화하여 최종 사용자 경험을 향상시킵니다. 사용자는 "데이터베이스 연결 오류" 같은 메시지 대신 애플리케이션이 항상 원활하게 작동하는 것을 경험하게 됩니다.

#### 시스템 복원력 강화

네트워크 불안정성, 데이터베이스 재시작, 일시적인 장애와 같은 상황에서도 시스템이 자동으로 복구될 수 있도록 합니다. 이는 현대적인 클라우드 환경이나 마이크로서비스 아키텍처에서 특히 중요합니다.

#### 운영 부담 감소

자동화된 검증 및 복구 메커니즘은 운영팀의 수작업 개입 필요성을 줄이고, 심야에 발생하는 긴급 상황을 최소화합니다. 시스템이 자체적으로 많은 일반적인 문제를 해결할 수 있기 때문입니다.

#### 성능과 신뢰성의 균형

HikariCP의 접근 방식은 불필요한 검증을 최소화하면서도 필요한 시점에 적절한 검증을 수행하여 신뢰성과 성능 사이의 이상적인 균형을 찾는 방법을 보여줍니다. 이는 다음과 같은 설계 원칙을 통해 달성됩니다:

1. **선택적 검증**: 모든 커넥션을 항상 검사하는 대신, 구성 가능한 조건에 따라 필요할 때만 검사합니다.
2. **효율적인 검증 방법**: 데이터베이스 드라이버가 제공하는 가장 효율적인 방법(일반적으로 `isValid()`)을 우선적으로 사용합니다.
3. **지능적인 백그라운드 처리**: 사용자 작업을 차단하지 않고 백그라운드에서 대부분의 유지 관리 작업을 수행합니다.
4. **점진적 복구**: 장애 발생 시 시스템에 갑작스러운 부하를 주지 않도록 점진적인 복구 전략을 사용합니다.

HikariCP의 커넥션 검증 접근 방식은 고성능 라이브러리가 실제 운영 환경의 요구 사항을 어떻게 균형 있게 충족시켜야 하는지에 대한 중요한 교훈을 제공합니다. 극단적인 성능 최적화만을 추구하는 것이 아니라, 실제 사용 환경에서의 안정성과 장애 허용성을 고려한 설계가 진정한 고품질 소프트웨어의 핵심입니다.

다음 챕터에서는 HikariCP의 전체적인 아키텍처와 구현 철학을 종합적으로 분석하고, 이러한 설계 결정이 모던 자바 애플리케이션 개발에 주는 시사점과 교훈을 살펴보겠습니다.
