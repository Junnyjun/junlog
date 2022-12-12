package git.io.join.adapter.out.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentMap;

public interface CacheRepository {
    Boolean isExists(String key);
    void save(String key);

    Integer size();

    @Slf4j
    @Repository
    @RequiredArgsConstructor
    class CacheValid implements CacheRepository {
        private final CacheManager cacheManager;

        @Override
        public Boolean isExists(String key) {
            return cacheManager.getCache("cache").get(key) != null;
        }

        @Override
        @Cacheable(value = "cache", key = "#key")
        public void save(String key) {
            log.info("Token Saved ::: {}", key);
        }

        @Override
        public Integer size() {
            return ((ConcurrentMapCache) cacheManager.getCache("cache")).getNativeCache().size();
        }
    }
}
