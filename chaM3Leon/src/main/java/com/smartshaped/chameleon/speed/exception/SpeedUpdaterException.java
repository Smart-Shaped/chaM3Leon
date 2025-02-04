package com.smartshaped.chameleon.speed.exception;

public class SpeedUpdaterException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new SpeedUpdaterException with the specified detail message.
   *
   * @param message the error message
   */
  public SpeedUpdaterException(String message) {
    super("Exception in the Speed Updater. Caused by: \n" + message);
  }

  /**
   * Constructs a new SpeedUpdaterException with the specified cause.
   *
   * @param err the cause of the exception
   */
  public SpeedUpdaterException(Throwable err) {
    super("Exception in the Speed Updater. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Constructs a new SpeedUpdaterException with the specified detail message and cause.
   *
   * @param errMessage the error message
   * @param err the cause of the exception
   */
  public SpeedUpdaterException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
