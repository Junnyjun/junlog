---
description: 조합된 기능이 많은 경우
---

# Decorator

객체의 결합으로 기능을 유연하게 확장할 수 있는 패턴

객체의 조합으로 더 큰 구조를 만들 때 사용합니다.

<img src="../../.gitbook/assets/file.drawing (2).svg" alt="" class="gitbook-drawing">

| class          | description |
| -------------- | ----------- |
| CreateConfig   | 설정 정보 리턴    |
| MakeProperties | 설정 정보 설정    |
| LoggingInform  | 로깅 유무       |

### How do code?

{% embed url="https://gist.github.com/Junnyjun/bc444ae2c8cbca3b334b427c1c39668e" %}

```
실행 결과
----------
NO LOGGING
----------
encoder & only url & no log = ENCODED-dXJs

----------
LOGGING TO FILE
CREATE FILE
WRITE FILE
----------
no encode & url,user,password & log = url and user and 1234
```
