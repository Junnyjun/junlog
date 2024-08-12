# Execution Engine

Execution Engine은 Java Virtual Machine(JVM) 내에서 클래스가 로드된 후 이를 실행하기 위해 필요한 핵심 모듈입니다. ClassLoader를 통해 JVM 내로 배치된 클래스는 Execution Engine을 통해 Bytecode를 실행하게 되며, 이 과정에서 JVM은 지정된 명령어 집합(Instruction Set)에 따라 동작합니다. 이러한 명령어들은 Bytecode에 정의되어 있으며, 실제로 클래스의 Bytecode를 실행하는 것이 바로 Execution Engine의 역할입니다.

#### Java 프로그램의 실행 과정

Java 프로그램이 JVM에서 실행되는 과정을 간략하게 살펴보겠습니다:

1. **소스 코드 작성 및 컴파일**: 개발자가 작성한 Java 소스 코드는 `.java` 확장자로 저장됩니다. 이 파일은 JDK에 포함된 `javac` 컴파일러를 통해 컴파일되어 `.class` 파일로 변환됩니다. 이 `.class` 파일은 JVM이 이해할 수 있는 Bytecode를 포함하고 있으며, Bytecode는 JVM의 명령어 집합으로 변환된 소스 코드입니다.
2. **클래스 로딩**: 컴파일된 `.class` 파일은 JVM의 ClassLoader에 의해 JVM 내로 로드되고, 이어서 링크(Link) 작업이 수행됩니다. 이 과정이 완료되면 클래스는 Runtime Data Areas의 Method Area에 배치됩니다. Method Area는 JVM 내에서 클래스 메타데이터, 메서드 데이터, 그리고 바이트코드가 저장되는 메모리 영역입니다.
3. **Execution Engine에 의한 실행**: 로드된 클래스의 Bytecode는 Execution Engine에 의해 실행됩니다. Execution Engine은 Bytecode를 읽고, 이를 JVM 명령어로 해석하여 실행합니다. 이때 JVM 명령어는 Instruction Set으로 정의되며, 각각의 Instruction은 Opcode와 Operand로 구성됩니다. Opcode는 수행할 작업을 의미하고, Operand는 작업의 대상이 되는 데이터를 가리킵니다.

#### Bytecode와 Instruction의 구조

Bytecode는 Java 소스 코드의 실행 흐름을 나타내는 명령어들의 집합입니다. Bytecode는 일반적으로 세 가지 부분으로 구성됩니다:

1. **메서드 시작 지점으로부터의 Offset**: Bytecode에서 특정 명령어가 메서드 내에서 위치하는 지점을 나타냅니다. 이 정보는 명령어 간의 제어 흐름을 관리하는 데 사용됩니다.
2. **Opcode와 Operand**: Opcode는 수행할 작업의 종류를 나타내며, Operand는 작업에 필요한 데이터를 나타냅니다. 예를 들어, `iconst_0`은 상수 0을 스택에 푸시하는 명령어이고, `istore_1`은 스택의 값을 로컬 변수에 저장하는 명령어입니다.
3. **주석**: Bytecode는 주석을 포함할 수 있으며, 이는 Bytecode를 해석하고 이해하는 데 도움을 주지만 실제 실행에는 영향을 미치지 않습니다.

#### Execution Engine의 동작 방식

Execution Engine은 JVM 내에서 Bytecode를 해석하고 실행하는 모듈입니다. 이는 두 가지 주요 방식으로 작동합니다:

1. **인터프리터 방식**: Execution Engine이 Bytecode를 한 줄씩 읽고, 이를 해석하여 즉시 실행하는 방식입니다. 이 방식의 장점은 Bytecode를 해석하는 시간이 짧다는 점이지만, 실행 속도는 느릴 수 있습니다. 특히, 루프와 같은 반복적인 작업에서 성능 저하가 발생할 수 있습니다.
2. **JIT(Just-In-Time) 컴파일러 방식**: JIT 컴파일러는 인터프리터 방식의 단점을 보완합니다. JVM은 처음에 Bytecode를 인터프리터 방식으로 실행하다가, 반복적으로 실행되는 코드 블록이 감지되면 JIT 컴파일러가 이를 네이티브 코드로 컴파일하여 성능을 향상시킵니다. 네이티브 코드로 컴파일된 후, JVM은 해당 코드를 직접 실행하여 속도를 극대화할 수 있습니다.

#### 성능 이슈와 최적화 기법

Java의 Execution Engine은 성능 최적화를 위해 다양한 기법을 사용합니다. 다음은 몇 가지 주요 성능 최적화 기법입니다:

1. **On-Stack Replacement (OSR)**: OSR은 JVM이 루프 내에서 반복적인 작업을 감지하면, 인터프리터로 실행 중이던 코드를 중단하고 JIT 컴파일러가 컴파일한 네이티브 코드로 대체하는 기법입니다. 이는 루프와 같은 반복적인 코드 블록에서 큰 성능 향상을 가져옵니다.
2. **Loop Unrolling**: 이 기법은 루프의 반복 횟수를 줄이기 위해 루프 본문을 여러 번 복사하여 실행하는 방식입니다. 이를 통해 루프의 오버헤드를 줄이고 명령어 실행 속도를 높일 수 있습니다.
3. **Dead Code Elimination**: 실행되지 않는 불필요한 코드를 제거하여 프로그램의 크기를 줄이고 실행 속도를 높이는 최적화 기법입니다. 예를 들어, 계산된 값이 사용되지 않는 경우 해당 계산 코드는 제거됩니다.
4. **Code Hoisting**: 이 기법은 루프 내에서 반복적으로 계산되는 값을 루프 외부로 이동시켜, 불필요한 연산을 줄이는 최적화를 수행합니다. 이를 통해 루프 내에서 중복 계산을 피하고 성능을 개선할 수 있습니다.

#### Execution Engine의 성능 이슈

Execution Engine은 Java 애플리케이션의 성능에 큰 영향을 미칩니다. 특히, 루프와 같은 반복적인 작업에서는 Bytecode가 계속해서 반복 실행되기 때문에 성능 저하가 발생할 수 있습니다. Java는 다차원 배열을 직접 지원하지 않으며, 배열 내에 배열을 포함하는 구조를 사용합니다. 이로 인해 메모리 사용이 비효율적일 수 있으며, 배열 접근 속도에서 성능 저하가 발생할 수 있습니다. 이러한 문제를 해결하기 위해 JVM 벤더들은 다양한 최적화 기술을 적용하여 성능 저하를 최소화하고 있습니다.

#### JVM의 기동 시간과 AOT 컴파일러

JVM은 기동 시 JIT 컴파일러가 많은 Bytecode를 네이티브 코드로 컴파일해야 하기 때문에 기동 시간이 길어질 수 있습니다. 이를 해결하기 위해 Java 6에서 Ahead-Of-Time(AOT) 컴파일러가 도입되었습니다. AOT 컴파일러는 JVM이 시작되기 전에 Bytecode를 네이티브 코드로 컴파일하여 Shared Cache에 저장합니다. 이 Shared Cache는 여러 JVM 인스턴스가 공유할 수 있으며, JVM 기동 시 이 네이티브 코드를 사용하여 기동 시간을 단축하고 초기 성능을 개선할 수 있습니다.

#### Hotspot 컴파일러의 동작 방식

Hotspot JVM은 두 가지 상이한 VM인 Client VM과 Server VM으로 구분됩니다. Client VM은 빠르고 가벼운 최적화를 제공하는 C1 컴파일러를 사용하며, Server VM은 보다 강력한 최적화를 제공하는 C2 컴파일러를 사용합니다. C1 컴파일러는 주로 정적인 컴파일을 수행하며, C2 컴파일러는 프로그램의 전반적인 최적화를 통해 성능을 극대화합니다.

Hotspot JVM은 이 두 가지 컴파일러를 통해 애플리케이션의 특성에 맞는 최적화 전략을 적용하여, 다양한 환경에서 최적의 성능을 제공합니다.

Execution Engine은 Java의 핵심 구성 요소 중 하나로, 다양한 최적화 기법을 통해 Java 애플리케이션의 성능을 극대화하며, Java의 "Write Once, Run Anywhere" 철학을 유지하면서도 높은 실행 성능을 보장하는 역할을 합니다.
