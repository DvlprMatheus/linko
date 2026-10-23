package com.dvlprmatheus.linko.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dvlprmatheus.linko.api.dto.projection.LinkCodeProjection;
import com.dvlprmatheus.linko.entity.Link;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Link repository")
class LinkRepositoryTest {

  @Autowired private LinkRepository linkRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("Finds only active links created before the expiration cutoff")
  void findsOnlyExpiredActiveCodes() {
    Instant cutoff = Instant.parse("2025-01-01T00:00:00Z");
    saveLink("expired-active", true, cutoff.minusSeconds(1));
    saveLink("recent-active", true, cutoff.plusSeconds(1));
    saveLink("expired-inactive", false, cutoff.minusSeconds(60));
    saveLink("at-cutoff", true, cutoff);

    List<String> codes =
        linkRepository.findExpiredActiveCodes(cutoff).stream()
            .map(LinkCodeProjection::getCode)
            .toList();

    assertEquals(List.of("expired-active"), codes);
  }

  @Test
  @DisplayName("Finds a link only when its code matches and it is active")
  void findsOnlyActiveLinkByCode() {
    saveLink("active-code", true, Instant.now());
    saveLink("inactive-code", false, Instant.now());

    Optional<Link> activeLink = linkRepository.findByCodeAndActiveTrue("active-code");
    Optional<Link> inactiveLink = linkRepository.findByCodeAndActiveTrue("inactive-code");
    Optional<Link> missingLink = linkRepository.findByCodeAndActiveTrue("missing-code");

    assertTrue(activeLink.isPresent());
    assertEquals("active-code", activeLink.orElseThrow().getCode());
    assertFalse(inactiveLink.isPresent());
    assertFalse(missingLink.isPresent());
  }

  @Test
  @DisplayName("Checks whether a code is already stored")
  void checksCodeExistence() {
    saveLink("stored-code", true, Instant.now());

    assertTrue(linkRepository.existsByCode("stored-code"));
    assertFalse(linkRepository.existsByCode("unknown-code"));
  }

  private void saveLink(String code, boolean active, Instant createdAt) {
    Link link =
        Link.builder()
            .code(code)
            .destinationUrl("https://destination.example/" + code)
            .shortenedUrl("https://short.example/" + code)
            .active(active)
            .build();
    linkRepository.saveAndFlush(link);
    jdbcTemplate.update(
        "UPDATE links SET created_at = ? WHERE code = ?", java.sql.Timestamp.from(createdAt), code);
  }
}
