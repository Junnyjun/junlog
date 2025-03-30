# Role Change

역할변경 패턴(Role Change Pattern)은 객체가 상황에 따라 동적으로 자신의 역할(행위)을 변경하거나 추가할 수 있도록 하는 패턴입니다.

```
           +--------------+
           |    Person    |  <-- 핵심 객체 (역할을 동적으로 관리)
           +--------------+
           | - roles: List<Role> |
           +--------------+
                   │
                   │ 역할 위임
                   ▼
           +--------------+
           |   Role       |  <-- 역할 인터페이스 (공통 행위 정의)
           +--------------+
           | + execute()  |
           +--------------+
             /           \
            /             \
           ▼               ▼
+----------------+  +------------------+
| StudentRole    |  | EmployeeRole     |
| (구체적 역할)   |  | (구체적 역할)     |
+----------------+  +------------------+

```

즉, 핵심 객체(예: Person)가 다양한 역할(Role)들을 외부에서 부여받아, 그 역할에 따른 행동을 실행할 수 있도록 하는 방식입니다.

이 패턴은 상속 대신 구성을 사용하여, 런타임 시 역할을 추가/제거하거나 변경할 수 있는 유연한 구조를 제공합니다.

### How do Code?

{% tabs %}
{% tab title="JAVA" %}
```java
public interface Role {
    void execute();
}

// StudentRole: Role 인터페이스를 구현하여 학생 역할을 수행
public class StudentRole implements Role {
    @Override
    public void execute() {
        System.out.println("StudentRole: 공부하는 중...");
    }
}

// EmployeeRole: Role 인터페이스를 구현하여 직원 역할을 수행
public class EmployeeRole implements Role {
    @Override
    public void execute() {
        System.out.println("EmployeeRole: 일하는 중...");
    }
}

// Person 클래스: 역할을 동적으로 관리하는 핵심 객체
import java.util.ArrayList;
import java.util.List;

public class Person {
    private List<Role> roles = new ArrayList<>();

    // 역할 추가
    public void addRole(Role role) {
        roles.add(role);
    }

    // 역할 제거
    public void removeRole(Role role) {
        roles.remove(role);
    }

    // 부여된 모든 역할의 행동을 실행
    public void performRoles() {
        for (Role role : roles) {
            role.execute();
        }
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
interface Role {
    fun execute()
}

// StudentRole: Role 인터페이스를 구현하여 학생 역할을 수행
class StudentRole : Role {
    override fun execute() {
        println("StudentRole: 공부하는 중...")
    }
}

// EmployeeRole: Role 인터페이스를 구현하여 직원 역할을 수행
class EmployeeRole : Role {
    override fun execute() {
        println("EmployeeRole: 일하는 중...")
    }
}

// Person 클래스: 역할을 동적으로 관리하는 핵심 객체
class Person {
    private val roles = mutableListOf<Role>()

    // 역할 추가
    fun addRole(role: Role) {
        roles.add(role)
    }

    // 역할 제거
    fun removeRole(role: Role) {
        roles.remove(role)
    }

    // 부여된 모든 역할의 행동을 실행
    fun performRoles() {
        roles.forEach { it.execute() }
    }
}
```
{% endtab %}
{% endtabs %}
