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
> kubectl describe nodes
Name:               Junnyland
Roles:              control-plane
Labels:            ...
```
