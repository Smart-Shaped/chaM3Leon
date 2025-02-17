package com.smartshaped.chameleon.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerExceptionTest {

  @Test
  void testKafkaConsumerExceptionThrown() {
    assertThrows(
        KafkaConsumerException.class,
        () -> {
          throw new KafkaConsumerException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception related to Kafka consumer";
    KafkaConsumerException exception = new KafkaConsumerException(errorMessage);

    String expectedMessage = "Exception related to Kafka consumer. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    KafkaConsumerException exception =
        assertThrows(
            KafkaConsumerException.class,
            () -> {
              throw new KafkaConsumerException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testKafkaConsumerExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    KafkaConsumerException exception = new KafkaConsumerException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception related to Kafka consumer")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testKafkaConsumerExceptionWithMessageAndThrowable() {
    String errorMessage = "Exception related to Kafka consumer";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    KafkaConsumerException exception = new KafkaConsumerException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
