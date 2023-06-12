# LifeCycle

**Java 언어에서 멀티스레딩은 Thread 의 핵심 개념에 의해 구동됩니다** . \
수명 주기 동안 스레드는 다양한 상태를 거칩니다.

<img src="../../../.gitbook/assets/file.excalidraw (10).svg" alt="" class="gitbook-drawing">

### 상태

<table><thead><tr><th width="138">STATUS</th><th>DESC</th></tr></thead><tbody><tr><td><em><strong>NEW</strong></em></td><td>아직 실행을 시작하지 않은 새로 생성된 스레드</td></tr><tr><td><em><strong>RUNNABLE</strong></em></td><td>실행 중이거나 실행할 준비가 되었지만 리소스 할당을 기다리고 있습니다</td></tr><tr><td><em><strong>BLOCKED</strong></em></td><td>동기화된 블록/방법에 들어가거나 다시 들어가기 위해 모니터 잠금을 획득하기 위해 대기 중</td></tr><tr><td><em><strong>WAITING</strong></em></td><td>시간 제한 없이 다른 스레드가 특정 작업을 수행하기를 기다립니다</td></tr><tr><td><em><strong>TIMED_WAITING</strong></em></td><td>지정된 기간 동안 다른 스레드가 특정 작업을 수행하기를 기다립니다</td></tr><tr><td><em><strong>TERMINATED</strong></em></td><td>실행을 완료했습니다</td></tr></tbody></table>

### How do code?

#### NEW , RUNNABLE

```kotlin
val runnable: Runnable = Runnable {
        println("Thread is running")
    }
val t = Thread(runnable)
println(t.state) // NEW
t.start()
println(t.state) // RUNNABLE
```

#### BLOCKED

```kotlin
class BlockRunnable: Runnable {
    override fun run() {
        commonResource();
        TODO("Not yet implemented")
    }
    @Synchronized
    fun commonResource() {
        while(true) { }
    }
}

fun main(args: Array<String>) {

    val thread1 = Thread(BlockRunnable())
    val thread2 = Thread(BlockRunnable())
    thread1.start();
    thread2.start();
    Thread.sleep(1000);
    System.out.println(thread2.getState()); // BLOCKED
}
```

#### WAITING

```kotlin
fun inner() : Runnable= Runnable { try {
    Thread.sleep(1000)
} catch (e: InterruptedException) {
    Thread.currentThread().interrupt()
    e.printStackTrace()
}
    println(thread.state) // WAITING
}

fun waiting() = Runnable {
    val inner = Thread(inner())
    inner.start()

    try {
        inner.join()
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        e.printStackTrace()
    }
}

val thread:Thread = Thread(waiting())

fun main() {
    thread.start();
}
```

#### TIME WAITING

```kotlin
fun timeWaiting() = Runnable {
    Thread.sleep(5000)
}

val thread:Thread = Thread(timeWaiting())

fun main() {
    thread.start();
    Thread.sleep(1000)
    println(thread.state) // TIME WAIT
}
```

#### TERMINATE

```kotlin
fun timeWaiting() = Runnable {
    println("TERMINATE")
}

val thread:Thread = Thread(timeWaiting())

fun main() {
    thread.start();
    Thread.sleep(1000)
    println(thread.state)
}
```
