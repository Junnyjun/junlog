# Flyweight

많은 객체를 생성하고, 공유할때 유용한 패턴입니다.



<img src="../../.gitbook/assets/file.drawing (9) (2) (1).svg" alt="" class="gitbook-drawing">

기존의 id를 가진 값이 있으면 그값을 주고 없으면 새롭게 객체를 만들고 추가해서 주는(공유) 방식입니다.\
Java의  여러 [Cache](../../jvm/clean-architecture/instance-cache.md) 에서 사용되고 있는 방식입니다.

{% embed url="https://gist.github.com/Junnyjun/6253ef071a244b5ebf0c664acad229d2" %}
