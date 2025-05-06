package com.smartshaped.chameleon.speed.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpeedUpdaterExceptionTest {

  @Test
  void testModelSaverExceptionThrown() {
    assertThrows(
        SpeedUpdaterException.class,
        () -> {
          throw new SpeedUpdaterException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Speed Updater";
    SpeedUpdaterException exception = new SpeedUpdaterException(errorMessage);

    String expectedMessage = "Exception in the Speed Updater. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    SpeedUpdaterException exception =
        assertThrows(
            SpeedUpdaterException.class,
            () -> {
              throw new SpeedUpdaterException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testModelSaverExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    SpeedUpdaterException exception = new SpeedUpdaterException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Speed Updater")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testModelSaverExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    SpeedUpdaterException exception = new SpeedUpdaterException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
