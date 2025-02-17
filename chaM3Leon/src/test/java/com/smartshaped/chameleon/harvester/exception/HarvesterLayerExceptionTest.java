package com.smartshaped.chameleon.harvester.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HarvesterLayerExceptionTest {

  @Test
  void testHarvesterLayerExceptionThrown() {
    assertThrows(
        HarvesterLayerException.class,
        () -> {
          throw new HarvesterLayerException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Harvester Layer";
    HarvesterLayerException exception = new HarvesterLayerException(errorMessage);

    String expectedMessage = "Exception in the Harvester Layer. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    HarvesterLayerException exception =
        assertThrows(
            HarvesterLayerException.class,
            () -> {
              throw new HarvesterLayerException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testHarvesterLayerExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    HarvesterLayerException exception = new HarvesterLayerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Harvester Layer")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testHarvesterLayerExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    HarvesterLayerException exception = new HarvesterLayerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
