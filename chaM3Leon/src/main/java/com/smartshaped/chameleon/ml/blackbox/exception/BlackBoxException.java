package com.smartshaped.chameleon.ml.blackbox.exception;

/** Java class to manage exceptions inside BlackBox class and those that extends it. */
public class BlackBoxException extends Exception {

  public BlackBoxException(String message) {
    super("Exception in the blackbox. Caused by: \n" + message);
  }

  public BlackBoxException(Throwable err) {
    super("Exception in the blackbox. Caused by : \n" + err.getMessage(), err);
  }

  public BlackBoxException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
