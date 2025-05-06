package com.smartshaped.chameleon.speed.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpeedLayerExceptionTest {

  @Test
  void testModelSaverExceptionThrown() {
    assertThrows(
        SpeedLayerException.class,
        () -> {
          throw new SpeedLayerException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Speed Layer";
    SpeedLayerException exception = new SpeedLayerException(errorMessage);

    String expectedMessage = "Exception in the Speed Layer. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    SpeedLayerException exception =
        assertThrows(
            SpeedLayerException.class,
            () -> {
              throw new SpeedLayerException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testModelSaverExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    SpeedLayerException exception = new SpeedLayerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Speed Layer")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testModelSaverExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    SpeedLayerException exception = new SpeedLayerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
