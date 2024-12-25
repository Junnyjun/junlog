# Lazy

코틀린의 `lazy`는 지연 초기화를 제공하는 중요한 기능 중 하나로, 선언된 프로퍼티가 실제로 사용될 때 초기화되도록 설계되었습니다. 본 글에서는 `lazy`가 컴파일 시점에 어떻게 동작하며, 컴파일 후 어떤 코드로 변환되는지를 자세히 알아보겠습니다.

***

### 1. lazy의 동작 개요

`lazy`는 코틀린 표준 라이브러리에서 제공하는 함수로, `Lazy<T>` 인터페이스를 활용합니다. 이 인터페이스는 `getValue()` 메서드를 통해 초기화된 값을 반환하며, 초기화되지 않았다면 람다를 실행해 값을 생성합니다.

```
val lazyValue: String by lazy {
    println("Initializing")
    "Hello, Kotlin!"
}
```

위 코드는 실제로 사용될 때 초기화가 발생하며, 이후에는 캐시된 값을 반환합니다.

***

### 2. 컴파일 전 코틀린 코드

컴파일 전의 `lazy` 코드는 매우 간단합니다. 아래와 같이 작성했다고 가정합니다.

```
val lazyValue: String by lazy {
    println("Initializing")
    "Hello, Kotlin!"
}

fun main() {
    println(lazyValue)  // 첫 호출: 초기화 발생
    println(lazyValue)  // 두 번째 호출: 캐시된 값 반환
}
```

***

### 3. 컴파일 후 디컴파일된 Java 코드

위 코드를 컴파일한 후 디컴파일하면 다음과 같은 Java 코드로 변환됩니다.

```
public final class MainKt {
    private static final Lazy<String> lazyValue = LazyKt.lazy(() -> {
        System.out.println("Initializing");
        return "Hello, Kotlin!";
    });

    public static final void main() {
        System.out.println(lazyValue.getValue()); // 첫 호출
        System.out.println(lazyValue.getValue()); // 두 번째 호출
    }
}
```

#### 분석

1. `lazy` 프로퍼티는 `Lazy<T>` 인터페이스를 구현한 객체로 변환됩니다.
2. 초기화 블록은 람다로 감싸져 `Lazy` 객체의 생성자에 전달됩니다.
3. `getValue()` 메서드가 호출되면 람다가 실행되어 값을 초기화하거나, 이미 초기화된 값을 반환합니다.

***

### 4. `Lazy` 구현체

코틀린의 표준 라이브러리는 세 가지 `Lazy` 구현체를 제공합니다.

#### 4.1 기본 구현체: `synchronizedLazyImpl`

`lazy`는 기본적으로 동기화를 제공합니다. 멀티스레드 환경에서 안전하게 동작하기 위해 `synchronizedLazyImpl`을 사용합니다.

```
public final class SynchronizedLazyImpl<T> implements Lazy<T> {
    private volatile Object _value = UNINITIALIZED_VALUE;
    private final Function0<T> initializer;

    public SynchronizedLazyImpl(Function0<T> initializer) {
        this.initializer = initializer;
    }

    public T getValue() {
        Object result = _value;
        if (result == UNINITIALIZED_VALUE) {
            synchronized (this) {
                result = _value;
                if (result == UNINITIALIZED_VALUE) {
                    result = initializer.invoke();
                    _value = result;
                }
            }
        }
        return (T) result;
    }
}
```

#### 4.2 다른 구현체: `UnsafeLazyImpl`, `SafePublicationLazyImpl`

* **`UnsafeLazyImpl`**: 동기화를 사용하지 않아 성능이 좋지만, 멀티스레드 환경에서는 안전하지 않습니다.
* **`SafePublicationLazyImpl`**: 초기화가 한 번 이상 발생할 수 있으나, 최종적으로 초기화된 값은 동일합니다.

`LazyThreadSafetyMode`를 통해 구현체를 선택할 수 있습니다.

```
val unsafeLazyValue: String by lazy(LazyThreadSafetyMode.NONE) {
    println("Initializing")
    "Hello, Unsafe!"
}
```

***

### 5. lazy 프로퍼티의 전체 동작 흐름

1. **컴파일 시점**
   * `by lazy`는 내부적으로 `Lazy` 구현체를 생성하고, 초기화 람다를 전달합니다.
   * 프로퍼티 접근은 `getValue()` 메서드 호출로 대체됩니다.
2. **실행 시점**
   * 첫 번째 호출 시 `getValue()`가 실행되어 람다가 호출됩니다.
   * 초기화된 값이 `_value`에 저장됩니다.
   * 이후 호출은 `_value`에 저장된 값을 반환합니다.
