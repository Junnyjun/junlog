# Password

몇가지 자주 이용하는 비밀번호 해싱에 알아봅니다. \
비밀번호 해싱은 네가지 속성을 가지고 있어야 합니다.

```
결정적 : 동일한 해시 함수로 처리된 동일한 메시지는 항상 동일한 해시를 생성해야 합니다.
되돌릴 수 없다 : 해시에서 메시지를 생성하는 것은 비실용적입니다. 
엔트로피가 높다 : 메시지를 조금만 변경해도 다른 해시값이 생성됩니다.
해시 충돌에 저항한다 : 두 개의 서로 다른 메시지가 동일한 해시를 생성해서는 안 됩니다.
```

이 외에도, 무차별 대입 방지를 위한 속도가 적당히 **느려야** 하는 추가적인 검토사항이 필요하다.

## [취약점](https://www.avira.com/en/blog/md5-the-broken-algorithm)이 발견된 알고리즘

### [MD5](password.md#md5)

해싱  충돌에 저항할수 없습니다. 현재에는 파일 무결성 검사로 종종 사용합니다.\
또한 굉장히 빠른 알고리즘이기 때문에 무차별 공격에 취약합니다.

### SHA-512

새로운 취약점이 발견됨에 따라 권장되지 않습니다.\
3세대 알고리즘중 가장 긴 키를 가지고 있으며, 자바에서 구현되는 강력한 버전 중 하나입니다.

## 추천 되는 알고리즘

### **BCrypt** <a href="#bd-3-implementing-bcrypt-and-scrypt-in-java" id="bd-3-implementing-bcrypt-and-scrypt-in-java"></a>

blowfish 암호 기반의 암호화 해시 함수이다. - 패스워드 저장을 목적으로 설계\
Blowfish 암호를 기반으로 설계된 암호화 함수이며 현재까지 사용중인 가장 강력한 해시 메커니즘 중 하나이다.

보안에 집착하기로 유명한 OpenBSD에서 사용하고 있다. (컴퓨터 보안에 특화된 오픈 소스 운영 체제)\
Salting과 Key stretching으로 Rainbow table attack, brute-force 공격에 대비할 수 있다.

### **SCrypt** <a href="#bd-3-implementing-bcrypt-and-scrypt-in-java" id="bd-3-implementing-bcrypt-and-scrypt-in-java"></a>

하드웨어 구현을 하는데 크기와 비용이 훨씬 더 비싸기 때문에, 주어진 자원에서 공격자가 사용할 수 있는 병렬처리의 양이 한정적이다.

OpenSSL 1.1 이상을 제공하는 시스템에서만 작동한다.

### **PBKDF2**  <a href="#bd-2-implementing-pbkdf2-in-java" id="bd-2-implementing-pbkdf2-in-java"></a>

해시 함수의 컨테이너인 PBKDF2는 솔트를 적용한 후 해시 함수의 반복 횟수를 임의로 선택할 수 있다.

PBKDF2는 아주 가볍고 구현하기 쉬우며, SHA와 같이 검증된 해시 함수만을 사용한다.
