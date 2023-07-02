# 쿠버네티스 기초

쿠버네티스는 쿠버네티스 마스터와 쿠버네티스 노드로 구성되어 있다.

쿠버네티스 마스터는 API 엔드포인트 제공, 컨테이너 스케줄링, 컨테이너 스케일링 등을 담당

쿠버네티스 노드는 이른바 도커 호스트에 해당, 실제로 컨테이너를 기동시키는 노드다.

쿠버네티스 클러스터 관리는 CLI도구인 kubectl과 YAML 형식이나 JSON 형식으로 작성된 매니페스트 파일을 사용하여 쿠버네티스 마스터에 '리소스'를 등록해야 한다.

매니페스트 파일은 가독성을 위해 일반적으로 YAML 형식으로 작성한다.

### 쿠버네티스와 리소스

#### **워크로드 API 카테고리**

```
파드(Pod)
레플리케이션 컨트롤러(Replication Controller)
레플리카셋(ReplicaSet)
디플로이먼트(Deployment)
데몬셋(DaemonSet)
스테이트풀셋(SatetfulSet)
잡(Job)
크론잡(CronJob)
```

#### **서비스 API 카테고리**

#### 서비스

```
ClusterIP
External IP(ClusterIP의 한 종류)
NodePort
LoadBalancer
Headless(None)
ExternalName
None-Selector
```

**컨피그 & 스토리지 API 카테고리**

```
시크릿(Secret)
컨피그맵(ConfigMap)
영구 볼륨 클레임(PersistentVolumeClaim)
```

**클러스터 API 카테고리**

```
노드(Node)
네임스페이스(Namespace)
영구 볼륨(PersistentVolume)
리소스 쿼터(ResourceQuota)
서비스 어카운트(ServiceAccount)
롤(Role)
클러스터 롤(ClusterRole)
롤바인딩(RoleBinding)
클러스터롤바인딩(ClusterRoleBinding)
네트워크 정책(NetworkPolicy)
```

**메타데이터 API 카테고리**

```
LimitRange
HorizontalPodAutoscaler(HPA)
PodDisruptionBudget(PDB)
커스텀 리소스 데피니션(CustomResourceDefinition)
```

#### &#x20;네임스페이스로 가상적인 클러스터 분리

쿠버네티스에는 네임스페이스라고 불리는 가상적인 쿠버네티스 클러스터 분리 기능이 있다. 완전한 분리 개념이 아니기 때문에 용도는 제한되지만, 하나의 쿠버네티스 클러스터를 여러 팀에서 사용하거나 서비스 환경/스테이징 환경/개발 환경으로 구분하는 경우 사용할 수 있다.

관리형 서비스나 구축 도구로 구축된 경우 대부분의 k8s 클러스터는 RBAC(Role-Based Access Control)가 기본값으로 활성화되어 있다. 또 일부 환경에서는 네트워크 정책을 사용할 수 있다.

&#x20;

이 책에서 네임스페이스 분리 범위에 대해서는 팀마다 분리하는 것이 좋고, 서비스/스테이징/개발 환경을 네임스페이스로 분리하는 것은 추천하지 않는다. 후자의 이유는 아래와 같다.

* 클러스터 업그레이드 시 동시에 모든 환경에서 장애가 발생할 가능성이 있다.
* 네임스페이스 명명 규칙이 prd-ns1/stg-ns1처럼 되면 매니페스트 재사용성이 현저하게 저하된다. 클러스터를 분리하면 각 환경에서 같은 네임스페이스 이름을 사용할 수 있기 때문에 완전히 같은 매니페스트를 재사용할 수 있다.
* 서비스 이름 해석 시 SERVICE.prd-ns1.svc.cluster.local 등의 다른 목적지에 통신을 해야 한다.
