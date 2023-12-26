# 자바 모듈 시스템

#### 14.1 압력 : 소프트웨어 유추

지금까지는 이해하고 유지보수하기 쉬운 코드를 구현하는 데 사용할 수 있는 새로운 언어 기능을 살펴봤다.

하지만 이러한 부분은 저수준의 영역에 해당하며, 소프트웨어 아키텍처에 해당하는 고수준의 영역에서는 생산성을 높일 수 있도록 추론하기 쉬운 소프트웨어 프로젝트가 필요하다.

**14.1.1 관심사 분리(SoC, Separation of concerns)**

컴퓨터 프로그램을 고유의 기능으로 나누는 동작을 권장하는 원칙이다.

SoC는 다음과 같은 장점을 가진다.

* 개별 기능을 따로 작업할 수 있으므로 팀이 쉽게 협업할 수 있다.
* 개별 부분을 재사용하기 쉽다.
* 전체 시스템을 쉽게 유지보수할 수 있다.

#### 14.1.2 정보 은닉

세부 구현을 숨김으로 코드를 관리하고 보호하는 데 유용한 원칙이다.

캡슐화된 코드는 다른 부분과 고립되어 있어 내부적인 변화가 의도치않게 영향을 미칠 가능성이 줄어든다.

***

#### 14.2 자바 모듈 시스템을 설계한 이유

**14.2.1 모듈화의 한계**

자바 9 이전까지는 모듈화된 소프트웨어 프로젝트를 만드는데 한계가 있었다. 자바는 클래스, 패키지, JAR 세 가지 수준의 코드 그룹화를 제공하는데, 이 중 패키지와 JAR 수준에서는 캡슐화가 거의 지원되지 않았다.

&#x20;

제한된 가시성 제어

한 패키지의 클래스와 인터페이스를 다른 패키지로 공개하려면 public으로 선언해야 한다. 결과적으로 이들 클래스와 인터페이스가 모두 공개되고, 사용자가 이 내부 구현을 마음대로 사용할 수 있게 된다.

&#x20;

클래스 경로

자바에서는 클래스를 모두 컴파일 한 다음 JAR 파일에 넣고 클래스 경로(class path)에 이 JAR 파일을 추가하여 번들로 사용할 수 있다.

이러한 클래스 경로와 JAR 조합에는 몇 가지 약점이 존재한다.

1. 클래스 경로에는 같은 클래스를 구분하는 버전 개념이 없다. 예를들어 파싱 라이브러리의 JSONParser 클래스를 지정할 때 버전 1.0인지 2.0인지 지정할 수 없으므로 클래스 경로에 두 버전의 같은 라이브러리가 존재하면 어떤 문제가 발생할 지 예측할 수 없다.
2. 클래스 경로는 명시적인 의존성을 지원하지 않는다. 한 JAR가 다른 JAR에 포함된 클래스 집합을 사용한다고 명시적으로 의존하지 않기 때문에 누락된 JAR를 확인하기 어렵고, 충돌이 발생할 수 있다.

**14.2.2 거대한 JDK**

자바 개발 키트(JDK)는 자바 프로그램을 만들고 실행하는 데 도움을 주는 도구의 집합이다.

시간이 흐르면서 JDK의 덩치가 커졌고, 모바일이나 JDK 전부를 필요로 하지 않는 클라우드 환경에서 문제가 되었다.

또한 자바의 낮은 캡슐화 지원 때문에 JDK의 내부 API가 외부에 공개되었고, 여러 라이브러리에서 JDK 내부 클래스를 사용했다. 결과적으로 호환성을 깨지 않고는 관련 API를 바꾸기 어려운 상황이 되었다.

&#x20;

이런 문제들 때문에 JDK 자체도 모듈화할 수 있는 자바 모듈 시스템 설계의 필요성이 제기되었다.

즉 JDK에서 필요한 부분만 골라서 사용하고, 클래스 경로를 쉽게 유추할 수 있으며, 플랫폼을 진화시킬 수 있는 강력한 캡슐화를 제공할 새로운 건축 구조가 필요했다.

***

#### 14.3 자바 모듈 : 큰 그림

자바 8에서는 모듈이라는 새로운 자바 프로그램 구조 단위를 제공한다.

모듈은 module 이라는 새 키워드에 이름과 바디를 추가해서 정의한다.

모듈 디스크립터는 module-info.java라는 파일에 저장되고, 보통 패키지와 같은 폴더에 위치한다.

<figure><img src="https://blog.kakaocdn.net/dn/uzydJ/btrg2MqZlYM/yVYRUuQFKG7wMJdqrETP31/img.png" alt=""><figcaption></figcaption></figure>

***

&#x20;

#### 14.5 여러 모듈 활용하기

**14.5.1 expotrs 구문**

exports는 다른 모듈에서 사용할 수 있도록 특정 패키지를 공개 형식으로 만든다.

기본적으로 모듈 내의 모든 것은 캡슐화되며, 다른 모듈에서 사용할 수 있는 기능만 무엇인지 명시적으로 결정해야한다.

```
module expense.readers {
  //exports 패키지명
  exports com.example.expense.readers;
  exports com.example.expense.readers.file;
  exports com.example.expense.readers.http;
}
```

**14.5.2 requires 구문**

requires는 의존하고 있는 모듈을 지정한다.&#x20;

```
module expense.readers {
  //requires 모듈명
  requires java.base;
}
```

**14.5.3 이름 정하기**

오라클에서는 패키지명처럼 인터넷 도메인명을 역순으로 모듈의 이름을 정하도록 권고하고있다.

모듈명은 노출된 주요 API 패키지와 이름이 같아야하며, 모듈이 패키지를 포함하지 않거나 어떤 다른 이유로 노출된 패키지 중 하나와 이름이 일치하지 않는 상황을 제외하면 모듈명은 작성자의 인터넷 도메인 명을 역순으로 시작해야 한다.

***

#### 14.6 컴파일과 패키징

메이븐을 이용해 컴파일한다면, 먼저 각 모듈에 pom.xml을 추가해야 한다. 또한 전체 프로젝트 빌드를 조정할 수 있도록 모든 모듈의 부모 모듈에도 pom.xml을 추가하고 의존성을 기술해야 한다.

***

#### 14.7 자동 모듈

httpClient 외부 라이브러리도 의존성을 기술하여 모듈로 사용할 수 있다. 모듈화가 되어있지 않은 라이브러리도 자동 모듈이라는 형태로 적절하게 변환한다. 다만, 자동 모듈은 암묵적으로 자신의 모든 패키지를 노출한다.

***

#### 14.8 모듈 정의와 구문들

**14.8.3 requires transitive**

다른 모듈이 제공하는 공개 형식을 한 모듈에서 사용할 수 있다고 지정할 수 있다.

```
module com.iteratrlearning.ui {
  requires transitive com.iteratrlearning.core;
  
  export com.iteratrlearning.ui.panels;
  export com.iteratrlearning.ui.widgets;
}

module com.iteratrlearning.application {
  requires com.iteratrlearning.ui;
}
```

결과적으로 com.iteratrlearning.application 모듈은 com.iteratrlearning.core에서 노출한 공개형식에 접근할 수 있다.

**14.8.4 exports to**

exports to 구문을 이용해 가시성을 좀 더 정교하게 제어할 수 있다.

```
module com.iteratrlearning.ui {
  requires com.iteratrlearning.core;
  
  export com.iteratrlearning.ui.panels;
  export com.iteratrlearning.ui.widgets to
    com.iteratrlearning.ui.widgetuser;
}
```

&#x20;

com.iteratrlearning.ui.widgets의 접근 권한을 가진 사용자의 권한을 com.iteratrlearning.ui.widgetuser로 제한할 수 있다.

**14.8.4 open과 opens**

모듈 선언에 open 한정자를 이용하면 모든 패키지를 다른 모듈에 반사적으로 접근을 허용할 수 있다.

리플렉션 때문에 전체 모듈을 개방하지 않고도 opens 구문을 모듈 선언에 이용해 필요한 개별 패키지만 개방할 수 있다.

```
open module com.iteratrlearning.ui {
}
```
