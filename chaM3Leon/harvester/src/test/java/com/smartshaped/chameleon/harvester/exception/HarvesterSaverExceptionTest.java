package com.smartshaped.chameleon.harvester.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HarvesterSaverExceptionTest {

  @Test
  void testHarvesterSaverExceptionThrown() {
    assertThrows(
        HarvesterSaverException.class,
        () -> {
          throw new HarvesterSaverException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the HarvesterSaver";
    HarvesterSaverException exception = new HarvesterSaverException(errorMessage);

    String expectedMessage = "Exception in the HarvesterSaver. Caused by : \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    HarvesterSaverException exception =
        assertThrows(
            HarvesterSaverException.class,
            () -> {
              throw new HarvesterSaverException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testHarvesterExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    HarvesterSaverException exception = new HarvesterSaverException(cause);

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
    HarvesterSaverException exception = new HarvesterSaverException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
