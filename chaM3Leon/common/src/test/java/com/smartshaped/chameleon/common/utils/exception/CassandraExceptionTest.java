package com.smartshaped.chameleon.common.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CassandraExceptionTest {

  @Test
  void testCassandraExceptionThrown() {
    assertThrows(
        CassandraException.class,
        () -> {
          throw new CassandraException("Test com.smartshaped.chameleon.batch.exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception related to Cassandra database";
    CassandraException exception = new CassandraException(errorMessage);

    String expectedMessage =
        "Exception related to Cassandra database. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    CassandraException exception =
        assertThrows(
            CassandraException.class,
            () -> {
              throw new CassandraException("Test com.smartshaped.chameleon.batch.exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testCassandraExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    CassandraException exception = new CassandraException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () ->
            assertTrue(exception.getMessage().contains("Exception related to Cassandra database")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testCassandraExceptionWithMessageAndThrowable() {
    String errorMessage = "Exception related to Cassandra database";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    CassandraException exception = new CassandraException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
