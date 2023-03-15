# Argo

Argo는 Kubernetes 상에서 머신러닝 워크플로우를 자동화하는 대표적인 툴입니다. Argo를 이용하면 Kubernetes 상에서 머신러닝 워크플로우를 쉽게 배포, 실행, 관리할 수 있습니다.

### Argo의 주요 기능

Argo는 다음과 같은 주요 기능을 제공합니다.

#### 1. YAML 파일을 이용한 워크플로우 정의

Argo는 워크플로우를 정의하는 YAML 파일을 이용해 머신러닝 워크플로우를 자동화합니다. 각 단계의 입력과 출력을 정의하고, 각 단계 사이의 의존성을 설정할 수 있습니다.

#### 2. 워크플로우 실행과 관리

Argo는 Kubernetes 위에서 동작하며, 워크플로우의 각 단계를 컨테이너로 실행합니다. 워크플로우가 실행 중일 때는 각 단계의 상태를 모니터링하며, 에러가 발생하면 자동으로 재시도합니다.

#### 3. 워크플로우 시각화

Argo는 워크플로우 전체를 시각화할 수 있는 대시보드를 제공합니다. 이를 통해 워크플로우가 어떤 단계에서 실행되고 있는지, 각 단계의 상태는 어떤지 등을 한 눈에 확인할 수 있습니다.

#### 4. 다양한 워크플로우 타입 지원

Argo는 다양한 워크플로우 타입을 지원합니다. 예를 들어, 서로 다른 입력 데이터를 가지고 여러 번 실행되는 트레이닝 워크플로우나, 대규모 데이터셋을 처리하는 데이터 전처리 워크플로우 등을 지원합니다.

### Argo의 장점

Argo를 이용하면 Kubernetes 상에서 머신러닝 워크플로우를 쉽게 자동화할 수 있습니다. 이는 다음과 같은 장점을 제공합니다.

#### 1. 높은 확장성

Kubernetes를 이용하므로, Argo를 이용한 머신러닝 워크플로우는 높은 확장성을 제공합니다. 필요에 따라 워크플로우를 확장하거나 축소할 수 있으며, Kubernetes 클러스터를 이용해 필요한 컴퓨팅 자원을 동적으로 할당할 수 있습니다.

#### 2. 높은 안정성

Argo는 각 단계의 상태를 모니터링하며, 에러가 발생하면 자동으로 재시도합니다. 이를 통해 머신러닝 워크플로우의 안정성을 높일 수 있습니다.

#### 3. 쉬운 관리

Argo는 Kubernetes 클러스터 위에서 동작하므로, Kubernetes 클러스터를 이용한 다른 서비스와 함께 쉽게 관리할 수 있습니다.

### argo 설치

```bash
> echo "Install Argo CD"
> kubectl create namespace argocd
> kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/ha/install.yaml

> echo "Install Argo CLi"
> mkdir ~/bin
> curl -sSL -o ~/bin/argocd https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
> chmod +x ~/bin/argocd

> echo "External application"
> kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort"}}'

> echo "Get password"
> $ kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
{{password}}

> kubectl get svc argocd-server -n argocd
NAME            TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
argocd-server   LoadBalancer   10.96.176.107   <pending>     80:32038/TCP,443:31129/TCP   8m24s
```

\{{Master IP\}} :31129 로 접속할 수 있다.



### 결론

Argo를 이용하면 Kubernetes 상에서 머신러닝 워크플로우를 쉽게 자동화할 수 있습니다. 이를 통해 머신러닝 모델 개발자는 머신러닝 모델의 개발과 배포에 더욱 집중할 수 있습니다.
