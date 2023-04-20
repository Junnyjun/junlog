# 리액티브 프로그래밍

#### 17.1 리액티브 매니패스토

리액티브 프로그래밍의 핵심 원칙

* 반응성 : 빠르면서 일정하고 예상할 수 있는 반응 시간을 제공한다.
* 회복성 : 장애가 발생해도 시스템은 반응성은 유지된다.
* 탄력성 : 무거운 작업 부하가 발생하면 자동으로 컴포넌트에 할당된 자원 수를 늘린다.
* 메시지 주도 : 컴포넌트 간의 약한 결합, 고립, 위치 투명성이 유지되도록 시스템은 비동기 메시지 전달에 의존한다.

**17.1.1 애플리케이션 수준의 리액티브**

* 이벤트 스트림을 블록하지 않고 비동기로 처리하는 것이 멀티코어 CPU의 사용률을 극대화할 수 있는 방법이다.\
  이를 위해 스레드를 퓨처, 액터, 일련의 콜백을 발생시키는 이벤트 루프 등과 공유하고 처리할 이벤트를 관리한다.
* 개발자 입장에서는 저수준의 멀티 스레드 문제를 직접 처리할 필요가 없어진다.
* 이벤트 루프 안에서는 절대 동작을 블락하지 않는다는 전제조건이 따른다. (데이터베이스, 파일 시스템 접근, 원격 소비스 호출 등 I/O 관련 동작 등등)
* 비교적 짧은 시간동안만 유지되는 데이터 스트림에 기반한 연산을 수행하며, 보통 이벤트 주도로 분류된다.

**17.1.2 시스템 수준의 리액티브**

* 여러 애플리케이션이 한개의 일관적이고 회복할 수 있는 플랫폼을 구성할 수 있게 해준다.
* 애플리케이션 중 하나가 실패해도 전체 시스템은 계속 운영될 수 있도록 한다.
* 애플리케이션을 조립하고 상호소통을 조절한다. (메시지 주도)
* 컴포넌트에서 발생한 장애를 고립시킴으로 문제가 다른 컴포넌트로 전파되면서 전체 시스템 장애로 이어지는 것을 막는다.(회복성)
* 모든 컴포넌트는 수신자의 위치와 상관 없이 다른 모든 서비스와 통신할 수 있는 위치 투명성을 제공한다. 이를 통해 시스템을 복제할 수 있으며 작업 부하에 따라 애플리케이션을 확장할 수 있다.(탄력성)

***

#### 17.2 리액티브 스트림과 플로 API

리액티브 스트림은 잠재적으로 무한의 비동기 데이터를 순서대로 그리고 블록하지 않는 역압력을 전제해 처리하는 표준 기술이다.

* 역압력 : 이벤트를 제공하는 속도보다 느린 속도로 이벤트가 소비되면서 문제가 발생하는 것을 막는 장치

17.2.1 Flow 클래스 소개

* 자바 9에서는 리액티브 프로그래밍을 제공하는 클래스 java.util.concurrent.Flow를 추가했다.
* 이 클래스는 정적 컴포넌트 하나만 포함하고 있으며 인스턴스화할 수 없다.
* Flow 클래스의 인터페이스
  * Publisher
  * Subscriber
  * Subscription
  * Processor
* Publisher가 항목을 발행하면 Subscriber가 한 개 또는 여러개씩 항목을 소비하는데 Subscription이 이 과정을 관리할 수 있도록 FLow 클래스는 관련된 인터페이스와 정적 메서드를 제공한다.

```
//Publisher가 발행한 리스너로 Subscriber에 등록할 수 있다.
@FunctionalInterface
public interface Publisher<T> {
  void subscribe(Subscriber<? super T> s);
}

//Publisher가 관련 이벤트를 발행할 때 호출할 수 있도록 콜백 메서드 네 개를 정의
public interface Subscriber<T> {
  void onSubscribe(Subscription s);
  void onNext(T t);
  void onError(Throwable t);
  void onComplete();
}

public interface Subscription {
  void request(long n); //publisher에게 이벤트를 처리할 준비가 되었음을 알림
  void cancel(); //publisher에게 이벤트를 받지 않음을 통지
}

//리액티브 스트림에서 처리하는 이벤트의 변환단계를 나타냄
//에러나 Subscription 취소 신호 등을 전파
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> { }
```

인터페이스 구현 규칙

* Publisher는 반드시 Subscription의 request 메서드에 정의된 개수 이하의 요소만 Subscriber에 전파해야 한다.
* Subscriber는 요소를 받아 처리할 수 있음을 Publisher에 알려야 한다. 이를 통해 역압력을 행사할 수 있다.
* Publisher와 Subscriber는 정확하게 Subscription을 공유해야한다. 그러려면 onSubscribe와 onNext 메서드에서 Subscriber는 request 메서드를 동기적으로 호출할 수 있어야 한다.

**17.2.2 첫 번째 리액티브 애플리케이션 만들기**

먼저 현재 보고된 온도를 전달하는 간단한 클래스를 정의한다.

* TempInfo. 원격 온도계를 흉내낸다.(0 \~ 99 사이의  화씨 온도를 임의로 만들어 연속으로 보고)
* TempSubscriber. 레포트를 관찰하면서 각 도시에 설치된 센서에서 보고된 온도 스트림을 출력한다.

```
import java.util.Random;

@AllArgsConstructor
public class TempInfo {
  public static final Random random = new Random();
  
  private final String town;
  private final int temp;
  
  public static TempInfo fetch(String town) {
    if (random.nextInt(10) == 0)
      throw new RuntimeException("Error!"); //10% 확률로 실패
  
    return new TempInfo(town, random.nextInt(100));
  }
}
```

Subscriber가 요청할 때마다 도시의 온도를 전송하도록 Subscription을 구현한다.

```
import java.util.concurrent.Flow.*;

@AllArgsConstructor
public class TempSubscription implements Subscription {
  private final Subscriber<? super TempInfo> subscriber;
  private final String town;

  @Override
  public void request(long n) {
    executor.submit( () -> { //다른 스레드에서 다음 요소를 구독자에게 보낸다.
      for (long i = 0L; i < n; i++) {
        try {
          subscriber.onNext(TempInfo.fetch(town)); //현재 온도를 subscriber로 전달
        } catch (Exception e) {
          subscriber.onError(e);  //온도 가져오기 실패하면 Subscriber로 에러 전달
          break;
        }
      }
    });
  }
  
  @Override
  public void cancel() {
    subscriber.onComplete();  //구독 취소되면 완료신호를 Subscriber로 전달
  }
}
```

새 요소를 얻을 때마다 Subscription이 전달한 온도를 출력하고 새 레포트를 요청하는 Subscriber 클래스를 구현한다.

```
import java.util.concurrent.Flow.*;

public class TempSubscriber implements Subscriber<TempInfo> {
  private Subscription subscription;

  @Override
  public void onSubscribe(Subscription subscription) { // 구독을 저장하고 첫 번째 요청을 전달
    this.subscription = subscription;
    subscription.request(1);
  }
  
  @Override
  public void onNext(TempInfo tempInfo) { // 수신한 온도를 출력하고 다음 정보를 요청
    System.out.println(tempInfo);
    subscription.request(1);
  }
  
  @Override
  public void onError(Throwable t) {
    System.out.println(t.getMessage());
  }
  
  @Override
  public void onComplete() {
    System.out.println("done!");
  }
}
```

리액티브 애플리케이션이 실제 동작할 수 있도록 Publisher를 만들고 TempSubscriber를 이용해 Publisher에 구독하도록 Main 클래스를 구현한다.

```
public class Main {
  public static void main(String[] args) {
    //뉴욕에 새 Publisher를 만들고 TempSubscriber를 구독시킴
    getTemperatures("New York").subscribe(new TempSubscriber());
  }
  
  private static Publisher<TempInfo> getTemperatures(String town) {
    //구독한 Subscriber에게 TempSubscription을 전송하는 Publisher를 반환
    return subscriber -> subscriber.onSubscribe(
        new TempSubscription(subscriber, town)
    );
  }
}
```

&#x20;

**17.2.3 Processor로 데이터 변환하기**

**17.2.4 자바는 왜 플로 API 구현을 제공하지 않는가?**

***

#### 17.3 리액티브 라이브러리 RxJava 사용하기

* 넷플릭스에서 개발한 라이브러리로 자바에서 리액티프 애플리케이션을 구현하는 데 사용한다.
* RxJava는 Flow.Publisher를 구현하는 두 클래스를 제공한다.
  * io.reactivex.Flowable : 역압력을 지원하는 Flow
  * io.reactivex.Observable : 역압력을 지원하지 않는 Flow.
* 천 개 이하의 요소를 가진 스트림이나 마우스 움직임, 터치 이벤트 등 역압력을 적용하기 힘든 GUI 이벤트, 자주 발생하지 않는 종류의 이벤트에는 역압력을 적용하지 말 것을 권장한다.

**17.3.1 Observable 만들고 사용하기**

**17.3.2 Observable을 변환하고 합치기**
