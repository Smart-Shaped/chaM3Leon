package com.smartshaped.chameleon.batch.exception;

public class BatchUpdaterException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs during the update of a batch.
   *
   * @param message the error message
   */
  public BatchUpdaterException(String message) {
    super("Exception in the Batch Updater. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs during the update of a batch.
   *
   * @param err the error
   */
  public BatchUpdaterException(Throwable err) {
    super("Exception in the Batch Updater. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs during the update of a batch.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public BatchUpdaterException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
