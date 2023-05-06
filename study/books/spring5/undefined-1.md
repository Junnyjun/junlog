# 기본개념

## 관찰자(Observer) 패턴

Subject와 Observer 두 개의 인터페이스로 구성된다

Observer는 관찰자 이며, Subject는 주체이다. Observer는 Subject에 등록되며 Subject로 부터 알림을 수신한다.

<figure><img src="https://blog.kakaocdn.net/dn/bV6TwA/btq3TMlrXgw/QbzRCyncdQ7LbU1EevJiV0/img.png" alt=""><figcaption></figcaption></figure>

* java.util 패키지에서 Observer, Observable 제공
* JDK 1.0에 릴리즈, 제네릭이 없어 type safety 하지 못함
* 옵저버 패턴은 멀티스레드 환경에서 효율적이지 않다private final ExecutorService executorService = Executors.newCacheThreadPool(); public void notifyObservers(String event) { observers.forEach(observer -> { executorService.submit(() -> observer.observe(event)); }); }
  * 스레드 풀 크기로 인해 OutOfMemoryError가 발생 가능하다
  * 클라이언트가 executor가 현재의 작업을 마치기도 전에 새로운 작업을 예약하도록 요청하는 상황에서 점점 더 많은 수의 스레드를 생성할 수 있다.
  * 각 스레드는 자바에서 약 1MB를 소비하므로 일반 JVM 응용 프로그램은 단 몇천 개의 스레드만으로 사용 가능한 메모리를 모두 소모할 수 있다.
* 실제로 자바 9에서 더 이상 사용되지 않는다

## 발행-구독(Publish-Subscribe)

스프링 프레임워크는 @EventListener 애노테이션과 이벤트 발행을 위한 ApplicationEventPublisher를 제공한다. 이는 발행-구독 패턴이다.

<figure><img src="https://blog.kakaocdn.net/dn/bw0Zjt/btq3TMMx6yV/c4CJv55js1KI3HIESUxtJk/img.png" alt=""><figcaption></figcaption></figure>

옵저버 패턴과 다른점은 Publisher와 Subscriber사이 간접접인 계층이 존재한다는 것

이를 _이벤트 채널, 메시지 브로커, 이벤트 버스_라고 부른다.

필터링 및 라우팅은 메시지 내용이나 메시지 주제, 때로는 둘 다에 의해 발생할 수 있다.

토픽 기반 시스템(topic-based system)의 구독자는 관심 토픽에 게시된 모든 메시지를 수신할 수 있다.

[발행-구독 모델 예제](https://github.com/wikibook/spring5-reactive/tree/master/chapter-02/src/main/java/org/rpis5/chapters/chapter\_02/pub\_sub\_app)

SseEmitter를 이용해 스프링 프레임워크를 브로커로 두고 발행-구독 모델 생성가능하다.

그러나 이 프로그램은 수명 주기 이벤트를 위해 도입된 것이지 고부하 및 고성능 시나리오를 위한 것은 아니다.

또 다른 단점 로직 구현을 위헤 스프링 내부 메커니즘 사용, 변경시 안정성이 보장되지 못한다.

@EventListener는 스트림의 종료와 오류 처리에 대한 구현을 추가할 수 없다.

## 리액티브 프레임워크 RxJava

RxJava 1.x 버전은 자바 플랫폼에서 리액티브 프로그래밍을 위한 표준 라이브러리였다.

현재 Akka Stream, 리액터 프로젝트 등이 더 존재한다.

RxJava 라이브러리는 Reactive Extensions(ReactiveX라고도 함)의 자바 구현체이다.

Reactive Extension은 동기식 또는 비동기식 스트림과 관계없이 명령형 언어를 이용해 데이터 스트림을 조작할 수 있는 일련의 고우이다.

ReativeX는 종종 옵저버 패턴, 반복자 패턴 및 함수형 프로그래밍의 조합으로 정의된다.

package rx; public interface Observer\<T> { void onCompleted(); void onError(Throwable var1); void onNext(T var1); }

```
package rx;

public interface Observer<T> {
	void onCompleted();
    void onError(Throwable var1); 
    void onNext(T var1); 
}
```

<figure><img src="https://blog.kakaocdn.net/dn/biGK5u/btq30FdC9vR/dskBqQzTM3OgKoWGQ8iypk/img.png" alt=""><figcaption></figcaption></figure>

* Observable은 0을 포함해 일정 개수의 이벤트 보낼 수 있다
* 연결된 각 구독자에 대한 Observable은 onNext()를 여러 번 호출한 다음 onComplete() 또는 onError()를 호출한다
* onComplete() 또는 onError()가 호출된 이후에는 onNext()가 호출되지 않는다

### RxJava 사용의 전제 조건 및 이점

핵심 개념은 구독자가 관찰 가능한 스트림에 가입한 후, 비동기적으로 이벤트를 생성해 프로세스를 시작한다.

RxJava를 사용하는 접근 방식은 응용 프로그램의 응답성을 크게 높여준다.

* 최초 데이터 수신 시간(Time To First Byte)
* 주요 렌더링 경로(Critical Rendering Path)

메트릭 성능 평가시 더 좋은 결과를 가져온다.
