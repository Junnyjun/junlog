# Flyweight

Flyweight 패턴은 수많은 객체를 생성해야 하는 상황에서 메모리 사용을 줄이고 성능을 개선하기 위한 디자인 패턴입니다.\
이 패턴의 핵심 아이디어는 동일한 상태를 가지는 객체를 여러 번 생성하는 대신, 이미 생성된 객체를 재사용(공유)하는 방식에 있습니다.

```
       +----------------+
       |   Flyweight    |  <-- 공통 인터페이스
       +----------------+
              ▲
              │
   +-----------------------+
   | ConcreteFlyweight     |  <-- 실제 객체 (공유 대상)
   +-----------------------+
              ▲
              │
   +-----------------------+
   | FlyweightFactory      |  <-- 객체 생성 및 공유 관리
   +-----------------------+
```

캐시(cache)나 풀(pool) 방식에서 이미 존재하는 id를 가진 객체가 있다면 새 객체를 생성하지 않고 해당 객체를 반환합니다. 만약 해당 객체가 없다면 새로 생성하여 저장한 후 반환하는 방식입니다.

기존의 id를 가진 값이 있으면 그값을 주고 없으면 새롭게 객체를 만들고 추가해서 주는(공유) 방식입니다.\
Java의 여러 [Cache](../../../jvm/clean-architecture/instance-cache.md) 에서 사용되고 있는 방식입니다.

***

### How do code

{% tabs %}
{% tab title="JAVA" %}
```java
public interface Flyweight {
    void operation();
}

// 구체적인 Flyweight 클래스
public class ConcreteFlyweight implements Flyweight {
    private final String id;

    public ConcreteFlyweight(String id) {
        this.id = id;
    }

    @Override
    public void operation() {
        System.out.println("Flyweight 객체 " + id + "의 작업 수행");
    }
}

public class FlyweightFactory {
    // 이미 생성된 객체들을 저장하는 캐시
    private static final Map<String, Flyweight> pool = new HashMap<>();

    // id를 기반으로 Flyweight 객체를 반환하는 메소드
    public static Flyweight getFlyweight(String id) {
        if (pool.containsKey(id)) {
            return pool.get(id);
        } else {
            Flyweight flyweight = new ConcreteFlyweight(id);
            pool.put(id, flyweight);
            return flyweight;
        }
    }
}

```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Flyweight 인터페이스 정의
interface Flyweight {
    fun operation()
}

// 구체적인 Flyweight 클래스 구현
class ConcreteFlyweight(private val id: String) : Flyweight {
    override fun operation() {
        println("Flyweight 객체 $id 의 작업 수행")
    }
}

// Flyweight 객체를 관리하는 팩토리 객체
object FlyweightFactory {
    // 이미 생성된 객체들을 저장하는 캐시
    private val pool = mutableMapOf<String, Flyweight>()

    // id를 기반으로 Flyweight 객체를 반환하는 함수
    fun getFlyweight(id: String): Flyweight {
        return pool.getOrPut(id) { ConcreteFlyweight(id) }
    }
}
```
{% endtab %}
{% endtabs %}

