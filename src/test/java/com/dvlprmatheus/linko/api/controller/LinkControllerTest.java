package com.dvlprmatheus.linko.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dvlprmatheus.linko.api.dto.request.LinkRequest;
import com.dvlprmatheus.linko.api.dto.response.LinkShortenedResponse;
import com.dvlprmatheus.linko.service.LinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("Link controller")
class LinkControllerTest {

  @Mock private LinkService linkService;

  @InjectMocks private LinkController linkController;

  @Test
  @DisplayName("Creates a link and returns its shortened URL")
  void createsLink() {
    LinkRequest request = new LinkRequest("https://destination.example");
    LinkShortenedResponse expected = new LinkShortenedResponse("https://short.example/abc");
    when(linkService.create(request)).thenReturn(expected);

    ResponseEntity<LinkShortenedResponse> response = linkController.createLink(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected, response.getBody());
    verify(linkService).create(request);
  }

  @Test
  @DisplayName("Deactivates a link and returns no content")
  void deactivatesLink() {
    ResponseEntity<Void> response = linkController.deactivateLink("abc");

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(linkService).deactivateLink("abc");
  }
}
