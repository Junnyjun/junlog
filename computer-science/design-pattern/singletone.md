# Singletone

인스턴스가 하나만 존재하도록 하고 싶을때

new를 사용한 객체생성을 딱 한번만 하고 싶은 경우 사용합니다.

<img src="../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

### Default Singleton ( eger)

가장 일반적으로 사용하는 싱글톤입니다.\
사용하지 않아도 클래스를 생성하기 때문에 낭비가 있습니다

{% embed url="https://gist.github.com/Junnyjun/8d1f5178de6ddfb322262bd4f2f27072" %}

### Instance field

default와 똑같이 Class loading시점에 이미 인스턴스가 생성되어 낭비가 됩니다.

{% embed url="https://gist.github.com/Junnyjun/ee988c735e2185146bb42e69ec2d310a" %}

### Lazy Loading

인스턴스 접근 시점에 초기화 하여 낭비를 줄일 수 있습니다.\
하지만 동기화 문제가 발생할 수도 있습니다\
syncronized 로 접근제한을 할 수도 있습니다.

{% embed url="https://gist.github.com/Junnyjun/589cc16ebf1830b96b537eeceb3e9f8a" %}

### Standard

현재 가장 많이 쓰이는 방법입니다.

{% embed url="https://gist.github.com/Junnyjun/800fe277aeb4ad2b629f23df41391983" %}

Joshua

조슈아 블로크가 제안한 방식입니다.

{% embed url="https://gist.github.com/Junnyjun/01dfcdba1aee855f8305dc0e8be113f2" %}
