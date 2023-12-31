# 함수형 프로그래밍 입

### 수학에서의 함수

수학에서의 함수는 동일한 입력에 출력값이 언제나 같아야 한다. \
즉, Side Effect가 없어야 한다

#### 부작용 이란?

함수 내부가 외부를 참조하거나 외부의 상태를 변화를 주는것이다.\
가장 대표적인 부작용으로 입출력(I/O)가 있다

#### 순수 함수

위와 같은 부작용이 없는 함수를 순수 함수라고 한다.\
외부의 변화를 가하거나, 외부의 상태를 참조하는 것 같은 부작용이 생기지 않으며, \
같은 입력에 같은 출력만을 반환해야 한다.

외부 상황에따라 다른 결과를 내놓지 않기에 **순수**하다고 표현한다

#### 변경 불가 함수

순수 함수와 함계 언어의 핵심 개념을 구성하고 있다.\
한번 값이 설정되면 다시 값을 바꿀수 없는 변수를 말한다.

Const, final과 같은 키워드를 사용하여 변경을 불가능하게 만들며, 재할당시 컴파일 에러가 발생한다.

### 절차 지향 언어 VS 선언형 언어

컴퓨터가 처리하는 과정을 모사한 순서대로 명령을 처리하는 언어가 **절차 지향 언어**이다\
절차 지향언어에서는 코드 한줄 한줄을 명령문 이라 하는 반면, \
함수형 언어에서의 코드 한줄 한줄은 식이다.

작동 원리가 아니라, 식&답을 요구하는 방식의 프로그래밍 언어를 선언형 언어라 한다.

#### 람다 대수

간단한 람다식을 예로 들어 설명한다.

```
λx.x + 1
```

λ 옆 X는 변수고 x+1이 식이다.\
x 를 임의의 문자로 바꾸어도 람다식이 의미하는 것은 동일하다.\
위 식의 값을 적용하는 방식은 다음과 같다

```mathml
(λx.x + 1)1  // 람다식에 1 대입
-> 1 + 1
-> 2
```

이렇게 단순한 치환의 공식을 람다라 하며, 고차함수의 근간이 되는 개념이다.
