package com.smartshaped.chameleon.harvester.exception;

/**
 * Exception thrown when an error occurs inside the Harvester Layer class and those that extends it.
 */
public class HarvesterLayerException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Constructs a new HarvesterLayerException with the specified detail message. */
  public HarvesterLayerException(String message) {
    super("Exception in the Harvester Layer. Caused by: \n" + message);
  }

  /** Constructs a new HarvesterLayerException with the specified cause. */
  public HarvesterLayerException(Throwable err) {
    super("Exception in the Harvester Layer. Caused by : \n" + err.getMessage(), err);
  }

  /** Constructs a new HarvesterLayerException with the specified detail message and cause. */
  public HarvesterLayerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
