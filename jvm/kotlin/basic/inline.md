# Inline

`inline` 키워드는 코틀린에서 고차 함수의 성능을 최적화하는 데 사용됩니다. 이는 함수 호출 오버헤드를 줄이고, 람다 표현식에 의한 메모리 할당을 방지하기 위해 함수 코드를 호출 위치에 직접 삽입합니다.&#x20;

***

### inline 키워드의 동작

`inline` 키워드는 함수가 호출될 때, 함수 호출을 제거하고 그 내용을 호출 위치에 삽입합니다. 이를 통해 호출 오버헤드와 람다 캡처에 따른 메모리 할당 비용을 줄일 수 있습니다.

#### 사용 예시

```
inline fun execute(action: () -> Unit) {
    println("Start")
    action()
    println("End")
}

fun main() {
    execute {
        println("Running action")
    }
}
```

***

### 컴파일 전 코틀린 코드

위 코드는 고차 함수 `execute`와 람다 표현식을 사용하고 있습니다. 이는 일반적으로 람다가 익명 클래스로 변환되어 객체를 생성하는 방식으로 동작합니다.

```
inline fun execute(action: () -> Unit) {
    println("Start")
    action()
    println("End")
}
```

`main` 함수에서 고차 함수를 호출합니다:

```
fun main() {
    execute {
        println("Running action")
    }
}
```

***

### 컴파일 후 디컴파일된 Java 코드

`inline` 키워드로 인해 함수 호출이 제거되고 코드가 호출 위치에 삽입됩니다. 디컴파일된 코드는 다음과 같이 나타납니다:

```
public final class MainKt {
    public static final void main() {
        System.out.println("Start");
        System.out.println("Running action");
        System.out.println("End");
    }
}
```

#### 디컴파일 결과 분석

`execute` 함수가 호출되지 않고, 함수 본문이 `main` 함수에 직접 삽입되었습니다. 또한 람다 표현식이 익명 클래스나 객체로 변환되지 않아 추가적인 메모리 할당이 발생하지 않습니다.

***

### inline의 이점

#### 호출 오버헤드 제거

일반 함수 호출은 스택 프레임을 생성하고, 호출 후 반환하는 과정에서 오버헤드가 발생합니다. `inline`은 이를 제거하여 성능을 최적화합니다.

#### 람다 캡처 비용 감소

람다 표현식이 외부 변수를 캡처하면 익명 객체를 생성하여 메모리를 사용하게 됩니다. `inline` 키워드를 사용하면 이러한 객체 생성 비용을 피할 수 있습니다.

***

### inline 사용 시 주의사항

#### 코드 크기 증가

`inline` 키워드를 남용하면 함수 본문이 여러 곳에 삽입되어 코드 크기가 증가할 수 있습니다. 이는 성능 저하를 초래할 수 있습니다.

#### noinline 키워드

특정 람다를 인라인화하지 않으려면 `noinline` 키워드를 사용할 수 있습니다.

```
inline fun process(action: () -> Unit, noinline anotherAction: () -> Unit) {
    action()
    anotherAction()
}
```

`anotherAction`은 인라인화되지 않고, 별도의 객체로 유지됩니다.

#### crossinline 키워드

람다에서 비지역 반환이 필요한 경우 `crossinline` 키워드를 사용해야 합니다.

```
inline fun runTask(crossinline action: () -> Unit) {
    Thread {
        action()
    }.start()
}
```

`crossinline`은 비지역 반환을 방지하여 런타임 오류를 방지합니다.

***

### inline의 동작 흐름

#### 컴파일 시점

함수 본문이 호출 위치에 삽입되고, 람다 캡처에 의한 익명 객체 생성이 제거됩니다.

#### 실행 시점

호출 오버헤드와 메모리 할당이 줄어들고, 삽입된 코드가 직접 실행됩니다.

`inline` 키워드는 고차 함수 호출과 람다 표현식 사용에서 성능을 최적화하는 강력한 도구입니다. 그러나 코드 크기 증가와 같은 부작용을 고려하여 필요한 경우에만 사용하는 것이 중요합니다. 본 글에서는 `inline`의 컴파일 전후 동작과 이점을 분석하며, 최적화 도구로서의 가능성을 탐구했습니다.
