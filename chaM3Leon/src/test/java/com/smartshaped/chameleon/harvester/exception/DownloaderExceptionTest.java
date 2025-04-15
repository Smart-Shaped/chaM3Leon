package com.smartshaped.chameleon.harvester.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DownloaderExceptionTest {

  @Test
  void testDownloaderExceptionThrown() {
    assertThrows(
        DownloaderException.class,
        () -> {
          throw new DownloaderException("Test exception");
        });
  }

  @Test
  void testConstructorWithMessage() {
    String errorMessage = "Exception in the Downloader ";
    DownloaderException exception = new DownloaderException(errorMessage);

    String expectedMessage = "Exception in the Downloader. Caused by: \n" + errorMessage;
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testExceptionThrownWithCause() {
    Throwable cause = new IllegalArgumentException("Original cause");
    DownloaderException exception =
        assertThrows(
            DownloaderException.class,
            () -> {
              throw new DownloaderException("Test exception", cause);
            });
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testDownloaderExceptionWithThrowable() {
    Throwable cause = new RuntimeException("Runtime error");
    DownloaderException exception = new DownloaderException(cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains("Exception in the Downloader")),
        () -> assertTrue(exception.getMessage().contains("Runtime error")),
        () -> assertEquals(cause, exception.getCause()));
  }

  @Test
  void testDownloaderExceptionWithMessageAndThrowable() {
    String errorMessage = "Error when reading from hdfs";
    Throwable cause = new IllegalArgumentException("Invalid argument");
    DownloaderException exception = new DownloaderException(errorMessage, cause);

    assertAll(
        () -> assertNotNull(exception),
        () -> assertTrue(exception.getMessage().contains(errorMessage)),
        () -> assertTrue(exception.getMessage().contains("Invalid argument")),
        () -> assertEquals(cause, exception.getCause()));
  }
}
