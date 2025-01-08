# Destructuring Declarations

Kotlin에서 데이터 클래스(data class)는 불변의 데이터 구조를 정의하는 데 유용하며, `toString()`, `hashCode()`, `equals()`와 같은 유용한 메서드를 기본적으로 제공합니다. 하지만 리플렉션을 사용하지 않고 데이터 클래스의 모든 필드를 반복해야 할 때는 다른 접근 방식이 필요할 수 있습니다.

### 2. 구조 분해 선언(Destructuring Declarations) 사용하기

많은 사람들이 데이터 클래스의 `componentN()` 함수를 사용하여 리플렉션 없이 클래스의 모든 속성을 반복할 수 있다고 오해합니다. 하지만 이 함수들은 속성 구조 분해를 위해서만 생성됩니다.

구조 분해 선언을 사용하면 객체의 속성을 추출하여 변수에 할당할 수 있습니다. 이는 진정한 의미의 반복은 아니지만, 데이터 클래스의 모든 필드를 추출하는 데 사용할 수 있습니다.

먼저, `name`과 `age`라는 두 개의 속성을 가진 `Person` 데이터 클래스를 살펴보겠습니다.

```kotlin
data class Person(val name: String, val age: Int)

@Test
fun `iterate fields using destructuring declaration`() {
    val person = Person("Robert", 28)
    val (name, age) = person
        
    assertEquals("Robert", name)
    assertEquals(28, age)
}
```

위 코드에서 `Person` 객체를 구조 분해하여 필드를 직접 추출하고 있습니다. 이 접근 방식은 구조 분해 선언 시 데이터 클래스가 가지고 있는 필드 수를 알고 있어야 하므로, 필드 수가 많은 데이터 클래스에는 적합하지 않을 수 있습니다.

구조 분해 선언은 컴파일 타임에 타입을 확인하므로, 타입 불일치로 인한 런타임 오류 위험을 줄여줍니다. 또한, 코드의 가독성을 높여 데이터 클래스의 속성에 접근하는 명확한 문법을 제공합니다. 그러나 이 방법은 동적인 반복에는 한계가 있으며, 많은 속성을 가진 데이터 클래스에서는 비효율적일 수 있습니다.

### 3. KClassUnpacker 애노테이션 프로세서 사용하기

데이터 클래스의 속성을 반복하는 과정을 단순화하기 위해, 애노테이션 프로세싱 빌드 플러그인을 프로젝트에 통합할 수 있습니다. `KClassUnpacker` 플러그인은 이 과정을 용이하게 해주지만, 설정이 복잡할 수 있으며 프로젝트의 `pom.xml` 파일에 특정 구성이 필요합니다.

#### 3.1. 빌드 플러그인 설정

먼저, Kotlin 애노테이션 프로세서이므로 `kotlin-maven-plugin`의 `kapt` 목표를 실행하도록 추가해야 합니다.

```xml
<execution>
    <id>kapt</id>
    <goals>
        <goal>kapt</goal>
    </goals>
    <configuration>
        <sourceDirs>
            <sourceDir>src/main/kotlin</sourceDir>
            <sourceDir>src/main/java</sourceDir>
        </sourceDirs>
        <annotationProcessorPaths>
            <annotationProcessorPath>
                <groupId>com.github.LunarWatcher</groupId>
                <artifactId>KClassUnpacker</artifactId>
                <version>v1.0.2</version>
            </annotationProcessorPath>
        </annotationProcessorPaths>
    </configuration>
</execution>
```

#### 3.2. 리포지토리 구성

다음으로, `KClassUnpacker`가 JitPack Maven 리포지토리에 호스팅되어 있으므로 프로젝트가 JitPack을 읽을 수 있도록 설정해야 합니다.

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

이 설정은 프로젝트가 `mavenCentral()`을 기본 소스로 사용하고, Maven Central에 없는 종속성은 JitPack에서 로드할 수 있도록 합니다.

#### 3.3. KClassUnpacker 종속성 추가

마지막으로, 애노테이션 프로세서로서의 기능을 활성화하기 위해 `KClassUnpacker` 종속성을 추가해야 합니다.

```xml
<dependency>
    <groupId>com.github.LunarWatcher</groupId>
    <artifactId>KClassUnpacker</artifactId>
    <version>v1.0.2</version>
</dependency>
```

이 섹션에서는 GitHub의 `KClassUnpacker` 라이브러리(버전 1.0.2)를 소개합니다. 이 설정은 컴파일 시점에 컴파일러가 해당 기능에 접근할 수 있도록 하며, 런타임 종속성에서는 제외됩니다.

### 4. KClassUnpacker 사용법

이제 `KClassUnpacker`를 설정했으므로, 리플렉션 없이 데이터 클래스의 모든 필드를 반복할 수 있습니다. 대상 데이터 클래스에 `@AutoUnpack` 애노테이션을 추가해야 합니다.

```kotlin
@AutoUnpack
data class Person(val name: String, val age: Int)
```

이제 `Person` 클래스의 필드를 반복하려면 다음과 같이 사용할 수 있습니다.

```kotlin
fun getFields(person: Person): List<String> {
    val list = mutableListOf<String>()
    val cls = person
    for (field in cls) {
        list.add(field.toString())
    }

    return list
}

@Test
fun `iterate fields using KClassUnpacker plugin`() {
    val person = Person("Robert", 28)
    val list = getFields(person)

    assertEquals("Robert", list[0])
    assertEquals("28", list[1])
}
```

위 예제는 `@AutoUnpack` 애노테이션을 통해 `Person` 클래스의 필드를 반복할 수 있게 해줍니다. 테스트를 통해 `getFields` 메서드가 올바르게 작동하는지 확인할 수 있습니다.

#### 4.1. 장단점

이 접근 방식에는 몇 가지 장점과 단점이 있습니다.

**장점:**

* **컴파일 타임 체크 및 코드 생성:** 애노테이션 프로세싱을 통해 코드의 정확성을 높이고 런타임 오류 가능성을 줄일 수 있습니다.
* **리플렉션 회피:** 리플렉션을 사용하지 않기 때문에 런타임 오버헤드를 줄이고 성능을 향상시킬 수 있습니다.
* **자동 코드 생성:** 플러그인이 언팩킹 코드를 자동으로 생성해주어 수작업으로 작성해야 하는 보일러플레이트 코드를 줄여줍니다.

**단점:**

* **복잡한 설정:** 애노테이션 프로세서를 구성하고 빌드 프로세스에 통합하는 것이 복잡할 수 있으며, 개발자에게는 학습 곡선이 있을 수 있습니다.
* **외부 의존성:** 외부 애노테이션 프로세서에 의존하게 되어 추가적인 종속성과 호환성 문제가 발생할 수 있습니다.
* **디버깅 어려움:** 생성된 코드를 디버깅하는 것이 어려울 수 있으며, 생성된 코드가 원본 소스 코드와 어떻게 대응되는지 명확하지 않을 수 있습니다.

