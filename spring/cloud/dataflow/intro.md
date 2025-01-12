# Intro

### 개요

* **Spring Cloud Data Flow**: 마이크로서비스 아키텍처 기반으로, **데이터 파이프라인(스트림 & 배치)을 손쉽게 구성**하고 운영할 수 있게 해주는 플랫폼.
* **주요 장점**
  * Spring Cloud Stream, Spring Cloud Task 등을 활용해 스트리밍 처리와 배치 처리 통합
  * REST API, 웹 UI, CLI(Shell) 등 다양한 방식으로 파이프라인 관리
  * 데이터 소스(Source) → 프로세서(Processor) → 싱크(Sink) 형태의 스트림 파이프라인을 시각적으로 관리
  * 스케일 아웃 구조로 확장성 용이
* **구성 요소**
  * **Data Flow Server**: 스트림·태스크 정의/배포/모니터링의 중심
  * **Skipper**: 버전 관리, 롤링 업데이트 및 롤백 등 스트림 운영 전반 지원
  * **Shell**: CLI 환경에서 서버와 통신하며 스트림·태스크 정의, 배포, 조회 등 수행

***

### SCDF 로컬 설치 및 실행

#### Docker Compose로 빠르게 세팅

아래 예시 `docker-compose.yml` 파일을 준비하고 `docker-compose up -d`로 컨테이너를 띄운다.

```yaml
version: '3.7'
services:
  dataflow-server:
    image: springcloud/spring-cloud-dataflow-server:latest
    container_name: dataflow-server
    ports:
      - 9393:9393
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - MAVEN_REMOTE_REPOSITORIES_RELEASE_ENABLED=true
      - MAVEN_REMOTE_REPOSITORIES_SNAPSHOTS_ENABLED=true
    depends_on:
      - skipper
      - mysql
    links:
      - skipper
      - mysql

  skipper:
    image: springcloud/spring-cloud-skipper-server:latest
    container_name: skipper
    ports:
      - 7577:7577
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - mysql
    links:
      - mysql

  mysql:
    image: mysql:5.7
    container_name: scdf-mysql
    environment:
      - MYSQL_DATABASE=dataflow
      - MYSQL_USER=dataflow
      - MYSQL_PASSWORD=dataflow
      - MYSQL_ROOT_PASSWORD=root
    ports:
      - 3306:3306

  rabbitmq:
    image: rabbitmq:3.8-management
    container_name: scdf-rabbit
    ports:
      - 5672:5672
      - 15672:15672
```

* **구성**
  * Data Flow Server, Skipper, MySQL, RabbitMQ 컨테이너가 함께 실행
  * `dataflow` DB와 `dataflow` 유저를 사용
*   **실행 명령어**

    ```bash
    docker-compose up -d
    ```
* 정상 구동 후 브라우저에서 `http://localhost:9393/dashboard` 접속하면 SCDF 웹 UI 확인 가능

#### Shell 사용

* Shell은 CLI로 스트림·태스크 정의와 배포를 돕는다.
*   다운로드 후 실행 예시:

    ```bash
    wget https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/<버전>/spring-cloud-dataflow-shell-<버전>.jar
    java -jar spring-cloud-dataflow-shell-<버전>.jar
    ```
*   SCDF 서버에 접속:

    ```bash
    dataflow:>dataflow config server --uri http://localhost:9393
    ```
* 이후 `stream list`, `task list` 등 명령어로 관리 가능

***

### 스트림 파이프라인 구성 예시

#### 애플리케이션 등록

* 미리 빌드된 Spring Cloud Stream 애플리케이션(소스, 프로세서, 싱크)을 등록해야 한다.
*   예: **HTTP 소스**와 **Log 싱크** 등록

    ```bash
    dataflow:>app register --name http-source --type source \
      --uri docker://springcloudstream/http-source-rabbit:3.2.2
    dataflow:>app register --name log-sink --type sink \
      --uri docker://springcloudstream/log-sink-rabbit:3.2.2
    ```
* `source`, `sink`, `processor` 등 타입에 맞춰 등록

#### 스트림 정의 & 배포

*   **스트림 정의** (소스 → 싱크 간 단순 파이프라인)

    ```bash
    dataflow:>stream create --name simpleStream \
      --definition "http-source | log-sink"
    ```
*   **배포**

    ```bash
    dataflow:>stream deploy simpleStream
    ```
* 배포 완료 후 `http://localhost:9393/dashboard`에서 파이프라인 상태 확인 가능

#### 스트림 테스트

*   `http-source`는 **HTTP POST**로 데이터 수신

    ```bash
    curl -X POST -H "Content-Type: text/plain" \
      -d "Hello SCDF" \
      http://<http-source-컨테이너-IP>:8080
    ```
* `log-sink` 컨테이너 로그를 확인하면 `"Hello SCDF"` 메시지 확인 가능

***

### 배치(태스크) 구성 & 운영

#### 태스크 등록

*   Spring Batch 애플리케이션을 Docker 이미지로 만들어 **태스크**로 등록

    ```bash
    dataflow:>app register --name simple-batch --type task \
      --uri docker://<이미지주소>/simple-batch-task:latest
    ```
* `--type task`가 핵심

#### 태스크 정의 & 실행

*   정의:

    ```bash
    dataflow:>task create simpleBatchTask --definition "simple-batch"
    ```
*   실행:

    ```bash
    dataflow:>task launch simpleBatchTask
    ```
* 실행 결과(성공/실패, 로그)는 UI 또는 Shell로 확인 가능

#### 스케줄링(선택)

* SCDF는 Kubernetes나 Cloud Foundry 등과 연동해 태스크 스케줄링 가능
* 로컬 Docker 환경에선 제한적일 수 있으므로, 운영 환경에서 고려해보자

#### 운영 모니터링

* **Data Flow UI**로 스트림·배치 작업 상태 일괄 관리
* 각 컨테이너(Pod)의 로그 및 메트릭을 조회해 실시간 모니터링
* Prometheus/Grafana 연동 시 성능·상태를 시각화해 운영 효율 상승

1.
