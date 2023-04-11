# Async

Spring Framework는 비동기적으로 실행되는 코드를 지원하며, 이는 Spring MVC와 같은 Web Framework에서도 사용할 수 있습니다. \
Spring에서 비동기 처리를 구현하는 방법 중 하나는 `@Async` 어노테이션을 사용하는 것입니다.&#x20;

### How do code?

setting gradle, bean

{% code title="gradle" %}
```kotlin
runtimeOnly("com.h2database:h2")
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-web")
```
{% endcode %}

```kotlin
@EnableAsync
@Configuration
class ExecutorConfig {

    @Bean
    fun executor() = ThreadPoolTaskExecutor()
        .also {
            it.corePoolSize = 2
            it.maxPoolSize = 2
            it.queueCapacity= 10
            it.setThreadNamePrefix("junnyland-")
            it.initialize()
        }
}
```

비동기 메소드를 호출하는 방법은 두 가지가 있습니다.&#x20;

첫 번째 방법은 메소드를 호출하고 반환 받지 않는 것 입니다\
두 번째 방법은 `Future` 객체를 사용하는 것입니다. `Future` 객체는 비동기 작업의 결과를 나타내며, 작업이 완료되기 전에는 블로킹되지 않습니다.

```kotlin
@Component
class AsyncComponent{
    @Async
    fun doSomething() {
        println("${Thread.currentThread()}:: start")
        println("${Thread.currentThread()}:: end")
    }
}
@Service
class TestService(
    private val service: AsyncComponent
) {
    fun execute() {
        println("${Thread.currentThread()}:: service start")
        service.doSomething()
        println("${Thread.currentThread()}:: service end")
    }
}

----------------------------------------------------
Thread[Test worker,5,main]:: service start
Thread[Test worker,5,main]:: service end
Thread[junnyland-1,5,main]:: start
Thread[junnyland-1,5,main]:: end
```

```kotlin
@Component
class AsyncComponent {
    @Async
    fun doResponse(): Future<String> {
        println("${Thread.currentThread()}:: start")
        println("${Thread.currentThread()}:: end")
        return AsyncResult("COMPLETE")
    }
}
@Service
class TestService(
    private val service: AsyncComponent
) {
    fun executeCallback() {
        println("${Thread.currentThread()}:: service start")
        val doResponse = service.doResponse()
        println("${Thread.currentThread()}:: service end : ${doResponse.get()}")
    }
}

----------------------------------------------------
Thread[Test worker,5,main]:: service start
Thread[junnyland-1,5,main]:: start
Thread[junnyland-1,5,main]:: end
Thread[Test worker,5,main]:: service end : COMPLETE
```

`Future.get()` 메소드는 결과가 반환될 때까지 현재 스레드를 차단(block)합니다.

## With Transaction

스프링에서 `@Async` 어노테이션과 `@Transactional` 어노테이션을 함께 사용하면, 예기치 않은 동작이 발생할 수 있습니다. \
이는 `@Async` 어노테이션과 `@Transactional` 어노테이션이 동시에 적용되는 메소드의 실행 컨텍스트가 다르기 때문입니다.

```kotlin
@Component
class AsyncComponent(
    private val repository: AsyncRepository,
    private val transactionManager: TransactionTemplate
) {
    @Async
    fun doResponse(): Future<String> {
        transactionManager.execute {
            println("${Thread.currentThread()}:: start")
            repository.saveAll() // error!
            println("${Thread.currentThread()}:: end")
            throw RuntimeException("test")
        }
        return AsyncResult("COMPLETE")
    }
}
```

TransactionTemplate을 사용하여 트랜잭션이 예기치 않게 커밋되지 않도록 해주어야 합니다.



## Difference Corrountine

`@Async`는 Spring Framework에서 지원하는 기능으로, Java의 Executor 또는 Java의 Thread를 사용하여 비동기 작업을 처리합니다. \
`@Async`는 별도의 스레드에서 비동기 작업을 실행하는 것으로, 스프링 애플리케이션 내에서 비동기적으로 실행할 수 있는 메소드를 지정하는 데 사용됩니다.

`Coroutine`은 Kotlin에서 제공하는 기능으로, 비동기 작업을 위해 논리적인 스레드를 사용합니다. \
Coroutine은 코루틴 스케줄러(Coroutine Scheduler)를 사용하여 비동기 작업을 실행합니다. 코루틴 스케줄러는 실제 스레드를 만들지 않고도 비동기 작업을 처리할 수 있도록 해주는 기술입니다.

### How do code

```kotlin
@Component
class AsyncComponent(
    private val repository: AsyncRepository,
    private val transactionManager: ReactiveTransactionManager
) {
    suspend fun doResponse(): String = transactionManager.suspendTransaction {
        println("${Thread.currentThread()}:: start")
        repository.saveAll() // error!
        println("${Thread.currentThread()}:: end")
        throw RuntimeException("test")
    }
}
```
