# Contract

코틀린의 \*\*컨트랙트(Contracts)\*\*는 컴파일러에게 함수가 어떻게 동작하는지에 대한 추가 정보를 제공하여, 코드를 더 안전하고 스마트하게 만들어주는 실험적인(Experimental) 기능입니다. 이를 통해 컴파일러는 코드의 흐름을 더 정확하게 분석할 수 있고, 불필요한 스마트 캐스트나 널 체크를 줄일 수 있습니다.

***

#### 1. 컨트랙트가 왜 필요한가?

코틀린은 강력한 타입 추론과 널 안정성(null safety)을 제공하지만, 때때로 컴파일러가 함수의 부작용(side-effects)이나 동작 방식을 완전히 이해하지 못하는 경우가 있습니다. 예를 들어, 특정 함수가 `false`를 반환할 때 인자가 `null`이 아니라는 것을 보장하고 싶을 수 있습니다.

Kotlin

```
fun isNull(value: String?): Boolean {
    return value == null
}

fun main() {
    val name: String? = "John"
    if (!isNull(name)) {
        // 컴파일러는 isNull 함수의 내부를 모르므로
        // 여전히 name이 null일 수 있다고 판단함
        // name.length // ❌ 안전한 호출이 아님
    }
}
```

위 코드에서 `if (!isNull(name))` 블록 내부에서 `name`은 여전히 널 가능(`String?`) 타입으로 남아있습니다. `isNull` 함수가 `true`를 반환할 때만 `name`이 `null`이라는 사실을 컴파일러가 알지 못하기 때문입니다. 이러한 경우를 해결하기 위해 컨트랙트가 사용됩니다.

***

#### 2. 컨트랙트의 핵심: `contract { ... }`

컨트랙트는 `contract { ... }` 블록 내에서 `kotlin.contracts` 패키지의 함수들을 사용하여 정의합니다. 가장 일반적인 컨트랙트는 함수의 반환 값과 인자 사이의 관계를 나타내는 \*\*`returns`\*\*와 \*\*`returns`와 함께 사용하는 `implies`\*\*입니다.

**`returns`와 `implies`**

`returns`는 함수의 반환 값에 대한 조건을 나타내고, `implies`는 그 조건이 참일 때 특정 인자에 어떤 조건이 성립하는지 명시합니다.

Kotlin

```
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.contracts.Returns
import kotlin.contracts.isNotNull

@OptIn(ExperimentalContracts::class)
fun isNull(value: String?): Boolean {
    contract {
        // returns(true)일 때, value는 null이라는 것을 보장
        returns(true) implies (value == null)
        // returns(false)일 때, value는 null이 아니라는 것을 보장
        returns(false) implies (value != null)
    }
    return value == null
}

fun main() {
    val name: String? = "John"

    if (!isNull(name)) {
        // 이제 컴파일러는 !isNull(name)이 true일 때,
        // name이 non-null임을 보장받으므로 스마트 캐스트가 가능
        println(name.length) // ✅ 안전한 호출!
    }
}
```

위 코드에서 `returns(false) implies (value != null)` 컨트랙트는 컴파일러에게 "이 함수가 `false`를 반환하면, `value`는 반드시 `null`이 아님"이라는 명확한 정보를 제공합니다. 덕분에 `if` 블록 내에서 `name`이 `String` 타입으로 스마트 캐스트됩니다.

***

#### 3. 컨트랙트의 종류

컨트랙트는 반환 값과 인자 간의 관계 외에도 여러 종류가 있습니다.

*   `CallsInPlace`: 람다 인자가 몇 번 호출되는지 명시합니다. 예를 들어, `run`, `with` 같은 표준 라이브러리 함수들은 람다가 정확히 한 번 호출된다는 컨트랙트를 가집니다. 이를 통해 `val` 변수를 `run` 블록 내에서 안전하게 초기화할 수 있습니다.

    Kotlin

    ```
    val name: String
    run {
        name = "Kotlin"
    } // `run`은 `CallsInPlace` 컨트랙트로 컴파일러에게 name이 한 번 초기화됨을 알림
    ```
* `Effects`: 함수가 어떤 부작용을 일으키는지 정의합니다. 아직 구체적인 구현은 제한적이지만, 미래에는 더 많은 동작을 명시할 수 있게 될 것입니다.

***



* 실험적 기능: 컨트랙트는 현재 실험적(Experimental) 기능입니다. `@OptIn(ExperimentalContracts::class)` 어노테이션을 사용해야만 쓸 수 있습니다.
* 컴파일러 검증: 컨트랙트는 컴파일러에게 정보를 주는 것일 뿐, 런타임에 컨트랙트가 지켜지는지 검증하지 않습니다. 컨트랙트와 실제 함수 동작이 다르면 컴파일러가 잘못된 정보를 기반으로 코드를 분석할 수 있으므로, 컨트랙트는 실제 함수의 동작과 일치해야 합니다
