package com.smartshaped.chameleon.harvester.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HarvesterExceptionTest {

  @Test
  void testHarvesterExceptionThrown() {
    assertThrows(
        HarvesterException.class,
        () -> {
          throw new HarvesterException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Harvester";
    HarvesterException exception = new HarvesterException(errorMessage);

    String expectedMessage = "Exception in the Harvester. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    HarvesterException exception =
        assertThrows(
            HarvesterException.class,
            () -> {
              throw new HarvesterException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testHarvesterExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    HarvesterException exception = new HarvesterException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Harvester")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testHarvesterExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    HarvesterException exception = new HarvesterException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
