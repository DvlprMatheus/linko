package com.dvlprmatheus.linko.config.schedule;

import com.dvlprmatheus.linko.config.ShortProperties;
import com.dvlprmatheus.linko.repository.LinkRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class LinkExpirationScheduler {

  private final LinkRepository linkRepository;
  private final ShortProperties shortProperties;

  @Transactional
  @Scheduled(cron = "0 0 6 * * *", zone = "America/Sao_Paulo")
  public void deactivateExpiredLinks() {
    log.info("Starting scheduled task to deactivate expired links");
    Instant expirationDate =
        LocalDate.now()
            .minus(shortProperties.getExpiration())
            .atStartOfDay(ZoneId.of("America/Sao_Paulo"))
            .toInstant();
    int deactivatedCount = linkRepository.deactivateExpiredLinks(expirationDate);
    log.info("Deactivated {} expired links", deactivatedCount);
  }
}
