package com.smartshaped.chameleon.harvester.exception;

public class HarvesterSaverException extends Exception {

  private static final long serialVersionUID = 1L;

  public HarvesterSaverException(String message) {
    super("Exception in the HarvesterSaver. Caused by : \n" + message);
  }

  public HarvesterSaverException(Throwable err) {
    super("Exception in the HarvesterSaver. Caused by : \n" + err.getMessage(), err);
  }

  public HarvesterSaverException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
