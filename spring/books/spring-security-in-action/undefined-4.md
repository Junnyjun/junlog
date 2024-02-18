# 인증 구현

인증 논리를 담당하는 것은 AuthenticationProvider이다. 이 인터페이스를 구현한 클래스를 만들어서 사용하면 된다. 인증 프로세스는 두가지의 경우로 이루어진다.

```kotlin
1. 요청한 엔티티가 인증되지 않은 경우
2. 요청한 엔티티가 인증된 경우
```

요청을 나타내는 방법을 이해하려면 Authentication 객체를 이해해야 한다. 이 객체는 요청한 엔티티의 정보를 담고 있다. 이 객체는 인증 프로세스를 통과한 후에도 사용된다.

## AuthenticationProvider

서비스에 따라 아이디&비밀번호 기반의 인증이 적합하지 않을수있다.

<img src="../../../.gitbook/assets/file.excalidraw (41).svg" alt="" class="gitbook-drawing">
