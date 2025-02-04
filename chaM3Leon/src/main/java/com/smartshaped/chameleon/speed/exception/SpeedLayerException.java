package com.smartshaped.chameleon.speed.exception;

public class SpeedLayerException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs during the execution of the Speed Layer.
   *
   * @param message the error message
   */
  public SpeedLayerException(String message) {
    super("Exception in the Speed Layer. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs during the execution of the Speed Layer.
   *
   * @param err the root cause of the exception
   */
  public SpeedLayerException(Throwable err) {
    super("Exception in the Speed Layer. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs during the execution of the Speed Layer.
   *
   * @param errMessage the error message
   * @param err the root cause of the exception
   */
  public SpeedLayerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
