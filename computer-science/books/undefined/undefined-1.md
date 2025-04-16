# 객체지향 프로그래밍 패러다임 이해하기

### 객체지향 프로그래밍 패러다임 이해하기

객체지향 프로그래밍(Object-Oriented Programming, OOP)은 현재 가장 널리 쓰이는 프로그래밍 패러다임이다. 절차적 프로그래밍, 함수형 프로그래밍과 함께 대표적인 패러다임으로 언급되지만, 특히 객체지향은 복잡한 대규모 소프트웨어 개발에서 강력한 장점을 발휘한다.

객체지향 프로그래밍의 핵심은 클래스(Class)와 객체(Object)를 기본 구성 단위로 사용한다는 점이다.

***

### 객체지향 프로그래밍 vs 객체지향 프로그래밍 언어

> **객체지향 프로그래밍**이란 클래스와 객체를 코드 구성의 단위로 사용하는 프로그래밍 스타일을 의미한다.\
> **객체지향 프로그래밍 언어**는 이러한 클래스와 객체 문법을 지원하는 프로그래밍 언어이다.

대표적인 객체지향 프로그래밍 언어로는 다음과 같은 언어들이 있다.

* **Java, C++, Python, Kotlin, Ruby, Scala** 등

> 단, 언어가 객체지향 문법을 지원한다고 해서 코드가 항상 객체지향적이지는 않다. (절차적 방식으로도 작성 가능)

***

### 객체지향 프로그래밍의 네 가지 특성

객체지향 프로그래밍은 다음의 네 가지 특성을 주로 언급한다.

* **캡슐화 (Encapsulation)**
* **추상화 (Abstraction)**
* **상속 (Inheritance)**
* **다형성 (Polymorphism)**

각 특성의 정의와 코드를 살펴보자.

***

#### 캡슐화 (Encapsulation)

캡슐화는 정보 은닉을 통해 객체의 상태를 보호하는 개념이다. Java에서는 접근 제어자(Access Modifier)로 이를 구현한다.

```java
java복사편집public class Wallet {
  private String id;
  private BigDecimal balance;

  public Wallet(String id) {
    this.id = id;
    this.balance = BigDecimal.ZERO;
  }

  public void deposit(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("금액 오류");
    }
    balance = balance.add(amount);
  }

  public BigDecimal getBalance() {
    return balance;
  }
}
```

* 클래스 외부에서 상태(`balance`)에 직접 접근하지 못하도록 보호한다.

***

#### 추상화 (Abstraction)

추상화는 복잡한 내부 구현을 숨기고, 객체의 핵심적인 기능만 외부로 노출시키는 것이다.\
주로 인터페이스와 추상 클래스를 이용해 구현한다.

```java
java복사편집public interface Storage {
  void save(String data);
}

public class FileStorage implements Storage {
  @Override
  public void save(String data) {
    // 파일에 저장하는 로직 구현
  }
}
```

* 사용자는 `Storage`라는 인터페이스만 알면 내부 구현을 몰라도 이용 가능하다.

***

#### 상속 (Inheritance)

상속은 클래스 간의 is-a 관계를 표현하며, 중복 코드를 제거하고 재사용성을 높인다.

```java
java복사편집public class Animal {
  public void sound() {
    System.out.println("소리낸다");
  }
}

public class Dog extends Animal {
  @Override
  public void sound() {
    System.out.println("멍멍");
  }
}
```

* 하위 클래스가 상위 클래스의 속성과 메서드를 재사용하거나 재정의할 수 있다.

***

#### 다형성 (Polymorphism)

다형성은 동일한 인터페이스를 통해 여러 다른 클래스의 객체를 다룰 수 있게 하는 기능이다.

```java
java복사편집public interface Shape {
  void draw();
}

public class Circle implements Shape {
  public void draw() { System.out.println("원 그리기"); }
}

public class Square implements Shape {
  public void draw() { System.out.println("사각형 그리기"); }
}

public class Demo {
  public void drawShape(Shape shape) {
    shape.draw();
  }
}
```

* 동일 메서드 호출(`draw()`)이 실제 객체 타입에 따라 다르게 작동한다.

***

### 객체지향 분석 및 설계 (OOP 분석 및 설계)

객체지향 소프트웨어 개발은 다음의 단계로 구성된다.

* **객체지향 분석(OA)**: 요구사항 분석 (무엇을 할지)
* **객체지향 설계(OD)**: 클래스 정의, 클래스 간 상호 작용 정의 (어떻게 할지)
* **객체지향 프로그래밍(OOP)**: 설계된 클래스를 실제 코드로 구현

예를 들어 인증 기능을 설계할 때는 다음과 같은 프로세스로 진행한다.

1. 요구사항 명세 명확화 (토큰 생성, 검증 방식 정하기)
2. 책임과 클래스 정의 (`AuthToken`, `ApiRequest`, `CredentialStorage`)
3. 클래스의 속성 및 메서드 정의
4. 클래스 간 관계 설정 (인터페이스로 추상화)

***

### 객체지향 vs 절차적 프로그래밍 vs 함수형 프로그래밍

#### 절차적 프로그래밍 (Procedural Programming)

* 데이터와 메서드가 분리된 상태로, 함수를 중심으로 코드를 작성한다.
* 대표 언어: C, Pascal 등

```c
c복사편집void print_user(User user) {
  printf("이름:%s 나이:%d\n", user.name, user.age);
}
```

#### 객체지향 프로그래밍 (Object-Oriented Programming)

* 데이터와 메서드를 클래스라는 단위로 묶어 관리한다.
* 대표 언어: Java, Kotlin, C++

```java
java복사편집class User {
  private String name;
  public void print() {
    System.out.println("이름: " + name);
  }
}
```

#### 함수형 프로그래밍 (Functional Programming)

* 상태 변경이 없는 순수 함수로 구성되어 수학적 표현에 적합하다.
* 대표 언어: Haskell, Scala(일부), Java8+(Stream, Lambda 지원)

```java
java복사편집Stream.of("foo", "bar")
      .map(String::toUpperCase)
      .forEach(System.out::println);
```

***

### 상속 vs 합성

객체지향 설계 시 흔한 고민은 **상속을 쓸지 합성을 쓸지** 결정하는 것이다. 최근 추세는 **상속보다 합성을 더 많이** 쓰는 것을 권장한다.

* **상속**은 강력하지만, 클래스 간 결합도가 높아져 변경이 어려워진다.
* **합성**은 인터페이스와 위임을 활용해 클래스 간 결합도를 낮추고 유연성을 높인다.

**상속 예시:**

```java
java복사편집class Bird {
  void fly() { System.out.println("날다"); }
}

class Sparrow extends Bird {}
```

**합성 예시(권장):**

```java
java복사편집interface Flyable {
  void fly();
}

class Bird {
  private Flyable flyable;
  
  void fly() {
    flyable.fly();
  }
}
```
