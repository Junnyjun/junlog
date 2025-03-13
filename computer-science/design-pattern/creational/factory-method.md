---
description: 객체 생성 처리를 서브 클래스로 분리
---

# Factory Method

Factory Method 패턴은 객체 생성의 책임을 하위 클래스에게 위임하여, 클라이언트가 구체적인 제품 클래스에 의존하지 않도록 만드는 디자인 패턴입니다.&#x20;

즉, Creator(생성자) 클래스는 팩토리 메서드(factoryMethod())를 선언하고, 이를 상속받은 ConcreteCreator들이 실제로 구체적인 제품(ConcreteProduct)을 생성합니다.&#x20;

이렇게 하면 새로운 제품을 추가하거나 변경할 때 클라이언트 코드를 수정할 필요 없이 하위 클래스만 수정하면 되므로, 확장성과 유연성이 크게 향상됩니다.

```
           +------------------------+
           |      Creator           |  <-- 추상 생성자 클래스
           +------------------------+
           | + factoryMethod():     |
           |   Product              |  <-- 추상 팩토리 메서드
           +------------------------+
                     │
         ┌───────────┴───────────┐
         │                       │
+----------------------+  +----------------------+
| ConcreteCreator1     |  | ConcreteCreator2     |  <-- 구체적인 생성자들
+----------------------+  +----------------------+
| + factoryMethod():   |  | + factoryMethod():   |
|   ConcreteProduct1   |  |   ConcreteProduct2   |  <-- 각자 제품 생성
+----------------------+  +----------------------+
                     │
                     ▼
               +--------------+
               |   Product    |  <-- 공통 제품 인터페이스 또는 추상 클래스
               +--------------+
                     │
         ┌───────────┴───────────┐
         │                       │
+---------------------+   +---------------------+
| ConcreteProduct1    |   | ConcreteProduct2    |  <-- 구체적인 제품들
+---------------------+   +---------------------+

```

### How do Code

{% tabs %}
{% tab title="JAVA" %}
```java
// 제품 인터페이스
public interface Product {
    void use();
}

// ConcreteProduct1: Product 인터페이스 구현
public class ConcreteProduct1 implements Product {
    @Override
    public void use() {
        System.out.println("ConcreteProduct1 사용");
    }
}

// ConcreteProduct2: Product 인터페이스 구현
public class ConcreteProduct2 implements Product {
    @Override
    public void use() {
        System.out.println("ConcreteProduct2 사용");
    }
}

// Creator 추상 클래스
public abstract class Creator {
    // 팩토리 메서드: 하위 클래스에서 구체적인 제품을 생성하도록 위임
    public abstract Product factoryMethod();

    // 생성된 제품을 활용하는 공통 로직
    public void doSomething() {
        Product product = factoryMethod();
        product.use();
    }
}

// ConcreteCreator1: ConcreteProduct1을 생성
public class ConcreteCreator1 extends Creator {
    @Override
    public Product factoryMethod() {
        return new ConcreteProduct1();
    }
}

// ConcreteCreator2: ConcreteProduct2를 생성
public class ConcreteCreator2 extends Creator {
    @Override
    public Product factoryMethod() {
        return new ConcreteProduct2();
    }
}

// 클라이언트 코드
public class FactoryMethodDemo {
    public static void main(String[] args) {
        Creator creator1 = new ConcreteCreator1();
        Creator creator2 = new ConcreteCreator2();

        creator1.doSomething();
        creator2.doSomething();
    }
}

```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 제품 인터페이스
interface Product {
    fun use()
}

// ConcreteProduct1: Product 인터페이스 구현
class ConcreteProduct1 : Product {
    override fun use() {
        println("ConcreteProduct1 사용")
    }
}

// ConcreteProduct2: Product 인터페이스 구현
class ConcreteProduct2 : Product {
    override fun use() {
        println("ConcreteProduct2 사용")
    }
}

// Creator 추상 클래스
abstract class Creator {
    // 추상 팩토리 메서드: 하위 클래스에서 구체적인 제품을 생성하도록 위임
    abstract fun factoryMethod(): Product

    // 생성된 제품을 사용하는 공통 메서드
    fun doSomething() {
        val product = factoryMethod()
        product.use()
    }
}

// ConcreteCreator1: ConcreteProduct1을 생성
class ConcreteCreator1 : Creator() {
    override fun factoryMethod(): Product = ConcreteProduct1()
}

// ConcreteCreator2: ConcreteProduct2를 생성
class ConcreteCreator2 : Creator() {
    override fun factoryMethod(): Product = ConcreteProduct2()
}

// 클라이언트 코드
fun main() {
    val creator1: Creator = ConcreteCreator1()
    val creator2: Creator = ConcreteCreator2()

    creator1.doSomething()
    creator2.doSomething()
}

```
{% endtab %}
{% endtabs %}

* **목적**: 구체적인 제품 객체 생성의 책임을 Creator 하위 클래스에 위임하여, 클라이언트가 구체 클래스에 의존하지 않도록 함
* **구조**: Creator(추상 생성자)는 팩토리 메서드를 선언하고, ConcreteCreator가 이 메서드를 구현하여 구체적인 제품을 생성
* **장점**: 새로운 제품 추가 시 클라이언트 코드를 수정하지 않아도 되고, 확장과 유지보수가 용이함
