# Argo

Argo CD는 쿠버네티스(Kubernetes) 환경에서 애플리케이션 배포를 자동화하는 오픈소스 도구입니다. GitOps 원칙을 따르며, Git 저장소에 저장된 애플리케이션 코드의 변경 사항을 추적하고 배포 자동화를 위한 설정을 관리합니다.

### Argo CD의 주요 기능

#### 1. 지속적인 배포

Argo CD는 지속적인 배포를 위해 설계되었습니다. 새로운 애플리케이션 릴리스를 배포할 때마다, Git 저장소의 변경 사항을 기반으로 자동으로 배포합니다.

#### 2. 선언적인 배포

Argo CD는 선언적인 배포를 지원합니다. 이는 사용자가 어떤 배포 작업을 수행해야 하는지 명시하는 대신, 어떤 상태가 되어야 하는지 설명하는 것입니다. 이를 통해 사용자는 시스템의 상태를 더 잘 이해하고, 더 쉽게 배포를 구성할 수 있습니다.

#### 3. 다양한 배포 방식

Argo CD는 다양한 배포 방식을 지원합니다. 쿠버네티스 자원, 헬름(Helm) 차트, Kustomize 등 다양한 배포 방식을 사용할 수 있습니다. 또한, 사용자 정의 배포 도구를 통합할 수 있습니다.

#### 4. 모니터링 및 로깅

Argo CD는 배포 과정에서 발생하는 이벤트를 추적하고, 배포 상태를 모니터링할 수 있습니다. 또한, 로그를 수집하고 분석할 수 있으므로, 문제가 발생할 경우 빠르게 대응할 수 있습니다.

### Argo CD의 장점

#### 1. 안정적인 배포

Argo CD는 GitOps 원칙을 따르므로, 안정적인 배포를 보장합니다. Git 저장소에 저장된 모든 변경 사항을 추적하고, 이를 기반으로 자동으로 배포하기 때문에, 실수나 인적 오류를 방지할 수 있습니다.

#### 2. 사용자 친화적인 UI

Argo CD는 사용자가 쉽게 배포를 관리할 수 있는 UI를 제공합니다. 사용자는 애플리케이션의 배포 상태를 쉽게 확인할 수 있으며, 배포 이력을 추적할 수 있습니다.

#### 3. 유연한 설정

Argo CD는 다양한 배포 방식을 지원하므로, 사용자는 다양한 환경에서 유연하게 배포를 구성할 수 있습니다. 또한, 사용자 정의 배포 도구를 통합할 수 있기 때문에, 더욱 유연한 배포 환경을 구성할 수 있습니다.

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

### 마무리

Argo CD는 쿠버네티스 환경에서 안정적이고 유연한 애플리케이션 배포를 위한 오픈소스 도구입니다. GitOps 원칙을 따르며, 사용자 친화적인 UI와 다양한 배포 방식을 지원합니다.
