# Idempotent Producer

Kafka의 멱등성 프로듀서(Idempotent Producer)는 **메시지 중복 전송 방지**를 위한 기능으로, **프로듀서가 동일한 메시지를 여러 번 보내더라도 Kafka는 메시지를 한 번만 처리**하는 것을 보장합니다.&#x20;

멱등성 프로듀서를 사용하면 **네트워크 오류**나 **재시도** 등으로 인해 **중복된 메시지**가 브로커에 전송되는 경우에도, Kafka가 이를 자동으로 처리해 **중복 없이 정확히 한 번 전송**(**Exactly Once Delivery**)을 구현할 수 있습니다.

#### Kafka 멱등성 프로듀서란?

멱등성(idempotency)은 동일한 요청을 여러 번 수행해도 **결과가 변하지 않는 특성**을 의미합니다.&#x20;

Kafka에서 **멱등성 프로듀서**는 **프로듀서의 중복 메시지 전송**을 방지하기 위해 설계되었습니다. 프로듀서가 **같은 메시지를 여러 번 전송할 때**, Kafka는 이를 중복 처리하지 않고 **한 번만 처리**하도록 보장합니다.

Kafka에서 멱등성 프로듀서는 메시지 전송이 실패하거나 **네트워크 문제가 발생해 재시도가 필요할 때**, **중복된 메시지가 전송되는 것을 방지**합니다.

#### 멱등성 프로듀서의 주요 특징

1. **메시지 중복 전송 방지**: 프로듀서가 같은 메시지를 여러 번 전송하더라도 Kafka는 중복된 메시지를 한 번만 처리합니다. 이를 통해 **재시도 시 중복 메시지가 발생하지 않도록** 보장합니다.
2. **프로듀서의 안정성 향상**: Kafka는 멱등성 프로듀서 사용 시 프로듀서 ID(Producer ID, PID)와 **시퀀스 넘버**를 통해 각 메시지를 추적합니다. 이를 통해 **메시지 중복과 순서 보장**을 지원하며, 네트워크 장애 또는 브로커 장애 시에도 메시지를 **정확히 한 번** 처리합니다.
3. **정확히 한 번 전송(Exactly Once Delivery)**: 멱등성 프로듀서는 메시지를 **정확히 한 번** 전송하는 것을 보장합니다. 이 기능은 특히 **장애 복구 후에도 데이터 일관성을 유지**하고자 할 때 유용합니다.

***

#### 동작 원리

Kafka의 멱등성 프로듀서는 각 **프로듀서 인스턴스**에 고유한 Producer ID (PID)를 할당하여, 메시지를 전송할 때마다 **시퀀스 넘버**와 함께 메시지를 브로커에 전송합니다.

1. **Producer ID**: Kafka는 각 프로듀서에 고유한 ID를 부여합니다. 이 ID는 프로듀서 인스턴스가 전송하는 모든 메시지에 포함됩니다.
2. **시퀀스 넘버**: 프로듀서는 각 메시지에 **시퀀스 넘버**를 추가합니다. Kafka 브로커는 해당 시퀀스 넘버를 기반으로 **메시지의 순서**를 추적하고, 중복된 시퀀스 넘버가 전송될 경우 이를 **무시**합니다.
3. **재전송 시 중복 처리 방지**:
   * 네트워크 오류나 재시도 상황에서, 프로듀서가 같은 메시지를 여러 번 전송할 수 있습니다. 그러나 Kafka는 **PID**와 **시퀀스 넘버**를 사용해 **중복된 메시지를 자동으로 필터링**하고, 한 번만 메시지를 처리합니다.
   * 중복 전송된 메시지를 Kafka가 자동으로 **무시**하므로, 중복 메시지로 인한 부작용이 발생하지 않습니다.

***

#### 멱등성 프로듀서 설정

Kafka에서 멱등성 프로듀서를 활성화하려면 **`enable.idempotence`** 설정을 `true`로 설정해야 합니다. 이 설정을 통해 Kafka는 프로듀서에게 고유한 **Producer ID**를 할당하고, 메시지 전송 시 **중복 메시지를 방지**하는 기능을 활성화합니다.

```kotlin
val props = Properties()
props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"  // 멱등성 활성화

val producer = KafkaProducer<String, String>(props)
val record = ProducerRecord("my-topic", "key", "value")

producer.send(record) { metadata, exception ->
    if (exception != null) {
        println("Error sending message: ${exception.message}")
    } else {
        println("Message sent successfully to partition ${metadata.partition()}, offset ${metadata.offset()}")
    }
}

producer.close()
```

위 코드를 보면 `enable.idempotence`가 `true`로 설정되어 있어 Kafka 프로듀서는 멱등성 모드로 동작합니다. 이를 통해 **메시지 중복 방지**와 정확히 한 번 전송(Exactly Once Delivery)이 보장됩니다.

***

#### 멱등성 프로듀서와 트랜잭션

Kafka의 **트랜잭션** 기능과 **멱등성 프로듀서**는 자주 함께 사용됩니다. Kafka에서 트랜잭션은 **여러 메시지를 원자적으로 처리**하는 기능을 제공하며, 멱등성 프로듀서를 사용해 **중복 전송을 방지**하고 **정확한 데이터 일관성**을 보장할 수 있습니다.

예를 들어, 멱등성 프로듀서를 트랜잭션 모드로 사용할 경우, **한 번에 여러 파티션**에 전송되는 메시지들이 **모두 성공하거나 모두 실패**하도록 처리할 수 있습니다. 이를 통해 **Exactly Once Semantics** (EOS)를 구현할 수 있습니다.

***

#### 멱등성 프로듀서의 장점

1. **메시지 중복 방지**: 네트워크 장애나 재시도 상황에서 중복 메시지를 방지하고, 메시지를 정확히 한 번만 처리합니다.
2. **안정성 향상**: PID와 시퀀스 넘버를 사용해 메시지의 중복 전송을 관리하며, 이를 통해 장애 상황에서도 데이터의 일관성을 유지할 수 있습니다.
3. **정확히 한 번 전송(Exactly Once Delivery)**: 멱등성 프로듀서는 메시지를 정확히 한 번만 전송하는 것을 보장합니다. 이 기능은 Kafka에서 **데이터 손실이나 중복을 허용하지 않아야 할 경우**에 매우 유용합니다.
4. **트랜잭션과 연계**: 멱등성 프로듀서는 Kafka의 트랜잭션 기능과 결합해 **여러 메시지 또는 파티션 간의 원자적 처리**를 구현할 수 있습니다.

***

#### 멱등성 프로듀서의 한계

멱등성 프로듀서는 **단일 파티션 내에서의 중복 전송을 방지**합니다. 즉, 동일한 파티션에 메시지를 중복 전송하는 것을 방지하는 데는 매우 효과적이지만, **다른 파티션에 메시지를 중복 전송하는 경우**에는 별도의 관리가 필요할 수 있습니다.
