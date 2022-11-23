# Runnable

Runnable은 기존에 extend된 Thread를 Runnable로 바꿔주기만 하면된다.



### How do code ?

{% embed url="https://gist.github.com/Junnyjun/06d7097ad73b7b7d4bbea6815f053bd5" %}
Runnable Thread
{% endembed %}

```basic
1. Printer Run
2. Searching Printer ... 1
Printer Status ... RUNNABLE
Printer Size ... 1
PRINTER NAME ::Printer :: 25
3. Printer Add more
PRINTING :: A
PRINTING :: B
PRINTING :: C
COMPLETE
Printer Status ... RUNNABLE
Printer Size ... 1
PRINTER NAME ::Printer :: 30
PRINTING :: 1
PRINTING :: 2
PRINTING :: 3
COMPLETE
4. Printer is Empty ...
5. Printer Stop
```

완성 하고 보니 1-2-3-4-5 순서는 맞는데 Thread.sleep()이 굉장히 많고,\
콘솔이 중구난방으로 찍혀있는 모습을 볼수있다



### Priority

스레드는 각각 우선순위를 갖는다. \
이 우선순위는 쓰레드의 실행시간과 비례하게된다.

<img src="../../../.gitbook/assets/file.drawing.svg" alt="Thread" class="gitbook-drawing">

이를 적용하여 sleep을 제거해 보도록 한다.

{% embed url="https://gist.github.com/Junnyjun/5f4047d76bcb1a16d049f06d2e075e99" %}

#### result

```basic
1. Printer Run
2. Searching Printer ... 
3. Printer Add more
Printer Status ... RUNNABLE
Printer Size ... 2
4. Printer is Empty ...
5. Printer Stop
PRINTER NAME ::Printer :: 25
PRINTER NAME ::Printer :: 24
PRINTING :: 1
PRINTING :: 2
PRINTING :: 3
PRINTING :: A
PRINTING :: B
PRINTING :: C
COMPLETE
COMPLETE
```
