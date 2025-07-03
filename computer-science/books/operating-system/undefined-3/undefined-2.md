# 스레드 라이브러리

스레드 라이브러리는 **스레드를 생성하고 관리하는 API**를 제공하는 도구이다. 스레드 라이브러리의 구현 방식은 두 가지가 있다.

**스레드 라이브러리 구현 방식**

1. **유저 공간 라이브러리**:
   * 커널 지원 없이 사용자 공간에서 구현됨.
   * 라이브러리 호출이 **시스템 콜을 수반하지 않으며**, 로컬 함수 호출로 처리됨.
2. **커널 공간 라이브러리**:
   * 운영체제가 직접 지원하며, 모든 코드와 데이터 구조가 커널 공간에 존재.
   * 라이브러리 함수 호출이 **시스템 콜을 유발**.

***

**대표적인 스레드 라이브러리 종류**

1. **POSIX Pthreads**
   * 유닉스 계열 운영체제에서 널리 사용.
   * 커널/유저 수준 모두 구현 가능.
   * `pthread_create()`, `pthread_join()` 등의 함수로 스레드 생성/관리.
   * 전역 변수는 모든 스레드 간 공유됨.
   * 프로그램 예시는 `runner()` 함수에서 `sum` 계산 후 `pthread_exit()`로 종료.
2. **Windows Threads**
   * Windows API 기반 커널 수준 라이브러리.
   * `CreateThread()`를 통해 스레드 생성.
   * `WaitForSingleObject()`로 스레드 종료 대기.
   * 여러 스레드를 기다릴 땐 `WaitForMultipleObjects()` 사용.
3. **Java Threads**
   * 자바는 모든 프로그램이 최소한 하나의 스레드를 포함함.
   * `Thread` 클래스를 상속하거나 `Runnable` 인터페이스 구현으로 스레드 정의.
   * `start()`로 스레드 시작 → 내부적으로 `run()` 호출.
   * `join()` 메서드를 사용해 다른 스레드 종료 대기.
   * 자바 1.8 이상부터 **람다(lambda)** 문법 지원 → 코드 간결화 가능.
   * Java는 전역 변수 개념이 없어, 공유 데이터 접근 시 명시적 구조 필요.

***

**Java Executor Framework**

* Java 1.5부터 도입된 **Executor 프레임워크**는 더 정교한 스레드 제어를 제공.
* `ExecutorService`를 이용해 `execute()`나 `submit()`로 작업 수행.
* `submit()`은 **Callable 객체**를 받아 **Future 객체**로 결과 반환.
* `get()`으로 결과를 비동기적으로 수신.

```java
ExecutorService pool = Executors.newSingleThreadExecutor();
Future<Integer> result = pool.submit(new Summation(upper));
System.out.println("sum = " + result.get());
```

* Java는 공유 데이터가 명시적으로 전달되어야 하며, **Callable → Future** 패턴을 통해 **스레드 반환값**도 안전하게 처리 가능.
* `Executor`는 **스레드 생성과 실행을 분리**하여 많은 수의 동시 작업을 안정적으로 관리할 수 있도록 설계됨.
