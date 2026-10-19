package com.dvlprmatheus.linko.api.controller;

import com.dvlprmatheus.linko.api.dto.response.LinkDestinationResponse;
import com.dvlprmatheus.linko.service.LinkService;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RequestMapping
@RestController
public class DestinationController {

  private final LinkService linkService;

  @GetMapping("/{code}")
  public ResponseEntity<Void> getDestinationByCode(@PathVariable String code) {
    log.info("GET /{} called", code);
    LinkDestinationResponse response = linkService.getActiveLinkByCode(code);
    return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
        .location(URI.create(response.destinationUrl()))
        .build();
  }
}
