package com.smartshaped.chameleon.ml.exception;

/** Java class to manage exceptions inside HdfsReader class and those that extends it. */
public class HdfsReaderException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs during the read from Hdfs.
   *
   * @param message the error message
   */
  public HdfsReaderException(String message) {
    super("Exception in the HDFS Reader. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs during the read from Hdfs.
   *
   * @param err the error
   */
  public HdfsReaderException(Throwable err) {
    super("Exception in the HDFS Reader. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs during the read from Hdfs.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public HdfsReaderException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
