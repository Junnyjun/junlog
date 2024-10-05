# Producer

## **Kafka 프로듀서란?**

Kafka 프로듀서는 Kafka 시스템에서 데이터를 생성하여 **Kafka 토픽으로 전송**하는 역할을 합니다. 프로듀서는 애플리케이션에서 발생한 이벤트나 메시지를 Kafka 클러스터로 보내며, 이 메시지는 나중에 컨슈머에 의해 처리됩니다. Kafka의 비동기 전송 방식과 고성능 배치 처리 덕분에, 프로듀서는 대규모 데이터 스트리밍 시스템의 핵심 요소입니다.

**Kafka 프로듀서**는 데이터를 생성하는 애플리케이션과 **Kafka 브로커** 간의 통신을 담당합니다. 프로듀서는 메시지를 **토픽**으로 전송하고, 각 메시지는 **파티션**에 저장됩니다.&#x20;

메시지의 순서와 분산 처리를 효과적으로 관리하기 위해 키(Key)와 오프셋(Offset)을 사용합니다.

Kafka 프로듀서는 비동기 방식으로 데이터를 전송하며, 브로커와 상호작용하는 동안 **복제 설정** 및 **확인 응답**을 통해 메시지 전송의 성공 여부를 제어할 수 있습니다.

<img src="../../../.gitbook/assets/file.excalidraw (60).svg" alt="" class="gitbook-drawing">



## **동작 원리**

**데이터 생성 및 메시지 구성**: 애플리케이션에서 발생한 데이터를 **레코드(Record)** 형태로 Kafka 프로듀서가 생성합니다. 레코드는 **키와 값**으로 구성됩니다.

**토픽 및 파티션 지정**: 프로듀서는 메시지를 전송할 **토픽**을 지정하고, Kafka는 **메시지의 키**를 기반으로 **어느 파티션에 저장할지 결정**합니다. 키가 없으면 Kafka는 라운드 로빈 방식으로 파티션을 선택합니다.

**브로커로 메시지 전송**: 프로듀서는 지정된 토픽과 파티션으로 **브로커에 메시지를 전송**합니다. 메시지가 성공적으로 전송되면, 브로커는 설정된 **확인 응답(acks)** 방식에 따라 프로듀서에 응답을 보냅니다.

**오프셋 관리**: 메시지가 브로커에 저장될 때 **오프셋**이 부여되며, 이를 통해 메시지의 순서가 관리됩니다. 이후, 컨슈머는 이 오프셋을 기반으로 메시지를 읽습니다.

```kotlin
class KafkaClientTest {
    val topic = "test"

    @Test
    fun sendTest() {
        val producer = KafkaProducer<String, String>(ProducerConfiguration().config())
        producer.send(ProducerRecord(topic, "key", "value"))

        producer.flush()
        producer.close()
    }

    class ProducerConfiguration {
        fun config() = Properties().apply {
            put(BOOTSTRAP_SERVERS_CONFIG, "{host}")
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        }
    }
}
```

### **옵션**

#### **acks (Acknowledgment)**

* **acks=0**: 브로커의 응답을 기다리지 않고 메시지를 전송합니다. 성능은 높지만, **데이터 손실** 위험이 있습니다.
* **acks=1**: 메시지가 **리더 파티션**에 저장되면 확인 응답을 받습니다. 리더만 저장한 경우 성능과 안정성 사이의 절충안입니다.
* **acks=all**: 메시지가 리더와 **모든 팔로워 파티션**에 저장되면 응답을 받습니다. 성능은 다소 느리지만, **데이터 내구성**을 보장합니다.

**retries (재시도)**

메시지 전송 실패 시 **재시도 횟수**를 설정할 수 있습니다. 이 기능을 통해 네트워크 오류나 일시적인 장애로 인해 데이터가 손실되지 않도록 방지합니다.

**compression.type (압축 방식)**

메시지를 **압축**하여 전송할 수 있습니다. **gzip**, **snappy**, **lz4** 등의 압축 방식을 지원하며, 이를 통해 **네트워크 대역폭 절약**과 **전송 속도 향상**을 기대할 수 있습니다.

**linger.ms**

메시지를 바로 전송하지 않고, **일정 시간 동안 대기**하여 더 많은 메시지를 모아 \*\*배치(batch)\*\*로 전송할 수 있습니다. 이를 통해 **네트워크 효율성**을 높이고 **전송 성능**을 향상시킬 수 있습니다.

**batch.size**

한 번에 전송할 **배치 크기**를 설정합니다. 크기가 클수록 더 많은 데이터를 한 번에 전송하여 **성능 최적화**를 할 수 있지만, 메모리 사용량이 증가할 수 있습니다.

맞습니다. Kafka에서 \*\*`ack` (acknowledgment)\*\*는 매우 중요한 개념이며, 메시지가 **안전하게 전송되고 처리되었는지 확인**하는 데 사용됩니다. **프로듀서**가 메시지를 브로커에 전송한 후, 브로커는 프로듀서에게 \*\*메시지가 성공적으로 처리되었는지 응답(acknowledgment)\*\*을 보냅니다. 이를 통해 **데이터 내구성**과 **신뢰성**을 보장할 수 있습니다.

### `acks`란?

`acks`는 **Kafka 프로듀서**가 메시지를 Kafka 브로커로 보낼 때, **브로커가 메시지를 정상적으로 수신했는지 확인하는 방식**을 설정하는 옵션입니다.&#x20;

이 설정을 통해 **메시지가 브로커에서 안전하게 처리되었는지**에 대한 신뢰성을 보장할 수 있습니다.

Kafka에서 제공하는 **`acks`** 설정은 세 가지 수준으로 나뉩니다:

**`acks=0`**:

* **브로커가 응답하지 않음**: 프로듀서는 **메시지를 브로커로 보낸 후** 확인 응답(ACK)을 기다리지 않고 바로 다음 메시지를 보냅니다.
* **장점**: 매우 빠르게 메시지를 전송할 수 있습니다.
* **단점**: **데이터 손실** 가능성이 있습니다. 브로커에 장애가 발생하거나 네트워크 문제가 생기면 메시지가 유실될 수 있지만, 프로듀서는 이를 알지 못합니다.

**`acks=1`**:

* **리더 파티션에서만 확인**: 프로듀서는 **리더 파티션**이 메시지를 수신하고 기록하면 확인 응답(ACK)을 받습니다. 하지만 **팔로워 파티션**이 이 메시지를 복제하지 않은 상태에서 리더가 장애를 일으키면 **데이터가 손실**될 수 있습니다.
* **장점**: **빠른 성능**과 **어느 정도의 신뢰성**을 모두 제공합니다.
* **단점**: 리더 파티션의 장애 시 **데이터 손실** 가능성이 있습니다.

**`acks=all` (또는 `acks=-1`)**:

* **모든 복제본에서 확인**: 프로듀서는 리더뿐만 아니라 **모든 팔로워 파티션**이 메시지를 복제하고 기록한 후에 확인 응답(ACK)을 받습니다. 이는 **가장 높은 수준의 내구성**을 제공합니다.
* **장점**: **데이터 손실** 가능성이 거의 없습니다. 리더가 장애가 발생해도, **팔로워 파티션**이 메시지를 복제했기 때문에 데이터는 안전하게 저장됩니다.
* **단점**: **성능이 느려질 수 있습니다**. 모든 팔로워 파티션이 메시지를 복제할 때까지 기다려야 하기 때문에 처리 속도가 느려질 수 있습니다.

Kafka 프로듀서 설정에서 **`acks`** 옵션을 설정하여 메시지 전송의 신뢰성 수준을 조정할 수 있습니다. 예를 들어, 메시지 손실을 방지하고 싶다면 `acks=all`로 설정하고, 성능을 우선시할 경우 `acks=0` 또는 `acks=1`을 선택할 수 있습니다.

다음은 **`acks`** 설정을 사용하는 예시입니다:

```kotlin
val props = Properties()
props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
props[ProducerConfig.ACKS_CONFIG] = "all"  // 모든 복제본이 기록할 때까지 기다림

val producer = KafkaProducer<String, String>(props)
val record = ProducerRecord("my-topic", "key", "value")

// 메시지 전송 후 콜백으로 결과 처리
producer.send(record) { metadata, exception ->
    if (exception != null) {
        println("Error sending message: ${exception.message}")
    } else {
        println("Message sent successfully to partition ${metadata.partition()}, offset ${metadata.offset()}")
    }
}

producer.close()
```

위 코드에서 \*\*`acks=all`\*\*로 설정하였기 때문에, 프로듀서는 **모든 복제본이 메시지를 기록**한 후에만 메시지 전송이 완료됩니다. 이는 **데이터의 내구성을 보장**하는 방법입니다.

***

### `acks`와 데이터 내구성의 관계

* **내구성 보장**: \*\*`acks=all`\*\*은 데이터 내구성을 가장 강력하게 보장합니다. 프로듀서는 리더와 모든 팔로워가 메시지를 기록한 후에 응답을 받기 때문에, 장애가 발생하더라도 데이터가 손실되지 않습니다.
* **성능과 신뢰성의 균형**: \*\*`acks=1`\*\*은 성능과 신뢰성 사이에서 **균형**을 찾는 설정입니다. 리더가 메시지를 수신하고 기록하면 곧바로 응답을 받으므로, 팔로워 복제가 완료되기 전에 리더가 장애가 발생하면 일부 데이터가 손실될 수 있습니다.
* **빠른 성능, 낮은 신뢰성**: \*\*`acks=0`\*\*은 메시지 전송 속도를 극대화하지만, 메시지가 유실될 위험이 있습니다. 이 옵션은 매우 **빠른 성능**을 요구하지만, 메시지 손실에 대한 신뢰성은 필요하지 않은 환경에서 적합합니다.

## Partioner

**Kafka Partitioner**는 Kafka 프로듀서가 메시지를 전송할 때, 해당 메시지를 **어느 파티션에 저장할지 결정하는 역할**을 합니다.&#x20;

토픽(Topic)은 여러 개의 파티션(Partition)으로 나누어지며, Partitioner는 메시지가 특정 파티션에 분배되도록 합니다. 이를 통해 Kafka는 **데이터 분산**과 **확장성**을 효과적으로 관리할 수 있습니다.

### **작동 원리**

**2.1 메시지에 키(Key)가 있는 경우:**

메시지에 **키**가 포함되어 있으면, Kafka는 이 **키를 기반으로 해시 함수**를 적용하여 특정 파티션을 선택합니다. **같은 키를 가진 메시지**는 항상 **동일한 파티션**에 저장되므로, 파티션 내에서 **메시지 순서가 보장**됩니다.&#x20;

**2.2 메시지에 키가 없는 경우:**

메시지에 키가 없을 때는, Kafka는 **라운드 로빈 방식** 또는 기타 기본 분배 규칙을 통해 메시지를 **균등하게 파티션에 분배**합니다. 이는 모든 파티션에 데이터가 고르게 분산되도록 하여, Kafka 클러스터의 **부하 분산**을 돕습니다. 이 방식에서는 **메시지 순서 보장이 필요하지 않은 경우**에 적합합니다.

### **커스텀 파티셔너(Custom Partitioner)**

Kafka는 기본 파티셔너 외에도 **사용자 정의(Custom) 파티셔너**를 구현할 수 있도록 지원합니다.&#x20;

```java
public class CustomPartitioner implements Partitioner {
    @Override
    public void configure(Map<String, ?> configs) {
        // 설정 초기화
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int partitionCount = cluster.partitionCountForTopic(topic);
        
        // 특정 키를 가진 메시지는 파티션 0에 할당
        if (key.equals("VIP")) {
            return 0;
        } else {
            return (key.hashCode() & Integer.MAX_VALUE) % partitionCount;
        }
    }

    @Override
    public void close() {
        // 파티셔너 종료 시 필요한 작업
    }
}
```

### **Partitioner의 필요성**

Partitioner는 다음과 같은 중요한 이유로 Kafka에서 필수적인 역할을 합니다

**데이터 일관성 및 순서 보장**: 같은 키를 가진 메시지를 **동일한 파티션**에 저장하여, **파티션 내에서 순서**를 보장할 수 있습니다. 이는 예를 들어 사용자 ID를 기준으로 사용자의 활동을 추적하는 경우 유용합니다.

**효율적인 부하 분산**: 키가 없는 메시지는 **균등하게 분산**되어, 클러스터의 부하를 고르게 나누어 처리 성능을 최적화할 수 있습니다.

**커스터마이징 가능**: 비즈니스 요구에 따라 **특정 조건에 맞게 데이터 분배 방식**을 설정할 수 있습니다. 이는 데이터 처리의 유연성을 높여줍니다.
