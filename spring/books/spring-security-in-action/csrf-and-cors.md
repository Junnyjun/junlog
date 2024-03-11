# CSRF\&CORS 적용

## CSRF
`CSRF`는 `Cross-Site Request Forgery`의 약자로, 사용자가 의도하지 않은 요청을 보내는 공격을 막기 위한 방어 기술이다.\
`CSRF` 공격은 사용자가 의도하지 않은 요청을 보내는 공격으로, 사용자가 의도하지 않은 요청을 보내는 것을 막기 위해 `CSRF` 토큰을 사용한다.

스프링 시큐리티에서는 `CSRF` 공격을 방어하기 위해 `CsrfFilter`를 사용한다. \
`CsrfFilter`는 `CSRF` 토큰을 생성하고, 요청이 유효한지 확인한다. \
`CsrfFilter`는 `CsrfConfigurer`를 통해 설정할 수 있다.

사용자가 처음 웹 페이지를 열때 `CSRF` 토큰을 생성하고, 이 토큰을 쿠키에 저장한다. \
이후 사용자가 요청을 보낼 때마다 `CSRF` 토큰을 함께 보내고, 서버에서는 이 토큰을 검증한다.

```kotlin
class CustomCsrfFilter : Filter {
    private val logger = LoggerFactory.getLogger(CustomFilter::class.java)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filter: FilterChain) {
        val csrf = req.getAttribute("_csrf")
        filter.doFilter(req, res)
    }
}
```

## CSRF 맞춤 구현

<details markdown="1">
  <summary> TokenRepository </summary>

```kotlin
@Entity
class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val token: UUID,
    val identifier: String,
) {
    constructor() : this(0, UUID.randomUUID(), "JUNNYLAND")
}
///
interface TokenRepository :JpaRepository<Token, String>{}
```
</details>
<details markdown="1">
  <summary> CsrfRepository </summary>

```kotlin
@Entity
class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: String,
    val token: String,
    val identifier: String,
) {}
///
interface TokenRepository :JpaRepository<Token, String>{}
```
</details>