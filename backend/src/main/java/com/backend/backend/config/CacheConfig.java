package com.backend.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProps.class)
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(CacheProps props) {
        var caffeine = Caffeine.newBuilder()
                .maximumSize(props.getMaximumSize())
                .expireAfterWrite(Duration.ofSeconds(props.getTtlSeconds()));

        var mgr = new SimpleCacheManager();
        mgr.setCaches(List.of(
                new CaffeineCache(CacheNames.PRODUCT_BY_ID, caffeine.build()),
                new CaffeineCache(CacheNames.PRODUCT_BY_SLUG, caffeine.build()),
                new CaffeineCache(CacheNames.PRODUCT_LIST, caffeine.build()),
                new CaffeineCache(CacheNames.SUPPLIER_BY_ID, caffeine.build()),
                new CaffeineCache(CacheNames.SUPPLIER_LIST, caffeine.build()),
                new CaffeineCache(CacheNames.CUSTOMER_BY_ID, caffeine.build()),
                new CaffeineCache(CacheNames.CUSTOMER_BY_SLUG, caffeine.build()),
                new CaffeineCache(CacheNames.CUSTOMER_LIST, caffeine.build()),
                new CaffeineCache(CacheNames.ORDER_BY_ID, caffeine.build()),
                new CaffeineCache(CacheNames.ORDER_LIST, caffeine.build()),
                new CaffeineCache(CacheNames.ORDER_BY_CUSTOMER, caffeine.build())
        ));
        return mgr;
    }
}

@ConfigurationProperties(prefix = "app.cache")
class CacheProps {
    private long ttlSeconds = 300;
    private long maximumSize = 1000;

    public long getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(long ttlSeconds) { this.ttlSeconds = ttlSeconds; }
    public long getMaximumSize() { return maximumSize; }
    public void setMaximumSize(long maximumSize) { this.maximumSize = maximumSize; }
}

