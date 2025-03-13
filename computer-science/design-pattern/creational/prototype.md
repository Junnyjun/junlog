# Prototype

Prototype 패턴은 객체를 생성할 때 매번 새 인스턴스를 만드는 대신, 기존 인스턴스를 복제(clone, fork)하여 새로운 인스턴스를 만드는 방식입니다.&#x20;

이 방식을 사용하면 복잡한 객체 생성 비용을 줄일 수 있으며, 객체의 초기 상태를 복사해 빠르게 인스턴스를 생성할 수 있습니다.

### How do code?

{% embed url="https://gist.github.com/Junnyjun/beb4e9e5063cd55343e7e348bfe2ea9e" %}

### How do Code

{% tabs %}
{% tab title="JAVA" %}
```java
// Prototype 인터페이스 (또는 추상 클래스)
public abstract class Prototype implements Cloneable {
    // 객체를 복제하는 메서드 (깊은 복사를 위해 필요한 경우, 내부 필드들도 복제)
    public Prototype clone() {
        try {
            // 기본적으로 Object의 clone()을 사용
            return (Prototype) super.clone();
        } catch (CloneNotSupportedException e) {
            // 예외 처리 (필요에 따라 적절히 처리)
            e.printStackTrace();
            return null;
        }
    }
    
    // 공통 기능 및 필드 선언
    public abstract void showInfo();
}

// ConcretePrototype: 실제 복제 가능한 객체
public class ConcretePrototype extends Prototype {
    private String data;
    
    public ConcretePrototype(String data) {
        this.data = data;
    }
    
    // 필드에 대한 getter와 setter (깊은 복사가 필요한 필드라면 복제 로직 추가)
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public void showInfo() {
        System.out.println("ConcretePrototype의 data: " + data);
    }
}

// 클라이언트 코드: 기존 객체를 복제하여 새로운 인스턴스 생성
public class PrototypeDemo {
    public static void main(String[] args) {
        ConcretePrototype original = new ConcretePrototype("원본 데이터");
        ConcretePrototype clone = (ConcretePrototype) original.clone();
        
        // 복제된 객체의 상태 확인
        clone.showInfo();
        
        // 복제된 객체의 상태 변경 (원본과 별개의 인스턴스임을 확인)
        clone.setData("복제본 데이터");
        System.out.println("원본 객체:");
        original.showInfo();
        System.out.println("복제 객체:");
        clone.showInfo();
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Prototype 인터페이스 (여기서는 명시적으로 구현하지 않고, data class의 copy() 메서드를 사용)
data class ConcretePrototype(var data: String) {
    // showInfo() 메서드로 객체의 상태를 출력
    fun showInfo() {
        println("ConcretePrototype의 data: $data")
    }
}

// 클라이언트 코드: 기존 객체를 복제하여 새로운 인스턴스 생성
fun main() {
    val original = ConcretePrototype("원본 데이터")
    // copy() 메서드를 사용하여 객체를 복제 (기본적으로 얕은 복사이므로, 내부에 가변 객체가 있다면 깊은 복사 로직 필요)
    val clone = original.copy()
    
    // 복제된 객체의 상태 확인
    clone.showInfo()
    
    // 복제된 객체의 상태 변경 (원본과 별개의 인스턴스임을 확인)
    clone.data = "복제본 데이터"
    println("원본 객체:")
    original.showInfo()
    println("복제 객체:")
    clone.showInfo()
}
```
{% endtab %}
{% endtabs %}

* **핵심 아이디어**\
  객체를 생성할 때마다 새로 인스턴스를 생성하는 대신, 기존 인스턴스를 복제하여 생성 비용을 절감합니다.
* **깊은 복사 vs 얕은 복사**\
  복제 시 내부 참조 객체까지 모두 복제해야 한다면(깊은 복사) 별도의 복제 로직이 필요합니다.\
  기본적으로 `Object.clone()`은 얕은 복사를 수행하므로, 필요에 따라 각 필드별 복제 처리가 필요합니다.
* **장점**\
  객체 생성 비용이 큰 경우 복제 방식을 사용하여 성능 최적화를 도모할 수 있습니다.
