# Collector

#### 컬렉터란 무엇인가?

Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다.

리스트를 만들기위해 toList를 Collector 인터페이스의 구현으로 사용하거나 groupingBy를 이용해서 각 키 버킷에 대응하는 요소 별로 맵을 만들 수도 있다.

**6.1.1 고급 리듀싱 기능을 수행하는 컬렉터**

컬렉터의 최대 강점은 collect로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이다.

스트림에서 collect를 호출하면 collect에서는 리듀싱 연산을 이용해서 스트림의 각 요소를 방문하면서 컬렉터가 작업을 수행한다.

보통 함수를 요소로 변환할 때는 컬렉터를 적용하며 최종 결과를 저장하는 자료구조에 값을 누적한다.

**6.1.2 미리 정의된 컬렉터**

Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 메서드를 제공한다.

Collectors에서 제공하는 메서드의 기능은 크게 세 가지로 구분된다.

* 스트림 요소를 하나의 값으로 리듀스하고 요약
* 요소 그룹화
* 요소 분할

***

#### 6.2 리듀싱과 요약

컬렉터로 스트림의 모든 항목을 하나의 결과로 합칠 수 있다.

첫 번째 예제로 counting() 팩토리 메서드가 반환하는 컬렉터를 사용해보자.

```
long howManyDishes = menu.stream().collect(Collectors.counting());
//collect 생략 가능
long howManyDishes = menu.stream().count();
```

**6.2.1 스트림 값에서 최댓값과 최솟값 검색**

Collectors.maxBy, Collectors.minBy 메서드를 이용해서 스트림의 최댓값과 최솟값을 계산할 수 있다.

두 컬렉터는 스트림의 요소를 비교하는 데 사용할 Comparator를 인수로 받는다.

```
Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCaloriesDish = menu.stream().collect(maxBy(dishCaloriesComparator));
```

스트림에 있는 객체의 숫자 필드의 합계나 평균 등을 반환하는 연산에도 리듀싱 기능이 자주 사용된다. 이러한 연산을 요약 연산이라 부른다.

**6.2.2 요약 연산**

Collectors 클래스는 Collectors.summingInt라는 특별한 요약 팩토리 메서드를 제공한다.

summingInt는 객체를 int로 매핑하는 함수를 인수로 받으며, 인수로 전달된 함수는 객체를 int로 매핑한 컬렉터를 반환한다. 그리고 summingInt가 collect 메서드로 전달되면 요약 작업을 수행한다.

다음은 메뉴 리스트의 총 칼로리를 계산하는 코드다.

```
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

이러한 단순 합계 외에 평균값 계산 등의 연산도 요약 기능으로 제공된다.

만약 두개 이상의 연산이 한번에 수행되어야 한다면 summarizingInt가 반환하는 컬렉터를 사용할 수 있다.

```
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
// menuStatistics : IntSummaryStatistics{count=9, sum=4300, min=120, average=477.778, max=800}
```

**6.2.3 문자열 연결**

컬렉터에 joining 팩토리 메서드를 이용하면 스트림의 각 객체에 toString 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
```

연결된 두 요소 간에 구분 문자열을 넣을 수 있도록 오버로드된 joining 팩토리 메서드도 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(joining(","));
```

**6.2.4 범용 리듀싱 요약 연산**

지금까지 살펴본 모든 컬렉터는 reducing 팩토리 메서드로도 정의할 수 있다.

예를 들어 다음 코드처럼 reducing 메서드로 만들어진 컬렉터로도 메뉴의 모든 칼로리 합게를 게산할 수 있다.

```
int totalCalrories = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j);
```

또는 한 개의 인수를 가진 reducing 버전을 이용해서 가장 칼로리가 높은 요리를 찾을 수도 있다.

```
Optional<Dish> mostCaloriesDish = menu.stream().collect(reducing(d1, d2)
  -> d1.getCalories() > d2.getCalories() ? d1 : d2));
```

컬렉션 프레임워크 유연성 : 같은 연산도 다양한 방식으로 수행할 수 있다.

이전 예제에서 람다 표현식 대신 Integer 클래스의 sum 메서드 참조를 이용하면 코드를 좀 더 단순화할 수 있다.

```
int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
```

counting 컬렉터도 세 개의 인수를 갖는 reducing 팩토리 메서드를 이용해서 구현할 수 있다.

```
public static <T> Collector<T, ?, Long> counting() {
  return reducing(0L, e -> 1L, Long::sum);
}
```

자신의 상황에 맞는 최적의 해법 선택

함수형 프로그래밍에서는 하나의 연산을 다양한 방법으로 해결할 수 있다.

컬렉터를 이용하면 스트림 인터페이스에서 직접 제공하는 메서드를 이용하는 것에 비해 코드가 복잡해지지만,

재사용성과 커스터마이즈 가능성을 제공하는 높은 수준의 추상화와 일반화를 얻을 수 있다.

***

#### 6.3그룹화

자바8의 함수형을 이용하면 가독성 있는 한 줄 코드로 그룹화를 구현할 수 있다.

```
Map<Dish, Type, List<Dish>> dishsByType = menu.stream().collect(groupingBy(Dish::getType));
//dishsByType : {FISH=[prawns, salmon], OTHERS=[french fries, rice, pizza], MEAT[pork, beef, chicken]}
```

스트림의 각 요리에서 Dish.Type과 일치하는 모든 요리를 추출하는 함수를 groupbingBy 메서드로 전달했다.

이 함수를 기준으로 스트림이 그룹화되므로 이를 분류함수라고 부른다.

&#x20;

단순한 속성 접근자 대신 더 복잡한 분류 기준이 필요한 상황에서는 메서드 참조를 분류 함수로 사용할 수 없다.

예를 들어 400 칼로리 이하를 'diet', 400\~700칼로리를 'normal', 700칼로리 이상을 'fat' 요리로 분류한다 가정해보자.

```
public enum CaloricLevel { DIET, NORMAL, FAT }

Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
  groupingBy(dish -> {
    if (dish.getCalories() <= 400 ) return CaloricLevel.DIET;
    else if (dish.getCalories() <= 700 ) return CaloricLevel.NORMAL;
    else return CaloricLevel.FAT;
  }));
```

**6.3.1 그룹화된 요소 조작**

요소를 그룹화 한 다음에는 각 결과 그룹의 요소를 조작하는 연산이 필요하다.

예를 들어 500칼로리가 넘는 요리만 필터한다고 가정하자. 다음 코드처럼 그룹화를 하기 전에 프레디케이트로 필터를 적용해 문제를 해결할 수 있다고 생각할 것이다.

```
Map<Dish, Type, List<Dish>> caloricDishesByType = menu.stream()
  .filter(dish -> dish.getCalories() > 500)
  .collect(groupingBy(Dish::getType));
//caloricDishesByType : {OTHER=[french fries, pizza], MEAT=[pork, beef]}
```

위 코드로 문제를 해결할 수 있지만, 필터 프레디케이트를 만족하는 FISH 종류 요리는 없으므로 결과 맵에서 해당 키 자체가 사라진다.

필터 프레디케이트를 groupingBy 팩토리 메서드의 인수로 사용하면 이런 문제를 해결할 수 있다.

```
Map<Dish, Type, List<Dish>> caloricDishesByType = menu.stream()
  .collect(groupingBy(Dish::getType, filtering(dish -> getCalrories() > 500, toList())));
//caloricDishesByType : {OTHER=[french fries, pizza], MEAT=[pork, beef], FISH=[]}
```

filtering 메서드는 Collectors 클래스의 또다른 정적 팩토리 메서드로 프레디케이트를 인수로 받는다. 이 프레디케이트로 각 그룹의 요소와 필터링된 요소를 재그룹화한다.

&#x20;

그룹화된 항목을 조작하는 다른 유용한 기능 중 하나는 매핑 함수를 이용해 요소를 변환하는 작업이다.

```
Map<Dish, Type, List<Sting>> dishNamesByTypes = menu.stream()
  .collect(groupingBy(Dish::Type, mapping(Dish::getName, toList())));
```

각 그룹이 리스트 형태라면 flatMap 변환을 사용해서 추출할 수도 있다.

```
Map<String, List<String>> dishTags = new HashMap<>();
dishTag.push("pork", asList("greasy", "salty"));
dishTag.push("beef", asList("salty", "roasted"));
dishTag.push("chicken", asList("fried", "crisp"));
dishTag.push("rice", asList("light", "natural"));

Map<Dish.Type, Set<String>> dishNamesByType = menu.stream()
  .collect(groupingBy(Dish::getType,
    flatMapping(dish -> dishTags.get(dish.getName()).stream(),
    toSet())));
```

**6.3.2 다수준 그룹화**

두 인수를 받는 팩토리 메서드 Collectors.groupingBy를 이용해서 항목을 다수준으로 그룹화할 수 있다.

```
Map<Dish.Type, Map<CalricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream()
  .collect(groupingBy(Dish::getType,
    groupingBy(dish -> {
      if (dish.getCalories() <= 400) return CaloricLevel.DIET;
      else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
      else return CaloricLevel.FAT;
    })
  )
};
//dishesByTypeCaloricLevel : 
//{MEAT={DIET=[chicken], NORMAL=[beef], FAT=[pork]}, FISH={DIET=[prawns], NORMAL=[salmon]}, OTHER={...}}
```

외부 맵은 첫 번째 수준의 분류함수에서 분류한 키값 'fish, meat, other'를 가지며, 내부 맵은 두 번째 분류 함수의 키값 'normal, diet, fat'을 가진다.

보통 groupingBy의 연산을 '버킷(물건을 담을 수 있는 양동이)' 개념으로 생각하면 쉽다.

첫 번째 groupingBy는 각 키의 버킷을 만든다. 그리고 준비된 각각의 버킷을 서브스트림 컬렉터로 채워가기를 반복하면서 n수준 그룹화를 달성한다.

**6.3.3 서브그룹으로 데이터 수집**

groupingBy 메서드의 두번째 인수로 전달받는 컬렉터의 형식은 제한이 없다.

분류 함수 한개의 인수를 갖는 groupingBy(f)는 groupingBy(f, toList())의 축약형일 뿐이며, 다양한 컬렉터를 전달받을 수 있다.

```
Map<Dish.Type, Long> typesCount = menu.stream().collect(groupingBy(Dish::getType, counting()));
//{MEAT=3, FISH=2, OTHER=4}

Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream()
  .collect(groupingBy(Dish::getType, maxBy(CompaingInt(Dish::getCalories))));
//{FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}
```

컬렉터 결과를 다른 형식에 적용하기

마지막 그룹화 연산에서 맵의 모든 값을 Optional로 감쌀 필요가 없으므로 Optional을 삭제할 수 있다.

Collectors.collectingAndThen 팩토리 메서드로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.

```
Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream()
  .collect(groupingBy(Dish::getType,
    collectingAndThen(maxBy(CompaingInt(Dish::getCalories)), Optional::get)));
//{FISH=salmon, OTHER=pizza, MEAT=pork}
```

groupingBy와 함꼐 사용하는 다른 컬렉터 예제

일반적으로 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때에는 팩토리 메서드 groupingBy에 두 번째 인수로 전달한 컬렉터를 사용한다.

```
Map<Dish.Type, Integer> totalCaloriesByType = menu.stream()
  .collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
```

이 외에도 mapping 메서드로 만들어진 컬렉터도 groupingBy와 자주 사용된다.

mapping은 입력 요소를 누적하기 전에 매핑 함수를 적용해서 다양한 형식의 객체를 주어진 형식의 컬렉터에 맞게 변환한다.

```
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream()
  .collect(groupingBy(Dish::getType, mapping(dish -> {
    if (dish.getCalories() <= 400) return caloricLevel.DIET;
    else if (dish.getCalories() <= 700) return caloricLevel.NORMAL;
    else return caloricLevel.FAT;
  }, toSet() )));
  //{OTHER=[DIET, NORMAL], MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL]}
```

#### 6.4 분할

분할은 분할 함수라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.

분할 함수는 불리언을 반환하므로 맵의 키 형식은 Boolean이며, 참 또는 거짓을 갖는 두 개의 그룹으로 분류된다.

```
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));
// {false=[pork, beef, chicken, prawns, salmon],
// true=[french fires, rice, season fruit, pizza]}
```

true 값의 키로 맵에서 모든 채식 요리를 얻을 수 있다.

```
List<Dish> vegetarianDishes = partitionedMenu.get(true); //채식인 요리
```

메뉴 리스트로 생성한 스트림을 프레디케이트로 필터링해도 같은 결과를 얻을 수 있다.

```
List<Dish> vegetarianDishes = menu.stream().filter(Dish::isVegetarian).collect(toList());
```

**6.4.1 분할의 장점**

분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할 함수의 장점이다.

&#x20;

또한 컬렉터를 두 번째 인수로 전달할 수 있는 오버로드된 버전의 partitioningBy 메서드도 있다.

```
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(
  Dish::isVegetarian, groupingBy(Dish::getType));
// {false=FISH=[prawns, salmon], MEAT=[pork, beef, chicken],
// true=OTHER=[french fires, rice, season fruit, pizza]}
```

이전 코드를 활용하면 채식 요리와 채식이 아닌 요리의 각각의 그룹에서 가장 칼로리가 높은 요리도 찾을 수 있다.

```
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(
  Dish::isVegetarian, collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get));
// {false=pork, true=pizza}
```

**6.4.2 숫자를 소수와 비소수로 분할하기**

정수 n을 인수로 받아서 2에서 n까지의 자연수를 소수와 비소수로 나누는 프로그램을 구현하자.

먼저 주어진 수가 소수인지 아닌지 판별하는 프레디케이트를 구현한다.

```
public boolean isPrime(int candidate) {
  return IntStream.range(2, candidate).noneMatch(i -> candidate % i == 0);
  //스트림의 모든 정수로 candidate를 나눌 수 없으면 참을 반환
}
```

다음처럼 소수의 대상을 주어진 수의 제곱근 이하의 수로 제한할 수 있다.

```
public boolean isPrime(int candidate) {
  int candidateRoot = (int) Math.sqrt((double)candidate);
  return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
  //스트림의 모든 정수로 candidate를 나눌 수 없으면 참을 반환
}
```

이제 n개의 숫자를 포함하는 스트림을 만든 다음, isPrime 메서드를 프레디케이트로 이용하고 partitioningBy 컬렉터로 리듀스해서 숫자를 소수와 비소수로 분할할 수 있다.

```
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
  return IntStream.rangeClosed(2, n).boxed().collect(partitioningBy(candidate -> isPrime(candidate)));
}
```

***

#### 6.5 Collector 인터페이스

Collector 인터페이스는 리듀싱 연산(즉, 컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다.

Collector 인터페이스를 살펴보기 전에 6장을 시작하면서 살펴본 팩토리 메서드인 toList를 자세히 확인하자.

다음 코드는 Collector 인터페이스의 시그니처와 다섯 개의 메서드 정의를 보여준다.

```
public interface Collector<T, A, R> {
  Supplier<A> supplier();
  BiConsumer<A, T> accumulator();
  Function<A, R> finisher();
  BinaryOperator<A> combiner();
  Set<Characteristics> characteristics();
}
```

위 코드는 다음처럼 설명할 수 있다.

* T는 수집될 스트림 항목의 제네릭 형식이다.
* A는 누적자. 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
* R은 수집 연산 결과 객체의 형식(항상 그런것은 아니지만 대게 컬렉션 형식)이다.

예를 들어 Stream\<T>의 모든 요소를 List\<T>로 수집하는 ToListCollector\<T>라는 클래스를 구현할 수 있다.

```
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```

**6.5.1 Collector 인터페이스의 메서드 살펴보기**

supplier 메서드 : 새로운 결과 컨테이너 만들기

supplier 메서드는 빈 결과로 이루어진 Supplier를 반환해야 한다. 즉, 수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수다.

ToListCollector처럼 누적자를 반환하는 컬렉터에서는 빈 누적자가 비어있는 스트림의 수집 과정의 결과가 될 수 있다.

```
public Supplier<List<T>> supplier() {
  return() -> new ArrayList<T>();
}

//생성자 참조 방식으로 전달도 가능
public Supplier<List<T>> supplier() {
  return ArrayList::new;
}
```

accumlator 메서드 : 결과 컨테이너에 요소 추가하기

accumlator 메서드는 리듀싱 연산을 수행하는 함수를 반환한다. 스트림에서 n번째 요소를 탐색할 때 두 인수, 즉 누적자(n-1번째 항목까지 수집한 상태)와 n번째 요소를 함수에 적용한다.

함수의 반환값은 void, 즉 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부 상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다.

```
public BiConsumer<List<T>, T> acuumulator() {
  return (list, item) -> list.add(item);
}

//다음처럼 메서드 참조를 이용하면 코드가 더 간결해진다.
public BiConsumer<List<T>, T> accumulator() {
  return List::add;
}
```

finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기

finisher 메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 객체로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다.

```
public Function<List<T> List<T>> finisher() {
  return Function.identity();
}
```

combiner 메서드 : 두 결과 컨테이너 병합

combiner는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다.

toList의 combiner는 비교적 쉽게 구현할 수 있으며, 스트림의 두 번째 서브 파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가하면 된다.

```
public BinaryOperator<List<T>> combiner() {
  return (list1, list2) -> {
    liat.addAll(list2);
    return list1;
  }
}
```

이 메서드를 이용하면 스트림의 리듀싱을 병렬로 처리할 수 있다.&#x20;

병렬 리듀싱 수행 과정

* 스트림을 분할해야 하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 스트림을 재귀적으로 분할한다.
* 모든 서브스트림의 각 요소에 리듀싱 연산을 순차적으로 적용해서 병렬로 처리한다.
* 컬렉터의 combiner 메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합친다.

Characteristics 메서드

characteristics 메서드는 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환한다.

Characteristics는 스트림을 병렬로 리듀스 할지와 병렬로 리듀스한다면 어떤 최적하를 선택해야할지 힌트를 제공한다.

&#x20;

Characteristics의 항목

* UNORDERED : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
* CONCURRENT : 다중 스레드에서 accumulator 함수를 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다. 컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어있지 않은 상황에서만 병렬 리듀싱을 수행할 수 있다.
* IDENTITY\_FINISH : finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있으며, 누적자 A를 결과 R로 안전하게 형변환할 수 있다.

**6.5.2 응용하기**

지금까지 살펴본 다섯 가지 메서드를 이용해서 자신만의 커스텀 toListCollector를 구현할 수 있다.

```
public class ToListCollector<T> implements Collect<T, List<T>, List<T>> {
  @Override
  public Supplier<List<T>> supplier() {
    return ArrayList::new;
  }
  
  @Override
  public BiConsumer<List<T>, T> accumulator() {
    return List::add;
  }
  
  @Override
  public Function<List<T> List<T>> finisher() {
    return Function.identity();
  }
  
  @Override
  public BinaryOperator<List<T>> combiner() {
    return (list1, list2) -> {
      liat.addAll(list2);
      return list1;
    }
  }
    
  @Override
  public Set<Characteristics> characteristics() {
    return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
  }
}
```

컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기

IDENTITY\_FINISH 수집 연산에서는 Collector 인터페이스를 완전히 새로 구현하지 않고도 같은 결과를 얻을 수 있다.

Stream은 세 함수(발행, 누적, 합침)를 인수로 받는 collect  메서드를 오버로드하여 각각의 메서드는 Collector 인터페이스의 메서드가 반환하는 함수와 같은 기능을 수행한다.

```
List<Dish> dishes = menuStream.collect(
  ArrayList::new, //발행
  List::add,  //누적
  List:addAll); //합침
```

***

#### 6.6 커스텀 컬렉터를 구현해서 성능 개선하기

이전 예제에서 n까지의 자연수를 소수와 비소수로 분할하는 커스텀 컬렉터를 만들었다.

```
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
  return IntStream.rangeClosed(2, n).boxed().collect(partitioningBy(candidate -> isPrime(candidate)));
}

public boolean isPrime(int candidate) {
  int candidateRoot = (int) Math.sqrt((double)candidate);
  return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
}
```

커스텀 컬렉터를 이용해서 성능을 더 개선해보자.

**6.6.1 소수로만 나누기**

우선 소수로 나누어떨어지는지 확인해서 대상의 범위를 좁힐 수 있다.

제수가 소수가 아니면 소용없으므로 제수를 현재 숫자 이하에서 발견한 소수로 제한할 수 있으며, 주어진 숫자가 소수인지 확인하기 위해 지금까지 발견한 소수 리스트에 접근해야한다.

우리가 살펴본 컬렉터로는 컬렉터 수집 과정에서 부분 결과에 접근할 수 없지만, 커스텀 컬렉터 클래스를 사용해서 해결할 수 있다.

&#x20;

```
public boolean isPrime(List<Integer> primes, int candidate) {
  return primes.stream().noneMatch(i -> candidate % i == 0);
}
```

이번에도 대상 숫자의 제곱근보다 작은 소수만 사용하도록 코드를 최적화해야한다.

filter(p -> p <= candidtaRoot)를 이용해서 대상의 루트보다 작은 소수를 필터링 할 수 있지만, filter는 전체 스트림을 처리한 다음 결과를 반환해야 한다.

대상의 제곱보다 큰 소수를 찾으면 검사를 중단하기 위해서는 talkWhile 메서드를 사용해야 한다.

```
public boolean isPrime(List<Integer> primes, int candidate) {
  int candidateRoot = (int) Math.sqrt((double)candidate);
  return primes.stream()
    .talkWhile(i -> i <= candidateRoot);
    .noneMatch(i -> candidate % i == 0);
}
```

&#x20;

talkWhile 메서드는 자바9부터 지원하는 기능이다. 자바 8에서는 직접 구현해야한다.

```
public static <A> List<A> talkWhile(List<A> list, Predicate<A> p) {
  int i = 0;
  for (A item : list) {
    if(!p.test(item)) {
      return list.subList(0, i); //프레디케이트를 만족하지 않으면 이전 하위항목 리스트를 반환
    }
    i++;
  }
  return list;
}
```

게으른 버전의 스트림 API와 달리 직접 구현한 talkWhile 메서드는 적극적으로 동작한다.

&#x20;

새로운 isPrime 메서드를 구현했으니 본격적으로 커스텀 컬렉터를 구현하자. Collector 클래스를 선언하고 Collector 인터페이스에서 요구하는 메서드 다섯 개를 구현해야한다.&#x20;

&#x20;

1단계 : Collector 클래스 시그니처 정의

```
public interface Collector<T, A, R>
```

T는 스트림 요소의 형식, A는 중간 결과를 누적하는 객체의 형식, R은 collect 연산의 최종 결과 형식을 의미한다.

소수와 비소수를 참과 거짓으로 나누기 위해 정수로 이루어진 스트림에서 누적자와 최종 결과 형식이 Map\<Boolean, List\<Integer>>인 컬렉터를 구현해야 한다.

```
public class PrimeNumbersCollector
  implements Collect<Integer,
  Map<Boolean, List<Integer>>,
  Map<Boolean, List<Integer>>>
```

2단계 : 리듀싱 연산 구현

먼저 supplier 메서드로 누적자를 만드는 함수를 반환해야 한다.

```
public Supplier<Map<Boolean, List<Integer>>> supplier() {
  return () -> new HashMap<Boolean, List<Integer>>() {{
    put(true, new ArrayList<Integer>());
    put(false, new ArrayList<Integer>());
  }};
}
```

위 코드에서는 누적자로 만들 맵을 만들면서 true, false 키와 빈 리스트로 초기화했다.

&#x20;

다음으로 스트림 요소를 어떻게 수집할지 결정하는 accumulator 메서드를 만든다.

이제 언제든지 원할 때 수집 과정의 중간 결과, 즉 지금까지 발견한 소수를 포함하는 누적자에 접근할 수 있다.

```
public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
  return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
    acc.get( isPrime(acc.get(true), candidate) ) //isPrime 결과에 따라 소수/비소수 리스트를 만든다.
      .add(candidate); //candidate를 알맞은 리스트에 추가한다.
  };
}
```

지금까지 발견한 소수 리스트 acc.get(true) 와 소수 여부를 확인할 수 있는 candidate를 인수로 isPrime 메서드를 호출했다. 그리고 호출 결과를 알맞은 리스트에 추가한다.

&#x20;

3단계 : 병렬 실행할 수 있는 컬렉터 만들기(가능하다면)

이번에는 병렬 수집 과정에서 두 부분 누적자를 합칠 수 있는 메서드를 만든다. 예제에서는 두 번째 맵의 소수 리스트와 비소수 리스트의 모든 수를 첫 번째 맵에 추가하는 연산이면 충분하다.

```
public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
  return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
    map1.get(true).addAll(map2.get(true));
    map1.get(false).addAll(map2.get(false));
  };
}
```

알고리즘 자체가 순차적이어서 컬렉터를 실제로 병렬로 사용할 순 없으므로 combiner 메서드는 빈 구현으로 남겨두거나 exception을 던지도록 구현하면 된다.

&#x20;

4단계 : finisher 메서드와 컬렉터의 characteristics 메서드

accumulator의 형식은 컬렉터 결과 형식과 같으므로 변환 과정은 필요없다. 따라서 항등 함수 identity를 반환하도록 finisher 메서드를 구현한다.

```
public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
  return Function.identity();
}
```

**6.6.2 컬렉터 성능 비교**

간단한 하니스를 만들어서 컬렉터 성능을 확인할 수 있다.

```
public class CollectorHarness {
  public static void main(String[] args) {
    long fastest = Long.MAX_VALUE;
    for (int i = 0; i < 10; i++) {
      long start = System.nanoTime();
      partitionPrimes(1_000_000); //백만 개의 숫자를 소수와 비소수로 분할
      long duration = (System.nanoTime() - start) / 1_000_000;
      if(duration < fastest) fastest = duration;
    }
    System.out.println"FASTEST EXECUTION DONE IN " + fastest + " msecs");
  }
```

collect 메서드로 PrimeNumbersCollector의 핵심 로직을 구현하는 세 함수를 전달해서 같은 결과를 얻을 수 있다.

```
public Map<Booelan, List<Integer>> partitionPrimesWithCustomCollector(int n) {
  IntStream.rangeClosed(2, n).boxed().collect(
    () -> new HashMap<Boolean, List<Integer>>() {{ // 발행
      put(true, new ArrayList<Integer>());
      put(false, new ArrayList<Integer>());
    }},
    (acc, candidate) -> { // 누적
      acc.get( isPrime(acc.get(true), candidate) )
        .add(candidate);
    },
    (map1, map2) -> { // 합침
      map1.get(true).addAll(map2.get(true));
      map1.get(false).addAll(map2.get(false));
    });
}
```
