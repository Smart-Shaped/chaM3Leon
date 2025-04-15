package com.smartshaped.chameleon.harvester.exception;

/** Exception thrown when an error occurs in the HarvesterSaver class. */
public class HarvesterSaverException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Creates a new instance with the given message. */
  public HarvesterSaverException(String message) {
    super("Exception in the HarvesterSaver. Caused by : \n" + message);
  }

  /** Creates a new instance wrapping the given throwable. */
  public HarvesterSaverException(Throwable err) {
    super("Exception in the HarvesterSaver. Caused by : \n" + err.getMessage(), err);
  }

  /** Creates a new instance with the given message and wrapping the given throwable. */
  public HarvesterSaverException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
