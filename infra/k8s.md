---
description: 기본세팅
---

# 🔩 K8s

admin, client 설정&#x20;

### CLIENT

[script](https://github.com/Junnyjun/infra-base/blob/master/default/kube\_client\_installer.sh)

```bash
kubeadm join {PUBLIC IP}:6443 --token {TOKEN} \
        --v=10 \
        --discovery-token-ca-cert-hash sha256:{HASH}
```

### MASTER

[script](https://github.com/Junnyjun/infra-base/blob/master/default/kube\_admin\_installer.sh)

```bash
> kubeadm init --apiserver-cert-extra-sans={PUBLIC IP} \
--control-plane-endpoint "{PUBLIC IP}:6443" \
--v=10 
```

```bash
> kubectl cluster-info
Kubernetes control plane is running at https://IP:6443
CoreDNS is running at https://IP:6443 ... 
> kubectl get nodes
NAME                     STATUS     ROLES           AGE     VERSION
instance-20230125-1513   Ready      <none>          2m43s   v1.26.1
instance-20230130-1315   Ready      control-plane   4m1s    v1.26.1

```



### 확인



```bash
> kubectl get pods --all-namespaces
NAMESPACE              NAME                                             READY   STATUS                  RESTARTS         AGE
kube-system            calico-kube-controllers-56dd5794f-6xtfs          0/1     ContainerCreating       0                6m27s
kube-system            calico-node-pxlnf                                0/1     Init:ImagePullBackOff   0                6m27s
kube-system            coredns-787d4945fb-7rm8w                         0/1     ContainerCreating       0                8m51s
kube-system            coredns-787d4945fb-8kq2t                         0/1     ContainerCreating       0                8m51s
kube-system            etcd-instance-20230125-1513                      1/1     Running                 319 (8m6s ago)   6m53s
kube-system            kube-apiserver-instance-20230125-1513            1/1     Running                 308 (9m3s ago)   9m35s
kube-system            kube-controller-manager-instance-20230125-1513   0/1     CrashLoopBackOff        343 (59s ago)    6m53s
kube-system            kube-proxy-55p8q                                 1/1     Running                 5 (2m46s ago)    8m52s
kube-system            kube-scheduler-instance-20230125-1513            0/1     CrashLoopBackOff        330 (39s ago)    9m35s
kubernetes-dashboard   dashboard-metrics-scraper-7bc864c59-ptll2        0/1     Pending                 0                4m53s
kubernetes-dashboard   kubernetes-dashboard-6c7ccbcf87-8wspv            0/1     Pending                 0                4m53s
```

```bash
> kubectl describe nodes
Name:               Junnyland
Roles:              control-plane
Labels:            ...
```
