# API 안정성을 확보하라

#### 프로그래밍에서는 안정적이고 표준 API를 선호한다

API가 변경되고 개발자가 이를 업데이트 했다면 여러 코드를 수동으로 업데이트 해야함\
많은 곳에서 api에 의존적이면 변경사항이 많을 수 있음

사용자가 새로운 API를 배워야함\
변경된 api를 쓰는 쪽에서는 변경사실을 알아야 하고 변경 부분에 대한 이해가 필요함

한번에 안정적인 API가 나왔으면 하지만 좋은 API 설계는 어렵기 때문에 우선 만들고 지속적으로 발전시켜 나가야 한다. 그래서 API 안정성을 지정해서 정보를 제공함으로써 api를 사용하는 곳에서 안정성을 확인할 수 있음

안정성 제공에 가장 간단한 방법은 문서에서 API 일부가 불안정한지 명확하게 지정

버전을 사용해 전체 라이브러리 또는 모듈의 안정성을 지정

#### Semantic Versioning : MAJOR.MINOR.PATCH의 3개 부분으로 구성

1. 호환되지 않는 API 변경을 수행하는 경우 MAJOR 버전
2. 이전 버전과 호환되는 방식으로 기능을 추가하는 경우 MINOR 버전
3. 이전 버전과 호환되는 버그를 수정을 할 때 PATCH 버전

추가로 안정적인 API에 새로운 요소를 추가할 때, 아직 해당 요소가 안정적이지 않다면 먼저 다른 브랜치에 해당 요소를 두고 \
일부 사용자가 이를 사용하도록 허용하려면 일단 Experimental 메타 어노테이션을 사용해 알려주는 것이 좋음

```kotlin
@Experimental(level = Experimental.Level.WARNING)
annotaion class ExperimentalNewApi

@ExperimentalNewApi
suspend fun getUsers(): List<User> {
   //... 
}
```

안정적인 API의 일부를 변경해야 한다면, 전환하는 데 시간을 두고 Deprecated 어노테이션을 활용해 사용자에게 미리 알려주는 것이 좋음

```kotlin
@Deprecated("User suspending getUsers instead")
fun getUsers(): List<User> {
   //... 
}
```

또한 직접적인 대안이 있는 ReplaceWith 경우 IDE에서 자동 전환을 허용하도록 지정

```kotlin
@Deprecated("User suspending getUsers instead", ReplaceWith("getUsers()"))
fun getUsers(callback: (List<User> -> Unit) {
   //... 
}
```

#### 정리

사용자는 API 안정성을 알고 써야함

모듈 또는 라이브러리 작성자와 해당 사용자 간의 올바른 통신이 중요

또한 안정적인 API의 변경사항을 적용하려면 충분한 시간을 가져야 함
