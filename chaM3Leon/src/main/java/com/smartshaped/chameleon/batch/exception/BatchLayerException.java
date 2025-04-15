package com.smartshaped.chameleon.batch.exception;

public class BatchLayerException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs in Batch Layer.
   *
   * @param message the error message
   */
  public BatchLayerException(String message) {
    super("Exception in the Batch Layer. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs in Batch Layer.
   *
   * @param err the error
   */
  public BatchLayerException(Throwable err) {
    super("Exception in the Batch Layer. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs in Batch Layer.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public BatchLayerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
