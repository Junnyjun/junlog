# Observer

Observer 패턴은 한 객체(Subject)의 상태 변화에 따라 여러 객체(Observer)들이 자동으로 갱신되도록 하는 1:N 관계를 구성하는 디자인 패턴입니다.&#x20;

```
           +----------------------+
           |      Subject         |  <-- 상태 변경을 통보하는 객체
           +----------------------+
           | + attach(o:Observer) |
           | + detach(o:Observer) |
           | + notifyObservers()  |
           +----------------------+
                    │
          ┌─────────┴─────────┐
          │         ...       │   1:N 관계
          │                   │
   +--------------+    +--------------+
   |  Observer    |    |  Observer    |  <-- 상태 변경을 받아 처리하는 객체들
   +--------------+    +--------------+
   | + update()   |    | + update()   |
   +--------------+    +--------------+
```

즉, Subject와 Observer 간의 느슨한 결합(loose coupling)을 통해, 데이터 변경이 있을 때 상대 클래스에 직접 의존하지 않고 Observer들에게 변경 사실을 알려줄 수 있습니다.

### How do code ?

{% tabs %}
{% tab title="JAVA" %}
```java
// Observer 인터페이스: Subject의 상태 변경을 통보받기 위한 메서드 정의
public interface Observer {
    void update(String message);
}

// Subject 인터페이스: Observer를 관리하는 메서드 정의
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

// ConcreteSubject: 실제로 Observer들을 관리하고, 상태 변경 시 통보하는 클래스
public class ConcreteSubject implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private String message;

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    // 상태 변경 시 Observer들에게 통보
    public void setMessage(String message) {
        this.message = message;
        notifyObservers();
    }
}

// ConcreteObserver: 상태 변경을 받아 처리하는 클래스
public class ConcreteObserver implements Observer {
    private final String name;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + "이(가) 업데이트 받음: " + message);
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Observer 인터페이스: 상태 변경을 통보받는 메서드 정의
interface Observer {
    fun update(message: String)
}

// Subject 인터페이스: Observer 관리 메서드 정의
interface Subject {
    fun attach(observer: Observer)
    fun detach(observer: Observer)
    fun notifyObservers()
}

// ConcreteSubject: Observer들을 관리하고 상태 변경 시 통보하는 클래스
class ConcreteSubject : Subject {
    private val observers = mutableListOf<Observer>()
    private var message: String = ""

    override fun attach(observer: Observer) {
        observers.add(observer)
    }

    override fun detach(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(message) }
    }

    // 상태 변경 시 Observer들에게 통보
    fun setMessage(message: String) {
        this.message = message
        notifyObservers()
    }
}

// ConcreteObserver: 상태 변경을 받아 처리하는 클래스
class ConcreteObserver(private val name: String) : Observer {
    override fun update(message: String) {
        println("$name 이(가) 업데이트 받음: $message")
    }
}
```
{% endtab %}
{% endtabs %}

**Subject (Observable 객체)**

* Observer를 등록(attach)하거나 해제(detach)하는 메서드를 가지고, 상태 변경 시 모든 Observer들에게 알리는 역할을 합니다.
* 내부 상태(예: 메시지)가 변경되면 `notifyObservers()`를 호출하여 등록된 Observer들에게 변경 내용을 전달합니다.

**Observer**

* Subject로부터 상태 변경을 통보받기 위한 `update()` 메서드를 정의합니다.
* 각각의 ConcreteObserver는 이 메서드를 구현하여 자신만의 방식으로 상태 변경에 대응합니다.

**장점**

* Subject와 Observer 간의 결합도를 낮춰, 서로 독립적으로 확장 및 수정할 수 있습니다.
* 한 객체의 상태 변경이 여러 객체에 자동으로 반영되므로, 데이터 일관성을 유지하기 쉽습니다.
