# 커맨드 라인 인터페이스 도구

### **인증 정보와 컨텍스트**

kubectl이 쿠버네티스 마스터와 통신할 때는 접속 대상의 서버 정보, 인증 정보 등이 필요하다. kubectl은 kubeconfig(기본 위치는 \~/.kube/config)에 쓰여 있는 정보를 사용하여 접속한다.

kubeconfig도 매니페스트와 동일한 형식으로 작성된다.

#### **kubectx/kubens를 사용한 전환**

컨텍스트나 네임스페이스를 전환할 때 kubectl 명령어를 사용하는 것이 불편하다면 오픈 소스 소프트웨어인 kubectx/kubens를 사용하는 것을 검토하자

### **매니페스트와 리소스 생성/삭제/갱신**

```
리소스 생성 : kubectl create -f sample-pod.yaml
생성한 파드 조회 : kubectl get pods
리소스 삭제 : kubectl delete -f sample-pod.yaml
특정 리소스만 삭제 : kubectl delete pod sample-pod
특정 리소스 종류를 모두 삭제 : kubectl delete pod -all
리소스 삭제(삭제 완료 대기) : kubectl delete -f sample-pod.yaml --wait
리소스 즉시 강제 삭제 : kubectl delete -f sample-pod.yaml --grace-perid 0 --force
리소스 업데이트 : kubectl apply -f sampe-pod.yaml
```

#### **리소스 생성에도 kubectl apply를 사용해야 하는 이유**

생성과 업데이트 시 명령어 분기를 할 이유가 없음

kubectl create와 kubectl apply를 섞어서 사용하면 kubectl apply를 실행할 때 변경 사항을 검출하지 못할 경우가 있다.

#### **파드 재기동**

kubectl apply -f sample-deployment.yaml

kubectl rollout restart deployment sample-deployment

#### **generateName으로 임의의 이름을 가진 리소스 생성**

kubect create 사용할 시, 난수가 있는 이름의 리소스를 생성 할 수 있음

metadata.generateName 지정

#### **리소스 상태 체크와 대기(wait)**

sample-pod가 정상적으로 기동할 때(Ready 상태가 될 때)까지 대기\
&#x20; kubectl wait --for=condition=Ready pod/sample-pod

모든 파드가 스케줄링될 때(PodScheduled 상태가 될 때)까지 대기\
&#x20; kubectl wait --for=condition=PodScheduled pod -all

모든 파드가 삭제될 때까지 파드마다 최대 5초씩 대기\
&#x20; kubectl wait --for=delete pod --all --timeout=5s&#x20;

#### **매니페스트 파일 설계**

하나의 매니페스트 파일에 여러 리소스를 정의

```
실행 순서를 정확하게 지켜야 하거나 리소스 간의 결합도를 높이고 싶을 경우
공통 설정 파일이나 패스워드 등은 분리하는 것이 좋다.
```

여러 매니페스트 파일을 동시에 적용

```
kubectl apply -f 디렉토리경로지정
```

매니페스트 파일 설계 방침

```
시스템 전체를 한 개의 디렉토리로 통합하는 패턴
시스템 전체를 특정 서브 시스템으로 분리하는 패턴
마이크로서비스별로 디렉토리를 나누는 패턴
```

#### **어노테이션과 레이블**

어노테이션 : 시스템 구성 요소가 사용하는 메타데이터

```
시스템 구성 요소를 위한 데이터 저장
모든 환경에서 사용할 수 없는 설정
정식으로 통합되기 전의 기능을 설정
```

레이블 : 리소스 관리에 사용하는 메타데이터

```
개발자가 사용하는 레이블
리소스 관리에 매우 유용
시스템이 사용하는 레이블
```

#### **편집기로 편집: edit**

#### **리소스 일부 정보 업데이트: set**

```
env
image
resources
selector
servceaccount
subject
```

kubectl set 명령어로 직접 설정 변경을 하더라도 매니페스트 파일은 업데이트되지 않음을 주의.

#### **로컬 매니페스트와 쿠버네티스 등록 정보 비교 출력: diff**

매니페스트 적용 전에 실제 쿠버네티스 클러스터에 등록된 정보를 비교하고 싶을 경우.

**사용 가능한 리소스 종류의 목록 가져오기: api-resources**

**리소스 정보 가져오기: get**

```
-l : 필터링할 레이블
--show-labels : 지정한 레이블을 표시
--output (-o) : JSON/YAML/Custom Columns/JSON Path/Go Template 등과 같은 다양한 형식으로 출력
all : all 카테고리에 속하는 몇 가지 리소스 목록을 가져올 수 있음
--watch : 리소스 상태의 변화가 있을 때 계속 결과를 출력할 수 있음
--output-watch-events : 해당 리소스가 API처럼 어떤 처리가 되었는지 이벤트 정보(ADDED/MODIFIED/DELETED)를 함께 표시할 수 있음
```

**리소스 상세 정보 가져오기: describe**

**실제 리소스 사용량 확인: top**

**컨테이너에서 명령어 실행: exec**

파드 내부의 컨테이너에서 /bin/ls 실행

* kubectl exec -it sample-pod -- /bin/ls

여러 컨테이너에 존재하는 파드의 특정 컨테이너에서 /bin/ls 실행

* kubectl exec -it sample-pod -c nginx-container -- /bin/ls

파이프 등 특정 문자가 포함된 경우 /bin/bash에 인수를 전달하는 형태로 실행

* kubectl exec -it sample-pod -- /bin/bash -c "ls --all --classify | grep lib"

**파드에 디버깅용 임시 컨테이너 추가: debug (1.18 alpha 기능)**

**로컬 머신에서 파드로 포트 포워딩: port-forward**

디버깅 용도 등으로 JMX 클라이언트에서 컨테이너에서 실행 중인 자바 애플리케이션 서버에 접속하거나, 데이터베이스 클라이언트에서 컨테이너에서 기동 중인 MySQL 서버에 접속해야할 경우 사용.

**컨테이너 로그 확인: logs**

**스턴을 사용한 로그 확인**

\- 오픈 소스 스턴(Stern)을 사용하면 로그를 더욱 편리하게 출력할 수 있다.

**컨테이너와 로컬 머신 간의 파일 복사: cp**

* 컨테이너 -> 로컬 : kubectl cp sample-pod:etc/hostname ./hostname
* 로컬 -> 컨테이너 : kubectl cp hostname sample-pod:/tmp/nefile

**kubectl 플러그인과 패키지 관리자: plugin/krew**

**kubectl에서 디버깅**

* \-v 옵션으로 로그 레벨을 지정하여 명령어 실행 내용을 좀 더 자세히 볼 수 있음

**kubectl의 기타 팁**

* alias 생성
* kube-ps1 : hash나 zsh의 프롬프트에 현재 작업 중인 쿠버네티스 클러스터와 네임스페이스를 표시
* 파드가 기동하지 않는 경우의 디버깅
  * kubectl logs 사용
  * kubectl describe 사용하여 Events 항목 확인
  * kubectl run 사용하여 실제 컨테이너 셸로 확인
