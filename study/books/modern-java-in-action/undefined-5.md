# 디폴트 메서드

인터페이스를 구현하는 클래스는 인터페이스에서 정의하는 모든 메서드 구현을 제공하거나 슈퍼클래스의 구현을 상속받아야 한다.  하지만 인터페이스가 바뀌면 인터페이스를 구현했던 모든 클래스의 구현도 고쳐야하기 때문에 문제가 된다.

&#x20;

자바 8에서는 이 문제를 해결하기 위해 기본 구현을 포함하는 인터페이스를 정의하는 두 가지 방법을 제공한다.

1. 정적 메서드
2. 디폴트 메서드

디폴트 메서드를 사용하면 자동으로 인터페이스를 구현하는 클래스에 메서드가 상속된다.

다음은 List 인터페이스에 새로 추가된 sort 메서드이다.

```
default void sort(Comparator<? super E> c) {
  Collections.sort(this, c);
}
```

반환형식 void 앞의 default 키워드는 해당 메서드가 디폴트 메서드임을 가리킨다.

#### 13.1 변화하는 API

시간이 지남에 따라 API에 대한 요구사항이 바뀌고 새로운 기능이 추가될 수 있다.

하지만 이미 릴리즈된 인터페이스를 고치면 기본 버전과의 호환성 문제가 발생한다.

&#x20;

바이너리 호환성, 소스 호환성, 동작 호환성

* 바이너리 호환성 - 인터페이스에 메서드를 추가해도 추가된 메서드를 호출하지 않으면 문제가 생기지 않음을 의미한다. 하지만 인터페이스를 구현하는 클래스를 재컴파일하면 에러가 발생한다.
* 소스 호환성 - 코드를 고쳐도 기존 프로그램을 성공적으로 재컴파일 할 수 있음을 의미한다. 인터페이스에 메서드를 추가하면 추가한 메서드를 구현하도록 소스를 고쳐야하기 때문에 소스 호환성이 아니다.
* 동작 호환성 - 코드를 고쳐도 같은 입력 값이 주어지면 같은 동작을 실행한다는 의미이다. 인터페이스가 바뀌더라도 프로그램에서 추가된 메서드를 호출할 일은 없으므로 동작 호환성은 유지된다.

#### 13.2 디폴트 메서드란 무엇인가?

디폴트 메서드는 default 키워드로 시작하며 다른 클래스에 선언된 메서드처럼 메서드 바디를 포함한다.

인터페이스를 구현하는 모든 클래스는 디폴트 메서드의 구현도 상속받으므로 소스 호환성이 유지된다.

&#x20;

추상 클래스와 자바 8의 인터페이스

공통점

* 추상 클래스와 인터페이스는 모두 바디를 포함하는 메서드를 정의할 수 있다.

차이점

* 클래스는 하나의 추상 클래스만 상속받을 수 있지만 인터페이스는 여러개 구현할 수 있다.
* 추상클래스는 인스턴스 변수(필드)로 공통 상태를 가질 수 있지만, 인터페이스는 인스턴스 변수를 가질 수 없다.

#### 13.3 디폴트 메서드 활용 패턴

**13.3.1 선택형 메서드**

인터페이스를 구현할 때 사용하지 않는 인터페이스의 메서드는 빈 구현으로 남겨두는 경우가 많았다.

디폴트 메서드를 이용하면 메서드의 기본 구현을 제공할 수 있으므로 구현 클래스에서 빈 구현을 만들 필요가 없다.

Iterator 인터페이스의 경우 많은 사용자들이 remove 기능을 잘 사용하지 않으므로 default 메서드로 제공한다.

```
interface Iterator<T> {
  boolean hesNext();
  T next();
  default void remove() {
    throw new unsupportedOperationException();
  }
}
```

**13.3.2 동작 다중 상속**

다중 상속 형식

자바에서 클래스는 한 개의 클래스만 상속할 수 있지만 인터페이스는 여러 개 구현할 수 있다.

자바8에서는 인터페이스가 구현을 포함할 수 있으므로 클래스는 여러 인터페이스에서 동작을 상속받을 수 있다.

&#x20;

기능이 중복되지 않은 최소의 인터페이스

```
public interface Rotatable {
  void setRotationAngle(int angleInDegrees);
  int getRotationAngle();
  default void reotateBy(int angleInDegrees) {
    setRotationAngle((getRotationAngle() + angleIndegrees) % 360);
  }
}
```

```
public interface Moveable {
  int getX();
  int getY();
  void setX(int x);
  void setY(int y);
  
  default void moveHorizontally(int distance) {
    setX(getX() + distance);
  }
  
  default void moveVertically(int distance) {
    setY(getY() + distance);
  }
}
```

```
public interface Resizable {
  int getWidth();
  int getHeight();
  void setWidth(int width);
  void setHeight(int height);
  void setAbsoluteSize(int width, int height);
  
  default void setRelativeSize(int wFactor, int hFactor) {
    setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
  }
}
```

위와 같이 회전, 이동, 크기조절 기능을 담당하는 인터페이스를 만들 수 있다.

&#x20;

인터페이스 조합

이제 이들 인터페이스를 조합해서 다양한 클래스를 구현할 수있다.

회전, 이동, 크기조절 기능이 모두 필요하다면 3개의 인터페이스를 모두 구현하면 된다.

회전과 이동이 되야하지만 크기는 조절할 수 없다면 Rotatable, Moveable 인터페이스만 구현하면 된다.

이때 디폴트메서드는 재사용할 수 있기 때문에 복사&붙여넣기를 할 필요가 없다.

&#x20;

옳지 못한 상속

한 개의 메서드를 재사용하려고 100개의 메서드와 필드가 정의되어있는 클래스를 상속받는 것은 좋은 생각이 아니다.

이럴 때는 델리게이션, 즉 멤버 변수를 이용해서 클래스에서 필요한 메서드를 직접 호출하는 메서드를 작성하는 것이 좋다.

디폴트 메서드에도 이 규칙을 적용해서 필요한 기능만 포함하도록 인터페이스를 유지하여 쉽게 기능을 조립할 수 있다.

#### 13.4 해석 규칙

여러 인터페이스로부터 같은 시그니처를 갖는 디폴트 메서드를 상속받는 상황이 생길 수 있다.

자바 8에서는 이러한 문제에 대한 해결규칙을 제공한다.

**13.4.1 알아야 할 세 가지 해결 규칙**

1. 클래스가 항상 이긴다. 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권을 갖는다.
2. 1번 규칙 이외의 상황에서는 서브인터페이스가 이긴다. B가 A를 상속받는다면 B가 이긴다.
3. 여전히 우선순위가 결정되지 않았다면 상속받는 클래스가 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.

**13.4.2 디폴트 메서드를 제공하는 서브인터페이스가 이긴다.**

```
public interface A {
  default void hello() {
    System.out.println("Hello A");
  };
}

public interface B extends A {
    default void hello() {
    System.out.println("Hello B");
  };
}
```

```
public class D implements A {}
public class C Extends D implements B, A {}
  public static void main(String... args) {
    new C().hello();
  }
}
```

위 상황에서 D는 hello를 오버라이드하지 않고 단순히 인터페이스 A를 구현했다. 따라서 D는 인터페이스 A의 디폴트 메서드 구현을 상속받는다.

그러므로 컴파일러는 인터페이스 A의 hello나 B의 hello를 중 하나를 선택해야 하고, B가 A를 상속받는 관계이므로 "Hello B"가 출력된다.

만약 D가 hello 메서드를 오버라이드했다면 규칙 1에 의해 D의 hello가 실행되었을 것이다.

**13.4.3 충돌 그리고 명시적인 문제 해결**

인터페이스 A와 인터페이스 B 간의 상속 관계가 없다면 2번 규칙을 적용할 수 없다. 이때는 직접 사용하려는 메서드를 명시적으로 선택해야 한다.

자바 8에서는 X.super.m(...)형태의 새로운 문법을 제공한다. 여기서 X는 호출하려는 m의 슈퍼인터페이스다.

```
public class C implements B, A {
  void hello() {
    B.super.hello();
  }
}
```

**13.4.4 다이아몬드 문제**

```
public interface A {
  default void hello() {
    System.out.println("Hello A");
  };
}

public interface B extends A { }
public interface C extends A { }
public class Dimplements B, C {}
  public static void main(String... args) {
    new D().hello();
  }
}
```

위 코드에서 D는 B와 C 중 누구의 디폴트 메서드 정의를 상속받을까? 실제로 선택할 수 있는 메서드 선언은 A의 디폴트 메서드 뿐이다. 따라서 결국 출력 결과는 "Hello A"가 된다.

만약 B에도 hello 디폴트 메서드가 있었다면 규칙 2에 따라 B가 선택되었을 것이다.

C에도 있었다면 충돌이 발생하므로 명시적으로 메서드를 호출해야 한다.
