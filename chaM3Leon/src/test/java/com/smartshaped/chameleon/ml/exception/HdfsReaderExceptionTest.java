package com.smartshaped.chameleon.ml.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HdfsReaderExceptionTest {

  @Test
  void testHDFSReaderExceptionThrown() {
    assertThrows(
        HdfsReaderException.class,
        () -> {
          throw new HdfsReaderException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the HDFS Reader";
    HdfsReaderException exception = new HdfsReaderException(errorMessage);

    String expectedMessage = "Exception in the HDFS Reader. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    HdfsReaderException exception =
        assertThrows(
            HdfsReaderException.class,
            () -> {
              throw new HdfsReaderException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testHDFSReaderExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    HdfsReaderException exception = new HdfsReaderException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the HDFS Reader")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testHDFSReaderExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    HdfsReaderException exception = new HdfsReaderException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
