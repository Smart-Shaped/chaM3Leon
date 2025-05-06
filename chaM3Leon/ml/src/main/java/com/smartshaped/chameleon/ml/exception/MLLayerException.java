package com.smartshaped.chameleon.ml.exception;

/** Java class to manage exceptions inside MLLayer class and those that extends it. */
public class MLLayerException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs in ML Layer.
   *
   * @param message the error message
   */
  public MLLayerException(String message) {
    super("Exception in the ML Layer. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs in ML Layer.
   *
   * @param err the error
   */
  public MLLayerException(Throwable err) {
    super("Exception in the ML Layer. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs in ML Layer.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public MLLayerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
