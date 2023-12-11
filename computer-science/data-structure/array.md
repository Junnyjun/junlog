# Array

메모리 공간에 순차적으로 저장된 데이터\
대부분 프로그래밍 언어에서는 동일 타입의 데이터를 저장합니다.\
배열을 구성하는 각각의 값을 `요소`라고 하며, 위치는 `인덱스`라고 한다

<img src="../../.gitbook/assets/file.drawing (3) (1).svg" alt="" class="gitbook-drawing">

Memory에`FFFF00 FFFF04 FFFF08 ...` 식으로 해당 타입의 Byte크기 만큼 순차적으로 저장된다

### feature

(대부분의 프로그래밍 언어에서) 동등한  데이터 유형을 가진다. \
Index는 0 부터 시작한다.\
요소에 접근하는 시간은 O(1)이다\
Indexing(요소 읽기), Slicing(분리)을 지원한다



배열 선언 후 할당된 정적 메모리 크기를 변경할 수 없다.\
데이터 삽입, 삭제의 오버헤드가 큰편이다.

{% embed url="https://gist.github.com/Junnyjun/11ec846e11780a788859277ff07aff60" %}

* JAVA의 Array는 크기가 모자르면 기존크기의 두배만큼을 할당해준다.
