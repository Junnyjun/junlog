# Observer

한 객체의 상태 변화에 따라 다른 객체의 상태들도 연동될 수 있도록 1:N의 관계를 구성한다.

상대 클래스에 의존하지 않으면서 데이터 변경을 알려줄 때 사용한다

<img src="../../.gitbook/assets/file.drawing (11) (1).svg" alt="" class="gitbook-drawing">

학생 성적을 추가할 때 마다 과목별 최고 점수를 호출합니다.



### How do code ?

{% embed url="https://gist.github.com/Junnyjun/d25161578b9ef9d461951323881a3678" %}

### Result?

```basic
ADD STUDENT EXAM
MAX MATH = 80
MAX ENGLISH = 65

MAX MATH = 92
MAX ENGLISH = 65

MAX MATH = 92
MAX ENGLISH = 86

MAX MATH = 100
MAX ENGLISH = 87
```

