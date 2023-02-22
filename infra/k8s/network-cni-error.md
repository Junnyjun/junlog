# Network CNI Error

k8s설치 시 network 문제가 생기는 경우

```bash
Junny$ > kubectl describe nodes
Name:               Junnyland-master
...
Conditions:
  Type             Status  LastHeartbeatTime                 LastTransitionTime                Reason                       Message
  ----             ------  -----------------                 ------------------                ------                       -------
...
  Ready            False   Wed, 15 Feb 2023 09:35:49 +0900   Wed, 15 Feb 2023 09:26:02 +0900   KubeletNotReady              container runtime network not ready: NetworkReady=false reason:NetworkPl                              uginNotReady message:Network plugin returns error: cni plugin not initialized

```

네트워크 설정은 /etc/cni/net.d에서 처리한다.\
단순히 Pod를 하나 추가해주면 문제가 해결된다.

```bash
# Flannel 설치
> kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

# Calico 설치
> curl https://raw.githubusercontent.com/projectcalico/calico/master/manifests/calico.yaml -O
> kubectl apply -f calico.yaml
```

