# Stack

한쪽 끝에서만 자료를 넣거나 뺄 수 있는 선형 구조로 되어있다. ( LIFO )\
스택이 가득 차 있을땐 `OverFlow` 상태이며, 스택이 비어 있을 땐 `UnderFlow` 상태입니다

<img src="../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

Push는 데이터를 넣는 작업입니다.\
&#x20;1\. 스택이 Overflow인지 확인.\
&#x20;2\. 스택이 가득 차지 않았으면 Top(마지막 데이터)을 증가 시킨 후 데이터를 추가

Pop은 데이터를 제거하는 작업입니다.\
&#x20;1\. 스택이 비어있는지 확인\
&#x20;2\. 비어있지 않으면 Top을 제거한 후 Top을 감소
