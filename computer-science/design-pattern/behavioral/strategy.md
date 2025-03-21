---
description: 행위를 동적으로 바꿔끼어 봅시다.
---

# Strategy

Strategy 패턴은 알고리즘(또는 행위)을 캡슐화하여 클라이언트가 실행 시점에 자유롭게 교체하거나 변경할 수 있도록 하는 디자인 패턴입니다.

```
           +---------------------+
           |     Context         |  <-- 전략(Strategy) 사용 객체
           +---------------------+
           | - strategy: Strategy|  <-- 실행할 전략(알고리즘)
           +---------------------+
           | + setStrategy(s)    |  <-- 전략 변경 메서드
           | + executeStrategy() |
           +---------------------+
                     │
                     │ 위임
                     ▼
           +---------------------+
           |     Strategy        |  <-- 공통 인터페이스 (알고리즘 정의)
           +---------------------+
           | + execute()         |
           +---------------------+
                     ▲
         ┌───────────┴───────────┐
         │                       │
+----------------------+  +----------------------+
| ConcreteStrategyA    |  | ConcreteStrategyB    |  <-- 구체적인 전략들
+----------------------+  +----------------------+
| + execute()          |  | + execute()          |
+----------------------+  +----------------------+

```

즉, 특정 규칙이나 방식을 독립된 전략 객체로 분리하여, 실행 중에 알고리즘을 변경할 수 있게 해줍니다.

User는 자신의 상태를 조회한다.

무기와 방어구를 상황에 맞게 교체할 수 있다.

### How do code?

{% tabs %}
{% tab title="JAVA" %}
```java
// Strategy 인터페이스: 실행할 알고리즘을 정의
public interface Strategy {
    void execute();
}

// ConcreteStrategyA: Strategy 인터페이스 구현 (알고리즘 A)
public class ConcreteStrategyA implements Strategy {
    @Override
    public void execute() {
        System.out.println("ConcreteStrategyA: 알고리즘 A 실행");
    }
}

// ConcreteStrategyB: Strategy 인터페이스 구현 (알고리즘 B)
public class ConcreteStrategyB implements Strategy {
    @Override
    public void execute() {
        System.out.println("ConcreteStrategyB: 알고리즘 B 실행");
    }
}

// Context: 실행 시점에 전략을 주입 받아 사용
public class Context {
    private Strategy strategy;

    // 생성자 혹은 setter를 통해 전략을 주입받음
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    // 전략에 따른 알고리즘 실행
    public void executeStrategy() {
        strategy.execute();
    }
}

// 클라이언트 코드
public class StrategyDemo {
    public static void main(String[] args) {
        // 초기 전략을 ConcreteStrategyA로 설정
        Context context = new Context(new ConcreteStrategyA());
        context.executeStrategy();

        // 실행 중에 전략을 ConcreteStrategyB로 변경
        context.setStrategy(new ConcreteStrategyB());
        context.executeStrategy();
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Strategy 인터페이스: 실행할 알고리즘을 정의
interface Strategy {
    fun execute()
}

// ConcreteStrategyA: Strategy 인터페이스 구현 (알고리즘 A)
class ConcreteStrategyA : Strategy {
    override fun execute() {
        println("ConcreteStrategyA: 알고리즘 A 실행")
    }
}

// ConcreteStrategyB: Strategy 인터페이스 구현 (알고리즘 B)
class ConcreteStrategyB : Strategy {
    override fun execute() {
        println("ConcreteStrategyB: 알고리즘 B 실행")
    }
}

// Context: 전략 객체를 주입받아 사용
class Context(private var strategy: Strategy) {
    // 전략 변경 메서드
    fun setStrategy(strategy: Strategy) {
        this.strategy = strategy
    }

    // 전략에 따라 알고리즘 실행
    fun executeStrategy() {
        strategy.execute()
    }
}

// 클라이언트 코드
fun main() {
    // 초기 전략을 ConcreteStrategyA로 설정
    val context = Context(ConcreteStrategyA())
    context.executeStrategy()

    // 실행 중에 전략을 ConcreteStrategyB로 변경
    context.setStrategy(ConcreteStrategyB())
    context.executeStrategy()
}
```
{% endtab %}
{% endtabs %}

* **Context**:
  * 전략을 실행할 환경(문맥)을 나타냅니다.
  * Strategy 인터페이스를 구현한 객체를 주입받아, 실행 시점에 해당 전략의 `execute()` 메서드를 호출합니다.
  * 전략 변경이 가능하여, 실행 중에도 알고리즘을 동적으로 전환할 수 있습니다.
* **Strategy 인터페이스**:
  * 실행할 알고리즘(또는 행위)을 정의합니다.
  * 여러 ConcreteStrategy들이 이 인터페이스를 구현하여 서로 다른 알고리즘을 제공합니다.
* **ConcreteStrategyA/B**:
  * Strategy 인터페이스의 구체적인 구현체로, 각기 다른 알고리즘을 수행합니다.
  * 클라이언트는 Context에 원하는 전략 객체를 주입하여, 실행 방식이나 규칙을 손쉽게 변경할 수 있습니다.
