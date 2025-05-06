package com.smartshaped.chameleon.harvester.exception;

/** Exception thrown when an error occurs in the Harvester class and those that extends it. */
public class HarvesterException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Creates a new HarvesterException with the given message. */
  public HarvesterException(String message) {
    super("Exception in the Harvester. Caused by: \n" + message);
  }

  /** Creates a new HarvesterException with the given message and throwable. */
  public HarvesterException(Throwable err) {
    super("Exception in the Harvester. Caused by : \n" + err.getMessage(), err);
  }

  /** Creates a new HarvesterException with the given error message and throwable. */
  public HarvesterException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
