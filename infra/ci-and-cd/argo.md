# Argo

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
> kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'

> echo "Get password"
> $ kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
{{password}}

> kubectl get svc argocd-server -n argocd
NAME            TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
argocd-server   LoadBalancer   10.96.176.107   <pending>     80:32038/TCP,443:31129/TCP   8m24s
> kubectl port-forward  --address=0.0.0.0 -n argocd svc/argocd-server 8080:443
```

\{{Master IP\}} :8080 로 접속할 수 있다.
