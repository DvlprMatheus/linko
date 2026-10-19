package com.dvlprmatheus.linko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LinkoApplication {
  public static void main(String[] args) {
    SpringApplication.run(LinkoApplication.class, args);
  }
}
