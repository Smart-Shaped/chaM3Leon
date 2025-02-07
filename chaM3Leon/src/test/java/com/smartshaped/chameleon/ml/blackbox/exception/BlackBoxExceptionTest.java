package com.smartshaped.chameleon.ml.blackbox.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackBoxExceptionTest {

  @Test
  void testBlackBoxExceptionThrown() {
    assertThrows(
        BlackBoxException.class,
        () -> {
          throw new BlackBoxException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the blackbox";
    BlackBoxException exception = new BlackBoxException(errorMessage);

    String expectedMessage = "Exception in the blackbox. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    BlackBoxException exception =
        assertThrows(
            BlackBoxException.class,
            () -> {
              throw new BlackBoxException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testBlackBoxExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    BlackBoxException exception = new BlackBoxException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the blackbox")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testBlackBoxExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    BlackBoxException exception = new BlackBoxException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
