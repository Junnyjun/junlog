# CompletableFuture

#### 16.1 Future의 단순 활용

시간이 걸릴 수 있는 작업을 Future 내부로 설정하면 호출자 스레드가 결과를 기다리는 동안 다른 작업을 할 수 있다.

Future 작업은 ExecutorService에서 제공하는 스레드에서 처리되고,

작업의 결과가 필요한 시점에 Future의 get 메서드로 결과를 가져올 수 있다.

하지만 get 메서드를 호출했을 때 결과가 준비되어있지 않다면 작업이 완료될 때까지 스레드를 블록시킨다.

**16.1.1 Future 제한**

Future 인터페이스에는 비동기 계산에 대한 대기와 결과 처리 메서드들이 있다. 하지만 여러 Future 간 의존성은 표현하기 어렵다.

그래서 자바8에서는 CompletableFuture 클래스로 다음과 같은 기능을 제공한다.

* 두 개의 비동기 계산 결과를 합친다. 두 결과는 서로 독립적 또는 한쪽에 의존적일 수 있다.
* Future 집합이 실행하는 모든 태스크의 완료를 기다린다.
* Future 집합에서 가장 빨리 완료되는 태스크를 기다렸다가 결과를 얻는다.
* 프로그램적으로 Future를 완료시킨다.(비동기 동작에서 수동으로 결과 제공)
* Future 완료 동작에 반응한다.(결과를 기다리면서 블록되지 않음)

***

#### 16.2 비동기 API 구현

```
public double getPrice(String product) {
  return calculatePrice(product);
}

private double calculatePrice(String product) {
  delay(); //1초간 블록
  return random.nextDouble() * product.charAt(0) + product.charAt(1);
}
```

사용자가 getPrice API를 호출하면 비동기 동작이 완료될 때까지 1초동안 블록된다.

이제 이 API를 비동기로 만들어보자.

**16.2.1 동기 메서드를 비동기 메서드로 변환**

```
public Future<Double> getPriceAsync(String product) {
  CompletableFuture<Double> futurePrice = new CompletableFuture<>();
  new Thread(() -> {
    double price = calculatePrice(product);
    futurePrice.complete(price);
  }).start();
  return futurePrice;
}
```

비동기 계산과 완료 결과를 포함하는 CompletableFuture 인스턴스를 만들었다.

그리고 계산 결과를 기다리지 않고 결과를 포함할 Future 인스턴트를 바로 반환했다.

```
Shop shop = new Shop("BestShop");
long start = System.nanoTime();
Future<Double> futurePrice = shop.getPriceAsync("my favorite product"); //제품 가격 요청
long invocationTime = ((System.nanoTime() - start) / 1_000_000);

//제품의 가격을 계산하는 동안
doSomethingElse();
//다른 상점 검색 등 작업 수행
try {
  double price = future.get(); //가격정보를 받을때까지 블록
} catch (Exception e) {
  throw new RuntimeException(e);
}
long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
```

가격 계산 API는 비동기로 처리되므로 즉시 Future를 반환하고, 그 사이에 다른 작업을 처리할 수 있다.

다른작업이 끝났다면 Future의 get 메서드를 호출해서 가격정보를 받을 때까지 대기한다.

**16.6.2 에러 처리 방법**

위 로직에서 가격을 계산하는 동안 에러가 발생한다면 어떻게 될까?

예외가 발생하면 해당 스레드에만 영향을 미치기 때문에 클라이언트는 get 메서드가 반환될 때까지 영원히 기다릴 수도 있다.

따라서 타임아웃을 활용해 예외처리를 하고, completeExceptionally 메서드를 이용해 CompletableFuture 내부에서 발생한 에외를 클라이언트로 전달해야 한다.

```
public Future<Double> getPriceAsync(String product) {
  CompletableFuture<Double> futurePrice = new CompletableFuture<>();
  new Thread(() -> {
    try {
      double price = calculatePrice(product);
      futurePrice.complete(price);
    } catch {
      futurePrice.completeExceptionally(ex); //에러를 포함시켜 Future를 종료
    }
  }).start();
  return futurePrice;
}
```

팩토리 메서드 supplyAsync로 CompletableFuture 만들기

좀 더 간단하게 CompletableFuture를 만드는 방법도 있다.

```
public Future<Double> getPriceAsync(String product) {
  return CompletableFuture.supplyAsync(() -> calculatePrice(product));
}
```

supplyAsync 메서드는 Supplier를 인수로 받아서 CompletableFuture를 반환한다.

ForkJoinPool의 Executor 중 하나가 Supplier를 실행하며, 두 번째 인수로 다른 Executor를 지정할 수도 있다.

***

#### 16.3 비블록 코드 만들기

다음과 같은 상점 리스트가 있다.

```
List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
                                 new Shop("LetsSaveBig"),
                                 new Shop("MyFavoriteShop"),
                                 new Shop("BuyItAll"));
```

그리고 다음처럼 제품명을 입력하면 상점 이름과 제품 가격 문자열을 반환하는 List를 구현해야 한다.

```
public List<String> findPrices(String product);
```

스트림을 이용하면 원하는 동작을 구현할 수 있다.

```
public List<String> findPrices(String product) {
  return shops.stream()
    .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
    .collect(toList());
}
```

하지만 네 개의 상점에서 각각 가격을 검색하는 동안 블록되는 시간이 발생할 것이다.

**16.3.1 병렬 스트림으로 요청 병렬화하기**

```
public List<String> findPrices(String product) {
  return shops.parallelStream()
    .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
    .collect(toList());
}
```

이제 네 개의 상점에서 병렬로 검색이 진행되므로 시간은 하나의 상점에서 가격을 검색하는 정도만 소요될 것이다.

**16.3.2 CompletableFutue로 비동기 호출 구현하기**

이번에는 findPrices 메서드의 호출을 비동기로 바꿔보자.

```
List<CompletableFuture<String>> priceFutures = 
  shops.stream()
    .map(shop -> CompletableFuture.suppltAsync(
      () -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
    .collect(toList());
}
```

위 코드로 List\<CompletableFuture\<String>>를 얻을 수 있고, 리스트의 CompletableFuture는 각각 계산 결과가 끝난 상점의 이름 문자열을 포함한다.

하지만 우리는 List\<String> 형식을 얻어야 하므로 모든 CompletableFuture의 동작이 완료되고 결괄르 추출한 다음 리스트를 반환해야 한다.

```
public List<String> findPrices(String product) {
  List<CompletableFuture<String>> priceFutures = 
    shops.stream()
      .map(shop -> CompletableFuture.suppltAsync(
        () -> shop.getName() + "price is " + shop.getPrice(product)))
      .collect(toList());
      
  return priceFutures.stream()
    .map(CompletableFuture::join) //모든 비동기 동작이 끝나길 대기
    .collect(toList());
}
```

두 map 연산을 하나의 스트림 처리 파이프라인이 아닌, 두 개의 파이프라인으로 처리했다는 사실에 주목하자.

스트림 연산은 게으른 특성이 있으므로 하나의 파이프라인으로 처리했다면 모든 가격 정보 요청 동작이 동기적, 순차적으로 이루어지게 된다.

CompletableFuture로 각 상점의 정보를 요청할 때 기존 요청 작업이 완료되어야 join이 결과를 반환하면서 다음 상점으로 정보를 요청할 수 있기 때문이다.

* 순차\
  shop1.getPrice() → future1.join()\
  → shop2.getPrice() → future2.join()\
  → shop3.getPrice() → future3.join()
* 병렬\
  shop1.getPrice() → future1.join() \
  &#x20;      ↓                    ↓\
  shop2.getPrice() → future2.join() \
  &#x20;      ↓                    ↓\
  shop3.getPrice() → future3.join()&#x20;

하지만 CompletableFuture를 사용한 결과는 순차 방식보단 빠르지만 병렬 스트림보단 느리다. 어떻게 개선할 수 있을까?

**16.3.3 더 확장성이 좋은 해결방법**

병렬 스트림 버전에서는 4개의 스레드에 4개의 작업을 병렬로 수행하면서 검색 시간을 최소화했다.

하지만 작업이 5개가 된다면, 4개 중 하나의 스레드가 완료된 후에 추가로 5번째 질의을 수행할 수 있다.

CompletableFuture는 병렬 스트림에 비해 작업에 이용할 수 있는 Executor를 지정할 수 있다는 장점이 있다.

**16.3.4 커스텀 Executor 사용하기**

실제로 필요한 작업량을 고려한 풀에서 관리하는 스레드 수에 맞게 Executor를 만들면 좋을 것 같다.

풀에서 관리하는 스레드 수는 어떻게 결정할 수 있을까?

> 스레드 풀 크기조절\
> N_thread_ = N_cpu_ \* U_cpu_ \* (1 + W/C)\
> \- N_cpu_ : Runtime.getRuntime().availableProcessors()가 반환하는 코어 수\
> \- U_cpu_ : 0과 1 사이의 값을 갖는 CPU 활용 비율\
> \- W/C : 대기시간과 계산시간의 비율

한 상점에 하나의 스레드가 할당될 수 있도록, 상점 수만큼 Executor를 설정한다.

서버 크래시 방지를 위해 하나의 Executor에서 사용할 스레드의 최대 개수는 100 이하로 설정한다.

```
private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), //상점 수만큼의 스레드를 갖는 풀 생성(0~100 사이)
    new ThreadFactory() {
  public Thread new Thread(Runnable r) {
    Thread t = new Thread(r);
    t.setDeamon(true);
    return t;
  }
});
```

데몬 스레드를 사용하면 자바 프로그램이 종료될 때 강제로 스레드 실행이 종료될 수 있다.

&#x20;

스트림 병렬화와 CompletableFuture 병렬화

* I/O가 포함되는 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하며 효율적일 수 있다.
* I/O를 기다리는 작업을 병렬로 실행할 때는 CompletableFuture가 더 많은 유연성을 제공하며, 대기/계산의 비율에 적합한 스레드 수를 설정할 수 있다. 스트림의 게으른 특성 때문에 스트림에서 I/O를 실제로 언제 처리할지 예측하기 어려운 문제도 있다.

#### 16.4 비동기 작업 파이프라인 만들기

```
public class Discount {
  public enum Code {
    NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
    
    private final int percentage;
    
    Code(int percentage) {
      this.percentage = percentage;
    }
  }
  ...
}
```

enum으로 할인율을 제공하는 코드를 정의하였다.

그리고 getPrice 메서드는 ShopName:price:DiscountCode 형식의 문자열을 반환하도록 수정했다.

```
public String getPrice(String product) {
  double price = calcuatePrice(product);
  Discount.Code code = Discount.Code.values()[
    random.nextInt(Discount.Code.values().length)];
  return String.format("%s:%.f:%s", name, price, code);
}
```

**16.4.1 할인 서비스 구현**

상점에서 제공한 문자열 파싱은 다음처럼 Quote 클래스로 캡슐화할 수 있다.

```
public class Quote {
  private final String shopName;
  private final double price;
  private final Discount.code discountCode;
  
  public Quote(String shopName, double price, Discount.code code) {
    this.shopName = shopName;
    this.price = price;
    this.discountCode = discountCode;
  }
  
  public static Quote parse(String s) {
    String[] split = s.split(":");
    String shopName = split[0];
    double price = Doule.parseDouble(split[1]);
    Discount.Code discountCode = Discount.Code.valueOf(split[2]);
    return new Quote(shopName, price, discountCode);
  }
  
  public String getShopName() {
    return shopName;
  }
  
  public String getPrice() {
    return price;
  }
  
  public Discount.code getDiscountCode() {
    return discountCode;
  }
  
  
}
```

상점에서 얻은 문자열을 정적 팩토리 메서드 parse로 넘겨주면 상점 이름, 할인전 가격, 할인된 가격 정보를 포함하는 Quote 클래스 인스턴스가 생성된다.

&#x20;

다음으로 Discount 서비스에서는 Quote 객체를 인수로 받아 할인된 가격 문자열을 반환하는 applyDiscount 메서드도 제공한다.

```
public class Discount {
  public enum Code {
    ...
  }
  
  public static String applyDiscount(Quote quote) {
    return quote.getShopName() + " price is " + Discount.apply(
      quote.getPrice(), quote.getDiscountCode());
  }
  
  pivate static double apply(double price, Code code) {
    delay();
    return format(price * (100 - code.percentage) / 100);
  }
}
```

**16.4.2 할인 서비스 이용**

먼저 가장 쉬운 방법인 순차적&동기방식으로 findPrice 메서드를 구현한다.

```
public List<String> findPrices(String product) {
  return shops.stream()
    .map(shop -> sho.getPrice(product)) //각 상점에서 할인전 가격 얻기
    .map(Quote::parse) //반환된 문자열을 Quote 객체로 변환
    .map(Discount::applyDiscount)  //Quote에 할인 적용
    .collect(toList());
}
```

코드를 수행해보면 순차적으로 다섯 상점에 가격을 요청하면서 5초가 소요되고, 할인코드를 적용하면서 5초가 소요된다.

앞서 확인한 것처럼 병렬 스트림으로 변환하면 성능을 개선할 수 있다. 하지만 스트림이 사용하는 스레드 풀의 크기가 고정되어 있으므로, 상점 수가 늘어나게되면 유연하게 대응할 수 없다.

따라서 CompletableFutuer에서 수행하는 태스크를 설정할 수 있는 커스텀 Executoer를 정의해서 CPU 사용을 극대화해야한다.

**16.4.3 동기 작업과 비동기 작업 조합하기**

```
public List<String> findPrices(String product) {
  List<CompletableFuture<String>> priceFutures = 
    shops.stream()
      .map(shop -> CompletableFuture.suppltAsync(
        () -> shop.getPrice(product), executor))
      .map(future -> future.thenApply(Quote::parse))
      .map(future -> future.thenCompose(quote ->
        CompletableFuture.supplyAsync(
          () -> Discount.applyDiscount(quote), executor))
      .collect(toList());
      
  return priceFutures.stream()
    .map(CompletableFuture::join)
    .collect(toList());
}
```

1. 가격정보 얻기\
   팩토리메서드 suuplyAsync에 람다 표현식5을 전달해서 비동기적으로 상점에서 정보를 조회했다.\
   반환 결과는 Stream\<CompletableFuture\<String>>이다.
2. Quote 파싱하기\
   CompletableFuture의 thenApply 메서드를 호출해서 Quote 인스턴스로 변환하는 Function으로 전달한다.\
   thenApply 메서드는 CompletableFutur가 끝날 때까지 블록하지 않는다.
3. CompletableFutuer를 조합해서 할인된 가격 계산하기\
   이번에는 원격 실행(1초의 지연으로 대체)이 포함되므로 이전 두 변환가 달리 동기적으로 작업을 수행해야 한다.\
   람다 표현식으로 이 동작을 supplyAsync에 전달할 수 있다. 그러면 다른 CompletableFutuer가 반환된다.\
   결국 두 가지 CompletableFuture로 이루어진 연쇄적으로 수행되는 두 개의 비동기 동작을 만들 수 있다.\
   &#x20; \- 상점에서 가격 정보를 얻어 와서 Quote로 변환하기\
   &#x20; \- 변환된 Quote를 Discount 서비스로 전달해서 할인된 최종가격 획득하기\
   thenCompose 메서드로 두 비동기 연산을 파이프 라인으로 만들수 있다.

**16.4.4 독립 CompletableFuture와 비독립 CompletableFuture 합치기**

독립적으로 실행된 두 개의 CompletableFuture 결과를 합쳐야할 때 thenCombine 메서드를 사용한다.

thenCombine 메서드의 BiFunction 인수는 결과를 어떻게 합질지 정의한다.

```
Funtion<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product)) //1번째 태스크 - 가격정보 요청
  .thenCombine(CompletableFuture.suuplyAsync(
      () -> exchangeService.getRate(Money.EUR, Money.USD)), //2번째 태스크 - 환율정보 요청
    (price, rate) -> price * rate)); //두 결과 합침
```

독립적인 두 개의 비동기 태스크는 각각 수행되고, 마지막에 합쳐진다.

**16.4.5 Future의 리플렉션과 CompletableFuture의 리플렉션**

CompletableFuture는 람다 표현식을 사용해 동기/비동기 태스크를 활용한 복잡한 연산 수행 방법을 효과적으로 정의할 수 있다.

또한 코드 가독성도 향상된다. 앞선 코드를 자바7로 구현하면서 비교해보자.

```
ExecutorService executor = Executors.newCachedThreadPool();
final Funtion<Double> futureRate = executor.submit(new Callable<Double>() {
  public Double call() {
    return exchangeService.getRate(Money.EUR, Money.USD);
  }
});

final Funtion<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
  public Double call() {
    double priceInEUR = shop.getPrice(product);
    return priceInEUR * futureRate.get();
  }
});
```

**16.4.6 타임아웃 효과적으로 사용하기**

Future가 작업을 끝내지 못할 경우 TimeoutException을 발생시켜 문제를 해결할 수 있다.

```
Funtion<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
  .thenCombine(CompletableFuture.suuplyAsync(
      () -> exchangeService.getRate(Money.EUR, Money.USD)),
    (price, rate) -> price * rate))
  .orTimeout(3, TimeUnit.SECONDS);
```

compleOnTimeout메서드를 통해 예외를 발생시키는 대신 미리 지정된 값을 사용하도록 할 수도 있다.

```
Funtion<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
  .thenCombine(CompletableFuture.suuplyAsync(
      () -> exchangeService.getRate(Money.EUR, Money.USD)),
      .completOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS),
    (price, rate) -> price * rate))
  .orTimeout(3, TimeUnit.SECONDS);
```

***

#### 16.5 CompletableFuture의 종료에 대응하는 방법

각 상점에서 물건 가격 정보를 얻어오는 findPrices 메서드가 모두 1초씩 지연되는 대신, 0.5\~2.5초씩 임의로 지연된다고 하자.

그리고 각 상점에서 가격 정보를 제공할때마다 즉시 보여줄 수 있는 최저가격 검색 어플리케이션을 만들어보자.

**16.5.1 최저가격 검색 에플리케이션 리팩터링**

```
public Stream<CompletableFuture<String>> findPriceStream(String product) {
  return shop.stream()
    .map(shop -> CompletableFuture.suppltAsync(
      () -> shop.getPrice(product), executor))
    .map(future -> future.thenApply(Quote::parse))
    .map(future -> future.thenCompose(quote ->
      CompletableFuture.supplyAsync(
        () -> Discount.applyDiscount(quote), executor)));
}
```

이제 findPriceStream 메서드 내부에서 세 가지 map 연산을 적용하고 반환하는 스트림에 네 번째 map 연산을 적용하자.

```
findPriceStream("myPhone").map(f -> f.thenAccept(System.out::println));
```

팩토리 메서드 allOf는 전달된 모든 CompletableFuture가 완료된 후에 CompletableFuture\<Void>를 반환한다.

이를 통해 모든 결과가 반환되었음을 확인할 수 있다.

```
CompletableFuture[] futures = findPriceStream("myPhone")
  .map(f -> f.thenAccept(System.out::println))
  .toArray(size -> new CompletableFuture[size]);
CompletableFuture.allOf(futues).join();
```

만약 CompletableFuture 중 하나만 완료되기를 기다리는 상황이라면 팩토리메서드 anyOf를 사용할 수 있다.
