# 일반적인 프로퍼티 패턴은 프로퍼티 위임으로 만들어라

코틀린은 프로퍼티 위임이라는 새로운 기능을 제공

* 프로퍼티 위임을 사용하면 일반적인 프로퍼티의 행위를 추출해서 재사용할 수 있다.
* 대표적으로 지연 프로퍼티인 lazy 프로퍼티가 존재
  * 처음 사용하는 요청이 들어올 때 초기화 되는 프로퍼티
  * val value by lazy { createValue() }
* 변화가 있을 때 이를 감지하는 observable 패턴, stdlib의 observable 델리게이트 기반으로 구현 가능

```
var items: List<Item> by
	Delegates.observable(listOf()) { _, _, _ -> 
    	notifyDataSetChanged()
    }
    
var key: String? by
	Delegates.observable(null) { _, old, new ->
    	Log.e("Key changed from new $old to $new")
    }
```

프로퍼티 위임 메커니즘을 활용하면, 다양한 패턴들을 간단하고 type-safe하게 만들 수 있음

프로퍼티 위임을 통한 getter, setter 로깅

* 프로퍼티 위임은 다른 객체의 메소드를 활용해서 프로퍼티의 접근자(게터와 세터)를 만드는 방식
* 이때 다른 객체의 메소드 이름이 중요함
* 게터는 getValue, 세터는 setValue 함수를 사용해서 만들어야 한다.
* 객체를 만든 뒤에는 by 키워드를 사용해서, getVlaue와 setValue를 정의한 클래스와 연결해 주면 된다.

```
class LoggingProperty<T>(var value: T) {
    operator fun getValue(
            thisRef: Any?,
            prop: KProperty<*>
    ): T {
        println("${prop.name} returned vale $value")
        return value
    }

    operator fun setValue(
            thisRef: Any?,
            prop: KProperty<*>,
            newValue: T
    ) {
        val name = prop.name
        println("$name changed from $value to $newValue")
        value = newValue
    }

}

fun main() {
    var tokenaaa: String? by LoggingProperty(null)
    var attempts: Int by LoggingProperty(0)

    tokenaaa
    tokenaaa = "a"

    attempts
    attempts = 1
}
```

프로퍼티 위임이 어떻게 동작하는지 이해하려면, by가 어떻게 컴파일되는지 보는 것이 좋다. 위와 같은 코드는 아래와 비슷하게 컴파일된다.

```
@JvmField
private val 'token$delegate' =
        LoggingProperty<String?>(null)
var token: String?
    get() = 'token$delegate'.getValue(this, ::token)
    set(value) {
        'token$delegate'.setValue(this, ::token, value)
    }
```

* 컨텍스트(this)와 프로퍼티 레퍼런스의 경계도 함께 사용하는 형태로 변경
* 컨텍스트를 활용하기 때문에 getValue와 setValue가 여러 개 있어도 문제가 없다. 상황에 따라서 적절한 메소드가 선택됨
* 위임 프로퍼티는 확장 함수로도 만들 수 있다.

코틀린 stdlib에서 알아 두면 좋은 프로퍼티 델리게이터

* lazy -> 지연 초기화
* Delegates.observable -> 프로퍼티의 데이터가 변할 때마다 callback을 받을 수 있다.
* Delegates.vetoable -> observable과 거의 유사하지만 반환 값이 있다
* Delgates.notNull -> 프로퍼티를 non-null 타입으로 변경
