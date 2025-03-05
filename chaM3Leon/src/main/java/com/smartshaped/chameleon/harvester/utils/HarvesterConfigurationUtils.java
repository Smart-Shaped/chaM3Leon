package com.smartshaped.chameleon.harvester.utils;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.harvester.Harvester;
import com.smartshaped.chameleon.harvester.downloader.Downloader;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.preprocessing.EmptyPreprocessor;
import com.smartshaped.chameleon.preprocessing.Preprocessor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class that extends {@link ConfigurationUtils} for reading configuration files related to
 * Harvester layer.
 */
public class HarvesterConfigurationUtils extends ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(HarvesterConfigurationUtils.class);

  private static final String SEPARATOR = ".";
  private static final String ROOT = "harvester";
  private static final String CLASS = "class";
  private static final String PATH = "path";
  private static final String SAVER = "saver";
  private static final String PREPROCESSOR = "preprocessor";
  private static final String DOWNLOADER = "downloader";
  private static final String HARVESTERS = "harvester.harvesters";
  private static final String URL = "url";
  private static final String PARAMS = "params";
  private static final String DUE_TO_EXCEPTION = " due to exception";
  private static final String COULD_NOT_INSTANTIATE = "Could not instantiate ";
  private static final String INPUT_PATH = "inputPath";
  private static final String HDFS_PATH = "hdfs-path";

  private static HarvesterConfigurationUtils configuration;

  private HarvesterConfigurationUtils() throws ConfigurationException {
    super();
    this.setConfRoot(ROOT.concat(SEPARATOR));
  }

  /**
   * Gets the instance of the HarvesterConfigurationUtils class, which is responsible for loading
   * the configurations for the Harvester layer.
   *
   * <p>If the configuration is not yet loaded, it will be loaded and stored in the static variable.
   *
   * <p>
   *
   * @return the instance of the HarvesterConfigurationUtils class
   * @throws ConfigurationException if there is an error loading the configurations
   */
  public static HarvesterConfigurationUtils getHarvesterConf() throws ConfigurationException {
    logger.debug("Loading harvester configuration");
    if (configuration == null) {
      logger.debug("No previous harvester configuration found, loading new configurations");
      configuration = new HarvesterConfigurationUtils();
    }
    return configuration;
  }

  /**
   * Gets the harvester ID for the given class name.
   *
   * <p>This method iterates over the list of harvesters in the configuration and returns the
   * harvester ID for the class name that matches the given argument.
   *
   * <p>
   *
   * @param className the class name to get the harvester ID for
   * @return the harvester ID
   * @throws ConfigurationException if no harvester ID is found for the given class name
   */
  public String getHarvesterId(String className) throws ConfigurationException {
    logger.debug("Retrieving harvester ID for class: {}", className);
    List<HierarchicalConfiguration<ImmutableNode>> harvesterList =
        config.childConfigurationsAt(HARVESTERS);
    for (HierarchicalConfiguration<ImmutableNode> harvesterNode : harvesterList) {
      String nodeClassName = harvesterNode.getString(CLASS);
      if (className.equals(nodeClassName)) {
        return HARVESTERS + SEPARATOR + harvesterNode.getRootElementName();
      }
    }
    throw new ConfigurationException("No harvester ID found for class: " + className);
  }

  /**
   * Returns a list of instances of the Harvester class, based on the harvesters listed in the
   * configuration.
   *
   * <p>The method iterates over the list of harvesters in the configuration and instantiates each
   * one using the {@link #loadInstanceOf(String, Class)} method. The instantiated harvesters are
   * then returned as a list.
   *
   * <p>
   *
   * @return the list of harvesters
   * @throws ConfigurationException if no harvesters are defined in the configuration or if there is
   *     an error while instantiating a harvester
   */
  public List<Harvester> getHarvesters() throws ConfigurationException {
    List<Harvester> harvesters = new LinkedList<>();
    Iterator<String> keys = config.getKeys(HARVESTERS);
    String fullKey;
    String harvesterClassName;
    String[] segments;

    logger.debug("Retrieving harvesters...");

    while (keys.hasNext()) {
      fullKey = keys.next();
      segments = fullKey.split("\\.");
      if (segments.length == 4 && "harvesters".equals(segments[1]) && CLASS.equals(segments[3])) {
        harvesterClassName = config.getString(fullKey);
        if (harvesterClassName == null || harvesterClassName.trim().isEmpty()) {
          throw new ConfigurationException(
              "At least a harvester must be defined in harvester.harvesters config");
        }
        try {
          harvesters.add(loadInstanceOf(harvesterClassName, Harvester.class));
          logger.debug("Harvester class '{}' succesfully loaded", harvesterClassName);
        } catch (ConfigurationException e) {
          throw new ConfigurationException(
              COULD_NOT_INSTANTIATE + Harvester.class.toString() + DUE_TO_EXCEPTION, e);
        }
      }
    }

    return harvesters;
  }

  /**
   * Retrieves an instance of the RequestHandler class based on the configuration.
   *
   * <p>The method attempts to fetch the RequestHandler class name from the configuration, and if it
   * is set to "default", it returns a new instance of the default RequestHandler. Otherwise, it
   * tries to instantiate the specified RequestHandler class.
   *
   * <p>
   *
   * @return an instance of RequestHandler
   * @throws ConfigurationException if no RequestHandler is defined in the configurations or if
   *     there is an error during instantiation
   * @throws CassandraException if there is an error related to Cassandra operations
   */
  public RequestHandler getRequestHandler() throws ConfigurationException, CassandraException {
    String handlerName = config.getString(ROOT.concat(SEPARATOR) + "RequestHandler");
    logger.debug("Loading RequestHandler: {}", handlerName);

    if (handlerName == null) {
      throw new ConfigurationException(
          "No RequestHandler defined in configurations in harvester.RequestHandler");
    }
    if (handlerName.equals("default")) {
      return new RequestHandler();
    }
    try {
      return loadInstanceOf(handlerName, RequestHandler.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          COULD_NOT_INSTANTIATE + RequestHandler.class + DUE_TO_EXCEPTION, e);
    }
  }

  /**
   * Retrieves a map of preprocessor parameters for the specified class.
   *
   * <p>This method constructs the base path for the preprocessor parameters using the provided
   * class name and iteratively fetches each key-value pair from the configuration. If a key
   * contains a period, only the segment after the last period is used as the final key in the map.
   *
   * <p>
   *
   * @param className the name of the class for which preprocessor parameters are being retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws RuntimeException if there is an error retrieving any parameter value.
   */
  public Map<String, String> getPreprocessorParams(String className) throws ConfigurationException {
    logger.debug("Starting to retrieve preprocessor parameters for class: {}", className);

    Map<String, String> params = new HashMap<>();
    String basePath = confRoot + PREPROCESSOR + SEPARATOR + className + SEPARATOR + PARAMS;
    logger.debug("Base path for preprocessor keys: {}", basePath);

    Iterator<String> iterator = config.getKeys(basePath);
    if (!iterator.hasNext()) {
      logger.warn("No keys found under base path: {}", basePath);
    }

    while (iterator.hasNext()) {
      String key = iterator.next();
      logger.debug("Processing key: {}", key);

      try {
        String originalKey = key;
        int lastDotIndex = key.lastIndexOf(SEPARATOR);
        if (lastDotIndex != -1) {
          key = key.substring(lastDotIndex + 1);
        }
        String value = config.getString(originalKey);
        logger.debug("Retrieved value for key '{}': {}", originalKey, value);
        params.put(key, value);
      } catch (Exception e) {
        throw new ConfigurationException("Failed to retrieve value for key: " + key, e);
      }
    }

    logger.debug(
        "Finished retrieving preprocesssor parameters for class: {}. Total parameters: {}",
        className,
        params.size());

    logger.info("Finished retrieving preprocessor parameters");
    return params;
  }

  /**
   * Retrieves a preprocessor instance based on the preprocessor name configured for the provided
   * harvester ID.
   *
   * <p>If no preprocessor is specified in the configurations for the given harvester ID, an empty
   * preprocessor is used.
   *
   * <p>
   *
   * @param harvesterId the ID of the harvester for which the preprocessor is being retrieved.
   * @return an instance of the preprocessor class configured for the given harvester ID.
   * @throws ConfigurationException if there is an error while loading the preprocessor instance.
   */
  public Preprocessor getPreprocessor(String harvesterId) throws ConfigurationException {
    String preprocessorName = config.getString(harvesterId + SEPARATOR.concat(PREPROCESSOR), "");

    if (preprocessorName.trim().isEmpty()) {
      logger.warn("No preprocessor specified in the configurations, using empty one...");
      return new EmptyPreprocessor();
    }

    try {
      return loadInstanceOf(preprocessorName, Preprocessor.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          COULD_NOT_INSTANTIATE + RequestHandler.class + DUE_TO_EXCEPTION, e);
    }
  }

  /**
   * Retrieves a map of url parameters for the specified class.
   *
   * <p>The method constructs the base path for the url parameters using the provided class name and
   * iteratively fetches each key-value pair from the configuration. If a key contains a period,
   * only the segment after the last period is used as the final key in the map.
   *
   * <p>
   *
   * @param className the name of the class for which url parameters are being retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws ConfigurationException if there is an error retrieving any parameter value.
   */
  public Map<String, String> getUrlParams(String className) throws ConfigurationException {
    logger.debug("Starting to retrieve url parameters for class: {}", className);

    String basePath = confRoot + DOWNLOADER + SEPARATOR + className + SEPARATOR + URL;
    logger.debug("Base path for configuration keys: {}", basePath);

    return getParams(basePath);
  }

  /**
   * Retrieves a map of query parameters for the specified class.
   *
   * <p>The method constructs the base path for the query parameters using the provided class name
   * and iteratively fetches each key-value pair from the configuration. If a key contains a period,
   * only the segment after the last period is used as the final key in the map.
   *
   * <p>
   *
   * @param className the name of the class for which query parameters are being retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws ConfigurationException if there is an error retrieving any parameter value.
   */
  public Map<String, String> getQueryParam(String className) throws ConfigurationException {
    logger.debug("Starting to retrieve query parameters for class: {}", className);
    String basePath = confRoot + DOWNLOADER + SEPARATOR + className + SEPARATOR + PARAMS;
    logger.debug("Base path for configuration keys: {}", basePath);

    return getParams(basePath);
  }

  /**
   * Retrieves a map of parameters for a specified base path in the configuration.
   *
   * <p>This method iterates over the configuration keys under the given base path and constructs a
   * map where each key is the segment after the last period in the original key, and the value is
   * the corresponding configuration value.
   *
   * <p>
   *
   * @param basePath the base path in the configuration from which parameters are retrieved.
   * @return a map where keys are parameter names and values are the corresponding parameter values.
   * @throws ConfigurationException if there is an error retrieving any parameter value.
   */
  private Map<String, String> getParams(String basePath) throws ConfigurationException {
    Map<String, String> params = new HashMap<>();

    Iterator<String> iterator = config.getKeys(basePath);
    if (!iterator.hasNext()) {
      logger.warn("No keys found under base path: {}", basePath);
    }

    while (iterator.hasNext()) {
      String key = iterator.next();
      logger.debug("Processing key: {}", key);
      try {
        String originalKey = key;
        int lastDotIndex = key.lastIndexOf(SEPARATOR);
        if (lastDotIndex != -1) {
          key = key.substring(lastDotIndex + 1);
        }
        String value = config.getString(originalKey);
        logger.debug("Retrieved value for key '{}': {}", originalKey, value);
        params.put(key, value);
      } catch (Exception e) {
        throw new ConfigurationException("Failed to retrieve value for key: " + key, e);
      }
    }

    logger.info("Finished retrieving parameters");
    logger.debug("Total parameters: {}", params.size());

    return params;
  }

  /**
   * Retrieves a Downloader instance based on the downloader name configured for the given harvester
   * ID.
   *
   * <p>This method fetches the downloader class name from the configuration using the provided
   * harvester ID. If no class name is specified, it throws a ConfigurationException. Otherwise, it
   * attempts to load and instantiate the downloader class.
   *
   * <p>
   *
   * @param harvesterId the ID of the harvester for which the downloader is being retrieved.
   * @return an instance of the configured Downloader class.
   * @throws ConfigurationException if no downloader is specified in the configurations or if there
   *     is an error during instantiation.
   */
  public Downloader getDownloader(String harvesterId) throws ConfigurationException {
    logger.debug("Attempting to retrieve downloader name ");
    logger.debug("Harvester ID: {}", harvesterId);
    String downloaderName =
        config.getString(
            harvesterId + SEPARATOR.concat(DOWNLOADER.concat(SEPARATOR.concat(CLASS))));

    if (downloaderName == null) {
      throw new ConfigurationException("No downloader specified in the configurations.");
    }

    try {
      logger.debug("Loading instance of downloader: {}", downloaderName);
      return loadInstanceOf(downloaderName, Downloader.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          COULD_NOT_INSTANTIATE + Downloader.class + DUE_TO_EXCEPTION, e);
    }
  }

  /**
   * Retrieves the HDFS path for a specified downloader class.
   *
   * <p>This method constructs the HDFS path using the provided class name by appending it to the
   * base path defined in the configuration. If the constructed path is null, a
   * ConfigurationException is thrown.
   *
   * <p>
   *
   * @param className the name of the downloader class for which the HDFS path is being retrieved.
   * @return the HDFS path for the specified downloader class.
   * @throws ConfigurationException if no HDFS path is specified in the configurations.
   */
  public String getDownloaderHdfsPath(String className) throws ConfigurationException {
    logger.debug("Attempting to retrieve downloader hdfs path String ");
    logger.debug("Downloader : {}", className);

    return config.getString(
        confRoot + DOWNLOADER + SEPARATOR + className + SEPARATOR + PATH + SEPARATOR + HDFS_PATH);
  }

  /**
   * Retrieves the output path configured for the given harvester ID.
   *
   * <p>This method fetches the output path from the configuration using the provided harvester ID.
   * If no output path is specified in the configurations, an empty string is returned.
   *
   * <p>
   *
   * @param harvesterId the ID of the harvester for which the output path is being retrieved.
   * @return the output path configured for the given harvester ID.
   */
  public String getOutputPath(String harvesterId) {
    return config.getString(
        harvesterId + SEPARATOR.concat(SAVER.concat(SEPARATOR.concat(PATH))), "");
  }

  /**
   * Retrieves the input path configured for the given harvester ID.
   *
   * <p>This method fetches the input path from the configuration using the provided harvester ID.
   * If no input path is specified in the configurations, an empty string is returned.
   *
   * <p>
   *
   * @param harvesterId the ID of the harvester for which the input path is being retrieved.
   * @return the input path configured for the given harvester ID.
   */
  public String getInputPath(String harvesterId) {
    String path = harvesterId + SEPARATOR + INPUT_PATH;
    logger.debug("Input path: {}", path);
    return config.getString(path, "");
  }
}
