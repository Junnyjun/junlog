# Multi Thread Consumer

Kafka에서 **멀티 스레드 컨슈머**를 사용하는 것은 **병렬 처리**를 위해 매우 중요합니다. Kafka는 기본적으로 각 컨슈머가 **하나의 스레드에서 실행**되도록 설계되었기 때문에, **여러 개의 파티션을 병렬로 처리**하려면 **멀티 스레드 방식**을 적용해야 합니다.&#x20;

하지만 Kafka는 **스레드 안전성**을 제공하지 않기 때문에, 멀티 스레드를 적용할 때 **주의할 사항**과 **특정 패턴**이 필요합니다.

#### 멀티 스레드 컨슈머에서 고려해야 할 사항

Kafka에서 **컨슈머**는 **스레드 안전하지 않으므로**, 각 스레드에서 독립된 KafkaConsumer 인스턴스를 사용하는 것이 중요합니다. 따라서, 멀티 스레드를 구현할 때는 일반적으로 다음과 같은 방식이 사용됩니다:

1. **컨슈머 스레드를 여러 개 생성**하여 각 스레드에서 독립적으로 KafkaConsumer를 실행.
2. **하나의 컨슈머 스레드**에서 데이터를 가져와 **여러 개의 워커 스레드**가 처리하는 방식.

### 다중 컨슈머 스레드 (Multiple Consumer Threads)

이 방식은 **여러 개의 스레드에서 독립적인 Kafka 컨슈머**를 각각 실행하는 방식입니다. 각 컨슈머는 자신에게 할당된 **파티션에서 메시지를 읽어옵니다**. Kafka는 **파티션 당 하나의 컨슈머**가 메시지를 소비하도록 설계되어 있기 때문에, **컨슈머 그룹** 내에서 각 파티션이 다른 스레드에 할당됩니다.

```kotlin
class ConsumerRunnable(val topic: String, val consumerProps: Properties) : Runnable {
    override fun run() {
        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.subscribe(listOf(topic))

        try {
            while (true) {
                val records = consumer.poll(Duration.ofMillis(100))
                records.forEach { record ->
                    println("Consumed message: ${record.value()} from partition ${record.partition()}")
                }
            }
        } catch (e: WakeupException) {
            println("Consumer is waking up...")
        } finally {
            consumer.close()
        }
    }
}

fun main() {
    val consumerProps = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    }

    // 스레드 풀 생성
    val executor = Executors.newFixedThreadPool(3)
    
    for (i in 1..3) {
        // 각 스레드에 독립적인 KafkaConsumer 인스턴스 할당
        executor.submit(ConsumerRunnable("my-topic", consumerProps))
    }

    // 일정 시간 후에 컨슈머를 종료하는 로직 추가 가능
}
```

**장점**

**독립적인 컨슈머**: 각 스레드가 자체 KafkaConsumer를 사용하므로, **스레드 간 충돌**이 발생하지 않음.

**병렬 처리**: 각 컨슈머가 할당된 파티션에서 병렬로 데이터를 처리할 수 있음.

**단점**

**스레드 수가 많아질 경우**: 컨슈머 수와 파티션 수가 맞지 않으면 **비효율적인 리소스 사용**이 발생할 수 있음. 각 파티션에 하나의 컨슈머만 할당되므로, 파티션 수보다 많은 컨슈머가 있을 때는 컨슈머가 놀게 됨.

***

### 하나의 컨슈머 + 워커 스레드 (Single Consumer with Worker Threads)

이 방식에서는 **하나의 컨슈머 스레드**가 메시지를 가져오고, 가져온 메시지를 여러 **워커 스레드**가 처리하는 구조입니다.&#x20;

KafkaConsumer는 스레드 안전하지 않으므로, **KafkaConsumer는 단일 스레드에서 실행**되고, 워커 스레드가 데이터를 처리하는 방식으로 멀티 스레드를 구현합니다

```kotlin
class WorkerRunnable(val record: ConsumerRecord<String, String>) : Runnable {
    override fun run() {
        println("Processing message: ${record.value()} from partition ${record.partition()}")
        // 메시지 처리 로직 추가
    }
}

fun main() {
    val consumerProps = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    }

    val consumer = KafkaConsumer<String, String>(consumerProps)
    consumer.subscribe(listOf("my-topic"))

    // 스레드 풀 생성 (워커 스레드)
    val executor = Executors.newFixedThreadPool(10)

    try {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            records.forEach { record ->
                // 워커 스레드에서 메시지 처리
                executor.submit(WorkerRunnable(record))
            }
        }
    } catch (e: WakeupException) {
        println("Consumer is waking up...")
    } finally {
        consumer.close()
        executor.shutdown()  // 워커 스레드 종료
    }
}
```

**장점:**

* **안전한 메시지 처리**: KafkaConsumer는 **단일 스레드**에서 메시지를 가져오고, 실제 메시지 처리는 **여러 워커 스레드**에서 진행됩니다. 이를 통해 안전한 스레드 처리가 가능합니다.
* **메시지 처리 병렬화**: 메시지를 가져오는 작업과 메시지 처리 작업을 분리하여, 처리 작업을 **병렬화**할 수 있습니다.

**단점:**

* **단일 컨슈머 병목**: 메시지를 가져오는 작업이 **단일 컨슈머**에 의존하므로, Kafka 파티션 수가 많을 경우 처리 속도가 한정될 수 있습니다.

***

#### 멀티 스레드 컨슈머 설계 시 고려 사항

1. **스레드 안전성**: KafkaConsumer는 **스레드 안전하지 않기 때문에**, 각 스레드는 독립된 KafkaConsumer 인스턴스를 사용해야 합니다. `assign()`이나 `subscribe()`를 사용하여 파티션을 컨슈머 그룹에 할당할 수 있습니다.
2. **오프셋 커밋 관리**: 멀티 스레드 환경에서는 **오프셋 커밋 관리**가 중요합니다. 만약 여러 스레드에서 메시지를 처리하고 있다면, **오프셋을 언제 커밋할지**에 대한 명확한 전략이 필요합니다. 특히 메시지가 정상적으로 처리된 후에만 커밋하도록 해야 **데이터 유실**을 방지할 수 있습니다.
3. **리밸런싱**: 컨슈머 그룹에서 **컨슈머가 추가되거나 제거**될 때 리밸런싱이 발생합니다. 이때 각 컨슈머에 할당된 파티션이 변경될 수 있으므로, 멀티 스레드 환경에서는 리밸런싱이 일어날 때 각 스레드가 **새로 할당된 파티션을 적절히 처리**하도록 해야 합니다.
