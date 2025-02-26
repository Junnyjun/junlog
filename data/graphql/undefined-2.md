# 유지보수

GraphQL API가 시간이 지나면서 어떻게 변경되고, 기존 클라이언트가 계속 작동하도록 관리하는 과정입니다. 새로운 기능을 추가하거나 기존 필드를 제거할 때, 기존 쿼리가 깨지지 않도록 신중히 관리해야 합니다. 이는 API의 장기적인 유지보수성과 확장성을 보장하는 데 필수적입니다.

### 브레이킹 체인지와 논브레이킹 체인지

스키마 변경은 크게 두 가지로 나눌 수 있습니다: 브레이킹 체인지와 논브레이킹 체인지.

* **브레이킹 체인지**: 기존 쿼리가 유효하지 않게 되는 변경입니다. \
  필드 제거, 필드 타입 변경, 필수 필드 추가 등이 있습니다. 이는 기존 클라이언트가 더 이상 작동하지 않을 수 있어 주의가 필요합니다.
* **논브레이킹 체인지**: 기존 쿼리에 영향을 주지 않는 변경입니다. \
  새로운 필드 추가, 필드 nullable로 변경 등이 있습니다. 그러나 논브레이킹 체인지도 클라이언트의 예상 행동에 영향을 미칠 수 있습니다.  필드가 nullable로 변경되면 클라이언트가 null 값을 처리해야 할 수 있습니다.

### 변경 관리

스키마 진화를 효과적으로 관리하기 위한 여러 전략이 있습니다.

#### **새로운 필드 추가**

새로운 필드는 기존 쿼리에 영향을 주지 않으므로 안전합니다. \
클라이언트는 필요할 때 새 필드를 사용하기 시작할 수 있습니다.&#x20;

`User` 타입에 `bio` 필드를 추가하면 기존 쿼리는 여전히 작동합니다

```graphql
type User {
  name: String!
  email: String!
  bio: String  # 새로운 필드, nullable로 추가
}
```

#### **기존 필드 폐기**

필드를 제거하기 전에 `@deprecated` 지시어를 사용해 폐기 이유를 명시하세요.

```graphql
type User {
  name: String! @deprecated(reason: "Use fullname instead")
  fullname: String!
}
```

이를 통해 클라이언트가 새로운 필드로 전환할 시간을 주세요. \
폐기 후 제거 시기는 클라이언트의 업데이트 속도에 따라 결정되며, 보통 몇 달 정도의 유예 기간을 둡니다.

#### **필드 타입 변경**

필드 타입을 변경해야 할 경우, 새로운 필드를 추가하고 기존 필드를 폐기하세요. \
`email` 필드를 `emailAddress`로 변경하려면

```graphql
type User {
  email: String! @deprecated(reason: "Use emailAddress instead")
  emailAddress: String!
}
```

이는 기존 클라이언트가 점진적으로 새로운 필드로 전환할 수 있도록 돕습니다.

#### **인자 변경**

필드의 인자를 변경할 경우, 새로운 인자를 추가하고 기존 인자를 폐기하세요. \
`posts(limit: Int)`를 `posts(first: Int, after: String)`로 변경하려면

```graphql
type Query {
  posts(limit: Int): [Post!]! @deprecated(reason: "Use postsConnection for pagination")
  postsConnection(first: Int, after: String): PostConnection!
}
```

### 데프리케이션과 클라이언트 통신

`@deprecated` 지시어는 필드, 인자, 열거형 값을 폐기할 때 사용됩니다.

```graphql
enum UserStatus {
  ACTIVE
  INACTIVE
  SUSPENDED @deprecated(reason: "Users are now either active or inactive")
}
```

폐기된 요소는 여전히 유효하지만, 클라이언트에게 더 이상 사용하지 말라고 경고합니다.

클라이언트에게 변경 사항을 효과적으로 알리기 위해 문서 업데이트와 알림 시스템을 활용하세요. \
API 변경 로그를 제공하거나, 클라이언트에게 이메일 알림을 보낼 수 있습니다. 이는 클라이언트가 변경에 적응할 시간을 주고, API의 신뢰성을 높입니다.
