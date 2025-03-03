package com.smartshaped.chameleon.harvester.exception;

public class RequestHandlerException extends Exception {

  private static final long serialVersionUID = 1L;

  public RequestHandlerException(String message) {
    super("Exception in the RequestHandler. Caused by : \n" + message);
  }

  public RequestHandlerException(Throwable err) {
    super("Exception in the RequestHandler. Caused by : \n" + err.getMessage(), err);
  }

  public RequestHandlerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
