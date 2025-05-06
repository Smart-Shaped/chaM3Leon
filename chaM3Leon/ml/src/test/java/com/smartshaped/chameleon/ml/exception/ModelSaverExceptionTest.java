package com.smartshaped.chameleon.ml.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModelSaverExceptionTest {

  @Test
  void testModelSaverExceptionThrown() {
    assertThrows(
        ModelSaverException.class,
        () -> {
          throw new ModelSaverException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Model Saver";
    ModelSaverException exception = new ModelSaverException(errorMessage);

    String expectedMessage = "Exception in the Model Saver. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    ModelSaverException exception =
        assertThrows(
            ModelSaverException.class,
            () -> {
              throw new ModelSaverException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testModelSaverExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    ModelSaverException exception = new ModelSaverException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Model Saver")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testModelSaverExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    ModelSaverException exception = new ModelSaverException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
