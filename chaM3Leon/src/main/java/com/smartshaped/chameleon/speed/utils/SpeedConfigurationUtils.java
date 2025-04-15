package com.smartshaped.chameleon.speed.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.speed.SpeedUpdater;

public class SpeedConfigurationUtils extends ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(SpeedConfigurationUtils.class);

  private static final String SPEED_KAFKA_SERVER = "speed.kafka.server";
  private static final String SPEED_KAFKA_TOPICS = "speed.kafka.topics";
  private static final String SPEED_KAFKA_INTERVAL = "speed.kafka.intervalMs";
  private static final String SPEED_UPDATER_CLASS = "speed.updater.class";
  private static final String NAME = "name";
  private static final String PATH = "path";
  private static final String CHECKPOINT = "checkpoint";
  private static final String SEPARATOR = ".";
  private static final String ROOT = "speed";

  private static SpeedConfigurationUtils configuration;

  private SpeedConfigurationUtils() throws ConfigurationException {
    super();
    this.setConfRoot(ROOT.concat(SEPARATOR));

    logger.debug("SpeedConfigurationUtils created");
  }

  /**
   * Returns an instance of the SpeedConfigurationUtils, which encapsulates the speed related
   * configurations.
   *
   * <p>If no previous instance of this class has been created, it will load the speed
   * configurations from the YAML configuration file.
   *
   * @return an instance of the SpeedConfigurationUtils, which encapsulates the speed related
   *     configurations.
   * @throws ConfigurationException if any error occurs while loading the configuration.
   */
  public static SpeedConfigurationUtils getSpeedConf() throws ConfigurationException {
    logger.debug("Loading speed configuration");
    if (configuration == null) {
      logger.debug("No previous speed configuration found, loading new configurations.");

      configuration = new SpeedConfigurationUtils();
    }

    return configuration;
  }

  /**
   * Returns a map of Kafka configuration keys to their corresponding values, as configured in the
   * YAML configuration file.
   *
   * <p>The configuration keys are:
   *
   * <ul>
   *   <li>servers: the Kafka server (required)
   *   <li>intervalMs: the interval in milliseconds for Kafka records (required)
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
    logger.debug("Loading Kafka configuration");
    Map<String, String> kafkaConfig = new HashMap<>();

    String kafkaServer = config.getString(SPEED_KAFKA_SERVER);

    if (!Objects.isNull(kafkaServer)) {
      kafkaConfig.put("servers", kafkaServer);
      logger.debug("Kafka server: {}", kafkaServer);
    } else {
      throw new ConfigurationException(
          "Missing server definition in " + SPEED_KAFKA_SERVER + " configuration key");
    }

    String intervalMs = config.getString(SPEED_KAFKA_INTERVAL);

    logger.debug("Kafka interval: {}", intervalMs);

    if (!Objects.isNull(intervalMs)) {
      logger.debug("Kafka interval: {}", intervalMs);
      kafkaConfig.put("intervalMs", intervalMs);
    } else {
      throw new ConfigurationException(
          "Missing interval definition in " + SPEED_KAFKA_INTERVAL + " configuration key");
    }

    List<HierarchicalConfiguration<ImmutableNode>> topicList =
        config.childConfigurationsAt(SPEED_KAFKA_TOPICS);

    if (topicList.isEmpty()) {
      throw new ConfigurationException(
          "Define at least a topic configuration as child of: ".concat(SPEED_KAFKA_TOPICS));
    }

    StringBuilder stringBuilder = new StringBuilder();

    String topicValue;
    String topic;
    String key;

    for (HierarchicalConfiguration<ImmutableNode> topicNode : topicList) {
      topic = topicNode.getRootElementName();
      key = SPEED_KAFKA_TOPICS.concat(SEPARATOR).concat(topic);
      topicValue = config.getString(key.concat(SEPARATOR).concat(NAME));
      kafkaConfig.put(
          topicValue.concat(SEPARATOR).concat(PATH),
          config.getString(key.concat(SEPARATOR).concat(PATH)));
      kafkaConfig.put(
          topicValue.concat(SEPARATOR).concat(CHECKPOINT),
          config.getString(key.concat(SEPARATOR).concat(CHECKPOINT)));
      stringBuilder.append(topicValue).append(",");
      logger.debug("Added Kafka topic: {}", topicValue);
    }

    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    logger.debug("Kafka topics: {}", stringBuilder);
    kafkaConfig.put("topics", stringBuilder.toString());
    logger.debug("Kafka configuration retrieved successfully");
    return kafkaConfig;
  }

  /**
   * Loads the configured SpeedUpdater class.
   *
   * <p>The SpeedUpdater class is configured in the configuration key: {@value #SPEED_UPDATER_CLASS}
   *
   * <p>If the class is not configured a {@link ConfigurationException} is thrown.
   *
   * @return the loaded SpeedUpdater class instance.
   * @throws ConfigurationException if the class is not configured, or if there is an error while
   *     loading the class.
   */
  public SpeedUpdater getSpeedUpdater() throws ConfigurationException {
    logger.debug("Loading SpeedUpdater class");
    String speedClassName = config.getString(SPEED_UPDATER_CLASS);

    if (speedClassName == null || speedClassName.trim().isEmpty()) {
      throw new ConfigurationException("SpeedUpdater class is not configured");
    }

    try {
      SpeedUpdater updater = loadInstanceOf(speedClassName, SpeedUpdater.class);
      logger.debug("SpeedUpdater instantiated successfully: {}", speedClassName);
      return updater;
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + SpeedUpdater.class + " due to exception", e);
    }
  }
}
