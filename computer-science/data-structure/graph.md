# Graph

그래프(Graph)는 정점(Vertex)의 집합 V와 간선(Edge)의 집합 E로 구성된 비선형 데이터 구조

`정점(Vertex)` : 자료를 저장하려는 자료의 단위, 노드(Node)라고도 말함.

`간선(Edge)` : 정점 사이를 연결하는 선으로 두 정점 쌍 (u, v)로 표현함.

<img src="../../.gitbook/assets/file.excalidraw (4) (3).svg" alt="" class="gitbook-drawing">

## GRAPH

### EDGE

#### 방향성(유향)

방향을 가진 정점의 쌍 (u, v)으로 화살표로 표현하고 단방향을 가리킵니다.

첫 번째 정점 u는 출발점을 의미하고 두 번째 정점 v는 도착점을 의미합니다.

방향성 간선을 가진 그래프를 방향성 그래프(Directed Graph)라고 합니다.

#### 무방향성(무향)

방향이 없는 정점의 쌍 (u, v)으로 직선으로 표현한다.

무방향성 간선 (u, v)와 (v, u)는 같다. (양방향을 가리킴)

무방향성 간선을 가진 그래프를 무방향성 그래프(Undirected Graph)라고 합니다.

<img src="../../.gitbook/assets/file.excalidraw (6).svg" alt="" class="gitbook-drawing">

### **adjacent & incident**

#### 인접 (adjacent)

무방향성 그래프에서 정점 u, v에 대하여 간선(u, v)이 있으면 정점 u는 정점 v에 인접(adjacent) 하다고 한다. (역도 성립)

방향성 그래프에서 정점 u, v에 대하여 간선(u,v)이 있으면 정점 v는 정점 u에 인접한다고 한다. (역은 성립하지 않음.)

부속 (incident)

무방향 그래프에서 정점 u,v에 대하여 간선(u, v)이 있으면 간선(u, v)은 정점 u와 v에 부속(incident)한다고 합니다.

<img src="../../.gitbook/assets/file.excalidraw (11).svg" alt="" class="gitbook-drawing">

### **Self-loop & isolated**

#### self-loop

한 간선이 같은 정점에 부속해 있을 때 self-loop라고 합니다.\
`e = (u, u)`

#### isolated

간선이 없는 경우를 말하며 간선이 없는 정점을 isolated vertex라고 합니다.
