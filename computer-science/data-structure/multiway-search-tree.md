# Multiway search tree

다원 탐색 트리(Multiway search tree)는 m-원 탐색 트리 (m-way search tree)라고도 합니다.\
다원 탐색 트리는 한 노드안에 최대 m-1개의 요소와 m개의 자식을 가질 수 있습니다.

<img src="../../.gitbook/assets/file.excalidraw (4) (2).svg" alt="" class="gitbook-drawing">

Key는 오름차순으로 정렬되어 있습니다\
다원 트리의 모든 서브 트리는 m-원 탐색 트리입니다.

트리의 높이를 h라 하면 트리의 작업 속도는 O(h)입니다.\
h 높이를 낮춤으로써 작업 속도를 높일 수 있습니다. \
노드에 저장할 수 있는 요소의 수를 늘림으로써 트리의 높이를 줄일 수 있습니다.

<img src="../../.gitbook/assets/file.excalidraw (5) (1).svg" alt="" class="gitbook-drawing">

다원 검색 트리는 이진 검색 트리보다 많은 요소를 저장할 수 있어 낮은 높이를 유지할 수 있습니다.

대표적인 활용 예시로 b-tree가 있습니다
