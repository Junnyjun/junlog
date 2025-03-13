---
description: 조합된 기능이 많은 경우
---

# Decorator

Decorator 패턴은 객체의 결합을 통해 기능을 유연하게 확장할 수 있도록 도와주는 디자인 패턴입니다. 즉, 기존 객체에 새로운 기능을 동적으로 추가할 때 상속 대신 객체 조합(composition)을 활용하여 확장성을 높입니다.&#x20;

```
          +----------------+
          |   Component    |  <-- 기본 인터페이스
          +----------------+
                   ▲
                   │
          +----------------+
          | ConcreteComponent |  <-- 기본 기능을 구현한 클래스
          +----------------+
                   ▲
                   │
          +----------------+
          |   Decorator    |  <-- Component를 감싸는 추상 클래스
          +----------------+
                   ▲
         ┌─────────┴─────────┐
         │                   │
+-----------------+   +-----------------+
| ConcreteDecoratorA |   | ConcreteDecoratorB |  <-- 구체적인 데코레이터들
+-----------------+   +-----------------+
```

이 패턴은 기능이 많거나 변화가 잦은 경우, 객체를 여러 데코레이터로 감싸서 점진적으로 기능을 추가할 수 있도록 해줍니다.

***

### How do code?

{% tabs %}
{% tab title="JAVA" %}
```java
// 기본 컴포넌트 인터페이스: 공통 기능을 정의
public interface Component {
    void operation();
}

// 기본 컴포넌트: 실제 기본 기능을 구현
public class ConcreteComponent implements Component {
    @Override
    public void operation() {
        System.out.println("ConcreteComponent 기본 작업 수행");
    }
}

// 추상 데코레이터: Component 인터페이스를 구현하며, 다른 Component 객체를 참조
public abstract class Decorator implements Component {
    protected Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    @Override
    public void operation() {
        // 기본적으로 감싼 컴포넌트의 작업을 수행
        component.operation();
    }
}

// 구체적인 데코레이터 A: 추가 기능을 구현
public class ConcreteDecoratorA extends Decorator {
    public ConcreteDecoratorA(Component component) {
        super(component);
    }

    @Override
    public void operation() {
        // 추가 작업 전
        System.out.println("ConcreteDecoratorA: 추가 작업 전 처리");
        super.operation();
        // 추가 작업 후
        System.out.println("ConcreteDecoratorA: 추가 작업 후 처리");
    }
}

// 구체적인 데코레이터 B: 추가 기능을 구현
public class ConcreteDecoratorB extends Decorator {
    public ConcreteDecoratorB(Component component) {
        super(component);
    }

    @Override
    public void operation() {
        // 추가 작업 전
        System.out.println("ConcreteDecoratorB: 추가 작업 전 처리");
        super.operation();
        // 추가 작업 후
        System.out.println("ConcreteDecoratorB: 추가 작업 후 처리");
    }
}

// 클라이언트 코드
public class DecoratorDemo {
    public static void main(String[] args) {
        // 기본 컴포넌트 생성
        Component basicComponent = new ConcreteComponent();
        
        // 데코레이터 A로 감싸기
        Component decoratedA = new ConcreteDecoratorA(basicComponent);
        
        // 데코레이터 B로 감싸기 (여러 데코레이터로 결합 가능)
        Component decoratedB = new ConcreteDecoratorB(decoratedA);
        
        // 최종 객체의 operation() 호출 시, 여러 데코레이터의 기능이 순차적으로 수행됨
        decoratedB.operation();
    }
}

```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 기본 컴포넌트 인터페이스
interface Component {
    fun operation()
}

// 기본 컴포넌트: 실제 기본 기능 구현
class ConcreteComponent : Component {
    override fun operation() {
        println("ConcreteComponent 기본 작업 수행")
    }
}

// 추상 데코레이터: Component 인터페이스를 구현하며, 다른 Component 객체를 감싸는 역할
abstract class Decorator(protected val component: Component) : Component {
    override fun operation() {
        component.operation()
    }
}

// 구체적인 데코레이터 A: 추가 기능 구현
class ConcreteDecoratorA(component: Component) : Decorator(component) {
    override fun operation() {
        println("ConcreteDecoratorA: 추가 작업 전 처리")
        super.operation()
        println("ConcreteDecoratorA: 추가 작업 후 처리")
    }
}

// 구체적인 데코레이터 B: 추가 기능 구현
class ConcreteDecoratorB(component: Component) : Decorator(component) {
    override fun operation() {
        println("ConcreteDecoratorB: 추가 작업 전 처리")
        super.operation()
        println("ConcreteDecoratorB: 추가 작업 후 처리")
    }
}

// 클라이언트 코드
fun main() {
    // 기본 컴포넌트 생성
    val basicComponent: Component = ConcreteComponent()
    
    // 데코레이터 A로 감싸기
    val decoratedA: Component = ConcreteDecoratorA(basicComponent)
    
    // 데코레이터 B로 감싸기 (여러 데코레이터 결합 가능)
    val decoratedB: Component = ConcreteDecoratorB(decoratedA)
    
    // 최종 객체의 operation() 호출 시, 여러 데코레이터의 기능이 순차적으로 수행됨
    decoratedB.operation()
}

```
{% endtab %}
{% endtabs %}

* **기본 컴포넌트(Component)**\
  공통 기능을 정의하는 인터페이스 또는 추상 클래스이며, ConcreteComponent가 이 인터페이스를 구현합니다.
* **추상 데코레이터(Decorator)**\
  Component 인터페이스를 구현하고, 다른 Component 객체를 필드로 가지고 있습니다.\
  기본적으로 Component의 operation()을 호출하며, 서브클래스에서 추가 기능을 구현할 수 있습니다.
* **구체적인 데코레이터(ConcreteDecoratorA, ConcreteDecoratorB)**\
  Decorator를 상속받아 추가 작업(전처리 및 후처리 등)을 구현합니다.\
  데코레이터는 기본 컴포넌트를 감싸며, 여러 데코레이터를 결합해 복합적인 기능을 추가할 수 있습니다.
* **클라이언트 코드**\
  기본 컴포넌트를 생성하고, 원하는 데코레이터로 감싼 후 operation() 메서드를 호출하면, 데코레이터 계층 구조에 따라 추가된 기능이 모두 수행됩니다.
