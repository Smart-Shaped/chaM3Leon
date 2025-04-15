package com.smartshaped.chameleon.common.exception;

public class KafkaConsumerException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new KafkaConsumerException with the specified detail message.
   *
   * @param message the detail message.
   */
  public KafkaConsumerException(String message) {
    super("Exception related to Kafka consumer. Caused by: \n" + message);
  }

  /**
   * Constructs a new KafkaConsumerException with the specified cause.
   *
   * @param err the cause of the exception.
   */
  public KafkaConsumerException(Throwable err) {
    super("Exception related to Kafka consumer. Caused by: \n" + err.getMessage(), err);
  }

  /**
   * Constructs a new KafkaConsumerException with the specified detail message and cause.
   *
   * @param errMessage the detail message.
   * @param err the cause of the exception.
   */
  public KafkaConsumerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
