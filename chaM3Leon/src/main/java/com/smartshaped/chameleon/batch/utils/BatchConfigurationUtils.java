package com.smartshaped.chameleon.batch.utils;

import com.smartshaped.chameleon.batch.BatchUpdater;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.preprocessing.Preprocessor;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BatchConfigurationUtils extends ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(BatchConfigurationUtils.class);

  private static final String BATCH_KAFKA_SERVER = "batch.kafka.server";
  private static final String BATCH_KAFKA_INTERVAL = "batch.kafka.intervalMs";
  private static final String BATCH_KAFKA_TOPICS = "batch.kafka.topics";
  private static final String BATCH_UPDATER_CLASS = "batch.updater.class";
  private static final String NAME = "name";
  private static final String CLASS = "class";
  private static final String PATH = "path";
  private static final String CHECKPOINT = "checkpoint";
  private static final String SEPARATOR = ".";
  private static final String ROOT = "batch";

  private static BatchConfigurationUtils configuration;

  private BatchConfigurationUtils() throws ConfigurationException {
    super();
    this.setConfRoot(ROOT.concat(SEPARATOR));

    logger.debug("BatchConfigurationUtils created");
  }

  /**
   * Static method to get a BatchConfigurationUtils instance.
   *
   * <p>This method will create a new instance of BatchConfigurationUtils if the configuration is
   * null, otherwise it will return the existing instance.
   *
   * @return BatchConfigurationUtils instance.
   * @throws ConfigurationException if any error occurs while creating the BatchConfigurationUtils
   *     instance.
   */
  public static BatchConfigurationUtils getBatchConf() throws ConfigurationException {
    logger.info("Loading batch configuration");
    if (configuration == null) {
      logger.warn("No previous batch configuration found, loading new configurations.");
      configuration = new BatchConfigurationUtils();
    }

    return configuration;
  }

  /**
   * Returns a map of topic names to their corresponding {@link Preprocessor} instances, as
   * configured in the YAML configuration file.
   *
   * @return a map of topic names to their corresponding {@link Preprocessor} instances.
   * @throws ConfigurationException if any error occurs while loading the configuration, or if a
   *     configured preprocessor can not be instantiated.
   */
  public Map<String, Preprocessor> getPreprocessors() throws ConfigurationException {

    logger.info("Loading preprocessors");

    List<HierarchicalConfiguration<ImmutableNode>> topicList =
        config.childConfigurationsAt(BATCH_KAFKA_TOPICS);

    Map<String, Preprocessor> preprocessorMap = new HashMap<>();
    String key;
    String topic;
    String preprocessorClassName;
    String topicName;

    for (HierarchicalConfiguration<ImmutableNode> topicNode : topicList) {

      topic = topicNode.getRootElementName();
      key = BATCH_KAFKA_TOPICS.concat(SEPARATOR).concat(topic);

      topicName = config.getString(key.concat(SEPARATOR).concat(NAME));

      preprocessorClassName = config.getString(key.concat(SEPARATOR).concat(CLASS));

      logger.debug("Topic name: {}", topicName);
      logger.debug("Preprocessor class name: {}", preprocessorClassName);

      try {
        preprocessorMap.put(topicName, loadInstanceOf(preprocessorClassName, Preprocessor.class));
      } catch (ConfigurationException e) {
        throw new ConfigurationException(
            "Could not instantiate " + Preprocessor.class + " due to exception", e);
      }
    }

    return preprocessorMap;
  }

  /**
   * Returns a map of Kafka configuration keys to their corresponding values, as configured in the
   * YAML configuration file.
   *
   * <p>The configuration keys are:
   *
   * <ul>
   *   <li>servers: the Kafka server (required)
   *   <li>intervalMs: the interval in milliseconds for Kafka streaming (required)
   *   <li>topics: a comma-separated list of topic names (required)
   *   <li>path: the HDFS path for each topic (required for each topic)
   *   <li>checkpoint: the checkpoint directory for each topic (required for each topic)
   * </ul>
   *
   * @return a map of Kafka configuration keys to their corresponding values.
   * @throws ConfigurationException if any error occurs while loading the configuration, or if a
   *     required key is not defined.
   */
  public Map<String, String> getKafkaConfig() throws ConfigurationException {
    logger.info("Loading Kafka configuration");
    Map<String, String> kafkaConfig = new HashMap<>();

    String kafkaServer = config.getString(BATCH_KAFKA_SERVER);

    if (!Objects.isNull(kafkaServer)) {
      kafkaConfig.put("servers", kafkaServer);
      logger.debug("Kafka server: {}", kafkaServer);
    } else {
      throw new ConfigurationException(
          "Missing server definition in " + BATCH_KAFKA_SERVER + " configuration key");
    }

    String intervalMs = config.getString(BATCH_KAFKA_INTERVAL);

    if (!Objects.isNull(intervalMs)) {
      logger.debug("Kafka interval: {}", intervalMs);
      kafkaConfig.put("intervalMs", intervalMs);
    } else {
      throw new ConfigurationException(
          "Missing interval definition in " + BATCH_KAFKA_INTERVAL + " configuration key");
    }

    List<HierarchicalConfiguration<ImmutableNode>> topicList =
        config.childConfigurationsAt(BATCH_KAFKA_TOPICS);

    if (topicList.isEmpty()) {
      throw new ConfigurationException(
          "Define at least a topic configuration as child of: ".concat(BATCH_KAFKA_TOPICS));
    }

    StringBuilder stringBuilder = new StringBuilder();

    String topicValue;
    String topic;
    String key;

    for (HierarchicalConfiguration<ImmutableNode> topicNode : topicList) {
      topic = topicNode.getRootElementName();
      key = BATCH_KAFKA_TOPICS.concat(SEPARATOR).concat(topic);
      topicValue = config.getString(key.concat(SEPARATOR).concat(NAME));
      kafkaConfig.put(
          topicValue.concat(SEPARATOR).concat(PATH),
          config.getString(key.concat(SEPARATOR).concat(PATH)));
      kafkaConfig.put(
          topicValue.concat(SEPARATOR).concat(CHECKPOINT),
          config.getString(key.concat(SEPARATOR).concat(CHECKPOINT)));
      stringBuilder.append(topicValue).append(",");
      logger.info("Added Kafka topic: {}", topicValue);
    }

    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    logger.debug("Kafka topics: {}", stringBuilder);
    kafkaConfig.put("topics", stringBuilder.toString());
    logger.info("Kafka configuration retrieved successfully");
    return kafkaConfig;
  }

  /**
   * Returns an instance of the configured {@link BatchUpdater} class.
   *
   * <p>The class name is read from the configuration key {@link #BATCH_UPDATER_CLASS}. If the key
   * is not defined, or if the class can not be instantiated, a {@link ConfigurationException} is
   * thrown.
   *
   * @return an instance of the configured {@link BatchUpdater} class, or {@code null} if the class
   *     is not configured.
   * @throws ConfigurationException if any error occurs while loading the configuration, or if the
   *     class can not be instantiated.
   */
  public BatchUpdater getBatchUpdater() throws ConfigurationException {
    logger.info("Loading BatchUpdater class");
    String batchClassName = config.getString(BATCH_UPDATER_CLASS);
    logger.debug("{}", batchClassName);

    if (batchClassName == null || batchClassName.trim().isEmpty()) {
      logger.warn("No BatchUpdater class configured");
      return null;
    }

    try {
      BatchUpdater updater = loadInstanceOf(batchClassName, BatchUpdater.class);
      logger.info("BatchUpdater instantiated successfully: {}", batchClassName);
      return updater;
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + BatchUpdater.class + " due to exception", e);
    }
  }
}
