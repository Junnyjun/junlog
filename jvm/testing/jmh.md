# JMH

JMH(Java Microbenchmark Harness)는 Java 기반 애플리케이션의 **마이크로 벤치마크**를 작성하기 위한 프레임워크입니다. 단순한 성능 테스트 코드로 인해 발생할 수 있는 **JVM 최적화**(JIT, Dead Code Elimination 등) 문제를 방지하고, 정확하고 신뢰할 수 있는 결과를 제공합니다.

JMH는 **JVM 워밍업**, **스레딩 옵션**, **성능 측정 반복** 등 다양한 기능을 제공하여 벤치마킹 과정에서 발생할 수 있는 흔한 실수를 예방합니다.

***

### **특징**

* **JIT 최적화 방지**: JVM의 핫스팟 컴파일러가 벤치마크 결과를 왜곡하지 않도록 관리.
* **멀티스레드 환경 지원**: 동시성 테스트를 쉽게 설정 가능.
* **정확한 성능 측정**: Warm-up, Iteration, Fork 설정을 통한 안정적인 결과 제공.
* **사용 편리성**: 어노테이션 기반 설정으로 쉽게 작성 가능.

***

### **설정 방법**

**Maven 프로젝트에 JMH 추가**

```xml
<dependencies>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.38</version> <!-- 최신 버전 확인 필요 -->
    </dependency>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.38</version>
    </dependency>
</dependencies>
```

**Gradle 프로젝트에 JMH 추가**

```groovy
plugins {
    id 'java'
    id 'me.champeau.jmh' version '0.7.1' // JMH 플러그인 사용
}

dependencies {
    implementation 'org.openjdk.jmh:jmh-core:1.38'
    annotationProcessor 'org.openjdk.jmh:jmh-generator-annprocess:1.38'
}
```

***

### **JMH 사용법**

```java
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput) // 1초당 실행 횟수 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 결과를 밀리초 단위로 출력
@State(Scope.Thread) // 상태를 각 스레드에서 독립적으로 유지
public class MyBenchmark {

    private int[] numbers;

    @Setup(Level.Iteration) // 각 반복 전 초기화
    public void setUp() {
        numbers = new int[1_000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i;
        }
    }

    @Benchmark
    public int sumArray() {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }

    @Benchmark
    public int sumArrayUsingStreams() {
        return java.util.Arrays.stream(numbers).sum();
    }
}
```

1. `mvn clean install` 또는 `./gradlew jmhJar`로 빌드.
2.  실행:

    ```bash
    java -jar target/benchmarks.jar
    ```

    또는

    ```bash
    java -jar build/libs/benchmarks.jar
    ```

***

#### **주요 어노테이션 및 설정**

* **@Benchmark**: 벤치마크 테스트 메서드 표시.
* **@BenchmarkMode**: 벤치마크 측정 모드 설정 (예: Throughput, AverageTime, SampleTime, AllModes 등).
* **@OutputTimeUnit**: 결과의 시간 단위 설정.
* **@State**: 벤치마크 실행 상태를 나타내는 객체 스코프 설정 (Thread, Group, Singleton).
* **@Setup / @TearDown**: 벤치마크 준비 및 정리 작업 지정.
* **@Param**: 파라미터를 설정하여 다양한 입력값 테스트 가능.

***

**6. JMH 실행 시 주요 설정**

*   **Fork**: JVM 프로세스를 몇 번 재시작하여 벤치마크 실행할지 결정.

    ```java
    @Fork(2) // 두 번의 프로세스를 실행
    ```
*   **Warmup**: JVM 워밍업을 몇 번 반복할지 설정.

    ```java
    @Warmup(iterations = 5) // 5회 워밍업
    ```
*   **Measurement**: 실제 벤치마크 반복 횟수.

    ```java
    @Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
    ```
