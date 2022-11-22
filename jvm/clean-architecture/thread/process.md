# Process

Process는 실행 중인 프로그램이다.

Thread가 둘 이상인 프로그램은 멀티 쓰레드 프로세스라고 한다.



Thread관련 예제는 전부 이와 같은 Printer를 기반으로 한다

<img src="../../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

모든 작업은 Expect로 진행 되어야 한다.



## Thread

Thread를 run해서 Main -> PrintScheduler -> print ... -> Main -> PrintScheduler -> Main\
을 실행 하려고 작성한 예제이다.

{% embed url="https://gist.github.com/Junnyjun/5a5fe3f5846266b8af50ecc4735cf238" %}
Thread
{% endembed %}
