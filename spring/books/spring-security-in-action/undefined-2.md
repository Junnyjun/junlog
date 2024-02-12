# 사용자 관리

사용자 관리를 위해서는 UserDetailsService & UserDetailManager를 구현해야 한다.\
이를 통해 사용자의 정보를 가져오고, 인증을 수행할 수 있다.\
UserDetailsService는 사용자의 정보를 검색하는 역할을 한다.

UserDetailManager는 대부분의 어플리케이션에 필요한 사용자 관리 기능을 제공한다.

스프링 시큐리티에서의 사용자는 작업을 수행할수 있는 권리를 가진자를 이야기한다. 이때의 권한은 GrantedAuthority를 구현한 객체로 표현된다.

<img src="../../../.gitbook/assets/file.excalidraw (40).svg" alt="" class="gitbook-drawing">

## UserDetailsService

```kotlin
interface UserDetails : Serializable {
    fun getAuthorities(): Collection<out GrantedAuthority>
    fun getPassword(): String
    fun getUsername(): String
    fun isAccountNonExpired(): Boolean
    fun isAccountNonLocked(): Boolean
    fun isCredentialsNonExpired(): Boolean
    fun isEnabled(): Boolean
}
```

이중 getPassword()와 getUsername()는 각 사용자의 이름과 암호를 반환한다\
getAuthorities()는 사용자가 작업을 수행할수 있는 권한을 반환한다.

그중 사용자가 작업을 부여받을 수 있는 권한을 나타내는 GrantedAuthority가 존재한다.

```kotlin
interface GrantedAuthority : Serializable {
    fun getAuthority(): String
}

val read: GrantedAuthority = GrantedAuthority { "READ" }
val write: GrantedAuthority = GrantedAuthority { "WRITE" }
```

위 두 객체로 Dummy 사용자를 생성하면 다음과 같다.

```kotlin
class DummyUser : UserDetails {
    override fun getAuthorities(): Collection<out GrantedAuthority> =
        listOf("READ", "WRITE").map { GrantedAuthority { it } }
    override fun getPassword(): String = "1234"
    override fun getUsername(): String = "junny"
    // ...
}
```

## Spring Security에서의 사용자 관리

스프링 시큐리티에서는 사용자의 정보를 가져오는 역할을 하는 UserDetailsService과 유저를 관리해주는 UserDetailsManager가 존재한다.

### UserDetailsService

```kotlin
interface UserDetailsService {
    fun loadUserByUsername(username: String): UserDetails
}
```

loadUserByUsername()는 사용자의 이름을 받아서 UserDetails를 반환하고 존재하지 않는다면 UsernameNotFoundException을 발생시킨다.

<img src="../../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">

Security구현에서 사용할 User를 새롭게 구현한다

```kotlin
data class Users(
    val username: String,
    val password: String,
    val authority: List<String>
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = authority.map { GrantedAuthority { it } }
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
```

또한 UserDetailsService를 구현하여 사용자 정보를 관리한다

```kotlin   
    @Service
    class UserDetailService(
        private val users: List<UserDetails>
    ) : UserDetailsService {
        override fun loadUserByUsername(username: String): UserDetails {
            val user = userRepository.findByUsername(username)
            return user.orElseThrow { UsernameNotFoundException("사용자를 찾을 수 없습니다.") }
        }
    }
 ```

유저를 넣어 빈으로 등록한다.

```kotlin
@Configuration
class UserManagementConfig {
    private val users : UserDetails = Users(
        username = "junnyland",
        password = "1234",
        authority = listOf("USER")
    )

    @Bean
    fun userDetailsService(): UserDetailsService = InMemoryUserDetailsService(listOf(users))

    @Bean
    fun passwordEncoder(): PasswordEncoder = createDelegatingPasswordEncoder()

}
``` 


### UserDetailsManager
이 인터페이스는 UserDetailsService를 확장하여 사용자를 관리할 수 있는 기능을 제공한다.

```kotlin
interface UserDetailsManager : UserDetailsService {
    fun createUser(user: UserDetails)
    fun updateUser(username: UserDetails)
    fun deleteUser(username: String)
    fun changePassword(old: String, new: String)
    fun userExists(username: String): Boolean
}
```

## JDBC를 이용한 사용자 관리
JdbcUserDetailsManager를 사용하여 사용자 정보를 관리할 수 있다.

```SQL
# 사용자 테이블
CREATE TABLE `users` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `enabled` TINYINT(1) NOT NULL,
    PRIMARY KEY (`id`)
);
# 권한 테이블00
CREATE TABLE `authorities` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `authority` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
);
```


