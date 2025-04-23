# HikariCP의 커넥션 프록시와 누수 감지 메커니즘

데이터베이스 커넥션 풀의 가장 흔한 문제 중 하나는 커넥션 누수(connection leak)입니다. 애플리케이션이 데이터베이스 커넥션을 사용한 후 적절히 반환하지 않으면, 풀의 가용 커넥션이 점차 고갈되어 결국 애플리케이션이 새로운 커넥션을 얻지 못하는 상황에 이르게 됩니다.&#x20;

### 커넥션 프록시의 필요성

데이터베이스 커넥션 풀에서 프록시를 사용하는 주요 이유는 다음과 같습니다:

#### 커넥션 행동 제어

프록시를 통해 커넥션의 동작을 가로채고 확장할 수 있습니다. 예를 들어, 커넥션 반환 시 실제로는 물리적 연결을 닫지 않고 풀로 반환하는 동작을 구현할 수 있습니다.

#### 커넥션 상태 추적

프록시는 커넥션이 언제, 어디서 획득되었고, 얼마나 오래 사용되었는지 등의 정보를 추적할 수 있습니다.

#### 안전장치 추가

프록시를 통해 이미 반환된 커넥션에 대한 접근을 방지하거나, 트랜잭션이 완료되지 않은 채로 반환되는 것을 감지하는 등의 안전장치를 구현할 수 있습니다.

### HikariCP의 프록시 아키텍처

HikariCP는 Java의 동적 프록시(Dynamic Proxy) 또는 바이트코드 생성(Javassist)을 사용하여 커넥션 프록시를 생성합니다. 기본 구조는 다음과 같습니다:

```java
public class ProxyFactory {
   /**
    * 주어진 인터페이스에 대한 프록시 객체를 생성합니다.
    */
   @SuppressWarnings("unchecked")
   public static <T> T createProxy(final T target, final InvocationHandler handler, final Class<?>... interfaces) {
      // 기본 인터페이스 목록에 제공된 인터페이스들을 추가
      final int length = interfaces.length;
      final Class<?>[] newInterfaces = new Class<?>[length + 1];
      System.arraycopy(interfaces, 0, newInterfaces, 0, length);
      newInterfaces[length] = ProxyObject.class;
      
      // 자바 표준 동적 프록시 생성
      return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), newInterfaces, handler);
   }
   
   // Javassist를 사용하는 구현도 있음
}
```

#### 핵심 인터페이스: ConnectionProxy

HikariCP의 커넥션 프록시 구현의 중심에는 ConnectionProxy 인터페이스가 있습니다:

```java
public interface ConnectionProxy extends Connection {
   /**
    * 이 프록시가 감싸고 있는 실제 커넥션을 반환합니다.
    *
    * @return 실제 JDBC 커넥션
    */
   Connection getDelegate();
   
   /**
    * 풀에 의해 close()가 호출되었을 때 수행되는 논리를 담습니다.
    */
   void close() throws SQLException;
   
   /**
    * 이 커넥션이 닫혔는지 확인합니다.
    */
   boolean isClosed() throws SQLException;
   
   /**
    * 이 커넥션이 취득된 스택 트레이스를 반환합니다.
    */
   StackTraceElement[] getStackTrace();
}
```

#### ConnectionHandler 구현

실제 커넥션 프록시 동작은 InvocationHandler 구현체에서 정의됩니다:

```java
public class ConnectionHandler implements InvocationHandler {
   private final Connection delegate;
   private final PoolEntry poolEntry;
   private final ProxyLeakTask leakTask;
   private final long creationTimeMillis;
   private final StackTraceElement[] stackTrace;
   private final HikariPool hikariPool;
   
   private volatile boolean closed;
   
   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      final String methodName = method.getName();
      
      // close() 메서드 처리
      if ("close".equals(methodName)) {
         closeConnection();
         return null;
      }
      
      // isClosed() 메서드 처리
      if ("isClosed".equals(methodName)) {
         return closed;
      }
      
      // 이미 닫힌 커넥션에 대한 호출 처리
      if (closed) {
         throw new SQLException("Connection is closed");
      }
      
      // getDelegate() 메서드 처리
      if ("getDelegate".equals(methodName)) {
         return delegate;
      }
      
      // getStackTrace() 메서드 처리
      if ("getStackTrace".equals(methodName)) {
         return stackTrace;
      }
      
      // 그 외 모든 메서드는 실제 커넥션에 위임
      try {
         return method.invoke(delegate, args);
      }
      catch (InvocationTargetException e) {
         throw e.getTargetException();
      }
   }
   
   /**
    * 커넥션을 닫고 풀로 반환합니다.
    */
   private void closeConnection() {
      if (!closed) {
         closed = true;
         
         // 누수 감지 작업이 있으면 취소
         if (leakTask != null) {
            leakTask.cancel();
         }
         
         // 사용 시간 기록
         final long elapsedTimeMs = System.currentTimeMillis() - creationTimeMillis;
         hikariPool.recordConnectionUsage(poolEntry, elapsedTimeMs);
         
         // 풀로 커넥션 반환
         hikariPool.recycle(poolEntry);
      }
   }
}
```

### 누수 감지 메커니즘

HikariCP의 누수 감지 메커니즘은 다음과 같은 핵심 구성 요소로 이루어져 있습니다:

#### 누수 감지 구성

HikariConfig에서는 누수 감지 관련 설정을 제공합니다:

```java
public class HikariConfig {
   // 커넥션 누수로 간주되기까지의 대여 시간 한계 (기본값 0 - 비활성화)
   private long leakDetectionThreshold;
   
   // 누수 감지 통계 수집 활성화 여부
   private boolean leakDetectionEnabled;
   
   // Getter와 Setter 메서드
   public long getLeakDetectionThreshold() { return leakDetectionThreshold; }
   public void setLeakDetectionThreshold(long leakDetectionThreshold) {
      this.leakDetectionThreshold = leakDetectionThreshold;
   }
}
```

#### ProxyLeakTask 구현

누수 감지의 핵심은 ProxyLeakTask 클래스입니다. 이 클래스는 TimerTask를 상속받아 예약된 시간 이후에 실행되며, 그 시점까지 커넥션이 반환되지 않았다면 경고 로그를 출력합니다:

```java
public class ProxyLeakTask extends TimerTask {
   private static final Logger LOGGER = LoggerFactory.getLogger(ProxyLeakTask.class);
   
   private final StackTraceElement[] stackTrace;
   private final long leakDetectionThreshold;
   private final String connectionName;
   private final AtomicInteger openConnections;
   
   @Override
   public void run() {
      LOGGER.warn("Connection leak detection triggered. Connection was obtained from the pool more than {} ms ago, and has not been returned. Stack trace of where the connection was obtained:", leakDetectionThreshold);
      
      for (StackTraceElement stackTraceElement : stackTrace) {
         LOGGER.warn("    at {}", stackTraceElement);
      }
      
      openConnections.incrementAndGet();
   }
}
```

#### 누수 감지 설정

커넥션 프록시 생성 시 누수 감지 작업을 설정합니다:

```java
private Connection createProxyConnection(PoolEntry poolEntry, Connection connection) {
   // 누수 감지가 활성화되어 있다면 프록시에 누수 감지 작업 추가
   ProxyLeakTask leakTask = null;
   if (config.getLeakDetectionThreshold() > 0) {
      leakTask = new ProxyLeakTask(poolEntry, config.getLeakDetectionThreshold());
      scheduleTask(leakTask, config.getLeakDetectionThreshold());
   }
   
   // 현재 스택 트레이스 캡처 (디버깅용)
   StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
   
   // 프록시 생성
   return ProxyFactory.createProxy(connection, new ConnectionHandler(poolEntry, connection, leakTask, System.currentTimeMillis(), stackTrace, this));
}
```

### 누수 감지 실전 예시

HikariCP의 누수 감지 기능이 실제로 어떻게 동작하는지 살펴보겠습니다:

#### 누수 감지 활성화

애플리케이션 설정에서 누수 감지를 활성화합니다:

```java
HikariConfig config = new HikariConfig();
// ... 다른 설정들 ...

// 30초 이상 대여된 커넥션은 누수로 간주
config.setLeakDetectionThreshold(30000);

HikariDataSource ds = new HikariDataSource(config);
```

#### 누수 시나리오

다음은 커넥션 누수가 발생하는 일반적인 시나리오입니다:

```java
Connection conn = dataSource.getConnection();
try {
   // 데이터베이스 작업 수행
   Statement stmt = conn.createStatement();
   ResultSet rs = stmt.executeQuery("SELECT * FROM users");
   
   // 비즈니스 로직 처리
   processResults(rs);
   
   // conn.close()를 호출하지 않음 - 누수 발생!
}
catch (SQLException e) {
   // 예외 처리
}
```

#### 누수 감지 로그

30초 후, HikariCP는 다음과 같은 경고 로그를 출력합니다:

```java
2023-05-15 14:23:45 WARN  ProxyLeakTask - Connection leak detection triggered. Connection was obtained from the pool more than 30000 ms ago, and has not been returned. Stack trace of where the connection was obtained:
    at com.example.service.UserService.getAllUsers(UserService.java:42)
    at com.example.controller.UserController.listUsers(UserController.java:28)
    at com.example.controller.UserController$$FastClassBySpringCGLIB$$e2dffb5.invoke(<generated>)
    at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
    at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:793)
```

이 로그를 통해 어떤 코드가 커넥션을 얻어간 후 반환하지 않았는지 정확히 파악할 수 있습니다.

### 누수 방지를 위한 추가 안전장치

HikariCP는 커넥션 누수 감지뿐만 아니라 누수를 방지하기 위한 몇 가지 추가 안전장치를 제공합니다:

#### maxLifetime 설정

모든 커넥션에 최대 수명을 설정하여, 오래된 커넥션을 자동으로 교체합니다:

```java
config.setMaxLifetime(1800000); // 30분
```

이 설정은 누수된 커넥션도 결국 최대 수명에 도달하면 강제로 폐기되도록 합니다.

#### connectionTimeout 설정

커넥션 획득에 대한 타임아웃을 설정하여, 풀이 고갈된 상황에서 무한정 대기하는 것을 방지합니다:

```java
config.setConnectionTimeout(30000); // 30초
```

#### 자원 해제 확인

HikariCP는 커넥션이 풀로 반환될 때 다양한 검사를 수행하여 자원이 적절히 해제되었는지 확인합니다:

```java
private void quietlyCloseAll(Connection connection, Statement... statements) {
   for (Statement statement : statements) {
      if (statement != null) {
         try {
            statement.close();
         }
         catch (SQLException e) {
            // 무시하거나 로깅
         }
      }
   }
}
```

### 프록시의 다른 용도

HikariCP의 커넥션 프록시는 누수 감지 외에도 다양한 기능을 제공합니다:

#### 트랜잭션 상태 관리

프록시는 커넥션의 트랜잭션 상태를 추적하여 자동 커밋 모드나 격리 수준이 변경되었을 때 이를 감지하고 풀로 반환 시 원래 상태로 복원할 수 있습니다:

```java
private void resetConnectionState(Connection connection, boolean originalAutoCommit) throws SQLException {
   if (connection.getAutoCommit() != originalAutoCommit) {
      connection.setAutoCommit(originalAutoCommit);
   }
}
```

#### 성능 측정

프록시는 각 SQL 작업의 실행 시간을 측정하여 성능 모니터링에 활용할 수 있습니다:

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
   long startTime = System.nanoTime();
   try {
      return method.invoke(delegate, args);
   }
   finally {
      long elapsedTime = System.nanoTime() - startTime;
      if (method.getName().startsWith("execute")) {
         metricsTracker.recordExecutionTime(elapsedTime);
      }
   }
}
```

#### 읽기 전용 모드 최적화

일부 데이터베이스는 읽기 전용 트랜잭션에 대해 최적화를 제공합니다. 프록시는 이러한 설정을 추적하고 관리할 수 있습니다:

```java
@Override
public void setReadOnly(boolean readOnly) throws SQLException {
   // 현재 상태와 다를 때만 설정 변경
   if (delegate.isReadOnly() != readOnly) {
      delegate.setReadOnly(readOnly);
   }
}
```

### 커넥션 프록시의 성능 영향

프록시는 추가적인 추상화 계층을 도입하므로 성능에 영향을 미칠 수 있습니다. HikariCP는 이러한 오버헤드를 최소화하기 위해 다음과 같은 전략을 사용합니다:

#### 경량 프록시 설계

기본적인 기능만 프록시에 구현하고, 불필요한 기능은 제외합니다:

```java
// 메서드 호출 위임의 단순화된 예
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
   // 몇 가지 특수 메서드만 직접 처리
   if ("close".equals(method.getName())) {
      closeConnection();
      return null;
   }
   
   // 나머지는 모두 위임
   return method.invoke(delegate, args);
}
```

#### 동적 바이트코드 생성

필요한 경우 Javassist와 같은 라이브러리를 사용하여 리플렉션보다 더 효율적인 프록시를 생성할 수 있습니다:

```java
// Javassist를 사용한 프록시 생성 예
public static <T> T createJavassistProxy(Class<T> interfaceClass, T delegate, InvocationHandler handler) {
   ProxyFactory factory = new ProxyFactory();
   factory.setSuperclass(interfaceClass);
   factory.setFilter(method -> !method.getName().equals("finalize"));
   
   Class<?> proxyClass = factory.createClass();
   try {
      T proxy = (T) proxyClass.getDeclaredConstructor().newInstance();
      ((ProxyObject) proxy).setHandler(handler);
      return proxy;
   }
   catch (Exception e) {
      throw new RuntimeException(e);
   }
}
```

#### 선택적 기능 활성화

누수 감지와 같은 고급 프록시 기능은 필요할 때만 활성화됩니다:

```java
// 누수 감지가 활성화된 경우에만 관련 코드 실행
if (config.getLeakDetectionThreshold() > 0) {
   leakTask = new ProxyLeakTask(poolEntry, config.getLeakDetectionThreshold());
   scheduleTask(leakTask, config.getLeakDetectionThreshold());
}
```

### 결론: 안정성과 디버깅을 위한 프록시

HikariCP의 커넥션 프록시와 누수 감지 메커니즘은 성능 최적화뿐만 아니라 안정성과 디버깅 용이성을 향상시키는 중요한 기능입니다. 이 시스템은 다음과 같은 이점을 제공합니다:

#### 개발자 친화적 디버깅

누수가 발생한 정확한 위치를 알려주는 스택 트레이스는 개발자가 문제를 빠르게 진단하고 해결하는 데 큰 도움이 됩니다. 오류 메시지만으로는 알기 어려운 컨텍스트 정보를 제공함으로써 디버깅 시간을 크게 단축시킵니다.

#### 시스템 안정성 향상

커넥션 누수와 같은 흔한 문제를 조기에 감지하고 대응함으로써 전체 시스템의 안정성을 향상시킵니다. 이는 특히 장기 실행 애플리케이션에서 중요합니다.

#### 투명한 확장

프록시 패턴을 사용함으로써 기존 JDBC API를 변경하지 않고도 추가 기능을 제공할 수 있습니다. 이는 애플리케이션 코드의 변경 없이도 고급 기능을 도입할 수 있게 합니다.

#### 성능과 안정성의 균형

HikariCP는 성능에 대한 집착으로 잘 알려져 있지만, 커넥션 프록시와 누수 감지 메커니즘은 적절한 오버헤드로 안정성을 크게 향상시킬 수 있다는 것을 보여줍니다. 이는 실제 프로덕션 환경에서 성능과 안정성 사이의 균형이 얼마나 중요한지를 잘 보여주는 예입니다.

HikariCP의 프록시 시스템은 "신뢰할 수 있는 고성능 라이브러리"라는 목표를 달성하기 위한 중요한 구성 요소입니다. 단순히 빠른 것뿐만 아니라 문제 상황에서도 개발자에게 유용한 정보를 제공하고 시스템을 안정적으로 유지하는 데 기여합니다.
