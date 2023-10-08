# AVL Tree

AVL 트리는 스스로 균형을 잡는 이진 탐색 트리입니다.&#x20;

트리의 높이가 h일 때 이진 탐색 트리의 시간 복잡도는 O(h)입니다.\
한쪽으로 치우친 편향 이진트리가 되면 트리의 높이가 높아지기 때문에, 높이 균형을 유지하는 AVL 트리를 사용하게 됩니다.

```
AVL 트리는 이진 탐색 트리의 속성을 가집니다.
왼쪽, 오른쪽 서브 트리의 높이 차이가 최대 1 입니다.
어떤 시점에서 높이 차이가 1보다 커지면 회전(rotation)을 통해 균형을 잡아 높이 차이를 줄입니다.
AVL 트리는 높이를 logN으로 유지하기 때문에 삽입, 검색, 삭제의 시간 복잡도는 O(logN) 입니다.
```

### Balance Factor(BF) <a href="#balance_factor-bf" id="balance_factor-bf"></a>

Balance Factor(BF)는 외쪽 서브트리의 높이에서 오른쪽 서브트리의 높이를 뺀 값입니다.

> Balance Factor (k) = height (left(k)) - height(right(k))

```
BF가 1이면 왼쪽 서브트리가 오른쪽 서브트리보다 높이가 한단계 높다는 것을 의미합니다.
BF가 0이면 왼쪽 서브트리와 오른쪽 서브트리의 높이가 같다는 것을 의미합니다.
BF가 -1이면 왼쪽 서브트리가 오른쪽 서브트리보다 높이가 한단계 낮다는 것을 의미합니다.
```

<img src="../../.gitbook/assets/file.excalidraw (2) (1) (2).svg" alt="" class="gitbook-drawing">

### Rotation

AVL트리는 이진 탐색 트리이기 때문에 모든 작업은 이진 탐색 트리에서 사용하는 방식으로 수행됩니다.\
검색 및 순회 연산은 BF를 변경하지 않지만 삽입 및 삭제에서는 BF가 변경될 수 있습니다.

삽입 삭제 시 불균형 상태(BF가 -1 ,0, 1이 아닌 경우) 가 되면 AVL트리는 불균형 노드를 기준으로 서브트리의 위치를 변경하는 rotation 작업을 수행하여 트리의 균형을 맞추게 됩니다.

삽입 삭제시 노드들의 배열에 따라 4가지(LL, RR, LR, RL) 불균형이 발생할 수 있으며 각 상황마다 rotation에 방향을 달리하여 트리의 균형을 맞춥니다.

<img src="../../.gitbook/assets/file.excalidraw (1) (1) (1) (3).svg" alt="" class="gitbook-drawing">
