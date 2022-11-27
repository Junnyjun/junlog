# Syncronize

둘 이상의 스레드가 하나의 공유자원을 write할 때(경합상태) 발생하는 문제를 해결하는 방식이다.

Syncronize로 되어있는 부분은 한번에  하나의 스레드만 접근을 허용합니다.

<img src="../../../.gitbook/assets/file.drawing (1) (3).svg" alt="" class="gitbook-drawing">

### How do code?

예제는 극적으로 연출하기 위해 여러개를 실행 했습니다.

{% embed url="https://gist.github.com/Junnyjun/8526a0beb0b8934e2c19a23f0922f992" %}

balance >= money과 acc.getBalance() > 0 라는 출금 방지를 이중으로 막은 조건문이 있음에도 결과는 -1300이 된것을 볼 수있다.



### With Syncronized

{% embed url="https://gist.github.com/Junnyjun/b58d2d4fe029c5a5d9f673f3d6151f35" %}

Syncronized를 추가하니 withdraw에  실행에는  단하나의 쓰레드만 접근 할 수 있는것을 확인할 수 있습니다.



### Precaution

<img src="../../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

멀티 프로세스 두 가지 이상의 작업이 서로 작업이 끝나기만을 기다리는 상태가 발생할 수 있습니다.

서로가 사용할 수 있는 공유 자원을 임계 영역이라고 합니다.

<img src="../../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

