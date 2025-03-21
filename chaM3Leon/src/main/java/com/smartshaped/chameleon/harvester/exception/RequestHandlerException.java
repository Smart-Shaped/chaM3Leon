package com.smartshaped.chameleon.harvester.exception;

/** Exception thrown when an error occurs during the execution of the RequestHandler class. */
public class RequestHandlerException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Constructs a new RequestHandlerException with the specified detail message. */
  public RequestHandlerException(String message) {
    super("Exception in the RequestHandler. Caused by : \n" + message);
  }

  /** Constructs a new RequestHandlerException with the specified cause. */
  public RequestHandlerException(Throwable err) {
    super("Exception in the RequestHandler. Caused by : \n" + err.getMessage(), err);
  }

  /** Constructs a new RequestHandlerException with the specified detail message and cause. */
  public RequestHandlerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
