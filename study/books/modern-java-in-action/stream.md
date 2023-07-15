# STREAM

#### 스트림이란 무엇인가?

스트림(Stream)은 자바 8 API에서 새로 추가된 기능이다.

스트림을 이용하면 선언형(데이터를 임시 구현 코드 대신 질의료 표현)으로 컬렉션 데이터를 처리할 수 있다.

또한 멀티스레드 코드를 구현하지 않아도 데이터를 투명하게 병렬로 처리할 수 있다.

&#x20;

기존 코드(자바7)

```
List<Dish> lowCaloricDishes = new ArrayList<>();

for(Dish dish : menu) {
    if(dish.getCalories() < 400) {
        lowCaloricDishes.add(dish);
    }
}
Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
    @Override
    public int compare(Dish d1, Dish d2) {
        return Integer.compare(d1.getCalories(), d2.getCalories());
    }
});
List<String> lowCaloricDishesName = new ArrayList<>();
for(Dish dish : lowCaloricDishes) {
    lowCaloricDishesName.add(dish.getName());
}
return lowCaloricDishesName;
```

위 코드에서는 lowCaloricDishes라는 컨테이너 역할만 수행하는 '가비지 변수'를 사용했다.

자바 8에서 이런 세부 구현은 모두 라이브러리에서 처리한다.

&#x20;

최신코드(자바8)

```
List<String> lowCaloricDishesName = menu.stream()
    .filter(d -> d.getCalories() < 400)
    .sorted(comparing(Dish::getCalories))
    .map(Dish::getName)
    .collect(toList());
```

스트림을 사용하면 루프와 조건문 등의 제어 블록을 사용할 필요 없이 "저칼로리의 요리만 선택하라"와 같은 동작의 수행을 선언형으로 지정할 수 있다. 또한 람다 코드를 활용해서 변하는 요구사항에 쉽게 대응할 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/bCLXgJ/btq8gmH77DJ/n2OKsNeMkZVnqSfOty9jmk/img.png" alt=""><figcaption></figcaption></figure>

filter, sorted, map, collect 같은 여러 빌딩 블록을 연산을 연결해서 복잡한 데이터 처리 파이프라인을 만들 수 있다.

이들은 멀티코어 아키텍처를 최대한 활용할 수 있게 구현되어 있어 병렬처리 시에 스레드와 락을 걱정할 필요가 없다.

자바 8의 스트림 API의 특징을 다음처럼 요약할 수 있다.

* 선언형 : 더 간결하고 가독성이 좋아진다.
* 조립할 수 있음 : 유연성이 좋아진다.
* 병렬화 : 성능이 좋아진다.



#### 4.2 스트림 시작하기

스트림이란 '데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소'로 정의할 수 있다.

이 정의를 하나씩 살펴보자.

* 연속된 요소\
  컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다.\
  컬렉션은 자료구조이므로 시간과 공간의 복잡성과 관련된 요소 저장 및 접근 연산이 주를 이루는 반면, 스트림은 filter, sorted, map처럼 표현 계산식이 주를 이룬다. 즉, 컬렉션의 주제는 데이터이고 스트림의 주제는 계산이다.
* 소스\
  스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다. 정렬된 컬렉션으로 스트림을 생성하면 정렬이 그대로 유지된다.
* 데이터 처리 연산\
  스트림은 함수형 프로그래밍 언어나 데이터베이스에서 지원하는 연산과 비슷한 연산을 지원한다. 스트림 연산은 순차적 또는 병렬로 실행할 수 있다.

또한 스트림에는 다음과 같은 두가지 중요 특징이 있다.

* 파이프라이닝\
  대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프 라인을 구성할 수 있도록 스트림 자산을 반환한다. 그덕분에 게으름(jaziness), 쇼트서킷(short circuiting) 같은 최적화도 얻을 수 있다.
* 내부 반복\
  반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다.

설명한 내용을 예제로 확인해보자.

```
List<String> treeHighCaloricDishesName = 
  menu.stream() // 메뉴에서 스트림을 얻는다.
    .filter(d -> d.getCalories() > 300) // 파이프라인 연산 만들기(고칼로리 요리 필터링)
    .map(Dish::getName) // 요리명 추출
    .limit(3) // 선착순 세 개만 선택
    .collect(toList()); // 결과를 다른 리스트로 저장
```

* filter : 람다를 인수로 받아 스트림에서 특정 요소를 제외시킨다.
* map : 람다를 이용해서 한 요소를 다른 요로소 변환하거나 정보를 추출한다.
* limit : 정해준 개수 이상의 요소가 스트리에 저장되지 못하게 스트림 크기를 축소 truncate 한다.
* collect : 다양한 변환 방법을 인수로 받아 스트림에 누적된 요소를 특정 결과로(다른 형식으로) 변환한다.

<figure><img src="https://blog.kakaocdn.net/dn/bo7kIz/btq8ivdirE4/jrINd9kk1U8shsRG5b8KsK/img.png" alt=""><figcaption></figcaption></figure>



#### 4.3 스트림과 컬렉션

컬렉션과 스트림 모두 연속된 형식의 값을 저장하는 자료구조의 인터페이스를 제공한다.

여기서 '연속된'이라는 표현은 순서와 상관없이 아무 값에나 접속할 수 있는 것이 아니라 순차적으로 값에 접근한다는 의미이다.

&#x20;

컬렉션과 스트림의 가장 큰 차이는 데이터를 언제 계산하느냐이다.

컬렉션은 현재 자료구조가 포함하는 모든 값을 메모리에 저장하는 자료구조다.

컬렉션에 요소를 추가하거나 제거할 수 있으며, 컬렉션의 모든 요소는 컬렉션에 추가하기 전에 계산되어야 한다.

&#x20;

반면 스트림은 이론적으로 요청할 때만 요소를 계산하는 고정된 자료구조다.

스트림에는 요소를 추가하거나 제거할 수 없다. 결과적으로 스트림은 생산자와 소비자 관계를 형성한다.

컬렉션이 DVD에 저장된 영화라면, 스트림은 인터넷으로 스트리밍하는 영화라고 할 수 있다.

**4.3.1 딱 한번만 탐색할 수 있다.**

반복자와 마찬가지로 스트림도 딱 한번만 탐색할 수 있다. 즉, 탐색된 스트림의 요소는 소비된다.

한 번 탐색한 요소를 다시 탐색하려면 초기 데이터 소스에서 새로운 스트림을 만들어야 한다.

만약 I/O채널처럼 반복 사용할 수 없는 소스라면 새로운 스트림을 다시 만들 수 없다.

**4.3.2 외부 반복과 내부 반복**

컬렉션 인터페이스를 사용하려면 for-each 등을 사용해서 사용자가 직접 요소를 반복하는 외부 반복을 사용한다.

반면 스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해주는 내부 반복을 사용한다.

내부 반복을 사용하면 작업을 투명하게 병렬로 처리하거나 더 최적화된 다양한 순서로 처리할 수 있다.



#### 4.4 스트림 연산

스트림 인터페이스의 연산을 크게 두 가지로 구분할 수 있다.

* 중간 연산 : filter, map, limit 등으로 서로 연결하여 파이프라인을 형성한다.
* 최종 연산 : collect로 파이프라인을 실행한 다음 닫는다.

<figure><img src="https://blog.kakaocdn.net/dn/dCvytm/btq8dSnLaTA/KsDVNBL473wcJmQlSFLI4k/img.png" alt=""><figcaption></figcaption></figure>

**4.4.1 중간 연산**

중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해 질의로 만들 수 있다.

중간 연산의 중요한 특징은 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다는 것, 즉 게으르다는 것이다. 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한번에 처리하기 때문이다.

스트림의 게으른 특성 덕분에 쇼트서킷, 루프 퓨전 등의 최적화 효과를 얻을 수 있다.(다음 장에서 자세히 설명한다)

**4.4.2 최종 연산**

최종 연산은 스트림 파이프라인에서 결과를 도출한다. 이를 통해 List, Integer, void 등 스트림 이외의 결과가 반환된다.

예를 들어 forEach는 스트림의 각 요소에 람다를 적용한 다음 void를 반환하는 최종 연산이다.

```
menu.stream().forEach(System.out::println);
```
