# 교착 상태

#### &#x20;교착 상태란

&#x20;

교착 상태(deadlock)

* 두 개 이상 프로세스가 각자 자원을 점유하고, 서로의 자원을 요구할 때에 더 이상 프로세스 진행이 불가해진다.
* 즉, 서로의 종료만을 기다리며 진행이 멈춰버릴 때 교착 상태가 발생한다.

&#x20;

식사하는 철학자 문제(dining philosophers problem)

* 교착상태를 잘 설명하는 예시
* 철학자는 프로세스 또는 쓰레드, 포크는 자원, 생각하는 행위는 자원을 기다리는 것으로 이해할 수 있다.
* 포크는 한 번에 한 스레드에만 접근할 수 있으니 임계 구역으로 생각할 수 있다.

&#x20;

식사하는 철학자 문제 과정

* 모든 철학자는 원형 테이블에 앉아 있다.
* 모든 철학자는 양손에 포크를 들었을 때 식사를 시작할 수 있다.
* 철학자는 생각을 끝마치면 왼쪽 포크를 먼저 집어들고, 이후 오른쪽 포크를 집어든다.
* 식사를 마치면 오른쪽 포크를 내려놓고, 이후에 왼쪽 포크를 내려놓는다.
* 이를 반복한다.

&#x20;

식사하는 철학자 문제 발생 이유

* 한 두명 철학자가 식사를 하면 문제가 없다.
* 그러나 모든 철학자가 동시에 포크를 집어 들면 모든 철학자는 식사를 할 수 없다.
* 즉 모두 생각만 하게되고, 다른 철학자의 식사가 끝나기만을 기다린다.

&#x20;

교착 상태의 발생 예시

* 뮤텍스 락에서도 교착상태는 발생할 수 있다.
* 각 프로세스가 lock 1과 2를 잠구고 서로의 lock이 풀리기 만을 기다리면 교착 상태가 발생한다.

&#x20;

교착 상태의 해결

* 교착 상태 발생 상황을 정확히 표현할 수 있어야 한다.
* 교착 상태가 일어나는 근본적 원인을 알아야 한다.

&#x20;

***

&#x20;

&#x20;

자원 할당 그래프(resource-allocation graph)

* 각 프로세스가 어떤 자원을 사용하고, 어떤 자원을 기다리는지 간단히 표현한 그래프
* &#x20;교착 상태는 자원 할당 그래프를 통해 단순히 표현될 수 있다.

&#x20;

자원 할당 그래프 작성 규칙

&#x20;

1\. 프로세스는 원으로, 자원은 사각형으로 표현한다.

&#x20;

2\. 사용할 수 있는 자원 개수는 자원 사각형 내 점으로 표현한다.

&#x20;

3\. 프로세스가 어떤 자원을 사용 중이라면 자원에서 프로세스를 향해 화살표 표시한다.

* 프로세스가 자원 사용을 종료하면 화살표는 삭제된다.

&#x20;

4\. 프로세스가 어떤 자원을 기다리면 프로세스에서 자원으로 화살표를 표시한다.

&#x20;

&#x20;

자원 할당 그래프 예시

&#x20;

* 아래 사진은 다음을 의미한다.
  * SSD는 자원 개수가 3개, CPU는 2개, 프린터는 1개이다.
  * 프로세스 A는 SSD를 사용한다.
  * 프로세스 B와 C는 CPU를 사용하고, 프로세스 F는 CPU 사용을 요청한다.
  * 프로세스 D는 프린터를 사용하고, 프로세스 E는 프린터 사용을 요청한다.

<figure><img src="https://blog.kakaocdn.net/dn/cAg5f6/btrTobUJsx6/YOQ9wHuLf2q7KhYxGJ3VB0/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

&#x20;

* 식사하는 철학자 문제는 아래와 같이 표현할 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/dKtTZ4/btrTsfhZPRb/aBNTCP2N8DngnvJYv2ZkAk/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

* 교착상태는 아래와 같이 표현된다.
  * 즉, 교착 상태는 자원 할당 그래프가 원의 형태를 띌 때 발생한다.

<figure><img src="https://blog.kakaocdn.net/dn/cHk0MR/btrTjGgUp0p/66zCTRPP7WkgkFe3RGBaRk/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

&#x20;

***

&#x20;

&#x20;

**교착 상태 발생 조건**

&#x20;

* 교착 상태 발생 조건은 다음 네가지가 있다.
  * 상호 배제
  * 점유와 대기
  * 비선점
  * 원형 대기

&#x20;

* 조건들이 모두 만족할 때 교착 상태 발생 가능성이 생긴다. 하나라도 만족하지 않으면 교착상태는 발생하지 않는다.

&#x20;

1\.  상호 배제(mutual exclusion)

* 한 프로세스가 사용하는 자원을 다른 프로세스가 사용할 수 없는 상황
* 자원 할당량을 프로세스가 모두 사용하고, 작업이 끝나지 않으면 교착 상태가 발생할 수 있다.

&#x20;

2\. 점유와 대기(hold and wait)

* 프로세스가 자원을 할당 받은 상태에서 다른 자원의 할당을 기다리는 상태
* 다른 프로세스의 작업이 끝나지 않으면 자원을 무한히 기다리게 된다.

&#x20;

3\. 비선점(nonpreemptive)

* 비선점 자원: 자원을 이용하는 프로세스가 작업이 끝나야만 이용할 수 있는 자원
* 어떤 프로세스도 다른 프로세스에 할당된 자원을 뺏지 못하면 교착상태가 발생할 수 있다.

&#x20;

4\. 원형 대기(circular wait)

* 프로세스들이 원형 형태로 자원을 대기하는 상태
* 자원 할당 그래프가 원 형태로 그려지면 교착상태가 발생할 수 있다.

&#x20;

&#x20;

***

&#x20;

#### 13-2. 교착 상태 해결 방법

&#x20;

교착 상태 해결 방법

&#x20;

1\. 예방

* 교착 상태 발생 조건에 부합하지 않게 자원을 분배하는 방법

&#x20;

2\. 회피

* 교착 상태가 발생치 않도록 자원을 조금씩 할당하며, 교착 상태 위험이 있으면 자원을 할당하지 않는 방법

&#x20;

3\. 검출 후 회복

* 자원을 제약없이 할당하다가 교착 상태가 검출되면 이를 회복하는 방법

#### &#x20;

***

&#x20;

교착 상태 예방

* 교착 상태 발생 조건 네 가지 중 하나를 충족하지 못하게 하는 방법

&#x20;

1\. 자원의 상호 배제 예방

* 모든 자원을 공유 가능하게 만든다.
* 이론적으로 교착 상태를 없앨 수 있으나, 현실적으로는 모든 자원의 상호 배제를 없애기 어려워 실제 적용은 힘들다.

&#x20;

2\. 점유와 대기 예방

* 특정 프로세스에 자원을 모두 할당하거나 아예 할당하지 않는 방식으로 자원을 배분한다.
* 한계\

  * 자원 활용률이 낮아질 우려가 있다. 당장 자원이 필요해도 기다리는 프로세스와 사용하지 않는 자원을 오래동안 할당받는 프로세스가 생긴다.
  * 많은 자원을 사용하는 프로세스가 불리해진다. 동시에 자원을 사용할 타이밍을 확보하기 어렵기 때문이다.
  * 많은 자원을 필요로 하는 프로세스는 무한정 기다리는 기아현상이 발생할 가능성이 크다.

&#x20;

3\. 비선점 조건 예방

* 자원을 이용 중인 프로세스로 부터 해당 자원을 빼앗는다.
* 선점하여 사용할 수 있는 일부 자원에 효과적이다.
* 한계\

  * 모든 자원이 선점 가능한 것은 아니다. 한 프로세스 작업이 끝날 때까지 다른 프로세스가 기다려야 하는 자원도 존재한다.
  * 모든 자원을 빼앗을 수 없기 때문에 범용성이 떨어지는 방법이다.

&#x20;

&#x20;

4\. 원형 대기 조건 예방

* 모든 자원에 번호를 붙이고 오름차순으로 자원을 할당하면 원형대기는 발생하지 않는다.
* 즉, 원형 상황이 되는 것을 방지한다. 현실적이고 실용적인 방법이다.
* 한계\

  * 컴퓨터 시스템 내 모든 자원에 번호를 붙이기 어렵다.
  * 각 자원에 어떤 번호가 붙는지에 따라 특정 자원 활용률이 떨어질 수 있다.

&#x20;

예방 방식의 특징과 한계

* 예방 방식은 교착 상태가 발생하지 않음을 보장할 수 있다.
* 하지만 여러 한계점들과 부작용이 존재한다.

&#x20;

***

&#x20;

교착 상태 회피

* 교착 상태 발생이 무분별한 자원 할당으로 인해 발생한다고 간주한다.
* 따라서 회피는 교착 상태가 발생하지 않을 정도로 조절해서 자원을 할당하는 방식으로 교착 상태를 피한다.

&#x20;

**안전 상태와 불안전 상태**

&#x20;

1\. 안전 상태(safe state)

* 교착 상태가 발생하지 않게 모든 프로세스가 정상적으로 자원 할당 및 종료 될 수 있는 상태
* 안전 순서열이 있는 상태는 안전 상태이다. 안전 순서열대로 프로세스에 자원을 배분시 교착 상태는 발생하지 않는다.
  * 안전 순서열(safe sequence): 교착 상태 없이 안전하게 프로세스들에 자원을 할당할 수 있는 순서

&#x20;

&#x20;

2\. 불안전 상태(unsafe state)

* 교착 상태가 발생할 수도 있는 상황
* 안전 순서열이 없는 상황
* 불안전 상태에 시스템이 놓이면 교착 상태가 발생할 위험이 생긴다.

&#x20;

운영체제의 교착 상태 회피

* 운영 체제는 불안전 상태가 애초에 되지 않게끔 자원을 할당한다.
* 시스템 상태가 안전 상태에서 안전 상태로 움직이는 경우에만 자원을 할당한다.
* 즉, 회피 방식은 항시 안전 상태를 유지하도록 자원을 할당하는 방식이다.
* 이를 위해 각 프로세스의 최대 요구량을 고려해서 자원을 할당한다. 최대 요구량이 현재 할당 가능 자원보다 큰 경우, 아예 자원 할당을 하지 않는다.

&#x20;

***

&#x20;

**교착 상태 검출 후 회복**

* 교착 상태 발생을 확인하고 사후에 조치하는 방식이다.
* 운영체제는 프로세스들이 자원을 요구할 때마다 모두 할당하며, 교착 상태 발생 여부를 주기적으로 검사한다.
* 교착 상태가 검출되면 2가지 방식을 통해 회복한다.

&#x20;

교착 상태 회복 방식

&#x20;

1\. 선점을 통한 회복

* 교착 상태가 해결될 때까지 한 프로세스씩 자원을 몰아주는 방식이다.
* 다른 프로세스로부터 자원을 빼앗고 한 프로세스에 자원을 모두 몰빵해서 프로세스 진행을 완료한다.

&#x20;

&#x20;

2\. 프로세스 강제 종료를 통한 회복

* 가장 단순하고 확실한 방식이다.
* (1) 교착 상태에 놓인 프로세스를 모두 종료한다.
  * 장점: 교착 상태를 해결할 수 있는 가장 확실한 방법
  * 단점: 많은 프로세스들의 작업 내역을 잃게 된다.

&#x20;

* (2) 한 프로세스씩 종료할 수 있다.
  * 장점: 작업 내역을 잃는 프로세스를 최대한 줄일 수 있다.
  * 단점: 교착 상태가 없어졌는지 확인하는 과정에서 오버헤드를 야기한다.

&#x20;

***

&#x20;

교착 상태 무시

* 타조 알고리즘(ostrich algorithm): 드물게 발생하는 잠재적 문제를 무시로 대처하는 방법이다.
  * 문제 발생 빈도나 심각성에 따라 최대 효율을 추구하는 경우는 이 방식이 적합할 때도 많다.
