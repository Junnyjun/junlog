# Kafka

스프링 카프카(Spring Kafka)는 스프링 프레임워크와 Apache Kafka를 통합하여 메시지 기반 애플리케이션을 쉽게 개발할 수 있도록 지원하는 프로젝트입니다. Apache Kafka는 대용량의 실시간 데이터 스트리밍을 처리하기 위한 분산형 메시징 시스템으로, 데이터 파이프라인과 스트리밍 애플리케이션 구축에 널리 사용됩니다.

1. **간편한 설정과 구성**: 스프링의 설정 방식을 활용하여 Kafka 프로듀서와 컨슈머를 손쉽게 구성할 수 있습니다. YAML 또는 프로퍼티 파일을 통해 필요한 설정을 직관적으로 적용할 수 있습니다.
2. **KafkaTemplate 제공**: `KafkaTemplate` 클래스를 통해 메시지 발행을 간소화하여 생산자 코드를 단순화할 수 있습니다.
3. **@KafkaListener 지원**: 어노테이션 기반의 리스너를 사용하여 컨슈머를 구현하고, 특정 토픽의 메시지를 비동기로 수신할 수 있습니다.
4. **트랜잭션 관리**: Kafka와 데이터베이스 간의 트랜잭션을 통합하여 데이터 일관성을 보장하고, 메시지 처리의 원자성을 유지할 수 있습니다.
5. **에러 처리 및 재시도 메커니즘**: 메시지 처리 중 발생하는 예외 상황에 대한 체계적인 에러 처리와 재시도 로직을 제공하여 안정성을 높입니다.
6. **모니터링과 관리**: 스프링의 Actuator와 통합하여 Kafka 관련 메트릭을 모니터링하고 관리할 수 있습니다.

_프로듀서 설정_

```kotlin
@EnableKafka
@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaTemplate():KafkaTemplate<String, Any> = KafkaTemplate(kafkaProperties())

    @Bean
    fun kafkaProperties(): DefaultKafkaProducerFactory<String, Any> = DefaultKafkaProducerFactory<String,Any>(mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "{IP}",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
        ProducerConfig.ACKS_CONFIG to "all",
        ConsumerConfig.GROUP_ID_CONFIG to "kafka-group"
        ))
}
```

_컨슈머 설정_

```kotlin
@EnableKafka
@Configuration
class KafkaConsumerConfiguration {

    @Bean
    fun kafkaConsumerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
        ConcurrentKafkaListenerContainerFactory<String, Any>()
            .also {
                it.setConcurrency(10)
                it.consumerFactory = consumerFactory()
                it.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
                it.containerProperties.listenerTaskExecutor = executor()
            }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> = DefaultKafkaConsumerFactory(consumerConfig())

    @Bean
    fun executor(): ThreadPoolTaskExecutor = ThreadPoolTaskExecutor()
        .also {
            it.corePoolSize = 10
            it.maxPoolSize = 200
            it.queueCapacity = 250
            it.setThreadFactory(CustomizableThreadFactory("kafka-thread")) // 이름 prefix
        }


    @Bean
    fun consumerConfig() = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "{IP}",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
        ConsumerConfig.GROUP_ID_CONFIG to "kafka-group",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
        ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to "1000"
    )
}
```

