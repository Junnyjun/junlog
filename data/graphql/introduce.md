# introduce

### 1. GraphQL 소개

GraphQL은 페이스북에서 개발되어 2015년에 오픈 소스로 공개된 API 쿼리 언어입니다. REST API에서는 클라이언트가 여러 엔드포인트를 호출해야 하거나, 필요 이상으로 많은 데이터를 받아오는 오버페칭(over-fetching) 문제가 발생할 수 있습니다. GraphQL은 이러한 문제를 해결하며, 클라이언트가 필요한 데이터만 정확히 요청할 수 있게 해줍니다.

#### GraphQL의 주요 특징

GraphQL의 강점은 다음과 같은 핵심적인 특징에서 비롯됩니다:

* **단일 엔드포인트**: REST API는 자원별로 엔드포인트를 만들어야 하지만, GraphQL은 `/graphql` 같은 단일 엔드포인트를 통해 모든 요청을 처리합니다. 이는 클라이언트가 여러 엔드포인트를 관리할 필요가 없어 API 관리가 간편해집니다.
* **강력한 타입 시스템**: GraphQL은 스키마를 통해 데이터의 구조를 명확히 정의합니다. 이 스키마는 쿼리(Query), 뮤테이션(Mutation), 구독(Subscription) 등의 타입을 포함하며, 클라이언트와 서버 간의 소통이 더 안전하고 예측 가능해집니다.
* **자체 문서화**: GraphQL 스키마는 API의 문서 역할을 하므로, 별도의 문서 작업 없이도 API의 기능을 쉽게 이해할 수 있습니다. 이는 개발 팀 간의 협업을 원활하게 하고, API 변경 사항을 즉시 반영할 수 있게 해줍니다.
* **클라이언트 주도 개발**: GraphQL은 클라이언트가 필요한 데이터를 정확히 요청할 수 있게 해줍니다. 즉, 오버페칭이나 언더페칭(under-fetching) 문제를 피할 수 있습니다.

이러한 특징 덕분에 GraphQL은 모바일 앱, 웹 프론트엔드, 마이크로서비스 아키텍처 등 다양한 환경에서 점점 더 많이 사용되고 있습니다.

***

### 2. GraphQL 작동 원리

GraphQL이 어떻게 동작하는지 이해하는 것은 GraphQL을 효과적으로 사용하는 데 필수적입니다. GraphQL은 클라이언트와 서버 간의 협업을 효율적으로 만드는 몇 가지 핵심 단계를 거칩니다.

#### 2.1. 스키마 정의

GraphQL의 모든 것은 **스키마**에서 시작됩니다. 스키마는 API에서 사용 가능한 데이터의 구조를 정의하는 역할을 합니다. 스키마는 다음과 같은 요소로 구성됩니다:

* **타입(Type)**: 데이터의 종류를 정의합니다. 예를 들어, `User` 타입은 사용자의 이름, 이메일 등의 필드를 가질 수 있습니다.
* **쿼리(Query)**: 클라이언트가 서버로부터 데이터를 요청하는 방법을 정의합니다. 예를 들어, `users` 쿼리는 모든 사용자의 목록을 가져오는 역할을 합니다.
* **뮤테이션(Mutation)**: 데이터를 생성, 수정, 삭제하는 방법을 정의합니다. 예를 들어, `createUser` 뮤테이션은 새로운 사용자를 생성합니다.
* **구독(Subscription)**: 실시간 데이터를 클라이언트에게 푸시하는 방법을 정의합니다. 예를 들어, 채팅 애플리케이션에서 새 메시지가 도착할 때 클라이언트에게 알림을 보낼 수 있습니다.

스키마는 GraphQL SDL(Schema Definition Language)이라는 언어로 작성되며, 이를 통해 클라이언트와 서버가 서로 어떤 데이터를 주고받을 수 있는지 명확히 알 수 있습니다.

#### 2.2. 쿼리 요청

클라이언트는 자신이 원하는 데이터를 구체적으로 요청하는 **쿼리**를 작성해 서버로 보냅니다. 쿼리는 GraphQL의 핵심 기능 중 하나로, 클라이언트가 필요한 데이터의 구조를 정확히 지정할 수 있게 해줍니다. 예를 들어, 다음과 같은 쿼리를 생각해 볼 수 있습니다:

```graphql
{
  user(id: 1) {
    name
    email
  }
}
```

이 쿼리는 "ID가 1인 사용자의 이름과 이메일만 가져와"라는 뜻입니다. REST API에서는 사용자의 모든 정보를 가져오는 엔드포인트를 호출한 뒤 필요한 필드만 골라내야 했지만, GraphQL에서는 서버가 클라이언트의 요청에 맞춰 정확한 데이터를 반환합니다.

#### 2.3. 리졸버 함수 실행

서버는 클라이언트의 쿼리를 받으면, 이를 분석해 해당 데이터를 가져오기 위해 **리졸버(resolver)** 함수를 실행합니다. 리졸버는 스키마에 정의된 각 필드에 대해 데이터를 가져오는 로직을 담당합니다. 예를 들어, `user` 쿼리의 리졸버는 데이터베이스에서 해당 ID의 사용자 정보를 조회하고, `name`과 `email` 필드를 추출해 반환합니다.

#### 2.4. 응답 반환

마지막으로, 서버는 클라이언트가 요청한 구조에 맞춰 데이터를 반환합니다. 응답은 JSON 형식으로 제공되며, 쿼리에서 요청한 필드만 포함됩니다. 예를 들어, 위의 쿼리에 대한 응답은 다음과 같을 수 있습니다:

```json
{
  "data": {
    "user": {
      "name": "John Doe",
      "email": "john@example.com"
    }
  }
}
```

이처럼 GraphQL은 클라이언트가 필요로 하는 데이터만 정확히 반환하므로, 네트워크 대역폭을 절약하고 클라이언트의 데이터 처리 로직을 간소화할 수 있습니다.

***

### 3. 코틀린을 사용한 GraphQL 샘플 설치법

이제 이론에서 벗어나, 실제로 코틀린과 Spring Boot를 활용해 간단한 GraphQL 서버를 만들어보겠습니다. 코틀린은 간결하고 안전한 문법을 제공하는 JVM 기반 언어로, GraphQL 서버 개발에 매우 적합합니다. 아래는 단계별 설치법입니다.

#### 3.1. 사전 준비

GraphQL 서버를 구축하기 전에 몇 가지 도구가 필요합니다:

* **JDK 11 이상**: Java Development Kit이 설치되어 있어야 합니다.
* **IDE**: IntelliJ IDEA를 추천하지만, Eclipse나 VS Code 등 다른 IDE를 사용해도 무방합니다.
* **빌드 도구**: Gradle이나 Maven 중 하나를 설치합니다. 이 예제에서는 Gradle을 사용하겠습니다.

#### 3.2. 프로젝트 생성

#### 3.3. GraphQL 의존성 추가

`build.gradle.kts` 파일에 GraphQL 관련 의존성을 추가합니다.

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.graphql-java:graphql-spring-boot-starter:5.0.2")
    implementation("com.graphql-java:graphql-java-tools:5.2.4")
}
```

의존성을 추가한 후, Gradle을 통해 프로젝트를 리프레시하여 라이브러리를 다운로드합니다.

#### 3.4. 스키마 정의

`src/main/resources` 폴더에 `schema.graphqls` 파일을 생성하고, 다음 내용을 작성합니다:

```graphql
type Query {
    hello: String
}
```

이 스키마는 "hello"라는 쿼리를 요청하면 문자열을 반환한다는 것을 의미합니다.

#### 3.5. 리졸버 구현

`src/main/kotlin/com/example/graphql_demo` 폴더에 `QueryResolver.kt` 파일을 생성하고, 다음 코드를 작성합니다:

```kotlin
package com.example.graphql_demo

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
class QueryResolver : GraphQLQueryResolver {
    fun hello(): String {
        return "Hello, GraphQL!"
    }
}
```

이 코드는 `hello` 쿼리에 대응하는 리졸버를 정의합니다.

#### 3.6. 애플리케이션 실행

`src/main/kotlin/com/example/graphql_demo/Application.kt` 파일을 열고, 서버를 실행합니다. IDE에서 "Run" 버튼을 클릭하거나, 터미널에서 `./gradlew bootRun` 명령어를 실행하세요.

#### 3.7. 쿼리 테스트

브라우저에서 `http://localhost:8080/graphiql`에 접속해 다음 쿼리를 테스트합니다:

```graphql
{
    hello
}
```

응답은 다음과 같습니다:

```json
{
    "data": {
        "hello": "Hello, GraphQL!"
    }
}
```
