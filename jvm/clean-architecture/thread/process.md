# Thread

Process는 실행 중인 프로그램이다.

Thread가 둘 이상인 프로그램은 멀티 쓰레드 프로세스라고 한다.

CPU가 번갈아 가며 Thread들을 실행하여, 여러개가 존재하는 것 처럼 보이게 해준다.



기본적으로 Thread를 추가하는 방법은 Thread, Runnable을 사용하는 방식이다.

~~extend~~하는 방식은 좋아하지 않기때문에 Thread를추천하지는 않는다



Thread관련 예제는 전부 아래에 있는  프린터 구현을 기반으로 설명할 예정이다.

<img src="../../../.gitbook/assets/file.drawing (1).svg" alt="" class="gitbook-drawing">

모든 작업은 Expect에 적혀있는 순서로 진행 되어야 한다.

## Thread

Thread를 사용하여 위  도안을 구현해보도록 한다

{% embed url="https://gist.github.com/Junnyjun/5a5fe3f5846266b8af50ecc4735cf238" %}
Thread
{% endembed %}

하지만 결과는 blokcing 되어 1 2 3 4 5 가 순서대로 실행되지 않았고.\
추가적인 작업이  프린트에추가가 되지 않았다

{% code title="1-2-4-3-5" %}
```basic
1. Printer Run
2. Searching Printer ... 1
Printer Status ... RUNNABLE
Printer Status ... 0
PRINTER NUMBER ::Thread-0 PRINTING :: A
PRINTER NUMBER ::Thread-0 PRINTING :: B
PRINTER NUMBER ::Thread-0 PRINTING :: C
PRINTER NUMBER ::Thread-0 PRINTING :: D
Printer Size ... 0
4. Printer is Empty ...
3. Printer Add more
5. Printer Stop
PRINTER NUMBER ::Thread-0 PRINTING :: E
PRINTER NUMBER ::Thread-0 PRINTING :: F
```
{% endcode %}

Run()은 Thread의 작업을 기다리고 있다는  것을 알 수 있다. (= 싱글 스레드로 동작한다 )



Main에 `scheduler.run(); =>  scheduler.start();` 로 바꾼뒤 다시 실행해준다

{% code title="1-2-3-5-4 " %}
```basic
1. Printer Run
2. Searching Printer ... 1
3. Printer Add more
5. Printer Stop
PRINTER NUMBER ::Thread-1 PRINTING :: A
PRINTER NUMBER ::Thread-1 PRINTING :: B
PRINTER NUMBER ::Thread-1 PRINTING :: C
Printer Status ... RUNNABLE
Printer Size ... 1
PRINTER NUMBER ::Thread-1 PRINTING :: 1
PRINTER NUMBER ::Thread-1 PRINTING :: 2
PRINTER NUMBER ::Thread-1 PRINTING :: 3
Printer Status ... RUNNABLE
Printer Size ... 0
4. Printer is Empty ...
```
{% endcode %}

추가한 작업도 처리되었지만 프린터가 돌고있는데 5(printer stop)이 나온것을 알 수있다.

### STATE

모든 작업(Thread)는 상태(state)를 가지고 있다.\
`Thread.currentThread().getState()` 로 현재 구동중인 ( 작업중인 ) Thread의 상태를 가져 올 수있다.

| TYPE         | DESCRIPTION            |
| ------------ | ---------------------- |
| NEW          | Thread가 생성된 후, 작업 대기상태 |
| RUNNABLE     | 작업 진행중인 상태             |
| BLOCKED      | 락이되어 있는 상태             |
| WAITING      | 다른 스레드의통지를기다리는 상태      |
| TIME\_WATING | 시간 만큼을 대기              |
| TERMINATED   | 작을 마친 상태               |

이것을 이용하여 프린트 스케줄러가 종료 되었는지를 확인한다.\


#### How do code ?

`PrintScheduler`에 작업을 확인할 수 있도록 Thread의 상태를 받아올 수 있도록 추가\
`Main`에 상태를 확인한 후 Main을 종료할 수 있도록 추가

{% embed url="https://gist.github.com/Junnyjun/edffebb8b8fb5d5bda93135c0c7a199d" %}

#### Result

```basic
1. Printer Run
2. Searching Printer ... 1
3. Printer Add more
PRINTER NUMBER ::Thread-1 PRINTING :: A
PRINTER NUMBER ::Thread-1 PRINTING :: B
PRINTER NUMBER ::Thread-1 PRINTING :: C
Printer Status ... RUNNABLE
Printer Size ... 1
PRINTER NUMBER ::Thread-1 PRINTING :: 1
PRINTER NUMBER ::Thread-1 PRINTING :: 2
PRINTER NUMBER ::Thread-1 PRINTING :: 3
Printer Status ... RUNNABLE
Printer Size ... 0
4. Printer is Empty ...
5. Printer Stop
```

정상적으로 Printer가 종료되었음을 알 수있다.



[View With Git](https://github.com/I-JUNNYLAND-I/KATA/tree/main/fiber/src/main/java)

