# 📘 JPA 완벽가이드

[GIT HUB](https://github.com/Junnyjun/pure-hibernate/blob/main/src/main/kotlin/executor/config/Persistence.kt)

WEB-INF를 사용하지 않고 순수 자바 설정으로 만든 예시입니다.

{% code title="" overflow="wrap" fullWidth="false" %}
```gradle
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.8.21'
}

group = 'io.github'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'javax.persistence:javax.persistence-api:2.2'
    implementation 'org.hibernate:hibernate-core:5.6.0.Final'
    implementation 'org.postgresql:postgresql:42.2.27'

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'
    testImplementation 'org.junit.jupiter:junit-jupiter-params:5.7.0'
    testImplementation 'org.jetbrains.kotlin:kotlin-test'


}

test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
```
{% endcode %}

{% code title="config.kt" fullWidth="false" %}
```kotlin
private val probs: Properties = Properties().also {
    it.setProperty("javax.persistence.jdbc.url", "jdbc:postgresql://URL:5432/mydb")
    it.setProperty("javax.persistence.jdbc.user", "ID")
    it.setProperty("javax.persistence.jdbc.password", "PW")
    it.setProperty("javax.persistence.jdbc.driver", "org.postgresql.Driver")
    it.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
    it.setProperty("hibernate.hbm2ddl.auto", "create")
    it.setProperty("hibernate.show_sql", "true")
    it.setProperty("hibernate.format_sql", "true") }


val emf = HibernatePersistenceProvider().createContainerEntityManagerFactory(
    Persistence,
    Collections.EMPTY_MAP
)

object Persistence:PersistenceUnitInfo {


    override fun getProperties(): Properties {
        return probs
    }

    override fun getManagedClassNames(): List<String?>? {
        return mutableListOf(
            Team::class.java.name,
        )
    }

    override fun getPersistenceUnitName(): String? {
        return "TestUnit"
    }

    override fun getPersistenceProviderClassName(): String? {
        return HibernatePersistenceProvider::class.java.name
    }

    override fun getTransactionType(): PersistenceUnitTransactionType? {
        return null
    }

    override fun getJtaDataSource(): DataSource? {
        return null
    }

    override fun getNonJtaDataSource(): DataSource? {
        return null
    }

    override fun getMappingFileNames(): List<String?>? {
        return null
    }

    override fun getJarFileUrls(): List<URL?>? {
        return null
    }

    override fun getPersistenceUnitRootUrl(): URL? {
        return null
    }

    override fun excludeUnlistedClasses(): Boolean {
        return false
    }

    override fun getSharedCacheMode(): SharedCacheMode? {
        return null
    }

    override fun getValidationMode(): ValidationMode? {
        return null
    }

    override fun getPersistenceXMLSchemaVersion(): String? {
        return null
    }

    override fun getClassLoader(): ClassLoader? {
        return null
    }

    override fun addTransformer(transformer: ClassTransformer?) {}

    override fun getNewTempClassLoader(): ClassLoader? {
        return null
    }
}
```
{% endcode %}

