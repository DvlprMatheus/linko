package com.dvlprmatheus.linko.service;

import com.dvlprmatheus.linko.api.dto.projection.LinkCodeProjection;
import com.dvlprmatheus.linko.api.dto.request.LinkRequest;
import com.dvlprmatheus.linko.api.dto.response.LinkDestinationResponse;
import com.dvlprmatheus.linko.api.dto.response.LinkShortenedResponse;
import com.dvlprmatheus.linko.api.exception.EntityNotFoundException;
import com.dvlprmatheus.linko.config.properties.ShortProperties;
import com.dvlprmatheus.linko.entity.Link;
import com.dvlprmatheus.linko.repository.LinkRepository;
import com.dvlprmatheus.linko.utils.CodeUtils;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class LinkService {

  private final LinkRepository linkRepository;
  private final ShortProperties shortProperties;

  @Cacheable(value = "activeLinks", key = "#code")
  public LinkDestinationResponse getActiveLinkByCode(String code) {
    log.info("Attempting to retrieve active link with code: {}", code);
    return this.linkRepository
        .findByCodeAndActiveTrue(code)
        .map(LinkDestinationResponse::new)
        .orElseThrow(EntityNotFoundException::new);
  }

  public List<LinkCodeProjection> findExpiredActiveCodes(Instant expirationDate) {
    log.info("Finding expired active link codes before: {}", expirationDate);
    return this.linkRepository.findExpiredActiveCodes(expirationDate);
  }

  @Transactional
  public LinkShortenedResponse create(LinkRequest linkRequest) {
    log.info("Creating a new link with destination URL: {}", linkRequest.destinationUrl());
    String code = this.generateSafeCode();
    Link link =
        Link.builder()
            .destinationUrl(linkRequest.destinationUrl())
            .shortenedUrl(shortProperties.getApplicationUrl() + "/" + code)
            .code(code)
            .active(true)
            .build();
    Link savedLink = this.linkRepository.save(link);
    log.info("Link created successfully with code: {}", savedLink.getCode());
    return new LinkShortenedResponse(savedLink);
  }

  private String generateSafeCode() {
    String code;
    do {
      code = CodeUtils.genrateUniqueCode();
    } while (linkRepository.existsByCode(code));
    return code;
  }

  @CacheEvict(value = "activeLinks", key = "#code")
  @Transactional
  public void deactivateLink(String code) {
    log.info("Deactivating link with code: {}", code);
    Link link =
        this.linkRepository.findByCodeAndActiveTrue(code).orElseThrow(EntityNotFoundException::new);
    link.setActive(false);
    this.linkRepository.save(link);
    log.info("Link with code: {} has been deactivated", code);
  }
}
