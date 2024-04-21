# 전역 메서드 보안

웹 어플리케이션과 웹이 아닌 어플리케이션의 권한 부여를 구성할 수 있는 방법 중 하나이다.\
전역 메서드 보안은 메서드 호출을 보호하기 위해 사용되는 스프링 시큐리티의 기능이다.

## 전역 메서드 보안 설정

전역 메서드 보안은 기존적으로 비활성화 되어 있다.\
전역 메서드 보안을 활성화 하려면 `@EnableGlobalMethodSecurity` 어노테이션을 사용하면 된다.

### 호출 권한 부여 

메서드를 호출할 수 있는지, 반환값에 접근할 수 있는지를 결정한다.\
메서드 호출 부여 방식은 AOP를 사용하여 구현되어 있다.

#### 사전 권한 부여

메서드 호출 전에 권한을 확인한다.\
특정 상황에서 메서드 호출을 허용하지 않는 규칙을 정의할 수 있다.

```kotlin
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class MethodSecurityConfig {
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun createBook() {
        // ...
    }
}

```

#### 사후 권한 부여

메서드 호출 후에 권한을 확인한다.\
메서드 호출이 완료된 후에 권한을 확인한다.

반환된 결과를 얻기 위해 메서드 호출을 필터링하는 방법을 제공한다.

```kotlin
@Configuration
@EnableGlobalMethodSecurity(postPostEnabled = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration() {
    ...
}
```