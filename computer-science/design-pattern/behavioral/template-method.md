# Template Method

Template Method 패턴은 알고리즘의 전체적인 뼈대를 추상 클래스에 정의해두고, 그 중 일부 구체적인 단계는 서브클래스에서 구현하도록 위임하는 디자인 패턴입니다.&#x20;

```
           +-------------------------+
           |    AbstractClass        |  <-- 템플릿 메서드 정의
           +-------------------------+
           | + templateMethod()      |  <-- 알고리즘의 전체 구조
           |   {                   } |
           |                         |
           | - primitiveOperation1() |  <-- 구현은 서브클래스에 위임
           | - primitiveOperation2() |  <-- 구현은 서브클래스에 위임
           +-------------------------+
                     /   \
                    /     \
                   /       \
   +----------------+   +----------------+
   | ConcreteClass1 |   | ConcreteClass2 |  <-- 구체적인 구현 클래스들
   +----------------+   +----------------+
   | + primitiveOperation1() 구현  | 
   | + primitiveOperation2() 구현  | 
   +----------------+   +----------------+

```

즉, 알고리즘의 공통 부분은 부모 클래스에서 처리하고, 변하는 부분은 자식 클래스에서 구현하여 코드의 중복을 줄이고, 알고리즘의 일관성을 유지할 수 있습니다.

### How do Code?

{% tabs %}
{% tab title="JAVA" %}
```java
// 추상 클래스: 알고리즘의 뼈대를 정의하는 템플릿 메서드 포함
public abstract class AbstractClass {
    
    // 템플릿 메서드: 알고리즘의 전체 구조를 정의하며, 일부 단계는 추상 메서드로 위임
    public final void templateMethod() {
        primitiveOperation1();
        primitiveOperation2();
        concreteOperation();
    }
    
    // 서브클래스에서 구현할 추상 메서드들
    protected abstract void primitiveOperation1();
    protected abstract void primitiveOperation2();
    
    // 공통으로 사용되는 구체적인 메서드 (필요에 따라 오버라이딩 가능)
    private void concreteOperation() {
        System.out.println("AbstractClass의 공통 작업 수행");
    }
}

// 구체적인 구현 클래스 1
public class ConcreteClass1 extends AbstractClass {
    @Override
    protected void primitiveOperation1() {
        System.out.println("ConcreteClass1: primitiveOperation1 수행");
    }
    
    @Override
    protected void primitiveOperation2() {
        System.out.println("ConcreteClass1: primitiveOperation2 수행");
    }
}

// 구체적인 구현 클래스 2
public class ConcreteClass2 extends AbstractClass {
    @Override
    protected void primitiveOperation1() {
        System.out.println("ConcreteClass2: primitiveOperation1 수행");
    }
    
    @Override
    protected void primitiveOperation2() {
        System.out.println("ConcreteClass2: primitiveOperation2 수행");
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 추상 클래스: 템플릿 메서드를 포함하여 알고리즘의 전체 흐름을 정의
abstract class AbstractClass {
    
    // 템플릿 메서드: 전체 알고리즘의 흐름을 정의, final 역할을 하므로 오버라이딩 금지
    fun templateMethod() {
        primitiveOperation1()
        primitiveOperation2()
        concreteOperation()
    }
    
    // 서브클래스에서 구현할 추상 메서드들
    protected abstract fun primitiveOperation1()
    protected abstract fun primitiveOperation2()
    
    // 공통 작업: 필요에 따라 서브클래스에서 변경하지 않고 사용
    private fun concreteOperation() {
        println("AbstractClass의 공통 작업 수행")
    }
}

// 구체적인 구현 클래스 1
class ConcreteClass1 : AbstractClass() {
    override fun primitiveOperation1() {
        println("ConcreteClass1: primitiveOperation1 수행")
    }
    
    override fun primitiveOperation2() {
        println("ConcreteClass1: primitiveOperation2 수행")
    }
}

// 구체적인 구현 클래스 2
class ConcreteClass2 : AbstractClass() {
    override fun primitiveOperation1() {
        println("ConcreteClass2: primitiveOperation1 수행")
    }
    
    override fun primitiveOperation2() {
        println("ConcreteClass2: primitiveOperation2 수행")
    }
}
```
{% endtab %}
{% endtabs %}

* **템플릿 메서드(templateMethod())**\
  알고리즘의 전체 흐름을 정의합니다. 템플릿 메서드 내에서 공통으로 수행해야 하는 단계와, 서브클래스에서 각각 다르게 구현해야 하는 추상 메서드를 호출합니다.
* **primitiveOperation1()와 primitiveOperation2()**\
  서브클래스에서 구체적으로 구현할 메서드입니다. 이 메서드들이 알고리즘의 변경 가능한 부분을 담당합니다.
* **concreteOperation()**\
  알고리즘에서 변경되지 않고 공통으로 수행되는 작업으로, 보통 private 메서드로 정의되어 서브클래스에서 오버라이딩하지 않습니다.
* **장점**\
  알고리즘의 공통 부분과 변화하는 부분을 분리함으로써 코드 재사용성을 높이고, 알고리즘의 구조를 일정하게 유지할 수 있습니다.
