# 함수형 프로그래밍이란?

## **함수형 프로그래밍의 특징**

### 불변성, 참조투명성, 일급함수, lazy evaluation

함수를 통해 데이터의 참조 투명성을 보장, 가변 데이터 생성 회피

객체지향 vs 함수형 프로그래밍이 아닌, 명령형 vs 함수형으로 봐야함

사이드 이펙트가 없는 구조를 통해 멀티스레딩 환경에 적함

간결한 코드

### **순수한 함수란 무엇인가?**

#### 동일한 입력은 항상 동일한 결과를 출력

같은 파라메터를 전달하더라도 다른 값을 출력하는 함수는 당연히 ‘순수하지 않은 함수’

#### 사이드이펙트가 없음

함수 내부에서 외부의 변수를 바꾸는 등의 행동을 하지 않음

#### 순수한 함수의 효과와 고려사항

멀티스레딩 환경에서 공유 자원 변경에 대한 리스크가 없으므로 안전한 프로그래밍 가능(참조 투명성 만족)

순수하지 못한 함수는 엔터프라이즈급 프로젝트에서 반드시 필요하기 때문에(네트워크 통신, 파일입출력 등) 이러한 필수 불가결한 기능들은 최소화 및 모듈화하는 방식으로 접근해야 함

### **사이드이펙트 없는 프로그램 작성하기**

객체 생성시 불변한 객체로 만듬, 함수에서 객체의 상태를 변경하는 경우 객체를 수정하는 대신, 새로운 객체를 생성하여 리턴

새로운 객체를 생성하였을때, 해당 객체의 참조 관계가 있거나 사이드이펙트를 가져가야 하는 작업은 반드시 순수한 영역과 분리하며, 이러한 처리가 외부로 드러나지 않도록 설계해야 함

### **참조 투명성이란?**

프로그램의 변경 없이 어떤 표현식을 값으로 대체 가능함\
참조 투명성을 만족 할 경우 컴파일러 코드 최적화에 용이하며, lazy evaulation 구현이 가능해짐

ex) 표현식 1 + 1는 2로 써도 같음 투명한 함수 f(x)가 y를 반환한다면 f(x)는 y로 대체 될 수 있음

## **일급 함수?**

### 일급 객체(first-class object)

객체를 함수의 매개변수로 전달 가능

객체를 함수의 리턴값으로 사용 가능

객체를 변수나 자료구조에 담을 수 있음

### 일급 함수(first-class function)

함수를 함수의 매개변수로 전달 가능

함수를 함수의 리턴값으로 사용 가능

함수를 변수나 자료구조에 담을 수 있음

### **일급 함수를 통한 추상화와 재사용성 높이기**

일급 함수를 사용하면 명령형 혹은 객체지향 프로그래밍에서 할 수 없는 추상화가 가능하며 재사용성이 높아짐

명령형 vs 객체지향 vs 함수형 프로그래밍의 계산기

#### 명령형

계산 기능간의 결합도가 높고, 응집도 / 확장성 / 재사용성이 낮음

```kotlin
fun main() {
    val calculator = SimpleCalculator()

    println(calculator.calculate('+', 3, 1))    // 4
  println(calculator.calculate('-', 3, 1))    // 2
}

class SimpleCalculator {
  fun calculate(operator: Char, num1: Int, num2: Int): Int = when (operator) {
        '+' -> num1 + num2
        '-' -> num1 - num2
        else -> throw IllegalArgumentException()
    }
}
```

#### 객체지향

캡슐화, 인터페이스를 통한 확장성, 코드 재사용성, 테스트 용이성의 특징을 갖음

```kotlin
fun main() {
    val plusCalculator = OopCalculator(Plus())
  println(plusCalculator.calculate(3, 1))  // 4

    val minusCalculator = OopCalculator(Minus())
    println(minusCalculator.calculate(3, 1))  // 2
}

interface Calculator {
    fun calculate(num1: Int, num2: Int): Int
}

class Plus : Calculator {
    override fun calculate(num1: Int, num2: Int): Int {
        return num1 + num2
    }
}

class Minus : Calculator {
    override fun calculate(num1: Int, num2: Int): Int {
        return num1 - num2
    }
}

class OopCalculator(private val calculator: Calculator) {
    fun calculate(num1: Int, num2: Int): Int = calculator.calculate(num1, num2)
}
```

#### 함수형

```kotlin
fun main() {
    val fpCalculator = FpCalculator()

    println(fpCalculator.calculate({ n1, n2 -> n1 + n2 }, 3, 1))    // 4
    println(fpCalculator.calculate({ n1, n2 -> n1 - n2 }, 3, 1))    // 2
}

class FpCalculator {
    fun calculate(calculator: (Int, Int) -> Int, num1: Int, num2: Int): Int = calculator(num1, num2)
}
```

일급 함수를 사용해 계산 로직 추상화

인터페이스X, 구현 클래스X

코드 간결성, 유지보수 용이

나눗셈 기능이 필요 할 경우 신규 클래스 추가 없이 기능 추가가 가능함

고차 함수, 펑터, 모나드 등을 활용하면 훨씬 더 강력한 추상화 구현이 가능

### **lazy evaulation을 통한 무한 자료구조**

일반적인 프로그래밍 언어에서 코드가 실행될 때 값은 즉시 평가되지만, 함수형 언어는 기본적으로 값이 필요한 시점에 연산이 이루어지며 평가 시점을 지정 할 수도 있음

값이 실제로 필요한 시점까지 연기되기 때문에 시간이 오래 걸리는 작업을 효율적으로 동작시킬 수 있음

명령형 언어에서는 일반적으로 무한대 값을 자료구조에 담을 수 없으나, 함수형 언어에서는 가능함

```kotlin
fun main() {
    val infiniteValue = generateSequence(0) { it + 5 }
    infiniteValue.take(5).forEach { print("$it ") }   // 0 5 10 15 20
}
```
