package com.smartshaped.chameleon.ml.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PipelineExceptionTest {

  @Test
  void testPipelineExceptionThrown() {
    assertThrows(
        PipelineException.class,
        () -> {
          throw new PipelineException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the ML Pipeline";
    PipelineException exception = new PipelineException(errorMessage);

    String expectedMessage = "Exception in the ML Pipeline. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    PipelineException exception =
        assertThrows(
            PipelineException.class,
            () -> {
              throw new PipelineException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testPipelineExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    PipelineException exception = new PipelineException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the ML Pipeline")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testPipelineExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    PipelineException exception = new PipelineException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
