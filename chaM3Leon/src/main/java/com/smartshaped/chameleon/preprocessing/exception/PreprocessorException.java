package com.smartshaped.chameleon.preprocessing.exception;

public class PreprocessorException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * This class represents an exception that is thrown when an error occurs during the preprocessing
   * process.
   */
  public PreprocessorException(String message) {
    super("Exception in the Preprocessor. Caused by: \n" + message);
  }

  /**
   * This constructor is used to create a new <code>PreprocessorException</code> with a given
   * message and underlying cause.
   *
   * @param err The underlying cause.
   */
  public PreprocessorException(Throwable err) {
    super("Exception in the Preprocessor. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * This constructor is used to create a new <code>PreprocessorException</code> with a given
   * message and underlying cause.
   *
   * @param errMessage The message for the exception.
   * @param err The underlying cause.
   */
  public PreprocessorException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
