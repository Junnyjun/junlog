# 리팩터링, 테스팅, 디버깅

#### 가독성과 유연성을 개선하는 리팩터링

람다, 메서드 참조, 스트림 등의 기능을 이용해서 더 가독성이 좋고 유연한 코드로 리팩터링하는 방법을 살펴보자.

**9.1.1 코드 가독성 개선**

가독성을 개선한다는 것은 구현한 코드를 다른 사람이 쉽게 이해하고 유지보수할 수 있게 만드는 것을 의미한다.

코드 가독성을 높이려면 코드의 문서화를 잘 하고, 표준 코딩 규칙을 준수하는 등의 노력을 기울여야 한다.

**9.1.2 익명 클래스를 람다 표현식으로 리팩터링하기**

익명클래스를 람다 표현식으로 변환하면 간결하고 가독성 좋은 코드를 구현할 수 있다.

&#x20;

익명클래스를 람다표현식으로 변환할 때 주의점

1\. 익명클래스에서 this는 익명클래스 자신을 가리키지만 람다에서 this는 람다를 감싸는 클래스를 가리킨다.

2\. 익명클래스는 감싸고있는 클래스의 변수를 가릴 수 있지만, 람다 표현식으로는 가릴 수 없다.

3\. 익명클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따른 모호함이 초래될 수 있다.

```
interface Task {
  public void execute();
}
public static void doSomething(Runnable r) { r.run(); }
public static void doSomething(Task a) { r.run(); }

//익명클래스로 전달 가능
doSomethig(new Task() {
  public void execute() {
    System.out.println("Danger danger!!");
  }
});

//Runnable과 Task 모두 대상 형식이 가능하므로 모호함 발생
doSomething(() -> System.out.println("Danger danger!!"));

//명시적 형변환을 사용해서 모호함을 제거할 수 있음
doSomething((Task)() -> System.out.println("Danger danger!!"));
```

**9.1.3 람다 표현식을 메서드 참조로 리팩터링하기**

메서드 참조를 사용하면 메서드명으로 코드의 의도를 명확히 알릴 수 있다.

또한 comparing과 maxBy같은 정적 헬퍼 메서드를 활용하는 것도 좋다.

```
inventory.sort(
  (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())); //비교구현에 신경써야함
inventory.sort(comparing(Apple::getWeight)); //코드가 문제 자체를 설명
```

람다표현식과 저수준 리듀싱 연산을 조합하는것보다 Collectors API를 사용하면 코드의 의도가 더 명확해진다.

```
//람다 + 저수준 리듀싱 사용
int totalaCalories = menu.stream().map(Dish::getCalories)
  .reduce(0, (c1, c2) -> c1 + c2);
  
//내장 컬렉터 사용
int totalaCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

**9.1.4 명령형 데이터 처리를 스트림으로 리팩터링하기**

스트림 API는 데이터 처리 파이프라인의 의도를 더 명확하게 보여준다. 스트림은 쇼트서킷과 게으름이라는 강력한 최적화뿐 아니라 멀티코어 아키텍처를 활용할 수 있는 지름길을 제공한다.

```
List<String> dishNames = new ArrayList<>();
for(Dish dish : menu) {
  if(dish.getCalories() > 300 ) {
    dishNames.add(dish.getName());
  }
}

//스트림 API로 변환하면 더 직접적으로 기술 & 쉽게 병렬화 가능
menu.parallelStream()
  .filter(d -> d.getCaloires() > 300)
  .map(Dish::getName)
  .collect(toList());
```

**9.1.5 코드 유연성 개선**

함수형 인터페이스 적용

람다표현식을 이용하려면 함수형 인터페이스를 코드에 추가해야한다.

&#x20;

조건부 연기 실행

```
if(logger.isLoggale(Log.FINER)) {
  logger.finner("Problem : " + generateDiagnostic());
}
```

위 코드는 logger의 상태가 isLoggable 메서드에 의해 클라이언트 코드로 노출된다. 또한 메시지를 로깅할때마다 logger의 객체를 매번 확인해야 한다.

```
logger.log(Lelvel.FINER, "Problem : " + generateDiagnostic());
```

불필요한 if문을 제거하고 logger의 상태를 노출할 필요도 없어졌지만, logger가 활성화되어 있지 않더라도 항상 로깅 메시지를 평가하게 된다.

```
logger.log(Level.FINER, () ->  "Problem : " + generateDiagnostic());
```

supplier를 인수로 갖는 오버로드된 log 메서드를 통해 위와같이 해결할 수 있다.

다음은 log 메서드의 내부구현 코드이다.

```
public void log(Level level, Supplier<String> msgSupplier) {
  if(logger.isLoggable(level)) {
    log(level, msgSupplier.get()); //람다 실행
  }
}
```

&#x20;

실행 어라운드

매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드가 있다면 람다로 변환할 수 있다.

```
String oneLine = processFile((BufferedReader b) -> b.readline()); //람다 전달
String twoLine = processFile((BufferedReader b) -> b.readline() + b.readline()); //다른 람다 전달

public static String processFile(BufferedReaderProcessor p) throws IOException {
  try(BufferedReader br = new BufferedReader(new fileReader("data.txt"))) {
    return p.process(br); //인수로 전달된 BufferedReaderProcessor 실행
  }
}

public interface BufferedReaderProcessor {
  string process(BufferedReader b) throws IOException;
}
```

#### 9.2 람다로 객체지향 디자인 패턴 리팩터링하기

**9.2.1 전략**

전략패턴은 한 유형의 알고리즘을 보유한 상태에서 런타임에 적절한 알고리즘을 선택하는 기법이다.

```
//String 문자열을 검증하는 인터페이스
public interface ValidationStrategy {
  boolean execute(String s);
}


//인터페이스를 구현하는 클래스
public class IsAllLowerCase implements ValidationStrategy {
  public boolean execute(String s) {
    return s.matches("[a-z]+");
  }
}

public class IsNumeric implements ValidationStrategy {
  public boolean execute(String s) {
    return s.matches("\\d+");
  }
}

//전략객체를 사용하는 클래스
public class Validator {
  private final ValidationStrategy strategy;
  public Validator(ValidationStrategy v) {
    this.strategy = v;
  }
  public boolean validate(String s) {
    return strategy.execute(s);
  }
}

Validator numericValidator = new Validator(new IsNumeric());
boolean b1 = numericValidator.validate("AAA"); //false
Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
boolean b2 = lowerCaseValidator.validate("bbb"); //true
```

&#x20;

람다 표현식 사용

다양한 전략을 구현하는 새로운 클래스를 구현할 필요 없이 람다 표현식을 직접 전달하면 코드가 간결해진다.

```
Validator numericValidator = new Validator((String s) -> s.matches("[a-z]+")); //람다를 직접 전달
boolean b1 = numericValidator.validate("AAA");
Validator lowerCaseValidator = new Validator((String s) -> s.matches("\\d+");
boolean b2 = lowerCaseValidator.validate("bbb");
```

**9.2.2 템플릿 메서드**

알고리즘의 개요를 제시한 다음에 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 템플릿 메서드 디자인 패턴을 사용한다.

```
abstract class OnlineBanking {
  public void processCustomer(int id) {
    Customer c = Database.getCustomerWithId(id);
    makeCustomerHappy(c);
  }
  abstract void makeCustomerHappy(Customer c);
}
```

processCustomer 메서드는 온라인 뱅킹 알고리즘이 해야 할 일을 보여준다.&#x20;

각각의 지점은 OnlineBanking 클래스를 상속받아 makeCustomerHappy 메서드가 원하는 동작을 수행하도록 구현할 수 있다.

&#x20;

람다 표현식 사용

```
//Consumer<Customer> 형식의 두번째 인수를 추가
public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
  Customer c = Database.getCustomerWithId(id);
  makeCustomerHappy.accept(c);
}

//람다표현식을 직접 전달 가능
new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello" + c.getName());
```

**9.2.3 옵저버**

어떤 이벤트가 발생했을 때 한 객체(주제)가 다른 객체 리스트(옵저버)에 자동으로 알림을 보내야하는 상황에서 옵저버 디자인 패턴을 사용한다.

```
//1. 옵저버
//주제가 호출할 수 있도록 notify 메서드 제공
interface Observer {
  void notify(String tweet);
}

Class NYTimes implements Observer {
  public void notify(String tweet) {
    if(tweet != null && tweet.contains("money")) {
      System.out.println("Breaking news in NY!" + tweet);
    }
  }
}

Class Guardian implements Observer {
  public void notify(String tweet) {
    if(tweet != null && tweet.contains("queen")) {
      System.out.println("Yet more news from London..." + tweet);
    }
  }
}

//2. 주제
interface Subject {
  void registerObserver(Observer o);
  void notifyObservers(String tweet);
}

Class Feed implements Subject {
  private final List<Observer> observers = new ArrayList<>();
  public void registerObserver(Observer o) {
    this.observers.add(0);
  }
  public void notifyServeres(String tweet) {
    observers.forEach(o -> o.notify(tweet));
  }
}

Feed f = new Feed();
f.registerObserver(new NYTimes());
f.registerObserver(new Guardian());
f.notifyServeres("The queen said her favorite book is Modern Java in Action!");
```

&#x20;

람다 표현식 사용

옵저버를 명시적으로 인스턴스화하지 않고 람다표현식을 직접 전달해서 실행할 동작을 지정할 수 있다.

```
f.registerObserver((String tweet) -> {
  if(tweet != null && tweet.contains("money")) {
    System.out.println("Breaking news in NY!" + tweet);
  }
});

f.registerObserver((String tweet) -> {
  if(tweet != null && tweet.contains("queen")) {
    System.out.println("Yet more news from London..." + tweet);
  }
});
```

**9.2.4 의무 체인**

작업 처리 객체의 체인(동작 체인 등)을 만들 떄는 의무 체인 패턴을 사용한다.

```
public abstract class processingObject<T> {
  protected ProcessingObject<T> successor;
  public void setSuccessor(ProcessingObject<T> successor) {
    this.successor = successor;
  }
  public T handle(T input) {
    T r = handleWork(input);
    if(successor != null) {
      return successor.handle(r);
    }
    return r;
  }
  abstract protected T handleWork(T input);
}
```

handle 메서드는 일부 작업을 어떻게 처리해야할지 전체적으로 기술한다.

ProcessingObject 클래스를 상속받아 handleWork 메서드를 구현하여 다양한 종류의 작업 처리 객체를 만들 수 있다.

```
public class HeaderTextProcessing extends ProcessingObject<String> {
  public String handleWork(String text) {
    return "From Raoul, Mario and Alan : " + text;
  }
}

public class SpellCheckerProcessing extends ProcessingObject<String> {
  public String handleWork(String text) {
    return text.replaceAll("labda", "lambda");
  }
}

ProcessingObject<String> p1 = new HeaderTextProcessing();
ProcessingObject<String> p2 = new SpellCheckerProcessing();
p1.setSuccessor(p2); //두 작업처리 객체를 연결
String result = p1.handle("Aren't labdas really sexy?!");
System.out.println(result);
```

&#x20;

람다 표현식 사용

작업처리 객체를 UnaryOperator\<String> 형식의 인스턴스를 표현할 수 있으며, andThen 메서드로 이들 함수를 조합해서 체인을 만들 수 있다.

```
UnaryOperator<String> headerProcessing = 
  (String text) -> "From Raoul, Mario and Alan : " + text;
UnaryOperator<String> spellCheckerProcessing = 
  (String text) -> text.replaceAll("labda", "lambda");
Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
String result = pipeline.apply("Aren't labdas really sexy?!");
```

**9.2.5 팩토리**

인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 팩토리 디자인 패턴을 사용한다.

```
public class ProdectFactor {
  public static Product createProduct(String name) {
    switch(name) {
      case "loan" : return new Loan();
      case "stock" : return new Stock();
      case "bond" : return new Bond();
      default : throw new RuntimeException("No Such product" + name);
    }
  }
}

product p = ProductFactory.createProduct("loan");
```

여기서 Loan, Stock, Bond는 모두 Product의 서브 형식이다. 생성자와 설정을 외부로 노출하지 않음으로써 클라이언트가 단순하게 상품을 생산할 수 있다.

&#x20;

람다 표현식 사용

```
Supplier<Product> loanSupplier = Loan::new;
Loan loan = loanSupplier.get();
```

위 코드와 같이 생성자도 메서드 참조처럼 접근할 수 있다.

```
//상품명과 생성자를 연결하는 Map 코드로 재구현
final static Map<String, Supplier<Product>> map = new HashMap<>();
static {
  map.put("loan", Loan::new);
  map.put("stock", Stock::new);
  map.put("bond", Bond::new);
}

//Map을 이용해 다양한 상품을 인스턴스화
public static Product createProduct(String name) {
  Supplier<product> p = map.get(name);
  if(p != null) return p.get();
  throw new RuntimeException("No Such product" + name);
}
```

#### 9.3 람다 테스팅

**9.3.1 보이는 람다 표현식의 동작 테스팅**

람다 표현식은 함수형 인터페이스의 인스턴스를 생성하므로, 생성된 인스턴스의 동작으로 람다 표현식을 테스트할 수 있다.

```
public class Point {
  public final static Comparator<Point> compareByXAndThenY = 
    comparing(Point::getX).thenComparing(Point::getY);;
 }
 
 //TEST 코드
 @Test
 public void testComparingTwoPoints() throws Exception {
   Point p1 = new Point(10, 15);
   Point p2 = new Point(10, 20);
   int result = Point.compareByXThenY.compare(p1, p2);
   assertTrue(result < 0);
 }
```

**9.3.2 람다를 사용하는 메서드의 동작에 집중하라**

람다 표현식을 사용하는 메서드의 동작을 테스트함으로써 람다를 공개하지 않으면서도 람다 표현식을 검증할 수 있다.

```
public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
  return points.stream().map(p -> new Point(p.getX() + x, p.getY())).collect(toList());
}

//TEST 코드
@Test
public void testComparingAllPointsRigtTwoPoints() throws Exception {
  List<Point> points = Arrays.asList(new Point(5, 5), new Point(10, 5));
  List<Point> excpectedPoints = Arrays.asList(new Point(15, 5), new Point(20, 5));
  
  List<Point> newPoinsts = Point.moveAllPointsRightBy(points, 10);
  assertEquals(expectedPoint, newPoints);
 }
```

**9.3.4 고차원 함수 테스팅**

함수를 인수로 받거나 다른 함수를 반환하는 메서드를 고차원 함수라고 한다.

메서드가 람다를 인수로 받는다면 다른 람다로 메서드의 동작을 테스트할 수 있다.

```
@Test
Public void testFilter() throws Exception {
  List<Integer> numbers = Arrays.asList(1,2,3,4);
  List<Integer> even = filter(numbers, i -> i % 2 == 0);
  List<Integer> smallerThanThree = filter(numbers, i -> i < 3);
  assertEquals(Arrays.asList(2,4), even);
  assertEquals(Arrays.asList(1,2), smallerThanThree);
}
```

#### 9.4 디버깅

람다 표현식과 스트림은 기존의 디버깅 기법(스택트레이스, 로깅)을 무력화한다.

**9.4.1 스택 트레이스 확인**

람다와 스택 트레이스

람다 표현식은 이름이 없기 때문에 복잡한 스택 트레이스가 생성된다.

메서드 참조를 사용해도 스택 트레이스에는 메서드 명이 나타나지 않는다.

메서드 참조를 사용하는 클래스와 같은 곳에 선언되어있는 메서드를 참조할 때는 메서드 참조 이름이 스택 트레이스에 나타난다.

**9.4.2 정보 로깅**

```
numbers.stream()
  .map(x -> x + 17)
  .filter(x -> x % 2 == 0)
  .limit(3)
  .forEach(System.out::println);
```

위 연산에서 forEach를 호출하는 순간 전체 스트림이 소비된다.

파이프라인에 적용된 각각의 연산(map, filter, limt)이 어떤 결과를 도출하는지 확인하려면 peek이라는 스트림 연산을 활용할 수 있다.

peek은 스트림의 각 요소를 소비한 것처럼 동작하지만, forEach처럼 실제로 소비하지는 않는다.

```
numbers.stream()
  .peek(x -> System.out.println(x))
  .map(x -> x + 17)
  .peek(x -> System.out.println(x))
  .filter(x -> x % 2 == 0)
  .peek(x -> System.out.println(x))
  .limit(3)
  .peek(x -> System.out.println(x))
  .collect(toList());
```
