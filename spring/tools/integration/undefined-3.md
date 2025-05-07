# 메시징 엔드포인트

### 메시징 엔드포인트의 개념

메시징 엔드포인트는 메시지 채널과 실제 비즈니스 로직 사이의 연결점 역할을 합니다. Spring Integration은 다양한 유형의 메시징 엔드포인트를 제공하여 메시지의 변환, 라우팅, 분할, 집계 등 여러 통합 패턴을 구현할 수 있게 합니다.

### 채널 어댑터(Channel Adapters)

채널 어댑터는 Spring Integration 시스템과 외부 시스템 사이의 연결을 담당합니다.

#### 인바운드 채널 어댑터(Inbound Channel Adapters)

외부 시스템에서 메시지를 수신하여 Spring Integration 채널로 전달합니다.

```
@Bean
public IntegrationFlow fileReadingFlow() {
    return IntegrationFlows
        .from(Files.inboundAdapter(new File("/path/to/input/directory"))
                .patternFilter("*.txt")
                .preventDuplicates(true),
              e -> e.poller(Pollers.fixedRate(5000)))
        .transform(Files.toStringTransformer())
        .channel("fileContentChannel")
        .get();
}
```

#### 아웃바운드 채널 어댑터(Outbound Channel Adapters)

Spring Integration 채널에서 메시지를 수신하여 외부 시스템으로 전달합니다.

```
@Bean
public IntegrationFlow fileWritingFlow() {
    return IntegrationFlows
        .from("fileOutputChannel")
        .handle(Files.outboundAdapter(new File("/path/to/output/directory"))
                .fileNameGenerator(message -> "output_" + System.currentTimeMillis() + ".txt")
                .autoCreateDirectory(true))
        .get();
}
```

### 메시지 핸들러(Message Handlers)

메시지 핸들러는 채널에서 메시지를 수신하여 처리하는 컴포넌트입니다.

#### 서비스 액티베이터(Service Activators)

비즈니스 로직을 수행하는 서비스를 호출하는 엔드포인트입니다.

```
@Bean
public IntegrationFlow serviceActivatorFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .handle("orderService", "processOrder")
        .channel("outputChannel")
        .get();
}

// 또는 람다 표현식 사용
@Bean
public IntegrationFlow lambdaServiceActivatorFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .handle((payload, headers) -> {
            Order order = (Order) payload;
            return orderProcessor.process(order);
        })
        .channel("outputChannel")
        .get();
}
```

#### 트랜스포머(Transformers)

메시지의 내용이나 구조를 변환하는 엔드포인트입니다.

```
@Bean
public IntegrationFlow transformerFlow() {
    return IntegrationFlows
        .from("inputChannel")
        // 객체를 JSON으로 변환
        .transform(new ObjectToJsonTransformer())
        // 헤더 추가
        .enrichHeaders(h -> h
            .header("Content-Type", "application/json")
            .headerExpression("timestamp", "T(java.lang.System).currentTimeMillis()"))
        .channel("outputChannel")
        .get();
}
```

#### 필터(Filters)

조건에 맞는 메시지만 다음 단계로 전달하는 엔드포인트입니다.

```
@Bean
public IntegrationFlow filterFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .filter(payload -> {
            Order order = (Order) payload;
            // 금액이 1000 이상인 주문만 처리
            return order.getAmount().compareTo(new BigDecimal("1000")) >= 0;
        }, f -> f.discardChannel("smallOrdersChannel"))
        .channel("largeOrdersChannel")
        .get();
}
```

#### 라우터(Routers)

조건에 따라 메시지를 다른 채널로 라우팅하는 엔드포인트입니다.

```
@Bean
public IntegrationFlow routerFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .<Order, String>route(order -> {
            if (order.getType() == OrderType.RETAIL) {
                return "retailChannel";
            } else if (order.getType() == OrderType.WHOLESALE) {
                return "wholesaleChannel";
            } else {
                return "otherOrdersChannel";
            }
        }, mapping -> mapping
            .subFlowMapping("retailChannel", sf -> sf
                .handle("retailOrderProcessor", "process")
                .channel("processedRetailChannel"))
            .subFlowMapping("wholesaleChannel", sf -> sf
                .handle("wholesaleOrderProcessor", "process")
                .channel("processedWholesaleChannel"))
            .defaultSubFlowMapping(sf -> sf
                .handle("defaultOrderProcessor", "process")
                .channel("processedOtherChannel")))
        .get();
}
```

#### 스플리터(Splitters)

하나의 메시지를 여러 개의 메시지로 분할하는 엔드포인트입니다.

```
@Bean
public IntegrationFlow splitterFlow() {
    return IntegrationFlows
        .from("batchOrdersChannel")
        .split(orderBatch -> {
            // 주문 배치를 개별 주문으로 분할
            List<Order> orders = ((OrderBatch) orderBatch).getOrders();
            return orders;
        })
        .channel("individualOrdersChannel")
        .get();
}
```

#### 애그리게이터(Aggregators)

여러 개의 메시지를 하나의 메시지로 결합하는 엔드포인트입니다.

```
@Bean
public IntegrationFlow aggregatorFlow() {
    return IntegrationFlows
        .from("individualResultsChannel")
        .<ProcessingResult, String>aggregate(aggregator -> aggregator
            .correlationStrategy(message -> {
                // 주문 ID로 상관관계 설정
                ProcessingResult result = (ProcessingResult) message.getPayload();
                return result.getOrderId();
            })
            .releaseStrategy(group -> {
                // 모든 아이템이 처리되면 릴리스
                return group.size() == group.getSequenceSize();
            })
            .expireGroupsUponCompletion(true)
            .groupTimeout(30000) // 30초 타임아웃
            .sendPartialResultOnExpiry(true)
            .outputProcessor(group -> {
                // 결과 합치기
                List<ProcessingResult> results = group.getMessages()
                    .stream()
                    .map(message -> (ProcessingResult) message.getPayload())
                    .collect(Collectors.toList());
                
                return new AggregatedResult(group.getGroupId(), results);
            })
        )
        .channel("aggregatedResultsChannel")
        .get();
}
```

### 게이트웨이(Gateways)

애플리케이션 코드에서 메시징 시스템에 접근할 수 있는 인터페이스를 제공합니다.

```
@MessagingGateway
public interface OrderService {
    @Gateway(requestChannel = "ordersChannel", replyChannel = "orderResultsChannel")
    OrderConfirmation submitOrder(Order order);
    
    @Gateway(requestChannel = "orderStatusRequestChannel", replyChannel = "orderStatusChannel")
    OrderStatus checkOrderStatus(String orderId);
}
```

사용 예:

```
@Service
public class OrderManagementService {
    
    private final OrderService orderService;
    
    @Autowired
    public OrderManagementService(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public OrderConfirmation processNewOrder(Order order) {
        // 메시징 시스템을 통해 주문 처리
        return orderService.submitOrder(order);
    }
    
    public OrderStatus getOrderStatus(String orderId) {
        return orderService.checkOrderStatus(orderId);
    }
}
```

### 브릿지(Bridges)

서로 다른 채널 간에 메시지를 전달하는 간단한 엔드포인트입니다.

```
@Bean
public IntegrationFlow bridgeFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .bridge(e -> e.poller(Pollers.fixedRate(1000)))
        .channel("outputChannel")
        .get();
}
```

### 채널 인터셉터(Channel Interceptors)

채널을 통과하는 메시지를 가로채어 추가 처리를 수행할 수 있습니다.

```
@Bean
public MessageChannel auditedChannel() {
    DirectChannel channel = new DirectChannel();
    channel.addInterceptor(loggingInterceptor());
    return channel;
}

@Bean
public ChannelInterceptor loggingInterceptor() {
    return new ChannelInterceptorAdapter() {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            // 전송 전 로깅
            log.info("Sending message: {}", message);
            return message;
        }
        
        @Override
        public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
            // 전송 후 로깅
            log.info("Message sent: {}, success: {}", message, sent);
        }
        
        @Override
        public void afterSendCompletion(Message<?> message, MessageChannel channel, 
                                       boolean sent, Exception ex) {
            if (ex != null) {
                // 예외 발생 시 로깅
                log.error("Error sending message: {}", message, ex);
            }
        }
    };
}
```

### 메시징 템플릿(Messaging Templates)

프로그래매틱하게 메시지를 보내고 받을 수 있는 편리한 방법을 제공합니다.

```
@Service
public class OrderNotificationService {
    
    private final MessagingTemplate messagingTemplate;
    
    @Autowired
    public OrderNotificationService(MessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void notifyOrderProcessed(Order order) {
        // 메시지 생성
        Message<Order> message = MessageBuilder
            .withPayload(order)
            .setHeader("notificationType", "ORDER_PROCESSED")
            .setHeader("timestamp", System.currentTimeMillis())
            .build();
        
        // 채널로 메시지 전송
        messagingTemplate.send("notificationChannel", message);
    }
    
    public OrderStatus getOrderStatus(String orderId) {
        // 요청 메시지 생성
        Message<String> requestMessage = MessageBuilder
            .withPayload(orderId)
            .build();
        
        // 요청 전송 및 응답 수신
        Message<?> responseMessage = messagingTemplate.sendAndReceive(
            "orderStatusRequestChannel", requestMessage);
        
        return (OrderStatus) responseMessage.getPayload();
    }
}
```

### 채널 어댑터와 엔드포인트 종류

#### 파일 시스템

파일 시스템과의 통합을 위한 어댑터를 제공합니다.

```
@Bean
public IntegrationFlow fileReadingFlow() {
    return IntegrationFlows
        .from(Files.inboundAdapter(new File("/path/to/input"))
                .patternFilter("*.csv")
                .preventDuplicates(true),
              e -> e.poller(Pollers.fixedRate(5000)))
        .transform(Files.toStringTransformer())
        .transform(new CsvToOrderTransformer())
        .channel("newOrdersChannel")
        .get();
}

@Bean
public IntegrationFlow fileWritingFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .transform(new OrderToCsvTransformer())
        .handle(Files.outboundAdapter(new File("/path/to/output"))
                .fileNameGenerator(message -> {
                    Order order = (Order) message.getPayload();
                    return "order_" + order.getId() + ".csv";
                })
                .autoCreateDirectory(true))
        .get();
}
```

#### JMS

JMS 메시징 시스템과의 통합을 위한 어댑터를 제공합니다.

```
@Bean
public IntegrationFlow jmsInboundFlow() {
    return IntegrationFlows
        .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                .destination("orderQueue")
                .configureListenerContainer(c -> c.sessionTransacted(true)))
        .transform(new JmsMessageToOrderTransformer())
        .channel("orderProcessingChannel")
        .get();
}

@Bean
public IntegrationFlow jmsOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .handle(Jms.outboundAdapter(connectionFactory)
                .destination("processedOrderQueue"))
        .get();
}
```

#### AMQP (RabbitMQ)

AMQP 프로토콜을 지원하는 메시징 시스템과의 통합을 위한 어댑터를 제공합니다.

```
@Bean
public IntegrationFlow amqpInboundFlow() {
    return IntegrationFlows
        .from(Amqp.inboundAdapter(connectionFactory, "orderQueue")
                .configureContainer(c -> c.prefetchCount(10)))
        .transform(new AmqpMessageToOrderTransformer())
        .channel("orderProcessingChannel")
        .get();
}

@Bean
public IntegrationFlow amqpOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .handle(Amqp.outboundAdapter(rabbitTemplate)
                .exchangeName("orders")
                .routingKey("processed"))
        .get();
}
```

#### HTTP

HTTP 기반 통신을 위한 어댑터를 제공합니다.

```
@Bean
public IntegrationFlow httpInboundFlow() {
    return IntegrationFlows
        .from(Http.inboundGateway("/orders")
                .requestMapping(r -> r.methods(HttpMethod.POST)
                                     .consumes("application/json"))
                .requestPayloadType(Order.class))
        .channel("orderProcessingChannel")
        .get();
}

@Bean
public IntegrationFlow httpOutboundFlow() {
    return IntegrationFlows
        .from("inventoryCheckChannel")
        .handle(Http.outboundGateway("https://inventory-service/check")
                .httpMethod(HttpMethod.POST)
                .expectedResponseType(InventoryStatus.class))
        .channel("inventoryStatusChannel")
        .get();
}
```

#### WebSocket

WebSocket 통신을 위한 어댑터를 제공합니다.

```
@Bean
public IntegrationFlow webSocketOutboundFlow() {
    return IntegrationFlows
        .from("notificationChannel")
        .transform(new OrderStatusToWebSocketMessageTransformer())
        .handle(new WebSocketHandler())
        .get();
}

public class WebSocketHandler implements MessageHandler {

    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public WebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    public void handleMessage(Message<?> message) {
        String payload = (String) message.getPayload();
        String userId = (String) message.getHeaders().get("userId");
        
        // WebSocket으로 메시지 전송
        messagingTemplate.convertAndSendToUser(
            userId, "/queue/notifications", payload);
    }
}
```
