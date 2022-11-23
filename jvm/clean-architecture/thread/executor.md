# Executor

Thread의 작업이 현재스레드인지 새스레드인지, \
작업의 실행 흐름을 제어할 수 있게 도와줍니다.\
작업실행이 꼭 비동기일 필요는 없습니다.

지금 까지와 동일한[ Printer](./)를 예제로 사용합니다



### How do code?

{% embed url="https://gist.github.com/Junnyjun/4bf5025bfd797e576b744049cd156cdd" %}

#### result

```basic
1. Printer Run
2. Start Printer Scheduling ... 
3. Printer Add more
Start Scheduler ... 2
PRINTER NAME ::pool-1-thread-1
PRINTING :: A
PRINTING :: B
PRINTING :: C
COMPLETE
REMOVED = A,B,C
PRINTER NAME ::pool-1-thread-1
PRINTING :: 1
PRINTING :: 2
PRINTING :: 3
COMPLETE
REMOVED = 1,2,3
4. Printer is Empty ...
5. Printer Stop
```

Executors.newFixedPoolSize( int ) 로 원하는 poolsize를 지정해준다

<img src="../../../.gitbook/assets/file.drawing (2).svg" alt="" class="gitbook-drawing">

Executors에 submit된 작업들은 Thread Pool에서 계속실행되게 된다.

Executors는 _shutdown()_ , _awaitTermination()_ 등을 사용하여 반드시 종료해주어야 한다

### Scheduled Executor

주기적으로 작업을 수행할 수 있습니다.

Executors를 `Executors.newSingleThreadScheduledExecutor();` 로 바꿔준 뒤, \
Main => `Future<?> executors = printExecutor.schedule(scheduler, 1, TimeUnit.SECONDS);` 이렇게 수정해 줍니다.

```java
public class Main {
    private final static ScheduledExecutorService printExecutor = PrintExecutor.scheduled;

    public static void main(String[] args) {
        System.out.println("1. Printer Run");
        System.out.println("2. Start Printer Scheduling ... ");

        PrinterScheduler scheduler = PrinterScheduler.init(Printer.job("A", "B", "C"));
        Future<?> executors = printExecutor.schedule(scheduler, 1, TimeUnit.SECONDS);

        System.out.println("3. Printer Add more");
        scheduler.addSchedule(Printer.job("1", "2", "3"));

        while (true){
            if (scheduler.isEmpty()) {
                System.out.println("4. Printer is Empty ...");
                break;
            }
        }
        printExecutor.shutdown();
        System.out.println("5. Printer Stop");
    }
}
```



