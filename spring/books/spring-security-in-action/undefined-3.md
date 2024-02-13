# 암호 처리

## PasswordEncoder 
`passwordEncoder`는 비밀번호를 암호화하는데 사용된다.
인증 프로세스에서 암호가 유효한지를 확인한다.

```kotlin
public interface PasswordEncoder {
    fun encode(rawPassword: CharSequence): String
    fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean
}

```