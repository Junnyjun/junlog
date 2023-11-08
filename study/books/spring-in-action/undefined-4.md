# 데이터 캐싱

### 캐시 지원하기

스프링의 캐시 추상화 지원 ->  애너테이션 주도 캐싱 or XML 선언 캐싱(생략)

&#x20;@Caheable, @CacheEvict : 해당 애너테이션을 사용하여, 메소드를 애너테이션하는 것이 가장 일반적인 방법

\* 빈의 캐싱 애너테이션 적용 전에는, 스프링의 애너테이션 주도 캐싱 지원을 사용해야함&#x20;

```
@Configuration 
@EnableCaching // 캐싱 활성화 
public class CachingConfig {
  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(); // 캐시 매니저 선언
  }
}
```

@EnableCaching : 스프링의 캐싱 애너테이션을 하는 포인트커트를 가지는 애스펙트 생성... 캐시 상태와 애너테이션에 따라 애스펙트는 캐시의 값을 가져오거나 추가하거나 삭제한다.

ConcurrentMapCacheManager : 간단한 캐시 매니저. 저장소로 ConcurrentHashMap 사용. 캐시 저장소는 메모리 기반이며 애플리케이션 라이프사이클에 얽혀있기 때문에, 개발이나 작은규모에서 적합하다

&#x20;



#### 캐시 매니저 설정하기

스프링 3.2이상에서는, 7개의 캐시매니저 구현체를 지원

* SimpleCacheManager
* NoOpCacheManager
* ConcurrentMapCacheManager
* CompositeCacheManager
* EhCacheCacheManager
* RedisCacheManager(스프링 Redis)
* GemfireCacheManager(스프링 GemFire)

&#x20;



**EhCache로 캐싱하기**

Ehcache - 가장 인기 있는 캐시 프로바이더 중 하나 - 스프링은 EhCacheCacheManager를 통해 통합된 EhCache 매니저를 제공한다 -> ehcache 특징 : [https://sjh836.tistory.com/129](https://sjh836.tistory.com/129)

```
@Configuration
@EnableCaching
public class CachingConfig {
  @Bean
  public EhCacheCacheManager cacheManager(CacheManager cm) { // EhCacheCacheManager 설정
    return new EhCacheCacheManager(cm);
  }
  
  @Bean
  public EhCacheManagerFactoryBean ehcache() { // EhCacheManagerFactoryBean
     EhCacheManagerFactoryBean ehCacheFactoryBean = new EhCacheManagerFactoryBean();
     ehCacheFactoryBean.setConfigLocation(new ClassPathResource("com/bobfull/cafe/cache/ehcache.xml"));
  
    return ehCacheFactoryBean;
  }
}
```

\* setConfiguration() 메소드를 통해, Ehcache 설정이 된 xml파일을 가져온다.

```
<ehcache>
  <cache name="cafeCache"
          maxBytesLocalHeap="50m"
          timeToLiveSeconds="100">
  </cache>
</ehcache>

## 50MB의 100초 유효기간을 가진 캐시저장소
```

&#x20;



#### Redis로 캐싱하기

캐시 엔트리에서는 키-값 이외에는 다른 값을 가지지 않음. 따라서 키-값 저장소인 Redis가 적합할 수 있다.

스프링 데이터 Redis에는 RedisCacheManager를 제공한다. RedisCacheManager는 RedisTemplate를 통해 Redis 서버를 가지고 동작 => RedisCacheManager를 위해서는, 각각 RedisTemplate과, RedisConnectionFactory의 구현 빈이 필요(12장)

```
@Configuration
@EnableCaching
public class CachingConfig {
  @Bean  // RedisCacheManager 빈
  public CacheManager cacheManager(RedisTemplate redisTemplate) { 
      return new RedisCacheManager(redisTemplate); // Redis템플릿의 인스턴스를 생성자에 전달하여, 생성
   }
  @Bean // RedisConnectionFactory 빈
  public JedisConnectionFactory redisConnectionFactory() {
     JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    jedisConnectionFactory.afterPropertiesSet();
    return jedisConnectionFactory;
  }
  @Bean // RedisTemplate 빈
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisCF) {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
    redisTemplate.setConnectionFactory(redisCF);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
} }
```

&#x20;



#### 다중 캐시 매니저 사용

CompositeCacheManager : 한 개 이상의 캐시매니저를 선택해야할 때 사용

```
@Bean
public CacheManager cacheManager(net.sf.ehcache.CacheManager cm, javax.cache.CacheManager jcm) {
  CompositeCacheManager cacheManager = new CompositeCacheManager();
  
  List<CacheManager> managers = new ArrayList<CacheManager>();
  managers.add(new JCacheCacheManager(jcm));
  managers.add(new EhCacheCacheManager(cm))
  managers.add(new RedisCacheManager(redisTemplate()));
  
  cacheManager.setCacheManagers(managers); // 개별 캐시 매니저 추가
  return cacheManager;
}
```

\* JCache구현체를 체크로 시작, 그 다음은 EhCache.. 최종적으로 캐시 엔트리를 위해 Redis 동작을 체크

\* 3개의 캐시매니저를 사용하여 설정

&#x20;



### 캐시 어노테이션 메소드

**캐시 어노테이션으로, 애스펙트를 생성해서 동작한다.**

<figure><img src="https://blog.kakaocdn.net/dn/bBHJi5/btqAkwchgWn/wrjITPqs431nt1djeXA7L0/img.png" alt=""><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/Gutad/btqAhqrtFlX/J0r5cztWKyRO7tD7gipQX0/img.png" alt=""><figcaption></figcaption></figure>

&#x20;



#### 캐시 채우기

@Cacheable, @CachePut는 둘다 캐시를 채우기 위한 어노테이션이지만, 각각 동작하는 방식이 다르다

@Cacheable : 먼저 캐시에서 항목을 찾고 일치하는 항목을 찾으면 메소드 호출을 우선. 일치하는 항목이 없으면 메소드가 호출되고 리턴 된 값이 캐시에 저장

@CachePut :  캐시에서 일치하는 값을 확인하지 않고 항상 대상 메소드를 호출 할 수 있도록하며 리턴 된 값을 캐시에 추가

<figure><img src="https://blog.kakaocdn.net/dn/4mnZ9/btqAjm2utzT/r97ddZAWsxLKeyfYKnZ6oK/img.png" alt=""><figcaption><p>@Cacheable, @CachePut이 공통으로 사용하는 어트리뷰트</p></figcaption></figure>

```
@Cacheable("spittleCache")
public Spittle findOne(long id) {
  try {
    return jdbcTemplate.queryForObject(
        SELECT_SPITTLE_BY_ID,
        new SpittleRowMapper(),
        id);
        } catch (EmptyResultDataAccessException e) {
    return null;
  } 
}
```

\* findeOne()호출 시, 캐싱 애스펙트가 호출을 가로챈 후, "spittleCache"라는 캐시에 반환된 값을, id라는 매개변수를 키로 찾음. 값이 있으면 캐시에 저장된 값을 반환한 후, 메소드는 호출하지 않음

\* 위 코드는 구현 메소드에, 어노테이션을 달았기 때문에 해당 메소드만 캐싱을 하지만, 만약 인터페이스에 어노테이션을 한다면, 모든 구현 메소드에 적용될 것

&#x20;



**캐시에 값 넣기**

@CachePut의 경우에는, 메소드가 항상 호출되고, 해당 반환 값이 캐시에 저장된다. 그렇기 때문에, save() 메소드 등, 저장을 할 때에 매우 용이하다(저장과 동시에 캐시가 저장되므로)

```
@CachePut("spittleCache")
Spittle save(Spittle spittle);
```

\-> 이런식으로 @CachePut으로 save()메소드를 어노테이션 할 수 있다. 그러나 @Cacheable과 달리, 이런 경우에 캐시 키가 Spittle이기 때문에, 모든 Spiittle이 키가 되면서, Key - Spittle, Value - Spittle인 이상한 상황 발생.. 이런 점을 해결하기 위해 맞춤형 캐시 키가 있음

&#x20;



**맞춤형 캐시 키 만들기**

@Cacheablue, @CachePut 모두, 기본 키를 SpEL을 통해서, 키를 바꿔줄 수 있음. 스프링은 캐싱을 위한 SpEL을 작성할 때에 유용한 메타데이터를 가지고 있음

<figure><img src="https://blog.kakaocdn.net/dn/c0ZbBq/btqAkxhYN2Q/SsMNaDVKytoxlrEKrZXkuk/img.png" alt=""><figcaption></figcaption></figure>

```
@CachePut(value="spittleCache", key="#result.id")
Spittle save(Spittle spittle);
```

\* 위의 표현식에서 @CachePut은 #result를 key를 지정해 줄 수 있음

\* key: id // Value : Spittle

&#x20;



**조건 캐싱**

캐시를 사용하다보면, 어떤 데이터의 경우에는 캐시를  사용하지 않으려 할 수 있다.

스프링에서는 이런 점을 고려하여, SpEL을 이용하여 조건부 캐싱을 할 수 있음(unless, conditon 사용)

unless - 캐싱을 하지 않는 조건

```
@Cacheable(value="spittleCache" unless="#result.message.contains('NoCache')")
Spittle findOne(long id);
```

\=> 위의 SpEL에서 있던 #result 사용.. -> message 내에 NoCache가 있는 것을 제외한다

conditon - 캐싱을 하는 조건

```
@Cacheable(value="spittleCache" unless="#result.message.contains('NoCache')" condition="#id >= 10")
Spittle findOne(long id);
```

\=> #id 가 10 이상일 때만 캐싱(unless의 조건도 포함)

&#x20;



#### 캐시 엔트리 삭제하기

@CacheEvict : 캐시를 제거하는 어노테이션 주로 remove() 등, 제거 메소드 등에서 사용하면 좋음(void에서 사용 가능)

```
@CacheEvict("spittleCache")
void remove(long spittleId);
```

\* spittleId에 맞는, 캐시가 삭제됨

<figure><img src="https://blog.kakaocdn.net/dn/cRFSky/btqAjMG51dg/xYHBDMez7OU6pcKNPcvtu0/img.png" alt=""><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/bmOd8U/btqAizhAodQ/grTvYZCpd6FUIUORNGHwu0/img.png" alt=""><figcaption></figcaption></figure>

==> @CacheEvict 어노테이션에서 사용가능한 어트리뷰트(unless는 제공하지 않음)
