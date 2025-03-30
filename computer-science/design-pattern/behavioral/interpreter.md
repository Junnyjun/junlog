# Interpreter

인터프리터 패턴은 주어진 언어(또는 표현식)를 해석하는 규칙을 클래스로 표현하여, 해당 언어의 문법을 해석하고 실행할 수 있도록 하는 디자인 패턴입니다.

```
          +--------------------------+
          |      Expression          |  <-- 인터프리터 인터페이스
          +--------------------------+
          | + interpret(context)     |
          +--------------------------+
                     ▲
        ┌────────────┴────────────┐
        │                         │
+-------------------+    +-------------------------+
| TerminalExpression|    | NonTerminalExpression   |
|   (숫자, 변수 등)  |    |  (연산자 등 복합 표현)    |
+-------------------+    +-------------------------+
| + interpret()     |    | + interpret()           |
+-------------------+    +-------------------------+
```

표현식(문법 규칙)을 캡슐화한 객체들(TerminalExpression, NonTerminalExpression)을 사용하여 복잡한 언어를 파싱하고 해석할 수 있습니다.

* **타겟(Target)**: 클라이언트가 사용하는 인터페이스입니다.
* **어댑티(Adaptee)**: 기존에 존재하는, 원하는 인터페이스와 다른 인터페이스를 가진 클래스입니다.
* **어댑터(Adapter)**: 어댑티의 인터페이스를 타겟 인터페이스로 변환하는 클래스입니다.

***

### How do code

{% tabs %}
{% tab title="JAVA" %}
```java
import java.util.Map;

// Expression 인터페이스: 해석 메서드를 정의
public interface Expression {
    int interpret(Map<String, Integer> context);
}

// TerminalExpression: 숫자와 변수 등의 터미널 표현식을 구현
public class NumberExpression implements Expression {
    private int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        return number;
    }
}

// NonTerminalExpression: 두 Expression을 더하는 덧셈 표현식을 구현
public class AddExpression implements Expression {
    private Expression leftExpression;
    private Expression rightExpression;

    public AddExpression(Expression left, Expression right) {
        this.leftExpression = left;
        this.rightExpression = right;
    }

    @Override
    public int interpret(Map<String, Integer> context) {
        // 왼쪽과 오른쪽 표현식을 해석하여 결과를 더함
        return leftExpression.interpret(context) + rightExpression.interpret(context);
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Expression 인터페이스: 해석 메서드 정의
interface Expression {
    fun interpret(context: Map<String, Int>): Int
}

// TerminalExpression: 숫자와 변수 등을 표현하는 터미널 클래스
class NumberExpression(private val number: Int) : Expression {
    override fun interpret(context: Map<String, Int>): Int = number
}

// NonTerminalExpression: 두 Expression을 더하는 덧셈 표현식
class AddExpression(
    private val leftExpression: Expression,
    private val rightExpression: Expression
) : Expression {
    override fun interpret(context: Map<String, Int>): Int =
        leftExpression.interpret(context) + rightExpression.interpret(context)
}
```
{% endtab %}
{% endtabs %}

* **Expression 인터페이스**:\
  모든 해석 대상(문법 요소)이 구현해야 하는 인터페이스로, `interpret()` 메서드를 통해 해석 결과를 반환합니다.
* **TerminalExpression (NumberExpression)**:\
  숫자나 변수와 같이 더 이상 분해할 수 없는 기본 요소를 표현합니다.\
  이 예제에서는 숫자 값을 단순히 반환합니다.
* **NonTerminalExpression (AddExpression)**:\
  두 개의 Expression 객체를 결합하여 연산(여기서는 덧셈)을 수행합니다.\
  내부적으로 왼쪽과 오른쪽 Expression을 해석하여 결과를 결합합니다.
* **Context**:\
  해석 시 필요한 외부 정보를 담은 객체(여기서는 변수 값 등)를 의미합니다.\
  예제에서는 사용하지 않았지만, 복잡한 언어를 해석할 때 변수 매핑 등을 위해 사용됩니다.
