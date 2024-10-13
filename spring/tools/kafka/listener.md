# Page 2

### Record Listener

#### 특징

* **개별 메시지 처리**: 각 메시지(레코드)를 하나씩 처리합니다.
* **간단한 구현**: 기본적인 메시지 처리를 위한 가장 일반적인 리스너입니다.
* **비동기 처리**: 메시지를 비동기적으로 수신하고 처리합니다.

#### 구현 방법

* `@KafkaListener` 어노테이션을 사용하여 리스너 메서드를 정의합니다.
* 메서드 파라미터로 메시지의 값 또는 `ConsumerRecord`를 받을 수 있습니다.

#### 코드 샘플

```kotlin
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RecordListener {

    @KafkaListener(topics = ["my-topic"], groupId = "my-group")
    fun listen(message: String) {
        println("Received message: $message")
        // 메시지 처리 로직
    }
}
```

#### 옵션

* **topics**: 리스닝할 토픽을 지정합니다.
* **groupId**: 컨슈머 그룹 ID를 설정합니다.
* **concurrency**: 동시에 실행될 스레드 수를 설정하여 병렬 처리를 지원합니다.
* **errorHandler**: 메시지 처리 중 예외 발생 시 사용할 에러 핸들러를 지정합니다.

***

### Batch Listener

#### 특징

* **여러 메시지 일괄 처리**: 한 번에 여러 메시지를 리스트로 받아 처리합니다.
* **성능 향상**: 대량의 메시지를 효율적으로 처리할 수 있습니다.
* **배치 작업에 적합**: 데이터베이스 일괄 입력 등 배치 작업에 유용합니다.

#### 구현 방법

* `ConcurrentKafkaListenerContainerFactory`의 `isBatchListener`를 `true`로 설정합니다.
* 리스너 메서드의 파라미터를 `List<ConsumerRecord<K, V>>` 또는 `List<V>`로 받습니다.

#### 코드 샘플

**설정 클래스**

```kotlin
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@Configuration
class KafkaConsumerConfig {

    @Bean
    fun consumerFactory(): DefaultKafkaConsumerFactory<String, String> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "my-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun batchFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = true
        return factory
    }
}
```

**배치 리스너 구현**

```kotlin
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BatchListener {

    @KafkaListener(
        topics = ["my-topic"],
        groupId = "my-group",
        containerFactory = "batchFactory"
    )
    fun listen(messages: List<String>) {
        println("Received batch messages: $messages")
        // 배치 메시지 처리 로직
    }
}
```

#### 옵션

* **batchSize**: 한 번에 가져올 메시지 수를 설정합니다.
* **pollTimeout**: 메시지를 가져올 때 대기할 최대 시간을 지정합니다.
* **concurrency**: 병렬로 실행될 컨슈머 스레드 수를 설정합니다.

***

### Acknowledgment Mode Listener

#### 특징

* **수동 오프셋 커밋**: 메시지 처리가 완료된 후 수동으로 오프셋을 커밋합니다.
* **정확한 처리 보장**: 메시지의 중복 처리나 손실을 방지할 수 있습니다.
* **트랜잭션 관리**: 데이터베이스 트랜잭션과 연계하여 사용 가능합니다.

#### 구현 방법

* 리스너 메서드의 파라미터로 `Acknowledgment` 객체를 받습니다.
* 메시지 처리가 완료된 후 `acknowledge()` 메서드를 호출하여 오프셋을 커밋합니다.

#### 코드 샘플

**리스너 구현**

```kotlin
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class AcknowledgmentListener {

    @KafkaListener(
        topics = ["my-topic"],
        groupId = "my-group",
        containerFactory = "kafkaManualAckListenerContainerFactory"
    )
    fun listen(message: String, ack: Acknowledgment) {
        try {
            println("Processing message: $message")
            // 메시지 처리 로직
            ack.acknowledge() // 오프셋 커밋
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
            // 에러 처리 로직
        }
    }
}
```

**설정 클래스**

```kotlin
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@Configuration
class KafkaConsumerConfig {

    @Bean
    fun consumerFactory(): DefaultKafkaConsumerFactory<String, String> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "my-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaManualAckListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        return factory
    }
}
```

#### 옵션

* **ackMode**: 수동 커밋 모드를 설정합니다 (`MANUAL`, `MANUAL_IMMEDIATE` 등).
* **syncCommits**: 커밋을 동기화할지 여부를 설정합니다.
* **errorHandler**: 예외 발생 시 처리 로직을 정의합니다.

***

### Consumer Aware Listener

#### 특징

* **Consumer 객체 접근**: 리스너 메서드에서 `Consumer` 객체를 받아 컨슈머에 대한 제어가 가능합니다.
* **동적 제어**: 런타임에 컨슈머 설정 변경이나 파티션 할당 등이 가능합니다.
* **고급 기능 활용**: 낮은 수준의 Kafka API를 활용할 수 있습니다.

#### 구현 방법

* 리스너 메서드의 파라미터로 `Consumer` 객체를 받습니다.
* `Consumer` API를 사용하여 컨슈머를 제어합니다.

#### 코드 샘플

```kotlin
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ConsumerAwareListener {

    @KafkaListener(topics = ["my-topic"], groupId = "my-group")
    fun listen(record: ConsumerRecord<String, String>, consumer: Consumer<String, String>) {
        println("Received message: ${record.value()} from partition: ${record.partition()}")
        // Consumer 객체를 사용한 추가 제어 로직
    }
}
```

#### 옵션

* **pollTimeout**: 메시지를 폴링할 때 사용할 타임아웃을 설정합니다.
* **listenerType**: 리스너의 타입을 지정하여 원하는 형태의 리스너를 구현할 수 있습니다.
* **concurrency**: 컨슈머의 병렬 처리를 위한 스레드 수를 지정합니다.

***

### Message Listener Container

#### 특징

* **커스텀 리스너 구현**: 어노테이션 없이 직접 리스너 컨테이너를 생성하여 사용합니다.
* **고급 설정 지원**: 세부적인 설정과 제어가 가능합니다.
* **유연성**: 복잡한 요구 사항이나 특수한 케이스에 적합합니다.

#### 구현 방법

* `MessageListenerContainer`를 직접 생성하고 설정합니다.
* `ContainerProperties`를 통해 필요한 설정을 적용합니다.

#### 코드 샘플

```kotlin
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.stereotype.Component

@Component
class CustomListenerContainer(
    consumerFactory: ConsumerFactory<String, String>
) {

    init {
        val containerProps = ContainerProperties("my-topic")
        containerProps.messageListener = MessageListener<String, String> { record: ConsumerRecord<String, String> ->
            println("Received message: ${record.value()}")
            // 메시지 처리 로직
        }
        val container = KafkaMessageListenerContainer(consumerFactory, containerProps)
        container.start()
    }
}
```

#### 옵션

* **ContainerProperties**: 오프셋 관리, 에러 처리, 스레드 수 등 다양한 설정이 가능합니다.
* **setAutoStartup**: 컨테이너의 자동 시작 여부를 설정합니다.
* **setConcurrency**: 컨슈머 스레드 수를 지정하여 병렬 처리를 지원합니다.

***

### 추가 옵션 및 고려 사항

* **에러 처리 (Error Handling)**
  * `errorHandler` 또는 `seekToCurrentErrorHandler`를 사용하여 예외 발생 시 재시도 로직을 구현합니다.
  * 데드 레터 큐(DLQ)를 설정하여 처리 실패한 메시지를 별도의 토픽에 저장할 수 있습니다.
* **병렬 처리 및 성능 튜닝**
  * **concurrency**: 리스너 컨테이너 팩토리에서 설정하여 병렬 처리 스레드 수를 늘립니다.
  * **파티션 수 증가**: 토픽의 파티션 수를 늘려 병렬 처리가 가능하도록 합니다.
* **보안 설정**
  * **SSL/TLS 암호화**: `ssl.keystore.location` 등의 설정을 통해 통신을 암호화합니다.
  * **인증 및 권한 부여**: SASL 또는 OAuth를 사용하여 인증과 권한 관리를 구현합니다.
* **트랜잭션 관리**
  * `ChainedKafkaTransactionManager`를 사용하여 Kafka와 데이터베이스 간의 트랜잭션을 통합할 수 있습니다.
* **모니터링 및 로깅**
  * 스프링 액추에이터(Actuator)와 통합하여 애플리케이션 상태를 모니터링합니다.
  * Kafka의 JMX 메트릭을 활용하여 성능 및 상태를 추적합니다.
