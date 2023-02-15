# 8080 refuse

현재 실행된 계정을 못찾는 경우이다

```bash
> mkdir -p $HOME/.kube
> sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
> sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

로 현재 계정에 추가해준다
