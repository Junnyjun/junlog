# null 대신 Optional 클래스

#### 값이 없는 상황을 어떻게 처리할까?

```
public String getCarInsuranceName(Person person) {
  return person.getCar().getInsurance().getName();
}
```

위 코드에서 person이 null이거나 getCar(), getInsurance()가 null을 반환한다면 어떻게 될까?

**11.1.1 보수적인 자세로 NullPointerException 줄이기**

```
public String getCarInsuranceName(Person person) {
  if(person != null) {
    Car car = person.getCar();
    if(car != null) {
      Insurance insurance = car.getInsurance();
      if(insurance != null) {
        return insurance.getName();
      }
    }
  }
  return "Unknown";
}
```

위 코드는 변수를 참조할때마다 null을 확인한다. 따라서 중첩된 if가 추가되면서 코드 들여쓰기 수준이 증가한다.

이와같은 반복 패턴 코드를 '깊은 의심(deep doubt)'라 부른다. 이를 반복하다보면 코드의 구조가 엉망이 되고 가독성도 떨어진다.

```
public String getCarInsuranceName(Person person) {
  if(person == null) {
    return "Unknown";
  }
  Car car = person.getCar();
  if(car == null) {
    return "Unknown";
  }
  Insurance insurance = car.getInsurance();
  if(insurance == null) {
    return "Unknown";
  }
  return insurance.getName();
}
```

위 코드처럼 중첩 if 블록을 없앨 수 있지만, 너무 많은 출구(return)이 생겼기 때문에 유지보수가 어려워지고 실수가 발생하기 쉽다.

**11.1.2 null 때문에 발생하는 문제**

* 에러의 근원이다 : NullPointerException은 자바에서 가장 흔한 에러
* 코드를 어지럽힌다 : 중첩된 null 확인 코드로 가독성 저하
* 아무 의미가 없다 : null은 아무 의미도 표현하지 않음
* 자바 철학에 위배된다 : 자바는 개발자로부터 모든 포인터를 숨겼지만, null은 예외
* 형식 시스템에 구멍을 만든다 : null은 무형식이며 정보를 포함하고있지 않으므로 모든 참조 형식에 null을 할당할 수 있다. 이렇게 할당된 null이 전파되면 애초에 null이 어떤 의미로 사용되었는지 알수 없다.

**11.1.3 다른 언어는 null 대신 무얼 사용하나?**

그루비 같은 언어에서는 안전 내비게이션 연산자(?.)를 도입해서 null 문제를 해결했다.

```
def carInsuranceName = person?.car?.insurance?.name
```

null이 할당될 수 있는 값에 안전 네비게이션 연산자를 이용하면 null 참조 예외 걱정 없이 객체에 접근할 수 있다. 이때 null인 참조가 있으면 결과로 null이 반환된다.

&#x20;

하스켈, 스칼라 등의 함수형 언어는 다른 관점에서 null 문제에 접근한다. 하스켈의 Maybe와 스칼라의 Optional은 주어진 형식의 값을 갖거나 아무 값도 갖지 않을 수 있는 구조를 가진다. 따라서 null 참조 개념은 자연스럽게 사라진다.



#### 11.2 Optional 클래스 소개

Optional은 선택형값을 캡슐화하는 클래스다. 값이 있으면 Optional 클래스는 값을 감싸고, 값이 없으면 Optional.empty 메서드로 Optional을 반환한다.

null을 참조하면 NullPointerException이 발생하지만 Optional.empty()는 Optional 객체이므로 이를 다양한 방식으로 활용할 수 있다.

Car 변수를 그냥 사용했을때는 null 참조가 할당되었을때 올바른 상황인지 아니지 알수 없다. 하지만 Optional을 사용하면 Optional\<Car>로 바뀌고, 이는 값이 없을 수 있음을 명시적으로 보여준다.\`



#### 11.3 Optional 패턴 적용

**11.3.1 Optional 객체 만들기**

빈 Optional

Optional.empty로 빈 Optional 객체를 만들 수 있다.

```
Optional<Car> optCar = Optional.empty();
```

&#x20;

null이 아닌 값으로 Optional 만들기

정적 팩토리 메서드 Optional.of로 null이 아닌 값을 포함하는 Optional을 만들 수 있다.

```
Optional<Car> optCar = Optional.of(car);
```

이제 car가 null이라면 즉시 NullPointerException이 발생한다.(Optional을 사용하지 않았다면 car 프로퍼티에 접근하려 할 때 에러가 발생했을 것이다.)

&#x20;

null값으로 Optional 만들기

정적 팩토리 메서드 Optional.ofNullable로 null 값을 저장할 수 있는 Optional을 만들 수 있다.

```
Optional<Car> optCar = Optional.ofNullable(car);
```

이제 car가 null이면 빈 Optional 객체가 반환된다.

**11.3.2 맵으로 Optional의 값을 추출하고 변환하기**

```
String name = null;
if(insurance != null) {
  name = insurance.getName();
}
```

위의 코드를 다음과 같이 Optional로 사용할 수 있다.

```
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optioanl<String> name = optInsurance.map(Insurance::getName);
```

Optional의 map 메서드는 스트림의 map 메서드와 개념적으로 비슷하다.

Optional이 값을 포함하면 map의 인수로 제공된 함수가 값을 바꾸고, 비어있으면 아무 일도 일어나지 않는다.

**11.3.3 flatMap으로 Optional 객체 연결**

```
public class Person {
  private Optional<Car> car;
  public Optional<Car> getCar() {
    return car;
  }
}

...

Optional<Person> optPerson = Optional.of(person);
Optional<String> name = optPerson
  .map(Person::getCar)
  .map(car::getInsurance)
  .map(Insurance::getName);
```

위 코드에서 optPerson의 형식은 Optional\<Person>이므로 map 메서드를 호출할 수 있다. 하지만 getCar는 Optional\<Car> 형식의 객체를 반환한다. 즉, map 연산의 결과는 Optional\<Optional\<Car>> 형식의 객체가 되어 컴파일되지 않는다.

위 문제를 flatMap을 사용해 해결할 수 있다. flatMap은 인수로 받은 함수를 적용해서 생성된 각각의 스트림에서 콘텐츠만 평준화하여 남긴다.

&#x20;

Optional로 자동차의 보험회사 이름 찾기

```
public String getCarInsuranceName(Optional<Person> person) {
  return = person.flatMap(Person::getCar)
    .flatMap(car::getInsurance)
    .map(Insurance::getName)
    .orElse("Unknown");
}
```

null을 확인하느라 조건 분기문을 추가해서 코드를 복잡하게 만들지 않으면서도 쉽게 이해할 수 있는 코드를 완성했다.

Optional을 인수로 받거나 Optioanl을 반환하는 메서드를 정의한다면 결과적으로 이 메서드가 빈 값을 받거나 빈 결과를 반환할 수 있음을 잘 문서화해서 제공하는 것과 같다.

&#x20;

도메인 모델에서 Optional을 사용했을 때 데이터를 직렬화할 수 없는 이유

설계 단계에서 Optional은 선택형 반환값을 지원하는 것일 뿐, 필드 형식으로 사용될 것을 가정하지 않았기 때문에 Serializable 인터페이스를 구현하지 않는다. 따라서 직렬화 모델이 필요하다면 다음 예제와 같이 Optional로 값을 반환받을 수 있는 메서드를 추가하는 것을 권장한다.

```
public class Person {
  private Car car;
  public Optional<Car> getCarAsOptional() {
    return Optional.ofNullable(car);
  }
}
```

**11.3.4 Optional 스트림 조작**

```
public Set<String> getCarInsuranceNames(List<Person> persons) {
  return persons.stream()
    .map(Person::getCar) //Stream<Optional<Car>>
    .map(optCar -> optCar.flatMap(Car::getInsurance)) //Stream<Optional<Insurance>>
    .map(optIns -> OptIns.map(Insurance::getName)) //Stream<Optional<String>>
    .flatMap(Optional::stream) //Stream<String>
    .collect(toSet()); //Set<String>
}
```

스트림 요소를 조작하려면 변환, 필터 등의 일련의 긴 체인이 필요한데, Optional로 값이 감싸있으면 이 과정이 조금 더 복잡해진다.

**11.3.5 디폴트 액션과 Optional 언랩**

Optional 클래스는 Optional 인스턴스에 포함된 값을 읽는 다양한 방법을 제공한다.

* get()은 값을 읽는 가장 간단하면서 가장 안전하지 않은 메서드이다. 래핑된 값이 있으면 해당 값을 반환하고 없으면 NoSuchElementException을 반환한다.
* orElse(T other) 메서드는 Optional이 값을 포함하지 않을 때 기본 값을 제공할 수 있다.
* orElseGet(Supplier\<? extends T> other)은 orElse 메서드에 대응하는 게으른 버전의 메서드다. Optional에 값이 없을때만 Supplier가 실행된다. 디폴트 메서드를 만드는데 시간이 걸리거나 Optional이 비어있을 때만 기본값을 생성하고자 할 때 사용한다.
* orElseThrow(Supplier\<? extends X> exceptionSupplier)는 Optional이 비어있을때 지정한 예외를 발생시킨다.
* ifPresent(Consumer\<? super T> consumer)는 값이 존재할 때 인수로 넘겨준 동작을 실행할 수 있다.
* ifPresentOrElse(Consumer\<? super T> action, Runnable emptyAction)은 Optional이 비어있을 때만 실행할 수 있는 Runnable을 인수로 받는다.

**11.3.6 두 Optional 합치기**

```
public Insurance findCheapestInsurance(Person person, Car car) {
  ...
  return cheapestCompany;
}

public Optional<Insurance> nullSafeFindCheapestInsurance(
    Optional<Person> person, Optional<Car> car) {
  if (person.isPresent() && car.isPresent()) {
    return Optional.of(findCheapestInsurance(person.get(), car.get()));
  } else {
    return Optional.empty();
  }
}
```

위 코드는 두 Optional을 인수로 받아서 둘 중 하나라도 비어있으면 빈 Optional\<Insurance>를 반환한다.

person과 car의 시그니처만으로 둘 다 아무 값도 반환하지 않을 수 있다는 정보를 명시적을 주지만, 구현 코드는 null 확인 코드와 크게 다르지 않다.

```
public Optional<Insutrance> nullSafeFindCheapestInsurance(
    Optional<Person> person, Optional<Car> car) {
  return person.flatMap( p -> car.map( c -> findCheapestInsurance(p, c)));
}
```

위 코드처럼 한 줄의 코드로 메서드를 재구현할 수 있다. 첫 번째 Optional에 flatMap을 호출했으므로 첫 번째 Optional이 비어있다면 인수로 전달된 표현식이 실행되지 않고 빈 Optional을 반환한다.

두 번째 Optional도 마찬가지이고, 두 값이 모두 존재한다면 map 메서드로 전달한 람다 표현식이 안전하게 findCheapestInsurance 메서드를 호출한다.

**11.3.7 필터로 특정값 거르기**

```
Insurance insurance = ...;
if(insurance != null && "CambridgeInsurance".equals(insurance.getName())){
  System.out.println("ok");
}
```

위 코드처럼 객체의 메서드를 호출해서 프로퍼티를 확인해야 하는 경우, 다음과 같이 재구현할 수 있다.

```
Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance -> "CambridgeInsurance".equals(insurance.getName()))
  .ifPresent(x -> System.out.println("ok"));
```



### 11.4 Optional을 사용한 실용 예제

**11.4.1 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기**

```
Object value=map.get("key");
```

위 코드와 같이 null이 반환될 수 있는 코드를 Optional로 감싸서 개선할 수 있다.

```
Optional<Object> value= Optional.ofNullable(map.get("key"));
```

**11.4.2 예외와 Optional 클래스**

null을 확인할 때는 if문을 사용했지만 예외를 발생하시키는 메서드는 try/catch 블록을 사용해야 한다.

```
public static Optional<Integer> stringToInt(String s) {
  try {
    return Optional.of(Integer.parseInt(s));
  } catch (NumberFormatException e) {
    return Optional.empty();
  }
}
```

위 코드와 같이 정수로 변환할 수 없는 문자열은 빈 Optional로 return하도록 구현할 수 있다.

**11.4.3 기본형 Optional을 사용하지 말아야 하는 이유**

Optional도 기본형으로 특화된 OptionalInt, OptionalLong, OptionalDouble 등의 클래스를 제공한다.

하지만 Optional의 최대 요소 수는 한개이므로 기본형 특화 Optional로 성능을 향상시킬 수 없다.

또한 기본형 특화 Optional은 map, flatMap, filter등을 제공하지 않고, 생성한 결과를 다른 일반 Optional과 혼용할 수 없다.&#x20;
