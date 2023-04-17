# Functional Interface

Java의 `@Funtional Interface` 처럼  Kotlin에서도 하나의 추상 메소드만 가질 수 있게 강제할 수있습니다.

```kotlin
fun interface SampleInterface {
    fun index(value:Int): String
}
```

default 메서드는 제외됩니다.

```kotlin
fun interface SampleInterface {
    fun index(value:Int): String
    fun index(value:String): String = "hello $value"
}
```



### 사용

SAM을 사용하는 방법과 아닌방법이 존재합니다\
※ SAM : 람다식을 이용한 변환

{% code title="SAM X" %}
```kotlin
fun sample(value: Int) : String = object : SampleInterface {
    override fun index(value: Int): String {
        return "hello $value"
    }
}.index(value)
```
{% endcode %}

{% code title="SAM O" %}
```kotlin
fun sample(value: Int) : String = SampleInterface { "hello $it" }.index(value)
```
{% endcode %}

