# JVM Overview

Java는 전 세계에서 가장 큰 기술 플랫폼 중 하나로, 약 900만에서 1000만 명의 개발자를 보유하고 있는 것은 의심의 여지가 없습니다. 설계상 많은 개발자들이 자신이 사용하는 플랫폼의 저수준 복잡성에 대해 알 필요가 없습니다.

그러나 성능에 관심이 있는 개발자들에게는 JVM 기술 스택의 기본을 이해하는 것이 중요합니다.\
JVM 기술을 이해하면 개발자는 더 나은 소프트웨어를 작성할 수 있으며, 성능 관련 문제를 조사하는 데 필요한 이론적 배경을 제공합니다.

## 인터프리팅과 클래스 로딩

Java 가상 머신을 정의하는 명세서에 따르면, JVM은 스택 기반의 인터프리터 기계입니다.\
이는 물리적 하드웨어 CPU와 같은 레지스터 대신, 부분 결과를 저장하는 실행 스택을 사용하며, 그 스택의 최상위 값(또는 값들)을 조작하여 계산을 수행함을 의미합니다.

JVM 인터프리터의 기본 동작은 프로그램의 각 opcode를 마지막 것과 독립적으로 처리하며, 평가 스택을 사용하여 중간 값을 저장하는 "while 루프 내의 스위치"로 생각할 수 있습니다.

<details>

<summary>참고 사항(Main)</summary>

```aiignore
Oracle/OpenJDK VM(HotSpot)의 내부를 자세히 들여다보면, 실제 프로덕션 급 Java 인터프리터의 상황은 더 복잡할 수 있지만, 현재로서는 while 루프 내의 스위치라는 정신적 모델이 적절합니다.
애플리케이션을 `java HelloWorld` 명령어로 실행하면, 운영 체제가 가상 머신 프로세스(java 바이너리)를 시작합니다. 이는 Java 가상 환경을 설정하고, HelloWorld 클래스 파일의 사용자 코드를 실제로 실행할 스택 머신을 초기화합니다.
애플리케이션의 진입점은 `HelloWorld.class`의 `main()` 메서드가 됩니다. 이 클래스에 제어를 넘기려면, 실행을 시작하기 전에 가상 머신이 이 클래스를 로드해야 합니다.

이를 달성하기 위해 Java 클래스 로딩 메커니즘이 사용됩니다. 새로운 Java 프로세스가 초기화될 때, 클래스 로더 체인이 사용됩니다. 
초기 로더는 Bootstrap 클래스 로더로, 핵심 Java 런타임의 클래스를 포함합니다. 
Bootstrap 클래스 로더의 주요 목적은 최소한의 클래스 집합(예: `java.lang.Object`, `Class`, `Classloader` 등)을 로드하여 다른 클래스 로더가 시스템의 나머지를 가져올 수 있도록 하는 것입니다.

```

</details>

<details>

<summary>참고 사항(클래스 로딩)</summary>

```aiignore
Java는 클래스 로더를 자체 런타임과 타입 시스템 내의 객체로 모델링하기 때문에 초기 클래스 로더 집합을 가져오는 방법이 필요합니다. 
그렇지 않으면 클래스 로더가 무엇인지 정의하는 데 순환 문제가 발생할 것입니다.

그 다음으로 Extension 클래스 로더가 생성됩니다. 이는 Bootstrap 클래스 로더를 부모로 정의하며, 필요 시 부모에게 위임합니다. 
확장은 널리 사용되지는 않지만, 특정 운영 체제와 플랫폼에 대한 오버라이드 및 네이티브 코드를 제공할 수 있습니다. 
특히 Java 8에서 도입된 Nashorn JavaScript 런타임은 Extension 로더에 의해 로드됩니다.

마지막으로 Application 클래스 로더가 생성됩니다. 이는 정의된 클래스패스에서 사용자 클래스를 로드하는 역할을 담당합니다. 
일부 텍스트에서는 이를 "System" 클래스 로더라고 부르지만, 이는 시스템 클래스를 로드하지 않기 때문에 피해야 합니다. 
Application 클래스 로더는 매우 자주 사용되며, Extension 로더를 부모로 가집니다.

Java는 프로그램 실행 중 처음 만난 새로운 클래스에 대한 종속성을 로드합니다. 클래스 로더가 클래스를 찾지 못하면, 일반적으로 부모에게 조회를 위임하는 동작을 합니다.   
조회 체인이 Bootstrap 클래스 로더에 도달하고 클래스가 발견되지 않으면 `ClassNotFoundException`이 발생합니다. 개발자들은 빌드 프로세스가 프로덕션에서 사용될 정확히 동일한 클래스패스를 사용하여 컴파일되도록 해야 이 잠재적인 문제를 완화할 수 있습니다.

일반적인 상황에서는 Java가 클래스를 한 번만 로드하고, 클래스 객체가 런타임 환경에서 해당 클래스를 나타내도록 생성됩니다. 
그러나 동일한 클래스가 다른 클래스 로더에 의해 두 번 로드될 수 있다는 점을 인식하는 것이 중요합니다. 
결과적으로 시스템의 클래스는 로드에 사용된 클래스 로더와 완전한 클래스 이름(패키지 이름 포함)에 의해 식별됩니다.
```

</details>

## 바이트코드 실행

Java 소스 코드는 실행 전에 상당한 수의 변환 과정을 거친다는 점을 이해하는 것이 중요합니다.\
첫 번째 단계는 종종 더 큰 빌드 프로세스의 일부로 호출되는 Java 컴파일러 `javac`를 사용한 컴파일 단계입니다.

`javac`의 역할은 Java 코드를 바이트코드를 포함하는 `.class` 파일로 변환하는 것입니다.\
이는 Java 소스 코드를 비교적 직접적으로 번역하여 이루어집니다.\
컴파일 중에 `javac`는 거의 최적화를 수행하지 않으며, 생성된 바이트코드는 표준 `javap`과 같은 디스어셈블리 도구로 볼 때 여전히 상당히 읽기 쉽고 Java 코드로 인식할 수 있습니다.

바이트코드는 특정 기계 아키텍처에 종속되지 않은 중간 표현입니다.\
기계 아키텍처로부터 분리함으로써 포터블리티를 제공하며, 이는 이미 개발된(또는 컴파일된) 소프트웨어가 JVM이 지원하는 모든 플랫폼에서 실행될 수 있음을 의미합니다.\
또한 Java 언어로부터의 추상화를 제공합니다.\
이는 JVM이 코드를 실행하는 방식을 이해하는 데 첫 번째 중요한 통찰력을 제공합니다.

<details>

<summary>참고 사항(클래스 파일)</summary>

```aiignore
Java 언어와 Java 가상 머신은 어느 정도 독립적이므로, JVM의 "J"는 다소 오해의 소지가 있을 수 있습니다. 
JVM은 유효한 클래스 파일을 생성할 수 있는 모든 JVM 언어를 실행할 수 있기 때문입니다. 

어떤 소스 코드 컴파일러를 사용하든, 결과 클래스 파일은 VM 명세서(Table 2-1)에 의해 매우 잘 정의된 구조를 가지고 있습니다. 
JVM에 의해 로드되는 모든 클래스는 실행을 허용하기 전에 예상된 형식에 맞는지 확인됩니다.
```

</details>

| **구성 요소**                 | **설명**                           |
| ------------------------- | -------------------------------- |
| **매직 넘버(Magic number)**   | 0xCAFEBABE                       |
| **클래스 파일 형식 버전**          | 클래스 파일을 컴파일하는 데 사용된 마이너 및 메이저 버전 |
| **상수 풀(Constant pool)**   | 클래스의 상수 풀                        |
| **액세스 플래그(Access flags)** | 클래스가 추상적인지, 정적인지 등의 수정자를 결정      |
| **이 클래스(This class)**     | 현재 클래스의 이름                       |
| **슈퍼 클래스(Superclass)**    | 슈퍼 클래스의 이름                       |
| **인터페이스(Interfaces)**     | 클래스의 인터페이스                       |
| **필드(Fields)**            | 클래스의 필드                          |
| **메서드(Methods)**          | 클래스의 메서드                         |
| **속성(Attributes)**        | 클래스의 속성 (예: 소스 파일 이름 등)          |

모든 클래스 파일은 매직 넘버 `0xCAFEBABE`로 시작하며, 이는 클래스 파일 형식에 대한 준수를 나타내는 첫 번째 4바이트입니다.\
다음 4바이트는 클래스 파일을 컴파일하는 데 사용된 마이너 및 메이저 버전을 나타내며, 이는 대상 JVM이 클래스 파일을 컴파일한 버전보다 낮지 않은지 확인하기 위해 검사됩니다.\
마이너 및 메이저 버전은 클래스 로더에 의해 호환성이 있는지 확인하기 위해 검사되며, 호환되지 않을 경우 런타임에 `UnsupportedClassVersionError`가 발생하여 실행 중인 JVM이 컴파일된 클래스 파일보다 낮은 버전임을 나타냅니다.

<details>

<summary>참고</summary>

```aiignore
매직 넘버는 Unix 환경에서 파일의 유형을 식별하는 방법을 제공하지만(Windows는 일반적으로 파일 확장자를 사용), 한 번 결정되면 변경하기 어렵습니다. 
불행히도, 이는 Java가 다가오는 미래에도 다소 부끄럽고 성차별적인 `0xCAFEBABE`를 계속 사용해야 함을 의미합니다. 
그러나 Java 9에서는 모듈 파일용 매직 넘버 `0xCAFEDADA`가 도입되었습니다.

상수 풀은 코드 내의 상수 값을 보유합니다. 예를 들어, 클래스, 인터페이스 및 필드의 이름 등이 포함됩니다. 
JVM이 코드를 실행할 때, 상수 풀 테이블은 런타임의 메모리 레이아웃에 의존하지 않고 값을 참조하는 데 사용됩니다.

액세스 플래그는 클래스에 적용된 수정자를 결정하는 데 사용됩니다. 
플래그의 첫 번째 부분은 클래스가 공개(public)인지 여부와 같은 일반 속성을 식별하며, 그 다음으로 클래스가 최종(final)인지, 서브클래싱할 수 없는지 등을 식별합니다. 
플래그의 마지막 부분은 클래스 파일이 소스 코드에 없는 합성 클래스인지, 주석 유형인지, 열거형(enum)인지 여부를 나타냅니다.

`This class`, `Superclass`, 및 `Interface` 항목은 상수 풀 내의 인덱스로 클래스의 타입 계층을 식별합니다. 필드와 메서드는 필드 또는 메서드에 적용되는 수정자를 포함한 서명과 유사한 구조를 정의합니다. 
일련의 속성은 더 복잡하고 고정 크기가 아닌 구조를 나타내는 데 사용됩니다. 
```

</details>

```java
public class HelloWorld {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Hello World");
        }
    }
}
```

Java에는 `.class` 파일을 검사할 수 있는 `javap`라는 클래스 파일 디스어셈블러가 함께 제공됩니다.\
`HelloWorld.class` 파일을 가져와 `javap -c HelloWorld` 명령을 실행하면 다음과 같은 출력이 나옵니다

```
public class HelloWorld {
    public HelloWorld();
    Code:
       0: aload_0
       1: invokespecial #1 <init>":()V
       4: return
    // Method java/lang/Object.<init>():V
    public static void main(java.lang.String[]);
    Code:
       0: iconst_0
       1: istore_1
       2: iload_1
       3: bipush 10
       5: if_icmplt 8
       8: getstatic #2 // Field java/lang/System.out:Ljava/io/PrintStream;
      11: ldc #3 // String Hello World
      13: invokevirtual #4 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      16: iinc 1, 1
      19: goto 2
      22: return
}
```

이 레이아웃은 `HelloWorld.class` 파일의 바이트코드를 설명합니다.\
더 자세한 내용은 `javap`에 `-v` 옵션을 추가하여 전체 클래스 파일 헤더 정보와 상수 풀 세부 사항을 제공할 수 있습니다.\
클래스 파일에는 두 개의 메서드가 포함되어 있지만, 소스 파일에 제공된 단일 `main()` 메서드만 포함되어 있습니다.

생성자에서 실행되는 첫 번째 명령어는 `aload_0`으로, 이는 `this` 참조를 스택의 첫 번째 위치에 놓습니다.\
그 다음 `invokespecial` 명령어가 호출되는데, 이는 슈퍼 생성자를 호출하거나 객체를 생성하는 특정한 인스턴스 메서드를 호출합니다.\
기본 생성자에서는 호출이 `Object`의 기본 생성자와 일치합니다. 이는 오버라이드가 제공되지 않았기 때문입니다.

<details>

<summary>참고 사항</summary>

```aiignore
JVM의 opcode는 간결하며, 타입, 연산 및 로컬 변수, 상수 풀, 스택 간의 상호 작용을 나타냅니다.

`main()` 메서드로 넘어가면, `iconst_0`은 정수 상수 0을 평가 스택에 푸시합니다. 
`istore_1`은 이 상수 값을 루컬 변수 오프셋 1(루프의 i 변수)에 저장합니다. 루컬 변수 오프셋은 0부터 시작하지만, 인스턴스 메서드의 경우 0번째 항목은 항상 `this`입니다. 
오프셋 1의 변수는 다시 스택에 로드되고, `bipush 10`이 푸시되어 비교에 사용됩니다(`if_icmplt`, "if integer compare less than"). 

초기 몇 번의 반복에서는 이 비교 테스트가 실패하므로, 명령어 8로 계속 진행됩니다. 
여기서 `System.out`의 정적 메서드가 해상되고, 상수 풀에서 "Hello World" 문자열이 로드됩니다. 
다음 `invokevirtual`은 클래스 기반의 인스턴스 메서드를 호출합니다. 
정수가 증가되고 `goto`가 호출되어 명령어 2로 루프가 돌아갑니다.

이 과정은 `if_icmplt` 비교가 결국 성공할 때까지 계속됩니다(루프 변수가 10 이상이 될 때). 
그 반복에서는 제어가 명령어 22로 넘어가고 메서드가 반환됩니다.
```

</details>

## HotSpot 소개

1999년 4월, Sun은 성능 측면에서 Java에 가장 큰 변화를 가져온 것 중 하나인 HotSpot 가상 머신을 도입했습니다. HotSpot 가상 머신은 C 및 C++와 같은 언어와 비교할 때 동등하거나 더 나은 성능을 가능하게 하는 Java의 핵심 기능입니다

언어 및 플랫폼 설계는 종종 원하는 기능과 관련된 결정을 내리고 트레이드오프를 하는 것을 포함합니다. 이 경우, "제로-오버헤드 추상화(zero-cost abstractions)"와 같은 아이디어에 의존하는 언어와, 개발자 생산성과 "일을 끝내는 것(getting things done)"을 우선시하는 언어 간의 분할이 이루어집니다.

### **—Bjarne Stroustrup**

제로 오버헤드 원칙은 이론상으로는 훌륭하지만, 이는 언어 사용자 모두가 운영 체제와 컴퓨터가 실제로 작동하는 저수준 현실을 다루어야 함을 요구합니다.\
이는 원시 성능을 주요 목표로 하지 않는 개발자들에게 큰 추가 인지적 부담을 줍니다.

뿐만 아니라, 소스 코드를 빌드 시점에 특정 플랫폼의 기계 코드로 컴파일해야 한다는 점도 문제입니다—이는 보통 Ahead-of-Time (AOT) 컴파일이라고 합니다.\
이는 인터프리터, 가상 머신 및 포터블리티 레이어와 같은 대체 실행 모델이 제로 오버헤드를 제공하지 않기 때문입니다.

또한 "사용하는 것은 자동화된 시스템보다 더 잘 손으로 코딩할 수 없다"는 문구는 여러 가지 문제를 내포하고 있습니다.\
이는 개발자가 자동화된 시스템(예: 컴파일러)보다 더 나은 코드를 생성할 수 있다는 것을 전제로 합니다.\
이는 안전한 가정이 아닙니다. 대부분의 사람들은 더 이상 어셈블리 언어로 코딩하고 싶어하지 않으므로, 자동화된 시스템(예: 컴파일러)을 사용하는 것은 분명히 대부분의 프로그래머에게 이점이 있습니다.

Java는 제로 오버헤드 추상화 철학을 따르지 않았습니다.\
대신, HotSpot 가상 머신이 취한 접근 방식은 프로그램의 런타임 동작을 분석하고 성능에 가장 큰 이점을 제공할 수 있는 곳에 지능적으로 최적화를 적용하는 것입니다.\
HotSpot VM의 목표는 JVM에 맞추기 위해 프로그램을 왜곡하는 대신, 관용적인 Java를 작성하고 좋은 설계 원칙을 따를 수 있도록 하는 것입니다.

### Just-in-Time 컴파일 소개

Java 프로그램은 바이트코드 인터프리터에서 실행을 시작하며, 여기서 명령어가 가상화된 스택 머신에서 수행됩니다.\
CPU로부터의 추상화는 클래스 파일의 포터블리티 이점을 제공하지만, 최대 성능을 얻으려면 프로그램이 CPU에서 직접 실행되어야 하며, CPU의 네이티브 기능을 활용해야 합니다.

HotSpot은 이를 달성하기 위해 프로그램의 단위를 인터프리터된 바이트코드에서 네이티브 코드로 컴파일합니다.\
HotSpot VM에서 컴파일 단위는 메서드와 루프입니다. 이는 Just-in-Time (JIT) 컴파일이라고 합니다.

JIT 컴파일은 애플리케이션이 인터프리터 모드에서 실행되는 동안 애플리케이션을 모니터링하고 가장 자주 실행되는 코드 부분을 관찰하여 작동합니다.\
이 분석 과정에서 더 정교한 최적화를 가능하게 하는 프로그램적 추적 정보가 캡처됩니다.\
특정 메서드의 실행이 임계값을 초과하면, 프로파일러는 해당 코드 섹션을 컴파일하고 최적화하려고 합니다.

JIT 접근 방식에는 많은 이점이 있지만, 주요 이점 중 하나는 인터프리터 단계에서 수집된 추적 정보를 기반으로 컴파일러 최적화 결정을 내릴 수 있다는 것입니다.\
이는 HotSpot이 더 정보에 입각한 최적화를 수행할 수 있게 합니다.

뿐만 아니라, HotSpot은 수백 년 이상의 공학적 개발 시간이 축적되었으며, 거의 모든 새로운 릴리스마다 새로운 최적화와 이점이 추가되고 있습니다.\
이는 HotSpot의 최신 릴리스에서 실행되는 모든 Java 애플리케이션이 VM에 존재하는 새로운 성능 최적화를 활용할 수 있음을 의미하며, 재컴파일할 필요도 없습니다.

<details>

<summary>바이트 코드</summary>

```aiignore
Java 소스에서 바이트코드로 변환되고 이제 또 다른 단계의 JIT 컴파일을 거치면서, 실제로 실행되는 코드는 작성된 원본 Java 소스 코드와 매우 다르게 변경되었음을 인식하는 것이 중요합니다.   
이는 핵심 통찰력이며, 성능 관련 조사에 접근하는 우리의 방식을 좌우할 것입니다.   
JVM에서 실행되는 JIT 컴파일된 코드는 원본 Java 소스 코드와 전혀 다르게 보일 수 있습니다.  

C++와 같은 언어(그리고 신흥 언어인 Rust)는 더 예측 가능한 성능을 가지는 경향이 있지만, 이는 사용자에게 많은 저수준 복잡성을 강요하는 대가를 치러야 합니다.

AOT 컴파일러는 코드가 다양한 프로세서에서 실행될 수 있도록 컴파일하며, 특정 프로세서 기능을 사용할 수 있다고 가정할 수 없게 만듭니다. 반면, 프로파일 가이드 최적화(PGO)를 사용하는 환경(예: Java)은 런타임 정보를 사용하는 방식에서 대부분의 AOT 플랫폼이 불가능한 방법을 사용할 수 있는 잠재력을 가지고 있습니다. 이는 동적 인라이닝과 가상 호출 최적화와 같은 성능 향상을 제공할 수 있습니다.

HotSpot은 VM 시작 시 정확한 CPU 유형을 감지할 수 있으며, 이를 통해 특정 프로세서 기능에 설계된 최적화를 사용할 수 있습니다.
```

</details>

<details>

<summary>프로세서 감지</summary>

```
정확한 프로세서 기능을 감지하는 기술은 JVM 인트린식(JVM intrinsics)으로 알려져 있으며, `synchronized` 키워드로 도입된 인트린식 락과 혼동해서는 안 됩니다. 
PGO와 JIT 컴파일에 대한 전체 논의는 9장과 10장에서 다룰 것입니다.

HotSpot이 취한 정교한 접근 방식은 대부분의 일반 개발자에게 큰 이점을 제공하지만, 
이러한 트레이드오프(제로 오버헤드 추상화를 포기함)는 고성능 Java 애플리케이션의 특정 경우에 
개발자가 Java 애플리케이션이 실제로 실행되는 방식을 지나치게 단순화된 사고 방식으로 오해하지 않도록 매우 신중해야 함을 의미합니다.
```

</details>

## JVM 메모리 관리

C, C++, Objective-C와 같은 언어에서는 프로그래머가 메모리 할당과 해제를 관리할 책임이 있습니다.\
메모리와 객체의 수명을 직접 관리하는 이점은 더 예측 가능한 성능과 객체 생성 및 삭제에 자원 수명을 묶을 수 있다는 점입니다.\
그러나 이러한 이점은 큰 비용을 수반합니다—정확하게 메모리를 계산할 수 있어야 하며, 이는 프로그래머에게 큰 부담을 줍니다.

불행히도, 수십 년간의 실무 경험을 통해 많은 개발자들이 메모리 관리에 대한 관용구와 패턴을 제대로 이해하지 못한다는 것이 드러났습니다.\
이후 C++와 Objective-C의 최신 버전은 표준 라이브러리에서 스마트 포인터 관용구를 사용하여 이를 개선했습니다. 그러나 Java가 만들어질 당시에는 메모리 관리가 애플리케이션 오류의 주요 원인이었습니다.\
이는 개발자와 관리자들 사이에서 비즈니스 가치를 제공하는 대신 언어 기능을 다루는 데 많은 시간을 소비하는 것에 대한 우려를 불러일으켰습니다.

Java는 가비지 컬렉션(GC)으로 알려진 프로세스를 사용하여 자동으로 관리되는 힙 메모리를 도입함으로써 문제를 해결하려고 했습니다.\
간단히 말해, 가비지 컬렉션은 JVM이 할당을 위해 더 많은 메모리가 필요할 때 더 이상 필요하지 않은 메모리를 회수하고 재사용하기 위해 트리거되는 비결정론적 프로세스입니다.

그러나 GC에 대한 이야기는 그리 간단하지 않으며, Java의 역사 동안 다양한 GC 알고리즘이 개발되고 적용되었습니다.\
GC는 비용을 수반합니다: 실행될 때 종종 "Stop The World"라는 뜻으로, GC가 진행되는 동안 애플리케이션이 일시 중지됩니다.\
보통 이러한 일시 중지는 매우 작게 설계되었지만, 애플리케이션에 압력이 가해질수록 증가할 수 있습니다.

### 스레딩과 Java 메모리 모델

Java가 첫 번째 버전에서 도입한 주요 발전 중 하나는 멀티스레드 프로그래밍에 대한 내장 지원이었습니다.\
Java 플랫폼은 개발자가 새로운 실행 스레드를 생성할 수 있도록 합니다.

```java
Thread t = new Thread(() -> { System.out.println("Hello World!"); });
t.start();
```

뿐만 아니라, Java 환경 자체도 본질적으로 멀티스레드이며, JVM도 마찬가지입니다.

대부분의 주요 JVM 구현에서, 각 Java 애플리케이션 스레드는 정확히 하나의 전용 운영 체제 스레드와 일치합니다.\
모든 Java 애플리케이션 스레드를 실행하기 위해 스레드 풀을 공유하는 대안(그린 스레드라고 함)은 허용할 만한 성능 프로파일을 제공하지 못하고 불필요한 복잡성을 추가하는 것으로 나타났습니다.

<details>

<summary>스레드와 스레드 풀</summary>

```aiignore
모든 JVM 애플리케이션 스레드는 해당 스레드 객체의 `start()` 메서드가 호출될 때 생성되는 고유한 OS 스레드에 의해 백업된다고 가정하는 것이 안전합니다.

Java의 멀티스레딩 접근 방식 다음과 같은 기본 설계 원칙을 가지고 있습니다:
1. Java 프로세스의 모든 스레드는 단일, 공통 가비지 컬렉션 힙을 공유합니다.
2. 하나의 스레드가 생성한 객체는 해당 객체에 대한 참조를 가진 다른 모든 스레드가 접근할 수 있습니다.
3. 객체는 기본적으로 변경 가능하며, 즉, 프로그래머가 `final` 키워드를 사용하여 명시적으로 불변으로 표시하지 않는 한 객체 필드의 값은 변경될 수 있습니다.

Java 메모리 모델(JMM)은 메모리에 대한 공식적인 모델로, 다른 실행 스레드가 객체에 저장된 변경 값을 어떻게 보는지를 설명합니다. 
운영 체제 스케줄러가 스레드를 CPU 코어에서 강제로 내보낼 수 있기 때문입니다. 
다른 스레드가 실행을 시작하고 원래 스레드가 객체를 완전히 처리하기 전에 객체에 접근하여 손상되거나 유효하지 않은 상태를 볼 수 있게 합니다.

Java가 제공하는 핵심 방어 수단은 상호 배제 락(mutual exclusion lock)뿐이며, 이는 실제 애플리케이션에서 사용하기 매우 복잡할 수 있습니다. 
```

</details>

### JVM 모니터링 및 툴링

JVM은 성숙한 실행 플랫폼으로, 실행 중인 애플리케이션의 계측, 모니터링, 가시성을 위한 다양한 기술 대안을 제공합니다.\
JVM 애플리케이션을 위한 이러한 유형의 도구들에 사용할 수 있는 주요 기술은 다음과 같습니다

* **Java Management Extensions (JMX)**
* **Java 에이전트**
* **JVM Tool Interface (JVMTI)**
* **Serviceability Agent (SA)**
