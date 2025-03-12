# Bridge

Bridge 패턴은 기능(Abstraction)과 구현(Implementation)을 분리하여 서로 독립적으로 확장하거나 변경할 수 있도록 하는 디자인 패턴입니다.&#x20;

```
       +------------------+
       |  Abstraction     |  <-- 기능 계층 (추상 클래스)
       +------------------+
                |
                |  (구현 객체 참조)
                v
       +------------------+
       | RefinedAbstraction|
       +------------------+
                |
                |  (구현 계층과 연결)
                v
       +------------------+
       |  Implementor     |  <-- 구현 계층 (인터페이스)
       +------------------+
                /   \
               /     \
              v       v
+------------------+  +---------------------+
|ConcreteImplementor1| |ConcreteImplementor2|
+------------------+  +---------------------+
```

이 패턴을 사용하면 상속에 의존하지 않고 두 계층을 독립적으로 발전시킬 수 있으므로, 확장성과 유연성이 크게 향상됩니다.

***

### How do code

{% tabs %}
{% tab title="JAVA" %}
```java
// 구현부 인터페이스 (Implementor)
@FunctionalInterface
public interface DrawingAPI {
    void drawCircle(double x, double y, double radius);
}

public class BridgePattern {
    // 기능 계층 (Abstraction)
    public static abstract class Shape {
        protected DrawingAPI drawingAPI;

        protected Shape(DrawingAPI drawingAPI) {
            this.drawingAPI = drawingAPI;
        }

        public abstract void draw();
        public abstract void resizeByPercentage(double pct);
    }

    // 구체적인 기능 클래스
    public static class CircleShape extends Shape {
        private double x, y, radius;

        public CircleShape(double x, double y, double radius, DrawingAPI drawingAPI) {
            super(drawingAPI);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        @Override
        public void draw() {
            drawingAPI.drawCircle(x, y, radius);
        }

        @Override
        public void resizeByPercentage(double pct) {
            radius *= (1.0 + pct / 100.0);
        }
    }

    public static void main(String[] args) {
        // 람다 표현식으로 DrawingAPI 구현
        DrawingAPI api1 = (x, y, radius) ->
            System.out.println("Lambda API1 - 원: (" + x + ", " + y + ") 반지름: " + radius);

        DrawingAPI api2 = (x, y, radius) ->
            System.out.println("Lambda API2 - 원: (" + x + ", " + y + ") 반지름: " + radius);

        Shape circle1 = new CircleShape(1, 2, 3, api1);
        Shape circle2 = new CircleShape(5, 7, 11, api2);

        circle1.resizeByPercentage(50);
        circle2.resizeByPercentage(50);

        circle1.draw();
        circle2.draw();
    }
}


```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// 코틀린에서 함수 타입을 사용해 DrawingAPI를 정의
typealias DrawingAPI = (x: Double, y: Double, radius: Double) -> Unit

// 기능 계층 (Abstraction)
abstract class Shape(protected val drawingAPI: DrawingAPI) {
    abstract fun draw()
    abstract fun resizeByPercentage(pct: Double)
}

// 구체적인 기능 클래스
class CircleShape(
    private var x: Double,
    private var y: Double,
    private var radius: Double,
    drawingAPI: DrawingAPI
) : Shape(drawingAPI) {

    override fun draw() {
        drawingAPI(x, y, radius)
    }

    override fun resizeByPercentage(pct: Double) {
        radius *= (1.0 + pct / 100.0)
    }
}

fun main() {
    // 람다로 DrawingAPI 구현
    val api1: DrawingAPI = { x, y, radius ->
        println("Lambda API1 - 원: ($x, $y) 반지름: $radius")
    }

    val api2: DrawingAPI = { x, y, radius ->
        println("Lambda API2 - 원: ($x, $y) 반지름: $radius")
    }

    val circle1 = CircleShape(1.0, 2.0, 3.0, api1)
    val circle2 = CircleShape(5.0, 7.0, 11.0, api2)

    circle1.resizeByPercentage(50.0)
    circle2.resizeByPercentage(50.0)

    circle1.draw()
    circle2.draw()
}

```
{% endtab %}
{% endtabs %}

