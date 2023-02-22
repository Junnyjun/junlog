# mysql

my sql 검색 : [https://artifacthub.io/](https://artifacthub.io/)&#x20;

```bash
# Repo 등록
> helm repo add my-repo https://charts.bitnami.com/bitnami
> helm repo list
NAME    URL
my-repo https://charts.bitnami.com/bitnami
# Mysql 설치
> helm install my-release my-repo/mysql --set "auth.rootPassword=admin1234"
> kubectl get secrets --namespace default my-release-mysql -o jsonpath="{.data.mysql-root-password}" | base64 -d
admin1234
# 상태 조회
> kubectl get svc
NAME                        TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
kubernetes                  ClusterIP   10.96.0.1        <none>        443/TCP    6d4h
my-release-mysql            ClusterIP   10.105.163.179   <none>        3306/TCP   2m6s
my-release-mysql-headless   ClusterIP   None             <none>        3306/TCP   2m6s
# 외부 연결
> kubectl edit svc my-release-mysql
spec:
  type: LoadBalancer
  ports:
    - name: mysql
      port: 3306
      targetPort: 3306
      protocol: TCP
      
> kubectl get svc
NAME                        TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes                  ClusterIP      10.96.0.1        <none>        443/TCP          6d4h
my-release-mysql            LoadBalancer   10.105.163.179   <pending>     3306:30016/TCP   9m33s
my-release-mysql-headless   ClusterIP      None             <none>        3306/TCP         9m33s

```
