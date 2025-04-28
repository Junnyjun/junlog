# 채널 어댑터와 메시징 게이트웨이

현대 기업 환경에서는 다양한 시스템과 애플리케이션 간의 통합이 필수적입니다. \
이러한 통합 요구는 다음과 같은 여러 요인에서 비롯됩니다:

* 레거시 시스템과 현대 애플리케이션의 공존
* 다양한 벤더 솔루션 사용
* 마이크로서비스 아키텍처에서의 서비스 간 통신
* 클라우드 서비스와의 통합 필요성
* 파트너 시스템과의 데이터 교환

Spring Integration은 이러한 다양한 외부 시스템과의 통합을 위해 채널 어댑터(Channel Adapter)와 메시징 게이트웨이(Messaging Gateway)라는 두 가지 핵심 메커니즘을 제공합니다.

### 채널 어댑터(Channel Adapter)

채널 어댑터는 Spring Integration의 메시징 채널과 외부 시스템 사이의 브리지 역할을 합니다. 채널 어댑터는 크게 두 가지 유형으로 나뉩니다:

#### 인바운드 채널 어댑터(Inbound Channel Adapter)

인바운드 채널 어댑터는 외부 시스템에서 데이터를 가져와 Spring Integration 메시징 채널로 전송합니다. \
이는 단방향 통신으로, 외부 시스템으로부터 데이터를 "수신"하는 역할을 담당합니다.

```java
@Bean
public IntegrationFlow fileReadingFlow() {
    return IntegrationFlows
        .from(Files.inboundAdapter(new File("/path/to/input"))
              .patternFilter("*.txt")
              .preventDuplicates(true),
              e -> e.poller(Pollers.fixedDelay(1000)))
        .transform(Files.toStringTransformer())
        .channel("fileContentChannel")
        .get();
}
```

위 예제는 파일 시스템 디렉토리를 모니터링하여 새로운 텍스트 파일이 생성되면 그 내용을 읽어 메시지로 변환하는 인바운드 채널 어댑터를 보여줍니다.

#### 아웃바운드 채널 어댑터(Outbound Channel Adapter)

아웃바운드 채널 어댑터는 Spring Integration 메시징 채널에서 데이터를 받아 외부 시스템으로 전송합니다. 이 역시 단방향 통신으로, 메시지를 외부 시스템으로 "전송"하는 역할을 담당합니다.

```java
@Bean
public IntegrationFlow jmsOutboundFlow() {
    return IntegrationFlows
        .from("orderProcessedChannel")
        .handle(Jms.outboundAdapter(connectionFactory)
               .destination("processedOrders")
               .configureJmsTemplate(t -> t.deliveryPersistent(true)))
        .get();
}
```

이 예제는 처리된 주문 메시지를 JMS 큐로 전송하는 아웃바운드 채널 어댑터를 보여줍니다.

### 주요 채널 어댑터 유형과 구현 예제

Spring Integration은 다양한 프로토콜과 시스템을 위한 풍부한 채널 어댑터 세트를 제공합니다. 가장 많이 사용되는 어댑터들을 살펴보겠습니다:

#### 파일 시스템 어댑터

파일 시스템에서 파일을 읽거나 파일로 쓰는 작업을 수행합니다.

```java
// 인바운드 파일 어댑터 (파일 → 메시지)
@Bean
public IntegrationFlow fileInboundFlow() {
    return IntegrationFlows
        .from(Files.inboundAdapter(new File("/path/to/input"))
              .patternFilter("*.csv")
              .useWatchService(true)
              .watchEvents(FileReadingMessageSource.WatchEventType.CREATE),
              e -> e.poller(Pollers.fixedDelay(5000)))
        .transform(Files.toStringTransformer(StandardCharsets.UTF_8))
        .split(splitter -> splitter.delimiters("\n").stripDelimiters(true))
        .transform(line -> {
            // CSV 라인을 객체로 변환
            String[] fields = ((String) line).split(",");
            return new Customer(fields[0], fields[1], fields[2]);
        })
        .channel("newCustomersChannel")
        .get();
}

// 아웃바운드 파일 어댑터 (메시지 → 파일)
@Bean
public IntegrationFlow fileOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .enrichHeaders(h -> h
            .headerExpression("file_name", 
                "payload.orderId + '_' + T(java.time.LocalDate).now() + '.json'"))
        .handle(Files.outboundAdapter(new File("/path/to/output"))
               .fileExistsMode(FileExistsMode.REPLACE)
               .autoCreateDirectory(true))
        .get();
}
```

#### JMS(Java Message Service) 어댑터

JMS 기반 메시징 시스템과의 통합을 지원합니다.

```java
// JMS 인바운드 어댑터 (JMS → 메시지)
@Bean
public IntegrationFlow jmsInboundFlow() {
    return IntegrationFlows
        .from(Jms.messageDrivenChannelAdapter(connectionFactory)
              .destination("incoming.orders")
              .configureListenerContainer(c -> c
                  .sessionTransacted(true)
                  .concurrentConsumers(5)))
        .transform(transformer -> {
            // JMS 메시지를 도메인 객체로 변환
            return orderDeserializer.deserialize((String) transformer.getPayload());
        })
        .channel("incomingOrdersChannel")
        .get();
}

// JMS 아웃바운드 어댑터 (메시지 → JMS)
@Bean
public IntegrationFlow jmsOutboundFlow() {
    return IntegrationFlows
        .from("outgoingOrdersChannel")
        .transform(order -> orderSerializer.serialize((Order) order))
        .handle(Jms.outboundAdapter(connectionFactory)
               .destination("outgoing.orders")
               .deliveryPersistent(true)
               .timeToLive(30000))
        .get();
}
```

#### JDBC 어댑터

관계형 데이터베이스와의 통합을 지원합니다.

```java
// JDBC 인바운드 어댑터 (데이터베이스 → 메시지)
@Bean
public IntegrationFlow jdbcInboundFlow() {
    return IntegrationFlows
        .from(Jdbc.inboundAdapter(dataSource)
              .query("SELECT * FROM orders WHERE status = 'NEW'")
              .rowMapper(new OrderRowMapper())
              .maxRows(10)
              .updateSql("UPDATE orders SET status = 'PROCESSING' WHERE id IN (:id)"),
              e -> e.poller(Pollers.fixedDelay(10000)))
        .channel("newOrdersChannel")
        .get();
}

// JDBC 아웃바운드 어댑터 (메시지 → 데이터베이스)
@Bean
public IntegrationFlow jdbcOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .handle(Jdbc.outboundAdapter(dataSource)
               .sql("UPDATE orders SET status = 'COMPLETED', " +
                    "completion_date = :completionDate WHERE id = :id")
               .sqlParameterSourceFactory(message -> {
                   Order order = (Order) message.getPayload();
                   MapSqlParameterSource params = new MapSqlParameterSource();
                   params.addValue("id", order.getId());
                   params.addValue("completionDate", new Date());
                   return params;
               }))
        .get();
}
```

#### HTTP 어댑터

HTTP/REST 서비스와의 통합을 지원합니다.

```java
// HTTP 인바운드 어댑터 (HTTP 요청 → 메시지)
@Bean
public IntegrationFlow httpInboundFlow() {
    return IntegrationFlows
        .from(Http.inboundChannelAdapter("/api/orders")
              .requestMapping(m -> m
                  .methods(HttpMethod.POST)
                  .consumes(MediaType.APPLICATION_JSON_VALUE))
              .requestPayloadType(OrderDTO.class))
        .transform(orderTransformer())
        .channel("newOrdersChannel")
        .get();
}

// HTTP 아웃바운드 어댑터 (메시지 → HTTP 요청)
@Bean
public IntegrationFlow httpOutboundFlow() {
    return IntegrationFlows
        .from("shippingRequestChannel")
        .handle(Http.outboundGateway("https://shipping-api.example.com/shipments")
               .httpMethod(HttpMethod.POST)
               .expectedResponseType(ShipmentConfirmation.class)
               .mappedRequestHeaders("orderId")
               .headerMapper(headerMapper()))
        .channel("shipmentConfirmationChannel")
        .get();
}
```

#### WebSocket 어댑터

WebSocket 프로토콜을 통한 양방향 통신을 지원합니다.

```java
@Bean
public WebSocketHandler webSocketHandler() {
    return new SubProtocolWebSocketHandler(clientInboundChannel(), clientOutboundChannel());
}

@Bean
public IntegrationFlow webSocketInboundFlow() {
    return IntegrationFlows
        .from(WebSocketInboundChannelAdapter.forSocket("/notifications"))
        .transform(new JsonToObjectTransformer(Notification.class))
        .channel("notificationsChannel")
        .get();
}

@Bean
public IntegrationFlow webSocketOutboundFlow() {
    return IntegrationFlows
        .from("systemAlertsChannel")
        .transform(new ObjectToJsonTransformer())
        .handle(new WebSocketOutboundMessageHandler("/topic/alerts"))
        .get();
}
```

#### 이메일 어댑터

이메일 서버와의 통합을 지원합니다.

```java
// 이메일 인바운드 어댑터 (이메일 → 메시지)
@Bean
public IntegrationFlow emailInboundFlow() {
    return IntegrationFlows
        .from(Mail.imapInboundAdapter("imaps://username:password@imap.gmail.com/INBOX")
              .javaMailProperties(p -> {
                  p.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                  p.put("mail.imaps.socketFactory.fallback", "false");
              })
              .shouldDeleteMessages(false)
              .simpleContent(true),
              e -> e.poller(Pollers.fixedDelay(60000)))
        .transform(Mail.toStringTransformer())
        .channel("incomingEmailsChannel")
        .get();
}

// 이메일 아웃바운드 어댑터 (메시지 → 이메일)
@Bean
public IntegrationFlow emailOutboundFlow() {
    return IntegrationFlows
        .from("orderConfirmationChannel")
        .transform(transformer -> {
            Order order = (Order) transformer.getPayload();
            
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(order.getCustomerEmail());
            mailMessage.setSubject("주문 확인: " + order.getId());
            mailMessage.setText("안녕하세요 " + order.getCustomerName() + "님,\n\n" +
                              "주문이 성공적으로 처리되었습니다.\n" +
                              "주문 ID: " + order.getId() + "\n" +
                              "주문 금액: " + order.getTotalAmount() + "\n\n" +
                              "감사합니다.");
            return mailMessage;
        })
        .handle(Mail.outboundAdapter("smtp.gmail.com")
               .port(587)
               .protocol("smtp")
               .credentials("username", "password")
               .javaMailProperties(p -> {
                   p.put("mail.smtp.auth", "true");
                   p.put("mail.smtp.starttls.enable", "true");
               }))
        .get();
}
```

#### 카프카(Kafka) 어댑터

Apache Kafka 분산 스트리밍 플랫폼과의 통합을 지원합니다.

```java
// 카프카 인바운드 어댑터 (Kafka → 메시지)
@Bean
public IntegrationFlow kafkaInboundFlow() {
    return IntegrationFlows
        .from(Kafka.messageDrivenChannelAdapter(
                consumerFactory, 
                KafkaMessageDrivenChannelAdapter.ListenerMode.record,
                "orders-topic")
              .configureListenerContainer(c -> 
                  c.ackMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE))
              .recoveryCallback(new ErrorMessageSendingRecoverer(errorChannel)))
        .transform(new JsonToObjectTransformer(Order.class))
        .channel("kafkaOrdersChannel")
        .get();
}

// 카프카 아웃바운드 어댑터 (메시지 → Kafka)
@Bean
public IntegrationFlow kafkaOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .transform(new ObjectToJsonTransformer())
        .handle(Kafka.outboundChannelAdapter(kafkaTemplate)
               .messageKey(m -> m.getHeaders().get("orderId"))
               .topic("processed-orders-topic"))
        .get();
}
```

#### AMQP(RabbitMQ) 어댑터

RabbitMQ와 같은 AMQP 기반 메시징 시스템과의 통합을 지원합니다.

```java
// AMQP 인바운드 어댑터 (RabbitMQ → 메시지)
@Bean
public IntegrationFlow amqpInboundFlow() {
    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, "orders-queue")
              .configureContainer(c -> c
                  .prefetchCount(10)
                  .defaultRequeueRejected(false)
                  .concurrentConsumers(3)))
        .transform(new JsonToObjectTransformer(Order.class))
        .channel("incomingOrdersChannel")
        .get();
}

// AMQP 아웃바운드 어댑터 (메시지 → RabbitMQ)
@Bean
public IntegrationFlow amqpOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .transform(new ObjectToJsonTransformer())
        .handle(Amqp.outboundAdapter(rabbitTemplate)
               .exchangeName("orders-exchange")
               .routingKey("order.processed")
               .confirmAckChannel("amqpConfirmAckChannel"))
        .get();
}
```

### 메시징 게이트웨이(Messaging Gateway)

메시징 게이트웨이는 외부 시스템과의 양방향 통신을 가능하게 하는 구성 요소입니다. 특히 중요한 것은 게이트웨이가 Spring Integration의 메시징 인프라와 애플리케이션 코드 사이의 "깔끔한" 인터페이스 역할을 한다는 점입니다.

게이트웨이를 사용하면:

* 애플리케이션 코드가 Spring Integration의 메시징 API에 직접 의존하지 않음
* 쉽게 테스트할 수 있는 깔끔한 POJO 기반 인터페이스 제공
* 동기 및 비동기 통신 패턴 모두 지원

#### 게이트웨이 기본 사용법

가장 기본적인 형태의 게이트웨이는 인터페이스를 정의하고 `@MessagingGateway` 어노테이션을 붙이는 것입니다:

```java
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel")
public interface OrderService {
    void processOrder(Order order);
}
```

위의 간단한 인터페이스를 통해 Spring Integration은 런타임에 프록시 구현체를 생성합니다. 이 구현체는 `processOrder` 메서드가 호출될 때 `Order` 객체를 메시지 페이로드로 변환하고, 이를 `orderProcessingChannel`로 전송합니다.

#### 응답이 있는 게이트웨이

응답이 필요한 경우 메서드의 반환 타입을 명시적으로 정의할 수 있습니다:

```java
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel",
                  defaultReplyChannel = "orderConfirmationChannel")
public interface OrderService {
    OrderConfirmation processOrder(Order order);
}
```

이 경우, Spring Integration은:

1. `Order` 객체를 메시지로 변환하여 `orderProcessingChannel`로 전송
2. `orderConfirmationChannel`에서 응답 메시지를 기다림
3. 응답 메시지의 페이로드를 `OrderConfirmation` 타입으로 변환하여 반환

#### 고급 게이트웨이 기능

게이트웨이는 다양한 고급 기능을 제공합니다:

**1. 특정 메서드에 대한 채널 오버라이드**

```java
@MessagingGateway(defaultRequestChannel = "standardOrdersChannel")
public interface OrderService {
    OrderConfirmation processOrder(Order order);
    
    @Gateway(requestChannel = "priorityOrdersChannel",
             replyTimeout = 10000)
    OrderConfirmation processPriorityOrder(Order order);
    
    @Gateway(requestChannel = "bulkOrdersChannel")
    void processBulkOrder(List<Order> orders);
}
```

**2. 비동기 처리**

`Future`, `CompletableFuture` 또는 Project Reactor의 /를 반환 타입으로 사용하여 비동기 처리를 구현할 수 있습니다: `MonoFlux`

```java
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel")
public interface OrderService {
    // Java의 Future 사용
    Future<OrderConfirmation> processOrderAsync(Order order);
    
    // CompletableFuture 사용
    CompletableFuture<OrderConfirmation> processOrderWithCompletableFuture(Order order);
    
    // Reactor의 Mono 사용
    Mono<OrderConfirmation> processOrderReactive(Order order);
    
    // 스트림 처리에 Flux 사용
    @Gateway(requestChannel = "bulkOrdersChannel")
    Flux<OrderConfirmation> processBulkOrdersReactive(List<Order> orders);
}
```

**3. 헤더 조작**

페이로드 외에도 메시지 헤더를 조작할 수 있습니다:

```java
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel")
public interface OrderService {
    // 매개변수를 헤더로 매핑
    @Gateway(requestChannel = "orderProcessingChannel")
    OrderConfirmation processOrder(
        Order order, 
        @Header("region") String region,
        @Header("priority") int priority);
    
    // 맵을 통한 여러 헤더 전송
    @Gateway(requestChannel = "orderProcessingChannel")
    OrderConfirmation processOrderWithHeaders(
        Order order, 
        @Headers Map<String, Object> headers);
    
    // 페이로드에서 헤더 추출
    @Gateway(requestChannel = "orderProcessingChannel")
    OrderConfirmation processOrderWithExtractedHeaders(
        Order order,
        @Header(expression = "payload.customerId") String customerId,
        @Header(expression = "payload.totalAmount > 10000 ? 'HIGH' : 'STANDARD'") String priority);
}
```

**4. 에러 처리**

게이트웨이는 에러 채널을 통해 예외 처리를 구성할 수 있습니다:

```java
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel",
                  errorChannel = "orderProcessingErrorChannel")
public interface OrderService {
    OrderConfirmation processOrder(Order order);
}

@Bean
public IntegrationFlow errorHandlingFlow() {
    return IntegrationFlows
        .from("orderProcessingErrorChannel")
        .handle(message -> {
            MessagingException exception = (MessagingException) message.getPayload();
            log.error("Error processing order: {}", exception.getMessage());
            // 에러 처리 로직
        })
        .get();
}
```

### 통합 주문 처리 시스템

다양한 채널을 통해 들어오는 주문을 처리하고, 여러 외부 시스템과 통합하는 주문 처리 시스템을 설계하겠습니다.

#### 시나리오 설명

* 웹사이트, 모바일 앱, 파트너 API를 통해 주문 수신
* 주문 데이터 검증 및 처리
* 재고 관리 시스템과 연동
* 결제 게이트웨이와 통합
* 물류 시스템으로 배송 정보 전송
* 고객에게 주문 상태 알림 발송

#### 구현

```java
@Configuration
@EnableIntegration
public class OrderProcessingIntegrationConfig {

    // 1. 다양한 채널에서 주문 수신
    @Bean
    public IntegrationFlow webOrdersFlow() {
        return IntegrationFlows
            .from(Http.inboundGateway("/api/orders")
                  .requestMapping(m -> m.methods(HttpMethod.POST))
                  .requestPayloadType(WebOrderDTO.class))
            .transform(webOrderTransformer())
            .channel("newOrdersChannel")
            .get();
    }
    
    @Bean
    public IntegrationFlow mobileOrdersFlow() {
        return IntegrationFlows
            .from(Http.inboundGateway("/api/mobile/orders")
                  .requestMapping(m -> m.methods(HttpMethod.POST))
                  .requestPayloadType(MobileOrderDTO.class))
            .transform(mobileOrderTransformer())
            .channel("newOrdersChannel")
            .get();
    }
    
    @Bean
    public IntegrationFlow partnerApiOrdersFlow() {
        return IntegrationFlows
            .from(Amqp.inboundAdapter(connectionFactory, "partner-orders-queue"))
            .transform(new JsonToObjectTransformer(PartnerOrderDTO.class))
            .transform(partnerOrderTransformer())
            .channel("newOrdersChannel")
            .get();
    }
    
    // 2. 주문 검증 및 중복 제거
    @Bean
    public IntegrationFlow orderValidationFlow() {
        return IntegrationFlows
            .from("newOrdersChannel")
            .filter(this::validateOrder, f -> f.discardChannel("invalidOrdersChannel"))
            .handle(idempotentReceiver())
            .channel("validatedOrdersChannel")
            .get();
    }
    
    // 3. 메인 주문 처리 플로우
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlows
            .from("validatedOrdersChannel")
            // 트랜잭션 시작
            .gateway(orderTransactionFlow())
            // 결과 분기
            .route(OrderResult.class, result -> result.getStatus(),
                r -> r.subFlowMapping("SUCCESS", sf -> sf
                          .channel("successfulOrdersChannel"))
                      .subFlowMapping("FAILED", sf -> sf
                          .channel("failedOrdersChannel"))
                      .subFlowMapping("PENDING", sf -> sf
                          .channel("pendingOrdersChannel")))
            .get();
    }
    
    // 4. 트랜잭션 처리 서브플로우
    @Bean
    public IntegrationFlow orderTransactionFlow() {
        return flow -> flow
            // 트랜잭션 경계 설정
            .transactional()
            // 재고 확인
            .handle(inventoryGateway(), "checkInventory")
            .filter(available -> (boolean) available, 
                   f -> f.discardFlow(df -> df
                        .transform(order -> new OrderResult(
                            (Order) order, "FAILED", "재고 부족")))
                   )
            // 결제 처리
            .handle(paymentGateway(), "processPayment")
            .<PaymentResult>handle((payload, headers) -> {
                PaymentResult result = (PaymentResult) payload;
                Order order = result.getOrder();
                
                if (result.isSuccessful()) {
                    orderRepository.save(order.setStatus(OrderStatus.PAID));
                    return new OrderResult(order, "SUCCESS", "결제 완료");
                } else {
                    orderRepository.save(order.setStatus(OrderStatus.PAYMENT_FAILED));
                    return new OrderResult(order, "FAILED", "결제 실패: " + result.getMessage());
                }
            })
            .get();
    }
    
    // 5. 성공한 주문 처리 (배송 및 알림)
    @Bean
    public IntegrationFlow successfulOrdersFlow() {
        return IntegrationFlows
            .from("successfulOrdersChannel")
            .transform(result -> ((OrderResult) result).getOrder())
            // 이벤트 발행 (와이어탭을 통해 메인 흐름에 영향 없이 처리)
            .wireTap(flow -> flow
                .channel("orderEventsChannel"))
            // 배송 정보 전송
            .handle(Amqp.outboundAdapter(rabbitTemplate)
                   .exchangeName("logistics-exchange")
                   .routingKey("shipment.new")
                   .messageConverter(messageConverter()))
            // 이메일 알림 전송
            .handle(customerNotificationGateway(), "sendOrderConfirmation")
            .channel("completedOrdersChannel")
            .get();
    }
    
    // 6. 주문 이벤트 처리 (분석, 감사 등)
    @Bean
    public IntegrationFlow orderEventsFlow() {
        return IntegrationFlows
            .from("orderEventsChannel")
            .publishSubscribeChannel(c -> c
                // 분석 데이터 전송
                .subscribe(s -> s
                    .handle(Kafka.outboundChannelAdapter(kafkaTemplate)
                        .topic("order-events")))
                // 감사 로그 저장
                .subscribe(s -> s
                    .handle(auditService(), "logOrderEvent"))
                // 재고 업데이트
                .subscribe(s -> s
                    .handle(inventoryService(), "updateInventory")))
            .get();
    }
    
    // 7. 게이트웨이 정의
    @MessagingGateway(defaultRequestChannel = "inventoryRequestChannel",
                      defaultReplyChannel = "inventoryResponseChannel")
    public interface InventoryGateway {
        boolean checkInventory(Order order);
    }
    
    @MessagingGateway(defaultRequestChannel = "paymentRequestChannel",
                      defaultReplyChannel = "paymentResponseChannel")
    public interface PaymentGateway {
        PaymentResult processPayment(Order order);
    }
    
    @MessagingGateway(defaultRequestChannel = "notificationRequestChannel")
    public interface CustomerNotificationGateway {
        void sendOrderConfirmation(Order order);
    }
    
    // 8. 재고 확인 플로우
    @Bean
    public IntegrationFlow inventoryFlow() {
        return IntegrationFlows
            .from("inventoryRequestChannel")
            .handle(Http.outboundGateway("http://inventory-service/api/check")
                   .httpMethod(HttpMethod.POST)
                   .expectedResponseType(InventoryResponse.class))
            .transform(response -> ((InventoryResponse) response).isAvailable())
            .channel("inventoryResponseChannel")
            .get();
    }
    
    // 9. 결제 처리 플로우
    @Bean
    public IntegrationFlow paymentFlow() {
        return IntegrationFlows
            .from("paymentRequestChannel")
            .transform(order -> {
                // 주문을 결제 요청으로 변환
                PaymentRequest request = new PaymentRequest();
                request.setOrderId(((Order) order).getId());
                request.setAmount(((Order) order).getTotalAmount());
                request.setCustomerId(((Order) order).getCustomerId());
                request.setCardDetails(((Order) order).getPaymentDetails());
                return request;
            })
            .handle(
```
