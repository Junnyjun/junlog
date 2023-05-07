# 리액티브 스트림

CompletionStage를 이용하는 자바 코어 라이브러리 RxJava와 같은 다양한 라이브러리 존재

코드 작성시 다양한 선택 가능

## 풀 방식과 푸시 방식

* 리액티브 초기 단계에서 모든 라이브러리의 데이터 흐름은 소스에서 구독자에서 푸시되는 방식이었다.
* 푸시 모델을 채택하는 가장 큰 이유는 요청하는 횟수를 최소화 하기 위해서이다.
* 반면 푸시 모델만 사용하는것은 _기술적 한계_가 있다.
  * 메시지 기반 통신의 본질은 요청에 응답하는 것
  * 프로듀서가 컨슈머의 처리 능력을 무시하면 전반적인 시스템 안정성에 영향을 미칠 수 있다

### 프로듀서 컨슈머 동작 케이스

#### 1. 느린 프로듀서 빠른 컨슈머

동적으로 시스템의 처리량을 증가시키는 것은 불가능 (프로듀서가 따라와줘야함)

#### 2. 빠른 프로듀서와 느린 컨슈머

프로듀서가 컨슈머가 처리할 수 있는 것보다 훨씬 많은 데이터 전송

부하를 받는 컴포넌트에 치명적인 오류가 발생가능

처리되지 않은 원소를 큐에 수집하는 방식으로 처리. 3가지 방식 존재

1. 무제한 큐 - 메모리 한도 도달시 전체 시스템에 손상
2. 크기가 제한된 드롭 큐 - 데이터 세트가 변경됨. 메시지의 중요성이 낮을 때 주로 사용
3. 크기가 제한된 블록킹 큐 - 한계에 다다르면 메시지 유입 차단. 블록킹 모델로 리액티브 성격에 반한다

> 해결책은 배압 제어 메커니즘💡

***

## 리액티브 스트림의 기본 스펙

package org.reactivestreams

```
<dependency>
    <groupId>org.reactivestreams</groupId>
    <artifactId>reactive-streams</artifactId>
    <version>1.0.3</version>
</dependency>
```

* Publisher (RxJava의 Observable)

```
public interface Publisher<T> {
	public void subscribe(Subscriber<? super T> s);
}
```

* Subscriber (RxJava의 Observer)

```
public interface Subscriber<T> {
	void onSubscribe(Subscription s); // Invoked after calling Publisher#subscribe
	void onNext(T t);
	void onError(Throwable t);
	void onComplete();
}
```

* Subscription (신규 인터페이스)

원소 생성을 제어하기 위한 기본적인 사항 제공

```
public interface Subscription {
	void request(long n);
	void cancel();
}
```

org.reactivestreams는 하이브리드 푸시-풀 모델을 제공한다.

순수 푸시 모델을 사용하고 싶은 경우 최대 개수 요소 요청 request(Long.MAX\_VALUE)

순수 풀 모델을 사용하고 싶은 경우 onNext()가 호출 될 때 마다 요청하면 된다.

* Processor

```
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

***

## 리액티브 스트림 기술 호환성 키트(TCK)

모든 동작을 검증하고 반응 라이브러리를 표준화해 호환하는지 확인하는 공통 도구

[ reactive-streams/reactive-streams-jvmReactive Streams Specification for the JVM. Contribute to reactive-streams/reactive-streams-jvm development by creating an account on GitHub.github.com](https://github.com/reactive-streams/reactive-streams-jvm/tree/master/tck)

***

## JDK 9

더그 리(Doug Lee)가 JDK 9에 리액티브 스트림 스펙을 추가하자는 제안을 했다

java.util.concurrent.Flow 클래스 내 정적 하위 클래스로 제공됨 (JDK 9)

하지만 이전에 많은 프로젝트는 org.reactivestreams.\* 패키지에 제공된 스펙을 사용하고 있었다.

FlowAdapters를 통해 가능하다

```
Flow.Publisher jdkPublisher = ...;
Publisher external = FlowAdapters.toPublisher(jdkPublisher);
Flow.Publisher jdkPublisher2 = FlowAdapter.toFlowPublisher(external);
```

***

## 리액티브 스트림을 활용한 비동기 및 병렬 처리

리액티브 스트림 API 규칙

* Publisher, Subscriber가 생성 소비한 모든 신호는 처리중에 논블로킹이어야 하며 방해받지 않아야 한다.
* on\*\*\* 메서드의 호출은 스레드 안전성을 보장하는 방식으로 신호를 보내야 하며, 다중 스레드에서 수행되는 경우 외부적인 동기화를 사용해야 한다.
  * 스트림의 요소를 병렬로 처리할 수 없다...

자원을 효율적으로 활용하기 위해 스트림 처리 파이프를 독립적인 스레드에 할당해 처리하도록 할 수 있다.

#### 일반적인 스트림 처리 파이프

```
             < 처리 흐름 >
소스   →   필터 → (이외 작업들) → 맵   →   목적지
```

#### 소스와 목적 데이터 사이의 비동기 처리 부분

* 두개의 스레드 사용 (처리 흐름을 소스와 함께)

사용 케이스: 원본 리소스가 목적지 리소스보다 더 적게 로드될 때

```
소스   →   필터 → (이외 작업들) → 맵   →   목적지
(              스레드 A             ) (스레드 B)
```

* 두개의 스레드 사용 (처리 흐름을 목적지와 함께)

사용 케이스: 목적지 리소스가 원본 리소스보다 더 적게 로드될 때

```
소스   →   필터 → (이외 작업들) → 맵   →   목적지
(스레드 A) (             스레드 B             )
```

* 각 컴포넌트 간 비동기 처리경계

사용 케이스: 메시지 생산과 소비가 모두 CPU 집약적인 작업일 때

```
소스   →   필터   →   (이외 작업들)   →   맵   →   목적지
(스레드 1) (스레드 2)   ( ... )    (스레드 N-1) (스레드 N)
```

***

## 리액티브 전망의 변화

#### RxJava

RxReactiveStream 클래스로 리액티브 타입을 변환해주는 추가 모듈 제공

```
<dependency>
    <groupId>io.reactivex</groupId>
    <artifactId>rxjava-reactive-streams</artifactId>
    <version>1.2.1</version>
</dependency>
```

#### 또 다른 리액티브 라이브러리

* Vert.x
* Ratpack
* (리액티브 스트림 기반의) MongoDB 드라이버

### 리액티브 기술 조합

리액티브 스트림 스펙은 연산자 사이에 여러 통신 수단을 허용한다

이러한 유연성을 비동기 영역을 다양하게 배치할 수 있게 해준다

리액티브 라이브러리 공급자들이 이러한 부분을 책임지고 구현하도록 강제하고 있다
