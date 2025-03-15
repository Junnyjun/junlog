# Command

Command 패턴은 실행될 기능(명령)을 하나의 객체로 캡슐화하여, 호출자와 수행자 사이의 결합도를 낮추고, 명령의 재사용성, 큐잉, 취소 기능 등 다양한 확장을 가능하게 하는 디자인 패턴입니다.

```
          +---------------------+
          |     Command         |  <-- 실행할 기능을 캡슐화한 인터페이스
          +---------------------+
          | + execute()         |
          +---------------------+
                   ▲
         ┌─────────┴─────────┐
         │                   │
+----------------------+  +----------------------+
| ConcreteCommand1     |  | ConcreteCommand2     |  <-- 구체적인 명령 구현
+----------------------+  +----------------------+
| - receiver: Receiver |  | - receiver: Receiver |  
| + execute()          |  | + execute()          |
+----------------------+  +----------------------+
                   │
                   ▼
            +--------------+
            |   Receiver |  <-- 명령을 실제 수행하는 객체
            +--------------+
            | + action()   |
            +--------------+
                   ▲
                   │
            +--------------+
            |   Invoker  |  <-- 명령 실행을 요청하는 객체
            +--------------+
            | - command: Command |
            | + setCommand(c: Command) |
            | + invoke()           |
            +--------------+

```

즉, 실행할 작업을 Command 인터페이스에 정의하고, 이를 구현한 구체적인 Command 객체를 생성하여, 클라이언트는 이 Command 객체를 호출하는 방식으로 작업을 수행할 수 있습니다.

***

### How do Code❔

{% tabs %}
{% tab title="JAVA" %}
```java
// Command 인터페이스: 실행할 명령을 정의
public interface Command {
    void execute();
}

// Receiver: 명령을 실제 수행하는 객체
public class Receiver {
    public void action() {
        System.out.println("Receiver의 action() 메서드 실행");
    }
}

// ConcreteCommand: Command 인터페이스 구현, Receiver를 통해 작업을 수행
public class ConcreteCommand implements Command {
    private Receiver receiver;

    public ConcreteCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        System.out.println("ConcreteCommand: execute() 호출");
        receiver.action();
    }
}

// Invoker: 명령 실행을 요청하는 객체
public class Invoker {
    private Command command;

    // 명령 객체를 설정
    public void setCommand(Command command) {
        this.command = command;
    }

    // 설정된 명령을 실행
    public void invoke() {
        if (command != null) {
            command.execute();
        }
    }
}

// 클라이언트 코드
public class CommandDemo {
    public static void main(String[] args) {
        // 수신자 생성
        Receiver receiver = new Receiver();

        // 구체적인 명령 생성 (Receiver 전달)
        Command command = new ConcreteCommand(receiver);

        // 호출자(Invoker) 생성 후, 명령 설정 및 실행 요청
        Invoker invoker = new Invoker();
        invoker.setCommand(command);
        invoker.invoke();
    }
}

```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Command 인터페이스: 실행할 명령을 정의
interface Command {
    fun execute()
}

// Receiver: 명령을 실제 수행하는 객체
class Receiver {
    fun action() {
        println("Receiver의 action() 메서드 실행")
    }
}

// ConcreteCommand: Command 인터페이스를 구현하며, Receiver를 통해 작업 수행
class ConcreteCommand(private val receiver: Receiver) : Command {
    override fun execute() {
        println("ConcreteCommand: execute() 호출")
        receiver.action()
    }
}

// Invoker: 명령 실행을 요청하는 객체
class Invoker {
    private var command: Command? = null

    // 명령 객체를 설정
    fun setCommand(command: Command) {
        this.command = command
    }

    // 설정된 명령을 실행
    fun invoke() {
        command?.execute()
    }
}

// 클라이언트 코드
fun main() {
    // 수신자 생성
    val receiver = Receiver()

    // 구체적인 명령 생성 (Receiver 전달)
    val command: Command = ConcreteCommand(receiver)

    // 호출자(Invoker) 생성 후, 명령 설정 및 실행 요청
    val invoker = Invoker()
    invoker.setCommand(command)
    invoker.invoke()
}

```
{% endtab %}
{% endtabs %}

* **Command 인터페이스**
  * `execute()` 메서드를 정의하여, 호출 시 실행될 기능을 캡슐화합니다.
* **ConcreteCommand 클래스**:
  * Command 인터페이스를 구현하며, 내부에 Receiver 객체를 가지고 있습니다.
  * `execute()` 호출 시 Receiver의 특정 메서드를 호출하여 실제 작업을 수행합니다.
* **Receiver**
  * 실제 명령을 수행하는 객체로, Command 객체에 의해 호출됩니다.
* **Invoker**
  * 클라이언트가 명령 실행을 요청하는 객체입니다.
  * Command 객체를 설정(setCommand)하고, 필요할 때 `invoke()` 메서드를 호출하여 명령을 실행합니다.
* **장점**
  * 명령 캡슐화를 통해 호출자와 수행자 간의 결합도를 낮추며, 명령을 객체로 관리할 수 있습니다.
  * 명령 큐, 취소(undo), 로깅 등 다양한 기능 확장이 용이해집니다.
