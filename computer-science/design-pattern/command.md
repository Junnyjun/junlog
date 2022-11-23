# Command

실행되는 기능을 캡슐화 하여 재사용성이 높도록 설계하는 패턴입니다



<img src="../../.gitbook/assets/file.drawing (5) (3).svg" alt="command" class="gitbook-drawing">

Client는 상태를 바꿀수 있고,  현재 상태를 출력할 수 있습니

상태는 준비,쇼핑중,대기 세가지가 존재합니다.



### How do code ?

{% embed url="https://gist.github.com/Junnyjun/8f541fe88c39f7e58d56fdcecef8e2c7" %}
code
{% endembed %}

Result&#x20;

```basic
I'm standby

I'm shopping

I'm ready
```
