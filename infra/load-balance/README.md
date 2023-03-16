# 🔀 Load Balance

Load Balancer는 여러 대의 서버에 대한 트래픽을 분산하는 기능을 수행하는 장치나 소프트웨어입니다. 이 기능을 통해 네트워크 트래픽을 효율적으로 분산시켜 서버의 부하를 분산시키고, 서버의 가용성을 향상시킬 수 있습니다.

### 기능

Load Balancer는 여러 대의 서버에 대한 트래픽을 분산시키는 역할을 합니다. \
이를 통해 서버의 부하를 분산시키고, 서버의 가용성을 향상시킬 수 있습니다.

또한, Load Balancer는 다음과 같은 기능을 수행합니다.

1. Health Check: Load Balancer는 서버의 상태를 정기적으로 체크하여 정상 작동 여부를 판단합니다.
2. SSL Termination: Load Balancer는 SSL 암호화된 요청을 받아서, SSL 연결을 해제하고, 일반 HTTP 요청으로 변환하여 백엔드 서버에 전달합니다.
3. Session Persistence: Load Balancer는 클라이언트의 요청이 특정 서버에 유지되도록 설정할 수 있습니다. 이를 통해 클라이언트의 세션이 유지되면서 서버의 부하를 분산시킬 수 있습니다.
4. Traffic Shaping: Load Balancer는 트래픽의 우선순위를 설정하여 우선순위가 높은 트래픽을 먼저 처리할 수 있도록 설정할 수 있습니다.

### 종류

Load Balancer는 하드웨어와 소프트웨어로 나눌 수 있습니다.

1. 하드웨어 Load Balancer: Load Balancer가 하드웨어 장비로 제공되며, 대부분 고성능 및 대용량 처리가 가능합니다.
2. 소프트웨어 Load Balancer: Load Balancer가 소프트웨어로 제공되며, 가상화 기술을 이용하여 가상화된 환경에서 동작합니다.

또한, Load Balancer는 Layer 4와 Layer 7로 나눌 수 있습니다.

1. Layer 4 Load Balancer: IP 주소와 포트 번호를 기반으로 트래픽을 분산시키는 방식입니다.
2. Layer 7 Load Balancer: HTTP 헤더 및 URL을 기반으로 트래픽을 분산시키는 방식입니다.

Load Balancer의 장점은 다음과 같습니다.

1. 서버의 가용성 향상: Load Balancer는 여러 대의 서버에 대한 트래픽을 분산시켜 서버의 부하를 분산시키고, 서버의 가용성을 향상시킵니다.
2. 성능 향상: Load Balancer는 트래픽을 분산시켜 서버의 부하를 분산시키므로, 서버의 성능을 향상시킵니다.
3. 스케일 아웃: Load Balancer를 이용하여 서버의 수를 증가시키면, 서비스 용량을 증대시킬 수 있습니다.

Load Balancer의 단점은 다음과 같습니다.

1. 추가 비용: Load Balancer를 사용하면, 하드웨어나 소프트웨어 등의 추가 비용이 발생합니다.
2. 단일 장애점(Single point of failure): Load Balancer는 하나의 장비로 구성되므로, 장애가 발생하면 전체 서비스에 영향을 미칩니다.

Load Balancer는 서버의 가용성을 향상시키고, 트래픽을 효율적으로 분산시켜 서버의 부하를 분산시킨다는 장점이 있습니다. 하지만, 추가 비용 및 단일 장애점 등의 단점도 있으므로 적절하게 사용해야 합니다.
