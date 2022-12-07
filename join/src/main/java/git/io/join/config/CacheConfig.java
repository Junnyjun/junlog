package git.io.join.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.google.common.cache.CacheBuilder.newBuilder;

import java.util.concurrent.ConcurrentMap;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@EnableCaching
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMap<Object, Object> config = newBuilder()
                .expireAfterWrite(3, MINUTES)
                .maximumSize(100)
                .build()
                .asMap();

        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, config, true);
            }
        };
    }
}