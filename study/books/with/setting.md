# SETTING

### 2.1.카프카 관리를 위한 주키퍼

#### 주키퍼란?

* 아파치 산하 프로젝트인 하둡, 나이파이, 에이치베이스, 스톰 등의 많은 애플리케이션이 부하 분산 및 확장이 용이한 분산 애플리케이션으로 개발
* 분산 애플리케이션을 사용하게 되면, 관리를 위한 안정적인 코디네이션 앱이 추가적으로 필요.
* 대용량 분산 처리 애플리케이션인 하둡의 중앙 관리 코디네이션 애플리케이션의 필요로 인해 서브 프로젝트로 개발되어졌음
* 카프카 또한 분산 애플리케이션의 한 종류로, 주키퍼를 코디네이션 로직으로 사용
* 코디네이션 시스템은 한 마디로,\
  부산 애플리케이션이 안정적인 서비스를 할 수 있도록 분산되어 있는 각 애플리케이션의 정보를 중앙에 집중하고 구성 관리, 그룹 관리 네이밍, 동기화 등의 서비스를 제공해줍니다.

#### 주키퍼의 특징

* 여러 주키퍼 서버를 앙상블(클러스터)를 구성
* 분산 애플리케이션이 클라이언트가 되어 주키퍼 서버들과 커넥션을 맺고 정보를 주고 받음
* 상태 정보들은 지노드(znode)라 불리는 곳에 key-value 형태로 저장
  * 지노드는 데이터를 정장하기 위한 공간 이름을 말하며, 일반 컴퓨터의 파일이나 폴더 개념이라고 생각
  * 지노드의 사이즈는 바이트에서 킬로바이트 정도로 아주 작은편
  * 일반적인 리눅스 디렉토리와 동일하게 부모 노드와 자식 노드가 있는 계층 구조주키퍼 계층형 구조(출처: [http://zookeeper.apache.org/doc/current/zookeeperOver.html](http://zookeeper.apache.org/doc/current/zookeeperOver.html))
  *

      <figure><img src="https://blog.kakaocdn.net/dn/zy0qO/btrgofH9YKZ/3hVKZ6EajTLne67O7aDOxk/img.png" alt=""><figcaption></figcaption></figure>

```
- 주키퍼의 지노드는 데이터 변경 등에 대한 유효성 검사를 위해 버전 번호를 관리하게 되며, 변경될 떄 마다 지노드의 버전 번호 증가(버저닝이 된다!)
- 주키퍼에 저장된 데이터를 메모리에 저장되어 처리량이 높고 속도가 빠름
```

#### 주키퍼 앙상블의 특징

* 과반수 이상의 주키퍼 서버가 살아 있어야 운영 가능
  * 총 3대의 주키퍼 서버가 앙상블 일때는 2대 이상 살아 있어야 운영 가능.
  * 총 5대의 주키퍼 서버가 앙상블 일때는 3대 이상 살아 있어야 운영 가능.
* 앙상블로 구성되는 주키퍼 서버가 올라갈 수록 초당 처리량이 증가함.
* 데이터 서버의 코로케이션된 서버에 주키퍼 서버를 설치한다고 했을 때, 서로 다른 랙에 설치된 서버에 설치함으로써 갑작스런 랙 장애 발생시 영향 범위를 낮추는 등의 전략도 필요.

### 2.2. 주키퍼 설치

#### 2.2.1. 주키퍼 다운로드

```
# 자바 설치
  (직접 jdk gzi으로 설치하고 자바 홈을 설정해도 무관)
## centos
yum -y install java-1.8.0-openjdk

## ubuntu
apt-get install openjdk-8-jdk

# 주키퍼 stable 버전 다운로드
## stable 버전 확인(확인 주소: http://apache.mirror.cdnetworks.com/zookeeper/stable/)

## stable 버전 다운로드
## 예: wget http://apache.mirror.cdnetworks.com/zookeeper/stable/apache-zookeeper-3.5.9.tar.gz
wget http://apache.mirror.cdnetworks.com/zookeeper/stable/apache-zookeeper-<version>.tar.gz

# 압축 풀기
tar zxf apache-zookeeper-3.5.9.tar.gz

# 심볼릭 링크 생서
ln -s apache-zookeeper-3.5.9 zookeeper

# 주키퍼는 애플리케이션에서 별도의 데이터 디렉토리 사용. 
## 이 노드에 지노드의 복사본인 스냅과 트랜잭션 로그 저장
## 모든 주키퍼 서버의 id를 다르게 설정 
mkdir -p /data
echo 1 > /data/myid

# 환경 설정 파일 구성
vi ../zookeeper/conf/zoo.cfg

## 설정 본문
## node1, 2, 3의 경우 별도의 DNS 서버를 둬서 처리하거나, 
   /etc/hosts 설정 필요
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/data
clientPort=2181
server.1=node1:2888:3888
server.2=node2:2888:3888
server.3=node3:2888:3888
```

#### 2.2.2. 주키퍼 실행

```
# 실행
../zookeeper/bin/zkServer.sh start

# 중지
../zookeeper/bin/zkServer.sh stop
```

#### systemd 설정

```
# service 파일 변경
vi /etc/systemd/system/zookeeper-server.service

# service 파일 본문
[Unit]
Description=zookeeper-server # 유닛 설명
After=netwrok.target #유닛이 시작되는 순서를 조정. after에 짖어된 유닛이 실행된 이후에 시작

[Service]
Type=forking   # 잘 모르겠음
User=root      # 서비스 프로세스 실행 유저
Group=root     # 서비스 프로세스 실행 그룹
SyslogIdentifier=zookeeper-server       # syslog에서 구분 하기 위한 이름
WorkingDirectory=/usr/local/zookeeper   # 주키퍼 경로
Restart=always  # systemctl 명령어로 인한 중지를 제외하고, 프로세스가 종료된 후 재시작.
RestartSecs=0s  # 이 옵션은 Restart 옵션과 연결되어 몇 오에 실행하지를 정함.
ExecStart=/usr/local/zookeeper/bin/zkServer.sh start # 서비스 시작 명령어 및 스크립트
ExecStop=/usr/local/zookeeper/bin/zkServer.sh stop   # 서비스 중지 명령어 및 스크립트

# service 파일 수정 후 systemd 재시작
systemctl daemon-reload

# systemctl 실행/중지/재시작
systemctl start zookeeper-server.service
systemctl stop zookeeper-server.service
systemctl restart zookeeper-server.service

# 서버 부팅시 서비스 자동 실행
systemctl enable zookeeper-server.service

# 서비스 상태 확인
systemctl status zookeeper-server.service
```

### 2.3.카프카 설치

#### 2.3.1. 카프카 다운로드

```
# 자바 설치
  (직접 jdk gzi으로 설치하고 자바 홈을 설정해도 무관)
## centos
yum -y install java-1.8.0-openjdk

## ubuntu
apt-get install openjdk-8-jdk

# 주키퍼 stable 버전 다운로드
## stable 버전 확인(확인 주소: http://apache.mirror.cdnetworks.com/kafka/)

## stable 버전 다운로드
## 예: wget http://apache.mirror.cdnetworks.com/kafka/2.7.0/
wget http://apache.mirror.cdnetworks.com/kafka/<version>/kafka_<version>.tgz

# 압축 풀기
tar zxf apache-zookeeper-3.5.9.tar.gz

# 심볼릭 링크 생서
ln -s apache-zookeeper-3.5.9 zookeeper
```

#### 2.3.2. 카프카 환경설정

카프카 환경 설정에 필요한 서버별 브로커 아이디, 카프카 저장 디렉토리, 주키퍼 정보 사전정리 필요

#### 카프카 저장 디렉토리

주키퍼 처럼 각기 다른 myid 설정

```
# 카프카 호스트 이름과 브로커 아이디
kafka01       broker.id=1
kafka02       broker.id=2
kafka03       broker.id=3
```

#### 저장 디렉토리 준비

* 데이터를 저장할 디렉토리를 여러 개로 설정 가능.
* 디스크가 여러 개면 디스크의 수 만큼 디렉토리를 설정해줘야 디스크 별로 I/O를 분산.
* 디렉토리는 카프카 브로커가 모두 동일하게 설정

#### 주키퍼 정보

주키퍼 앙상블 서버 리스트를 모두 입력해야 주키퍼에 장애 발생해도 고가용성이 보장됨.

> 주키퍼, 카프카 설정시 통신 테스트 방법\
> nc -v 로 테스트\
> ex) nc -v 8443 192.168.100.200

#### 카프카 환경설정

```
vi ../kafka/config/server.properties

# 주요 설정 항목
## broker.id=<broker-id>
## log.dir=<log-directory>
## zookeeper.connect=<zookeeper-ip:zookeeper-port,zookeeper-ip:zookeeper-port,zookeeper-ip:zookeeper-port....>
```

#### 카프카 실행 방법

`sh ../kafka/bin/kafka-server-start.sh <server.properties-path> &`

> 백그라운드 실행을 위해 &을 붙여주거나 -daemon을 명령어 뒤에 붙여줘야함\
> 카프카도 주키퍼와 동일하게 systemd 등록을 위해 서비스 설정 권장

### 2.4.카프카 상태 확인

#### TCP 포트 확인 방법

특정 포트가 Listen 중 인것으로 출력되면 정상

`netstat -ntlp | grep <port-number>`

#### 주키퍼 지노드를 이용한 카프카 정보 확인

```
/usr/local/zookeeper/bin/zkCli.sh

ls <gnode-path>
ex) ls /peter-kafka/brokers/ids
```

#### 카프카 로그 확인

`cat ../kafka/logs/server.log`

#### 카프카 시작하기

```
# 토픽 생성
../kafka/bin/kafka-topics.sh --zookeeper <zookeepers> \
    --replication-factor <replica-count>\
    --partitions <partition-count> \
    --topic <topic-name> --create

# 토픽 삭제
../kafka/bin/kafka-topics.sh --zookeeper <zookeepers> \
    --topic <topic-name> --delete

# 카프카 프로듀서 쉘로 토픽 접근
../kafka/bin/kafka-console-producer.sh --broker-list <brokers> \
    --topic <topic-name>
## > 프롬프트에 입력 

# 카프카 컨슈머 쉘로 토픽 접근
../kafka/bin/kafka-console-consumer.sh --broker-list <brokers> \
    --topic <topic-name> --from-beginning
```
