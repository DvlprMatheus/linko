package com.dvlprmatheus.linko.config.redis;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cache.redis")
public class CacheProperties {
  private Duration timeToLive;
}
