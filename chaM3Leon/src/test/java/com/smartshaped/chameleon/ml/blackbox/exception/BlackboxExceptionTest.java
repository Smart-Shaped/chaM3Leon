package com.smartshaped.chameleon.ml.blackbox.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackboxExceptionTest {

  @Test
  void testBlackBoxExceptionThrown() {
    assertThrows(
        BlackboxException.class,
        () -> {
          throw new BlackboxException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the blackbox";
    BlackboxException exception = new BlackboxException(errorMessage);

    String expectedMessage = "Exception in the blackbox. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    BlackboxException exception =
        assertThrows(
            BlackboxException.class,
            () -> {
              throw new BlackboxException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testBlackBoxExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    BlackboxException exception = new BlackboxException(cause);

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
    BlackboxException exception = new BlackboxException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
