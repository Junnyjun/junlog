# Memento



Memento 패턴은 객체의 내부 상태를 저장(스냅샷)해두었다가, 필요할 때 해당 상태를 복원할 수 있도록 하는 패턴입니다.\
즉, 객체의 상태를 외부에 노출하지 않고 캡슐화하여 보존하고, 나중에 원래 상태로 복원할 수 있게 하는 역할을 합니다.

```plaintext
plaintext복사       +---------------+       저장/복원       +---------------+
       |   Originator  | -------------------> |   Memento     |
       | (상태 관리 객체)| <------------------- | (상태 캡슐화)  |
       +---------------+                       +---------------+
               │                                       ▲
               │                                       │
               │                                       │
               ▼                                       │
       +---------------+                               │
       |   Caretaker   | ------------------------------┘
       | (보관자 역할) |
       +---------------+
```

* **Originator**: 자신의 상태를 생성, 저장, 복원할 책임이 있는 객체입니다.
* **Memento**: Originator의 내부 상태를 캡슐화한 객체로, 외부에는 상태에 대한 세부 내용이 공개되지 않습니다.
* **Caretaker**: Memento 객체를 보관하며, 필요할 때 Originator에게 전달하여 상태 복원을 요청하는 역할을 합니다.

***

### How do code?

{% tabs %}
{% tab title="JAVA" %}
```java
// Memento: Originator의 상태를 캡슐화 (불변 객체로 설계하는 것이 좋음)
public class Memento {
    private final String state;

    public Memento(String state) {
        this.state = state;
    }

    // 상태 정보는 내부적으로만 사용되고 외부에 공개되지 않음
    protected String getState() {
        return state;
    }
}

// Originator: 자신의 상태를 저장하고 복원하는 역할
public class Originator {
    private String state;

    public void setState(String state) {
        this.state = state;
        System.out.println("Originator 상태 변경: " + state);
    }

    public String getState() {
        return state;
    }

    // 현재 상태를 Memento에 저장하여 반환
    public Memento saveStateToMemento() {
        return new Memento(state);
    }

    // Memento를 이용해 상태 복원
    public void getStateFromMemento(Memento memento) {
        this.state = memento.getState();
        System.out.println("Originator 상태 복원: " + state);
    }
}

// Caretaker: Memento를 보관하는 역할
public class Caretaker {
    private List<Memento> mementoList = new ArrayList<>();

    public void add(Memento state){
        mementoList.add(state);
    }

    public Memento get(int index){
        return mementoList.get(index);
    }
}

// 클라이언트 코드
public class MementoDemo {
    public static void main(String[] args) {
        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();

        originator.setState("State #1");
        originator.setState("State #2");
        caretaker.add(originator.saveStateToMemento());  // 상태 저장

        originator.setState("State #3");
        caretaker.add(originator.saveStateToMemento());  // 상태 저장

        originator.setState("State #4");
        System.out.println("현재 상태: " + originator.getState());

        // 이전 상태 복원
        originator.getStateFromMemento(caretaker.get(0));
        System.out.println("복원된 상태: " + originator.getState());
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Memento: Originator의 상태를 캡슐화 (불변 객체)
data class Memento(private val state: String) {
    // 상태 정보는 캡슐화되어 외부에 직접 노출되지 않음
    fun getState() = state
}

// Originator: 자신의 상태를 관리하고, Memento를 통해 저장 및 복원
class Originator {
    var state: String = ""
        private set

    fun setState(state: String) {
        this.state = state
        println("Originator 상태 변경: $state")
    }

    // 현재 상태를 Memento 객체로 저장
    fun saveStateToMemento(): Memento = Memento(state)

    // Memento 객체를 통해 상태 복원
    fun getStateFromMemento(memento: Memento) {
        state = memento.getState()
        println("Originator 상태 복원: $state")
    }
}

// Caretaker: Memento 객체들을 보관하는 역할
class Caretaker {
    private val mementoList = mutableListOf<Memento>()

    fun add(memento: Memento) {
        mementoList.add(memento)
    }

    fun get(index: Int): Memento = mementoList[index]
}

// 클라이언트 코드
fun main() {
    val originator = Originator()
    val caretaker = Caretaker()

    originator.setState("State #1")
    originator.setState("State #2")
    caretaker.add(originator.saveStateToMemento())  // 상태 저장

    originator.setState("State #3")
    caretaker.add(originator.saveStateToMemento())  // 상태 저장

    originator.setState("State #4")
    println("현재 상태: ${originator.state}")

    // 이전 상태 복원
    originator.getStateFromMemento(caretaker.get(0))
    println("복원된 상태: ${originator.state}")
}

```
{% endtab %}
{% endtabs %}

* **상태 저장**:\
  Originator 객체는 자신의 현재 상태를 Memento 객체에 저장할 수 있으며, 이를 Caretaker가 보관합니다.
* **상태 복원**:\
  저장된 Memento 객체를 Originator에게 전달하여, 이전 상태로 복원할 수 있습니다.
* **장점**:\
  객체의 내부 상태를 외부에 노출하지 않고 보존하며, 언제든지 특정 시점의 상태로 복원할 수 있기 때문에, 작업 취소(undo) 기능이나 스냅샷 관리에 유용합니다.
