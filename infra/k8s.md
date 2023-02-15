---
description: 기본세팅
---

# 🔩 K8s

admin, client 설정&#x20;

### CLIENT

[script](https://github.com/Junnyjun/infra-base/blob/master/default/kube\_client\_installer.sh)

```bash
kubeadm join {PUBLIC IP}:6443 --token {TOKEN} \ 
        --v=5 \
        --discovery-token-ca-cert-hash sha256:{HASH}
```

### MASTER

[script](https://github.com/Junnyjun/infra-base/blob/master/default/kube\_admin\_installer.sh)

```bash
> kubeadm init --apiserver-cert-extra-sans={PUBLIC IP} \
       --pod-network-cidr 10.244.0.0/16 \ ## flannel cidr
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
kube-flannel   kube-flannel-ds-mmb4h                            1/1     Running   2 (35s ago)      52s
kube-system    coredns-787d4945fb-l98gc                         1/1     Running   0                51s
kube-system    coredns-787d4945fb-xvkk7                         1/1     Running   0                51s
kube-system    etcd-instance-20230125-1513                      1/1     Running   325 (101s ago)   9s
kube-system    kube-apiserver-instance-20230125-1513            1/1     Running   314 (71s ago)    68s
kube-system    kube-controller-manager-instance-20230125-1513   1/1     Running   1 (101s ago)     104s
kube-system    kube-proxy-mp69j                                 1/1     Running   2 (38s ago)      52s
kube-system    kube-scheduler-instance-20230125-1513            1/1     Running   336 (101s ago)   103s
```

```bash
> kubectl describe nodes
Name:               Junnyland
Roles:              control-plane
Labels:            ...
```
