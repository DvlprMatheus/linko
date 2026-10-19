package com.dvlprmatheus.linko.config.redis;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(
      RedisConnectionFactory connectionFactory,
      CacheProperties cacheProperties,
      ObjectMapper objectMapper) {
    RedisCacheConfiguration configuration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(cacheProperties.getTimeToLive())
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJacksonJsonRedisSerializer(objectMapper)));
    return RedisCacheManager.builder(connectionFactory).cacheDefaults(configuration).build();
  }
}
