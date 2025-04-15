package com.smartshaped.chameleon.batch.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BatchLayerExceptionTest {

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Batch Layer";
    BatchLayerException exception = new BatchLayerException(errorMessage);

    String expectedMessage = "Exception in the Batch Layer. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testBatchLayerExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    BatchLayerException exception = new BatchLayerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Batch Layer")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testBatchLayerExceptionWithMessageAndThrowable() {
    String errorMessage = "Error during batch process";
    Throwable cause = new IllegalArgumentException("Illegal Argument");
    BatchLayerException exception = new BatchLayerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Illegal Argument")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testBatchLayerExceptionThrown() {
    assertThrows(
        BatchLayerException.class,
        () -> {
          throw new BatchLayerException(new RuntimeException("Test exception"));
        });
  }
}
