# 자바에서의 동기화

#### 자바 모니터 (Java Monitors)

자바의 모든 객체는 단 하나의 \*\*락(lock)\*\*을 가집니다. `synchronized` 키워드로 선언된 메서드는 오직 락을 소유한 스레드만이 접근할 수 있습니다.

* 엔트리 세트(entry set): 락을 얻기 위해 기다리는 스레드들의 집합입니다.
* 대기 세트(wait set): `wait()` 메서드를 호출한 스레드가 락을 해제하고 대기하는 집합입니다.
* `wait()` 메서드는 스레드를 대기 세트로 보내고 락을 해제합니다.
* `notify()` 메서드는 대기 세트에서 임의의 스레드 하나를 선택하여 엔트리 세트로 이동시킵니다. 이 스레드는 다시 락을 얻기 위해 경쟁하게 됩니다.

***

#### 재진입 락 (Reentrant Locks)

`synchronized` 키워드보다 더 유연한 대안으로 `ReentrantLock`이 있습니다. 이 락은 `lock()`과 `unlock()` 메서드를 사용하여 상호 배제를 제공합니다. 일반적으로 `try-finally` 블록 내부에 `unlock()`을 배치하여 어떤 상황에서든 락이 항상 해제되도록 합니다.

***

#### 세마포어 (Semaphores)

자바 API는 카운팅 세마포어를 제공합니다. `Semaphore(int value)` 생성자로 초기값을 설정할 수 있으며, `acquire()`와 `release()` 메서드를 통해 세마포어를 획득하고 해제합니다. `acquire()`는 획득에 실패할 경우 스레드를 차단합니다.

***

#### 조건 변수 (Condition Variables)

조건 변수는 `wait()`와 `notify()`보다 더 유연한 기능을 제공합니다. `ReentrantLock`과 함께 사용되며, `newCondition()` 메서드로 `Condition` 객체를 생성합니다. `await()`는 스레드를 대기시키고, `signal()`은 대기 중인 스레드 중 하나를 깨워주는 역할을 합니다.
