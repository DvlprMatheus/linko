package com.dvlprmatheus.linko.api.controller;

import com.dvlprmatheus.linko.api.dto.request.LinkRequest;
import com.dvlprmatheus.linko.api.dto.response.LinkShortenedResponse;
import com.dvlprmatheus.linko.service.LinkService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RequestMapping("/api/v1/links")
@RestController
public class LinkController {

  private final LinkService linkService;

  @PostMapping("/create")
  public ResponseEntity<LinkShortenedResponse> createLink(@RequestBody LinkRequest linkRequest) {
    log.info("POST /links called");
    LinkShortenedResponse response = linkService.create(linkRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{code}")
  public ResponseEntity<Void> deactivateLink(@PathVariable String code) {
    log.info("DELETE /links/{} called", code);
    linkService.deactivateLink(code);
    return ResponseEntity.noContent().build();
  }
}
