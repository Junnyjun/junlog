# 고급 기능

### 고급 에러 처리 및 복원성 패턴

#### 전역 오류 처리 전략

```
@Configuration
public class GlobalErrorHandlingConfig {
    @Bean
    public IntegrationFlow globalErrorHandlingFlow() {
        return IntegrationFlows
            .from(ErrorChannel.class)
            .handle((message, headers) -> {
                MessagingException exception = (MessagingException) message.getPayload();
                
                // 고급 오류 로깅
                ErrorDetails errorDetails = new ErrorDetails(
                    exception.getFailedMessage(),
                    LocalDateTime.now(),
                    determineErrorSeverity(exception)
                );
                
                // 오류 분류 및 처리
                return routeError(errorDetails);
            })
            .channel("errorProcessingChannel")
            .get();
    }

    private ErrorSeverity determineErrorSeverity(MessagingException exception) {
        // 예외 유형에 따른 심각도 분류 로직
        if (exception.getCause() instanceof NetworkException) {
            return ErrorSeverity.CRITICAL;
        }
        // 다른 예외 유형에 대한 심각도 분류
        return ErrorSeverity.MINOR;
    }

    private ErrorRouteResult routeError(ErrorDetails errorDetails) {
        // 오류 유형에 따른 라우팅 및 복구 전략
        switch (errorDetails.getSeverity()) {
            case CRITICAL:
                return new ErrorRouteResult(
                    ErrorAction.RETRY_WITH_BACKOFF, 
                    errorDetails
                );
            case MAJOR:
                return new ErrorRouteResult(
                    ErrorAction.NOTIFY_SUPPORT, 
                    errorDetails
                );
            default:
                return new ErrorRouteResult(
                    ErrorAction.LOG_AND_CONTINUE, 
                    errorDetails
                );
        }
    }
}
```

#### 재시도 및 폴백 메커니즘

```
@Configuration
public class RetryConfig {
    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(3)
            .retryPolicy(new SimpleRetryPolicy(3, 
                Map.of(
                    NetworkException.class, true,
                    ServiceUnavailableException.class, true
                )))
            .recoveryPolicy(new RecoveryCallback<Object>() {
                @Override
                public Object recover(RetryContext context) throws Exception {
                    // 모든 재시도 실패 후 폴백 로직
                    Exception lastException = context.getLastThrowable();
                    return handleFinalFailure(lastException);
                }
            })
            .exponentialBackOffPolicy()
            .build();
    }

    private Object handleFinalFailure(Exception exception) {
        // 최종 실패 처리 로직
        errorNotificationService.sendCriticalErrorAlert(exception);
        return ErrorAction.ABORT;
    }
}
```

### 고급 라우팅 전략

#### 동적 라우팅

```
@Configuration
public class DynamicRoutingConfig {
    @Bean
    public IntegrationFlow dynamicRoutingFlow() {
        return IntegrationFlows
            .from("dynamicRoutingChannel")
            .route(Message<?>::getPayload, 
                mapping -> mapping
                    .subFlowMapping(Order.class, this::processOrderFlow)
                    .subFlowMapping(Payment.class, this::processPaymentFlow)
                    .subFlowMapping(User.class, this::processUserFlow)
                    .defaultSubFlowMapping(sf -> sf
                        .handle(message -> {
                            // 알 수 없는 메시지 유형 처리
                            log.warn("Unknown message type: {}", 
                                message.getPayload().getClass());
                        })
                    )
            )
            .get();
    }

    private IntegrationFlowDefinition<?> processOrderFlow(IntegrationFlowDefinition<?> flow) {
        return flow
            .transform(this::enrichOrder)
            .handle("orderProcessingService", "process")
            .channel("processedOrdersChannel");
    }

    private IntegrationFlowDefinition<?> processPaymentFlow(IntegrationFlowDefinition<?> flow) {
        return flow
            .handle("paymentValidationService", "validate")
            .route(Payment::getStatus, 
                mapping -> mapping
                    .subFlowMapping(PaymentStatus.APPROVED, 
                        sf -> sf.handle("paymentProcessingService", "processApproved"))
                    .subFlowMapping(PaymentStatus.DECLINED, 
                        sf -> sf.handle("paymentProcessingService", "processDeclined"))
            );
    }

    private IntegrationFlowDefinition<?> processUserFlow(IntegrationFlowDefinition<?> flow) {
        return flow
            .handle("userManagementService", "processUser")
            .channel("userProcessedChannel");
    }
}
```

### 고급 변환 및 보강 패턴

#### 맞춤형 변환기

```
@Component
public class CustomOrderTransformer implements Transformer {
    @Override
    public Object transform(Message<?> message) {
        Order originalOrder = (Order) message.getPayload();
        
        // 복잡한 변환 로직
        EnrichedOrder enrichedOrder = new EnrichedOrder(originalOrder);
        
        // 비즈니스 규칙 적용
        applyBusinessRules(enrichedOrder);
        
        // 추가 메타데이터 보강
        enrichedOrder.setTransformationTimestamp(LocalDateTime.now());
        
        return enrichedOrder;
    }

    private void applyBusinessRules(EnrichedOrder order) {
        // 주문에 대한 고급 비즈니스 규칙 적용
        if (order.getTotalAmount().compareTo(BULK_ORDER_THRESHOLD) > 0) {
            order.setOrderType(OrderType.WHOLESALE);
            applyWholesaleDiscounts(order);
        }
    }
}
```

#### 메시지 보강 패턴

```
@Configuration
public class MessageEnrichmentConfig {
    @Bean
    public IntegrationFlow orderEnrichmentFlow() {
        return IntegrationFlows
            .from("rawOrderChannel")
            .transform(new CustomOrderTransformer())
            .enrichHeaders(h -> h
                .header("processingStartTime", System.currentTimeMillis())
                .headerExpression("correlationId", 
                    "T(java.util.UUID).randomUUID().toString()")
                .header("orderSource", "ONLINE_CHANNEL")
            )
            .handle(new MessageEnricher())
            .channel("enrichedOrderChannel");
    }

    public class MessageEnricher implements MessageHandler {
        @Override
        public void handleMessage(Message<?> message) throws MessagingException {
            EnrichedOrder order = (EnrichedOrder) message.getPayload();
            
            // 외부 서비스 호출을 통한 추가 정보 보강
            CustomerProfile customerProfile = 
                customerService.getProfileById(order.getCustomerId());
            
            order.setCustomerTier(customerProfile.getTier());
            order.setCustomerSegment(customerProfile.getSegment());
            
            // 재고 정보 확인
            InventoryStatus inventoryStatus = 
                inventoryService.checkInventory(order.getLineItems());
            
            order.setFullyAvailable(inventoryStatus.isFullyAvailable());
            order.setAvailableItems(inventoryStatus.getAvailableItems());
        }
    }
}
```

### 고급 분할 및 집계 패턴

#### 복잡한 분할 전략

```
@Configuration
public class AdvancedSplitterConfig {
    @Bean
    public IntegrationFlow complexSplitterFlow() {
        return IntegrationFlows
            .from("bulkOrderChannel")
            .split(new OrderBatchSplitter())
            .channel(c -> c.executor(splitTaskExecutor()))
            .aggregate(new CustomOrderAggregator())
            .channel("processedBatchOrderChannel")
            .get();
    }

    public class OrderBatchSplitter implements Splitter {
        @Override
        public List<?> split(Message<?> message) {
            OrderBatch batch = (OrderBatch) message.getPayload();
            
            return batch.getOrders().stream()
                .collect(Collectors.groupingBy(
                    this::determineProcessingGroup,
                    Collectors.toList()
                ))
                .values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        }

        private String determineProcessingGroup(Order order) {
            // 주문 유형, 금액, 고객 세그먼트 등에 따른 그룹화
            if (order.getTotalAmount().compareTo(BULK_THRESHOLD) > 0) {
                return "LARGE_ORDER";
            }
            return "STANDARD_ORDER";
        }
    }

    public class CustomOrderAggregator implements Aggregator {
        @Override
        public Object aggregate(List<Message<?>> messages) {
            List<Order> processedOrders = messages.stream()
                .map(message -> (Order) message.getPayload())
                .collect(Collectors.toList());
            
            // 고급 집계 로직
            BatchProcessingResult result = new BatchProcessingResult(
                processedOrders,
                calculateOverallStatus(processedOrders)
            );
            
            return result;
        }

        private BatchProcessStatus calculateOverallStatus(List<Order> orders) {
            // 배치 처리 상태 계산 로직
            long successCount = orders.stream()
                .filter(Order::isProcessed)
                .count();
            
            double successRate = (double) successCount / orders.size();
            
            return successRate >= 0.9 ? 
                BatchProcessStatus.MOSTLY_SUCCESSFUL : 
                BatchProcessStatus.PARTIAL_FAILURE;
        }
    }
}
```

### 고급 게이트웨이 및 서비스 활성화기

#### 비동기 게이트웨이

```
@MessagingGateway
public interface AsyncOrderProcessingGateway {
    @Gateway(
        requestChannel = "asyncOrderProcessingChannel", 
        replyChannel = "orderProcessingResultChannel",
        replyTimeout = 5000
    )
    CompletableFuture<OrderProcessingResult> processOrderAsync(Order order);

    @Gateway(
        requestChannel = "bulkOrderProcessingChannel",
        replyChannel = "bulkOrderResultChannel"
    )
    ListenableFuture<BatchProcessingResult> processBulkOrderAsync(List<Order> orders);
}
```

#### 고급 서비스 활성화기

```
@Configuration
public class AdvancedServiceActivatorConfig {
    @Bean
    public IntegrationFlow advancedOrderProcessingFlow() {
        return IntegrationFlows
            .from("orderProcessingChannel")
            .handle(new ServiceActivator() {
                @Override
                public Object handle(Message<?> message) throws Exception {
                    Order order = (Order) message.getPayload();
                    
                    // 복잡한 주문 처리 로직
                    OrderProcessingContext context = 
                        createProcessingContext(order, message.getHeaders());
                    
                    OrderProcessingResult result = 
                        processOrder(context);
                    
                    // 후처리 및 이벤트 발행
                    publishOrderProcessingEvent(result);
                    
                    return result;
                }
            })
            .channel("orderResultChannel")
            .get();
}
```

### 워크플로우 및 상태 머신 통합

```
@Configuration
public class OrderWorkflowConfig {
    @Bean
    public IntegrationFlow orderWorkflowStateMachine() {
        return IntegrationFlows
            .from("orderInitiationChannel")
            .handle(new StateMachineHandler<OrderState, OrderEvent>() {
                @Override
                protected void configureStateMachine(
                    StateMachineConfigurer<OrderState, OrderEvent> config
                ) {
                    config
                        .withStates()
                            .initial(OrderState.CREATED)
                            .state(OrderState.VALIDATED)
                            .state(OrderState.PROCESSING)
                            .state(OrderState.SHIPPED)
                            .end(OrderState.COMPLETED)
                            .end(OrderState.CANCELLED)
                        .withTransitions()
                            .fromState(OrderState.CREATED)
                                .event(OrderEvent.VALIDATE)
                                .toState(OrderState.VALIDATED)
                            .fromState(OrderState.VALIDATED)
                                .event(OrderEvent.PROCESS)
                                .toState(OrderState.PROCESSING)
                            // 추가 상태 전이 정의
                }
            })
            .channel("orderWorkflowResultChannel")
            .get();
}
```
