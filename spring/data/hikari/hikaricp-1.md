# HikariCP의 유틸클래스 (1)

### UtilityElf란?

는 HikariCP 내부에서 사용되는 다양한 유틸리티 메서드를 모아둔 클래스입니다. 이름에서 알 수 있듯이, '요정(Elf)'처럼 뒤에서 HikariCP의 다양한 기능을 지원하는 역할을 합니다.

### 보안 관련 기능: JDBC URL 비밀번호 마스킹

```java
private static final Pattern PASSWORD_MASKING_PATTERN = Pattern.compile("([?&;][^&#;=]*[pP]assword=)[^&#;]*");

public static String maskPasswordInJdbcUrl(String jdbcUrl) {
   return PASSWORD_MASKING_PATTERN.matcher(jdbcUrl).replaceAll("$1<masked>");
}
```

이 메서드는 JDBC URL에서 비밀번호 부분을 로 대체합니다. 로깅이나 오류 메시지에 민감한 정보가 노출되지 않도록 하는 중요한 보안 기능입니다. `<masked>`

정규식을 자세히 살펴보면:

* \- URL에서 password 파라미터를 찾습니다 `([?&;][^&#;=]*[pP]assword=)`
* `[^&#;]*` - 파라미터 값(비밀번호)을 찾습니다
* \- 비밀번호 부분을 로 대체합니다 `$1<masked><masked>`

예를 들어, `jdbc:mysql://localhost:3306/mydb?user=root&password=secret123`는 `jdbc:mysql://localhost:3306/mydb?user=root&password=<masked>`로 변환됩니다.

### 스레드 관련 유틸리티

```java
public static void quietlySleep(final long millis) {
   try {
      Thread.sleep(millis);
   }
   catch (InterruptedException e) {
      // I said be quiet!
      currentThread().interrupt();
   }
}
```

이 메서드는 예외를 던지지 않고 지정된 시간(밀리초) 동안 현재 스레드를 대기시킵니다. 인터럽트가 발생하면 인터럽트 상태를 다시 설정하여 호출자에게 알립니다. 주석의 유머("I said be quiet!")는 이 메서드가 조용히 작동해야 한다는 의도를 보여줍니다.

### 스레드 풀 생성 유틸리티

```java
public static ThreadPoolExecutor createThreadPoolExecutor(final int queueSize, final String threadName, ThreadFactory threadFactory, final RejectedExecutionHandler policy) {
   return createThreadPoolExecutor(new LinkedBlockingQueue<>(queueSize), threadName, threadFactory, policy);
}

public static ThreadPoolExecutor createThreadPoolExecutor(final BlockingQueue<Runnable> queue, final String threadName, ThreadFactory threadFactory, final RejectedExecutionHandler policy) {
   if (threadFactory == null) {
      threadFactory = new DefaultThreadFactory(threadName);
   }

   var executor = new ThreadPoolExecutor(1 /*core*/, 1 /*max*/, 5 /*keepalive*/, SECONDS, queue, threadFactory, policy);
   executor.allowCoreThreadTimeOut(true);
   return executor;
}
```

이 메서드들은 HikariCP 내부에서 사용하는 스레드 풀을 생성합니다. 주목할 점:

1. **단일 스레드 풀**: 코어 스레드 수와 최대 스레드 수가 모두 1로 설정됩니다. HikariCP는 단일 스레드로 여러 작업을 효율적으로 처리합니다.
2. **코어 스레드 타임아웃**: 로 설정하여 유휴 상태의 코어 스레드도 종료되도록 합니다. `allowCoreThreadTimeOut(true)`
3. **맞춤형 스레드 팩토리**: 스레드 이름 지정과 데몬 스레드 설정을 위한 `DefaultThreadFactory`를 제공합니다.

### 트랜잭션 격리 수준 처리

```java
public static int getTransactionIsolation(final String transactionIsolationName) {
   // 생략된 구현 내용
}
```

이 메서드는 문자열로 지정된 트랜잭션 격리 수준을 해당하는 JDBC 정수 값으로 변환합니다. 세부 구현에는 몇 가지 흥미로운 점이 있습니다:

1. **터키어 로케일 버그 방지**: 를 사용하여 특정 로케일에서 발생하는 문제를 방지합니다. `toUpperCase(Locale.ENGLISH)`
2. **열거형과 정수 값 지원**: 문자열 이름(예: "SERIALIZABLE")과 정수 값(예: "8") 모두 지원합니다.
3. **포괄적인 예외 처리**: 잘못된 격리 수준 값에 대해 명확한 오류 메시지를 제공합니다.

### 보조 클래스

#### CustomDiscardPolicy

```java
public static class CustomDiscardPolicy implements RejectedExecutionHandler {
   @Override
   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
   }
}
```

이 클래스는 스레드 풀이 작업을 거부할 때 어떻게 처리할지 정의합니다. 구현은 비어 있어서 작업을 조용히 버립니다. 이는 특정 상황에서 작업을 잃어버리는 것이 블로킹되는 것보다 낫다는 HikariCP의 철학을 반영합니다.

#### DefaultThreadFactory

```java
public static final class DefaultThreadFactory implements ThreadFactory {
   private final String threadName;
   private final boolean daemon;

   public DefaultThreadFactory(String threadName) {
      this.threadName = threadName;
      this.daemon = true;
   }

   @Override
   @SuppressWarnings("NullableProblems")
   public Thread newThread(Runnable r) {
      var thread = new Thread(r, threadName);
      thread.setDaemon(daemon);
      return thread;
   }
}
```

이 클래스는 이름이 지정된 데몬 스레드를 생성합니다. 데몬 스레드를 사용하면 애플리케이션이 종료될 때 스레드가 강제로 종료되므로, 애플리케이션 종료를 막지 않습니다.

### UtilityElf의 설계 철학

UtilityElf 클래스는 HikariCP의 전반적인 설계 철학을 잘 보여줍니다:

1. **단순성**: 각 메서드는 하나의 작업만 수행합니다.
2. **효율성**: 불필요한 객체 생성이나 연산을 피합니다.
3. **안전성**: 예외 상황을 신중하게 처리합니다.
4. **명확성**: 메서드명과 주석이 명확합니다.
