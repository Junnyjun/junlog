# 펑터

#### 펑터(Functor)

매핑할 수 있는 것(can be mapped over)이라는 행위를 선언한 타입 클래스.

#### 메이비(Maybe)

어떤 값이 있을 수도 있고 없을 수도 있는 하스켈의 컨테이너형 타입이다. \
자바8에는 옵셔널(Optional), 스칼라에서는 옵션(Option)이라는 이름으로 동일한 역할을 하는 컨테이너형 타입을 제공한다. \
코틀린에서는 물음표를 사용해서 널에 대한 처리가 가능하기 때문에 내부적으로 메이비와 같은 타입을 지원하지 않는다.

#### 이더(Either)

레프트(Left) 또는 라이트(Right) 타입만 허용하는 대수적 타입.

#### 일급 함수

함수에 대해 다음 세 가지 조건을 만족하는 것.

```
함수를 함수의 매개변수로 넘길 수 있다.
함수를 함수의 반환값으로 돌려 줄 수 있다.
함수를 변수나 자료구조에 담을 수 있다.
```

### 펑터의 법칙(functor law)

펑터가 되기 위해서 만족해야 하는 두 가지 법칙.

*   항등 함수(identity function)에 펑터를 통해서 매핑하면,&#x20;

    환되는 펑터는 원래의 펑터와 같다.
* 두 함수를 합성한 함수의 매핑은 각 함수를 매핑한 결과를 합성한 것과 같다.

#### 펑터 제 1 법칙

fmap을 호출할 때 항등 함수 id를 입력으로 넣은 결과는 반드시 항등 함수를 호출한 결과와 동일해야 한다.

`표현식: fmap(identity()) == identity()`

#### 펑터 제 2 법칙

함수 f와 g를 먼저 합성하고 fmap 함수의 입력으로 넣어서 얻은 결괏값은 함수 f를 fmap에 넣어서 얻은 함수와 g를 fmap에 넣어서 얻은 함수를 합성한 결과와 같아야 한다.

`표현식: fmap(f compose g) == fmap(f) compose fmap(g)`

## &#x20;<a href="#undefined" id="undefined"></a>

함수형 프로그래밍은 카테고리 이론(Category theory)이라는 수학적 원리를 토대로 만들어졌다.

어떤 값을 담을 수 있는 타입은 항상 펑터로 만드는 것을 생각해 볼 수 있다.

Functor의 타입 생성자는 매개변수가 한 개이기 때문에 타입이 다른 두 개 이상의 매개변수를 가지는 타입을 Functor의 인스턴스로 만들기 위해서는 fmap 함수에 의해서 변경되는 매개변수를 제외한 나머지 값들을 고정해야 한다.

함수형 언어에서는 함수도 타입이다.

Functor 타입 클래스의 타입 생성자는 하나의 매개변수만 가진다. 하지만 함수의 타입은 함수의 매개변수가 여러 개인 경우, 하나 이상의 타입 매개변수를 가질 수 있다. 따라서 변경할 수 있는 타입 한 개를 제외한 나머지는 고정해야 한다.

펑터는 왜 펑터의 법칙을 만족하도록 만들어야 할까?

* 타입이 두 가지 펑터의 법칙을 만족하면 어떻게 동작할 것인가에 대한 동일한 가정을 할 수 있다. 즉, fmap 함수를 호출했을 때, 매핑하는 동작 외에 어떤 것도 하지 않는다는 것을 알 수 있다.
* 이러한 예측 가능성은 함수가 안정적으로 동작할 뿐만 아니라 더 추상적인 코드로 확장할 때도 도움이 된다.
