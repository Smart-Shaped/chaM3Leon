package com.smartshaped.chameleon.ml.blackbox.exception;

/** Java class to manage exceptions inside BlackBox class and those that extends it. */
public class BlackboxException extends Exception {

  public BlackboxException(String message) {
    super("Exception in the blackbox. Caused by: \n" + message);
  }

  public BlackboxException(Throwable err) {
    super("Exception in the blackbox. Caused by : \n" + err.getMessage(), err);
  }

  public BlackboxException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
