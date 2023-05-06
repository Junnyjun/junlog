# 왜 리액티브인가?

## &#x20;<a href="#undefined" id="undefined"></a>

### 수요(부하)의 변화 및 외부서비스의 가용성 변화에 대해 어떻게 대응할 것인가?

탄력성(slasticity)

```
다양한 작업 부하에서 응답성을 유지하는 능력
사용자가 많으면 처리량이 자동으로 증가, 수요가 감소하면 자동으로 감소
평균 지연 시간에 영향 없이 시스템 확장 가능
```

복원력

```
시스템 실패에도 반응성을 유지할 수 있는 능력
기능 요소 격리 -> 내부 장애 격리 -> 독립성 확보
```

탄력성 + 복원력 => 응답성

## 메세지 기반 통신 <a href="#undefined" id="undefined"></a>

분산 시스템에서 서비스간 통신시, 자원을 효율적으로 사용\


<figure><img src="https://velog.velcdn.com/images/garin0112/post/c981439d-9130-4fb3-b38d-6b13d0f50b6c/image.png" alt=""><figcaption></figcaption></figure>

ex) 메시지 브로커 사용

## 왜 리액티브 스프링인가? <a href="#undefined" id="undefined"></a>

```
Akka : Scala 생태계의 일부로 구축. Java 지원
자바 커뮤니티에서 인기를 누리진 못함.
Vert.x : 논블로킹 및 이벤트 기반
```

## 서비스 레벨에서의 반응성 <a href="#undefined" id="undefined"></a>

콜백

```
공유 데이터 변경
콜백 지옥
멀티 스레딩 이해 필요
```

java.util.concurrent.Future

```
Future 클래스 사용 -> 결과값 반환 지연
콜백 지옥 피하기
멀티 스레드 복잡성 숨김
자바 8 - CompletableFuture
컨텍스트 스위칭 문제
```
