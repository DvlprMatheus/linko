package com.dvlprmatheus.linko.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.dvlprmatheus.linko.api.dto.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("Global exception handler")
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  @DisplayName("Returns a not found error response for missing links")
  void returnsNotFoundResponse() {
    MockHttpServletRequest request = request("/abc");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleEntityNotFoundException(
            new EntityNotFoundException("Link not found"), request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().status());
    assertEquals("Not Found", response.getBody().error());
    assertEquals("Link not found", response.getBody().message());
    assertEquals("/abc", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  @DisplayName("Returns an internal server error response for unexpected exceptions")
  void returnsInternalServerErrorResponse() {
    MockHttpServletRequest request = request("/api/v1/links/create");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleException(new IllegalStateException("Unexpected error"), request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().status());
    assertEquals("Internal Server Error", response.getBody().error());
    assertEquals("Unexpected error", response.getBody().message());
    assertEquals("/api/v1/links/create", response.getBody().path());
    assertNotNull(response.getBody().timestamp());
  }

  private MockHttpServletRequest request(String path) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(path);
    return request;
  }
}
