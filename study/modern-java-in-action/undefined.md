# 동작 파라미터화 코드 전달하기

동작 파라미터화란 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록을 의미한다.

이 코드 블록은 나중에 프로그램에서 호출한다. 즉, 코드 블록의 실행은 나중으로 미뤄진다.

결과적으로 코드 블록에 따라 메서드의 동작이 파라미터화 된다.

&#x20;

예를 들어 컬렉션을 처리할 때 다음과 같은 메서드를 구현한다고 가정하자.

* 리스트의 모든 요소에 대해서 '어떤 동작'을 수행할 수 있음
* 리스트 관련 작업을 끝낸 다음에 '어떤 다른 동작'을 수행할 수 있음
* 에러가 발생하면 '정해진 어떤 다른 동작'을 수행할 수 있음

동작 파라미터화로 이처럼 다양한 기능을 수행할 수 있으며, 변화하는 요구사항에 유연하게 대응할 수 있다.



### 2.1 변화하는 요구사항에 대응하기

예제 코드를 점차 개선하면서 유연한 코드를 만드는 작업을 진행해보자.

**2.1.1 첫 번째 시도 : 녹색 사과 필터링**

먼저 녹색 사과만 필터링하는 기능을 추가한다.

```
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getColor() == Color.GREEN) {
        result.add(apple);
      }
    }
    return result;
  }
```

**2.1.2 두 번째 시도 : 색을 파라미터화**

filterGreenApples의 코드를 반복 사용하지 않고 빨간 사과를 필터링하는 메서드를 구현하려면 어떻게 해야할까?

메서드에 파라미터를 추가해서 요구사항에 유연하게 대응할 수 있다.

```
  public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getColor() == color) {
        result.add(apple);
      }
    }
    return result;
  }
```

다음 요구사항으로 무게를 기준으로 필터링하는 메서드도 필요해졌다.

```
  public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getWeight() > weight) {
        result.add(apple);
      }
    }
    return result;
  }
```

하지만 목록을 검색하고, 각 사과에 필터링 조건을 적용하는 부분의 코드가 색 필터링 코드와 대부분 중복된다.

**2.1.3 세 번째 시도 : 가능한 모든 속성으로 필터링**

모든 속성을 추가한 뒤, 어떤 것을 기준으로 필터링할지 가리키는 플래그를 추가할 수 있을 것이다.

```
  public static List<Apple> filterApplesByWeight(List<Apple> inventory, Color color, int weight, boolean flag) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if ((flag && apple.getColor().equals(color)) ||
           (!flag && apple.getWeight() > weight)) {
        result.add(apple);
      }
    }
    return result;
  }
```

하지만 이런식의 코드는 파라미터가 명확하지 않으며, 요구사항이 바뀌었을 때 유연하게 대응할 수도 없다.

요구사항이 복잡해지면 결국 여러 중복된 필터 메서드를 만들거나 모든 것을 처리하는 거대한 하나의 필터 메서드를 구현하게 될 것이다.



#### 2.2 동작 파라미터화

요구사항에 유연하게 대응하기 위해 선택 조건에 따라 불리언 값을 반환하는 방식을 생각해보자.

참 또는 거짓을 반환하는 함수를 프레디케이트라고 한다.

&#x20;

먼저 선택 조건을 결정하는 인터페이스를 정의하자.

```
 public class ApplePredicate {
   boolean test(Apple apple);
 }
```

그러면 다양한 선택 조건을 대표하는 여러 버전의 ApplePredicate를 정의할 수 있다.

```
  public class AppleHeavyWeightPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
      return apple.getWeight() > 150;
    }
  }

  public class AppleGreenColorPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
      return apple.getColor() == Color.GREEN;
    }
  }
```

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/VNuHa/btq6EVNBGw6/wqJHUIpUCQ0of1RjHsok6K/img.png" alt=""><figcaption></figcaption></figure>

위 조건에 따라 filter 메서드가 다르게 동작할 것이라고 예상할 수 있다. 이를 전략 디자인 패턴이라고 한다.

전략 디자인 패턴은 각 알고리즘(전략)을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음 런타임에 선택하는 기법이다.

**2.2.1 네 번째 시도 : 추상적 조건으로 필터링**

filterApples 메서드가 ApplePredicate 객체를 인수로 받도록 고치면,

메서드 내부에서 컬렉션을 반복하는 로직과 컬렉션의 각 요소에 적용할 동작을 분리할 수 있게된다.

```
  public static List<Apple> filter(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }
```

코드/동작 전달하기

이제 필요한 대로 다양한 ApplePredicate를 만들어서 filterApples 메서드로 전달할 수 있다.

새로운 동작을 정의하는 것은 test 메서드이지만, 메서드는 객체만 인수로 받으므로 test 메서드를 ApplePredicate 객체로 감싸서 전달해야한다.&#x20;



#### 2.3 복잡한 과정 간소화

**2.3.1 익명 클래스**

익명 클래스를 이용하면 클래스 선언과 인스턴스화를 동시에 할 수 있다.

즉, 즉석에서 필요한 구현을 만들어서 사용할 수 있다.

**2.3.2 다섯 번째 시도 : 익명 클래스 사용**

```
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
  public boolean test(Apple a) {
    return RED.equals(apple.getColor());
  }
});
```

하지만 익명 클래스도 많은 공간을 차지하며, 많은 프래그래머가 익명 클래스 사용에 익숙하지 않다.

&#x20;

익명 클래스로 인터페이스를 구현하는 여러 클래스를 선언하는 과정은 조금 줄일 수 있지만,

객체를 만들고 명시적으로 새로운 동작을 정의해야하는 메서드를 구현해야 한다는 점은 변하지 않는다.

**2.3.3 여섯 번째 시도 : 람다 표현식 사용**

```
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

람다 표현식을 이용해서 코드를 위와같이 간결하게 재구현할 수 있다.

**2.3.4 일곱 번째 시도 : 리스트 형식으로 추상화**

```
public interface predicate<T> {
  boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
  List<T> result = new ArrayList<>();
  for(T e : list) {
    if(p.test(e)) {
      result.add(e);
    }
  }
  return result;
}

List<Apple> redApples = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
List<Apple> evenNumers = filter(numbers, (Integer i) -> i % 2 == 0);
```

이제 클래스, 정수, 문자열 등의 리스트에 필터 메서드를 사용할 수 있다.



#### 2.4 실전 예제

**2.4.1 Comparator로 정렬하기**

컬렉션 정렬은 반복되는 프로그래밍 작업이다.

자바 8에서는 java.util.Comparator 객체를 이용해 List에 있는 sort의 동작을 파라미터화 할 수 있다.

```
// java.util.Comparator
public interface Comparator<T> {
  int compare(T o1, T 02);
}
```

comparator를 구현해 sort 메서드의 동작을 다양화할 수 있다.

```
inventory.sort(new Comparator<Apple>() {
  public int compare(Apple a1, Apple a2) {
    return a1.getWeight().compareTo(a2.getWeight());
  }
});
```

그리고 람다 표현식을 이용해 다음과 같이 간단하게 코드를 구현할 수 있다.

```
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

**2.4.2 Runnable로 코드 블록 실행하기**

자바 8까지는 Thread 생성자에 객체만을 전달할 수 있었으므로 보통 결과를 반환하지 않는 void run 메소드를 포함하는 익명 클래스가 Runnable 인터페이스를 구현하도록 하는 것이 일반적인 방법이었다.

```
//java.lang.Runnable
public interface Runnable {
  void run();
}

Thread t = new Thread(new Runnable() {
  public void run() {
    System.out.println("hello world");
  }
});
```

람다 표현식을 이용하면 다음처럼 스레드 코드를 구현할 수 있다.

```
Thread t = new Thread(() -> System.out.println("Hello world"));
```

**2.4.3 Callable로 결과 반환하기**

ExecutorService 인터페이스를 이용하면 태스크를 스레드 풀로 보내고 결과를 Future로 저장할 수 있다.

```
// java.util.concurrent.Callable
public interface Callabble<V> {
  V call();
}
```

아래 코드와 같이 실행 서비스에 태스크를 제출해서 위 코드를 활용할 수 있다.

```
ExecutorService executorService = Executors.newCachedThreadPool();
Future<String> threadName = executorService.submit(new Callable<String>() {
  @Override public String call() throws Exception {
    return Thread.currentThread().getName();
  }
});
```

람다를 이용해 다음과 같이 줄일 수 있다.

```
Future<String> threadName = executorService.submit(() -> Thread.currentThread().getName());
```
