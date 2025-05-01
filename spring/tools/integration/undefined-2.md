# 메시지 흐름 제어와 오류 처리

기업 애플리케이션에서는 메시지 처리 과정에서 다양한 상황이 발생할 수 있습니다. 처리량이 갑자기 증가하거나, 외부 시스템이 응답하지 않거나, 예상치 못한 데이터가 유입될 수 있습니다. 따라서 메시지 흐름을 효과적으로 제어하고 오류 상황에 적절히 대응하는 것은 안정적인 통합 시스템 구축의 핵심입니다.

Spring Integration은 이러한 상황을 처리하기 위한 다양한 메커니즘을 제공합니다. 이번 챕터에서는 메시지 흐름 제어와 오류 처리 전략에 대해 자세히 알아보겠습니다.

### 메시지 흐름 제어 메커니즘

#### 폴러(Poller) 구성

폴러는 채널에서 메시지를 가져오는 빈도와 방식을 제어합니다. 특히 과 같은 폴링 채널을 사용할 때 중요합니다. `QueueChannel`

```java
@Bean
public IntegrationFlow pollingFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .channel(c -> c.queue("processingQueue"))
        .handle("orderProcessor", "processOrder", e -> e.poller(
            Pollers.fixedRate(1000)  // 1초마다 폴링
                .maxMessagesPerPoll(10)  // 한 번에 최대 10개 메시지 처리
                .errorChannel("pollingErrorChannel")  // 폴링 중 오류 발생시 전달할 채널
                .taskExecutor(taskExecutor())  // 폴링 작업에 사용할 스레드 풀
        ))
        .channel("outputChannel")
        .get();
}

@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("order-processor-");
    executor.initialize();
    return executor;
}
```

#### 스로틀링(Throttling)

메시지 처리 속도를 제한하여 시스템 과부하를 방지합니다:

```java
@Bean
public MessageChannelInterceptor throttlingInterceptor() {
    ChannelInterceptorAdapter interceptor = new ChannelInterceptorAdapter() {
        private final RateLimiter rateLimiter = RateLimiter.create(10); // 초당 10개 메시지로 제한
        
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            rateLimiter.acquire(); // 토큰 획득 전까지 블로킹
            return super.preSend(message, channel);
        }
    };
    return interceptor;
}

@Bean
public DirectChannel throttledChannel() {
    DirectChannel channel = new DirectChannel();
    channel.addInterceptor(throttlingInterceptor());
    return channel;
}
```

#### 회로 차단기(Circuit Breaker)

외부 시스템에 문제가 발생했을 때 지속적인 연결 시도를 방지합니다:

```java
@Bean
public IntegrationFlow circuitBreakerFlow() {
    return IntegrationFlows
        .from("requestChannel")
        .handle(new CircuitBreakerHandlerAdapter(
            new HttpRequestExecutingMessageHandler("https://api.example.com/process"),
            circuitBreakerFactory()))
        .channel("responseChannel")
        .get();
}

// CircuitBreaker 어댑터 구현
public class CircuitBreakerHandlerAdapter extends AbstractReplyProducingMessageHandler {
    
    private final AbstractReplyProducingMessageHandler delegate;
    private final CircuitBreaker circuitBreaker;
    
    public CircuitBreakerHandlerAdapter(
            AbstractReplyProducingMessageHandler delegate, 
            CircuitBreakerFactory circuitBreakerFactory) {
        this.delegate = delegate;
        this.circuitBreaker = circuitBreakerFactory.create("apiService");
    }
    
    @Override
    protected Object handleRequestMessage(Message<?> message) {
        return circuitBreaker.run(() -> delegate.handleRequestMessage(message),
                throwable -> handleFailure(throwable, message));
    }
    
    private Object handleFailure(Throwable throwable, Message<?> message) {
        // 대체 응답 생성 또는 오류 처리
        log.error("Circuit breaker triggered for message: {}", message, throwable);
        return MessageBuilder.withPayload(new ServiceUnavailableResponse())
                .copyHeaders(message.getHeaders())
                .build();
    }
}
```

#### 흐름 동적 제어

조건에 따라 메시지 흐름을 동적으로 제어할 수 있습니다:

```java
@Bean
public IntegrationFlow dynamicControlFlow() {
    return IntegrationFlows
        .from("inputChannel")
        // 조건부 게이트웨이: 처리량이 임계값 이하일 때만 처리
        .gateway(m -> metricsService.getCurrentThroughput() <= maxThroughput,
                 g -> g.transform(this::processMessage)
                       .channel("outputChannel"),
                 // 처리량 초과시 지연 메시지로 변환
                 g -> g.transform(m -> {
                          DelayedMessage delayed = new DelayedMessage(m);
                          log.info("Rate limiting applied, message delayed: {}", m.getHeaders().getId());
                          return delayed;
                      })
                       .channel("delayedMessagesChannel"))
        .get();
}

// 지연된 메시지 재처리
@Bean
public IntegrationFlow delayedMessageFlow() {
    return IntegrationFlows
        .from(delayedMessagesSource())
        .transform(DelayedMessage::getOriginalMessage)
        .channel("inputChannel")  // 재시도를 위해 원래 채널로 전송
        .get();
}

@Bean
@InboundChannelAdapter(value = "delayedMessagesChannel", poller = @Poller(fixedDelay = "5000"))
public MessageSource<?> delayedMessagesSource() {
    return () -> {
        if (metricsService.getCurrentThroughput() <= maxThroughput * 0.8) {
            // 처리량이 충분히 낮아졌을 때 지연된 메시지 처리
            return delayedMessagesQueue.poll();
        }
        return null;
    };
}
```

### 오류 처리 전략

Spring Integration은 메시지 처리 과정에서 발생하는 다양한 오류를 처리하기 위한 여러 메커니즘을 제공합니다.

#### 오류 채널(Error Channel)

기본적으로 모든 처리 오류는 `errorChannel`이라는 이름의 글로벌 오류 채널로 전송됩니다:

```java
@Bean
public IntegrationFlow errorHandlingFlow() {
    return IntegrationFlows
        .from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
        .handle(message -> {
            MessagingException exception = (MessagingException) message.getPayload();
            Message<?> failedMessage = exception.getFailedMessage();
            
            log.error("메시지 처리 오류: [{}], 메시지: [{}]", 
                      exception.getMessage(), 
                      failedMessage.getPayload(),
                      exception);
            
            // 오류 유형별 처리
            if (exception.getCause() instanceof DataAccessException) {
                // 데이터베이스 관련 오류
                dataAccessErrorHandler.handleError(failedMessage, exception);
            } else if (exception.getCause() instanceof HttpClientErrorException) {
                // HTTP 클라이언트 오류 (4xx)
                httpClientErrorHandler.handleError(failedMessage, exception);
            } else if (exception.getCause() instanceof HttpServerErrorException) {
                // HTTP 서버 오류 (5xx)
                httpServerErrorHandler.handleError(failedMessage, exception);
            } else {
                // 기타 일반 오류
                genericErrorHandler.handleError(failedMessage, exception);
            }
        })
        .get();
}
```

#### 오류 채널 라우터(Error Channel Router)

오류를 유형별로 다른 채널로 라우팅할 수 있습니다:

```
@Bean
public IntegrationFlow errorRoutingFlow() {
    return IntegrationFlows
        .from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
        .<MessagingException, Class<?>>route(
            exception -> exception.getCause().getClass(),
            mapping -> mapping
                .subFlowMapping(ValidationException.class, sf -> sf
                    .channel("validationErrorChannel"))
                .subFlowMapping(DataAccessException.class, sf -> sf
                    .channel("dbErrorChannel"))
                .subFlowMapping(HttpClientErrorException.class, sf -> sf
                    .channel("httpClientErrorChannel"))
                .subFlowMapping(TimeoutException.class, sf -> sf
                    .channel("timeoutErrorChannel"))
                .defaultSubFlowMapping(sf -> sf
                    .channel("genericErrorChannel")))
        .get();
}
```

#### 메시지별 오류 채널 지정

특정 메시지 처리에 대한 오류 채널을 지정할 수 있습니다:

```java
@Bean
public IntegrationFlow customErrorChannelFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .enrichHeaders(h -> h
            .header(IntegrationMessageHeaderAccessor.ERROR_CHANNEL, "orderProcessingErrorChannel"))
        .handle("orderService", "processOrder")
        .channel("outputChannel")
        .get();
}

@Bean
public IntegrationFlow orderErrorHandlingFlow() {
    return IntegrationFlows
        .from("orderProcessingErrorChannel")
        .handle(message -> {
            MessagingException exception = (MessagingException) message.getPayload();
            Message<?> failedMessage = exception.getFailedMessage();
            Order order = (Order) failedMessage.getPayload();
            
            log.error("주문 처리 오류: 주문 ID [{}], 오류: [{}]", 
                      order.getId(), 
                      exception.getMessage());
            
            // 주문 상태 업데이트
            orderRepository.updateStatus(order.getId(), OrderStatus.PROCESSING_FAILED);
            
            // 고객에게 알림
            notificationService.sendOrderErrorNotification(order, exception.getMessage());
        })
        .get();
}
```

#### 재시도 메커니즘(Retry Mechanism)

일시적인 오류에 대응하기 위해 재시도 전략을 구현할 수 있습니다:

```java
@Bean
public RequestHandlerRetryAdvice retryAdvice() {
    RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
    
    RetryTemplate retryTemplate = new RetryTemplate();
    
    // 지수 백오프 정책 설정
    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(1000);  // 첫 번째 재시도 전 1초 대기
    backOffPolicy.setMultiplier(2.0);        // 대기 시간을 2배씩 증가
    backOffPolicy.setMaxInterval(60000);     // 최대 대기 시간은 1분
    retryTemplate.setBackOffPolicy(backOffPolicy);
    
    // 재시도 정책 설정
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3);  // 최대 3번 시도
    
    // 특정 예외에 대해서만 재시도
    Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
    retryableExceptions.put(ResourceAccessException.class, true);
    retryableExceptions.put(HttpServerErrorException.class, true);
    retryableExceptions.put(JmsException.class, true);
    retryableExceptions.put(DataAccessException.class, false);  // DB 오류는 재시도하지 않음
    
    retryPolicy = new SimpleRetryPolicy(3, retryableExceptions);
    retryTemplate.setRetryPolicy(retryPolicy);
    
    advice.setRetryTemplate(retryTemplate);
    
    return advice;
}

@Bean
public IntegrationFlow retryableFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .handle("externalServiceGateway", "process", 
                e -> e.advice(retryAdvice()))
        .channel("outputChannel")
        .get();
}
```

#### 회로 차단기와 폴백(Fallback) 전략

외부 서비스 호출에 문제가 있을 때 대체 전략을 사용합니다:

```java
@Bean
public IntegrationFlow serviceCallWithFallbackFlow() {
    return IntegrationFlows
        .from("requestChannel")
        .handle(message -> {
            try {
                // 주 서비스 호출 시도
                return primaryService.process(message.getPayload());
            } catch (Exception e) {
                // 실패 시 백업 서비스로 폴백
                log.warn("Primary service failed, using backup service: {}", e.getMessage());
                return backupService.process(message.getPayload());
            }
        })
        .channel("responseChannel")
        .get();
}
```

#### 데드 레터 채널(Dead Letter Channel)

처리할 수 없는 메시지를 별도로 관리합니다:

```java
@Bean
public IntegrationFlow deadLetterChannelFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .filter(payload -> {
            try {
                // 메시지 검증
                validator.validate(payload);
                return true;
            } catch (Exception e) {
                // 검증 실패한 메시지는 데드 레터 채널로 전송
                Message<?> failedMessage = MessageBuilder.withPayload(payload)
                    .setHeader("failureReason", e.getMessage())
                    .setHeader("failureTimestamp", System.currentTimeMillis())
                    .build();
                
                deadLetterChannel().send(failedMessage);
                return false;
            }
        })
        .handle("processor", "process")
        .channel("outputChannel")
        .get();
}

@Bean
public QueueChannel deadLetterChannel() {
    return new QueueChannel();
}

// 데드 레터 처리 플로우
@Bean
public IntegrationFlow deadLetterProcessingFlow() {
    return IntegrationFlows
        .from(deadLetterChannel())
        .handle(message -> {
            // 데드 레터 처리 (로깅, 데이터베이스에 저장 등)
            log.error("Dead letter received: {}, reason: {}", 
                     message.getPayload(), 
                     message.getHeaders().get("failureReason"));
            
            deadLetterRepository.save(new DeadLetter(
                message.getPayload(), 
                message.getHeaders().get("failureReason", String.class),
                new Date(message.getHeaders().get("failureTimestamp", Long.class))
            ));
        })
        .get();
}
```

### 트랜잭션 관리

Spring Integration은 메시지 처리 흐름에서 트랜잭션을 지원합니다.

#### 기본 트랜잭션 구성

```java
@Bean
public IntegrationFlow transactionalFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .transactional(transactionManager())
        .handle("orderService", "process")
        .channel("outputChannel")
        .get();
}

@Bean
public PlatformTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource);
}
```

#### 다양한 트랜잭션 속성 설정

```java
@Bean
public IntegrationFlow advancedTransactionalFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .transactional(txManager -> txManager
            .propagation(Propagation.REQUIRED)
            .isolation(Isolation.READ_COMMITTED)
            .timeout(30)  // 30초 타임아웃
            .readOnly(false)
            .transactionManager(transactionManager()))
        .split()
        .channel(c -> c.executor(taskExecutor()))
        .handle("itemProcessor", "process")
        .aggregate()
        .channel("outputChannel")
        .get();
}
```

#### 분산 트랜잭션 관리

여러 리소스(JMS, 데이터베이스 등)에 걸친 트랜잭션을 관리합니다:

```java
@Bean
public PlatformTransactionManager jtaTransactionManager() {
    JtaTransactionManager transactionManager = new JtaTransactionManager();
    transactionManager.setTransactionManagerName("java:/TransactionManager");
    return transactionManager;
}

@Bean
public IntegrationFlow jtaTransactionalFlow() {
    return IntegrationFlows
        .from(Jms.messageDrivenChannelAdapter(connectionFactory)
              .destination("incomingOrders")
              .configureListenerContainer(c -> c.sessionTransacted(true)))
        .transactional(jtaTransactionManager())
        .transform(new JsonToObjectTransformer(Order.class))
        .handle("orderService", "process")
        .handle(Jms.outboundAdapter(connectionFactory)
               .destination("processedOrders"))
        .get();
}
```

### 메시지 만료 및 시간 제한

시간 제한을 설정하여 메시지 처리의 신뢰성을 높입니다.

#### 메시지 헤더를 통한 만료 시간 설정

```java
@Bean
public IntegrationFlow expiringMessageFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .enrichHeaders(h -> h
            .headerExpression(IntegrationMessageHeaderAccessor.EXPIRATION_DATE, 
                               "T(java.lang.System).currentTimeMillis() + 60000")) // 1분 후 만료
        .channel("processChannel")  // 여기서 처리 지연 가능
        .handle((payload, headers) -> {
            Long expirationDate = headers.get(
                IntegrationMessageHeaderAccessor.EXPIRATION_DATE, Long.class);
            
            if (expirationDate != null && System.currentTimeMillis() > expirationDate) {
                // 만료된 메시지 처리
                log.warn("Message expired: {}", headers.getId());
                return MessageBuilder.withPayload(new ExpiredMessageResponse()).build();
            }
            
            // 정상 처리
            return processor.process(payload);
        })
        .channel("outputChannel")
        .get();
}
```

#### 요청-응답 시나리오의 타임아웃 설정

```java
@MessagingGateway(defaultRequestTimeout = 5000,  // 기본 5초 타임아웃
                 defaultReplyTimeout = 5000)
public interface OrderService {
    @Gateway(requestTimeout = 10000,  // 이 메서드는 10초 타임아웃
             replyTimeout = 10000)
    OrderConfirmation processOrder(Order order);
    
    // 기본 타임아웃(5초) 사용
    OrderStatus checkOrderStatus(String orderId);
}
```

### 실제 비즈니스 시나리오: 종합 오류 처리 전략

복잡한 주문 처리 시스템의 오류 처리와 흐름 제어를 종합적으로 구현해 보겠습니다.

```java
@Configuration
@EnableIntegration
public class OrderProcessingIntegrationConfig {

    // 주문 처리 메인 플로우
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlows
            .from("newOrdersChannel")
            // 메시지 ID 로깅
            .wireTap(flow -> flow.handle(message -> 
                log.info("Processing order: {}", message.getHeaders().getId())))
            // 트랜잭션 시작
            .transactional(txManager())
            // 주문 검증
            .filter(this::validateOrder, 
                   f -> f.discardChannel("invalidOrdersChannel"))
            // 재고 확인
            .handle("inventoryService", "checkAndReserveStock",
                   e -> e.advice(retryAdvice()))
            // 결제 처리
            .handle(message -> {
                Order order = (Order) message.getPayload();
                // 결제 처리 자체는 별도 트랜잭션에서 수행하기 위해 gateway 사용
                return paymentGateway.processPayment(order);
            })
            // 결제 결과에 따른 라우팅
            .<PaymentResult, PaymentStatus>route(PaymentResult::getStatus,
                mapping -> mapping
                    .subFlowMapping(PaymentStatus.APPROVED, sf -> sf
                        .transform(PaymentResult::getOrder)
                        .handle("fulfillmentService", "scheduleFulfillment",
                               e -> e.advice(circuitBreakerAdvice()))
                        .channel("completedOrdersChannel"))
                    .subFlowMapping(PaymentStatus.DECLINED, sf -> sf
                        .transform(PaymentResult::getOrder)
                        .handle((order, headers) -> {
                            log.warn("Payment declined for order: {}", ((Order) order).getId());
                            orderRepository.updateStatus(
                                ((Order) order).getId(), OrderStatus.PAYMENT_DECLINED);
                            return order;
                        })
                        .channel("declinedOrdersChannel"))
                    .subFlowMapping(PaymentStatus.ERROR, sf -> sf
                        .transform(PaymentResult::getOrder)
                        .channel("paymentErrorChannel")))
            .get();
    }
    
    // 인벤토리 서비스 호출을 위한 재시도 어드바이스
    @Bean
    public RequestHandlerRetryAdvice retryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // 지수 백오프 정책
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        // 재시도 정책 (최대 3회)
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        
        // 재시도 가능한 예외 목록
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(InventoryUnavailableException.class, true);
        retryableExceptions.put(ResourceAccessException.class, true);
        retryableExceptions.put(DataAccessException.class, true);
        
        retryPolicy = new SimpleRetryPolicy(3, retryableExceptions);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        advice.setRetryTemplate(retryTemplate);
        
        // 최종 실패 시 호출될 핸들러
        advice.setRecoveryCallback(context -> {
            Throwable lastThrowable = context.getLastThrowable();
            Order order = (Order) context.getAttribute("message");
            
            log.error("Retry exhausted for order: {}", order.getId(), lastThrowable);
            
            // 재고 부족 예외인 경우
            if (lastThrowable instanceof InventoryUnavailableException) {
                // 백오더로 처리
                return backOrderManager.createBackOrder(order);
            }
            
            // 기타 예외는 오류 처리
            throw new MessagingException("Failed to process order after retries", lastThrowable);
        });
        
        return advice;
    }
    
    // 서킷브레이커 어드바이스
    @Bean
    public CircuitBreakerFactoryBean circuitBreakerAdvice() {
        CircuitBreakerFactoryBean circuitBreakerFactoryBean = new CircuitBreakerFactoryBean();
        
        // 서킷 브레이커 구성
        circuitBreakerFactoryBean.setThreshold(5);  // 5번 실패하면 회로 개방
        circuitBreakerFactoryBean.setHalfOpenAfter(30000);  // 30초 후 반개방 상태로 변경
        
        // 회로 개방 시 대체 응답 생성
        circuitBreakerFactoryBean.setRecoveryCallback(context -> {
            Order order = (Order) context.getAttribute("message");
            
            log.warn("Circuit open, using fallback for fulfillment: {}", order.getId());
            
            // 대체 처리 로직
            DelayedFulfillmentRequest fallback = new DelayedFulfillmentRequest(order);
            fallback.setScheduledTime(new Date(System.currentTimeMillis() + 3600000)); // 1시간 후
            
            // 지연 큐에 저장
            delayedFulfillmentQueue.add(fallback);
            
            // 고객에게 지연 알림
            notificationService.sendFulfillmentDelayNotification(order);
            
            return fallback;
        });
        
        return circuitBreakerFactoryBean;
    }
    
    // 메인 오류 처리 플로우
    @Bean
    public IntegrationFlow errorHandlingFlow() {
        return IntegrationFlows
            .from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
            .transform(MessagingException.class, exception -> {
                Message<?> failedMessage = exception.getFailedMessage();
                Throwable cause = exception.getCause();
                
                // 에러 정보 생성
                ErrorDetail errorDetail = new ErrorDetail();
                errorDetail.setMessageId(failedMessage.getHeaders().getId().toString());
                errorDetail.setTimestamp(new Date());
                errorDetail.setException(cause.getClass().getName());
                errorDetail.setMessage(cause.getMessage());
                errorDetail.setPayload(failedMessage.getPayload());
                
                // 중요 헤더 복사
                Map<String, Object> relevantHeaders = new HashMap<>();
                failedMessage.getHeaders().forEach((key, value) -> {
                    if (isRelevantHeader(key)) {
                        relevantHeaders.put(key, value);
                    }
                });
                errorDetail.setRelevantHeaders(relevantHeaders);
                
                return errorDetail;
            })
            .<ErrorDetail, String>route(errorDetail -> {
                // 에러 유형에 따라 라우팅
                Throwable cause = errorDetail.getException() != null 
                    ? (Throwable) errorDetail.getException() 
                    : new RuntimeException(errorDetail.getMessage());
                
                if (cause instanceof PaymentException) {
                    return "payment";
                } else if (cause instanceof InventoryException) {
                    return "inventory";
                } else if (cause instanceof FulfillmentException) {
                    return "fulfillment";
                } else if (cause instanceof DataAccessException) {
                    return "database";
                } else if (cause instanceof ValidationException) {
                    return "validation";
                } else {
                    return "general";
                }
            }, mapping -> mapping
                .subFlowMapping("payment", sf -> sf.channel("paymentErrorChannel"))
                .subFlowMapping("inventory", sf -> sf.channel("inventoryErrorChannel"))
                .subFlowMapping("fulfillment", sf -> sf.channel("fulfillmentErrorChannel"))
                .subFlowMapping("database", sf -> sf.channel("databaseErrorChannel"))
                .subFlowMapping("validation", sf -> sf.channel("validationErrorChannel"))
                .defaultSubFlowMapping(sf -> sf.channel("generalErrorChannel")))
            .get();
    }
    
    // 결제 오류 처리 서브플로우
    @Bean
    public IntegrationFlow paymentErrorFlow() {
        return IntegrationFlows
            .from("paymentErrorChannel")
            .handle((errorDetail, headers) -> {
                log.error("Payment error: {}", errorDetail);
                
                // 원본 주문 추출
                Order order = null;
                if (errorDetail.getPayload() instanceof Order) {
                    order = (Order) errorDetail.getPayload();
                } else if (errorDetail.getPayload() instanceof PaymentResult) {
                    order = ((PaymentResult) errorDetail.getPayload()).getOrder();
                }
                
                if (order != null) {
                    // 주문 상태 업데이트
                    orderRepository.updateStatus(order.getId(), OrderStatus.PAYMENT_ERROR);
                    
                    // 오류 로그 저장
                    paymentErrorRepository.save(new PaymentError(
                        order.getId(), 
                        errorDetail.getMessage(),
                        new Date()
                    ));
                    
                    // 알림 메시지 전송
                    notificationService.sendPaymentErrorNotification(order, errorDetail.getMessage());
                    
                    // 운영팀에 알림
                    if (isWorkingHours()) {
                        alertService.sendOperationalAlert(
                            "Payment Error", 
                            String.format("Order %s failed: %s
                    ...
```
