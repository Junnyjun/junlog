# 제너릭, 열거형, 애너테이션

### 1. 제너릭(Generics) <a href="#1-generics" id="1-generics"></a>

다양한 타입의 객체들을 다루는 메서드나 컬렉션 클래스에 컴파일 시의 타입 체크를 해주는 기능이다.

#### 장점 <a href="#undefined" id="undefined"></a>

1. 타입 안정성을 제공한다.
2. 타입체크와 형변환을 생략할 수 있으므로 코드가 간결해 진다.

#### &#x20;선언 <a href="#undefined" id="undefined"></a>

```java
class Box<T> {
	T item;
    
    void setItem(T item){ this.item = item; }
    T getItem(){ return item; }
}
```

타입문자는 T(type)말고도 E(element)나 K(key),V(value)도 쓰이기도 한다.\
기호의 종류만 다를 뿐 '임의의 참조형 타입'을 의미한다는 것은 모두 같다.

> Box\<T> : 지네릭 클래스. 'T의 Box' 또는 'T Box'라 읽는다.\
> T : 타입 변수 또는 타입 매개변수.(T는 타입 문자)\
> Box : 원시 타입(raw type)

#### 제한 <a href="#undefined" id="undefined"></a>

static멤버에 타입 변수 T를 사용할 수 없다.\
static멤버는 대입된 타입의 종류에 관계없이 동일한 것이어야하기 때문이다.

타입의 배열을 생성하는거은 허용되지 않는다.\
new 연산자는 컴파일 시점에 타입 T가 뭔지 정확히 알아야 한다.

#### 와일드 카드 <a href="#undefined" id="undefined"></a>

\<? extends T> : 와일드 카드의 상한 제한. T와 그 자손들만 가능.\
\<? super T> : 와일드 카드의 상한 제한. T와 그 조상들만 가능.\
\<?> : 제한 없음. 모든 타입이 가능. \<? extends Object>와 동일

#### 메서드 <a href="#undefined" id="undefined"></a>

메서드 선언부에 제너릭 타입이 선언된 메서드 이다.\
`static <T> void sort(List<T> list, Comparator<? super T> c)`

클래스에 정의된 타입 매개변수와 제너릭 메서드에 정의된 타입 매개변수는 전혀 별개의 것이다.

메서드에 선언된 타입 매개변수는 매서드 내에서만 지역적으로 사용될 것이므로 메서드가 static이건 아니건 상관이 없다.

#### 타입의 형변환 <a href="#undefined" id="undefined"></a>

제너릭 타입과 넌제너릭 타입 -> 가능은 하지만 경고 발생\
제너릭 타입과 제너릭 타입 -> 불가능\
다만, 와일드 카드를 이용하면 가능

#### 타입의 제거 <a href="#undefined" id="undefined"></a>

이전의 소스코드와의 호환성을 위해 컴파일러는 제너릭 타입을 이용해서 소스파일을 체크하고, 필요한 곳에 형변환을 넣어주고 제너릭 타입을 제거한다.

1. 제너릭 타입의 경계를 제거한다.
2. 제너릭 타입을 제거한 후에 타입이 일치하지 않으면, 형변환을 추가한다.



### 2. 열거형(enums) <a href="#2-enums" id="2-enums"></a>

서로 관련된 상수를 편리하게 선언하기 위한 것이다.

C언어에서는 타입이 달라도 값이 같으면 조건식 결과가 참이었는데, 자바의 열거형은 실제 값이 같아도 타입이 다르면 컴파일 에러가 발생한다.

#### 열거형의 정의와 사용 <a href="#undefined" id="undefined"></a>

`enum 열거형이름 { 상수명1, 상수명2, ... }`

#### 열거형에 멤버 추가하기 <a href="#undefined" id="undefined"></a>

지정된 값을 저장할 수 있는 인스턴스 변수와 생성자를 추가해야 한다.

먼저 열거형 상수를 모두 정의한 다음에 다른 멤버들을 추가해야 한다.

```java
enum Direction {
	EAST(1, ">"), SOUTH(2, "V"), WEST(3, "<"), NORTH(4, "^");
    
    private final int value;
    private final String symbol;
    
    Direction(int value, String symbol) {
    	this.value = value;
		this.symbol = symbol;
    }
}
```

#### 열거형에 추상 메서드 추가하기 <a href="#undefined" id="undefined"></a>

열거형에 추상 메서드를 선언하면 각 열거형 상수가 이 추상 메서드를 반드시 구현해야 한다.



### 3. 애너테이션(annotation) <a href="#3-annotation" id="3-annotation"></a>

프로그램의 소스코드 안에 다른 프로그램을 위한 정보를 미리 약속된 형식으로 포함시킨 것

애너테이션은 JDK에서 기본적으로 제공하는 것과 다른 프로그램에서 제공하는 것들이 있는데, 어느 것이든 그저 약속된 형식으로 정보를 제공하기만 함녀 될 뿐이다.

JDK에서 제공하는 표준 애너테이션은 주로 컴파일러를 위한 것으로 컴파일러에게 유용한 정보를 제공한다. 그리고 새로운 애너테이션을 정의할 때 메타 애너테이션을 제공한다.

#### 표준 애너테이션 <a href="#undefined" id="undefined"></a>

| 애너테이션                | 설명                                   |
| -------------------- | ------------------------------------ |
| @Override            | 컴파일러에게 오버라이딩하는 메서드라는 것을 알린다.         |
| @Deprecated          | 앞으로 사용하지 않을 것을 권장하는 대상에 붙인다.         |
| @SuppressWarnings    | 컴파일러의 특정 경고메시지가 나타나지 않게 해준다.         |
| @SafeVarargs         | 지네릭스 타입의 가변인자에 사용한다.                 |
| @FunctionalInterface | 함수형 인터페이스라는 것을 알린다.                  |
| @Native              | native메서드에서 참조되는 상수 앞에 붙인다.          |
| @Target\*            | 애너테이션이 적용가능한 대상을 지정하는데 사용한다.         |
| @Documented\*        | 애너테이션 정보가 javadoc으로 작성된 문서에 포함되게 한다. |
| @Inherited\*         | 애너테이션이 자손 클래스에 상속되도록 한다.             |
| @Retention\*         | 애너테이션이 유지되는 범위를 지정하는데 사용한다.          |
| @Repeatable\*        | 애너테이션을 반복해서 적용할 수 있게 한다.             |

* \*가 붙은 것은 메타 애너테이션이다.

#### 애너테이션 타입 정의하기 <a href="#undefined" id="undefined"></a>

```java
@interface 애너테이션 이름 {
	타입 요소이름();	//애너테이션의 요소를 선언한다.
    ...
}
```

#### 애너테이션의 요소 <a href="#undefined" id="undefined"></a>

애너테이션 내에 선언된 메서드를 '애너테이션의 요소'라고 한다.

애너테이션의 요소는 반환값이 있고 매개변수는 없는 추상 메서드의 형태를 가지며, 상속을 통해 구현하지 않아도 된다.\
다만, 애너테이션을 적용할 때 이 요소들의 값을 빠짐없이 지정해주어야 한다.

애너테이션의 각 요소는 기본값을 가질 수 있으며, 기본값이 있는 요소는 애너테이션을 적용할 때 값을 지정하지 않으면 기본값이 사용된다.

#### 마커 애너테이션 <a href="#undefined" id="undefined"></a>

요소가 하나도 정의되지 않은 애너테이션을 마커 애너테이션이라고 한다.

#### 애너테이션 요소의 규칙 <a href="#undefined" id="undefined"></a>

* 요소의 탕비은 기본형, String, enum, 애너테이션, Class만 허용된다.
* ()안에 매개변수를 선언할 수 없다.
* 예외를 선언할 수 없다.
* 요소를 타입 매개변수로 정의할 수 없다.
