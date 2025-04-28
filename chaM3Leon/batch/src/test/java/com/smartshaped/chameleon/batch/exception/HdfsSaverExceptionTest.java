package com.smartshaped.chameleon.batch.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HdfsSaverExceptionTest {

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Hdfs Saver";
    HdfsSaverException exception = new HdfsSaverException(errorMessage);

    String expectedMessage = "Exception in the Hdfs Saver. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testConstructorWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    HdfsSaverException exception = new HdfsSaverException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Hdfs Saver")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testConstructorWithMessageAndThrowable() {
    String errorMessage = "Exception in the Hdfs Saver";
    Throwable cause = new RuntimeException("Runtime error");
    HdfsSaverException exception = new HdfsSaverException(errorMessage, cause);

    String expectedMessage = errorMessage + "\n" + cause.getMessage();
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testExceptionThrown() {
    assertThrows(
        HdfsSaverException.class,
        () -> {
          throw new HdfsSaverException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Illegal Argument");
    HdfsSaverException exception =
        assertThrows(
            HdfsSaverException.class,
            () -> {
              throw new HdfsSaverException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }
}
