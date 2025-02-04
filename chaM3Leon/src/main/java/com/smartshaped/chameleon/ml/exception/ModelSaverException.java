package com.smartshaped.chameleon.ml.exception;

/** Java class to manage exceptions inside ModelSaver class and those that extends it. */
public class ModelSaverException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when an error occurs saving model to Cassandra.
   *
   * @param message the error message
   */
  public ModelSaverException(String message) {
    super("Exception in the Model Saver. Caused by: \n" + message);
  }

  /**
   * Exception thrown when an error occurs saving model to Cassandra.
   *
   * @param err the error
   */
  public ModelSaverException(Throwable err) {
    super("Exception in the Model Saver. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Exception thrown when an error occurs saving model to Cassandra.
   *
   * @param errMessage the error message
   * @param err the error
   */
  public ModelSaverException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
