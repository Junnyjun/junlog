# 스키마

### 스키마 ?

GraphQL 스키마는 클라이언트가 요청할 수 있는 데이터와 그 구조를 정의하는 청사진입니다. 이는 Schema Definition Language (SDL)로 작성되며, 서버와 클라이언트 간의 계약 역할을 합니다. 예를 들어, 블로그 플랫폼에서 사용자, 게시글, 댓글의 구조를 명확히 정의할 수 있습니다.

스키마는 데이터 조회(쿼리), 수정(뮤테이션), 실시간 업데이트(구독)를 지원하며, 이를 통해 클라이언트는 필요한 데이터만 정확히 요청할 수 있습니다. 설계 과정에서 명확한 이름 규칙, nullability 처리, 관계 정의, 확장성 고려가 중요합니다. 특히, 스키마는 자체 문서화 기능을 제공하여 별도의 API 문서가 필요하지 않을 수 있습니다.

***

## 스키마 설계

스키마 설계는 GraphQL API의 성공을 좌우하는 중요한 단계입니다.&#x20;

### **타입**

타입은 스키마의 기본 단위로, 데이터 구조를 정의합니다.

**스칼라 타입**: 기본 데이터 타입으로, `String`, `Int`, `Float`, `Boolean`, `ID` 등이 있습니다.

| 스칼라 타입                                                     | 설명                   |
| ---------------------------------------------------------- | -------------------- |
| String                                                     | UTF-8 문자 시퀀스         |
| Int                                                        | 32비트 부호 있는 정수        |
| Float                                                      | 64비트 부동 소수점 숫자       |
| Boolean                                                    | 참(true) 또는 거짓(false) |
| ID                                                         | 고유 식별자, 주로 노드에 사용    |
| 커스텀 스칼라 타입(예: `Date`, `Email`)도 정의 가능하며, 이는 데이터 검증에 유용합니다. |                      |

**객체 타입**: 실제 데이터 모델을 나타냅니다.&#x20;

```graphql
type User {
  id: ID!
  name: String!
  email: String!
}
```

여기서 `!`는 해당 필드가 null이 될 수 없음을 의미하며, 선택적 필드는 생략됩니다.

**인터페이스 타입**: 여러 타입이 공통으로 가지는 필드를 정의합니다. 이는 다형성을 지원하며,&#x20;

```graphql
interface Node {
  id: ID!
}

type User implements Node {
  id: ID!
  name: String!
}
```

이는 공통 필드를 공유하는 타입 간의 유연성을 제공합니다.

**유니온 타입**: 필드가 여러 객체 타입 중 하나를 반환할 수 있도록 합니다.

```graphql
union SearchResult = User | Post
```

이는 검색 결과가 사용자 또는 게시글일 수 있음을 나타냅니다.

**열거형(Enum 타입)**: 제한된 값 집합을 정의합니다.

```graphql
enum UserStatus {
  ACTIVE
  INACTIVE
  SUSPENDED
}
```

이는 상태 값이 특정 범위 내에 있어야 함을 보장합니다.

**입력 타입(Input 타입)**: 뮤테이션에서 클라이언트 입력 데이터를 정의합니다.

```graphql
input CreateUserInput {
  name: String!
  email: String!
}
```

이는 복잡한 입력 데이터를 구조화하여 처리할 수 있게 합니다.

### **쿼리, 뮤테이션, 구독의 역할**

**쿼리(Query)**: 데이터를 조회하는 진입점입니다.

```graphql
type Query {
  users: [User!]!
  user(id: ID!): User
}
```

이는 모든 사용자 목록이나 특정 사용자를 조회할 수 있습니다. 인자를 통해 필터링이나 페이지네이션을 지원할 수 있습니다.

**뮤테이션(Mutation)**: 데이터를 생성, 수정, 삭제하는 진입점입니다.

```graphql
type Mutation {
  createUser(input: CreateUserInput!): User!
  updateUser(id: ID!, input: UpdateUserInput!): User!
  deleteUser(id: ID!): Boolean!
}
```

입력 타입을 사용해 클라이언트가 보낸 데이터를 명확히 처리하며, 작업 결과를 반환합니다.

**구독(Subscription)**: 실시간 데이터 업데이트를 제공합니다.&#x20;

```graphql
type Subscription {
  newPost: Post!
  newComment(postId: ID!): Comment!
}
```

주로 웹소켓을 통해 서버가 클라이언트에 데이터를 푸시하며, 실시간 알림 시스템에 유용합니다.

### **검토 사항**

스키마 설계 시 다음 사항을 고려하면 더 나은 API를 만들 수 있습니다.

**명확한 Naming Convention**:

* 타입은 대문자 명사(예: `User`), 필드는 소문자 명사(예: `name`), 쿼리/뮤테이션은 동사+명사(예: `createUser`)로 작성.
* 일관된 이름 규칙은 유지보수성을 높입니다.

**Nullability 처리**:

* 필수 필드는 `!`로 표시(예: `id: ID!`), 선택적 필드는 생략(예: `bio: String`).
* 이는 클라이언트가 예상치 못한 오류를 줄이는 데 도움을 줍니다.

**입력과 출력 구분**:

*   뮤테이션에서 `input` 타입을 사용해 입력 데이터를 정의.

    ```graphql
    input UpdateUserInput {
      name: String
      email: String
    }
    ```
* 출력은 객체 타입으로 처리하여 데이터 구조를 명확히 합니다.

**관계 정의**:

*   객체 간 관계를 명확히 설계. 예를 들어, 사용자와 게시글의 관계:

    ```graphql
    type User {
      posts: [Post!]!
    }
    type Post {
      author: User!
    }
    ```
* one-to-many, many-to-many 관계를 적절히 표현하세요.

**페이지네이션과 필터링**:

*   대량 데이터 처리 시 커서 기반 페이지네이션을 추천.

    ```graphql
    type Query {
      users(first: Int, after: String): UserConnection!
    }

    type UserConnection {
      edges: [UserEdge!]!
      pageInfo: PageInfo!
    }

    type UserEdge {
      node: User!
      cursor: String!
    }

    type PageInfo {
      hasNextPage: Boolean!
      endCursor: String!
    }
    ```
* 필터링 인자를 통해 데이터 검색 기능을 강화하세요.
* **확장성 고려**:
  *   새로운 필드는 nullable로 추가하고, 기존 필드는 `@deprecated`로 폐기 안내.&#x20;

      ```graphql
      type User {
        oldField: String @deprecated(reason: "Use newField")
        newField: String
      }
      ```
  * 스키마를 진화시키면서 기존 클라이언트와의 호환성을 유지하세요.

### **샘플 예제**

블로그 애플리케이션(사용자, 게시글, 댓글 포함)의 스키마를 설계해보겠습니다.

**객체 타입**:

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
  comments: [Comment!]!
}

type Post {
  id: ID!
  title: String!
  content: String!
  author: User!
  comments: [Comment!]!
}

type Comment {
  id: ID!
  text: String!
  author: User!
  post: Post!
}
```

**쿼리**:

```graphql
type Query {
  users: [User!]!
  user(id: ID!): User
  posts: [Post!]!
  post(id: ID!): Post
  comments(postId: ID!): [Comment!]!
}
```

**뮤테이션**:

```graphql
input CreateUserInput {
  name: String!
  email: String!
}

input CreatePostInput {
  title: String!
  content: String!
  authorId: ID!
}

input CreateCommentInput {
  text: String!
  authorId: ID!
  postId: ID!
}

type Mutation {
  createUser(input: CreateUserInput!): User!
  createPost(input: CreatePostInput!): Post!
  createComment(input: CreateCommentInput!): Comment!
  updatePost(id: ID!, title: String, content: String): Post!
  deletePost(id: ID!): Boolean!
}
```

**구독**:

```graphql
type Subscription {
  newPost: Post!
  newComment(postId: ID!): Comment!
}
```

#### Appendix

* **스키마 스티칭**: 여러 스키마를 통합하여 단일 API 제공. 마이크로서비스 아키텍처에서 유용.
* **지시어**: 메타데이터 추가나 동작 수정. 예: `@deprecated`.
*   **스키마 확장**: 기존 스키마에 새로운 필드 추가.

    ```graphql
    extend type Query {
      newFeature: String!
    }
    ```
* **에러 처리와 검증**: 커스텀 스칼라 타입(예: Date, Email)으로 데이터 검증.
