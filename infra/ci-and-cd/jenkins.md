---
description: 설치
---

# Jenkins

## Jenkins란 무엇인가?

Jenkins는 오픈소스 자동화 도구로, 소프트웨어 개발 프로세스를 자동화하고, 테스트, 빌드, 배포 등의 작업을 수행하는 CI/CD 도구입니다. Jenkins는 다양한 플러그인을 지원하여 많은 형태의 빌드, 테스트, 배포 등을 자동화할 수 있습니다. Jenkins는 Java로 작성되었으며, 무료로 사용할 수 있습니다.

### Jenkins의 주요 기능

#### Continuous Integration (CI)

Jenkins는 코드 변경이 일어나면 자동으로 소프트웨어를 빌드하고, 테스트를 수행하는 CI 기능을 제공합니다. 이를 통해 여러 명의 개발자들이 동시에 작업할 때 코드 충돌이나 오류를 미리 발견하고, 빠르게 수정할 수 있습니다.

#### Continuous Delivery (CD)

Jenkins는 CI 이후, 소프트웨어를 자동으로 배포하는 CD 기능도 제공합니다. 이를 통해 릴리즈 주기를 단축하고, 빠른 피드백을 받아 개선할 수 있습니다.

#### 플러그인 기능

Jenkins는 다양한 플러그인을 지원하여 빌드, 테스트, 배포 등 다양한 작업들을 자동화할 수 있습니다. 이를 통해 사용자의 요구에 맞게 Jenkins를 확장하여 사용할 수 있습니다.

### Jenkins의 장단점

#### 장점

* 오픈소스이므로 무료로 사용할 수 있습니다.
* 다양한 플러그인을 지원하여 많은 작업들을 자동화할 수 있습니다.
* CI/CD 기능을 제공하여 릴리즈 주기를 단축하고, 빠른 피드백을 받아 개선할 수 있습니다.

#### 단점

* 설정이 복잡하고, 처음 사용하기에는 어려울 수 있습니다.
* 보안에 취약할 수 있습니다.

### Jenkins 설치하기

Jenkins를 설치하기 위해서는 Java가 필요합니다. Java가 설치되어 있지 않은 경우, 먼저 Java를 설치해야 합니다. 그 후, 아래의 단계를 따라 Jenkins를 설치할 수 있습니다.

1. Jenkins 공식 홈페이지에서 Jenkins를 다운로드합니다.
2. 다운로드한 Jenkins 파일을 실행합니다.
3. Jenkins 설치 과정을 따라 설치를 완료합니다.

### Docker

```bash
> sudo docker run -d -p 8888:8080 -v /jenkins:/var/jenkins_home --name my_jenkins -u root jenkins/jenkins:lts
```

### K8s

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jenkins
spec:
  replicas: 1
  selector:
    matchLabels:
      app: jenkins
  template:
    metadata:
      labels:
        app: jenkins
    spec:
      containers:
      - name: jenkins
        image: jenkins/jenkins:lts
        ports:
          - name: http-port
            containerPort: 8080
          - name: jnlp-port
            containerPort: 50000
        volumeMounts:
          - name: jenkins-vol
            mountPath: /var/jenkins_vol
      volumes:
        - name: jenkins-vol
          emptyDir: {}
```

### 결론

Jenkins는 오픈소스 CI/CD 도구로, 다양한 플러그인을 지원하여 많은 작업들을 자동화할 수 있습니다. Jenkins를 사용하면 릴리즈 주기를 단축하고, 빠른 피드백을 받아 개선할 수 있습니다. 단, 설정이 복잡하고 보안에 취약할 수 있다는 점을 유의해야 합니다.
