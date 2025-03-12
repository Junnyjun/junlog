---
description: What is❔
---

# Abstract Factory

Abstract Factory 패턴은 구체적인 클래스에 의존하지 않고, 여러 제품(객체)들의 조합을 생성하는 인터페이스를 제공합니다.\
즉, 클라이언트는 구체적인 클래스의 구현 세부 사항에 대해 알 필요 없이, 팩토리 인터페이스를 통해 일관된 방식으로 객체들을 생성할 수 있습니다.

```
         +---------------------+
         |  AbstractFactory    |  <-- 제품들의 생성 인터페이스
         +---------------------+
         | + createProductA()  |
         | + createProductB()  |
         +---------------------+
                  /   \
                 /     \
                /       \
+--------------------+   +--------------------+
| ConcreteFactory1   |   | ConcreteFactory2   |  <-- 구체적인 팩토리들
+--------------------+   +--------------------+
| + createProductA() |   | + createProductA() |  
|   -> ConcreteA1    |   |   -> ConcreteA2    |
| + createProductB() |   | + createProductB() |  
|   -> ConcreteB1    |   |   -> ConcreteB2    |
+--------------------+   +--------------------+
         |                        |
         |                        |
         v                        v
+----------------+        +----------------+
|  ConcreteA1    |        |  ConcreteA2    |  <-- 추상 제품 A 구현체들
+----------------+        +----------------+
         |                        |
         |                        |
         v                        v
+----------------+        +----------------+
|  ConcreteB1    |        |  ConcreteB2    |  <-- 추상 제품 B 구현체들
+----------------+        +----------------+
```

예를 들어, 서로 관련된 제품군(ProductA, ProductB 등)을 생성해야 하는 상황에서, AbstractFactory 인터페이스를 통해 제품들의 조합을 생성할 수 있습니다.

***

### How do Code❔

{% tabs %}
{% tab title="JAVA" %}
```java
// 추상 제품 A
public interface ProductA {
    void performA();
}

// 추상 제품 B
public interface ProductB {
    void performB();
}

// 구체적인 제품 A1
public class ConcreteProductA1 implements ProductA {
    @Override
    public void performA() {
        System.out.println("ConcreteProductA1 작업 수행");
    }
}

// 구체적인 제품 A2
public class ConcreteProductA2 implements ProductA {
    @Override
    public void performA() {
        System.out.println("ConcreteProductA2 작업 수행");
    }
}

// 구체적인 제품 B1
public class ConcreteProductB1 implements ProductB {
    @Override
    public void performB() {
        System.out.println("ConcreteProductB1 작업 수행");
    }
}

// 구체적인 제품 B2
public class ConcreteProductB2 implements ProductB {
    @Override
    public void performB() {
        System.out.println("ConcreteProductB2 작업 수행");
    }
}

// Abstract Factory 인터페이스: 제품들의 조합을 생성하는 메서드를 정의
public interface AbstractFactory {
    ProductA createProductA();
    ProductB createProductB();
}

// 구체적인 팩토리 1: 제품 A1과 B1을 생성
public class ConcreteFactory1 implements AbstractFactory {
    @Override
    public ProductA createProductA() {
        return new ConcreteProductA1();
    }

    @Override
    public ProductB createProductB() {
        return new ConcreteProductB1();
    }
}

// 구체적인 팩토리 2: 제품 A2와 B2를 생성
public class ConcreteFactory2 implements AbstractFactory {
    @Override
    public ProductA createProductA() {
        return new ConcreteProductA2();
    }

    @Override
    public ProductB createProductB() {
        return new ConcreteProductB2();
    }
}

// 클라이언트 예제
public class AbstractFactoryDemo {
    public static void main(String[] args) {
        // 원하는 팩토리를 선택하여 사용
        AbstractFactory factory = new ConcreteFactory1();

        ProductA productA = factory.createProductA();
        ProductB productB = factory.createProductB();

        productA.performA();
        productB.performB();
    }
}

```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 추상 제품 A
interface ProductA {
    fun performA()
}

// 추상 제품 B
interface ProductB {
    fun performB()
}

// 구체적인 제품 A1
class ConcreteProductA1 : ProductA {
    override fun performA() {
        println("ConcreteProductA1 작업 수행")
    }
}

// 구체적인 제품 A2
class ConcreteProductA2 : ProductA {
    override fun performA() {
        println("ConcreteProductA2 작업 수행")
    }
}

// 구체적인 제품 B1
class ConcreteProductB1 : ProductB {
    override fun performB() {
        println("ConcreteProductB1 작업 수행")
    }
}

// 구체적인 제품 B2
class ConcreteProductB2 : ProductB {
    override fun performB() {
        println("ConcreteProductB2 작업 수행")
    }
}

// Abstract Factory 인터페이스
interface AbstractFactory {
    fun createProductA(): ProductA
    fun createProductB(): ProductB
}

// 구체적인 팩토리 1: 제품 A1과 B1 생성
class ConcreteFactory1 : AbstractFactory {
    override fun createProductA(): ProductA = ConcreteProductA1()
    override fun createProductB(): ProductB = ConcreteProductB1()
}

// 구체적인 팩토리 2: 제품 A2과 B2 생성
class ConcreteFactory2 : AbstractFactory {
    override fun createProductA(): ProductA = ConcreteProductA2()
    override fun createProductB(): ProductB = ConcreteProductB2()
}

// 클라이언트 예제
fun main() {
    // 원하는 팩토리를 선택하여 사용
    val factory: AbstractFactory = ConcreteFactory1()

    val productA = factory.createProductA()
    val productB = factory.createProductB()

    productA.performA()
    productB.performB()
}

```
{% endtab %}
{% endtabs %}

* **목적**: 구체적인 클래스에 의존하지 않고, 관련된 객체들의 조합을 생성하는 인터페이스를 제공하여 제품군의 일관된 생성을 보장
* **장점**: 클라이언트는 구체적인 구현을 몰라도 제품들을 생성할 수 있어, 코드의 확장성과 유지보수성이 향상됨
* **사용 예**: GUI 툴킷, 데이터베이스 연결, 플랫폼별 제품군 등
