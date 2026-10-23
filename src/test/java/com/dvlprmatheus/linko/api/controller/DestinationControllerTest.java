package com.dvlprmatheus.linko.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dvlprmatheus.linko.api.dto.response.LinkDestinationResponse;
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
@DisplayName("Destination controller")
class DestinationControllerTest {

  @Mock private LinkService linkService;

  @InjectMocks private DestinationController destinationController;

  @Test
  @DisplayName("Redirects permanently to the destination of an active code")
  void redirectsToDestination() {
    when(linkService.getActiveLinkByCode("abc"))
        .thenReturn(new LinkDestinationResponse("https://destination.example/path"));

    ResponseEntity<Void> response = destinationController.getDestinationByCode("abc");

    assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
    assertEquals(
        "https://destination.example/path", response.getHeaders().getLocation().toString());
    verify(linkService).getActiveLinkByCode("abc");
  }

  @Test
  @DisplayName("Rejects a destination that cannot be parsed as a redirect URI")
  void rejectsInvalidDestinationUri() {
    when(linkService.getActiveLinkByCode("abc"))
        .thenReturn(new LinkDestinationResponse("://invalid"));

    assertThrows(
        IllegalArgumentException.class, () -> destinationController.getDestinationByCode("abc"));
  }
}
