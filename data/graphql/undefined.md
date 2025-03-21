# 기본 기능

### 1. 쿼리(Query): 데이터 읽기의 핵심

**쿼리**는 GraphQL에서 데이터를 요청하는 기본 방법입니다. 클라이언트가 서버에 필요한 데이터를 정확히 지정하면, 서버는 그에 맞춰 데이터를 반환합니다. REST API의 GET 요청과 비슷하지만, 쿼리는 클라이언트가 원하는 데이터만 선택적으로 가져올 수 있어 더 유연합니다.

#### 1.1. 쿼리의 기본 구조

GraphQL 쿼리는 아래와 같은 구조로 작성됩니다:

```graphql
query {
  field {
    subfield
  }
}
```

* `query`: 쿼리 작업임을 나타내는 키워드로, 생략 가능합니다.
* `field`: 서버 스키마에 정의된 최상위 필드를 호출합니다.
* `subfield`: 요청한 필드의 하위 데이터를 선택합니다.

예를 들어, 모든 사용자의 이름과 이메일을 가져오는 쿼리는 다음과 같습니다:

```graphql
{
  users {
    name
    email
  }
}
```

이 쿼리는 서버에서 `users` 필드를 호출하고, 각 사용자의 `name`과 `email`만 반환합니다.

#### 1.2. 인자(Arguments)를 활용한 정밀 요청

특정 조건에 맞는 데이터를 요청하려면 **인자**를 사용합니다. 인자는 필드에 추가되어 서버가 데이터를 필터링하거나 조정할 수 있게 합니다.

예시로, ID가 1인 사용자의 정보만 가져오는 쿼리는 다음과 같습니다:

```graphql
{
  user(id: 1) {
    name
    email
  }
}
```

여기서 `id: 1`은 `user` 필드에 전달되는 인자이며, 서버는 이 조건에 맞는 데이터만 반환합니다.

#### 1.3. 별칭(Aliases)과 프래그먼트(Fragments)

같은 필드를 다른 조건으로 여러 번 호출하려면 **별칭**을 사용합니다. 예를 들어, 두 명의 사용자를 동시에 요청하는 쿼리는 다음과 같습니다:

```graphql
{
  user1: user(id: 1) {
    name
  }
  user2: user(id: 2) {
    name
  }
}
```

또한, 반복되는 필드 선택을 줄이기 위해 **프래그먼트**를 활용할 수 있습니다:

```graphql
fragment userFields on User {
  name
  email
}

{
  user(id: 1) {
    ...userFields
  }
}
```

프래그먼트는 재사용 가능한 필드 집합으로, 쿼리를 간결하게 만듭니다.

#### 1.4. 쿼리의 주요 장점

* **오버페칭 방지**: 필요한 데이터만 요청해 불필요한 전송을 줄입니다.
* **중첩 데이터 요청**: 관련 데이터를 한 번에 가져와 네트워크 부하를 줄입니다.
* **타입 안전성**: 스키마 기반으로 작동하므로 잘못된 요청을 사전에 방지합니다.

***

### 2. 뮤테이션(Mutation): 데이터 변경의 핵심

**뮤테이션**은 데이터를 생성, 수정, 삭제하는 작업을 처리합니다. REST API의 POST, PUT, DELETE 요청과 비슷하지만, 더 명확하고 구조화된 방식으로 데이터 변경을 수행합니다.

#### 2.1. 뮤테이션의 기본 구조

뮤테이션은 다음과 같은 형식으로 작성됩니다:

```graphql
mutation {
  mutationField(input: { ... }) {
    returnedField
  }
}
```

* `mutation`: 뮤테이션 작업임을 나타냅니다.
* `mutationField`: 스키마에 정의된 뮤테이션 필드를 호출합니다.
* `input`: 변경에 필요한 데이터를 전달합니다.
* `returnedField`: 작업 후 반환할 데이터를 지정합니다.

새로운 사용자를 생성하는 뮤테이션 예제는 다음과 같습니다:

```graphql
mutation {
  createUser(input: { name: "홍길동", email: "hong@example.com" }) {
    id
    name
  }
}
```

이 뮤테이션은 `createUser`를 호출해 사용자를 추가하고, 생성된 사용자의 `id`와 `name`을 반환합니다.

#### 2.2. 다중 뮤테이션 실행

한 번의 요청으로 여러 뮤테이션을 처리할 수도 있습니다. 예를 들어, 사용자를 생성한 후 즉시 이름을 수정하는 경우:

```graphql
mutation {
  createUser(input: { name: "김영희", email: "kim@example.com" }) {
    id
  }
  updateUser(id: 1, input: { name: "김영희2" }) {
    name
  }
}
```

#### 2.3. 뮤테이션의 특징

* **명확성**: 데이터 변경 작업이 명시적으로 드러납니다.
* **구조화된 입력**: 복잡한 데이터를 입력 객체로 전달할 수 있습니다.
* **에러 처리**: 부분 성공 시에도 결과와 오류를 함께 반환합니다.

***

### 3. 구독(Subscription)

**구독**은 실시간 데이터 업데이트를 가능하게 합니다. 클라이언트는 서버의 이벤트를 구독해 데이터 변경 시 즉시 알림을 받을 수 있으며, 주로 웹소켓을 통해 구현됩니다.

#### 3.1. 구독의 기본 구조

구독은 아래와 같은 구조를 가집니다:

```graphql
subscription {
  subscriptionField {
    returnedField
  }
}
```

* `subscription`: 구독 작업임을 나타냅니다.
* `subscriptionField`: 구독할 이벤트를 지정합니다.
* `returnedField`: 이벤트 발생 시 반환할 데이터를 정의합니다.

새 메시지 도착을 구독하는 예제는 다음과 같습니다:

```graphql
subscription {
  newMessage {
    id
    content
    sender
  }
}
```

이 구독은 `newMessage` 이벤트가 발생할 때마다 메시지 정보를 클라이언트에 전달합니다.

#### 3.2. 구독의 작동 방식

구독은 HTTP 대신 지속적인 연결(예: 웹소켓)을 사용합니다. 클라이언트가 구독을 요청하면 서버는 이벤트를 감시하고, 이벤트 발생 시 데이터를 푸시합니다. 채팅, 알림, 실시간 업데이트 등에 유용합니다.

#### 3.3. 구독의 장점

* **실시간성**: 데이터 변경을 즉시 반영합니다.
* **이벤트 기반**: 특정 이벤트에 반응하도록 설정 가능합니다.
* **연결 유지**: 클라이언트와 서버 간 지속적인 통신을 지원합니다.
