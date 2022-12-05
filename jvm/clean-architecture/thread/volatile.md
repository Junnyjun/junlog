# Volatile

Volatile를 설명하기 전, 컴퓨의 작동 방식 부터 설명후 진행합니다.



CPU와 MEMORY는 데이터를 주고 받습니다.\
CPU는 많은 명령을 수행하기 때문에 RAM에서 가져오지 않고 \
[Out-of-order execution, ](https://en.wikipedia.org/wiki/Out-of-order\_execution)[Branch predictor, ](https://en.wikipedia.org/wiki/Branch\_predictor)[Speculative execution,](https://en.wikipedia.org/wiki/Speculative\_execution) [Caching](https://en.wikipedia.org/wiki/CPU\_cache)을  이용합니다

Cpu와 Memory가 데이터를 주고 받는 메모리 구조부터 확인합니다

<img src="../../../.gitbook/assets/file.drawing (4).svg" alt="" class="gitbook-drawing">
