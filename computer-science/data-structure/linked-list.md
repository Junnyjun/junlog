# Linked List

데이터와 포인트로 구성된 노드간의 연결을 이용하여 리스트를 구현한 자료구조\
배열의 고정 크기의 단점을 보완하기 위해 만들어 졌으며, 연속적 메모리 공간에 저장되어 있지 않아 연결이 필요하다.

<img src="../../.gitbook/assets/file.drawing (6).svg" alt="" class="gitbook-drawing">

Head는 리스트의 시작을 의미한다.\
Next는 다음 노드를 지정해주는 포인터다\
각 노드는 Next로 다음 노드와 연결된다.\
포인터가 Null을 가르킨다면 리스트가 끝났음을 의미한다.

### Feature

Next의 다음 노드의 위치를 지정하여 선형 자료구조의 형태이다.\
노드가 연결되어 있어 데이터 탐색은 순차접근이다\
삽입과 삭제가 배열에 비해 빠르고 쉽다.

| 장점         | 단점            |
| ---------- | ------------- |
| 크기가 가변적 이다 | Index 액세스 불가능 |
| 삽입 삭제 용이   | 포인터 메모리공간 필요  |

### 종류

<img src="../../.gitbook/assets/file.drawing (2) (1) (4).svg" alt="" class="gitbook-drawing">

## How do code?

{% embed url="https://gist.github.com/Junnyjun/0888442a5c050dac738dc2a961b930cf" %}
