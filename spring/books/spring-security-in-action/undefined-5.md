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

### User 등록
<details markdown="1">
  <summary> USER ENTITY</summary>

```kotlin
@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val username: String,
    val password: String,

    @Enumerated(EnumType.STRING)
    val algorithm: EncryptAlgorithm,

    @OneToMany
    val authorities: List<Authority>
) {

    enum class EncryptAlgorithm {
        BCRYPT, SCRYPT
    }
}

interface UserRepository: JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}
```
</details>
<details markdown="1">
  <summary> AUTHORITY ENTITY</summary>

```kotlin
@Entity
class Authority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val name: String,
    @ManyToOne
    @JoinColumn(name = "user")
    val user: User
) {

}

interface AuthorityRepository: JpaRepository<Authority, Long> {
}
```
</details>

## 인증 논리 구현

<details markdown="1">
  <summary> UserDetails</summary>

```kotlin
class CustomUserDetails(
    private val user: User
): UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> = user.authorities
        .map { authority -> GrantedAuthority { authority.name } }
        .toList()

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
```
</details>

<details markdown="1">
  <summary> UserDetailsService </summary>

```kotlin
@Service
class CustomUserDetailService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails = userRepository.findByUsername(username)
        ?.let { CustomUserDetails(it) }
        ?: throw UsernameNotFoundException("User not found")
}
```
</details>
