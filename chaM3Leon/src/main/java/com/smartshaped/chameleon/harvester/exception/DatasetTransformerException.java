package com.smartshaped.chameleon.harvester.exception;

public class DatasetTransformerException extends Exception {

  private static final long serialVersionUID = 1L;

  public DatasetTransformerException(String message) {
    super("Exception in the DatasetTransformer. Caused by: \n" + message);
  }

  public DatasetTransformerException(Throwable err) {
    super("Exception in the DatasetTransformer. Caused by : \n" + err.getMessage(), err);
  }

  public DatasetTransformerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
