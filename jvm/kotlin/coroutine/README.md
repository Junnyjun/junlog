# Coroutine

코틀린의 코루틴(Coroutine)은 비동기 프로그래밍을 간결하고 효율적으로 처리하기 위한 강력한 도구입니다. 코루틴의 동작 원리를 깊이 있게 이해하기 위해서는 컴파일 과정에서의 변환, JVM에서의 실행 방식, 스택 프레임 관리, 그리고 운영체제와 CPU 수준에서의 작동 방식을 자세히 살펴볼 필요가 있습니다.

## 코루틴의 컴파일 과정과 CPS 변환

코루틴은 컴파일 시에 Continuation-Passing Style(CPS)로 변환됩니다. \
이는 함수의 호출을 일종의 상태 기계(state machine)로 바꾸는 과정입니다.

#### **`suspend` 함수의 변환**

`suspend` 키워드가 붙은 함수는 컴파일 시에 추가적인 파라미터로 `Continuation` 객체를 받는 형태로 변환됩니다. 이 `Continuation` 객체는 코루틴의 현재 상태를 유지하고 재개할 수 있도록 도와줍니다.

```kotlin
suspend fun compute(value: Int): Int {
    return value * 2
}
```

위의 `compute` 함수는 컴파일 시에 다음과 같이 변환됩니다.

```kotlin
fun compute(value: Int, continuation: Continuation<Int>): Any {
    // 변환된 코드 내용
}
```

여기서 `Any` 타입은 코루틴이 일시 중단될 수 있기 때문에 필요한 일반적인 반환 타입입니다.

#### **상태 기계(State Machine)로의 변환**

코루틴은 일시 중단 지점마다 상태를 저장하고 관리하기 위해 상태 기계로 변환됩니다. \
각 `suspend` 지점은 상태 번호로 표시되며, 코루틴이 재개될 때 해당 상태부터 실행을 이어갑니다.

```kotlin
suspend fun exampleCoroutine() {
    val result1 = suspendFunction1()
    val result2 = suspendFunction2(result1)
    println(result2)
}
```

컴파일러는 위의 코드를 상태 기계로 변환하여 각 `suspend` 함수 호출을 상태로 관리합니다.

### **상태 객체의 생성**

코루틴의 실행 상태를 저장하기 위해 컴파일러는 내부적으로 상태 객체를 생성합니다. \
이 객체는 지역 변수, 현재 실행 위치, `Continuation` 등 코루틴의 실행 정보를 포함합니다.

#### Continuation 인터페이스와 코루틴 컨텍스트

`Continuation` 인터페이스는 코루틴의 재개를 위한 메서드를 제공합니다.

```kotlin
interface Continuation<T> {
    val context: CoroutineContext
    fun resumeWith(result: Result<T>)
}
```

* `context`: 코루틴의 실행 컨텍스트를 나타내며, 디스패처, 잡(Job), 이름 등의 정보를 포함합니다.
* `resumeWith`: 코루틴을 재개하기 위한 메서드로, 결과 또는 예외를 받아서 처리합니다.

#### JVM에서의 코루틴 실행

코루틴은 JVM 상에서 일반 함수 호출과 유사하게 동작하지만, 일시 중단 지점에서 실행 상태를 저장하고 복원하는 추가적인 로직이 포함됩니다.

**스택 프레임 대신 힙 메모리 사용**

일반적인 함수 호출은 호출 스택에 스택 프레임을 생성하지만, 코루틴은 일시 중단과 재개를 위해 실행 상태를 힙 메모리에 저장합니다. 이는 코루틴이 스레드에 종속되지 않고 독립적으로 실행 상태를 관리할 수 있게 해줍니다.

```kotlin
suspend fun fetchData(): String {
    delay(1000)
    return "데이터 수신 완료"
}
```

컴파일러는 이 함수를 내부적으로 다음과 같이 변환합니다.

```kotlin
fun fetchData(continuation: Continuation<String>): Any {
    val continuationImpl = object : ContinuationImpl(continuation) {
        var label = 0
        override fun invokeSuspend(result: Result<Any>): Any {
            when (label) {
                0 -> {
                    label = 1
                    if (delay(1000, this) == COROUTINE_SUSPENDED) {
                        return COROUTINE_SUSPENDED
                    }
                }
                1 -> {
                    return "데이터 수신 완료"
                }
                else -> throw IllegalStateException("Unexpected label")
            }
        }
    }
    return continuationImpl.invokeSuspend(Result.success(Unit))
}
```

위의 코드는 코루틴의 상태를 관리하기 위한 상태 기계를 보여줍니다. `label` 변수는 현재 실행 위치를 나타내며, `invokeSuspend` 메서드는 코루틴이 재개될 때 호출됩니다.

#### 운영체제와 CPU 수준에서의 동작

코루틴은 \*\*사용자 공간(user-space)\*\*에서 동작하므로, 운영체제의 스케줄러나 CPU의 컨텍스트 스위칭에 직접적인 영향을 주지 않습니다.

**스레드와 코루틴의 차이점**

* **스레드**: 운영체제가 관리하는 실행 단위로, 각 스레드는 독립적인 호출 스택과 스택 프레임을 가집니다. 스레드 간의 컨텍스트 스위칭은 비용이 높습니다.
* **코루틴**: 사용자 공간에서 관리되는 경량 실행 단위로, 스레드 내에서 동작합니다. 코루틴 간의 컨텍스트 스위칭은 상태 객체의 전환만 필요하므로 오버헤드가 적습니다.

**디스패처와 스레드 풀**

코루틴의 실행은 `CoroutineDispatcher`에 의해 관리됩니다. 디스패처는 코루틴을 어느 스레드에서 실행할지 결정하며, 일반적으로 스레드 풀을 사용합니다.

* `Dispatchers.Default`: 공유된 백그라운드 스레드 풀을 사용합니다.
* `Dispatchers.IO`: I/O 작업에 최적화된 스레드 풀을 사용합니다.
* `Dispatchers.Main`: 메인 스레드에서 실행됩니다.

#### 코루틴의 일시 중단과 재개 과정

코루틴의 일시 중단과 재개는 다음과 같은 과정을 거칩니다.

1. **일시 중단 지점 도달**: `suspend` 함수나 일시 중단 함수 호출 시 코루틴은 현재 상태를 저장하고 `COROUTINE_SUSPENDED`를 반환합니다.
2. **상태 저장**: 코루틴의 지역 변수와 실행 위치 등이 상태 객체에 저장됩니다.
3. **재개 시점**: 일시 중단된 작업이 완료되면, `Continuation.resumeWith`를 통해 코루틴이 재개됩니다.
4. **상태 복원**: 저장된 상태를 바탕으로 코루틴의 실행이 이어집니다.

#### 코루틴의 메모리 관리

코루틴의 상태 객체는 힙 메모리에 저장되므로, 가비지 컬렉터에 의해 관리됩니다. 코루틴이 완료되거나 취소되면 관련된 메모리는 자동으로 회수됩니다.

**메모리 누수 방지**

코루틴을 사용할 때 메모리 누수를 방지하기 위해서는 코루틴의 생명 주기를 적절히 관리해야 합니다. 특히, 코루틴 내부에서 참조하는 자원(예: 네트워크 연결, 파일 등)은 필요에 따라 명시적으로 해제해야 합니다.

#### 코루틴과 스택 프레임

코루틴은 일시 중단 시 스택 프레임을 유지하지 않고 힙에 저장된 상태 객체를 통해 실행 상태를 관리합니다. 이는 코루틴이 스레드에 종속되지 않고, 많은 수의 코루틴을 효율적으로 관리할 수 있게 해줍니다.

#### 실제 코드 예시: 상태 기계로의 변환

다음은 `suspend` 함수가 상태 기계로 변환되는 과정을 보여주는 예시입니다.

```kotlin
suspend fun simpleCoroutine() {
    println("코루틴 시작")
    delay(1000)
    println("코루틴 재개")
}
```

컴파일러에 의해 변환된 코드:

```kotlin
class SimpleCoroutine(continuation: Continuation<Unit>) : ContinuationImpl(continuation) {
    var label = 0
    override fun invokeSuspend(result: Result<Any?>): Any? {
        when (label) {
            0 -> {
                label = 1
                println("코루틴 시작")
                if (delay(1000, this) == COROUTINE_SUSPENDED) {
                    return COROUTINE_SUSPENDED
                }
            }
            1 -> {
                println("코루틴 재개")
                return Unit
            }
            else -> throw IllegalStateException("잘못된 상태")
        }
    }
}
```

여기서 `label`은 현재 실행 위치를 나타내며, 코루틴이 재개될 때 해당 위치부터 실행을 이어갑니다.

#### 코루틴의 성능 이점

코루틴은 스레드에 비해 다음과 같은 성능적인 이점을 제공합니다.

* **낮은 메모리 사용량**: 스레드보다 훨씬 적은 메모리를 사용하여 수많은 코루틴을 생성할 수 있습니다.
* **빠른 컨텍스트 스위칭**: 코루틴 간의 전환은 힙에 저장된 상태 객체만 교체하면 되므로 매우 빠릅니다.
* **비동기 작업의 간결한 표현**: 복잡한 비동기 로직을 동기 코드처럼 표현할 수 있어 코드의 가독성과 유지 보수성이 향상됩니다.

#### 코루틴과 JVM 바이트코드

코루틴이 컴파일되면 JVM 바이트코드로 변환되며, 이는 일반적인 함수 호출과는 다른 형태를 가집니다. 코루틴의 일시 중단 지점은 `invokeSuspend` 메서드 내의 분기문으로 표현되며, 상태 관리를 위한 변수들이 추가됩니다.

**바이트코드 분석**

컴파일된 코루틴의 바이트코드를 보면 `L$0`, `L$1` 등의 변수가 등장합니다. 이는 코루틴의 지역 변수와 실행 상태를 저장하기 위한 필드입니다.

```kotlin
public final class SimpleCoroutine extends ContinuationImpl {
    Object L$0;
    int label;

    public SimpleCoroutine(Continuation<?> continuation) {
        super(continuation);
    }

    @Override
    public final Object invokeSuspend(Object result) {
        switch (label) {
            case 0:
                // 코루틴 시작 부분
                break;
            case 1:
                // 코루틴 재개 부분
                break;
            default:
                throw new IllegalStateException("잘못된 상태");
        }
    }
}
```

#### 코루틴의 한계와 고려 사항

* **디버깅의 복잡성**: 코루틴의 비동기 실행과 상태 기계로의 변환으로 인해 디버깅이 복잡할 수 있습니다.
* **메모리 누수 위험**: 코루틴의 생명 주기를 적절히 관리하지 않으면 메모리 누수가 발생할 수 있습니다.
* **스레드 전환 비용**: 코루틴이 다른 디스패처로 전환되면 스레드 스위칭이 발생하여 오버헤드가 증가할 수 있습니다.
