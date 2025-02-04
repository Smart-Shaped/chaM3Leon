package com.smartshaped.chameleon.common.utils;

import com.smartshaped.chameleon.common.exception.KafkaConsumerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Map;

public class KafkaConsumer {

  private static final Logger logger = LogManager.getLogger(KafkaConsumer.class);

  /**
   * Utility class for reading from Kafka.
   *
   * <p>This class provides a single static method for reading from Kafka, both in Speed and Batch
   * Layer.
   */
  private KafkaConsumer() throws KafkaConsumerException {
    throw new KafkaConsumerException(
        "KafkaConsumer is an utility class and you can access to its methods in a static way");
  }

  /**
   * Read from Kafka in streaming mode, subscribing to different topics loaded from kafkaConfig.
   *
   * @param kafkaConfig Mappa con le configurazioni di Kafka.
   * @param sparkSession Sessione Spark.
   * @return Dataset<Row> Dataset letto da Kafka.
   * @throws KafkaConsumerException Se avviene un errore durante la lettura.
   */
  public static Dataset<Row> kafkaRead(Map<String, String> kafkaConfig, SparkSession sparkSession)
      throws KafkaConsumerException {

    String topics = kafkaConfig.get("topics");
    Dataset<Row> df;

    try {
      logger.info("Instantiating Kafka consumer in streaming mode.");

      df =
          sparkSession
              .readStream()
              .format("kafka")
              .option("kafka.bootstrap.servers", kafkaConfig.get("servers"))
              .option("subscribe", topics)
              .option("startingOffsets", "latest")
              .load();

      logger.info("Dataset subscribed to Kafka on topics: {}", topics);

    } catch (Exception e) {
      throw new KafkaConsumerException(
          "An error occurred during subscribing to Kafka. Cause: " + e.getMessage(), e);
    }

    return df;
  }
}
