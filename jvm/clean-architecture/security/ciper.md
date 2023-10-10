# MD5

MD5는 128비트 해시를 생성하는 암호화 해시 함수입니다.\
대부분 무결성 검사를 위해 사용되다 인증을 위해 사용되는 경우로 변하고 있습니다.

<img src="../../../.gitbook/assets/file.excalidraw (18).svg" alt="" class="gitbook-drawing">

같은 값은 항상 같은 32자(16byte)의 해시함수를 생성합니다. \
아무리 큰 데이터도 같은 32자의 해시함수를 생성하기 때문에, 데이터의 유효성을 검사하기 좋습니다.

## How do code?

```kotlin
class Md5(
    private val password: String
) {
    fun encrypt(): String = with(MessageDigest.getInstance("MD5")){
        this.update(password.toByteArray())
        this.digest()
    }
        .let {
            val sb = StringBuilder()
            for (b in it) {
                sb.append(String.format("%02x", b))
            }
            sb.toString()
        }
    fun verify(raw:String,encrypted:String): Boolean = encrypt() == encrypted
}
-------------------------------------------------------------------------------------
fun main(args: Array<String>) {
    val target = "Junnyland"
    val md5=Md5(target)
    val encrypt = md5.encrypt()
    println(md5.verify(target,encrypt)) // true
}
```

