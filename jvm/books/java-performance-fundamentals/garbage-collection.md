# Garbage Collection

## Garbage Collection (GC)

### Garbage Collection이란?

Java는 메모리 관리에서 자동화를 도입하여, 개발자가 메모리를 할당할 수 있지만 해제는 개발자의 영역이 아닙니다. 이는 Java의 중요한 특징 중 하나로, 메모리 해제는 Garbage Collector(GC)가 자동으로 수행합니다. Java의 메모리 관리 방식은 Java Virtual Machine Specification에 명시되어 있습니다. 메모리 해제는 Heap과 Method Area에 있는 객체를 메모리에서 삭제하는 것을 의미합니다. 이는 전체 메모리가 아닌 특정 객체 단위로 이루어집니다. Java에서는 변수나 객체를 new, newarray, anewarray, multianewarray와 같은 Bytecode로 변환하여 메모리를 할당받지만, 이미 할당된 객체나 변수를 해제하는 방법은 없습니다. System.gc() 나 close() 같은 함수는 명시적으로 GC를 수행하거나 해당 객체의 사용을 중지하겠다는 의사 표현일 뿐, 객체를 메모리에서 제거하는 것은 아닙니다.

### Garbage Collection 대상

Garbage Collection의 대상은 사용되지 않는 객체입니다. 사용되지 않는다는 의미는 Root Set과의 Reference 관계로 판단됩니다. Root Set은 보통 세 가지로 구분됩니다:

1. **Stack의 참조 정보**: LocalVariable Section과 Operand Stack에 객체의 Reference 정보가 있으면 이 객체는 현재 사용 중인 것으로 간주됩니다.
2. **Method Area에 로드된 Class**: 특히 Constant Pool에 있는 Reference 정보는 직접적으로 참조하지 않지만 간접적으로 링크하고 있는 것으로 간주됩니다.
3. **Native Method로 넘겨진 객체 Reference**: JNI 형식으로 현재 참조 관계가 있는 객체입니다.

이러한 Reference 정보에 의해 직간접적으로 참조되고 있다면 Reachable Object이고, 그렇지 않다면 Garbage Object로 GC의 대상이 됩니다.

### Garbage Collection의 목적

GC는 메모리 압박이 있을 때 수행됩니다. 메모리 압박이란 새로운 객체를 할당할 공간이 부족한 상황을 말합니다. 이러한 상황에서 GC는 메모리의 해제를 통해 한정된 Heap 공간을 재활용하려는 목적으로 수행됩니다. GC 후 메모리 해지는 할당한 그 자리에서 이루어지기 때문에 Garbage가 빠져나간 자리는 듬성듬성하게 됩니다. 이는 메모리의 단편화(Fragmentation)를 유발할 수 있습니다. 단편화가 발생하면 메모리의 개별 Free Space 크기보다 큰 객체를 할당할 경우 재활용이 의미가 없어집니다. 이를 방지하기 위해 GC는 Compaction과 같은 알고리즘을 사용합니다. 결국 GC란 Root Set에서 참조되지 않는 객체를 제거하여 가용한 메모리 공간을 만드는 작업입니다.

### Garbage Collection 알고리즘

#### 1. Reference Counting Algorithm

Reference Counting Algorithm은 각 객체마다 Reference Count를 관리하여 Reference Count가 0이 되면 그때그때 GC를 수행하는 방식입니다. 객체에 Reference가 추가되면 Reference Count는 증가하고, Reference가 사라지면 감소합니다. 이 방식은 간단하지만 순환 참조(Circular Reference) 문제로 인해 메모리 누수(Memory Leak)를 발생시킬 수 있습니다. 예를 들어, 두 객체가 서로를 참조하면 Reference Count가 0이 되지 않아 GC 대상이 되지 않습니다.

#### 2. Mark-and-Sweep Algorithm

Mark-and-Sweep Algorithm은 Tracing Algorithm으로도 불리며, Root Set에서 시작하여 Reference 관계를 추적합니다. Mark Phase에서는 Garbage Object와 Live Object를 구별합니다. Marking 작업은 객체의 Header에 Flag를 설정하거나 별도의 Bitmap Table을 사용하는 방식으로 이루어집니다. Sweep Phase에서는 Marking되지 않은 객체를 제거합니다. 이 알고리즘은 정확한 GC를 보장하지만, 단편화 문제를 해결하지 못합니다.

#### 3. Mark-and-Compacting Algorithm

Mark-and-Compacting Algorithm은 Mark-and-Sweep Algorithm의 단점을 극복하기 위해 고안되었습니다. 이 알고리즘은 Sweep Phase 대신 Compaction Phase를 포함합니다. Compaction 작업은 Live 객체를 연속된 메모리 공간으로 이동시켜 단편화를 방지합니다. Mark Phase에서 Marking된 정보를 바탕으로 Live 객체를 이동시키고, Garbage 객체를 제거합니다. 이 방식은 메모리 공간을 효율적으로 사용하게 하지만, 객체를 이동시키는 과정에서 추가적인 Overhead가 발생할 수 있습니다.

#### 4. Copying Algorithm

Copying Algorithm은 Fragmentation 문제를 해결하기 위해 도입된 방식입니다. Heap을 Active 영역과 Inactive 영역으로 나누어, Active 영역에만 객체를 할당합니다. Active 영역이 가득 차면 GC가 수행되어 Live 객체를 Inactive 영역으로 복사합니다. 복사된 객체는 Inactive 영역의 낮은 주소부터 연속적으로 정렬되므로 단편화 문제가 발생하지 않습니다. 하지만 이 방식은 전체 Heap의 절반만을 사용할 수 있다는 단점이 있습니다.

#### 5. Generational Algorithm

Generational Algorithm은 Heap을 여러 세대(Young Generation, Old Generation)로 나누어 관리합니다. 대부분의 객체는 생성된 지 얼마 지나지 않아 Garbage가 되는 짧은 수명을 가지고 있다는 경험적 지식에 기반합니다. Young Generation에서는 주로 짧은 수명의 객체를 관리하며, Old Generation에서는 오래된 객체를 관리합니다. Young Generation에서 Minor GC를 수행하여 Live 객체를 Survivor 영역으로 이동시키고, 일정 횟수 이상 살아남은 객체는 Old Generation으로 Promotion 됩니다.

#### 6. Train Algorithm

Train Algorithm은 Incremental Algorithm으로 불리며, Heap을 작은 Memory Block으로 나누어 GC를 수행합니다. 각 Block은 Single Block 단위로 GC가 수행되며, 이는 전체 Heap의 Suspend 시간을 줄이기 위한 것입니다. Suspend를 분산시켜 전체적인 Pause Time을 줄이는 것이 목적입니다. 각 Block은 서로 참조 관계를 가지며, Remember Set을 통해 외부 참조를 관리합니다. 이는 GC가 객체를 이동시킬 때 참조 관계를 정확히 유지하는 데 도움을 줍니다.

#### 7. Adaptive Algorithm

Adaptive Algorithm은 특정 GC 방법을 지칭하는 것이 아니라, Heap의 현재 상황을 모니터링하여 적절한 알고리즘을 선택 적용하거나 Heap Sizing을 자동화하는 일련의 방법을 의미합니다. 이는 Hotspot JVM의 Ergonomics 기능이나 IBM JVM의 Tilting 기능 등으로 구현됩니다. Adaptive Algorithm은 Application이나 사용자의 패턴에 따라 최적의 GC 방식을 선택하는 것이 중요합니다.

### Hotspot JVM의 Garbage Collection

Hotspot JVM은 Generational Collection 방식을 사용합니다. Heap을 Young Generation과 Old Generation으로 나누어 관리합니다. Young Generation은 Eden 영역과 두 개의 Survivor 영역으로 구성됩니다. Young Generation에서 Minor GC를 수행하고, 성숙된 객체는 Old Generation으로 Promotion 됩니다. Major GC는 Old Generation의 메모리가 부족할 때 수행됩니다.

#### 주요 Garbage Collector

1. **Serial Collector**: 가장 기본적인 GC 방식으로, Single CPU를 사용하여 Serial로 GC를 수행합니다. Young Generation에서는 Generational Algorithm을, Old Generation에서는 Mark-and-Compacting Algorithm을 사용합니다.
2. **Parallel Collector**: 처리량을 중시하는 GC 방식으로, Young Generation을 병렬 처리하여 처리량을 증가시킵니다. Old Generation에서는 기존의 Mark-and-Compacting Algorithm을 사용합니다.
3. **Parallel Compacting Collector**: Young Generation과 Old Generation 모두에서 병렬 처리를 적용하여 GC를 수행합니다. Old Generation에서는 Parallel Compaction Algorithm을 사용합니다.
4. **CMS (Concurrent Mark-Sweep) Collector**: 응답 시간을 개선하기 위한 Low Pause Collector로, Young Generation에서는 Parallel Copy Algorithm을, Old Generation에서는 Concurrent Mark-and-Sweep Algorithm을 사용합니다.
5. **G1 (Garbage First) Collector**: 실시간 성능을 목표로 하는 GC 방식으로, 다양한 크기의 Region을 사용하여 GC를 수행합니다. Generational 구분 없이 필요한 Region을 GC 대상으로 삼아, Suspend 시간을 최소화합니다.

### JVM 옵션

JVM 옵션은 크게 Standard Option과 Non-Standard Option으로 나뉩니다. Non-Standard Option은 JVM마다 다르며, 성능 개선을 위한 다양한 옵션을 제공합니다.

#### 주요 Heap Sizing 관련 옵션

Heap Sizing 관련 옵션은 초기 Heap 크기, 최대 Heap 크기, Young Generation의 크기, Permanent Area의 크기 등을 설정할 수 있습니다. 이는 JVM의 메모리 관리를 최적화하는 데 중요한 역할을 합니다.

* Hotspot JVM은 객체의 나이와 함께 프로모션을 위한 임계값을 설정하는 옵션을 제공함. 이 옵션은 `-XX:MaxTenuringThreshold`로 기본값은 31이다. 이 수치가 작다고 생각해 100이나 1000으로 설정해도 실제 나이의 최대값은 31이다. 그 이유는 객체 헤더의 첫 번째 워드에 기록된 나이가 6비트로 표현되기 때문이다. 이는 0부터 31까지의 값만을 기록할 수 있다.

**Promotion and Survivor Areas in Hotspot JVM**

* 그림 51에서는 마크 단계가 끝난 후 프로모션 가능한 성숙한 객체를 표시하고 있다. Eden 영역에는 LiveObjectA가 있고 Survivor 2에는 LiveObject로 B와 M이 있다. 이 중 M은 성숙한 객체이다.
* From과 To는 논리적인 명칭으로, 항상 To 영역으로 복사되고 From 영역은 보내기만 한다. Minor GC 당시 객체가 남아 있는 곳이 From이 되고 비어 있는 곳이 To가 된다. 이후 다음 Minor GC에는 From과 To가 서로 뒤바뀐다.

**Old Generation의 Garbage Collection: Mark-and-Compacting**

* Old Generation은 Young Generation과는 다른 방식으로 GC를 수행한다. Old Generation의 GC는 자주 발생하지 않지만 발생 시 Minor GC보다 더 긴 일시 정지 시간을 필요로 한다. 이는 Old Generation의 크기가 Young Generation보다 크기 때문이다.
* Old Generation은 Mark-and-Compaction 알고리즘으로 GC를 수행하며, 이는 시간이 많이 소요된다. Old Generation에서의 GC는 프로모션을 위한 메모리 공간이 부족할 때 발생한다.

**Serial Collector 옵션**

* `-XX:+UseSerialGC`: Serial Collector를 사용하고자 할 때 설정해야 하는 옵션.
* `-XX:InitialTenuringThreshold=<value>`: 객체의 초기 나이를 설정하는 옵션. 기본값은 0이며, 객체를 빨리 프로모션되게 하려면 이를 증가시킨다.
* `-XX:MaxTenuringThreshold=<value>`: 객체가 프로모션되는 나이를 지정하는 옵션. 기본값은 31이다.
* `-XX:PretenureSizeThreshold=<byte size>`: Young Generation에 생성되는 객체의 크기를 제한하는 옵션. 설정된 크기보다 큰 객체는 Tenured 영역에 바로 생성된다.
* `-XX:+PrintTenuringDistribution`: Young Generation에 할당된 객체의 나이 정보와 서바이버 영역의 적정 임계값 등을 판단할 수 있도록 정보를 제공한다.

**Incremental Collector**

* Incremental Collector는 1.3.1 버전에서 소개된 Low Pause Goal을 충족시키기 위한 최초의 Collector이다. Young Generation의 GC를 위해 Serial Collector와 동일한 Generational Algorithm을 사용하지만, Old Generation에서는 Train Algorithm을 사용한다.

**Parallel Collector**

* Parallel Collector는 여러 개의 멀티 스레드가 동시에 GC를 수행한다. Young Generation에서만 적용되며, Old Generation은 Mark-and-Compacting 방식을 유지한다.
* `-XX:+UseParallelGC`: Parallel Collector를 선택하는 옵션.
* `-XX:ParallelGCThreads=<value>`: GC를 수행할 스레드의 개수를 설정하는 옵션. 기본값은 CPU 개수와 동일하다.
* `-XX:+AlwaysTenure`: 모든 객체를 Old Generation으로 프로모션하는 옵션.

**CMS (Concurrent Mark-Sweep) Collector**

* CMS Collector는 Pause Time Goal을 가진 Collector로, Old Generation의 GC를 Concurrent하게 수행하여 일시 정지 시간을 줄인다.
* `-XX:+UseConcMarkSweepGC`: CMS Collector를 사용하기 위한 옵션.
* `-XX:+UseParNewGC`: Young Generation에서 Parallel GC를 수행하도록 설정하는 옵션.
* `-XX:+CMSParallelRemarkEnabled`: Remark Phase의 일시 정지 시간을 줄이기 위해 사용되는 옵션.

**Garbage First (G1) Collector**

* G1 Collector는 Region 단위로 Heap을 나누어 관리하며, Pause Time Goal을 가지면서도 예측 가능한 일시 정지 시간을 제공한다.
* `-XX:+UnlockExperimentalVMOptions`: G1 Collector를 사용하기 위해 실험적 옵션을 활성화하는 옵션.
* `-XX:+UseG1GC`: G1 Collector를 사용하기 위한 옵션.

**IBM JVM Garbage Collection**

* IBM JVM의 GC는 Hotspot JVM과는 다르게 구성되어 있으며, Generational Heap을 사용하고 각 단계마다 Mark, Sweep, Compaction 단계를 포함한다.
* `-Xgcpolicy:<optthruput | optavgpause | gencon | subpool>`: GC 정책을 선택하는 옵션.
* `-disableexplicitgc`: 명시적인 GC 호출을 무시하는 옵션.
* `-verbose:gc`: GC 정보를 화면에 출력하는 옵션.
* `-compactexplicitgc`: 명시적인 GC 호출 시마다 Compaction을 수행하도록 설정하는 옵션.

**Optimize for Throughput Collector**

* 이 Collector는 처리량에 초점을 맞추고 병렬로 GC를 수행하며, AF (Allocation Failure) 시 Compaction을 수행한다.
* `Parallel Mark`, `Parallel Bitwise Sweep`, `Incremental Compaction` 등의 알고리즘을 사용한다.

**Optimize for Pause Time Collector**

* Pause Time을 줄이기 위해 Concurrent하게 GC를 수행하며, `Concurrent Mark`, `Concurrent Sweep`, `Mostly Concurrent Compaction` 등의 알고리즘을 사용한다.

**Generational Concurrent Collector**

* Young Generation과 Old Generation을 구분하여 GC를 수행하며, `Scavenge`, `Global Collection` 등의 알고리즘을 사용한다.

**Subpooling Collector**

* 대규모 SMP 시스템에서 사용되며, FreeList를 크기별로 구성하여 사용한다.

이 가이드는 각 JVM의 GC 옵션과 알고리즘을 이해하고 적절하게 설정하여 애플리케이션 성능을 최적화하는 데 도움을 줄 수 있다.
