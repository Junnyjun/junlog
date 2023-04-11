# Variable

### 숫자형 데이터 타입

Kotlin에서는 기본적으로 4개의 숫자형 데이터 타입을 제공합니다. 이 중에서 가장 작은 범위를 갖는 타입은 `Byte`이며, 8비트 크기를 갖습니다. 다음으로 작은 범위는 `Short`이며, 16비트 크기를 갖습니다. `Int`는 32비트 크기를 갖으며, `Long`은 64비트 크기를 갖습니다. 이러한 숫자형 데이터 타입들은 다음과 같이 선언할 수 있습니다.

| 타입    | 크기   |
| ----- | ---- |
| Byte  | 8비트  |
| Short | 16비트 |
| Int   | 32비트 |
| Long  | 64비트 |

### 연산자

Kotlin에서는 다양한 연산자를 활용하여 숫자형 데이터를 처리할 수 있습니다. 이러한 연산자는 산술 연산자, 비교 연산자, 논리 연산자 등이 있습니다.

#### 산술 연산자

Kotlin에서는 다음과 같은 산술 연산자를 사용할 수 있습니다.

| 연산자 | 설명  |
| --- | --- |
| +   | 더하기 |
| -   | 빼기  |
| \*  | 곱하기 |
| /   | 나누기 |
| %   | 나머지 |

#### 비교 연산자

Kotlin에서는 다음과 같은 비교 연산자를 사용할 수 있습니다.

| 연산자 | 설명     |
| --- | ------ |
| ==  | 같다     |
| !=  | 다르다    |
| <   | 작다     |
| >   | 크다     |
| <=  | 작거나 같다 |
| >=  | 크거나 같다 |

#### 논리 연산자

Kotlin에서는 다음과 같은 논리 연산자를 사용할 수 있습니다.

| 연산자  | 설명          |
| ---- | ----------- |
| &&   | 논리 곱 (AND)  |
| \|\| | 논리 합 (OR)   |
| !    | 논리 부정 (NOT) |

### 숫자형 데이터 타입 간 변환

Kotlin에서는 숫자형 데이터 타입 간 변환을 위해 다음과 같은 함수를 제공합니다.

* `toByte()`
* `toShort()`
* `toInt()`
* `toLong()`
* `toFloat()`
* `toDouble()`
* `toChar()`

이러한 함수를 사용하여, 다른 숫자형 데이터 타입으로 변환할 수 있습니다. 예를 들어, `Int` 타입의 변수를 `Long` 타입으로 변환하려면 다음과 같이 작성할 수 있습니다.

```kotlin
kotlinCopy codeval intNum: Int = 123
val longNum: Long = intNum.toLong()
```

위 코드에서는 `Int` 타입의 `intNum` 변수를 `Long` 타입으로 변환하여, `longNum` 변수에 대입하고 있습니다.

또한, Kotlin에서는 숫자형 데이터 타입 간의 자동 형 변환도 지원합니다. 즉, 작은 데이터 타입에서 큰 데이터 타입으로의 자동 형 변환이 가능합니다. 예를 들어, `Byte` 타입의 변수를 `Int` 타입의 변수에 대입하면, `Byte` 타입의 변수가 자동으로 `Int` 타입으로 형 변환되어 대입됩니다.

```kotlin
kotlinCopy codeval byteNum: Byte = 10
val intNum: Int = byteNum
```

### 부호 없는 정수형 데이터 타입

Kotlin에서는 부호 없는 정수형 데이터 타입으로 `UByte`, `UShort`, `UInt`, `ULong`을 제공합니다. 부호 없는 정수형 데이터 타입은 부호 있는 정수형 데이터 타입과 다르게 음수 값을 표현할 수 없으며, 양수 값의 범위가 더 넓습니다.

| 타입     | 크기   |
| ------ | ---- |
| UByte  | 8비트  |
| UShort | 16비트 |
| UInt   | 32비트 |
| ULong  | 64비트 |

### 부호 없는 정수형 데이터 타입 사용

부호 없는 정수형 데이터 타입은 다음과 같이 선언할 수 있습니다.

```kotlin
kotlinCopy codeval ubyteNum: UByte = 10u
val ushortNum: UShort = 100u
val uintNum: UInt = 1000u
val ulongNum: ULong = 10000uL
```

부호 없는 정수형 데이터 타입의 리터럴 값은 `u` 또는 `U`를 사용하여 표기합니다. `ULong` 타입의 경우, `L` 또는 `l`을 사용하여 표기합니다.

부호 없는 정수형 데이터 타입은 부호 있는 정수형 데이터 타입과 마찬가지로 산술 연산자, 비교 연산자, 논리 연산자 등을 사용할 수 있습니다.

```kotlin
kotlinCopy codeval ubyteNum1: UByte = 10u
val ubyteNum2: UByte = 20u

val result1 = ubyteNum1 + ubyteNum2
val result2 = ubyteNum1 > ubyteNum2

println("result1: $result1") // 30
println("result2: $result2") // false
```

### 불리언 데이터 타입

Kotlin에서는 불리언 데이터 타입으로 `Boolean`을 제공합니다. `Boolean` 타입은 참(true)과 거짓(false) 두 가지 값만 가질 수 있습니다.

### 불리언 데이터 타입 사용

`Boolean` 타입의 변수는 다음과 같이 선언할 수 있습니다.

```kotlin
kotlinCopy codeval isTrue: Boolean = true
val isFalse: Boolean = false
```

불리언 데이터 타입은 주로 조건문(if문, when문)에서 사용됩니다.

```kotlin
kotlinCopy codeval x = 10
val y = 5

if (x > y) {
    println("x is greater than y")
} else {
    println("y is greater than x")
}
```

```kotlin
kotlinCopy codeval isSunny = true
val isWarm = false

val isNiceDay = isSunny && isWarm
val isBadDay = isSunny || isWarm
val isNotSunny = !isSunny

println("isNiceDay: $isNiceDay") // false
println("isBadDay: $isBadDay") // true
println("isNotSunny: $isNotSunny") // false
```

### 문자열 템플릿

```kotlin
kotlinCopy codeval name = "Alice"
val age = 25

val message = "My name is $name and I am $age years old."
println(message)
```

위 코드에서는 `$` 기호를 사용하여 `name`과 `age` 변수의 값을 문자열에 포함하고 있습니다. 이러한 문자열 템플릿을 사용하면, 변수나 식의 값을 쉽게 문자열에 포함할 수 있으며, 가독성이 좋아집니다.

또한, `${}` 기호를 사용하여 변수나 식의 값을 조합하여 표현식을 작성할 수도 있습니다.

```kotlin
kotlinCopy codeval x = 10
val y = 5

val message = "The sum of $x and $y is ${x + y}."
println(message)
```

위 코드에서는 `${}` 기호를 사용하여 `x`와 `y` 변수의 값을 더한 결과를 문자열에 포함하고 있습니다. 이러한 표현식은 `${}` 기호 안에 임의의 표현식을 작성할 수 있으며, 변수나 식의 값을 조합하여 복잡한 표현식을 만들 수도 있습니다.

### 문자열 리터럴

Kotlin에서는 문자열을 작성할 때, 다음과 같이 3가지 종류의 따옴표(`""`, `''`, `""" """`)를 사용할 수 있습니다.

* `""`: 일반적인 문자열을 나타냅니다.
* `''`: 문자 하나를 나타냅니다.
* `""" """`: 여러 줄에 걸쳐 문자열을 나타냅니다.

```kotlin
kotlinCopy codeval s1 = "Hello"
val c1 = 'H'
val s2 = """
            Hello,
            World!
        """.trimIndent()
```

위 코드에서는 `""` 기호를 사용하여 `s1` 변수에 일반적인 문자열을, `''` 기호를 사용하여 `c1` 변수에 문자 하나를, `""" """` 기호를 사용하여 `s2` 변수에 여러 줄에 걸쳐 문자열을 각각 대입하고 있습니다.
