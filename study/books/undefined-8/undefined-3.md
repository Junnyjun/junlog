# 쓰레드

### Threads <a href="#threads" id="threads"></a>

스레드는 프로세스의 작업 흐름을 말한다. 하나의 프로세스가 **한 번에 하나의 작업만 수행하는 것은 싱글스레드**(Single thread)이며, 하나의 프로세스가 **동시에 여러 작업을 수행하는 것은 멀티스레드**(Multi thread)라고 한다. 프로세서와 메모리가 발전하며 가능해진 기술이다. 멀티프로그래밍 시스템이니까 프로세스를 여러개 돌려도 되는데 굳이 스레드를 나누는 데는 이유가 있다.

1. 두 프로세스가 하나의 데이터를 공유하려면 메시지 패싱이나 공유 메모리 또는 파이프를 사용해야 하는데, 효율도 떨어지고 개발자가 구현, 관리하기도 번거롭다.
2. 프로세스 사이 컨텍스트 스위치가 계속 일어나면 성능 저하가 발생한다. 스레드 전환에도 컨텍스트 스위치가 일어나지만 속도가 더 빠르다.

### Multithreaded Server Architecture <a href="#multithreaded-server-architecture" id="multithreaded-server-architecture"></a>

서버와 클라이언트 사이에도 멀티스레드를 구현한다. 클라이언트가 서버에게 요청을 보내면 서버는 새로운 스레드를 하나 생성해 요청을 수행한다. 프로세스를 생성하는 것보다 스레드를 생성하는 것이 더 빠르기 때문이다.

### Multicore Programming <a href="#multicore-programming" id="multicore-programming"></a>

이렇게 멀티코어 또는 멀티프로세서 시스템을 구현할 때는 동시성(Concurrency)와 병렬성(Parallelism)을 알아야 한다. 동시성은 싱글 프로세서 시스템에서 사용되는 방식으로, 프로세서가 여러 개의 스레드를 번갈아가며 수행함으로써 **동시에 실행되는 것처럼 보이게 하는 방식**이다. 병렬성은 멀티코어 시스템에서 사용되는 방식으로, 여러 개의 코어가 각 **스레드를 동시에 수행하는 방식**이다.

### User Threads and Kernel Threads <a href="#user-threads-and-kernel-threads" id="user-threads-and-kernel-threads"></a>

유저 스레드는 사용자 수준의 스레드 라이브러리가 관리하는 스레드다. 스레드 라이브러리에는 대표적으로 POSIX Pthreads, Win32 threads, Java threads가 있다. 커널 스레드는 커널이 지원하는 스레드다. 커널 스레드를 사용하면 안정적이지만 유저 모드에서 커널 모드로 계속 바꿔줘야 하기 때문에 성능이 저하된다. 반대로 유저 스레드를 사용하면 안정성은 떨어지지만 성능이 저하되지는 않는다.

### Multithreading Models <a href="#multithreading-models" id="multithreading-models"></a>

유저 스레드와 커널 스레드의 관계를 설계하는 여러가지 방법이 있다.

#### Many-to-One Model <a href="#many-to-one-model" id="many-to-one-model"></a>

하나의 커널 스레드에 여러 개의 유저 스레드를 연결하는 모델이다. **한 번에 하나의 유저 스레드만 커널에 접근**할 수 있기 때문에 멀티코어 시스템에서 병렬적인 수행을 할 수가 없다. 요즘에는 잘 사용되지 않는 방식이다.

#### One-to-One Model <a href="#one-to-one-model" id="one-to-one-model"></a>

하나의 유저 스레드에 하나의 커널 스레드가 대응하는 모델이다. 동시성을 높여주고, 멀티프로세서 시스템에서는 동시에 여러 스레드를 수행할 수 있도록 해준다. 유저 스레드를 늘리면 커널 스레드도 똑같이 늘어나는데, 커널 스레드를 생성하는 것은 오버헤드가 큰 작업이기 때문에 성능 저하가 발생할 수 있다.

#### Many-to-Many Model <a href="#many-to-many-model" id="many-to-many-model"></a>

여러 유저 스레드에 더 적거나 같은 수의 커널 스레드가 대응하는 모델이다. 운영체제는 충분한 수의 커널 스레드를 만들 수 있으며, 커널 스레드의 구체적인 개수는 프로그램이나 작동 기기에 따라 다르다. 멀티프로세서 시스템에서는 싱글프로세서 시스템보다 더 많은 커널 스레드가 만들어진다.

#### Two-level Model <a href="#two-level-model" id="two-level-model"></a>

Many-to-Many 모델과 비슷한데, 특정 **유저 스레드를 위한 커널 스레드를 따로 제공**하는 모델을 말한다. 점유율이 높아야 하는 유저 스레드를 더 빠르게 처리해줄 수 있다.

### Thread Pools <a href="#thread-pools" id="thread-pools"></a>

스레드를 요청할 때마다 매번 새로운 스레드를 생성하고, 수행하고, 지우고를 반복하면 성능이 저하된다. 그래서 **미리 스레드 풀에 여러 개의 스레드를 만들어두고** 요청이 오면 스레드 풀에서 스레드를 할당해주는 방법을 사용한다.

[\
](https://parksb.github.io/article/9.html)