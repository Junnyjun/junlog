# Control node schedule

Control node(MASTER)의 스케줄링 비활성화

```bash
$kubectl taint nodes junny-master node-role.kubernetes.io=master:NoSchedule-
node/junny-master untainted
```

Control node(MASTER)의 스케줄링 활성화

```bash
$kubectl taint nodes junny-master node-role.kubernetes.io=master:NoSchedule
node/iap03 tainted
```



PENDING중이던 노드가 시작으로 변경

```
junnyland# kubectl get pods --all-namespaces
NAMESPACE              NAME                                             READY   STATUS      RESTARTS   AGE
ingress-nginx          ingress-nginx-admission-create-h2sth             0/1     Completed   0          20m
ingress-nginx          ingress-nginx-admission-patch-4wnhz              0/1     Completed   2          20m
ingress-nginx          ingress-nginx-controller-686bffdd45-ztc2k        1/1     Running     0          20m
kubernetes-dashboard   dashboard-metrics-scraper-6fdb9d6cdd-pd6hg       1/1     Running     0          36m
kubernetes-dashboard   kubernetes-dashboard-55d6784bb-56gr6             1/1     Running     0          36m

```



