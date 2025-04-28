package com.smartshaped.chameleon.ml.exception;

/** Java class to manage exceptions inside Pipeline class and those that extends it. */
public class PipelineException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs during Pipeline execution.
   *
   * @param message the error message
   */
  public PipelineException(String message) {
    super("Exception in the ML Pipeline. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs during Pipeline execution.
   *
   * @param err the error
   */
  public PipelineException(Throwable err) {
    super("Exception in the ML Pipeline. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs during Pipeline execution.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public PipelineException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
