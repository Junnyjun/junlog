# Singleton

싱글톤 패턴은 애플리케이션 내에서 단 하나의 인스턴스만 생성되어 공유되어야 할 때 사용합니다. 대표적인 예로 로깅(Logger)이나 스프링빈이 있습니다.

***

### Default Singleton (Eager Initialization)

클래스가 로딩될 때 미리 인스턴스를 생성합니다.

* 장점: 구현이 간단하며 멀티스레드 환경에서도 안전합니다.
* 단점: 사용하지 않더라도 인스턴스가 생성되어 메모리 낭비가 발생할 수 있습니다.

{% tabs %}
{% tab title="JAVA" %}
```java
public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() { }

    public static EagerSingleton getInstance() {
        return instance;
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
object EagerSingleton {
    fun doSomething() {
        println("EagerSingleton 작업 수행")
    }
}
```
{% endtab %}
{% endtabs %}

***

### Instance Field 방식

클래스 로딩 시점에 인스턴스가 생성되므로 Default 방식과 동일하게 메모리 사용이 불필요할 수 있습니다.

{% tabs %}
{% tab title="JAVA" %}
```java
public class InstanceFieldSingleton {
    private static InstanceFieldSingleton instance = new InstanceFieldSingleton();

    private InstanceFieldSingleton() { }

    public static InstanceFieldSingleton getInstance() {
        return instance;
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
class InstanceFieldSingleton private constructor() {

    // 싱글톤 인스턴스는 클래스가 로딩될 때 바로 생성됨.
    companion object {
        private val instance = InstanceFieldSingleton()

        // 외부에서 인스턴스에 접근할 때 사용하는 메서드
        fun getInstance(): InstanceFieldSingleton = instance
    }

    // 싱글톤 객체의 기능을 정의하는 메서드 예제
    fun doSomething() {
        println("InstanceFieldSingleton 작업 수행")
    }
}

```
{% endtab %}
{% endtabs %}

***

### Lazy Loading

클래스가 로딩될 때 인스턴스를 생성하지 않고, 실제로 필요할 때 생성하여 메모리 낭비를 줄입니다.

* 장점: 실제 사용 시점에 인스턴스를 생성하므로 메모리 효율적입니다.
* 단점: 멀티스레드 환경에서 동기화(synchronized) 처리가 필요합니다.

{% tabs %}
{% tab title="JAVA" %}
```java
public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() { }

    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
class LazySingleton private constructor() {
    companion object {
        val instance: LazySingleton by lazy { LazySingleton() }
    }

    fun doSomething() {
        println("LazySingleton 작업 수행")
    }
}
```
{% endtab %}
{% endtabs %}

***

### Standard / Joshua 블록 방식

Joshua Bloch가 제안한 방식으로, Bill Pugh의 내부 정적 클래스 방식을 사용합니다.\
이 방법은 클래스 로딩 시점과 실제 사용 시점의 장점을 모두 취하며, 멀티스레드 환경에서 안전합니다.

{% tabs %}
{% tab title="JAVA" %}
```java
public class StandardSingleton {
    private StandardSingleton() { }

    private static class SingletonHelper {
        private static final StandardSingleton INSTANCE = new StandardSingleton();
    }

    public static StandardSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 코틀린 예제: 코틀린에서는 object로 간단하게 구현 가능
object StandardSingleton {
    fun doSomething() {
        println("StandardSingleton 작업 수행")
    }
}
```
{% endtab %}
{% endtabs %}

* **Default/Instance Field 방식**: 클래스 로딩 시점에 인스턴스가 생성되어 간단하지만, 사용하지 않을 경우에도 인스턴스가 생성됩니다.
* **Lazy Loading 방식**: 인스턴스가 실제로 필요할 때 생성하여 메모리를 효율적으로 사용하지만, 동기화 비용이 발생할 수 있습니다.
* **Standard/Joshua 방식**: Bill Pugh의 내부 클래스 방식을 활용해 스레드 안전하면서도 지연 초기화를 구현할 수 있습니다.
