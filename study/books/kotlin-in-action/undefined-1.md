# 기초

코틀린의 기본 요소에는 함수(function)와 변수(variable)가 있다. 코틀린에서는 많은 종류의 타입 선언을 생략해도 좋은데, 이는 가변의 데이터를 지양하고 프로그램을 불변의 상태로 유지할 수 있게 만드는데 도움이 된다. 그럼 이제 함수와 변수에 대해 알아보자.

### 함수 <a href="#ed-a-ec" id="ed-a-ec"></a>

코틀린 함수의 기본 구조는 다음과 같다.

```kotlin
fun max(a: Int, b; Int): Int{ //함수 이름, 파라미터, 리턴 타입
	return if(a>b) a else b //함수 바디
}
```

이때 위의 함수는 하나의 expression body로만 이루어져 있다. 따라서 다음과 같이 간략하게 만드는 것이 가능하다.

```kotlin
fun max(a: Int, b; Int): = if(a>b) a else b
```

이 경우 함수의 return 타입이 생략된 것을 알 수 있다. 코틀린은 컴파일 타임에 함수의 expression body를 해석하고 그것의 타입을 함수의 return 타입으로 사용할 수 있기 때문에 위와 같은 방식이 허용된다. 이때 return 타입 생략은 expression body를 가지고 있는 함수에만 허용되며, 중괄호로 둘러싸인 block body를 가지고 있어서 특정한 값을 return 하는 경우에는 반드시 이를 명시해줘야 한다.

#### 참고: Expression과 Statement <a href="#ec-b-b-ea-b-a-a-expression-ea-b-bc-statement" id="ec-b-b-ea-b-a-a-expression-ea-b-bc-statement"></a>

> 코틀린은 satement보다 expression을 지양하는 언어이다. 이때 이 둘의 차이는 무엇일까? Expression은 ‘수식’이라는 뜻이다. Expression들은 평가(evaluate)가 가능하기 때문에 하나 이상의 값으로 환원될 수 있다. Expression은 값을 가지기 때문에 다른 expression의 일부분으로 사용될 수 있다. 그러나 satement는 ‘서술’이라는 뜻으로, 프로그래밍에서는 실행 가능한(executable) 최소의 독립적인 코드 조각을 일컫는다. 스스로 값을 가지지 않는다는 점이 expression과의 차이점이 된다.

```angelscript
/*C언어 예시*/
int a=10; //Statement
a+b; //Expression
```

### 변수 <a href="#eb-b-ec" id="eb-b-ec"></a>

자바에서는 변수의 선언을 위해서 해당 변수의 타입을 먼저 결정해야 한다. 그러나 코틀린에서는 많은 경우 변수의 타입을 생략할 수 있다. 그러나 특정한 경우 변수의 타입을 명시해줄 수도 있다.

```kotlin
val answer = 42 //타입 생략
val answer: Int = 42 //타입 명시
```

또한 선언과 동시에 초기화를 하지 않는 경우에는 타입을 반드시 명시해야 한다.

```kotlin
val answer: Int //초기화를 하지 않으므로, 타입 명시
answer = 42
```

#### 가변과 불변의 변수 <a href="#ea-b-eb-b-ea-b-bc-eb-b-eb-b-ec-d-eb-b-ec" id="ea-b-eb-b-ea-b-bc-eb-b-eb-b-ec-d-eb-b-ec"></a>

코틀린에서는 변수를 선언하기 위한 두 개의 키워드가 존재한다. 이는 다음과 같다.

* val(값): 불변 레퍼런스로, 초기화 된 후에는 값 변경이 불가능하다. 자바의 final 변수와 같다.
* var(변수): 가변 레퍼런스로, 변경이 가능하다. 자바의 non-final 변수와 같다.

여기서 주의할 점은 레퍼런스 자체는 불변이라고 해도 객체는 가변일 수 있다는 점이다. 다음과 같은 예시에서 확인할 수 있다.

```reasonml
val languages = arrayListOf(“Java”) //가변형 List 생성
languages.add(“Kotlin”)
```

또한 var 타입이 가변 레퍼런스라고 할지라도 타입은 변경되지 않는다.

```stata
var answer = 42
answer = “no answer” //Int 타입에 String 할당, 타입 오류
```

### class <a href="#class" id="class"></a>

객체 지향이라는 컨셉은 자바에서 많이 접할 수 있었다. 그러나 코틀린에서는 같은 로직을 최소한의 변경으로 여러 곳에서 재사용될 수 있는 코드(보일러플레이트 코드라고 부른다)를 만들 수 있다. 다음은 같은 로직을 나타내는 자바와 코틀린 코드를 각각 보인 것이다.

```arduino
/*Java*/
public class Person{
	private final String name;
	
	public Person(String name){ //생성자
		this.name = name;
	}
	public String getName(){ //get 메소드
		return name;
	}
}
```

```angelscript
/*Kotlin*/
class Person(val name: String)
```

참고로 이런 식으로 데이터만 가지고 있으며 코드가 없는 클래스를 value object라고 부른다. 그렇다면 코틀린은 어떻게 이렇게 간단한 표현이 가능할까? 우선 코틀린에서는 public이 디폴트로 선언되어 있기에 생략이 가능하다. 또한, 코틀린에서는 getter와 setter를 내부적으로 선언해 주기 때문에 자바처럼 getter setter 메소드를 활용할 필요가 없다.

```angelscript
class Person(
    val name: String // 읽기 전용 프로퍼티로 private field와 public getter 함수를 생성
    var age: Int // 쓰기 가능 프로퍼티로 private field와 public getter, public setter 함수를 생성
    var isMarried: Boolean
)
```

따라서 자바에서 person.setMarried(false)와 같이 사용해야 하는 것과는 달리, 코틀린에서는 person.isMarried=false로 사용할 수 있다.

#### 커스텀 접근자 <a href="#ec-bb-a-ec-a-a-ed-ec-a-ea-b-bc-ec-e" id="ec-bb-a-ec-a-a-ed-ec-a-ea-b-bc-ec-e"></a>

코틀린이 자동으로 getter와 setter를 제공한다고 해도, 개발자가 원하는 대로 접근자 메소드를 만들어야 할 때가 있다. 이때 접근자 메소드는 다음과 같이 생성할 수 있다.

```angelscript
class Rectangle(val height: Int, val width: Int){
    val isSquare: Boolean
        get(){
            return height == width
        }
}
```

이때 isSquare는 값을 저장할 필드가 필요하지 않으며, 구현된 getter 함수 만을 가지고 있다는 점을 알 수 있다.

### enum <a href="#enum" id="enum"></a>

코틀린에서 enum은 soft keyword라고 불린다. soft keyword는 class 앞에 선언될 경우 특별한 의미를 가지지만, 다른 곳에서 사용될 경우에는 특별한 의미를 가지지 않는다. enum class는 열거형 클래스라는 의미로, 상수를 열거하여 집합으로 관리할 수 있다. (enum class는 그냥 class 와 다른 게 무엇일까?)

```angelscript
enum class Color(
		val r: Int, val g: Int, val b: Int //파라미터
) {
	RED(255, 0, 0), ORANGE(255, 165, 0),
	YELLOW(255, 255, 0), GREEN(0, 255, 0), BLUE(0, 0, 255),
	INDIGO(75, 0, 130), VIOLET(238, 130, 238); //세미콜론 필수

	fun rgb() = (r * 256 + g) * 256 + b
}
>>> println(Color.BLUE.rgb())
255
```

### when <a href="#when" id="when"></a>

자바에서의 switch는 코틀린에서 when으로 표현할 수 있다. 이때 if문과 마찬가지로, when을 통해 return 값을 반환할 수 있다. 다음은 그 예시이다.

```xl
fun getMnemonic(color: Color) =
	when (color) {
		Color.RED -> "Richard"
		Color.ORANGE -> "Of"
		Color.YELLOW -> "York"
		Color.GREEN -> "Gave"
		Color.BLUE -> "Battle"
		Color.INDIGO -> "In"
		Color.VIOLET -> "Vain"
		Color.WHITE, Color.BLACK -> "Not in Rainbow!"
	}
```

자바와 달리 코틀린에서는 break를 명시할 필요가 없으며, 성공적인 match가 일어난 branch만 리턴된다. 또한 콤마(,)를 활용하여 여러 브랜치가 동일한 리턴값을 가지도록 할 수 있다.

### smart cast <a href="#smart-cast" id="smart-cast"></a>

코틀린에서는 개발자가 타입을 캐스팅하지 않아도 컴파일러가 알아서 타입 캐스팅을 하는 기능이 있다. 먼저 다음과 같은 코드가 있다고 가정해보자.

```angelscript
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr
```

위 코드는 왼쪽(left)과 오른쪽(right)을 받아서 Sum에 저장하기 위한 우선 변수를 선언해 놓은 것이다. Sum 값을 반환하기 위해서 새로운 메소드를 작성한다면, 다음과 같이 표현할 수 있다.

```kotlin
fun eval(e: Expr): Int =
        when(e) { //when 활용하여 리턴값 지정
            is Num -> e.value //is 활용하여 타입 변환 자동으로 거치기
            is Sum -> eval(e.left) + eval(e.right)
            else -> throw IllegalArgumentException("Unknown expression")
        }
fun main(arg: Array<String>){
    println(eval3(Sum(Sum(Num(10), Num(7)), Num(3))))
}
```

is라는 키워드는 자동으로 캐스팅하는 기능을 가지고 있기 때문에(이를 smart cast라고 한다) 타입 변환을 거칠 필요가 없다. 또한 코틀린에서는 매칭되는 branch를 리턴해주는 when문이 있다. 이를 모두 활용하면 위와 같은 코드가 완성된다. (자바의 Upcasting, Downcasting 조사하기)

### 반목문: while 과 for <a href="#eb-b-eb-aa-a-eb-ac-b-a-while-ea-b-bc-for" id="eb-b-eb-aa-a-eb-ac-b-a-while-ea-b-bc-for"></a>

코틀린의 while과 do-while은 자바와 상당히 비슷한 형식을 띄고 있다.

```gauss
while (condition) {
	/*...*/
}
do {
	/*...*/
} while (condition)
```

while문의 형식은 다른 것이 없기 때문에 넘어가도록 하겠다. 그런데 for문의 경우는 조금 달라지는데, 자바에서 제공하는 보통의 for문은 코틀린에서 존재하지 않는다. 따라서 range를 통해서 반복문을 활용해야 하며, 이때 .. operator가 활용된다.

```angelscript
val oneToTen = 1..10
```

또는 일정한 step을 통해서 for 문을 사용할 수 있다.

```angelscript
for (i in 100 downTo 1 step 2) {
	print(fizzBuzz(i))
}
```

이러한 식으로, step을 활용하여 순차적으로 반복하 수 있고, 몇 개의 숫자는 건너뛰는 것도 가능하다.

또한 map을 통해 반복적인 순회를 할 수 있다.

```gams
val binaryReps = TreeMap<Char, String>()
for (c in 'A'..'F') {
	val binary = Integer.toBinaryString(c.toInt()) //아스키 코드를 이진수로 변환
	binaryReps[c] = binary
}
for ((letter, binary) in binaryReps) {
	println("$letter = $binary")
}
```

### Try\&Catch <a href="#try-catch" id="try-catch"></a>

코틀린에서 try와 catch는 expression으로 다루어진다. 따라서 값을 할당하는 것이 가능하다. 이때 자바와의 가장 큰 차이점은 자바의 throws문은 현재 코드에 존재하지 않는다는 것이다. 예를 들어, 자바의 throws IOException은 함수 선언 뒤에 필수적으로 명시해야 한다. 이때 명시적으로 표현되어야 하는 것은 바로 exception이다. 즉, 자바에서는 함수가 던질 수 있는 모든 예외를 특정한 exception에서 받아서 처리해야 한다. 이에 반해 코틀린에서는 다음과 같은 방식으로 예외 처리를 진행한다.

```kotlin
fun readNumber(reader: BufferedReader) {
	val number = try {
		Integer.parseInt(reader.readLine())
	} catch (e: NumberFormatException) {
		null
	}
	println(number)
}
```

\
