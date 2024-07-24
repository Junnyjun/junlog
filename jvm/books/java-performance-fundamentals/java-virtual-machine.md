# Java Virtual Machine

#### Java란 무엇인가?

"Java가 뭐야?"라는 질문을 받았을 때, 간단히 "프로그래밍 언어"라고 대답하는 경우가 많습니다. 하지만 Java는 단순히 프로그래밍 언어로 정의하기에는 너무나 큰 개념입니다. Java는 'Write Once, Run Everywhere'라는 철학으로 시작되어 발전한 개념입니다. 이 철학을 실현하기 위해 Java는 네 가지 상호 연관된 기술을 엮어놓았습니다:

1. The Java Programming Language
2. The Java Class File Format
3. The Java Application Programming Interface (Java API)
4. The Java Virtual Machine (JVM)

이 네 가지 기술을 살펴보기 전에, Java로 프로그램을 만들어 수행하는 과정을 생각해보겠습니다. Eclipse나 NetBeans 같은 IDE 환경이나 메모장 같은 텍스트 에디터를 사용하여 Java 소스 코드를 작성하면, `.java` 확장자를 가진 파일로 저장됩니다.

이 Java 소스 파일은 단순히 코드만을 담고 있는 파일일 뿐, 자체로는 실행이 불가능합니다. 이를 개발자의 의도대로 실행하기 위해서는 컴파일을 통해 `.class` 파일의 형태로 변경해주어야 합니다. 이 작업을 '컴파일(Compile)'이라고 합니다. Java에서는 보통 JDK에 내장되어 있는 'javac' 컴파일러를 이용하여 수행합니다. 이 작업을 거치면 소스 파일과 같은 이름이지만 `.class` 확장자를 가진 바이너리 파일이 생성됩니다.

이 `.class` 파일은 실행 가능한 형태의 파일입니다. 그러나, 실행할 수 있는 형태라는 것은 단지 실행이 가능하다는 의미이지, exe 파일처럼 더블클릭을 하거나 프롬프트 상에서 파일 이름을 주고 엔터를 친다고 해서 바로 실행되는 것은 아닙니다. 이 파일을 개발자의 의도대로 실행하기 위해서는 최소한 JRE(Java Runtime Environment)가 필요합니다. 물론 JDK에서도 가능합니다. JDK에는 JRE가 기본적으로 내장되어 있기 때문입니다.

이 프로그램을 실행하기 위해서는 'java'라는 프로그램을 호출하여 우리가 생성한 `.class` 파일을 인수로 제공하면 됩니다. 이때 'java' 프로그램은 Java 실행 환경에 `.class` 파일을 로딩하고, Java Virtual Machine(JVM)을 하나의 프로세스로 올리는 작업을 함께 수행합니다. 그 이후 클래스 파일을 분석하여 JRE 내의 Java Application Programming Interface(Java API)와 함께 프로그램을 실행하도록 하는 것입니다.

위의 과정 중에서 `.java`라는 확장자를 가진 소스 파일로 `.class` 파일을 생성하는 시점을 '컴파일 타임(Compile Time)'이라고 합니다. 그리고 컴파일을 거쳐 생성된 `.class` 파일을 실행하는 시점을 '런타임(Run Time)'이라고 합니다. 아래 그림은 Java 프로그램이 어떻게 수행되는지를 도식화한 것입니다.

<img src="../../../.gitbook/assets/file.excalidraw (49).svg" alt="" class="gitbook-drawing">

이 그림을 자세히 살펴보면 앞에서 언급한 Java를 구성하는 네 가지 기술들이 모두 포함되어 있는 것을 알 수 있습니다. 처음 프로그램을 작성할 때 사용한 것은 Java Programming Language입니다. 그리고 작성한 프로그램을 컴파일하면 Java Class File Format으로 변경됩니다. 이를 실행하기 위해서는 Java Virtual Machine을 구동시켜 클래스 파일을 로딩합니다.

JVM으로 로딩된 프로그램은 단독으로 수행하는 것이 아니라 Java Application Programming Interface와 동적으로 연결되어 비로소 실행되는 것입니다. 그렇다면 이 네 가지 기술은 각각 어떤 것이며 이들이 어떻게 Java의 철학을 실현하고 있는지 자세히 살펴보도록 하겠습니다.

**The Java Programming Language**

Java 언어는 객체지향 프로그래밍 언어로, 생산성과 동적인 면에서 큰 장점을 가지고 있습니다. Java는 비교적 최신의 소프트웨어 기술이 적용된 언어로, 객체지향 외에도 멀티스레딩, 구조화된 에러 핸들링, 가비지 컬렉션, 동적 링크 및 동적 확장 등의 기술들이 접목되어 있습니다.

특히, Java의 Class 파일은 실행 가능한 형태로 변환된 것이 아니라, JVM이 읽을 수 있는 형태로 번역된 것으로 이해할 수 있습니다. 이는 JVM 위에서 다시 실행 가능한 형태로 변형됩니다. 실행을 위한 링크 작업은 그때 일어나게 됩니다. Class 파일은 실행 시 링크를 할 수 있도록 Symbolic Reference만을 가지고 있습니다. 이 Symbolic Reference는 런타임 시점에서 메모리상에서 실제로 존재하는 물리적인 주소로 대체되는 작업인 링크가 이루어지기 때문에, 이를 '동적 링크(Dynamic Linking)'라고 합니다.

이 Dynamic Linking 기술 덕분에 Class 파일의 크기를 작게 유지할 수 있습니다. Java의 철학을 구현하는 의미로 확장됩니다. Java는 플랫폼에 독립적이기 때문에 컴파일된 파일만 있으면 그대로 실행이 가능합니다. 또한 네트워크를 통해 객체를 전송하고 배포하는 데 있어서 파일의 크기는 작을수록 유리합니다.

**The Java Class File Format**

Java는 개발자가 작성한 프로그램을 컴파일러를 통해 Class 파일로 재생성하는 과정을 거칩니다. 이렇게 생성된 Class 파일은 다음의 네 가지 특징을 가지게 됩니다.

* Compact한 형태
* Bytecode로의 변경
* 플랫폼 독립적
* 네트워크 바이트 오더의 사용

Java의 철학을 가장 두드러지게 대변하는 것이 바로 이 Class File Format입니다. Class 파일은 Bytecode를 Binary 형태로 담아놓은 것입니다. Bytecode는 JVM이 읽을 수 있는 언어를 의미합니다. 다시 말해, Java에서 컴파일이라고 하는 것은 텍스트 기반의 사용자 친화적인 Java 언어를 JVM이 읽을 수 있는 언어로 번역한 것입니다.

JVM은 Class를 로딩한 후 여기서 Bytecode를 읽어들여 실행 가능하도록 해석(Interpret)하는 과정을 거칩니다. Class가 로드된 후에는 JIT(Just-In-Time) Compiler나 Hotspot Compiler와 같은 Execution Engine을 거쳐 실행됩니다.

Bytecode는 Java의 철학을 실현하는 중요한 요소 중 하나입니다. Bytecode가 JVM을 위한 언어라는 점, 모든 코드가 Bytecode의 Binary 형태로 실체화된 Class라는 것, 이것이 플랫폼의 제약을 뛰어넘을 수 있게 되었다는 것을 의미합니다.

**The Java Application Interface**

Java Application Interface, 즉 Java API는 런타임 라이브러리의 집합으로, Java 프로그램이 운영체제의 자원과 상호작용할 수 있도록 돕는 인터페이스 역할을 합니다. Java API는 OS 시스템과 Java 프로그램 사이를 이어주는 가교 역할을 합니다. Java API는 네이티브 메소드를 통해 OS 자원과 연계되어 있으며, Java 프로그램과 맞닿아 있습니다. 이를 통해 Java 프로그램은 플랫폼에 상관없이 동일한 방식으로 동작할 수 있습니다.

**The Java Virtual Machine (JVM)**

Java의 네 가지 구성 요소 중 가장 핵심적인 것은 단연 Java Virtual Machine(JVM)입니다. JVM은 Java 프로그램을 실행하기 위한 가상 머신으로, 물리적인 형태가 아닌 소프트웨어로 존재하며, 하나의 개념으로 존재합니다. JVM은 정의된 스펙(Specification)을 구현한 독자적인 Runtime Instance입니다.

JVM의 역할은 우리가 작성한 Java 프로그램을 실행하는 것입니다. 이는 Class 파일을 JVM으로 로딩하고 Bytecode를 해석(Interpret)하며, 메모리와 같은 리소스를 할당하고 관리하며, 정보 처리 작업을 포괄합니다. 이 때 JVM은 Thread 관리 및 Garbage Collection과 같은 메모리 재생 작업도 수행합니다.

JVM의 기본적인 아키텍처는 다음과 같습니다

<img src="../../../.gitbook/assets/file.excalidraw (50).svg" alt="" class="gitbook-drawing">

JVM은 ClassLoader System을 통해 클래스 파일들을 로딩합니다. 로딩된 클래스 파일들은 Execution Engine을 통해 해석됩니다. 이렇게 해석된 프로그램은 Runtime Data Areas에 배치되어 실질적인 수행이 이루어집니다. 이러한 실행 과정 속에서 JVM은 필요에 따라 Thread Synchronization과 Garbage Collection 같은 관리 작업을 수행하게 됩니다.

이 책의 전반부는 JVM의 내부 메커니즘에 대해 알아보는 목적을 가지고 있습니다. 앞으로는 JVM의 각 모듈을 설명하는 식으로 전개해보도록 하겠습니다.
