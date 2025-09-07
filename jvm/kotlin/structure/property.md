# property

### 코틀린 프로퍼티 위임: `by` 키워드로 코드 재사용성 높이기

코틀린의 \*\*프로퍼티 위임(Delegated Properties)\*\*은 객체의 특정 동작을 다른 객체에 위임하는 강력한 기능입니다. 이 기능을 사용하면 반복적인 코드를 줄이고, 로직을 분리하여 코드 재사용성과 가독성을 크게 향상시킬 수 있습니다. 이번 포스트에서는 프로퍼티 위임이 무엇인지, 그리고 어떻게 활용하는지 자세히 알아보겠습니다.

***

#### 프로퍼티 위임이란?

프로퍼티 위임은 `by` 키워드를 사용하여 프로퍼티의 `get()`과 `set()` 접근자 로직을 별도의 객체(델리게이트)에 맡기는 것을 말합니다. 즉, 개발자는 프로퍼티의 값을 직접 관리하는 대신, `get`과 `set`의 동작을 정의하는 델리게이트 객체를 지정하기만 하면 됩니다.

기본 문법은 다음과 같습니다.

```
var <property name>: <Type> by <expression>
```

여기서 `<expression>`은 \*\*`getValue`\*\*와 `setValue` (var의 경우) 메서드를 구현하는 객체입니다. 코틀린은 이 델리게이트 객체의 메서드를 자동으로 호출하여 프로퍼티의 값을 읽고 씁니다.

***

#### 프로퍼티 위임의 핵심: `ReadOnlyProperty`와 `ReadWriteProperty` 인터페이스

프로퍼티 위임에 사용되는 델리게이트 객체는 특정 인터페이스를 구현해야 합니다.

*   `ReadOnlyProperty`: `val` (읽기 전용) 프로퍼티에 사용됩니다. 이 인터페이스는 `getValue` 메서드만 정의합니다.

    ```
    interface ReadOnlyProperty<in T, out V> {
        operator fun getValue(thisRef: T, property: KProperty<*>): V
    }
    ```
*   `ReadWriteProperty`: `var` (읽기/쓰기 가능) 프로퍼티에 사용됩니다. 이 인터페이스는 \*\*`getValue`\*\*와 `setValue` 메서드를 모두 정의합니다.

    ```
    interface ReadWriteProperty<in T, out V> {
        operator fun getValue(thisRef: T, property: KProperty<*>): V
        operator fun setValue(thisRef: T, property: KProperty<*>, value: V)
    }
    ```

코틀린 컴파일러는 위임된 프로퍼티를 만났을 때, 내부적으로 이 메서드들을 호출하여 실제 로직을 실행합니다.

***

#### 코틀린 표준 라이브러리의 유용한 위임들

코틀린은 자주 사용되는 위임 패턴을 표준 라이브러리에서 제공합니다.

**1. `lazy` 위임: 지연 초기화**

\*\*`lazy`\*\*는 프로퍼티의 값이 처음 사용될 때 초기화되는 지연 초기화 기능을 제공합니다. 이는 리소스 소모가 큰 객체를 필요할 때만 생성하고 싶을 때 유용합니다.

```
val expensiveProperty: String by lazy {
    println("값 초기화 중...")
    "초기화된 값"
}

fun main() {
    println(expensiveProperty) // "값 초기화 중..." 출력 후 "초기화된 값" 출력
    println(expensiveProperty) // 이미 초기화되어 있으므로, 값만 출력
}
```

`lazy`는 기본적으로 스레드 안전하게 동작하며, `LazyThreadSafetyMode`를 통해 동작 방식을 변경할 수 있습니다.

**2. `Delegates.observable`와 `Delegates.vetoable`**

이 위임들은 프로퍼티의 값이 변경될 때 특정 로직을 실행하는 옵저버 패턴을 구현하는 데 사용됩니다.

*   `Delegates.observable`: 값이 변경된 후에 콜백을 호출합니다.

    ```
    import kotlin.properties.Delegates

    var name: String by Delegates.observable("초기값") {
        prop, old, new ->
        println("이름이 '$old'에서 '$new'로 변경되었습니다.")
    }

    fun main() {
        name = "코틀린"
        // 출력: 이름이 '초기값'에서 '코틀린'로 변경되었습니다.
    }
    ```
*   `Delegates.vetoable`: 값이 변경되기 전에 콜백을 호출하며, 변경을 취소할 수 있습니다. 콜백이 `false`를 반환하면 값 변경이 취소됩니다.

    ```
    import kotlin.properties.Delegates

    var isEnabled: Boolean by Delegates.vetoable(false) {
        prop, old, new ->
        println("상태 변경 시도: $old -> $new")
        new // true를 반환해야만 값이 변경됨
    }
    ```

**3. `Delegates.notNull`**

초기화가 나중에 이루어지지만 `null`이 될 수 없는 `var` 프로퍼티에 사용됩니다. 만약 초기화 전에 접근하면 `IllegalStateException`을 발생시켜 안전성을 높여줍니다.

***

#### 커스텀 프로퍼티 위임 만들기

표준 라이브러리 위임 외에도, 직접 `getValue`와 `setValue`를 구현하여 커스텀 델리게이트를 만들 수 있습니다.

예시: 로컬 저장소에 값을 저장하는 위임

```
import kotlin.reflect.KProperty

class LocalStorageDelegate(private val key: String, private val defaultValue: String) {
    // SharedPreferences 등을 사용한다고 가정
    private val storage = mutableMapOf<String, String>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        println("'$key'에서 값 읽기")
        return storage[key] ?: defaultValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("'$key'에 '$value' 쓰기")
        storage[key] = value
    }
}

class UserSettings {
    var username: String by LocalStorageDelegate("username", "Guest")
}

fun main() {
    val settings = UserSettings()
    println(settings.username) // 출력: 'username'에서 값 읽기, Guest
    settings.username = "Alice" // 출력: 'username'에 'Alice' 쓰기
    println(settings.username) // 출력: 'username'에서 값 읽기, Alice
}
```

이처럼 프로퍼티 위임을 사용하면 로깅, 데이터 저장, 스레드 관리 등 다양한 로직을 한 곳에 모아 재사용할 수 있습니다.
