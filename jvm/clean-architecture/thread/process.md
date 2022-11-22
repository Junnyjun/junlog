# Thread

Process는 실행 중인 프로그램이다.

Thread가 둘 이상인 프로그램은 멀티 쓰레드 프로세스라고 한다.

CPU가 번갈아 가며 Thread들을 실행하여, 여러개가 존재하는 것 처럼 보이게 해준다.



기본적으로 Thread를 추가하는 방법은 Thread, Runnable을 사용하는 방식이다.

여기서 필자는 ~~extend~~하는 방식은 좋아하지 않기때문에 추천하지는 않는다



Thread관련 예제는 전부 아래에 있는  프린터 구현을 기반으로 설명할 예정이다.

<img src="../../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

모든 작업은 Expect에 적혀있는 순서로 진행 되어야 한다.

## Thread

Thread를 사용하여 Main -> PrintScheduler -> print ... -> Main -> PrintScheduler -> Main\
를 구현해보도록 한다다

### Run ?

{% embed url="https://gist.github.com/Junnyjun/5a5fe3f5846266b8af50ecc4735cf238" %}
Thread
{% endembed %}

하지만 결과는 blokcing 되어 1 2 3 4 5 가 순서대 실행되지 않았고.\
추가적인 프린트에 값 추가가 되지 않았다

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

Run&#x20;

