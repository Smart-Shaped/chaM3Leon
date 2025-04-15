package com.smartshaped.chameleon.harvester.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestHandlerExceptionTest {

  @Test
  void testRequestHandlerExceptionThrown() {
    assertThrows(
        RequestHandlerException.class,
        () -> {
          throw new RequestHandlerException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the RequestHandler";
    RequestHandlerException exception = new RequestHandlerException(errorMessage);

    String expectedMessage = "Exception in the RequestHandler. Caused by : \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    RequestHandlerException exception =
        assertThrows(
            RequestHandlerException.class,
            () -> {
              throw new RequestHandlerException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testRequestHandlerExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    RequestHandlerException exception = new RequestHandlerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the RequestHandler")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testRequestHandlerExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    RequestHandlerException exception = new RequestHandlerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
