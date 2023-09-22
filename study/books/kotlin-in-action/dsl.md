# DSL

이번 챕터에서는 영역 특화 언어(`Domain-Specific Language`)를 통해 관용적인 API를 디자인하는 법을 배울 것이다. 전통적인 API와 DSL 스타일 API의 차이점을 공부하고, DSL 스타일 API가 다양한 실용적인 문제, 즉 DB 접근, HTML 생성, 테스팅 등에 사용되는 방법을 배울 것이다.

코틀린 DSL 설계는 코틀린 언어의 여러 특성을 활용하는데 그중 하나가 5장에서 살펴본 수신 객체 지정 람다이다. 또다른 새로운 특징은 `invoke`로, 이는 10장에서 `KFunction`을 call이나 invoke로 호출할 수 있는 것에 관해서 11장에 설명할 것이라고 하고 넘어갔다. `invoke`를 사용하면 DSL 코드 안에서 람다와 프로퍼티 대입을 더 유연하게 조합할 수 있다.

### APIs에서 DSLs로 <a href="#apis-ec-ec-c-dsls-eb-a-c" id="apis-ec-ec-c-dsls-eb-a-c"></a>

우리의 목표는 읽기 쉽고 유지보수하기 쉬운 코드를 만드는 것이다. 이 목표를 위해서는 각각의 클래스에만 집중하는 것이 아니라, 각 클래스가 서로 상호작용하는 바를 살펴봐야 한다. 즉, 클래스 APIs에 집중해야 한다.

이 책을 통해서 우리는 clean APIs를 만들기 위한 코틀린의 다양한 기능들을 살펴보았다. clean APIs란 무엇일까? 이는 다음과 같은 말을 뜻한다.

* 코드를 읽는 독자가 어떤 일이 벌어질지 명확하게 이해할 수 있어야 한다. 어떤 언어를 사용하건 이름을 잘 붙이고 적절한 개념을 사용하는 것은 매우 중요하다.
* 코드가 간결해야 한다. 불필요한 구문이나 번잡한 준비 코드가 가능한 한 적어야 한다. 이 조건을 만족시키는 것이 이번 챕터의 주요 목표이다.

코틀린에서 깔끔한 API를 작성하기 위해 지원하는 기능들을 표로 살펴보자.

일반 구문 간결한 구문 사용한 언어 특성

| StringUtil.capitalize(s)    | s.capitalize()          | 확장 함수             |
| --------------------------- | ----------------------- | ----------------- |
| [1.to](http://1.to/)(“one”) | 1 to “one”              | 중위 호출             |
| set.add(2)                  | set += 2                | 연산자 오버로딩          |
| map.get(“key”)              | map\[“key”]             | get 메소드에 대한 관례    |
| file.use({ f -> f.read() }) | file.use{ it.read() }   | 람다를 괄호 밖으로 빼내는 관례 |
| sb.append(“yes”)            | with(sb){append(“yes”)} | 수신 객체 지정 람다       |

코틀린 DSL은 clean-syntax를 제공하는 기능과 그런 구문을 확장해 여러 메소드 호출을 조합함으로써 구조를 만들어내는 기능을 활용한다.

그 결과로 DSL은 각각의 메소드 호출만을 제공하는 API에 비해 더 표현력이 풍부해지고 사용하기 편해진다.

Kotlin DSLs는 코틀린과 마찬가지로, 컴파일 시점에 타입이 정해진다(`fully statically typed`). 따라서 DSL 패턴을 사용할 때도 코틀린의 장점을 누릴 수 있다.

다음은 DSLs이 할 수 있는 일의 예시이다.

```kotlin
val yesterday = 1.days.ago

//위의 코드는 다음과 같은 HTMl 코드를 생성함.
fun createSimpleTable() = createHTML().
	table {
		tr {
			td { +"cell" }
	}
}
```

자세한 이야기를 하기 전에, 먼저 DSLs이 무엇인지 살펴보자.

### 영역 특화 언어(DSL)란? <a href="#ec-ec-a-d-ed-a-b-ed-ec-b-ec-b-dsl-eb-e-f" id="ec-ec-a-d-ed-a-b-ed-ec-b-ec-b-dsl-eb-e-f"></a>

영역 특화 언어라는 것 자체는 오래된 개념이다. 범용 프로그래밍 언어(general-purpose programming language)를 기반으로 하여 필요하지 않은 기능을 없앤 영역 특화 언어를 `DSL`이라고 부른다. 이는 특정 도메인과 그 도메인에 연관된 기능에만 집중하는 언어를 의미하며, `SQL`이나 `정규식(regular expressions)` 등이 여기에 속한다. 이 언어들은 특정 task를 처리하는 데는 휼륭하나, 이 언어만으로 전체 애플리케이션을 모두 작성할 수는 없다.

그러나 이 언어들은 제공하는 기능을 축소함으로써 자신의 목표를 효과적으로 성취할 수 있다. SQL을 사용하기 위해서는 함수나 클래스를 작성할 필요가 없다. 이와 같이 `DSL`은 범용 프로그래밍 언어와 달리 `declarative(선언적)`하다. 이와 달리 범용 프로그래밍 언어는 `imperative`하다.

`declarative`는 결과를 기술하기만 하고 그 결과를 달성하기 위한 세부 실행은 언어를 해석하는 엔진에 맡기는데, 실행 엔진이 결과를 얻는 과정을 최적화하기 때문에 declarative 언어가 더 효율적인 경우가 종종 있다.

그러나 DSLs은 또한 단점도 가지고 있는데, 그건 바로 범용 언어로 만든 애플리케이션과 조합하기 어렵다는 점이다. DSLs은 고유한 문법을 가지고 있어 다른 언어로 만든 프로그램과 통합하기 어렵다. 따라서 DSL로 작성한 프로그램을 다른 언어에서 호출하기 위해서는 DSL 프로그램을 별도의 파일이나 문자열 리터럴로 저장해야 한다.

하지만 이런 식으로 DSL을 저장할 경우 호스트 프로그램과 DSL의 상호작용을 컴파일 시점에 검증하거나 DSL 프로그램을 디버깅하거나 DSL 코드 작성을 돕는 IDE 기능을 제공하기 어려워지는 문제가 있다. 이런 문제를 극복하기 위해 `internal DSL`이라는 개념이 유명해지고 있다.

### internal DSL <a href="#internal-dsl" id="internal-dsl"></a>

독립적인 문법 구조를 가진 `external DSL`과 달리 `internal DSL`은 범용 언어로 작성된 프로그래밍의 일부이며 범용 언어와 동일한 문법을 사용한다. 즉, internal DSL은 독립적인 언어라기보다 DSL의 핵심 장점을 유지하면서 주 언어를 특별하게 사용하는 방법이다. 이 두 가지 접근법을 비교하기 위해 `external DSL`과 `internal DSL`에서 테스크가 어떻게 완료되는지 확인해보자.

```routeros
SELECT Country.name, COUNT(Customer.id)
    FROM Country
    JOIN Customer
        ON Country.id = Customer.country_id
GROUP BY Country.name
ORDER BY COUNT(Customer.id) DESC
    LIMIT 1
```

위 SQL은 가장 많은 고객이 살고 있는 나라를 알아내는 질의문으로, 질의 언어가 주 애플리케이션 언어 사이에 상호 작용하는 방법을 제공해야 하기 때문에 불편할 수도 있다. 아래는 위와 같은 기능을 하는 코드지만 코틀린으로 작성된 것이다.

```reasonml
(Country join Customer)
	.slice(Country.name, Count(Customer.id))
	.selectAll()
	.groupBy(Country.name)
	.orderBy(Count(Customer.id), isAsc = false)
	.limit(1)
```

이 두 코드는 동일한 프로그램을 생성하고 실행하지만, 두 번째 코드는 일반 코틀린 코드로 일반 코틀린 메소드를 사용한다. 이를 `internal DSL`이라고 부른다.

### DSL의 구조 <a href="#dsl-ec-d-ea-b-ac-ec-a-b" id="dsl-ec-d-ea-b-ac-ec-a-b"></a>

일반적인 API와 DSL 간의 명확한 구분이라는 것은 없다. “DSL은 보면 알 수 있다”라고 구분하고는 한다. DSLs은 종종 언어의 특징에 의존하며, 다른 맥락에서도 폭넓게 사용될 수 있다. 하지만 자주 거론되는 하나의 특성은 바로 APIs에 존재하지 않는 특징, 즉 구조(structure)와 문법(grammar)이다.

일반적으로 라이브러리는 많은 메소드를 가지고 있으며, 클라이언트는 이러한 메소드를 하나씩 호출함으로써 라이브러리를 사용할 수 있다. 이러한 일련의 호출에 대한 내부적인 구조나 맥락은 존재하지 않는다. 이러한 API를 command-query API라고 부른다. 이와는 반대로, DSL에서의 메소드 호출은 좀 더 큰 구조를 가지고 있는데, 이는 DSL의 grammar로 정의된다.

코틀린 DSL에서는 보통 람다를 중첩시키거나 메소드 호출을 연쇄시키는 방식으로 구조를 만든다. 그런 구조는 위에서 살펴본 SQL 예제에서 확인할 수 있었다.

DSL에서는 질의를 실행하기 위해 필요한 메소드들을 조합해야하며, 그렇게 메소드를 조합해서 만든 질의는 질의에 필요한 인자를 메소드 호출 하나에 모두 넘기는 것보다 훨씬 더 가독성이 높다. DSL 구조의 장점은 매번 함수가 호출될 때마다 반복하는 것이 아닌, 여러 번의 함수 호출에 대해 같은 context를 가질 수 있다는 것이다. 다음 예제를 살펴보자.

```gradle
//Kotlin DSL
dependencies {
	compile("junit:junit:4.11")
	compile("com.google.inject:guice:4.1.0")
}
//command-query API
project.dependencies.add("compile", "junit:junit:4.11")
project.dependencies.add("compile", "com.google.inject:guice:4.1.0")
```

DSLs에 구조를 만들기 위해 사용하는 다른 방법은 Chained method calls(연속적인 코드 줄에서 개체의 Method를 반복적으로 호출하는 것)이다.

```kotlin
//kotlintest 
str should startWith("kot")
//JUnit APIs
assertTrue(str.startsWith("kot"))
```

### internal DSL로 HTML 구축 <a href="#internal-dsl-eb-a-c-html-ea-b-ac-ec-b" id="internal-dsl-eb-a-c-html-ea-b-ac-ec-b"></a>

이 섹션에서는 internal DSL로 HTML 구축하는 법을 좀 더 자세하게 배운다.

```kotlin
//kotlinx.html library
fun createSimpleTable() = createHTML().
table {
	tr {
		td { +"cell" }
	}
}
//HTML 
<table>
	<tr>
		<td>cell</td>
	</tr>
</table>
```

`createSimpleTable` 함수는 HTML 조각을 가지고 있는 string을 반환한다. 그런데 왜 직접 작성하는 것이 아니라 코틀린을 통해 HTML을 작성해야 할까? 그 이유는 코틀린은 `type-safe`하기 때문이다. 코틀린에서는 `tr` 태그 안에만 `td` 태그를 작성할 수 있다. 또 다른 이유는 코틀린은 다른 어떤 언어도 내부에 구축할 수 있다는 점이다. 이 말은 목표하던 데이터를 포함하고 있는 HTML 조각을 동적으로 생성 가능하다는 뜻이다.

```kotlin
//kotlinx.html library
fun createAnotherTable() = createHTML().table {
	val numbers = mapOf(1 to "one", 2 to "two")
	for ((num, string) in numbers) {
		tr {
			td { +"$num" }
			td { +string }
		}
	}
}
//HTML 
<table>
	<tr>
		<td>1</td>
		<td>one</td>
	</tr>
	<tr>
		<td>2</td>
		<td>two</td>
	</tr>
</table>
```

이제 DSL이 무엇인지, 그리고 왜 사용하고 싶은 지를 알았으니 코틀린이 이를 어떻게 지원하는 지를 살펴보자. 먼저 `수신 객체 지정 람다`를 봐야 한다.

### 구조화된 API 구축 : DSL에서 수신 객체 지정 람다 사용 <a href="#ea-b-ac-ec-a-b-ed-eb-c-api-ea-b-ac-ec-b-a-dsl-ec-ec-c-ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b" id="ea-b-ac-ec-a-b-ed-eb-c-api-ea-b-ac-ec-b-a-dsl-ec-ec-c-ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b"></a>

수신 객체 지정 람다는 구조화된 API를 만들 때 도움이 되는 강력한 코틀린 기능이다.

#### 수신 객체 지정 람다와 확장 함수 타입 <a href="#ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b-a-ec-ed-ec-e-a-ed-a-ec-ed-ec-e" id="ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b-a-ec-ed-ec-e-a-ed-a-ec-ed-ec-e"></a>

우리는 5.5에서 with나 apply같은 scope function를 소개할 때 수신 객체 지정 람다에 대해 언급했었다. 이제 buildString 함수를 통해 코틀린이 수신 객체 지정 람다를 어떻게 구현하는지 살펴보자.

buildString은 한 StringBuilder 객체에 여러 내용을 추가할 수 있다. 람다를 인자로 받는 `buildString()`을 정의해보자.

```kotlin
fun buildString(
		builderAction: (StringBuilder) -> Unit
): String {
	val sb = StringBuilder()
	builderAction(sb)
	return sb.toString()
}

>>> val s = buildString {
... it.append("Hello, ")
... it.append("World!")
... }
>>> println(s)
Hello, World!
```

이 코드는 이해하기 쉽지만 사용하기 편리하지는 않는데, 그 이유는 람다 본문에서 매번 it을 사용해 StringBuilder를 참조해야하기 때문이다. 수신 객체 지정 람다를 사용하여 `it`이라는 이름을 사용하지 않는 람다를 인자로 넘겨보자.

```kotlin
fun buildString(
		builderAction: StringBuilder.() -> Unit
) : String {
	val sb = StringBuilder()
	sb.builderAction()
	return sb.toString()
}
>>> val s = buildString {
... 	this.append("Hello, ")
...	 append("World!")
... }
>>> println(s)
Hello, World!
```

두 코드를 비교해 보면, 우선 `builderAction`가 확장 함수 타입을 사용했다.

```xl
//확장 함수 타입 선언 과정
(StringBuilder) -> StringBuilder() -> StringBuilder.()
```

이제 람다를 수신 인자로 보낼 수 있기 때문에, `it`을 제거해도 된다. 따라서 `this.append()`의 형태로 사용 가능하다.

두 번째로는 `buildString` 함수의 선언이 달라졌다. `(StringBuilder) -> Unit`를 `StringBuilder.() -> Unit`로 대체하였다.

<figure><img src="https://blog.kakaocdn.net/dn/mvJkq/btrXBkOu29H/0UeRNkS8uhDGQa9bYPKjr1/img.png" alt=""><figcaption></figcaption></figure>

그런데 왜 굳이 확장 함수 타입을 사용할까? 확장 함수의 본문에서는 확장 대상 클래스에 정의된 메소드를 마치 그 클래스 내부에서 호출하듯이 사용할 수 있다.

즉, sb.builderAction()에서 builderAction은 StringBuilder 클래스 안에 정의된 함수가 아니며 StringBuilder 인스턴스인 sb는 확장 함수를 호출할 때와 동일한 구문으로 호출할 수 있는 함수 타입의 인자일 뿐이다.

#### HTML builder에서 수신 객체 지정 람다 사용 <a href="#html-builder-ec-ec-c-ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b-a-ec-ac-ec-a-a" id="html-builder-ec-ec-c-ec-ec-b-a-ea-b-d-ec-b-b-ec-a-ec-a-eb-e-c-eb-b-a-ec-ac-ec-a-a"></a>

HTML을 위한 Kotlin DSL은 보통 `HTML builder`라고 불린다. 이는 `type-safe builders`라는 컨셉을 대표한다. Builder는 객체 계층을 명시적으로 표현하기 위해 사용되는데, 이는 XML 또는 UI 요소를 편리하게 생성할 수 있다.

코틀린도 위와 같은 아이디어를 사용하지만, 코틀린 builder는 type-safe하다.

```kotlin
fun createSimpleTable() = createHTML().
	table {
		tr {
			td { +"cell" }
		}
}
```

위의 코틀린 코드에서 `table`, `tr`, `td`는 각각 함수이다. 이들은 모두 고차 함수로, 람다를 수신 객체로 받을 수 있다. 이러한 람다는 \*이름 풀이 규칙(name-resolution rules)\*을 바꿀 수 있다. `table` 함수에 전달된 람다 안에서는 `tr` 함수를 사용할 수 있다. 람다 밖에서는 `tr` 함수는 unresolved된다.

아래는 HTML builder를 완전하게 구현한 것이다.

```kotlin
open class Tag(val name: String) {
	private val children = mutableListOf<Tag>() //중첩 태그 저장

	protected fun <T : Tag> doInit(child: T, init: T.() -> Unit) {
		child.init() //자식 태그 초기화
		children.add(child) //자식 태그의 레퍼런스 저장
	}
	override fun toString() =
		"<$name>${children.joinToString("")}</$name>"
}
fun table(init: TABLE.() -> Unit) = TABLE().apply(init)

class TABLE : Tag("table") {
	fun tr(init: TR.() -> Unit) = doInit(TR(), init) //TR 태그의 새로운 인스턴스 추가
}
class TR : Tag("tr") {
	fun td(init: TD.() -> Unit) = doInit(TD(), init) //TD 태그의 새로운 인스턴스 추가
}
class TD : Tag("td")
fun createTable() =
	table {
		tr {
			td {
			}
		}
	}
>>> println(createTable())
<table><tr><td></td></tr></table>
```

모든 태그는 중첩 태그의 리스트를 가지고 있고, 이를 따라 순차적으로 태그를 만들어간다.

### Kotlin builders: 추상화와 재사용 가능 <a href="#kotlin-builders-a-ec-b-ec-ed-ec-ec-e-ac-ec-ac-ec-a-a-ea-b-eb-a-a" id="kotlin-builders-a-ec-b-ec-ed-ec-ec-e-ac-ec-ac-ec-a-a-ea-b-eb-a-a"></a>

만약 프로그램에서 일반적인 코드를 작성하고자 한다면, 중복을 피하고 코드를 보기 좋게 만들기 위한 다양한 도구들이 있다. 그 중 하나는 반복적인 코드를 새로운 함수로 만드는 것이다. 그러나 이러한 일은 SQL이나 HTML에서 굉장히 어렵거나 심지어 불가능할 수도 있다. 이때 internal DSLs을 사용하면 추상화된 기능을 가지는 새로운 함수를 만들어 재사용할 수 있다.

```javascript
<div class="dropdown">
		<button class="btn dropdown-toggle">
			Dropdown
		<span class="caret"></span>
	</button>
	<ul class="dropdown-menu">
		<li><a href="#">Action</a></li>
		<li><a href="#">Another action</a></li>
		<li role="separator" class="divider"></li>
		<li class="dropdown-header">Header</li>
		<li><a href="#">Separated link</a></li>
	</ul>
</div>
```

위의 코드에서는 `div`, `button`, `ul`, `li`와 같은 함수를 통해 같은 구조를 반복적으로 만들 수 있다. 하지만 이것보다 더 잘 할 수 있는 방법도 존재한다.

```kotlin
fun dropdownExample() = createHTML().dropdown {
	dropdownButton { +"Dropdown" }
	dropdownMenu {
		item("#", "Action")
		item("#", "Another action")
		divider()
		dropdownHeader("Header")
		item("#", "Separated link")
	}
}
```

위의 코드는 함수화를 했으면서도 불필요한 디테일을 숨기고 있어 더 보기 좋은 코드를 만든다.

### invoke convention을 사용한 더 유연한 블록 중첩 <a href="#invoke-convention-ec-d-ec-ac-ec-a-a-ed-c-eb-d-ec-c-a-ec-b-ed-c-eb-b-eb-a-d-ec-a-ec-b-a" id="invoke-convention-ec-d-ec-ac-ec-a-a-ed-c-eb-d-ec-c-a-ec-b-ed-c-eb-b-eb-a-d-ec-a-ec-b-a"></a>

invoke convention을 사용하면 함수처럼 호출할 수 있는 객체를 만드는 클래스를 정의할 수 있다. 하지만 이 기능은 일상적으로 사용하기 위해서 만들어진 기능은 아니다. invoke convention을 남용하면 `1()`과 같이 이해하기 어려운 코드가 생길 수 있다. 그러나 DSL에서는 invoke convention이 유용한 경우가 자주 있다.

#### invoke convention : 함수처럼 호출할 수 있는 객체 <a href="#invoke-convention-a-ed-a-ec-ec-b-eb-f-bc-ed-b-ec-b-c-ed-a-ec-ec-e-eb-a-ea-b-d-ec-b-b" id="invoke-convention-a-ed-a-ec-ec-b-eb-f-bc-ed-b-ec-b-c-ed-a-ec-ec-e-eb-a-ea-b-d-ec-b-b"></a>

우리는 이미 7장에서 코틀린의 convention에 대하여 학습하였다. 가령 `foo`라는 변수가 있고 `foo[bar]`라는 식을 사용하면 이는 `foo.get(bar)`로 변환된다. 이때 get은 Foo라는 클래스 안에 정의된 함수이거나 확장 함수여야 한다.

invoke convention 역시 같은 역할을 수행하는데, get과는 다르게 괄호()를 사용한다. `operator` 변경자가 붙은 `invoke` 메소드 정의가 들어있는 클래스의 객체는 함수처럼 호출할 수 있다. 다음은 이에 대한 예시이다.

```kotlin
class Greeter(val greeting: String) {
	operator fun invoke(name: String) { //invoke 메소드 정의
		println("$greeting, $name!")
	}
}
>>> val bavarianGreeter = Greeter("Servus") //함수로써 Greeter 인스턴스 호출
>>> bavarianGreeter("Dmitry")
Servus, Dmitry!
```

위에 코드에서는 bavarianGreeter 객체가 마치 함수처럼 호출되는 것을 확인할 수 있다. 이 때 `bavarianGreeter(“Dmitry”)`는 내부적으로 `bavarianGreeter.invoke(“Dmitry”)`로 컴파일된다.

#### invoke convention : convention과 함수형 타입 <a href="#invoke-convention-a-convention-ea-b-bc-ed-a-ec-ed-ed-ec-e" id="invoke-convention-a-convention-ea-b-bc-ed-a-ec-ed-ed-ec-e"></a>

invoke convention에서 배웠기 때문에, 우리는 일반적인 람다 호출 방식(람다 뒤에 괄호를 붙이는 방식: `lambda()`)이 실제로는 invoke convention을 사용하는 것에 지나지 않음을 충분히 알 수 있다.

인라인된 람다를 제외한 모든 람다는 함수형 인터페이스(Function1 등)을 구현하는 클래스로 컴파일된다. 각 함수형 인터페이스 안에는 그 인터페이스 이름이 가리키는 개수만큼(예를 들어, Function1이라면 1개\*\*) 파라미터를 받는 invoke 메소드\*\*가 들어있다.

람다를 함수처럼 호출할 수 있으면 어떤 점이 좋을까? 우선 복잡한 람다를 여러 메소드로 분리하면서도 여전히 분리 전의 람다처럼 외부에서 호출할 수 있는 객체를 만들 수 있다.

#### DSL의 invoke convention : Gradle에서 의존관계 정의 <a href="#dsl-ec-d-invoke-convention-a-gradle-ec-ec-c-ec-d-ec-a-b-ea-b-ea-b-ec-a-ec-d" id="dsl-ec-d-invoke-convention-a-gradle-ec-ec-c-ec-d-ec-a-b-ea-b-ea-b-ec-a-ec-d"></a>

```gradle
dependencies.compile("junit:junit:4.11")// 첫 번째방식

dependencies {// 두 번째 방식
	compile("junit:junit:4.11")
}
```

위 코드에서, 첫 번째 경우는 dependenices 변수에 대해 compile 메소드를 호출하고, 두 번째 경우에는 dependenices 안에 람다를 받는 invoke 메소드를 정의하면 두 번째 방식의 호출을 사용할 수 있다.

이러한 invoke convention으로 인해 DSL API의 유연성이 커지게 된다.

### 실전 코틀린 DSL <a href="#ec-b-a-ec-a-ec-bd-ed-b-eb-a-b-dsl" id="ec-b-a-ec-a-ec-bd-ed-b-eb-a-b-dsl"></a>

이제부터는 실용적인 DSL 예제, 즉 테스트 프레임워크, 날짜 리터럴, 데이터베이스 질의 등에 대해 살펴보도록 하자.

#### 중위 호출 연쇄 : 테스트 프레임워크의 should <a href="#ec-a-ec-c-ed-b-ec-b-c-ec-b-ec-a-ed-c-ec-a-a-ed-a-b-ed-eb-a-ec-e-ec-b-c-ed-ac-ec-d-should" id="ec-a-ec-c-ed-b-ec-b-c-ec-b-ec-a-ed-c-ec-a-a-ed-a-b-ed-eb-a-ec-e-ec-b-c-ed-ac-ec-d-should"></a>

앞서 살펴본 kotlintest DSL에서 중위 호출을 어떻게 활용하는지 살펴보자.

`s should startWith("kot")`와 같은 코드에서, s에 들어간 값이 kot로 시작하지 않으면 이 단언은 에러가 나게 된다. 이 코드는 마치 The s string should start with this constant처럼 읽히게 된다. 이 목적을 달성하기 위해서는 should 함수 선언 앞에 `infix` 변경자를 붙여야 한다(그래야 중위 함수가 되므로).

```kotlin
infix fun <T> T.should(matcher: Matcher<T>) = matcher.test(this)
```

위와 같이 should 함수를 구현할 때, should 함수는 Matcher의 인스턴스를 요구한다. Matcher는 값에 대한 단언문을 표현하는 제네릭 인터페이스로, `startWith`로 구현되며, 이는 어떤 문자열이 주어진 문자열로 시작하는지 검사한다.

kotlintest DSL에서 연쇄적인 호출을 사용하면, 다음과 같이 만들 수도 있다.

```kts
"kotlin" should start with "kot"
```

이 문장은 코틀린처럼 보이지 않는다. 이 문장이 어떻게 작동하는지 보기 위해서는 중위 호출을 일반적인 것으로 고쳐야 한다.

```kts
"kotlin".should(start).with("kot")
```

위의 코드를 통해서 우리는 `should`와 `with`가 연쇄적으로 중위 호출을 한다는 사실을 알 수 있다.

### 원시 타입에 대한 확장 함수 정의: 날짜 처리 <a href="#ec-b-ec-b-c-ed-ec-e-ec-eb-c-ed-c-ed-ec-e-a-ed-a-ec-ec-a-ec-d-a-eb-a-ec-a-c-ec-b-eb-a-ac" id="ec-b-ec-b-c-ed-ec-e-ec-eb-c-ed-c-ed-ec-e-a-ed-a-ec-ec-a-ec-d-a-eb-a-ec-a-c-ec-b-eb-a-ac"></a>

챕터가 시작할 때 보았던 예제 중 날짜에 대해서 살펴보자.

```kts
**val** yesterday = 1.days.ago
**val** tomorrow = 1.days.fromNow
```

이 DSL을 java.time API와 코틀린을 통해 구현하기 위해서는 몇 줄의 코드만 있으면 된다.

```kotlin
import java.time.Period
import java.time.LocalDate

val Int.days: Period
    get() = Period.ofDays(this)

val Period.ago: LocalDate
    get() = LocalDate.now() - this

val Period.fromNow: LocalDate
    get() = LocalDate.now() + this

println(1.days.ago)
// 2020-05-15
println(1.days.fromNow)
// 2020-05-17
```

### 멤버 확장 함수 : SQL을 위한 내부 DSL <a href="#eb-a-a-eb-b-ed-ec-e-a-ed-a-ec-a-sql-ec-d-ec-c-ed-c-eb-b-eb-b-dsl" id="eb-a-a-eb-b-ed-ec-e-a-ed-a-ec-a-sql-ec-d-ec-c-ed-c-eb-b-eb-b-dsl"></a>

이제 멤버 확장을 사용하는 예제를 살펴보자. 다음은 익스포즈드 프레임워크에서 제공한 SQL을 위한 internal DSL에서 가져온 예제이다. 익스포즈드 프레임워크에서 SQL로 테이블을 다루기 위해서는 Table 클래스를 확장한 객체로 대상 테이블을 정의해야 한다.

```kotlin
object Country: Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 50)
}
```

위의 선언은 데이터베이스의 테이블과 일치한다. 이 테이블을 생성하기 위해서는 `SchemaUtils.create(Country)` 메소드를 통해 다음과 같은 SQL을 구현해야 한다.

```sql
CREATE TABLE IF NOT EXISTS Country (
	id INT AUTO_INCREMENT NOT NULL,
	name VARCHAR(50) NOT NULL,
	CONSTRAINT pk_Country PRIMARY KEY (id)
)
```
