package com.smartshaped.chameleon.ml.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MLLayerExceptionTest {

  @Test
  void testMLLayerExceptionThrown() {
    assertThrows(
        MLLayerException.class,
        () -> {
          throw new MLLayerException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the ML Layer";
    MLLayerException exception = new MLLayerException(errorMessage);

    String expectedMessage = "Exception in the ML Layer. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    MLLayerException exception =
        assertThrows(
            MLLayerException.class,
            () -> {
              throw new MLLayerException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testMLLayerExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    MLLayerException exception = new MLLayerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the ML Layer")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testMLLayerExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    MLLayerException exception = new MLLayerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
