# 스레드 스케줄링

#### 경쟁 범위 (Contention Scope)

사용자 수준 스레드와 커널 수준 스레드 간의 주요 차이점 중 하나는 스케줄링 방식입니다. 다대일(many-to-one) 모델 및 다대다(many-to-many) 모델을 구현하는 시스템에서 스레드 라이브러리는 사용 가능한 경량 프로세스(LWP)에서 사용자 수준 스레드를 실행하도록 스케줄링합니다. 이 방식을 \*\*프로세스 경쟁 범위(Process Contention Scope, PCS)\*\*라고 하는데, CPU 경쟁이 동일한 프로세스에 속하는 스레드들 사이에서 발생하기 때문입니다.

커널이 CPU에서 실행할 커널 수준 스레드를 스케줄링하기 위해 \*\*시스템 경쟁 범위(System Contention Scope, SCS)\*\*를 사용합니다. SCS 스케줄링에서 CPU 경쟁은 시스템의 모든 스레드 사이에서 발생합니다. Windows 및 Linux와 같이 일대일(one-to-one) 모델을 사용하는 시스템은 SCS만 사용하여 스레드를 스케줄링합니다.

일반적으로 PCS는 우선순위에 따라 수행됩니다. 스케줄러는 실행 가능한 스레드 중 가장 높은 우선순위를 가진 스레드를 선택하여 실행합니다. 사용자 수준 스레드 우선순위는 프로그래머가 설정하며, 스레드 라이브러리에 의해 조정되지 않습니다.

#### Pthread 스케줄링 (Pthread Scheduling)

Pthread API는 스레드 생성 시 PCS 또는 SCS를 지정할 수 있는 기능을 제공합니다. Pthread는 다음 경쟁 범위 값을 식별합니다

* PTHREAD\_SCOPE\_PROCESS: PCS 스케줄링을 사용하여 스레드를 스케줄링합니다.
* PTHREAD\_SCOPE\_SYSTEM: SCS 스케줄링을 사용하여 스레드를 스케줄링합니다.

다대다 모델을 구현하는 시스템에서 `PTHREAD_SCOPE_PROCESS` 정책은 사용 가능한 LWP에 사용자 수준 스레드를 스케줄링합니다. LWP의 수는 스레드 라이브러리에 의해 유지되며, 스케줄러 활성화(Section 4.6.5)를 사용할 수 있습니다.

`PTHREAD_SCOPE_SYSTEM` 스케줄링 정책은 다대다 시스템에서 각 사용자 수준 스레드에 대해 LWP를 생성하고 바인딩하여 사실상 스레드를 일대일 정책으로 매핑합니다.

Pthread IPC(프로세스 간 통신)는 경쟁 범위 정책을 설정하고 가져오는 두 가지 함수를 제공합니다:

* `pthread_attr_setscope(pthread_attr_t *attr, int scope)`
* `pthread_attr_getscope(pthread_attr_t *attr, int *scope)`

이 함수들을 통해 프로그래머는 스레드의 특성 집합에 대한 포인터를 전달하고, 스코프 인수에

`PTHREAD_SCOPE_SYSTEM` 또는 `PTHREAD_SCOPE_PROCESS` 값을 전달하여 경쟁 범위를 설정할 수 있습니다.&#x20;
