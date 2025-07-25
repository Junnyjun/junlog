# 프로세스의

#### 프로세스의 동시 실행과 동적 생성

대부분의 시스템에서 프로세스는 **동시에(concurrently)** 실행될 수 있으며,\
**동적으로 생성 및 제거**될 수 있어야 한다.\
따라서 이러한 시스템은 **프로세스 생성 및 종료를 위한 메커니즘**을 제공해야 한다.

이 절에서는 프로세스 생성에 관여하는 메커니즘과\
UNIX 및 Windows 시스템에서의 프로세스 생성 방식을 설명한다.

***

#### Process Creation

프로세스는 실행 중에 여러 개의 \*\*자식 프로세스(child processes)\*\*를 생성할 수 있다.\
이때 원래의 프로세스는 \*\*부모 프로세스(parent process)\*\*라고 한다.

이러한 구조는 다시 자식이 자식을 생성하는 방식으로 이어지며,\
\*\*프로세스 트리(process tree)\*\*를 구성한다.

운영체제는 보통 각 프로세스를 고유한 정수형 ID인 \*\*PID(Process ID)\*\*로 식별한다.\
PID는 커널 내부에서 해당 프로세스의 속성을 참조하기 위한 인덱스로 사용된다.

***

#### Linux에서의 프로세스 트리 예시

Linux 시스템에서의 프로세스 트리는 다음과 같은 특징을 가진다:

* 루트 프로세스는 항상 `systemd`이며, PID는 1이다.
* systemd는 부팅 직후 생성되어 이후 사용자 프로세스들을 생성한다.

예를 들어 다음과 같은 트리가 존재할 수 있다:

* `systemd` → `logind`, `sshd`
* `logind` → `bash` → `ps`, `vim`
* `sshd` → 원격 로그인 사용자를 위한 쉘 실행

***

#### init와 systemd

전통적인 UNIX 시스템에서는 `init`이 루트 프로세스로 사용되었다.\
→ 이 경우 `init`은 PID 1을 가지며, 부팅 시 가장 먼저 실행된다.

Linux는 초기에 System V 기반의 `init`을 사용했으나,\
최근 배포판은 \*\*더 유연하고 기능이 많은 `systemd`\*\*로 대체하였다.

시스템의 전체 프로세스 트리는 `ps -el` 혹은 `pstree` 명령어를 통해 확인 가능하다.

***

#### 자식 프로세스의 자원 구성

자식 프로세스는 다음과 같은 자원이 필요하다:

* CPU 시간
* 메모리
* 파일
* 입출력 장치

운영체제에 따라 자식은:

* 부모로부터 직접 자원을 상속받거나
* 운영체제로부터 독립적으로 할당받을 수 있다

자식에게 할당되는 자원을 제한함으로써\
과도한 자식 생성으로 인한 시스템 과부하를 방지할 수 있다.

또한, 부모는 자식에게 초기 데이터(예: 파일명)를 넘겨줄 수 있다.

***

#### 실행 방식의 두 가지 분기

프로세스 생성 후의 실행에는 두 가지 방식이 있다:

1. 부모와 자식이 동시에 실행됨
2. 부모가 자식이 종료될 때까지 기다림

주소 공간 구성 또한 두 가지 방식이 존재한다:

1. 자식이 부모와 동일한 프로그램 및 데이터로 구성됨
2. 자식이 새로운 프로그램을 로드함

***

#### UNIX에서의 fork()와 exec()

UNIX에서는 `fork()` 시스템 호출로 새로운 프로세스를 생성한다:

* `fork()`는 부모의 주소 공간을 복사한 새로운 자식 프로세스를 생성한다
* 두 프로세스는 `fork()` 호출 직후부터 **같은 명령어 이후를 실행**

→ 차이는 `fork()`의 반환값:

* 자식 프로세스에는 0 반환
* 부모에게는 자식의 PID 반환

`fork()` 이후, 자식 프로세스는 일반적으로 `exec()`를 호출하여\
**새로운 프로그램을 자신의 주소 공간에 로드한다.**

`exec()`는 현재 프로세스의 메모리를 파괴하고,\
대신 지정된 바이너리를 로드해 실행을 시작한다.

→ `exec()`는 **성공 시 절대 반환하지 않음**, 실패 시에만 복귀

***

#### UNIX 프로세스 생성 예제

```c
pid = fork();
if (pid == 0) {
  execlp("/bin/ls", "ls", NULL);
} else {
  wait(NULL);
  printf("Child Complete");
}
```

위 예시는:

* 자식 프로세스는 `ls` 명령어 실행
* 부모는 자식이 종료될 때까지 `wait()`로 대기
* 자식이 종료되면 "Child Complete" 출력

자식은 부모로부터:

* 권한
* 스케줄링 속성
* 열린 파일

등을 상속받는다.

***

#### Windows의 CreateProcess()

Windows에서는 `CreateProcess()` 함수로 자식 프로세스를 생성한다.

특징:

* 부모와 자식의 주소 공간은 **서로 다름**
* 자식 생성 시 명시적으로 로드할 프로그램을 지정해야 함
* 최소 10개의 파라미터를 전달해야 함

```c
CreateProcess(
  NULL, "C:\\WINDOWS\\system32\\mspaint.exe",
  NULL, NULL, FALSE, 0, NULL, NULL,
  &si, &pi
);
WaitForSingleObject(pi.hProcess, INFINITE);
```

이 예제는:

* 그림판 실행
* 부모는 자식 종료까지 대기
* 이후 핸들 정리

`STARTUPINFO`와 `PROCESS_INFORMATION` 구조체를 사용하여\
프로세스 초기 정보와 핸들을 구성한다.

***

#### Process Termination

프로세스는 마지막 명령어를 실행한 후\
`exit()` 시스템 호출을 통해 종료된다.

* `exit()`는 부모에게 상태값을 반환
* 프로세스의 모든 자원(메모리, 파일, I/O 버퍼)은 운영체제가 회수

부모는 `wait()`를 통해:

* 자식의 종료 상태를 얻고
* 종료된 자식의 PID를 얻을 수 있다

***

#### 좀비 프로세스와 고아 프로세스

* 종료된 프로세스는 자원의 대부분이 회수되지만\
  → **프로세스 테이블 항목과 종료 상태는 남아 있음**
* `wait()`를 호출하지 않은 부모가 있다면\
  → 해당 자식은 **좀비(zombie)** 상태가 된다

UNIX 시스템에서는 부모가 사라질 경우\
→ `init`이 고아 자식 프로세스를 **자동으로 수거**함

Linux에서는 `systemd` 또는 기타 시스템 프로세스가\
이 역할을 수행할 수 있다.

***

#### Android의 프로세스 중요도 계층

Android는 **자원이 부족할 때 종료할 프로세스를 우선순위로 결정**한다.\
다음은 중요도 순서:

1. **Foreground process**: 화면에 보이는 현재 앱
2. **Visible process**: 직접 보이지 않지만 사용자와 관련된 활동
3. **Service process**: 음악 재생 등 백그라운드 서비스
4. **Background process**: 사용자에게 보이지 않는 작업
5. **Empty process**: 활성 컴포넌트가 없는 앱

→ 시스템은 자원이 부족해질수록 **덜 중요한 프로세스부터 종료**한다.

Android는 프로세스 종료 전 상태를 저장하고,\
사용자가 다시 앱으로 돌아오면 이전 상태부터 재개할 수 있도록 설계되어 있다.
