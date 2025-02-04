package com.smartshaped.chameleon.speed;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.exception.KafkaConsumerException;
import com.smartshaped.chameleon.common.utils.KafkaConsumer;
import com.smartshaped.chameleon.speed.exception.SpeedLayerException;
import com.smartshaped.chameleon.speed.exception.SpeedUpdaterException;
import com.smartshaped.chameleon.speed.utils.SpeedConfigurationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.Map;

public abstract class SpeedLayer {

  private static final Logger logger = LogManager.getLogger(SpeedLayer.class);

  private final SpeedConfigurationUtils configurationUtils;
  private final SparkSession sparkSession;
  SpeedUpdater speedUpdater;
  private Map<String, String> kafkaConfig;

  /**
   * Initializes the SpeedLayer instance.
   *
   * <p>This constructor instantiates a new SpeedLayer with the loaded configuration and Spark
   * session. It also loads the SpeedUpdater specified in the configuration.
   *
   * <p>If any error occurs during the initialization process, a SpeedLayerException is thrown.
   *
   * @throws SpeedLayerException if there is an error initializing the SpeedLayer instance
   * @throws ConfigurationException if there is an error loading the configuration
   */
  protected SpeedLayer() throws SpeedLayerException, ConfigurationException {

    this.configurationUtils = SpeedConfigurationUtils.getSpeedConf();
    logger.info("Speed configurations loaded correctly");

    SparkConf sparkConf = configurationUtils.getSparkConf();
    logger.info("Spark configurations loaded correctly");

    try {
      logger.info("Instantiating Spark Session");
      sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
      logger.info("Spark Session created");
    } catch (Exception e) {
      throw new SpeedLayerException("Error getting or creating SparkSession", e);
    }

    this.kafkaConfig = configurationUtils.getKafkaConfig();
    logger.info("Kafka configurations loaded correctly");

    this.speedUpdater = configurationUtils.getSpeedUpdater();
    logger.info("Speed Updater loaded correctly");
  }

  /**
   * Starts the SpeedLayer process.
   *
   * <p>This method initializes the data flow by reading data from Kafka using the specified Kafka
   * configurations and Spark session. It then triggers the SpeedUpdater to process the incoming
   * data at the configured interval. The method waits for any streaming queries to terminate and
   * handles exceptions that may occur during the process.
   *
   * @throws KafkaConsumerException if there is an error reading from Kafka
   * @throws CassandraException if there is an error related to Cassandra operations
   * @throws SpeedLayerException if there is an error in the SpeedLayer process
   * @throws NumberFormatException if the intervalMs configuration cannot be parsed to a Long
   * @throws SpeedUpdaterException if there is an error in the SpeedUpdater process
   * @throws ConfigurationException if there is a configuration error
   */
  public void start()
      throws KafkaConsumerException,
          CassandraException,
          SpeedLayerException,
          NumberFormatException,
          SpeedUpdaterException,
          ConfigurationException {

    logger.info("Starting SpeedLayer...");

    Dataset<Row> df = KafkaConsumer.kafkaRead(kafkaConfig, sparkSession);

    speedUpdater.startUpdate(df, Long.parseLong(kafkaConfig.get("intervalMs")));
    logger.info("SpeedUpdater completed.");

    try {
      sparkSession.streams().awaitAnyTermination();
    } catch (StreamingQueryException e) {
      throw new SpeedLayerException("Error awayting all Spark Streaming queries termination", e);
    }

    sparkSession.stop();

    logger.info("SpeedLayer completed successfully");
  }
}
