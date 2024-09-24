# Kafka?

**Apache Kafka**는 **분산형 스트리밍 플랫폼**으로, 대규모 데이터를 실시간으로 처리하는 데 최적화된 메시지 브로커 시스템입니다. Kafka는 **데이터 스트리밍**, **데이터 파이프라인**, **이벤트 소싱** 및 **분산 로그 저장소** 등 다양한 용도로 사용되며, 주로 실시간 데이터 처리와 관련된 환경에서 많이 활용됩니다. Kafka의 작동 원리와 구동 방식을 이해하면, 왜 Kafka가 대규모 실시간 데이터 처리에 최적화되어 있는지 알 수 있습니다.

<img src="../../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">

## Data Replication

Kafka는 크게 **브로커(broker)**, **프로듀서(producer)**, **컨슈머(consumer)**, **토픽(topic)**, **파티션(partition)**, 그리고 **리플리케이션(replication)** 등으로 구성됩니다.

```
PLAINTEXT://:9092: 클러스터 내부에서 사용하는 기본적인 Kafka 통신을 위한 포트입니다. 
인증 없이 데이터를 주고받습니다.

CONTROLLER://:9093: 컨트롤러 역할을 수행하는 노드에서 사용하는 포트로, 
Kafka 컨트롤러와 브로커 간 통신에 사용됩니다.

EXTERNAL://:9094: 외부에서 Kafka 브로커에 접속할 수 있도록 설정한 포트입니다. 
외부 클라이언트가 이 포트로 Kafka에 접근할 수 있습니다.
```

