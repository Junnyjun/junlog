# 병렬 처리와 커스텀 스레드 풀

Java 8에서 도입된 Stream API는 데이터에 대한 벌크 연산을 효율적으로 수행할 수 있는 강력한 기능입니다. \
특히 병렬 스트림(Parallel Stream)은 성능 향상을 제공할 수 있지만, 동시에 몇 가지 주의해야 할 점들이 있습니다.

### 병렬 스트림 기본

기본적으로 병렬 스트림은 `parallelStream()` 메서드를 통해 생성할 수 있습니다:

```
List<Long> aList = new ArrayList<>();
Stream<Long> parallelStream = aList.parallelStream();
assertTrue(parallelStream.isParallel());
```

기본적으로 이 스트림은 애플리케이션 전체에서 공유되는 `ForkJoinPool.commonPool()`을 사용합니다.

### 커스텀 스레드 풀 사용하기

때때로 기본 스레드 풀 대신 커스텀 스레드 풀을 사용해야 할 때가 있습니다. 예를 들어, 장기 실행 작업이나 네트워크 소스 처리 시 유용합니다:

```
ForkJoinPool customThreadPool = new ForkJoinPool(4);
long actualTotal = customThreadPool.submit(
    () -> aList.parallelStream().reduce(0L, Long::sum)).get();
```

스레드 풀의 병렬성 수준은 보통 CPU 코어 수를 기준으로 결정합니다.

### 메모리 누수 주의하기

커스텀 스레드 풀 사용 시 주의해야 할 중요한 점은 메모리 누수입니다. 스레드 풀을 사용한 후에는 반드시 `shutdown()` 메서드를 호출해야 합니다:

```
try {
    long actualTotal = customThreadPool.submit(
        () -> aList.parallelStream().reduce(0L, Long::sum)).get();
} finally {
    customThreadPool.shutdown();
}
```
