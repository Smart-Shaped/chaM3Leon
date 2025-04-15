package com.smartshaped.chameleon.harvester.exception;

/** Exception thrown when an error occurs in the Downloader. */
public class DownloaderException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Constructs a new exception with the specified detail message. */
  public DownloaderException(String message) {
    super("Exception in the Downloader. Caused by: \n" + message);
  }

  /** Constructs a new exception with the specified cause and a detail message. */
  public DownloaderException(Throwable err) {
    super("Exception in the Downloader. Caused by : \n" + err.getMessage(), err);
  }

  /** Constructs a new exception with the specified detail message and cause. */
  public DownloaderException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
