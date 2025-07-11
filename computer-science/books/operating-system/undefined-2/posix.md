# POSIX 공유 메모리

POSIX 공유 메모리

**■ 개요**

POSIX shared memory는 여러 프로세스가 **공통된 메모리 영역**을 통해 데이터를 공유할 수 있도록 하는 기능이다. 이 메커니즘은 `shm_open()`을 통해 공유 메모리 객체를 만들고, `mmap()`을 통해 이를 프로세스의 주소 공간에 매핑하여 사용된다.

**■ 주요 시스템 콜 및 흐름**

1. **shm\_open(name, flag, mode)**
   * 공유 메모리 객체 생성 또는 열기
   * `name`: "/myregion"과 같이 `/`로 시작하는 이름
   * `flag`: `O_CREAT | O_RDWR`
   * `mode`: `0666` 권한 등
2. **ftruncate(fd, size)**
   * 생성된 공유 메모리 객체의 크기 지정
3. **mmap(NULL, size, PROT\_READ | PROT\_WRITE, MAP\_SHARED, fd, 0)**
   * 메모리 공간을 프로세스 주소 공간에 매핑
4. **munmap(ptr, size)**
   * 메모리 매핑 해제
5. **shm\_unlink(name)**
   * 공유 메모리 객체 삭제 (언링크)

**■ 프로세스 간 공유 흐름 예시**

* **생성자 프로세스**: `shm_open()` → `ftruncate()` → `mmap()` → 쓰기
* **소비자 프로세스**: `shm_open()` → `mmap()` → 읽기

**■ 특징**

* **커널의 개입은 최초 설정 시에만 필요**
* 이후 접근은 **일반 메모리 접근 속도**로 처리
* **동기화는 별도로 구현 필요 (세마포어 등)**

***

#### POSIX 메시지 큐&#x20;

**■ 개요**

POSIX message queue는 **커널 수준의 메시지 큐**로, 메시지를 구조체 형태로 송수신 가능하며, **이름 기반**으로 관리된다. 프로세스 간 데이터 교환 시 유용하며, 공유 메모리보다 **동기화 구현이 간편**하다.

**■ 주요 시스템 콜 및 흐름**

1. **mq\_open(name, flag, mode, attr)**
   * 메시지 큐 생성 및 열기 (`O_CREAT | O_RDWR` 등)
   * `attr`은 `mq_attr` 구조체로, 큐 속성 지정 가능
2. **mq\_send(mqd, msg\_ptr, msg\_len, priority)**
   * 메시지 전송
   * `priority`는 우선순위 지정 가능
3. \*_mq\_receive(mqd, msg\_ptr, msg\_len, priority)_
   * 메시지 수신, 블로킹/논블로킹 가능
4. **mq\_getattr() / mq\_setattr()**
   * 큐 속성 조회 및 변경
5. **mq\_close() / mq\_unlink()**
   * 큐 종료 및 삭제

**■ 메시지 큐 구조**

* 메시지는 **FIFO 또는 우선순위 기반**으로 저장
* `mq_attr` 구조체
  * `mq_maxmsg`: 큐에 저장 가능한 최대 메시지 수
  * `mq_msgsize`: 각 메시지의 최대 크기

**■ 특징**

* 커널이 직접 **동기화 및 메시지 순서 관리** 수행
* 비동기식 신호 처리도 가능 (예: `SIGEV_SIGNAL`)
* 공유 메모리보다 **초기 설정은 복잡하지만 관리가 안정적**

***

#### 결론: 공유 메모리 vs 메시지 큐 비교

| 항목     | 공유 메모리         | 메시지 큐             |
| ------ | -------------- | ----------------- |
| 성능     | 효율고 (메모리 접근)   | 비교적 느림 (커널 호출 수반) |
| 동기화    | 프로그래머 직접 구현 필요 | 내장 큐 관리로 간편함      |
| 사용 용도  | 대용량 연속 데이터 전송  | 이벤트 알림, 구조화된 통신   |
| 설정 복잡도 | 간단             | 속성 지정 등 복잡함       |

