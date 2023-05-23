# 프로세스 API

#### fork() 시스템 콜

<figure><img src="https://blog.kakaocdn.net/dn/Sm8TL/btrhNph5yTh/cLGW0PTr6jWZorK4z6kQ0K/img.png" alt="" width="479"><figcaption></figcaption></figure>

fork()로 생성된 프로세스는 호출한 프로세스의 복사본이다.\
자식 프로세스는 main()에서 시작하지 않고, fork()를 호출한 시점에서 시작한다.&#x20;

자식 프로세스는 부모 프로세스와 완전히 동일하지는 않다.\
자식 프로세스는 자신의 주소 공간, 자신의 레지스터, 자신의 PC값을 갖는다.

또한 fork() 시스템 콜의 반환 값이 서로 다르다.\
fork()로부터 부모 프로세스는 생성된 자식의 프로세스의 PID를 반환받고, 자식 프로세스는 0을 반환받는다.

<figure><img src="https://blog.kakaocdn.net/dn/smQBZ/btrhMmzeOlt/VdmvoKlF9qWBqEjEb2VK90/img.png" alt="" width="375"><figcaption></figcaption></figure>

단일 CPU 시스템에서 이 프로그램을 실행하면 프로세스가 생성되는 시점에 2개의 프로세스 중 하나가 실행된다.

위와 같이 실행 순서가 달라질 수 있다.\
CPU 스케줄러(Scheduler)는 실행할 프로세스를 선택한다.

스케줄러의 동작은 복잡하고 상황에 따라 다른 선택이 이루어지기 때문에,\
어느 프로세스가 먼저 실행된다라고 단정하기 어렵다.

이 비결정성(Nondeterminism)으로 인해 멀티 쓰레드 프로그램 실행 시 다양한 문제가 발생한다.&#x20;

#### wait() 시스템 콜

<figure><img src="https://blog.kakaocdn.net/dn/ppuIi/btrhMJAWnsC/7I9OT8SwiGt4Armdkl4F10/img.png" alt="" width="375"><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/cWxUZ2/btrhOIgZSiG/cK6abYKaswj8BYAqtSsiBk/img.png" alt="" width="375"><figcaption></figcaption></figure>

부모 프로세스는 wait() 시스템 콜을 호출하여 자식 프로세스 종료 시점까지 \
자신의 실행을 잠시 중지시킨다.

자식 프로세스가 종료되면 wait()는 리턴한다.

#### exec() 시스템 콜

exec() 시스템 콜은 자기 자신이 아닌 다른 프로그램을 실행해야 할 때 사용한다.

<figure><img src="https://blog.kakaocdn.net/dn/2KvkB/btrhNojcZvs/87xakLV0sT6APQaFYEAAKk/img.png" alt="" width="375"><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/ldJH4/btrhLSr2iEi/kKRv1LEwxX92DKkUAehXpk/img.png" alt="" width="375"><figcaption></figcaption></figure>

wc는 파일의 line 수, 단어 수, 바이트를 세는 프로그램이다.

&#x20;
