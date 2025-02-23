# introduce

GraphQL은 기존 REST API가 가진 한계를 극복하기 위해 탄생했습니다.&#x20;

REST API는 한 번의 요청에 너무 많은 데이터를 보내거나 필요한 데이터가 누락되는 문제(오버페칭, 언더페칭)를 발생시키곤 했습니다. 이를 해결하여 클라이언트는 필요한 데이터만 선별하여 요청하고, 서버는 효율적인 데이터 전송을 할 수 있게 되었습니다.

### **ddi**

GraphQL은 단일 엔드포인트에서 클라이언트가 요청한 데이터만 정확하게 받아올 수 있는 쿼리 언어이자 런타임입니다.&#x20;

스키마 기반의 설계를 통해 API의 데이터 구조와 타입을 명확하게 정의하고, 이를 바탕으로 클라이언트와 서버 간의 계약을 체계적으로 관리합니다.&#x20;

또한 데이터 조회(쿼리)뿐 아니라 데이터 생성/수정(뮤테이션)과 실시간 데이터 업데이트(서브스크립션)까지 모두 하나의 통합된 방식으로 처리할 수 있어 API의 확장성과 유지보수성이 크게 향상됩니다.

### **동작구조**

GraphQL의 동작 방식은 크게 다음과 같은 구성요소로 이루어집니다.

* **스키마(Schema):**\
  API가 제공할 데이터의 타입, 관계, 그리고 필드를 정의합니다. 이를 통해 클라이언트는 어떤 데이터를 어떻게 요청할 수 있는지 명확하게 알 수 있습니다.
* **쿼리(Query):**\
  클라이언트가 서버에 요청할 데이터를 명시하는 언어입니다. 필요한 필드만 선택적으로 요청하여, 불필요한 데이터 전송을 줄이고 네트워크 효율을 높입니다.
* **리졸버(Resolver):**\
  스키마의 각 필드에 대응하여 실제 데이터를 가져오는 함수입니다. 클라이언트의 요청이 들어오면, 해당 리졸버가 호출되어 데이터베이스나 다른 API로부터 데이터를 반환합니다.

추가로, 데이터 변경이 필요한 경우에는 **뮤테이션(Mutation)**, 실시간 데이터 처리를 위해서는 **서브스크립션(Subscription)** 기능이 함께 사용됩니다.

**사용 예시 (코틀린 샘플 코드 + 도커)**\
아래는 Spring Boot와 Spring for GraphQL을 사용하여 코틀린으로 작성한 GraphQL 서버의 예시입니다. 이 예제에서는 단순히 `hello` 쿼리를 통해 "안녕하세요, GraphQL in Kotlin!" 메시지를 반환하도록 구성되어 있습니다.

▣ **1. Gradle 빌드 설정 (build.gradle.kts)**

```kotlin
kotlin복사plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

▣ **2. GraphQL 스키마 정의 (src/main/resources/graphql/schema.graphqls)**

```graphql
graphql복사type Query {
    hello: String!
}
```

▣ **3. 코틀린 메인 애플리케이션 (src/main/kotlin/com/example/demo/DemoApplication.kt)**

```kotlin
kotlin복사package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@Controller
class HelloController {
    @QueryMapping
    fun hello(): String {
        return "안녕하세요, GraphQL in Kotlin!"
    }
}
```

▣ **4. 도커파일 (Dockerfile)**\
아래 Dockerfile은 빌드된 JAR 파일을 컨테이너에 복사하고, OpenJDK 11 이미지를 기반으로 애플리케이션을 실행합니다.

```dockerfile
dockerfile복사# OpenJDK 11 JRE slim 이미지 사용
FROM openjdk:11-jre-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너로 복사 (JAR 파일명은 빌드 결과에 따라 변경)
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
```

▣ **5. 실행 및 테스트 단계**

1.  **프로젝트 빌드 및 JAR 파일 생성**\
    터미널에서 아래 명령어를 실행하여 JAR 파일을 생성합니다.

    ```bash
    bash복사./gradlew bootJar
    ```
2.  **도커 이미지 빌드**\
    생성된 JAR 파일이 위치한 프로젝트 루트에서 도커 이미지를 빌드합니다.

    ```bash
    bash복사docker build -t demo-graphql-kotlin .
    ```
3.  **도커 컨테이너 실행**\
    빌드된 이미지를 기반으로 컨테이너를 실행합니다.

    ```bash
    bash복사docker run -p 8080:8080 demo-graphql-kotlin
    ```
4.  **GraphQL 쿼리 테스트**\
    브라우저나 GraphQL Playground, Apollo Studio에서 `http://localhost:8080/graphql` 엔드포인트에 접속한 후, 아래와 같은 쿼리를 실행합니다.

    ```graphql
    graphql복사query {
        hello
    }
    ```

    실행 결과:

    ```json
    json복사{
      "data": {
        "hello": "안녕하세요, GraphQL in Kotlin!"
      }
    }
    ```

**설치법**\
GraphQL 서버를 구축하기 위해 필요한 환경 구성 및 설치 과정을 정리하면 다음과 같습니다.

* **개발 환경 준비:**
  * JDK 11 이상과 Kotlin, Spring Boot 개발 환경을 설치합니다.
  * Gradle이나 Maven으로 프로젝트를 초기화하고, Spring Boot Starter GraphQL 등 필요한 의존성을 추가합니다.
* **스키마 파일 작성:**
  * `src/main/resources/graphql` 폴더에 `schema.graphqls` 파일을 생성하고, GraphQL 스키마를 정의합니다.
* **리졸버 구현:**
  * 코틀린으로 작성한 컨트롤러 클래스에 `@QueryMapping` 등의 어노테이션을 사용하여 쿼리 요청에 대한 리졸버 메서드를 구현합니다.
* **애플리케이션 빌드:**
  * Gradle을 통해 프로젝트를 빌드하여 실행 가능한 JAR 파일을 생성합니다.
* **도커 컨테이너 배포:**
  * 도커파일을 작성하여 JAR 파일을 포함한 이미지를 빌드하고, 도커 명령어로 컨테이너를 실행합니다.
  * 포트 매핑 및 환경 변수 설정을 통해 운영 환경에 맞게 배포합니다.
