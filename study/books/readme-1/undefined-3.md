# 객체 지향 프로그래밍

### OO란 무엇인가?

* 데이터와 함수의 조합?\
  이는 터무니없는 말이다. o.f() f(o)와 다르다는 의미를 내포하기 때문이다.
* 실제 세계를 모델링하는 새로운 방법?\
  이는 얼버무리는 수준에 지나지 않는다. 의도가 불분명하며, 그 정의가 너무 모호하다.
* 캡슐화, 상속, 다형성?\
  OO(Obejct-Oreiented)가 이 세 가지 개념을 적절하게 조합한 것이거나, 또는 최소한 세 가지 요소를 반드시 지원해야 한다고 말한다. 이 세 가지 개념에 대해 살펴보자.

### 캡슐화?

OO를 정의하는 요소 중 하나로 캡슐화를 언급하는 이유는 데이터와 함수를 쉽교 효율적으로 캡슐화하는 방법을 OO언어가 제공하기 때문이다.&#x20;

* 데이터와 함수가 응집력 있게 구정된 집단을 서로 구분 짓는 선을 그을 수 있다.
* 구분선 바깥에서 데이터는 은닉되고, 일부 함수만이 외부에 노출된다.
* 이 개념들이 OO언어에서는 클래스의 private 멤버 데이터와 public 멤버 함수로 표현된다.

이 개념들이 OO에만 국한된 것은 아니다. 아래 C코드를 보자

&#x20;

point.h

```
struct Point;
struct Point* makePoint(double x, double y);
double distance (struct Point *p1, struct Point *p2)
```

point.c

```
#include "point.h"
#include <stdlib.h>
#include <math.h>

struct Point {
	double x,y;
};


struct Point *makePoint(double x, double y) {
	struct Point* p = malloc(sizeof(struct Point));
 	p->x = x;
    p->y = y;
    return p;
}

dpible distance(struct Point* p1, struct Point* p2) {
	double dx = p1->x - p2->x;
    double dy = p1->y - p2->y;
    return sqrt(dx*dx+dy*dy);
}
```

위 코드에서 point.h를 사용하는 측에서 struct Point의 멤버에 접근할 방법이 전혀 없다.

사용자는 makePoint() 함수와 distance() 함수를 호출할 수는 있지만, Point 구조체의 데이터 구조체가 어떻게 구현되어 있는지 알 수 없다.

이것이 완벽한 캡슐화이다.

&#x20;

이후 C++라는 형태로 OO가 등장했고, C가 제공하던 완벽한 캡슐화가 깨졌다.

C++ 컴파일러는 기술적인 이유(컴파일러는 클래스의 인스턴스 크기를 알 수 있어야 한다.)로 클래스의 멤버 변수를 해당 클래스의 헤더 파일에 선언할 것을 요구했다. 따라서 앞의 Point 프로그램은 C++에서 아래와 같이 변경해야 한다.

&#x20;

&#x20;

point.h

```
class Point {
public:
	Poimt(double x, double y);
    double distance(const Point& p) const;
   
private:
	double x;
    double y;
};
```

&#x20;

point.cc

```
#include "point.h"
#include <math.h>

Point::Point(double x, double y)
: x(x), y(y)
{}

double Point::distance(const Point& p) coonst {
	double dx = x-p.x;
    double dy = y-p.y;
    return sqrt(dx*dx + dy*dy);
}
```

&#x20;

이제 point.h 헤더 파일을 사용하는 측에서는 멤버 변수인 x와 y를 알게 된다.

물론 컴파일러가 멤버 변수에 접근하는 일은 컴파일러가 막겠지만, 사용자는 멤버 변수가 존재한다는 사실 자체를 알게 된다. 이는 캡슐화가 깨진 것이다.&#x20;

언어에 public, private protected 키워드를 도입함으로써 불완전한 캡슐화를 어느 정도 보완하기는 했지만 이는 임시방편일 뿐이다.

&#x20;

자바와 C#에서는 헤더와 구현체를 분리하는 방식을 모두 버렸고, 이로 인해 캡슐화는 더욱 심하게 훼손되었다.

&#x20;

위와 같은 이유때문에, OO가 강력한 캡슐화에 의존한다는 정의는 받아들이기 힘들다.

&#x20;

&#x20;

### 상속?

상속은 OO 언어가 확실히 제공하고 있다.

하지만 상속이란 단순히 어떤 변수와 함수를 하나의 유효 범위로 묶어서 재정의하는 일에 불과하다.&#x20;

사실상 아래 코드와 같이 언어의 도움 없이 구현할 수 있었다.

&#x20;

&#x20;

namedPoint.h

```
struct NamedPoint;

struct NamedPoint* makeNamedPoint(double x, double y, char* name);
void setName(struct NamedPoint* np, char* name);
char* getName(struct NamedPoint* np);
```

&#x20;

namedPoint.c

```
#include "namedPoint.h"
#include <stdlib.h>

struct NamedPoint {
	double x, y;
	char* name;
};

struct NamedPoint* makeNamedPoint(double x, double y, char* name) {
	struct NamedPoint* p = malloc(sizeof(struct NamedPoint));
	p->x = x;
	p->y = y;
	p->name = name;
	return p;
}

void setName(struct NamedPoint* np, char* name) {
	np->name = name;
}

char* getName(struct NamedPoint* np) {
	return np->name;
}
```

&#x20;

main.c

```
#include "point.h"
#include "namedPoint.h"
#include <stdio.h>

int main(int ac, char** av) {
	struct NamedPoint* origin = makeNamedPoint(0.0, 0.0, "origin");
	struct NamedPoint* upperRight = makeNamedPoint(1.0, 1.0, "upperRight");
	printf("distance=%f\n", distance((struct Point*) origin, (struct Point*) upperRight));
}
```

&#x20;

main 프로그램을 살펴보면 NamedPoint 데이터 구조가 마치 Point 데이터 구조로부터 파생된 구조인 것처럼 동작한다는 사실을 볼 수 있다. 이는 NamedPoint에 선언된 두 변수의 순서가 Point와 동일하기 때문이다. 이는 NamedPoint가 순전히 Point를 포함하는 상위 집합으로, Point에 대응하는 멤버 변수의 순서가 그대로 유지되기 때문이다.

C++에서는 이 방법을 이용해서 단일 상속을 구현했다.

&#x20;

이 방법은 상속과 비슷하다고 말하기에는 어폐가 있다

* 실제 상속만큼 편리한 방식은 절대 아니다.
* 이 방법으로 다중 상속을 구현하기에 어려움이 있다.

또한 main.c에서 NamedPoint 인자를 Point로 타입을 강제로 변환한 점도 확인할 수 있다.

OO언어에서는 이러한 업캐스팅(upcasting)이 암묵적으로 이뤄진다.

&#x20;

따라서 OO언어가 완전히 새로운 개념을 만들지는 못했지만, 데이터 구조에 가면을 씌우는 일을 상당히 편리한 방식으로 제공했다고 볼 수는 있다.

&#x20;

### 다형성?

OO 언어 이전에 다형성을 표현가능한 언어가 있는가? 아래 C로 작성한 복사 프로그램을 살펴보자.

```
#include <strdio.h>

void copy() {
	int c;
    while ((c=getchar()) != EOF)
    	putchar(c);
}
```

getchar() 함수는 STDIN에서 문자를 읽는다. 그러면 DTDIN은 어떤 장치인가?

putchar() 함수는 STDOUT으로 문자를 쓴다. 그런데 STDOUT은 또 어떤 장치인가?

&#x20;

이러한 함수는 다형적이다. 즉 행위가 STDIN과 STDOUT의 타입에 의존한다.

&#x20;

STDIN과 STDOUT은 사실상 자바 형식의 인터페이스로, 자바에서는 각 장치별로 구현체가 있다.

C에서는 그렇다면 어떤 방식으로 문자를 읽는 장치 드라이버를 호출할 수 있는가?

&#x20;

유닉스에서는 모든 입출력 장치 드라이버가 다섯 가지 표준 함수를 제공할 것을 요구한다.

열기(open), 닫기(close), 읽기(read), 쓰기(write), 탐색(seek)이 바로 이 표준 함수들이다.

FILE 데이터 구조는 이들 다섯 함수를 가리키는 포인터들을 포함한다. 이 예제의 경우라면 다음과 같을 것이다.

&#x20;

```
struct FILE {
	void(*open)(char* name, int mode);
	void(*close)();
	int(*read)();
	void(*write)(char);
	void(*seek)(long index, int mode);
};
```

&#x20;

&#x20;

&#x20;콘솔용 입출력 드라이버에서는 이들 함수를 아래와 같이 정의하며, FILE 데이터 구조를 함수에 대한 주소와 함께 로드할 것이다.

&#x20;

```
#include "file.h"

void open(char *name, int mode) {/*...*/ }
void close() {/*...*/ }
int read() {
	int c;
	/*...*/
	return c;
}
void write(char c) {/*...*/ }
void seek(long index, int mode} {/*...*/}

struct FILE console = { open, close, read, write, seek };
```

이제 STDIN을 FILE\*로 선언하면, STDIN은 콘솔 데이터 구조를 가리키므로, getchar()는 아래와 같은 방식으로 구현할 수 있다.

&#x20;

```
extern struct FILE* StDIN;

int getchar() {
	return STDIN->read();
}
```

&#x20;

다시 말해 getchar()는 STDIN으로 참조되는 FILE 데이터 구조의 read 포인터가 가리키는 함수를 단순히 호출할 뿐이다.

이처럼 단순한 기법이 모든 OO가 지닌 다형성의 근간이 된다.

&#x20;

예를 들어 C++에서는 클래스의 모든 가상 함수는 vtable이라는 테이블에 포인터를 가지고 있고, 모든 가상 함수 호출은 이 테이블을 거치게 된다. 파생 클래스의 생성자는 생성하려는 객체의 vtable을 단순히 자신의 함수들로 덮어 쓸 뿐이다.

&#x20;

> 함수를 가리키는 포인터를 응용한 것이 다형성이다.

1940년대 후반 폰 노이만 아키텍처가 처음 구현된 이후 프로그래머는 다형적 행위를 수행하기 위해 함수를 가리키는 포인터를 사용해 왔다. 따라서 OO가 새롭게 만든 것은 전혀 없다.

&#x20;

하지만 이 말이 완전히 옳은 말은 아니다.

&#x20;

OO 언어는 다형성을 제공하지는 못했지만, 다형성을 좀 더 안전하고 더욱 편리하게 사용할 수 있게 해준다.

&#x20;

함수에 대한 포인터를 직접 사용하여 다형적 행위를 만드는 이 방식에는 문제가 있는데, 함수 포인터는 위험하다는 사실이다.

* 프로그래머는 이들 포인터를 초기화하는 관계를 준수해야 한다.
* 이들 포인터를 통해 모든 함수를 호출하는 관례를 지켜야 한다.

프로그래머가 위 관례를 망각하게 되면 버그가 발생하고, 이러한 버그는 수정이 어렵다.

&#x20;

OO 언어는 이러한 관례를 없애주며, 따라서 실수할 위험이 없다.

OO 언어를 사용하면 다형성은 대수럽지 않은 일이 된다.

OO 언어는 과거 C 프로그래머가 꿈에서야 볼 수 있던 강력한 능력을 제공한다.

&#x20;

이러한 이유로 OO는 제어흐름을 간접적으로 전환하는 규칙을 부과한다고 결론 지을 수 있다.

&#x20;

### 다형성이 가진 힘?

이전 복사 프로그램 예제를 다시 살펴보자.

새로운 입출력 장치가 생긴다면? 이 새로운 장비에서도 복사 프로그램이 동작하도록 만들려면 어떻게 수정해야 하는가?

수정할 필요가 전혀 없다.

&#x20;

복사 프로그램의 소스 코드는 입출력 드라이버의 소스코드에 의존하지 않기 때문이다.

&#x20;

즉, 입출력 드라이버가 FILE에 정의된 다섯 가지 표준 함수를 구현한다면, 복사 프로그램에서는 이 입출력 드라이버를 얼마든지 사용할 수 있다.

&#x20;

입출력 드라이버가 복사 프로그램의 플러그인(plugin)이 된 것이다.

&#x20;

왜 유닉스 운영체제는 입출력 장치들을 플러그인 형태로 만들었는가?

프로그램이 장치 독립적이어야 하기 때문이다.

프로그램에 다른 장치에서도 동일하게 동작할 수 있도록 하는 것이 우리가 진정 바랐던 일임을 깨달았기 때문이다.

&#x20;

플러그인 아키텍처는 입출력 장치 독립성을 지원하기 위해 만들어졌고, 등장 이후 거의 모든 운영체제에서 구현되었다.

그럼에도 프로그래머는 이러한 개념을 확정하여 적용하지 않았다, 함수를 가리키는 포인터는 위험을 수반하기 때문이었다.

&#x20;

하지만 OO의 등장으로 언제 어디서든 플러그인 아키텍처를 적용할 수 있게 되었다.

&#x20;

### \*의존성 역전

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/QUICX/btqZ7KQyNMz/mnhtGCH4s4jqwopu7ko8uk/img.png" alt=""><figcaption><p>소스코드 의존성 vs. 제어흐름</p></figcaption></figure>

위 전형적힌 호출 트리를 보자.

main 함수가 고수준 함수를 호출하고, 고수준 함수는 다시 중간 수준 함수를 호출하며, 중간 수준 함수는 다시 저수준 함수를 호출한다. 이러한 호출 트리에서 소스 코드 의존성의 방향은 반드시 제어흐름을 따르게 된다.

&#x20;이러한 제약 조건으로 인해 제어흐름은 시스템의 행위에 따라 결정되며, 소스 코드 의존성은 제어흐름에 따라 결정된다.

&#x20;

하지만 다형성을 적용하면 얘기가 다르다.

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/ZRyr8/btqZ6jTd4fm/vR7BCYvJGpyC3YrkkssjM1/img.png" alt=""><figcaption><p>의존성 역전</p></figcaption></figure>

위 그림에서 HL1 모듈은 ML1 모듈의 F() 함수를 호출한다. 소스코드에서는 HL1 모듈은 인터페이스를 통해 F() 함수를 호출한다. 이 인터페이스는 런타임에는 존재하지 않는다. HL1은 단순히 ML1 모듈의 함수 F()를 호출할 뿐이다.

&#x20;

하지만 ML1과 I 인터페이스 사이의 소스코드 의존성(상속 관계)이 제어 흐름과는 반대이다.

&#x20;

이를 의존성 역전이라 부른다.

> OO 언어가 다형성을 안전하고 편리하게 제공한다는 것은 소스 코드 의존성을 어디에서든 역전시킬 수 있다는 뜻이기도 하다.

이러한 접근법을 사용한다면 OO 언어로 개발된 시스템을 다루는 소프트웨어 아키텍트는 시스템의 소스 코드 의존성 전부에 대해 방향을 결정할 수 있는 절대적인 권한을 갖는다.

&#x20;

즉, 소스 코드 의존성이 제어흐름의 방향과 일치되도록 제한되지 않는다.

&#x20;

&#x20;

아래 예시 처럼 업무 규칙이 데이터베이스와 사용자 인터페이스에 의존하는 대신에, 시스템의 소스 코드 의존성을 반대로 배치하여 데이터베이스와 UI가 업무 규칙에 의존하게 만들 수 있다.

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/bqHt74/btqZ7LWgRoA/eLqnJWxbNSWhbl66CJmYyK/img.png" alt=""><figcaption><p>데이터베이스와 사용자 인터페이스가 업무 규칙에 의존한다.</p></figcaption></figure>

&#x20;

즉 UI와 데이터베이스가 업무 규칙의 플러그인이 된다는 뜻이다.

다시 말해, 업무 규칙의 소스 코드에서는 UI나 데이터베이스를 호출하지 않는다.

&#x20;

결과적으로 업무 규칙(비즈니스 로직), UI, 데이터베이스는 세 가지로 분리된 컴포넌트 또는 배포 가능한 단위(jar, DLL, Gem 등)로 컴파일 할 수 있고, 이 배포 단위들의 의존성 역시 소스 코드 사이의 의존성과 같다.

&#x20;

따라서 업무 규칙을 포함하는 컴포넌트는 UI와 데이터베이스를 포함하는 컴포넌트에 의존하지 않는다.

&#x20;

따라서 업무 규칙을 UI와 데이터베이스와는 독립적으로 배포할 수 있다.

&#x20;

UI나 데이터베이스에서 발생한 변경사항은 업무 규칙에 일절 영향을 미치지 않는다. 즉 이들 컴포넌트는 독립적으로 배포 가능하다.

&#x20;

배포 독립성 - 컴포넌트의 소스 코드가 변경되면, 해당 코드가 포함된 컴포넌트만 다시 배포하면 된다.

개발 독립성 - 배포 독립성이 있으면,서로 다른 팀에서 각 모듈을 독립적으로 개발할 수 있다.

&#x20;

&#x20;

### 결론

OO란 다형성을 이용하여 전체 시스템의 모든 소스 코드 의존성에 대한 절대적인 제어 권한을 획득할 수 있는 능력이다.

OO를 사용하면 아키텍트는 플러그인 아키텍처를 구성할 수 있고, 이를 통해 고수준의 정책을 포함하는 모듈은 저수준의 세부사항을 포함하는 모듈에 대해 독립성을 보장할 수 있다.

&#x20;

저수준의 세부사항은 중요도가 낮은 플러그인 모듈로 만들 수 있고, 고수준의 정책을 포함하는 모듈과는 독립적으로 개발하고 배포할 수 있다.
