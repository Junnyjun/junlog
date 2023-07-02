# Service discovery

on-premise 서버 기반의 Monolithic Architecture의 문제점을 보완하기 위해 클라우드 환경을 이용하여 서버를 구성하는 Micro Service Architecture(MSA)가 떠오르고 있습니다.

\
MSA와 같은 분산 환경에서의 동작은 서비스 간의 원격 호출(API 호출)로 구성되며, 원격 호출은 각 서비스의 ip 주소와 port를 기반으로 요청되는데, 클라우드 환경이 기반이 되면서 서비스가 AutoScaling 등에 의해 동적으로 생성되거나, 컨테이너 기반의 배포로 인해서 서비스의 ip가 동적으로 변경되는 일이 잦아졌습니다.

그러나 이러한 변경은 클라우드에서 일어난 것이기 때문에 동적으로 변하는 ip를 하나하나 확인하여 수동으로 대응할 수는 없는 단점이있다.

때문에 클라우드 환경에서는 서비스 클라이언트가 서비스를 호출할 때, 서비스의 위치(ip 주소와 port)를 알아낼 수 있는 기능이 필요한데, 이것을 바로 Service Discovery라고 하며, 서비스 디스커버리를 구현하는 방법으로는 크게 Client Side Discovery 방식과 Server Side Discovery 방식이 있습니다.

&#x20;

Service Discovery의 기본적인 기능은 서비스를 등록하고 등록된 서비스의 목록을 반환하는 기능이지만, 등록된 서비스들의 Health check를 통해 현재 서비스가 가능한 서비스의 목록만 리턴한다거나, 서비스 간의 부하 분산 비율을 조정하는 등의 기능을 추가할 수 있습니다.

## Client Side Discovery

<img src="../../../.gitbook/assets/file.excalidraw (3).svg" alt="" class="gitbook-drawing">

service client가 service registry에 query를 통해 서비스의 위치를 물어보고 호출하는 방식이며, 호출 시에는 로드밸런싱 알고리즘을 통해 서비스가 호출됩니다.

service instance가 생성될 때(구동될 때) ip 주소, port, 서비스명 등이 service registry에 등록되며, service instance가 종료될 때 service registry에서 삭제됩니다.\
_(service instance의 등록 및 삭제는 heartbeat mechanism에 따라서 주기적으로 refresh 됩니다.)_

_E.g )_ Netflix Eureka

## Server Side Discovery

<img src="../../../.gitbook/assets/file.excalidraw (2).svg" alt="" class="gitbook-drawing">

호출되는 서비스 앞에 Load Balancer를 넣는 방식으로 service client가 Load Balancer를 호출하면 Load Balancer가 service register로 서비스의 위치를 물어보고 가용할 수 있는 서비스 인스턴스를 라우팅 합니다.

client side discovery와 마찬가지로 서비스 인스턴스가 생성될 때(구동될 때) service registry에 생성되고 종료될 때 삭제됩니다.
