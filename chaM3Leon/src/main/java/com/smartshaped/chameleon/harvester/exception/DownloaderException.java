package com.smartshaped.chameleon.harvester.exception;

public class DownloaderException extends Exception {

  private static final long serialVersionUID = 1L;

  public DownloaderException(String message) {
    super("Exception in the Downloader. Caused by: \n" + message);
  }

  public DownloaderException(Throwable err) {
    super("Exception in the Downloader. Caused by : \n" + err.getMessage(), err);
  }

  public DownloaderException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
