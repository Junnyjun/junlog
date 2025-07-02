# 스레드 라이브러리

스레드 라이브러리는 \*\*사용자 수준(user-level)\*\*에서 스레드를 생성하고 관리하기 위한 API 집합이다. 이 라이브러리는 사용자 수준에서 구현되어 커널 호출 없이 작동하며, 주요 목적은 **스레드 생성, 종료, 동기화, 스케줄링**을 관리하는 것이다.

***

**스레드 라이브러리 구현 방식**

1. **사용자 수준(User level)**
   * 라이브러리 자체가 **커널에 의존하지 않고** 사용자 공간에서 구현됨
   * 예시: GNU Portable Threads
2. **커널 수준(Kernel level)**
   * 커널 내부에서 직접 스레드 라이브러리 API 제공
   * 예시: Windows API, Linux Native POSIX Threads

***

**주요 스레드 라이브러리**

* **POSIX Pthreads**
  * UNIX 계열 시스템에서 사용됨
  * **C 기반의 표준 스레드 API** 제공
  * POSIX 표준에 기반함
* **Windows Threads**
  * Windows OS에서 제공하는 기본 API
  * 커널 수준에서 직접 관리
  * C/C++에서 직접 사용 가능
* **Java Threads**
  * `java.lang.Thread` 클래스를 통해 제공
  * JVM 위에서 실행됨
  * 내부적으로는 기본 OS의 스레드 기능을 사용

***

**Pthreads 예시**

```c
#include <pthread.h>

pthread_t tid;
pthread_create(&tid, NULL, function_pointer, arg);
pthread_join(tid, NULL);
```

* `pthread_create()`로 스레드를 생성하고
* `pthread_join()`으로 해당 스레드가 종료될 때까지 대기
