# Runtime Data Area

#### JVM의 Runtime Data Areas

JVM의 Runtime Data Areas는 자바 프로그램이 실행되는 동안 다양한 데이터를 저장하는 메모리 영역입니다. 이 메모리 영역들은 JVM의 성능 및 안정성에 중요한 역할을 하며, 각기 다른 목적을 가지고 있습니다. Runtime Data Areas는 크게 다음과 같은 구성 요소로 나뉩니다:

1. **PC Registers**: 각 스레드마다 하나씩 존재하며, 현재 실행 중인 JVM 명령어의 주소를 저장합니다.
2. **Java Virtual Machine Stacks**: 각 스레드마다 하나씩 존재하며, 메소드 호출 시마다 생성되는 스택 프레임을 저장합니다. 각 스택 프레임에는 지역 변수, 운영 스택, 프레임 데이터 등이 포함됩니다.
3. **Native Method Stacks**: JNI를 통해 호출되는 네이티브 메소드의 실행 정보를 저장합니다. 이 스택도 각 스레드마다 하나씩 존재합니다.
4. **Method Area**: 모든 스레드가 공유하는 메모리 영역으로, 클래스와 인터페이스 메타데이터, 정적 변수, 상수 풀 등을 저장합니다. HotSpot JVM에서는 Permanent Generation(PermGen) 또는 Metaspace라는 이름으로 구현됩니다.
5. **Heap**: 모든 스레드가 공유하는 메모리 영역으로, 객체 인스턴스와 배열을 저장합니다. 힙은 Young Generation과 Old Generation으로 나뉘며, Young Generation은 Eden과 두 개의 Survivor 영역으로 구성됩니다. 객체는 처음에 Eden에 생성되고, 일정 시간이 지나면서 Survivor 영역과 Old Generation으로 이동합니다.

이제 각 구성 요소를 상세히 살펴보겠습니다.

#### PC Registers

**PC Registers**는 Program Counter Registers의 약자로, 각 스레드마다 하나씩 존재합니다. 각 스레드는 독립적인 실행 흐름을 가지며, PC 레지스터는 현재 실행 중인 JVM 명령어의 주소를 저장합니다. 이는 스레드가 다음에 실행할 명령어를 추적하는 데 사용됩니다. 만약 스레드가 네이티브 메소드를 실행 중이라면, 이 레지스터는 정의되지 않을 수 있습니다.

#### Java Virtual Machine Stacks

**Java Virtual Machine Stacks**는 자바 메소드가 호출될 때마다 생성되는 **스택 프레임**을 저장합니다. 각 스레드는 하나의 JVM 스택을 가지며, 이 스택은 메소드 호출과 반환의 구조를 관리합니다. 스택 프레임에는 다음과 같은 정보가 포함됩니다:

* **Local Variables**: 메소드의 매개변수와 지역 변수를 저장합니다.
* **Operand Stack**: 메소드가 실행되는 동안 중간 연산 결과를 저장합니다.
* **Frame Data**: 메소드 호출 시 필요한 추가적인 정보를 저장합니다.

JVM 스택은 스레드에 종속적이므로, 스레드가 종료되면 JVM 스택도 해제됩니다.

#### Native Method Stacks

**Native Method Stacks**는 자바 네이티브 인터페이스(JNI)를 통해 호출되는 네이티브 메소드의 실행 정보를 저장합니다. 각 스레드는 하나의 네이티브 메소드 스택을 가지며, 이는 JVM이 아닌 네이티브 코드를 실행할 때 사용됩니다. 이러한 스택은 JVM의 구현 방식에 따라 다를 수 있으며, 일부 JVM은 일반적인 JVM 스택과 네이티브 메소드 스택을 통합하여 사용할 수 있습니다.

#### Method Area

**Method Area**는 JVM이 시작될 때 생성되며, 모든 스레드가 공유하는 메모리 영역입니다. 이 영역은 클래스와 인터페이스의 메타데이터, 정적 변수, 상수 풀 등을 저장합니다. 클래스 로더가 클래스를 메모리에 로드할 때, 해당 클래스의 메타데이터와 정적 변수가 이 영역에 저장됩니다.

HotSpot JVM에서는 Method Area를 **Permanent Generation(PermGen)** 또는 **Metaspace**라는 이름으로 구현합니다. PermGen은 고정된 크기의 메모리 영역인 반면, Metaspace는 가비지 컬렉션을 통해 동적으로 확장 및 축소될 수 있습니다.

#### Heap

**Heap**은 JVM의 가장 큰 메모리 영역으로, 모든 스레드가 공유합니다. 이 영역은 객체 인스턴스와 배열을 저장하는 데 사용됩니다. 힙은 크게 **Young Generation**과 **Old Generation**으로 나뉩니다.

* **Young Generation**: 새롭게 생성된 객체가 할당되는 영역입니다. Young Generation은 다시 **Eden**과 두 개의 **Survivor** 영역으로 나뉩니다. 객체는 처음에 Eden에 생성되며, 일정 시간이 지나면서 Survivor 영역을 거쳐 Old Generation으로 이동합니다.
  * **Eden**: 새로 생성된 객체가 할당되는 영역입니다.
  * **Survivor**: Eden에서 살아남은 객체가 이동하는 두 개의 영역으로, 가비지 컬렉션 과정에서 사용됩니다.
* **Old Generation**: Young Generation을 거쳐 오래된 객체가 저장되는 영역입니다. 이 영역은 Full GC의 대상이 됩니다.

#### 예제 코드와 메모리 할당

다음은 예제 코드를 통해 Runtime Data Areas의 메모리 할당을 설명한 것입니다:

```java
class VariableArrange {
    static int ci = 3;
    static String cs = "Static";
    int mi = 4;
    String ms = "Member";

    void method(int pi, String ps) {
        int li = 5;
        String ls = "Local";
    }
}
```

이 예제 코드에서 변수들은 각각 다음과 같은 메모리 영역에 할당됩니다:

* `ci`와 `cs`: **Method Area**의 **Class Variable** 영역에 할당
* `mi`와 `ms`: **Heap**에 생성된 **Instance**에 할당
* `pi`와 `ps`, `li`와 `ls`: **Java Virtual Machine Stacks**의 **Local Variable** 영역에 할당

이와 같이 JVM의 Runtime Data Areas는 자바 프로그램의 실행 중에 다양한 데이터를 효율적으로 관리하고 저장하기 위해 설계된 중요한 메모리 영역입니다. 각 영역은 특정한 목적을 가지고 있으며, JVM의 안정성과 성능을 유지하는 데 중요한 역할을 합니다.
