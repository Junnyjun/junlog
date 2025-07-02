# 스레드 모델

#### 구성 개요

> 사용자 스레드(user-level thread) ↔ 커널 스레드(kernel-level thread)의 관계\
> 스레드 구현 방식에 따른 모델 세 가지:

***

#### ① Many-to-One 모델

* **여러 사용자 스레드 → 하나의 커널 스레드**
* 사용자 공간(user space)에서 스레드 관리 → 효율적
* 단점:
  * 하나의 스레드가 **blocking system call**을 하면 **전체 프로세스가 block**
  * 멀티코어 CPU에서도 **병렬 실행 불가**
* 예: **Green Threads** (구 Java, Solaris 초기 버전)

***

#### ② One-to-One 모델

* **각 사용자 스레드 → 하나의 커널 스레드**
* 장점:
  * blocking system call 발생 시 **다른 스레드 실행 가능**
  * 멀티코어에서 병렬성 보장
* 단점:
  * 스레드 수 = 커널 스레드 수 → 많은 스레드는 **성능 저하 유발**
* 예: **Linux**, **Windows 계열 운영체제**

***

#### ③ Many-to-Many 모델

* **많은 사용자 스레드 ↔ 소수 또는 동일 수의 커널 스레드**
* 장점:
  * blocking 시 커널이 다른 스레드 실행
  * 사용자 스레드 수 제약 없음
* **Two-Level 모델**:
  * many-to-many에 기반하되, 특정 user thread를 특정 kernel thread에 **고정(binding)** 가능

***

* many-to-many는 유연하지만 **구현 난이도 높음**
* 현재는 **one-to-one이 표준화** 경향
* 하지만 고급 concurrency 라이브러리들은 여전히 many-to-many 기반 **task-to-thread 매핑**을 제공
