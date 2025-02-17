package com.smartshaped.chameleon.common.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Objects;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;

import com.smartshaped.chameleon.common.exception.ConfigurationException;

/** Utility class for reading configuration files. */
public abstract class ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(ConfigurationUtils.class);

  private static final String INCLUDE = "include";
  private static final String CASSANDRA_KEYSPACE_NAME = "cassandra.keyspace.name";
  private static final String CASSANDRA_KEYSPACE_REPLICATION =
      "cassandra.keyspace.replicationFactor";
  private static final String CASSANDRA_CONNECTION_HOST = "spark.cassandra.connection.host";
  private static final String CASSANDRA_CONNECTION_PORT = "spark.cassandra.connection.port";
  private static final String CASSANDRA_DATACENTER = "cassandra.datacenter";
  private static final String CASSANDRA_CHECKPOINT = "cassandra.checkpoint";
  private static final String CASSANDRA_MODEL_CLASS = "cassandra.model.class";

  protected YAMLConfiguration config;
  protected String confRoot;

  /**
   * Initializes the configuration utility.
   *
   * <p>This constructor initializes the configuration utility by loading the configuration file
   * specified by the {@code mainConfigFile} parameter. The configuration file is processed to look
   * for the "include" keyword and the specified configuration file is loaded and merged with the
   * current one.
   *
   * <p>The configuration of the custom interpolator is also set up here.
   *
   * @throws ConfigurationException if a configuration file is not found or is invalid
   */
  protected ConfigurationUtils() throws ConfigurationException {

    logger.info("Loading configuration file");

    String mainConfigFile = "framework-config.yml";
    YAMLConfiguration ymlConfig = new YAMLConfiguration();
    FileHandler fileHandler = new FileHandler(ymlConfig);

    ConfigurationInterpolator interpolator = ymlConfig.getInterpolator();
    interpolator.registerLookup(INCLUDE, new IncludeLookup(new File(mainConfigFile).getParent()));

    try {
      fileHandler.load(mainConfigFile);
      processIncludes(ymlConfig);
      this.config = ymlConfig;
      String env = this.config.getString("env");
      logger.debug("Configuration loaded successfully for environment: {}", env);
    } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
      throw new ConfigurationException("Unable to load configuration file", e);
    }
  }

  /**
   * Recursively processes the configuration file looking for the "include" keyword, and loads the
   * specified configuration file, merging it with the current one. The "include" keyword is then
   * removed from the configuration after processing.
   *
   * @param config the configuration to be processed
   * @throws ConfigurationException if a configuration file is not found or is invalid
   */
  private void processIncludes(YAMLConfiguration config) throws ConfigurationException {
    if (config.containsKey(INCLUDE)) {
      String includeFile = config.getString(INCLUDE);

      includeFile = (String) config.getInterpolator().interpolate(includeFile);
      YAMLConfiguration includedConfig = new YAMLConfiguration();
      FileHandler includedFileHandler = new FileHandler(includedConfig);
      try {
        includedFileHandler.load(includeFile);
        config.append(includedConfig);
        config.clearProperty(INCLUDE);
      } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
        throw new ConfigurationException("Unable to load configuration file: " + includeFile, e);
      }
    }
  }

  /**
   * Retrieves the Spark configuration as a SparkConf object. The configuration is loaded from the
   * configuration file and the keys are prefixed with the value of the confRoot variable. The
   * configuration is then processed and the relevant SparkConf settings are set.
   *
   * @return A SparkConf object with the configuration settings
   */
  public SparkConf getSparkConf() {
    logger.debug("Loading Spark configuration");
    SparkConf sparkConf = new SparkConf();
    Iterator<String> keys = config.getKeys(confRoot + "spark");

    logger.debug("Reading configurations that starts with \"{}spark\"", confRoot);

    int prefixLength = confRoot.length();
    String fullKey;
    String sparkKey;
    String value;

    while (keys.hasNext()) {
      fullKey = keys.next();
      sparkKey = fullKey.substring(prefixLength);

      value = config.getString(fullKey);

      if (value != null && !value.trim().isEmpty()) {

        logger.debug("Setting Spark configuration: {} = {}", sparkKey, value);
        sparkConf.set(sparkKey, value);
      } else {
        logger.warn("Skipping empty configuration for key: {}", fullKey);
      }
    }

    logger.debug("Spark configuration retrieved successfully");
    return sparkConf;
  }

  /**
   * Load an instance of the specified class, given its name and type.
   *
   * @param className the name of the class to load
   * @param type the type that the loaded class must be an instance of
   * @return an instance of the loaded class
   * @throws ConfigurationException if the class cannot be loaded or instantiated
   */
  protected <T> T loadInstanceOf(String className, Class<T> type) throws ConfigurationException {
    logger.debug("Loading instance of class: {} for type: {}", className, type.getSimpleName());
    Class<?> instance = null;
    try {
      instance = Class.forName(className);
      return type.cast(instance.getDeclaredConstructor().newInstance());
    } catch (ClassNotFoundException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException
        | NoSuchMethodException e) {
      throw new ConfigurationException("Unable to load class: " + className, e);
    }
  }

  /**
   * Sets the prefix used to access the configuration properties.
   *
   * <p>The prefix is prepended to the property keys in the configuration file to retrieve the
   * values.
   *
   * @param confRoot the prefix to use
   */
  protected void setConfRoot(String confRoot) {
    this.confRoot = confRoot;
  }

  /**
   * Returns the Cassandra key space as specified in the configuration file. If no key space is
   * specified, an empty string is returned.
   *
   * @return The Cassandra key space.
   */
  public String getCassandraKeySpaceName() {
    return config.getString(confRoot + CASSANDRA_KEYSPACE_NAME, "");
  }

  /**
   * Returns the replication factor of the Cassandra key space as specified in the configuration
   * file. If no replication factor is specified, the default replication factor of 1 is used.
   *
   * @return The Cassandra replication factor.
   */
  public int getCassandraReplicationFactor() {
    return config.getInt(confRoot + CASSANDRA_KEYSPACE_REPLICATION, 1);
  }

  /**
   * Returns the Cassandra node as specified in the configuration file. If no node is specified, an
   * empty string is returned.
   *
   * @return The Cassandra node.
   */
  public String getCassandraNode() {
    return config.getString(confRoot + CASSANDRA_CONNECTION_HOST, "");
  }

  /**
   * Returns the Cassandra port as specified in the configuration file. If no port is specified, the
   * default port of 9042 is used.
   *
   * @return The Cassandra port.
   */
  public int getCassandraPort() {
    return config.getInt(confRoot + CASSANDRA_CONNECTION_PORT, 9042);
  }

  /**
   * Returns the Cassandra data center as specified in the configuration file. If no data center is
   * specified, an empty string is returned.
   *
   * @return The Cassandra data center.
   */
  public String getCassandraDataCenter() {
    return config.getString(confRoot + CASSANDRA_DATACENTER, "");
  }

  public String getCassandraCheckpoint() throws ConfigurationException {

    String checkpoint = config.getString(confRoot + CASSANDRA_CHECKPOINT);

    if (Objects.isNull(checkpoint)) {
      throw new ConfigurationException(
          "Checkpoint location is not defined in " + confRoot + CASSANDRA_CHECKPOINT);
    }

    return checkpoint;
  }

  /**
   * Returns the fully qualified class name of the TableModel as specified in the configuration
   * file.
   *
   * <p>If the configuration is missing or empty, an empty string is returned.
   *
   * @return The fully qualified class name of the TableModel.
   */
  public String getModelClassName() {
    return config.getString(confRoot + CASSANDRA_MODEL_CLASS, "");
  }

  /**
   * Creates an instance of the TableModel as specified in the configuration file.
   *
   * <p>The instance is created using the class name specified in the configuration file. If the
   * class name is missing or empty, a ConfigurationException is thrown.
   *
   * <p>If the class cannot be instantiated or has no valid binding, a ConfigurationException is
   * thrown.
   *
   * @param className the class name of the TableModel to create
   * @return an instance of the TableModel
   * @throws ConfigurationException if the class name is missing, empty or invalid
   */
  public TableModel createTableModel(String className) throws ConfigurationException {
    TableModel tableModel;

    if (className == null || className.trim().isEmpty()) {
      throw new ConfigurationException(
          "Missing or empty configuration for key: " + confRoot + CASSANDRA_MODEL_CLASS);
    }

    logger.debug("TableModel class: {}", className);

    try {
      tableModel = loadInstanceOf(className, TableModel.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + TableModel.class + " due to exception", e);
    }

    logger.debug("TableModel instantiated successfully");

    return tableModel;
  }
}
