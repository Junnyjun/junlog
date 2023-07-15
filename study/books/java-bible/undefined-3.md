# 배열

### 배열 <a href="#1" id="1"></a>

> 배열은 캍은 타입의 여러 변수를 하나의 묶음으로 다루는 것

#### 배열 선언과 생성 <a href="#undefined" id="undefined"></a>

| 선언방법        | 선언 예           |
| ----------- | -------------- |
| 타입\[ ] 변수이름 | `int[] score;` |
| 타입 변수이름\[ ] | `int score[];` |

* 배열을 선언하는 것은 단지 생성된 배열을 다루기 위한 참조변수를 위한 공간이 만들어질 뿐이고, 배열을 연산자 'new'와 함께 생성해야만 비로소 값을 저장할 수 있는 공간이 만들어지는 것이다.

> 타입\[ ] 변수이름;\
> 변수이름 = new 타입\[길이];
>
> ```java
> int[] score = new int[5];
> ```

* 배열의 길이는 int범위의 양의 정수(0도 포함)이어야 한다.
* 배열은 한번 생성하면 길이를 변경할 수 없다.
* 초기화하지 않은 경우 기본값으로 초기화 된다.

> * 변수 타입에 따른 기본값

| 자료형              | 기본값      |
| ---------------- | -------- |
| boolean          | false    |
| char             | '\u0000' |
| byte, short, int | 0        |
| long             | 0L       |
| float            | 0.0f     |
| double           | 0.0      |
| 참조형 변수           | null     |

#### 배열 초기화 <a href="#undefined" id="undefined"></a>

```java
int[] score = new int[]{ 50, 60, 70, 80, 90};
int[] score = { 50, 60, 70, 80, 90};
```

* 단, 배열의 선언과 생성을 따로하는 경우에는 생략할 수 없다.

```java
int[] score;
score = new int[]{ 50, 60, 70, 80, 90};	//OK
score = { 50, 60, 70, 80, 90};			//에러
```



### 2. String 배열 <a href="#2-string" id="2-string"></a>

* String 클래스는 cahr배열에 기능을 추가한 것이다.
* String객체는 읽을 수만 있을 뿐 내요을 변경할 수 없다.
* char배열과 String은 서로 변환이 가능하다.

```java
char[] chArr = { 'A', 'B', 'C' };
String str = new String(chArr);
char[] tmp = str.toCharArray();
```

* 커맨드라인을 통해 입력된 문자열은 String 배열에 담겨서 main매서드의 매개변수(args)에 전달된다.

```java
public static void main(String[] args) {...}
```



### 3. 다차원 배열 <a href="#3" id="3"></a>

#### 2차원 배열의 선언과 인덱스 <a href="#2" id="2"></a>

| 선언방법             | 선언 예             |
| ---------------- | ---------------- |
| 타입\[ ]\[ ]] 변수이름 | `int[][] score;` |
| 타입 변수이름\[ ]\[ ]  | `int score[][];` |
| 타입\[ ] 변수이름\[ ]  | `int[] score[];` |

#### 2차원 배열의 초기화 <a href="#2" id="2"></a>

```java
int[][] arr = new int[][]{ {1, 2, 3}, {4, 5, 6} };
int[][] arr = { {1, 2, 3}, {4, 5, 6} };
```

#### 가변 배열 <a href="#undefined" id="undefined"></a>

```java
int[][] score = new int[5][];
score[0] = new int[4];
score[1] = new int[3];
score[2] = new int[2];
score[3] = new int[2];
score[4] = new int[3];
```
