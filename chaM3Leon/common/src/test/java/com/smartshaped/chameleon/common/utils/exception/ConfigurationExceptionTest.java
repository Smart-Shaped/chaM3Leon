package com.smartshaped.chameleon.common.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationExceptionTest {

  @Test
  void testConfigurationExceptionThrown() {
    assertThrows(
        ConfigurationException.class,
        () -> {
          throw new ConfigurationException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    ConfigurationException exception =
        assertThrows(
            ConfigurationException.class,
            () -> {
              throw new ConfigurationException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testConfigurationExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    ConfigurationException exception = new ConfigurationException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testConfigurationExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    ConfigurationException exception = new ConfigurationException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
