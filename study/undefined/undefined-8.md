---
description: 데이터를 가져오는 정책에 대해서 알아봅니다.
---

# 요구 페이징

## 요구페이징?

메모리를 효율적으로 관리하기 위해, 적은양의 프로세스만 유지한다.

필요한 모듈들만 올려 응답 속도를 향상하기 위해서다



사용자가 요구한 특정 기능을 수행할 때 해당 모듈들을 메모리에 올려두면 여러 이점들이 있는데, 이와 같이 요구한 페이지를 메모리에 올리는 것을 요구 페이징 이라고 한다.

{% hint style="info" %}
미리가져오기?? :  요구페이징과 반대로 앞으로 필요할 것이라고 예상된 페이지를 미리 가져온다
{% endhint %}

### 페이지 테이블엔트리

가상 메모리는 물리 메모리와 스왑 영역을 합친 것이다.

<img src="../../.gitbook/assets/file.drawing (2) (1).svg" alt="swapIn &#x26; swapOut" class="gitbook-drawing">

사용자 프로세스는 물리 메모리와 스왑 영역 둘중 한곳에 있다.

페이지 테이블은 페이지가 메모리에 있는지 스왑 영역에 있는지 표시해야 하는데, 이걸 `유효 비트`라고 한다



PTE 내부에는 페이지번호, 플래그 비트, 프레임 번호가 존재하는데, 이중 프레임 번호를 가지고 어느 프레임에 있는지 알려준다.

이 프레임 번호는 주소 필드 라고도 한다.

<figure><img src="../../.gitbook/assets/image (2).png" alt=""><figcaption></figcaption></figure>

이 PTE에는 여러 플래그 비트들이 존재한다.

```
접근 비트 : 페이지에 올라온 후 사용된 적이 있는지 여부 (참조 비트라고 한다)
변경 비트 : 페이지에 올라온 후 변경된 적이 있는지 여부 (더티 비트라고 한다)
유효 비트 : 실제 메모리에 있는지 아닌지 나타내는 경우 (프레젠트 비트 라고 한다)
읽기&쓰기&실행 비트 : 권한을 나타내는 비트
```

### 페이지 부재

페이지에 위치에 따라 유효 비트는 0,1을 가지게 된다.

```
유효비트 1 -> 스왑영역 , 유효비트 0 -> 물리 메모리 영역 
```

프로세스가 페이지를 요청했을 때 그 페이지가 메모리에 없는 상황을 페이지 부재라고 한다.

<img src="../../.gitbook/assets/file.drawing.svg" alt="" class="gitbook-drawing">

페이지 폴트가 발생하면 스왑 영역에서 메모리로 데이터를 옮겨야 한다.

페이지 폴트가 발생하면 스왑영역의 데이터를 메모리의 빈 공간에 옮긴 뒤 갱신 한다.

메모리에 빈 프레임이 많은 경우엔 수월하지만, 메모리 프레임이 가득 차있다면 `페이지 교체 알고리즘`을 통해 특정 페이지(`대상페이지`)를 스압 영역으로 내보낸다.

### 지역성

메모리가 꽉차서 스왑 영역으로 보낼 때는 앞으로 사용하지 않을 페이지를 쫓아내는 것이 좋다.

지역성은 아래 세가지를 기준으로 체크한다.

#### 공간의 지역성

현재 위치에서 가까운 데이터에 접근할 확률이 먼 거리에 있는 확률 보다 높다.

#### 시간의 지역성

현재를 기준으로 가장 가까운 시간에 접근한 데이터가 먼 시간에 접근한 데이터보다 확률이 높다

#### 순차적 지연성

작업은 순차적으로 진행되는 경향이 있다는 걸 의미한다.


