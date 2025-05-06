package com.smartshaped.chameleon.batch.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BatchUpdaterExceptionTest {

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Batch Updater";
    BatchUpdaterException exception = new BatchUpdaterException(errorMessage);

    String expectedMessage = "Exception in the Batch Updater. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testConstructorWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    BatchUpdaterException exception = new BatchUpdaterException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Batch Updater")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testConstructorWithMessageAndThrowable() {
    String errorMessage = "Exception in the Batch Updater";
    Throwable cause = new RuntimeException("Runtime error");
    BatchUpdaterException exception = new BatchUpdaterException(errorMessage, cause);

    String expectedMessage = errorMessage + "\n" + cause.getMessage();
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testExceptionThrown() {
    assertThrows(
        BatchUpdaterException.class,
        () -> {
          throw new BatchUpdaterException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Illegal Argument");
    BatchUpdaterException exception =
        assertThrows(
            BatchUpdaterException.class,
            () -> {
              throw new BatchUpdaterException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }
}
