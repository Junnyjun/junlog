# 베이스 패턴

#### Chapter 18. Base Patterns

이 장에서는 소프트웨어 설계에서 자주 사용되는 기본적인 패턴들을 설명합니다. 이러한 패턴들은 시스템의 복잡성을 줄이고, 코드의 재사용성을 높이며, 유지보수성을 향상시키기 위해 자주 활용됩니다. 이 장에서는 주로 객체 지향 프로그래밍에서의 설계 패턴들을 다루며, 각 패턴이 어떻게 작동하는지, 언제 사용해야 하는지에 대한 지침을 제공합니다.

**Gateway (게이트웨이)**

**Gateway**는 외부 시스템이나 리소스에 대한 접근을 캡슐화하는 객체입니다. 객체 지향 시스템에서는 종종 객체가 아닌 외부 자원, 예를 들어, 관계형 데이터베이스 테이블, XML 데이터 구조, 또는 CICS 트랜잭션과 같은 것을 다루어야 합니다. 이러한 외부 자원에 접근할 때는 보통 해당 자원을 위한 API를 사용하게 되는데, 이러한 API는 자원의 특성에 맞추어 설계되어 복잡한 경우가 많습니다. Gateway 패턴은 이러한 복잡한 외부 API를 간단한 인터페이스로 감싸는 역할을 합니다.

**작동 방식**

Gateway는 외부 시스템이나 리소스와 상호작용하는 부분을 단순화하는 래퍼(wrapper)로 작동합니다. 애플리케이션에서 외부 자원을 필요로 할 때, 해당 자원에 직접 접근하는 대신 Gateway를 통해 접근하여, 외부 자원의 복잡성을 애플리케이션 내부의 다른 부분에 드러내지 않습니다.

예를 들어, 외부 메시지 서비스를 사용하는 애플리케이션이 있다고 가정합시다. 이때 메시지 서비스의 API가 복잡하거나 다루기 어려운 경우, Gateway 패턴을 사용하여 메시지 전송을 위한 단순한 메서드 집합을 제공합니다. 이렇게 하면 애플리케이션의 다른 부분에서는 메시지 전송이 복잡한 API 호출 없이도 쉽게 이루어집니다.

**서비스 스텁과의 연계**

Gateway 패턴의 또 다른 중요한 역할은 **Service Stub**(서비스 스텁) 패턴을 쉽게 적용할 수 있는 지점을 제공한다는 것입니다. 서비스 스텁은 테스트 환경에서 실제 외부 시스템을 모방하는 역할을 합니다. 예를 들어, 메시지 서비스가 실제로 존재하지 않거나 접근할 수 없는 경우, Gateway 패턴을 사용하여 메시지 전송 로직을 단순화하고, 실제 메시지 서비스 대신에 서비스 스텁을 사용하여 테스트를 수행할 수 있습니다.

**장점**

* **단순화**: 외부 서비스와의 상호작용을 단순화하여 애플리케이션 내부의 복잡성을 줄일 수 있습니다.
* **유연성**: 외부 서비스가 변경되거나 다른 서비스로 대체될 때, 애플리케이션의 다른 부분에 미치는 영향을 최소화할 수 있습니다.
* **테스트 용이성**: Service Stub과 결합하여 테스트를 쉽게 수행할 수 있습니다.

**단점**

* **복잡성 증가**: 모든 외부 자원 접근을 Gateway를 통해 처리하면, Gateway 객체 자체가 복잡해질 수 있습니다.
* **성능 문제**: 모든 요청이 Gateway를 통해 전달되므로, 성능에 영향을 미칠 수 있습니다.

**예시**

다음은 메시지 서비스를 사용하는 Gateway의 예시입니다:

```java
class MessageGateway {
    protected static final String CONFIRM = "CNFRM";

    public void sendConfirmation(String orderID, int amount, String symbol) {
        Object[] args = new Object[]{orderID, new Integer(amount), symbol};
        send(CONFIRM, args);
    }

    private void send(String msg, Object[] args) {
        int returnCode = doSend(msg, args);
        if (returnCode != MessageSender.SUCCESS) {
            throw new IllegalStateException("Unexpected error from messaging system: " + returnCode);
        }
    }

    protected int doSend(String msg, Object[] args) {
        // 실제 메시지 서비스로 메시지를 전송하는 로직
        return MessageSender.send(msg, args);
    }
}
```

이 Gateway는 메시지 서비스를 사용하여 확인 메시지를 전송하는 역할을 합니다. 여기서는 `sendConfirmation` 메서드를 사용하여 확인 메시지를 전송할 수 있으며, 이 메서드는 내부적으로 메시지 서비스를 호출합니다. 이 패턴을 사용하면 실제 메시지 서비스와의 상호작용을 Gateway를 통해 캡슐화하여 애플리케이션 코드의 단순성과 유연성을 유지할 수 있습니다.

**Mapper (매퍼)**

**Mapper**는 두 독립적인 객체 간의 통신을 설정하는 객체입니다. 이는 서로 독립적인 두 서브시스템 간의 의존성을 제거하기 위해 사용됩니다. 예를 들어, 데이터베이스와 도메인 모델 사이의 매핑을 처리하는 **Data Mapper**(데이터 매퍼)가 이 패턴의 일반적인 예시입니다. Mapper 패턴은 데이터나 객체를 다른 시스템이나 계층으로 변환하는 데 사용됩니다.

**작동 방식**

Mapper는 중간 계층으로서, 두 서브시스템 간의 통신을 관리합니다. 이를 통해 두 서브시스템은 서로의 존재를 인식하지 않으면서도 필요한 데이터를 주고받을 수 있습니다. Mapper는 주로 데이터를 변환하거나, 데이터를 서로 다른 구조로 매핑하는 역할을 합니다. 예를 들어, 데이터베이스의 테이블 구조를 객체 지향 프로그래밍의 객체로 변환하는 작업을 Mapper가 수행할 수 있습니다.

**장점**

* **결합도 감소**: 시스템의 각 부분 간의 결합도를 낮추어 시스템의 유연성을 높입니다.
* **유지보수성 향상**: 서브시스템 간의 의존성을 제거하여 시스템을 더 쉽게 유지보수할 수 있습니다.

**단점**

* **복잡성**: Mapper는 Gateway보다 더 복잡하며, 이를 구현하고 유지보수하는 데 더 많은 노력이 필요합니다.

**예시**

예를 들어, 데이터베이스에서 가져온 결과를 도메인 객체로 변환하기 위해 다음과 같은 Mapper를 사용할 수 있습니다:

```java
class EmployeeMapper {
    public Employee map(ResultSet rs) {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setName(rs.getString("name"));
        employee.setSalary(rs.getBigDecimal("salary"));
        return employee;
    }
}
```

이 예시에서는 데이터베이스에서 조회된 결과(`ResultSet`)를 도메인 객체인 `Employee`로 변환하는 매퍼를 구현하였습니다. 이 Mapper를 사용하면 데이터베이스와 도메인 모델 간의 데이터를 쉽게 변환할 수 있습니다.

**Layer Supertype (레이어 슈퍼타입)**

**Layer Supertype**은 레이어 내의 모든 객체의 슈퍼타입 역할을 하는 타입입니다. 동일한 레이어에 속하는 객체들이 공통적으로 필요로 하는 메서드나 필드를 중복 작성하지 않고, 단일 슈퍼타입에 모아놓는 방식입니다.

**작동 방식**

Layer Supertype 패턴은 간단하게, 특정 레이어 내의 모든 객체들이 공통적으로 필요로 하는 기능을 하나의 슈퍼타입으로 묶는 것입니다. 예를 들어, 도메인 객체 레이어에서 모든 객체가 공통으로 ID 필드를 가지고 있다면, 이 ID 필드를 슈퍼타입에 정의하여 모든 객체가 상속받도록 할 수 있습니다.

**장점**

* **중복 코드 제거**: 공통 기능을 한 곳에 모아놓음으로써 코드의 중복을 줄일 수 있습니다.
* **코드 관리 용이성**: 모든 객체가 공통적으로 사용하는 기능을 중앙에서 관리할 수 있어, 코드의 유지보수가 용이해집니다.

**단점**

* **너무 일반화될 수 있음**: 너무 많은 기능을 슈퍼타입에 넣으면 오히려 불필요한 복잡성을 초래할 수 있습니다.

**예시**

다음은 도메인 객체에 대한 Layer Supertype의 예시입니다:

```java
class DomainObject {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
```

이 예시에서는 `DomainObject`라는 슈퍼타입을 정의하여, 모든 도메인 객체가 공통적으로 사용할 ID 필드를 정의하였습니다. 이를 통해 도메인 객체들은 이 필드를 상속받아 사용할 수 있으며, 중복 코드를 줄일 수 있습니다.

**Separated Interface (분리된 인터페이스)**

**Separated Interface**는 인터페이스를 구현과 분리하여 별도의 패키지에 정의하는 패턴입니다. 이 패턴을 사용하면 클라이언트 코드가 특정 구현에 의존하지 않고 인터페이스만을 사용하게 할 수 있습니다. 이를 통해 시스템의 유연성을 높이고, 다양한 구현을 쉽게 교체할 수 있습니다.

**작동 방식**

Separated Interface 패턴에서는 인터페이스와 구현을 서로 다른 패키지에 배치합니다. 이렇게 함으로써 클라이언트 코드가 구현에 대한 의존성을 가지지 않도록 합니다. 클라이언트 코드가 인터페이스

만을 사용하게 되어, 구현을 변경해야 할 때 클라이언트 코드를 수정할 필요가 없습니다. 이 패턴은 인터페이스와 구현 간의 결합도를 줄이고, 시스템을 더 모듈화할 수 있습니다.

**장점**

* **유연성**: 구현과 인터페이스를 분리함으로써 코드의 유연성을 높일 수 있습니다.
* **결합도 감소**: 인터페이스에만 의존하게 함으로써 구현을 쉽게 교체하거나 확장할 수 있습니다.

**단점**

* **복잡성 증가**: 인터페이스와 구현을 분리하여 관리해야 하므로 초기 설계와 관리가 복잡할 수 있습니다.

**예시**

다음은 데이터베이스에 접근하는 코드를 인터페이스와 구현으로 분리한 예시입니다:

```java
// 인터페이스 정의
package com.example.data;

public interface UserRepository {
    User findUserById(Long id);
}

// 인터페이스의 구현
package com.example.data.impl;

public class UserRepositoryImpl implements UserRepository {
    public User findUserById(Long id) {
        // 데이터베이스 접근 로직
    }
}
```

이 예시에서는 `UserRepository`라는 인터페이스를 정의하고, 이를 구현한 `UserRepositoryImpl` 클래스를 별도의 패키지에 배치했습니다. 클라이언트 코드에서는 `UserRepository` 인터페이스만을 의존하게 되어, 구현을 변경하더라도 클라이언트 코드를 수정할 필요가 없습니다.

**Registry (레지스트리)**

**Registry**는 다른 객체들이 공통적으로 사용하는 객체나 서비스를 찾을 수 있는 잘 알려진 객체입니다. 시스템에서 전역적으로 접근해야 하는 객체나 서비스를 관리하고 제공하는 역할을 합니다.

**작동 방식**

Registry 패턴에서는 시스템 내에서 자주 사용되는 객체나 서비스를 중앙에서 관리합니다. 정적 메서드를 통해 이러한 객체나 서비스에 접근할 수 있도록 하며, 필요에 따라 특정 스코프(프로세스, 스레드, 세션 등)에 따라 다른 Registry를 구현할 수 있습니다. 예를 들어, 스레드마다 다른 데이터베이스 연결을 사용할 수 있도록 스레드 로컬(thread-local) 레지스트리를 구현할 수 있습니다.

**장점**

* **공통 서비스 관리**: 공통적으로 사용하는 객체나 서비스를 중앙에서 관리할 수 있어 일관성을 유지할 수 있습니다.
* **유연성**: 스코프에 따른 다양한 레지스트리를 구현할 수 있어, 여러 실행 컨텍스트에 맞게 데이터를 관리할 수 있습니다.

**단점**

* **전역 상태 관리**: 전역 데이터를 관리하므로, 동시성 문제가 발생할 수 있으며, 복잡한 동기화 코드가 필요할 수 있습니다.
* **디버깅 어려움**: 전역 상태로 인해 오류 발생 시 문제를 추적하고 해결하는 것이 어려울 수 있습니다.

**예시**

다음은 단순한 싱글톤 레지스트리의 예시입니다:

```java
public class Registry {
    private static Registry soleInstance = new Registry();

    private PersonFinder personFinder = new PersonFinder();

    public static PersonFinder personFinder() {
        return soleInstance.personFinder;
    }
}
```

이 예시에서는 `Registry` 클래스를 사용하여 `PersonFinder` 객체를 전역적으로 관리합니다. 이처럼 전역 객체를 관리하기 위한 간단한 패턴으로 Registry를 사용할 수 있습니다.

**Value Object (값 객체)**

**Value Object**는 동등성을 아이덴티티가 아닌 값에 기반하여 판단하는 작은 객체입니다. 이 패턴은 객체의 값 자체가 중요한 경우에 사용되며, 값 객체는 보통 불변 객체(immutable object)로 설계됩니다.

**작동 방식**

Value Object는 그 값 자체로 동등성을 판단합니다. 동일한 값이면 동일한 객체로 취급되며, 값이 변경되지 않도록 불변 객체로 설계하는 것이 일반적입니다. 이렇게 하면 값 객체를 여러 곳에서 참조하더라도 값이 변하지 않아 안전하게 사용할 수 있습니다.

**장점**

* **안정성**: 불변 객체로 설계하여 값의 변경으로 인한 부작용을 방지할 수 있습니다.
* **간단한 비교**: 값에 기반한 동등성 판단이 가능하여, 복잡한 동등성 비교 로직을 간단히 처리할 수 있습니다.

**단점**

* **성능 문제**: 복잡한 객체가 아닌 작은 객체에 적합하며, 큰 데이터 구조에서는 성능 문제를 초래할 수 있습니다.

**예시**

다음은 금액을 표현하는 Value Object인 `Money` 클래스의 예시입니다:

```java
public class Money {
    private final long amount;
    private final Currency currency;

    public Money(long amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount == money.amount && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
```

이 `Money` 클래스는 금액과 통화를 값으로 가지며, 동등성 비교 시 객체의 참조가 아닌 값 자체를 비교합니다. 이는 `Money` 객체가 동일한 금액과 통화를 가지면 동일한 객체로 취급된다는 의미입니다.

**Money (금액)**

**Money**는 금전적 가치를 표현하는 객체로, 다양한 통화와 연산을 안전하게 처리할 수 있도록 합니다. 이 클래스는 통화와 금액을 속성으로 가지며, 금전 연산 시 발생할 수 있는 다양한 문제를 방지합니다.

**작동 방식**

Money 클래스는 금액(`amount`)과 통화(`currency`)를 필드로 가지며, 금전 연산에 필요한 다양한 메서드를 제공합니다. 이 클래스는 통화에 따른 연산 오류를 방지하고, 올바른 라운딩 처리와 분배 알고리즘을 포함하여 금전 연산이 정확하게 이루어지도록 합니다.

예를 들어, 두 Money 객체를 더하거나 빼는 연산에서 서로 다른 통화를 사용할 때는 오류를 발생시키거나, 자동으로 환율을 적용하는 로직을 포함할 수 있습니다. 또한, 금액을 정수로 저장하여 부동소수점 연산에서 발생할 수 있는 문제를 방지합니다.

**장점**

* **안전한 금전 연산**: 다양한 통화를 안전하게 관리하고, 정확한 금전 연산을 지원합니다.
* **라운딩 처리**: 금액을 정확하게 분배하고 라운딩하는 로직을 제공하여, 소수점 이하의 금액을 정확히 처리할 수 있습니다.

**단점**

* **복잡성**: 모든 금전적 연산에서 통화 단위를 고려해야 하므로, 연산이 복잡해질 수 있습니다.
* **성능 문제**: 금전 연산이 많은 경우, 성능 문제가 발생할 수 있습니다.

**예시**

다음은 금액을 관리하는 `Money` 클래스의 예시입니다:

```java
public class Money {
    private final long amount;
    private final Currency currency;

    public Money(long amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        assertSameCurrencyAs(other);
        return new Money(amount + other.amount, currency);
    }

    public Money subtract(Money other) {
        assertSameCurrencyAs(other);
        return new Money(amount - other.amount, currency);
    }

    private void assertSameCurrencyAs(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("다른 통화는 연산할 수 없습니다.");
        }
    }
    
    // 동등성 비교 메서드, 해시코드 메서드 등
}
```

이 `Money` 클래스는 통화와 금액을 속성으로 가지며, 두 Money 객체를 더하거나 빼는 연산에서 동일한 통화인지 확인합니다. 이를 통해 다른 통화 간의 잘못된 연산을 방지할 수 있습니다.

**Special Case (특수 케이스)**

**Special Case**는 특정 경우에 대한 특별한 동작을 제공하는 서브클래스입니다. 예를 들어, 객체가 `null`인 경우와 같이 일반적으로 예외적인 상황에서 별도의 로직을 제공하여 코드의 중복을 줄이고, 예외적인 상황을 처리하는 로직을 단순화할 수 있습니다.

**작동 방식**

Special Case 패턴에서는 일반 객체와 동일한 인터페이스를 가진 특수한 객체를 생성하여, 특정 상황에 대한 특별한 행동을 구현합니다. 예를 들어, 고객 객체가 `null`인 경우 `Null Customer` 객체를 반환하여, `null` 체크를 하지 않고도 안전하게 코드를 실행할 수 있습니다.

**장점**

* **코드 중복 감소**: 예외적인 상황에 대한 처리 로직을 중앙화하여 코드의 중복을 줄

일 수 있습니다.

* **안전성**: `null`과 같은 예외적인 상황을 안전하게 처리할 수 있습니다.

**단점**

* **복잡성**: 모든 경우에 대해 Special Case를 생성하면 코드가 복잡해질 수 있습니다.
* **오버헤드**: 불필요한 Special Case 객체가 많이 생성될 경우, 시스템 자원을 낭비할 수 있습니다.

**예시**

다음은 `null` 객체를 처리하기 위한 Special Case의 예시입니다:

```java
public class Customer {
    private String name;

    public String getName() {
        return name;
    }
}

public class NullCustomer extends Customer {
    @Override
    public String getName() {
        return "Null Customer";
    }
}
```

이 예시에서 `Customer` 객체가 `null`일 가능성이 있는 경우, `Null Customer` 객체를 사용하여 `null` 체크 없이 안전하게 코드를 실행할 수 있습니다. 이렇게 하면, `null` 객체에 대한 처리 로직을 간단하게 만들 수 있습니다.

**Plugin (플러그인)**

**Plugin** 패턴은 컴파일 시가 아닌 구성 시에 클래스를 연결하는 패턴입니다. 런타임 환경에 따라 다양한 구현을 연결해야 하는 경우 사용됩니다. 예를 들어, 개발 환경과 운영 환경에서 다른 데이터베이스를 사용해야 할 때, 이 패턴을 사용하여 코드 변경 없이 구성만으로 필요한 구현을 연결할 수 있습니다.

**작동 방식**

Plugin 패턴에서는 런타임에 구현을 동적으로 선택하고 연결할 수 있도록 구성 파일 등을 사용하여 인터페이스와 구현을 연결합니다. 이를 통해 시스템은 다양한 환경에서 쉽게 구현을 변경할 수 있으며, 구성 파일이나 설정을 통해 새로운 구현을 추가할 수 있습니다.

**장점**

* **유연성**: 런타임에 구현을 선택할 수 있으므로, 다양한 환경에 쉽게 대응할 수 있습니다.
* **구성 관리**: 구성 파일이나 설정을 사용하여 구현을 관리할 수 있으므로, 시스템의 확장성과 관리 용이성이 높아집니다.

**단점**

* **구성 관리의 복잡성**: 구성 파일이나 설정 관리가 복잡해질 수 있으며, 구성 오류 시 문제를 해결하기 어렵습니다.
* **성능 문제**: 런타임에 구현을 동적으로 선택하기 때문에, 성능 문제가 발생할 수 있습니다.

**예시**

다음은 ID 생성기를 사용하는 Plugin 패턴의 예시입니다:

```java
// ID 생성기를 위한 인터페이스 정의
public interface IdGenerator {
    Long nextId();
}

// 개발 환경에서 사용할 구현
public class InMemoryIdGenerator implements IdGenerator {
    private long id = 0;

    @Override
    public Long nextId() {
        return id++;
    }
}

// 운영 환경에서 사용할 구현
public class DatabaseIdGenerator implements IdGenerator {
    @Override
    public Long nextId() {
        // 데이터베이스에서 ID를 생성하는 로직
    }
}

// 구성 파일을 통해 구현을 선택하는 플러그인 팩토리
public class PluginFactory {
    private static Properties props = new Properties();

    static {
        try {
            props.load(PluginFactory.class.getResourceAsStream("/plugin.properties"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Object getPlugin(Class<?> iface) {
        String implName = props.getProperty(iface.getName());
        if (implName == null) {
            throw new RuntimeException("구현을 찾을 수 없습니다: " + iface.getName());
        }
        try {
            return Class.forName(implName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("플러그인을 생성할 수 없습니다: " + iface.getName(), e);
        }
    }
}
```

이 예시에서는 `IdGenerator`라는 인터페이스를 정의하고, 이를 구현한 `InMemoryIdGenerator`와 `DatabaseIdGenerator`를 제공합니다. `PluginFactory`는 구성 파일에서 구현을 읽어와 런타임에 적절한 구현을 반환합니다. 이를 통해 개발 환경에서는 인메모리 구현을, 운영 환경에서는 데이터베이스 구현을 사용할 수 있습니다.

**Service Stub (서비스 스텁)**

**Service Stub**은 테스트 시에 실제 서비스 대신 사용되는 모의 객체(Mock Object)입니다. 이는 외부 서비스에 의존하지 않고, 테스트를 빠르고 안정적으로 수행할 수 있도록 도와줍니다. 예를 들어, 외부의 세금 계산 서비스나 결제 서비스가 실제로 연결되지 않은 상황에서도 테스트를 수행할 수 있습니다.

**작동 방식**

Service Stub 패턴에서는 실제 서비스에 접근하는 Gateway를 인터페이스로 정의하고, 테스트 환경에서는 이를 구현한 Service Stub을 사용합니다. Service Stub은 실제 서비스와 동일한 인터페이스를 구현하지만, 간단한 로직을 사용하여 응답을 제공합니다. 이렇게 하면 외부 서비스에 의존하지 않고도 애플리케이션을 테스트할 수 있습니다.

**장점**

* **테스트 용이성**: 외부 서비스에 의존하지 않고, 빠르고 안정적인 테스트를 수행할 수 있습니다.
* **개발 생산성 향상**: 외부 서비스가 아직 개발 중이거나 사용할 수 없는 상황에서도 개발을 계속할 수 있습니다.

**단점**

* **테스트와 실제 환경의 차이**: Service Stub이 실제 서비스와 완전히 동일하지 않기 때문에, 테스트 결과가 실제 환경에서 달라질 수 있습니다.
* **추가 작업 필요**: Service Stub을 구현하고 관리하는 데 추가적인 노력이 필요합니다.

**예시**

다음은 세금 계산 서비스를 위한 Service Stub의 예시입니다:

```java
// 세금 서비스 인터페이스 정의
public interface TaxService {
    TaxInfo getSalesTaxInfo(String productCode, Address addr, Money saleAmount);
}

// 실제 세금 서비스의 스텁 구현
public class StubTaxService implements TaxService {
    @Override
    public TaxInfo getSalesTaxInfo(String productCode, Address addr, Money saleAmount) {
        // 단순히 고정된 세율을 반환하는 스텁
        return new TaxInfo(new BigDecimal("0.05"), saleAmount.multiply(new BigDecimal("0.05")));
    }
}
```

이 예시에서는 `TaxService` 인터페이스를 정의하고, 실제 세금 계산 서비스의 동작을 모방하는 `StubTaxService`를 구현하였습니다. 테스트 시에는 이 스텁을 사용하여 외부 세금 계산 서비스에 의존하지 않고 애플리케이션을 테스트할 수 있습니다.

**Record Set (레코드 세트)**

**Record Set**은 테이블 형식의 데이터를 메모리 내에서 표현하는 패턴입니다. 이 패턴은 관계형 데이터베이스의 쿼리 결과와 같은 데이터를 메모리에 유지하면서, 비즈니스 로직에서 해당 데이터를 조작할 수 있도록 합니다.

**작동 방식**

Record Set은 보통 데이터베이스에서 쿼리된 결과를 메모리에 유지하며, 데이터를 조작할 수 있도록 합니다. 예를 들어, JDBC의 `ResultSet`이나 ADO.NET의 `DataSet`이 Record Set의 예시입니다. 이러한 Record Set은 단순히 쿼리 결과를 유지하는 역할을 하는 것이 아니라, 비즈니스 로직에 따라 데이터를 수정하고, 다시 데이터베이스에 커밋할 수 있는 기능을 제공합니다.

**장점**

* **데이터 조작의 유연성**: 메모리에 데이터를 유지하면서, 비즈니스 로직에 따라 데이터를 쉽게 조작할 수 있습니다.
* **UI와의 연계**: UI와 쉽게 연계되어 데이터를 표시하거나 수정할 수 있습니다.

**단점**

* **메모리 사용량**: 대규모 데이터를 메모리에 유지해야 하므로, 메모리 사용량이 증가할 수 있습니다.
* **복잡성**: Record Set을 효과적으로 사용하려면 데이터를 조작하고 동기화하는 복잡한 로직이 필요할 수 있습니다.

**예시**

다음은 JDBC의 `ResultSet`을 사용하는 Record Set의 예시입니다:

```java
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "user", "password");
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM employees");

while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");
    BigDecimal salary = rs.getBigDecimal("salary");
    // 데이터를 조작하거나 처리하는 로직
}

rs.close();
stmt.close();
conn.close();
```

이 예시에서는 `ResultSet`을 사용하여 데이터베이스에서 조회된 데이터를 메모리에 유지하고, 이를 조작하거나 처리하는 로직을 구현합니다. 이를 통해 데이터베이스와의 상호작용을 단순화하고, 비즈니스 로직에 따라 데이터를 유연하게 처리할 수 있습니다.
