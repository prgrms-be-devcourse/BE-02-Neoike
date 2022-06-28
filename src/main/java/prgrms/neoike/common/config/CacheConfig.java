package prgrms.neoike.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import prgrms.neoike.common.cache.CacheType;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
            .map(cache ->
                new CaffeineCache(cache.getCacheName(), Caffeine.newBuilder()
                    .recordStats()
                    .expireAfterWrite(Duration.ofMinutes(cache.getExpiredMinuteAfterWrite()))
                    .maximumSize(cache.getMaximumSize())
                    .build()
                )
            )
            .toList();

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);

        return cacheManager;
    }
}