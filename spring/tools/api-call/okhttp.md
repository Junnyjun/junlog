# Okhttp

OkHttp는 Square, Inc.라는 회사에서 개발되었습니다. \
OkHttp는 Square, Inc.의 개발자들이 만든 고성능 HTTP 클라이언트 라이브러리로, 안드로이드 및 자바 애플리케이션에서 네트워크 통신을 처리하는 데 사용됩니다.&#x20;

### SETUP

```groovy
dependencies {
    implementation 'com.squareup.okhttp3:okhttp:4.9.2'
}
```

## HOW DO CODE ?

### GET

```kotlin
class OkhttpGetApiCall(
    private val okhttpClient: OkHttpClient = OkHttpClient()
) {

    private val getRequest = okhttp3.Request.Builder()
        .url("https://gorest.co.in/public/v2/users")
        .get()
        .header("content-type", "application/json")
        .build()

    fun get(): ResponseBody = okhttpClient.newCall(getRequest)
        .execute()
        .peekBody(Long.MAX_VALUE)

    fun getAsync(): Unit = okhttpClient.newCall(getRequest)
        .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("Failed to execute request")
            override fun onResponse(call: Call, response: Response) = println("Response received ${response.body?.string()}")
        })
}
```

### POST, PUT, PATCH, DELETE

```kotlin
class OkhttpPostApiCall(
    private val okhttpClient: OkHttpClient = OkHttpClient(),
) {

    private val postRequest = okhttp3.Request.Builder()
        .url("https://gorest.co.in/public/v2/users")
        .post(FormBody.Builder() // patch put delete ... 
            .add("name","junny")
            .add("email","junny@mail.com")
            .add("gender","junny")
            .build()
        )
        .header("content-type", "application/json")
        .build()

    fun post(): ResponseBody = okhttpClient.newCall(postRequest)
        .execute()
        .peekBody(Long.MAX_VALUE)

    fun postAsync(): Unit = okhttpClient.newCall(postRequest)
        .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = println("Failed to execute request")
            override fun onResponse(call: Call, response: Response) = println("Response received ${response.body?.string()}")
        })
}
```

