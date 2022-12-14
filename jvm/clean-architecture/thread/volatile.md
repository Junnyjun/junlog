# Volatile

Volatile를 설명하기 전, 컴퓨의 작동 방식 부터 설명후 진행합니다.



CPU와 MEMORY는 데이터를 주고 받습니다.\
CPU는 많은 명령을 수행하기 때문에 RAM에서 가져오지 않고 \
[Out-of-order execution, ](https://en.wikipedia.org/wiki/Out-of-order\_execution)[Branch predictor, ](https://en.wikipedia.org/wiki/Branch\_predictor)[Speculative execution,](https://en.wikipedia.org/wiki/Speculative\_execution) [Caching](https://en.wikipedia.org/wiki/CPU\_cache)을  이용합니다

Cpu와 Memory가 데이터를 주고 받는 메모리 구조부터 확인합니다

<img src="../../../.gitbook/assets/file.drawing (1).svg" alt="Cpu&#x26;Memory" class="gitbook-drawing">

여러 로컬 캐시에 저장되는 공유 리소스 데이터의 균일성을 일관되게 동작하게 해야 합니다.\
프로그램이  모리 캐시를 관리하는 경우 일관성 없는 데이터에 문제가 발생할 수 있으며, 다중 처리 CPU에서는 더욱 두드러집니다.

<img src="../../../.gitbook/assets/file.drawing (2).svg" alt="" class="gitbook-drawing">

위 그림처럼 Counter
