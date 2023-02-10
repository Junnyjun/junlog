# 🔩 K8s

[admin, client 설정](https://github.com/Junnyjun/infra-base/tree/master/default)&#x20;

```bash
> kubeadm init --apiserver-cert-extra-sans={PUBLIC IP} \
--control-plane-endpoint "{PUBLIC IP}:6443" \
--v=10 
```
