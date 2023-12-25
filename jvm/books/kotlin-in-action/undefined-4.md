# 람다

람다는 다른 함수에 넘길 수 있는 작은 코드 조각을 의미한다. 람다를 통해 공통 코드 구조를 라이브러리 함수로 뽑아낼 수 있는데, 심지어 처음부터 람다를 고려하지 않고 만든 라이브러리도 사용 가능하다. 이제부터 람다로 프로그래밍하는 법을 알아보도록 하자.

### 람다 <a href="#eb-e-c-eb-b-a" id="eb-e-c-eb-b-a"></a>

코틀린에서는 람다 식을 통해 함수를 선언하는 대신 코드 블록을 직접 함수의 인자로 전달할 수 있다. 다음과 같이 자바는 무명 내부 클래스를 선언하기 때문에 코드가 번잡스러워진다.

```aspectj
/* Java */
button.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View view) { //무명 내부 클래스의 선언
	/*클릭 시 수행할 동작 */
	}
});

/* Kotlin */
button.setOnClickListener{ /*클릭 시 수행할 동작 */}
```

위에서 볼 수 있는 것처럼, 람다는 주로 메서드가 하나뿐인 무명 객체 대신 많이 사용한다.

### 람다와 컬렉션 <a href="#eb-e-c-eb-b-a-ec-ec-bb-ac-eb-a-ec" id="eb-e-c-eb-b-a-ec-ec-bb-ac-eb-a-ec"></a>

람다를 통해 컬렉션을 편리하게 처리할 수 있는 라이브러리를 사용할 수 있다. 예를 들어, 아래에서 볼 수 있는 것처럼 모든 컬렉션에 대해 `maxBy` 함수를 호출하여 가장 큰 값을 찾을 수 있는데, 이때 `maxBy` 함수는 비교할 값을 돌려주는 함수, 즉 `it.age`를 인자로 받는다.

```reasonml
>>> val people = listOf(Person("Alice", 29), Person("Bob", 31))
>>> println(people.maxBy { it.age }) //나이 프로터피를 비교해 가장 큰 값을 돌려줌
Person(name=Bob, age=31)

people.maxBy(Person::age) //위와 같은 역할을 하는 코드
```

### 람다 식의 문법 <a href="#eb-e-c-eb-b-a-ec-b-d-ec-d-eb-ac-b-eb-b" id="eb-e-c-eb-b-a-ec-b-d-ec-d-eb-ac-b-eb-b"></a>

<figure><img src="https://blog.kakaocdn.net/dn/MJjO3/btrWCVn2pl1/vZbIaN3lnS9LM8kvDFdZo1/img.png" alt=""><figcaption></figcaption></figure>

람다 식은 항상 중괄호 사이에 위치하고, 화살표(`->`)가 인자 목록과 람다 본문을 구분해준다. 또한 람다 식을 변수에 저장하기도 하는데, 이렇게 변수에 저장한 람다를 다른 일반 함수와 마찬가지로 다룰 수 있다(즉, 변수 이름 뒤에 괄호를 놓고 그 안에 필요한 인자를 넣어 호출 가능하다).

```gml
>>> val sum = { x: Int, y: Int -> x+y} 
>>> println(sum(1, 2)) //방법 1
```

또한 `{ println(42) }()`처럼 직접 람다를 호출할 수 있지만, 이는 읽기도 어렵고 쓸모도 없다. 만약 이렇게 코드의 일부분을 블록으로 실행할 필요가 있다면 `run`을 사용한다(run은 인자로 받은 람다를 실행해 주는 라이브러리 함수). 따라서 `run{ println(42) }`처럼 사용 가능하다.

따라서 위에서 언급한 maxBy 함수는 `people.maxBy ( { p: Person -> p.age } )`처럼 사용할 수 있다. 또한 코틀린에서는 함수 호출 시 맨 뒤에 있는 인자가 람다 식이라면 그 람다를 괄호 밖으로 빼낼 수 있다. `people.maxBy () { p: Person -> p.age }` 또한 람다가 어떤 함수의 유일한 인자이고, 괄호 뒤에 람다를 썼다면 호출 시 빈 괄호를 없앨 수 있다. `people.maxBy { p: Person -> p.age }`

또한, 람다의 파라미터 타입을 생략하면 컴파일러가 자동으로 추론해준다. 따라서 명시할 필요가 없다. `people.maxBy { p -> p.age }`

마지막으로, 파라미터 이름을 디폴트 이름인 `it`으로 만들 수 있다. `people.maxBy { it.age }`

> 📌 여러 줄로 이루어진 람다의 경우, 본문의 맨 마지막에 있는 식이 람다의 결과 값이 된다.

### 현재 영역에 있는 변수 접근 <a href="#ed-ec-e-ac-ec-ec-a-d-ec-ec-e-eb-a-eb-b-ec-ec-a-ea-b-bc" id="ed-ec-e-ac-ec-ec-a-d-ec-ec-e-eb-a-eb-b-ec-ec-a-ea-b-bc"></a>

`forEach` 함수는 각각의 원소에 대해 수행할 작업을 람다로 받는다.

```subunit
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
	messages.forEach {
	println("$prefix $it")
	}
}
>>> val errors = listOf("403 Forbidden", "404 Not Found")
>>> printMessagesWithPrefix(errors, "Error:")
Error: 403 Forbidden
Error: 404 Not Found
```

코틀린의 람다 안에서는 파이널 변수가 아닌 변수에 접근이 가능하다. 또한 람다 안에서 바깥의 변수를 변경해도 된다.

```kotlin
fun printProblemCounts(responses: Collection<String>) {
	var clientErrors = 0
	var serverErrors = 0
	responses.forEach {
		if (it.startsWith("4")) { //람다 안에서 람다 안의 변수 변경 가능
			clientErrors++
		} else if (it.startsWith("5")) {
			serverErrors++
		}
	}
	println("$clientErrors client errors, $serverErrors server errors")
}
>>> val responses = listOf("200 OK", "418 I'm a teapot",
... "500 Internal Server Error")
>>> printProblemCounts(responses)
1 client errors, 1 server errors
```

이렇게 람다 안에서 사용하는 외부 변수를 `‘람다가 포획(capture)한 변수’`라고 부른다. 기본적으로는 함수 안에 정의된 로컬 변수의 생명주기는 함수가 반환되면 끝나지만, 어떤 함수가 자신의 로컬 변수를 포획한 람다를 반환하거나 다른 변수에 저장한다면 로컬 변수의 생명주기와 함수의 생명주기가 달라질 수 있다.

코틀린에서는 이렇게 변경 가능한 변수(var)를 포획하면 변수를 Ref 클래스 인스턴스에 넣는다. 따라서 이 Ref 인스턴스에 대한 참조를 파이널로 만들면 쉽게 람다로 포획할 수 있다.

### 멤버 참조 <a href="#eb-a-a-eb-b-ec-b-b-ec-a-b" id="eb-a-a-eb-b-ec-b-b-ec-a-b"></a>

```inform7
val getAge = Person::age
val getAge = {person:Person -> person.age} //위의 식의 의미
```

코틀린에서는 `::`를 클래스 이름과 참조하려는 멤버(프로퍼티나 메서드) 이름 사이에 위치하면, 함수를 값으로 바꾸어 직접 넘길 수 있다. 최상위 함수의 경우에는 클래스 이름을 생략하고 ::로 바로 참조를 시작할 수 있다.

### 바운드 멤버 참조 <a href="#eb-b-ec-a-b-eb-c-eb-a-a-eb-b-ec-b-b-ec-a-b" id="eb-b-ec-a-b-eb-c-eb-a-a-eb-b-ec-b-b-ec-a-b"></a>

```reasonml
>>> val p = Person("Dmitry", 34)
>>> val personsAgeFunction = Person::age
>>> println(personsAgeFunction(p))
34
>>> val dmitrysAgeFunction = p::age //바운드 멤버 참조
>>> println(dmitrysAgeFunction())
34
```

바운드 멤버 참조를 사용하여, 멤버 참조를 생성할 때 클래스 인스턴스를 함께 저장하여 나중에 그 인스턴스에 대해 멤버를 호출해준다.

### 필수적인 함수: filter와 map <a href="#ed-ec-ec-a-ec-d-b-ed-a-ec-a-filter-ec-map" id="ed-ec-ec-a-ec-d-b-ed-a-ec-a-filter-ec-map"></a>

filter 함수: 컬렉션을 이터레이션하면서 주어진 람다에 각 원소를 넘겨서 람다가 true를 반환하는 원소만 모은다.

* `people.filter{ it.age > 30 }`

map 함수: 주어진 람다를 컬렉션의 각 원소에 적용한 결과를 모아서 새 컬렉션을 만든다.

* `list.map { it * it } //숫자의 제곱이 모아져 있는 리스트 [1, 4, 9, 16] 생성`
* `people.map { it.name }`

따라서 나이가 30살 이상인 사람의 이름을 출력해보면 다음과 같다.

* `people.filter{ it.age > 30 }.map(Person::name)`

### all, any, count, find: 컬렉션에 술어 적용 <a href="#all-c-any-c-count-c-find-a-ec-bb-ac-eb-a-ec-ec-ec-a-ec-b-ec-a-ec-a-a" id="all-c-any-c-count-c-find-a-ec-bb-ac-eb-a-ec-ec-ec-a-ec-b-ec-a-ec-a-a"></a>

count 함수: 주어진 조건을 만족하는 원소의 개수 반환

find 함수: 조건을 만족하는 첫 번째 원소 반환

any: 술어를 만족하는 원소가 하나라도 있는지 궁금할 때 사용

all: 모든 원소가 술어를 만족하는지 궁금할 때 사용

### groupBy: 리스트를 여러 그룹으로 이루어진 맵으로 변경 <a href="#groupby-a-eb-a-ac-ec-a-a-ed-a-b-eb-a-bc-ec-ac-eb-f-ac-ea-b-b-eb-a-b-ec-c-bc-eb-a-c-ec-d-b-eb-a-a-ec" id="groupby-a-eb-a-ac-ec-a-a-ed-a-b-eb-a-bc-ec-ac-eb-f-ac-ea-b-b-eb-a-b-ec-c-bc-eb-a-c-ec-d-b-eb-a-a-ec"></a>

groupBy 함수: 파라미터로 넘겨 준 값이 같은 원소끼리 그룹으로 만든다.

### flatMap과 flatten: 중첩된 컬렉션 안의 원소 처리 <a href="#flatmap-ea-b-bc-flatten-a-ec-a-ec-b-a-eb-c-ec-bb-ac-eb-a-ec-ec-ec-d-ec-b-ec-c-ec-b-eb-a-ac" id="flatmap-ea-b-bc-flatten-a-ec-a-ec-b-a-eb-c-ec-bb-ac-eb-a-ec-ec-ec-d-ec-b-ec-c-ec-b-eb-a-ac"></a>

flatMap 함수: 인자로 주어진 람다를 컬렉션의 모든 객체에 적용하고, 람다를 적용한 결과 얻어지는 여러 리스트를 한 리스트로 한데 모은다. 이때, 특별히 변환할 내용이 없다면 리스트의 리스트를 평평하게 펼치는 함수인 `flatten` 함수를 사용해도 된다.

### 지연 계산(lazy) 컬렉션 연산 <a href="#ec-a-ec-b-ea-b-ec-b-lazy-ec-bb-ac-eb-a-ec-ec-b-ec-b" id="ec-a-ec-b-ea-b-ec-b-lazy-ec-bb-ac-eb-a-ec-ec-b-ec-b"></a>

앞에서 살펴본 컬렉션 함수는 결과 컬렉션을 즉시 생성하는데(=매 단계의 계산 중간 결과를 새로운 컬렉션에 담음) `시퀀스`를 사용하면 이러한 중간 컬렉션을 사용하지 않아도 컬렉션 연산을 인쇄할 수 있다. `asSequence` 확장 함수를 호출하면 어떤 컬렉션이든 시퀀스로 바꿀 수 있다.

```less
people.map(Person::name).filter { it.startsWith("A") } //원소가 많을 수록 리스트가 더 생기는 게 문제 생김

people.asSequence() //원본 컬렉션을 시퀀스로 변환하고, 결과 시퀀스를 다시 리스트로 변환
	.map(Person::name)
	.filter { it.startsWith("A") }
	.toList()
```

### 시퀀스 연산 실행: 중간 연산과 최종 연산 <a href="#ec-b-c-ed-ec-a-a-ec-b-ec-b-ec-b-a-ed-a-ec-a-ea-b-ec-b-ec-b-ea-b-bc-ec-b-c-ec-a-ec-b-ec-b" id="ec-b-c-ed-ec-a-a-ec-b-ec-b-ec-b-a-ed-a-ec-a-ea-b-ec-b-ec-b-ea-b-bc-ec-b-c-ec-a-ec-b-ec-b"></a>

<figure><img src="https://blog.kakaocdn.net/dn/Jz9YY/btrWCVn2s9q/yiCjcuVT5RguBQx7KpEch0/img.png" alt=""><figcaption></figcaption></figure>

시퀀스에 대한 연산은 중간 연산과 최종 연산으로 나뉜다. 중간 연산은 다른 시퀀스를 반환하고, 최종 연산은 결과를 반환한다.

이 점을 고려하면 컬렉션의 종류나 연산 순서에 따라서 연산의 성능을 계산할 수 있다. 예를 들어, filter 후 map을 만들면 필요 없는 원소를 먼저 삭제하여 효율이 좋다.

### 시퀀스 만들기 <a href="#ec-b-c-ed-ec-a-a-eb-a-c-eb-a-ea-b-b" id="ec-b-c-ed-ec-a-a-eb-a-c-eb-a-ea-b-b"></a>

시퀀스는 asSequnce 함수 뿐만 아니라, `generateSequnce`를 사용하여 만들 수 있다. 이 함수는 이전의 원소를 인자로 받아 다음 원소를 계산한다.

```kotlin
fun createAllDoneRunnable(): Runnable {
return Runnable { println("All done!") }
}
>>> createAllDoneRunnable().run()
All done!
```

### SAM 생성자 <a href="#sam-ec-d-ec-b-ec-e" id="sam-ec-d-ec-b-ec-e"></a>

SAM 생성자는 람다를 함수형 인터페이스로 명시적으로 변경하는 역할을 한다. 이는 컴파일라가 자동으로 생성한 함수로, 함수형 인터페이스의 인스턴스를 반환하는 메서드가 있다면 람다를 직접 반환할 수 없고, 반환하고픈 람다를 SAM 생성자로 감싸야 한다.

```angelscript
>>> val naturalNumbers = generateSequence(0) { it+1 }
>>> val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
>>> println(numbersTo100.sum()) //지연 연산은 sum의 결과 계산 시 수행됨
5050
```

### 수신 객체 지정 람다: with와 apply <a href="#ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b-a-a-with-ec-apply" id="ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b-a-a-with-ec-apply"></a>

with 함수: 첫 번째 인자로 받은 객체를 두 번째 인자로 받은 람다의 수신 객체로 만든다.

```kotlin
fun alphabet(): String {
	val stringBuilder = StringBuilder()
	return with(stringBuilder) {
		for (letter in 'A'..'Z') {
			this.append(letter)
		}
		append("\\nNow I know the alphabet!")
			this.toString()
		}
}
```

apply 함수: with 함수와 거의 비슷하지만, 유일한 차이는 항상 자신에게 전달된 객체, 즉 수신 객체를 반환한다는 것이다.
