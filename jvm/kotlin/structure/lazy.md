# lazy

### 코틀린 `lazy` 프로퍼티: 컴파일된 코드 분석

`lazy` 프로퍼티는 단순한 문법적 설탕이 아닙니다. 컴파일러는 이 코드를 런타임에 지연 초기화 로직을 수행하는 실제 바이트코드로 변환합니다. 이 과정을 이해하면 `lazy`의 동작 원리와 성능 특성을 더 깊이 파악할 수 있습니다.

***

#### 1. `lazy` 컴파일 과정의 핵심

코틀린 컴파일러는 `by lazy { ... }` 구문을 만나면, `lazy` 델리게이트를 위한 보조 필드(backing field)를 생성합니다. 이 필드는 `Lazy<T>` 타입의 객체를 저장하며, 초기화 람다식은 이 객체 내부에 캡처됩니다.

```
// 원본 코틀린 코드
class MyClass {
    val myLazyValue: String by lazy {
        "Hello, World!"
    }
}
```

위 코드는 JVM 바이트코드로 컴파일될 때, 다음과 유사한 자바 코드로 역컴파일되어 보일 수 있습니다. (실제 생성되는 바이트코드를 의사 코드로 표현한 것임)

```
// 컴파일된 자바 의사 코드
public final class MyClass {
   // myLazyValue$delegate 필드 생성. Lazy 객체를 저장
   private final Lazy myLazyValue$delegate;

   // myLazyValue 프로퍼티의 게터(getter)
   public final String getMyLazyValue() {
      // 델리게이트 객체의 value 속성을 통해 실제 값에 접근
      return (String)this.myLazyValue$delegate.getValue();
   }

   public MyClass() {
      // lazy 함수를 호출하여 Lazy 객체 초기화
      // LazyThreadSafetyMode.SYNCHRONIZED가 기본값으로 사용됨
      this.myLazyValue$delegate = LazyKt.lazy(null, new MyClass$$special$$inlined$myLazyValue$1());
   }
}
```

`MyClass`의 인스턴스가 생성될 때, `myLazyValue` 자체가 바로 초기화되는 것이 아니라, `Lazy` 객체를 생성하는 `lazy` 함수가 호출됩니다. 이 `Lazy` 객체는 초기화 로직을 담고 있는 람다(컴파일러가 생성한 `MyClass$$special$$inlined$myLazyValue$1` 클래스)에 대한 참조를 가집니다.

***

#### 2. `LazyThreadSafetyMode`에 따른 실제 동작 차이

`Lazy<T>` 구현체는 `LazyThreadSafetyMode`에 따라 내부 로직이 달라집니다.

*   SYNCHRONIZED (기본값):

    getValue()가 호출될 때, synchronized 블록이나 ReentrantLock 같은 \*\*잠금(lock)\*\*을 사용하여 람다식이 한 번만 실행되도록 보장합니다. 여러 스레드가 동시에 getValue()를 호출해도, 첫 번째 스레드만 람다식을 실행하고 나머지는 대기하게 됩니다. 초기화가 완료되면 \_value 필드에 결과가 저장되고, 이후에는 람다식 실행 없이 즉시 \_value를 반환합니다.
*   PUBLICATION:

    getValue()는 잠금을 사용하지 않습니다. 여러 스레드가 동시에 접근하면 모두 람다식을 실행할 수 있습니다. 그러나 람다식의 결과가 \_value 필드에 쓰여지는 시점에 원자적인 compare-and-swap (CAS) 연산을 사용합니다. 이 연산은 \_value가 아직 초기화되지 않았을 때만 값을 덮어씁니다. 따라서 여러 스레드가 동시에 초기화하더라도, 최종적으로는 하나의 값만 \_value에 저장됩니다. 이는 스레드 간 동기화 오버헤드를 줄여 성능을 향상시킵니다.
*   NONE:

    getValue()는 아무런 동기화 메커니즘을 사용하지 않습니다. 순수한 단일 스레드 환경을 가정하며, \_value 필드가 null인지 확인하고 null이면 람다식을 실행하여 값을 할당합니다. 이 모드는 가장 빠르지만, 스레드 안전성이 보장되지 않습니다.

***

#### 3. 왜 `lazy`는 `val`에만 사용될까?

`lazy`는 `val` (읽기 전용) 프로퍼티에만 사용될 수 있습니다. 그 이유는 `lazy`의 본질이 단 한 번의 초기화를 보장하기 때문입니다. `lazy`는 값을 캐시하고, 이후의 모든 접근에 대해 동일한 캐시된 값을 반환합니다. 만약 `var`에 `lazy`가 허용된다면, `myLazyValue = "newValue"`와 같이 값을 재할당하는 것은 이 "한 번의 초기화"라는 `lazy`의 핵심 개념과 충돌하게 됩니다. `val`에 대한 `lazy`는 게으른 싱글톤 패턴의 프로퍼티 버전과 유사하다고 볼 수 있습니다.

`lazy`는 단순한 문법적 편의를 넘어, 런타임에 지연 초기화 로직을 수행하는 복잡한 객체와 바이트코드로 변환됩니다. 특히 `LazyThreadSafetyMode`를 통해 개발자는 스레드 환경에 따라 최적의 성능 전략을 선택할 수 있습니다. `lazy`는 단순히 코드를 줄이는 도구가 아니라, 성능과 안전성을 동시에 고려하는 시니어 개발자의 필수적인 도구입니다.
