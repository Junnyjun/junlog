# 작고 안전한 웹 어플리케이션

어플리케이션의 제품 정보와 사용자는 데이터베이스에 저장되고, 각 사용자의 암호는 `BCrypt`를 사용하여 암호화된다.
이 작은 프로젝트의 구현은 다음과 같다

1. 데이터베이스
2. 사용자 관리
3. 인증 논리 구현
4. 주 페이지
5. 실행 및 테스트

## 사용자 관리

사용자는 데이터베이스에 저장되며, 사용자의 암호는 `BCrypt`를 사용하여 암호화된다.

### PasswordEncoder 등록

```kotlin
@Configuration
class UserManagementConfig {
    @Bean
    fun bCryptPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun sCryptPasswordEncoder(): PasswordEncoder = SCryptPasswordEncoder()

}
```