# 분산 시스템

분산 시스템은 네트워크로 연결된 여러 대의 독립 컴퓨터(노드)가 서로 긴밀히 협력하며 하나의 일관된 시스템처럼 동작하도록 설계된 구조입니다. 운영체제 차원에서 분산 시스템을 지원하기 위해서는 **통신**, **자원 공유**, **일관성 유지**, **장애 허용(Fault Tolerance)**, **투명성(Transparency)** 등의 복합적 과제를 해결해야 합니다.

***

#### **분산 시스템의 핵심 특성**

* **투명성(Transparency)**
  * **접근 투명성**: 로컬과 원격 자원을 동일하게 접근
  * **위치 투명성**: 객체의 물리적 위치를 숨김
  * **복제 투명성**: 여러 복제본 중 하나로 자동 연결
  * **병행성 투명성**: 동시 접근 제어를 숨김
  * **장애 투명성**: 장애 발생을 사용자가 인지하지 않음
* **자율성(Autonomy)**
  * 각 노드는 독립적이며 자체 관리 기능을 가짐
* **이질성(Heterogeneity)**
  * 서로 다른 하드웨어, OS, 네트워크 프로토콜이 공존
* **동시성(Concurrency)**
  * 여러 프로세스가 동시에 분산 자원에 접근
* **확장성(Scalability)**
  * 노드를 추가해 성능·용량을 수평 확장

***

#### **통신 모델 및 미들웨어**

* **메시지 패싱(Message Passing)**
  * 명시적 send/receive 호출로 데이터를 주고받음
  * 동기식(Synchronous) vs 비동기식(Asynchronous) 통신
* **원격 프로시저 호출(Remote Procedure Call, RPC)**
  * 로컬 함수 호출처럼 보이지만 네트워크로 호출
  * 스텁(Stub) 생성, 매개변수 직렬화(Serialization)·역직렬화
* **데이터 직렬화(Serialization)**
  * JSON, XML, Protocol Buffers 등 포맷 활용
  * 플랫폼·언어 독립성 보장
* **미들웨어(Middleware)**
  * **애플리케이션 서버**(EJB, Spring Remoting)
  * **메시지 큐(MQ)**: RabbitMQ, Kafka
  * **서비스 디스커버리**: ZooKeeper, Consul

***

#### **분산 자원 관리**

* **분산 파일 시스템(Distributed File System)**
  * Google File System(GFS), Hadoop Distributed File System(HDFS)
  * **메타데이터 서버**: 파일-블록 매핑 관리
  * **데이터 노드**: 블록 저장 및 재복제
* **분산 공유 메모리(DSM)**
  * 메모리 페이지 일관성 유지 프로토콜(MSI, MESI 변형)
  * **페이지 그레뉼러리티**와 **통신 오버헤드** 균형
* **분산 트랜잭션**
  * Two-Phase Commit(2PC), Three-Phase Commit(3PC)
  * 원자성·일관성·고립성·지속성(ACID) 제공

***

#### **데이터 일관성과 CAP 정리**

* **CAP 정리**
  * **C(Consistency)**: 모든 클라이언트가 같은 시점의 데이터를 봄
  * **A(Availability)**: 모든 요청에 대한 응답 보장
  * **P(Partition tolerance)**: 네트워크 분할 시에도 계속 동작
  * 실제 시스템은 CP, AP, CA를 조합해 트레이드오프
* **강력 일관성(Strong Consistency)** vs **최종 일관성(Eventual Consistency)**
  * ZooKeeper, etcd는 강력 일관성 제공
  * DynamoDB, Cassandra는 최종 일관성 모델

***

#### **장애 허용성(Fault Tolerance) 및 복제**

* **복제(Replication)**
  * **동기 복제**: 쓰기 요청 시 모든 복제본에 쓰여야 성공
  * **비동기 복제**: 메인 복제본만 쓰기, 나머지는 나중에 복제
* **리더-팔로워(Leader-Follower) 아키텍처**
  * 리더(Primary) 선출, 팔로워복제, 리더 장애 시 자동 재선출
* **재시도(Retry)·백오프(Backoff)** 메커니즘
  * 네트워크 오류 시 지수적 백오프로 재시도

***

#### **시간 동기화**

* **논리적 클럭(Logical Clocks)**
  * Lamport Clock, Vector Clock을 통해 사건 순서 결정
* **물리적 시간**
  * NTP(Network Time Protocol), PTP(Precision Time Protocol)로 클록 싱크

***

#### **보안 및 신뢰성**

* **인증·인가** 분산 환경 확장
  * Kerberos, OAuth 2.0, JWT(JSON Web Token)
* **TLS/SSL**: 노드 간 데이터 전송 암호화
* **침입 탐지**: 클러스터 모니터링 에이전트
