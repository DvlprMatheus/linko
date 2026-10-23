package com.dvlprmatheus.linko.config.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dvlprmatheus.linko.api.dto.projection.LinkCodeProjection;
import com.dvlprmatheus.linko.config.properties.ShortProperties;
import com.dvlprmatheus.linko.service.LinkService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Link expiration scheduler")
class LinkExpirationSchedulerTest {

  private static final ZoneId SCHEDULE_ZONE = ZoneId.of("America/Sao_Paulo");

  @Mock private LinkService linkService;

  @Mock private ShortProperties shortProperties;

  @InjectMocks private LinkExpirationScheduler scheduler;

  @Test
  @DisplayName("Deactivates every code returned as expired")
  void deactivatesExpiredLinks() {
    when(shortProperties.getExpiration()).thenReturn(Period.ofMonths(3));
    when(linkService.findExpiredActiveCodes(any()))
        .thenReturn(List.of(projection("expired-1"), projection("expired-2")));

    scheduler.deactivateExpiredLinks();

    ArgumentCaptor<Instant> expirationCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(linkService).findExpiredActiveCodes(expirationCaptor.capture());
    Instant expectedExpiration =
        LocalDate.now(SCHEDULE_ZONE)
            .minus(Period.ofMonths(3))
            .atStartOfDay(SCHEDULE_ZONE)
            .toInstant();
    assertEquals(expectedExpiration, expirationCaptor.getValue());
    verify(linkService).deactivateLink("expired-1");
    verify(linkService).deactivateLink("expired-2");
  }

  @Test
  @DisplayName("Does not deactivate links when no expired codes are found")
  void doesNothingWhenNoExpiredLinksExist() {
    when(shortProperties.getExpiration()).thenReturn(Period.ofMonths(3));
    when(linkService.findExpiredActiveCodes(any())).thenReturn(List.of());

    scheduler.deactivateExpiredLinks();

    verify(linkService, never()).deactivateLink(any());
  }

  @Test
  @DisplayName("Propagates failures while searching for expired codes")
  void propagatesSearchFailure() {
    when(shortProperties.getExpiration()).thenReturn(Period.ofMonths(3));
    when(linkService.findExpiredActiveCodes(any()))
        .thenThrow(new IllegalStateException("repository unavailable"));

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class, scheduler::deactivateExpiredLinks);
  }

  private LinkCodeProjection projection(String code) {
    return () -> code;
  }
}
