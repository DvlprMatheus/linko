package com.dvlprmatheus.linko.config.schedule;

import com.dvlprmatheus.linko.api.dto.projection.LinkCodeProjection;
import com.dvlprmatheus.linko.config.properties.ShortProperties;
import com.dvlprmatheus.linko.service.LinkService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class LinkExpirationScheduler {

  private static final ZoneId SCHEDULE_ZONE = ZoneId.of("America/Sao_Paulo");

  private final LinkService linkService;
  private final ShortProperties shortProperties;

  @Transactional
  @Scheduled(cron = "0 0 6 * * *", zone = "America/Sao_Paulo")
  public void deactivateExpiredLinks() {
    log.info("Starting scheduled task to deactivate expired links");
    Instant expirationDate =
        LocalDate.now(SCHEDULE_ZONE)
            .minus(shortProperties.getExpiration())
            .atStartOfDay(SCHEDULE_ZONE)
            .toInstant();
    List<String> expiredCodes =
        linkService.findExpiredActiveCodes(expirationDate).stream()
            .map(LinkCodeProjection::getCode)
            .toList();
    expiredCodes.forEach(linkService::deactivateLink);
    log.info("Deactivated {} expired links", expiredCodes.size());
  }
}
