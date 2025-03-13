---
description: What is❔
---

# Composite

Composite 패턴은 여러 개의 객체들로 구성된 복합 객체(Composite)와 단일 객체(Leaf)를 동일한 인터페이스로 다룰 수 있도록 해주는 디자인 패턴입니다.&#x20;

```
       +----------------+
       |    Graphic     |  <-- 공통 인터페이스
       +----------------+
              /  \
             /    \
            /      \
  +---------------+   +--------------------+
  |    Ellipse    |   | CompositeGraphic   |
  |   (Leaf)      |   |   (Composite)      |
  +---------------+   +--------------------+
                          /      |       \
                         /       |        \
                        /        |         \
               +----------+ +----------+ +----------+
               | Graphic  | | Graphic  | | Graphic  |
               | (Leaf or | | (Leaf or | | (Leaf or |
               | Composite)| | Composite)| | Composite)|
               +----------+ +----------+ +----------+
```

즉, 사용자는 개별 객체와 복합 객체를 구분하지 않고 동일한 연산을 수행할 수 있게 됩니다.&#x20;

이 패턴은 전체-부분 관계를 표현할 때 유용하며, 트리 구조의 계층을 구성하는 데 적합합니다.

***

### How do Code❔

* 장점: 구현이 간단하며 멀티스레드 환경에서도 안전합니다.
* 단점: 사용하지 않더라도 인스턴스가 생성되어 메모리 낭비가 발생할 수 있습니다.

{% tabs %}
{% tab title="JAVA" %}
```java
// 공통 인터페이스: Leaf와 Composite 모두 동일한 인터페이스를 구현합니다.
public interface Graphic {
    void draw();
}

// Leaf 클래스: 더 이상 구성요소가 없는 개별 객체입니다.
public class Ellipse implements Graphic {
    private final String name;

    public Ellipse(String name) {
        this.name = name;
    }

    @Override
    public void draw() {
        System.out.println("Ellipse 그리기: " + name);
    }
}

// Composite 클래스: 하나 이상의 Graphic 객체를 포함하는 복합 객체입니다.
public class CompositeGraphic implements Graphic {
    private final List<Graphic> children = new ArrayList<>();

    // 자식 추가
    public void add(Graphic graphic) {
        children.add(graphic);
    }

    // 자식 제거
    public void remove(Graphic graphic) {
        children.remove(graphic);
    }

    @Override
    public void draw() {
        System.out.println("CompositeGraphic 그리기 시작");
        for (Graphic graphic : children) {
            graphic.draw();
        }
        System.out.println("CompositeGraphic 그리기 종료");
    }
}

// 사용 예제
public class CompositePatternDemo {
    public static void main(String[] args) {
        // Leaf 객체 생성
        Ellipse ellipse1 = new Ellipse("Ellipse 1");
        Ellipse ellipse2 = new Ellipse("Ellipse 2");
        Ellipse ellipse3 = new Ellipse("Ellipse 3");

        // Composite 객체 생성 및 Leaf 객체 추가
        CompositeGraphic compositeGraphic = new CompositeGraphic();
        compositeGraphic.add(ellipse1);
        compositeGraphic.add(ellipse2);

        // 또 다른 Composite 객체 생성 및 Leaf, Composite 추가
        CompositeGraphic complexGraphic = new CompositeGraphic();
        complexGraphic.add(ellipse3);
        complexGraphic.add(compositeGraphic);

        // 전체 구성 요소에 대해 draw() 호출
        complexGraphic.draw();
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 공통 인터페이스: Leaf와 Composite 모두 동일한 인터페이스를 구현합니다.
interface Graphic {
    fun draw()
}

// Leaf 클래스: 더 이상 구성요소가 없는 개별 객체입니다.
class Ellipse(private val name: String) : Graphic {
    override fun draw() {
        println("Ellipse 그리기: $name")
    }
}

// Composite 클래스: 하나 이상의 Graphic 객체를 포함하는 복합 객체입니다.
class CompositeGraphic : Graphic {
    private val children = mutableListOf<Graphic>()

    // 자식 추가
    fun add(graphic: Graphic) {
        children.add(graphic)
    }

    // 자식 제거
    fun remove(graphic: Graphic) {
        children.remove(graphic)
    }

    override fun draw() {
        println("CompositeGraphic 그리기 시작")
        children.forEach { it.draw() }
        println("CompositeGraphic 그리기 종료")
    }
}

// 사용 예제
fun main() {
    // Leaf 객체 생성
    val ellipse1 = Ellipse("Ellipse 1")
    val ellipse2 = Ellipse("Ellipse 2")
    val ellipse3 = Ellipse("Ellipse 3")

    // Composite 객체 생성 및 Leaf 객체 추가
    val compositeGraphic = CompositeGraphic().apply {
        add(ellipse1)
        add(ellipse2)
    }

    // 또 다른 Composite 객체 생성 및 Leaf, Composite 추가
    val complexGraphic = CompositeGraphic().apply {
        add(ellipse3)
        add(compositeGraphic)
    }

    // 전체 구성 요소에 대해 draw() 호출
    complexGraphic.draw()
}

```
{% endtab %}
{% endtabs %}

* **목적**: 전체-부분 관계를 표현하여, 단일 객체와 복합 객체를 동일한 방식으로 다룰 수 있도록 함
* **장점**: 클라이언트가 복합 구조의 내부 구성에 대해 신경 쓰지 않고 동일한 인터페이스로 연산 수행 가능
* **적용 예**: 그래픽, 파일 시스템, UI 컴포넌트 등 계층적인 구조를 다룰 때 유용함
