package com.dvlprmatheus.linko.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dvlprmatheus.linko.api.dto.projection.LinkCodeProjection;
import com.dvlprmatheus.linko.api.dto.request.LinkRequest;
import com.dvlprmatheus.linko.api.dto.response.LinkDestinationResponse;
import com.dvlprmatheus.linko.api.dto.response.LinkShortenedResponse;
import com.dvlprmatheus.linko.api.exception.EntityNotFoundException;
import com.dvlprmatheus.linko.config.properties.ShortProperties;
import com.dvlprmatheus.linko.entity.Link;
import com.dvlprmatheus.linko.repository.LinkRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Link service")
class LinkServiceTest {

  private static final Pattern SHORT_CODE_PATTERN = Pattern.compile("[0-9a-f]{10}");

  @Mock private LinkRepository linkRepository;

  @Mock private ShortProperties shortProperties;

  @InjectMocks private LinkService linkService;

  @Test
  @DisplayName("Returns the destination for an active code")
  void returnsDestinationForActiveCode() {
    Link link = link("destination", "https://destination.example/path", true);
    when(linkRepository.findByCodeAndActiveTrue("destination")).thenReturn(Optional.of(link));

    LinkDestinationResponse response = linkService.getActiveLinkByCode("destination");

    assertEquals("https://destination.example/path", response.destinationUrl());
  }

  @Test
  @DisplayName("Throws not found when an active code does not exist")
  void throwsWhenActiveCodeDoesNotExist() {
    when(linkRepository.findByCodeAndActiveTrue("missing")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> linkService.getActiveLinkByCode("missing"));
  }

  @Test
  @DisplayName("Returns expired active codes from the repository")
  void returnsExpiredActiveCodes() {
    Instant expirationDate = Instant.parse("2025-01-01T00:00:00Z");
    List<LinkCodeProjection> expiredCodes = List.of(() -> "expired");
    when(linkRepository.findExpiredActiveCodes(expirationDate)).thenReturn(expiredCodes);

    assertEquals(expiredCodes, linkService.findExpiredActiveCodes(expirationDate));
  }

  @Test
  @DisplayName("Creates an active link with a unique ten-character code")
  void createsLinkWithUniqueCode() {
    when(shortProperties.getApplicationUrl()).thenReturn("https://short.example");
    when(linkRepository.existsByCode(anyString())).thenReturn(false);
    when(linkRepository.save(any(Link.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, Link.class));

    LinkShortenedResponse response =
        linkService.create(new LinkRequest("https://destination.example"));

    assertNotNull(response.shortenedUrl());
    assertTrue(response.shortenedUrl().startsWith("https://short.example/"));
    String code = response.shortenedUrl().substring("https://short.example/".length());
    assertTrue(SHORT_CODE_PATTERN.matcher(code).matches());
    verify(linkRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                link ->
                    link.isActive()
                        && link.getCode().equals(code)
                        && link.getDestinationUrl().equals("https://destination.example")
                        && link.getShortenedUrl().equals(response.shortenedUrl())));
  }

  @Test
  @DisplayName("Retries code generation when a generated code is already in use")
  void retriesWhenGeneratedCodeExists() {
    when(shortProperties.getApplicationUrl()).thenReturn("https://short.example");
    when(linkRepository.existsByCode(anyString())).thenReturn(true, false);
    when(linkRepository.save(any(Link.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, Link.class));

    linkService.create(new LinkRequest("https://destination.example"));

    verify(linkRepository, times(2)).existsByCode(anyString());
    verify(linkRepository).save(any(Link.class));
  }

  @Test
  @DisplayName("Propagates repository errors when creating a link")
  void propagatesRepositoryErrorWhenCreatingLink() {
    when(shortProperties.getApplicationUrl()).thenReturn("https://short.example");
    when(linkRepository.existsByCode(anyString())).thenReturn(false);
    when(linkRepository.save(any(Link.class)))
        .thenThrow(new IllegalStateException("database error"));

    assertThrows(
        IllegalStateException.class,
        () -> linkService.create(new LinkRequest("https://destination.example")));
  }

  @Test
  @DisplayName("Deactivates an active link and persists the change")
  void deactivatesActiveLink() {
    Link link = link("active", "https://destination.example", true);
    when(linkRepository.findByCodeAndActiveTrue("active")).thenReturn(Optional.of(link));
    when(linkRepository.save(link)).thenReturn(link);

    linkService.deactivateLink("active");

    assertFalse(link.isActive());
    verify(linkRepository).save(link);
  }

  @Test
  @DisplayName("Throws not found when deactivating a missing or inactive link")
  void throwsWhenDeactivatingMissingOrInactiveLink() {
    when(linkRepository.findByCodeAndActiveTrue("inactive")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> linkService.deactivateLink("inactive"));

    verify(linkRepository, never()).save(any(Link.class));
  }

  private Link link(String code, String destinationUrl, boolean active) {
    return Link.builder()
        .code(code)
        .destinationUrl(destinationUrl)
        .shortenedUrl("https://short.example/" + code)
        .active(active)
        .build();
  }
}
