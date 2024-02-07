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
이중 getPassword()와 getUsername()는 각 사용자의 이름과 암호를 반환한다 \
getAuthorities()는 사용자가 작업을 수행할수 있는 권한을 반환한다.

그중 사용자가 작업을 부여받을 수 있는 권한을 나타내는 GrantedAuthority가 존재한다.
```kotlin
interface GrantedAuthority : Serializable {
    fun getAuthority(): String
}

///
val read:GrantedAuthority = GrantedAuthority { "READ" }
val write:GrantedAuthority = GrantedAuthority { "WRITE" }
```

위 두 객체로 Dummy 사용자를 생성하면 다음과 같다.
```kotlin
class DummyUser : UserDetails {
    override fun getAuthorities(): Collection<out GrantedAuthority> = listOf("READ", "WRITE").map { GrantedAuthority { it } }
    override fun getPassword(): String = "1234"
    override fun getUsername(): String = "junny"
    // ...
}
```
  
## Spring Security에서의 사용자 관리
스프링 시큐리티에서는 사용자의 정보를 가져오는 역할을 하는 UserDetailsService과
유저를 관리해주는 UserDetailsManager가 존재한다.