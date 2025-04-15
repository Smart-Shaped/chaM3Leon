package com.smartshaped.chameleon.common.exception;

/** Exception thrown when an error occurs during the interaction with the Cassandra database. */
public class CassandraException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the given message.
   *
   * @param message the message of the exception
   */
  public CassandraException(String message) {
    super("Exception related to Cassandra database. Caused by: \n" + message);
  }

  /**
   * Constructs a new exception with the given cause.
   *
   * @param err the cause of the exception
   */
  public CassandraException(Throwable err) {
    super("Exception related to Cassandra database. Caused by : \n" + err.getMessage(), err);
  }

  /**
   * Constructs a new exception with the given message and cause.
   *
   * @param errMessage the message of the exception
   * @param err the cause of the exception
   */
  public CassandraException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
