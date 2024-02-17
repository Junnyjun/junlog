---
description: 설치
---

# Jenkins

### Continuous Integration

Jenkins는 코드 변경이 일어나면 자동으로 소프트웨어를 빌드하고, 테스트를 수행하는 CI 기능을 제공합니다. 이를 통해 여러 명의 개발자들이 동시에 작업할 때 코드 충돌이나 오류를 미리 발견하고, 빠르게 수정할 수 있습니다.

#### 플러그인 기능

Jenkins는 다양한 플러그인을 지원하여 빌드, 테스트, 배포 등 다양한 작업들을 자동화할 수 있습니다. 이를 통해 사용자의 요구에 맞게 Jenkins를 확장하여 사용할 수 있습니다.

### 장단점

#### 장점

* 오픈소스이므로 무료로 사용할 수 있습니다.
* 다양한 플러그인을 지원하여 많은 작업들을 자동화할 수 있습니다.
* CI/CD 기능을 제공하여 릴리즈 주기를 단축하고, 빠른 피드백을 받아 개선할 수 있습니다.

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
