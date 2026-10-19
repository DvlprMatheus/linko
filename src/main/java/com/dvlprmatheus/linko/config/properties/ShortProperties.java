package com.dvlprmatheus.linko.config.properties;

import java.time.Period;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "short")
@Configuration
public class ShortProperties {
  private String applicationUrl;
  private Period expiration;
}
