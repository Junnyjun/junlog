# DMA Controller

CPU와 Memory는 서로 데이터를 주고 받습니다.\
이때 메모리가 데이터를 CPU에게 전달하는 과정에서 CPU의 interrupt가 발생하는데,\
매번 데이터를 전달할때 마다 CPU의 작업이 멈추면 전체적인 프로세스가 지연되게 됩니다.

이걸 방지하기 위해 CPU에 데이터가 전달 될 때, 전달될 데이터를 모아두는 일종의 Buffer가\
필요한데 이것을 DMA라 부릅니다.

<img src="../../.gitbook/assets/file.excalidraw (34) (1).svg" alt="" class="gitbook-drawing">

## 종류

