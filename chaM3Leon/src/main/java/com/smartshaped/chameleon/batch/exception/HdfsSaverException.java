package com.smartshaped.chameleon.batch.exception;

public class HdfsSaverException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs while saving data to HDFS.
   *
   * @param message the error message
   */
  public HdfsSaverException(String message) {
    super("Exception in the Hdfs Saver. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs while saving data to HDFS.
   *
   * @param err the error
   */
  public HdfsSaverException(Throwable err) {
    super("Exception in the Hdfs Saver. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs while saving data to HDFS.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public HdfsSaverException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
