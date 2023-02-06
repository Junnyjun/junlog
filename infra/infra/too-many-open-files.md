---
description: Too many open files
---

# Too many open files

종종 DB나 storage server에서 너무 잦은 쓰기 요청이 들어오면 \
linux 기본 사용자 계정은 file 횟수 제한이 걸릴 때가 있다.

```bash
[junny@storage home] ulimit -a
... 
open files                      (-n) 1024
... 
```

기본값은 동시에 1024개로 제한되어 있는데,\
기본 값을 올려주거나 중간 중간 계속 변경해주는 방식이 있다.

#### 임시 적용 방식

`ulimit -n 4096` 명령어로 조정 해줄 수 있다

영구 적용 방식

```bash
[junny@storage home] vi /etc/security/limits.conf
* hard nofile 4096 # 모두 추가 root 제외
* soft nofile 4096
root hard nofile 4096
root soft nofile 4096
```



Database에서 발생했을 경우 database 설정도 바꿔준다

{% code title="MYSQL" %}
```bash
[junny@storage home] vi /etc/my.cnf
open_file_limit=4096
```
{% endcode %}
