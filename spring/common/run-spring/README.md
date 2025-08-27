# Run Spring

스프링을 실행하는 코드는 다음과 같다

```kotlin
public ConfigurableApplicationContext run(String... args) {
	Startup startup = Startup.create();
	if (this.registerShutdownHook) {
		SpringApplication.shutdownHook.enableShutdownHookAddition();
	}
	DefaultBootstrapContext bootstrapContext = createBootstrapContext();
	ConfigurableApplicationContext context = null;
	configureHeadlessProperty();
	SpringApplicationRunListeners listeners = getRunListeners(args);
	listeners.starting(bootstrapContext, this.mainApplicationClass);
	try {
		ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
		ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
		Banner printedBanner = printBanner(environment);
		context = createApplicationContext();
		context.setApplicationStartup(this.applicationStartup);
		prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
		refreshContext(context);
		afterRefresh(context, applicationArguments);
		startup.started();
		if (this.logStartupInfo) {
			new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), startup);
		}
		listeners.started(context, startup.timeTakenToStarted());
		callRunners(context, applicationArguments);
	}
	catch (Throwable ex) {
		throw handleRunFailure(context, ex, listeners);
	}
	try {
		if (context.isRunning()) {
			listeners.ready(context, startup.ready());
		}
	}
	catch (Throwable ex) {
		throw handleRunFailure(context, ex, null);
	}
	return context;
}
```

알겠습니다! 번호 없이 정리하겠습니다.

***

`Startup startup = Startup.create();`\
애플리케이션의 실행 시작 시점을 기록하기 위한 `Startup` 객체를 생성합니다. 실행 시간을 추적하는 데 사용됩니다.

`if (this.registerShutdownHook)`\
애플리케이션 종료 시 안전하게 종료될 수 있도록 JVM의 shutdown hook을 등록합니다. 이 hook은 애플리케이션이 종료될 때 호출되어 리소스 정리 및 빈 종료 등의 작업을 처리합니다.

`DefaultBootstrapContext bootstrapContext = createBootstrapContext();`\
애플리케이션 부트스트랩 과정에서 필요한 객체들을 저장하는 `DefaultBootstrapContext` 객체를 생성합니다. 이는 컨텍스트가 완전히 초기화되기 전에 필요한 정보를 담는 역할을 합니다.

`configureHeadlessProperty();`\
헤드리스 모드를 설정합니다. 서버 환경에서 UI가 없는 상태로 애플리케이션이 실행되도록 설정하는 옵션입니다.

`SpringApplicationRunListeners listeners = getRunListeners(args);`\
애플리케이션 실행 과정에서 발생하는 이벤트를 감지할 수 있는 리스너들을 초기화합니다. 애플리케이션 생명 주기 동안 다양한 이벤트를 처리하기 위해 사용됩니다.

`listeners.starting(bootstrapContext, this.mainApplicationClass);`\
애플리케이션이 시작되었음을 리스너들에게 알립니다. `BootstrapContext`와 메인 애플리케이션 클래스 정보를 전달합니다.

`ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);`\
커맨드라인에서 전달된 애플리케이션 인자들을 `ApplicationArguments` 객체로 변환하여 저장합니다. 이는 애플리케이션 실행 중에 필요할 수 있는 인자 정보들을 담고 있습니다.

`ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);`\
애플리케이션 환경을 설정합니다. 프로파일, 프로퍼티 소스 등을 설정하여 애플리케이션 실행 환경을 준비합니다.

`Banner printedBanner = printBanner(environment);`\
애플리케이션 시작 시 콘솔에 출력되는 Spring 배너를 출력합니다.

`context = createApplicationContext();`\
`ApplicationContext`를 생성합니다. Spring의 핵심 컨테이너로, 모든 빈과 설정 정보를 관리하는 역할을 합니다.

`context.setApplicationStartup(this.applicationStartup);`\
애플리케이션의 시작 정보를 `ApplicationContext`에 설정합니다.

`prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);`\
애플리케이션 컨텍스트를 초기화하고, `Environment`, 리스너, 애플리케이션 인자 등을 적용합니다.

`refreshContext(context);`\
`ApplicationContext`를 새로고침하여 모든 빈이 초기화되고 애플리케이션이 준비되도록 합니다.

`afterRefresh(context, applicationArguments);`\
컨텍스트 새로고침 이후 추가적인 작업을 처리합니다. 추가적인 초기화나 이벤트 처리 등이 포함될 수 있습니다.

`startup.started();`\
애플리케이션이 성공적으로 시작되었음을 기록합니다.

`if (this.logStartupInfo)`\
애플리케이션의 시작 정보를 로그로 출력합니다. 애플리케이션이 실행된 메인 클래스, 실행 시간 등을 기록합니다.

`listeners.started(context, startup.timeTakenToStarted());`\
애플리케이션이 시작되었다는 이벤트를 리스너들에게 전달합니다.

`callRunners(context, applicationArguments);`\
`ApplicationRunner` 또는 `CommandLineRunner` 인터페이스를 구현한 빈들을 실행합니다. 이 빈들은 애플리케이션이 시작된 후 추가적인 작업을 수행합니다.

`catch (Throwable ex)`\
애플리케이션 실행 중 발생한 예외를 처리하고, 실패 시 적절하게 리소스를 정리하거나 종료 작업을 수행합니다.
