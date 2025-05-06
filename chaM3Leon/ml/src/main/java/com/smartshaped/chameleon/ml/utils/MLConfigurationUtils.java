package com.smartshaped.chameleon.ml.utils;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.ml.HdfsReader;
import com.smartshaped.chameleon.ml.ModelSaver;
import com.smartshaped.chameleon.ml.Pipeline;
import com.smartshaped.chameleon.ml.blackbox.Blackbox;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class that extends {@link ConfigurationUtils} for reading configuration files related to
 * ML layer.
 */
public class MLConfigurationUtils extends ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(MLConfigurationUtils.class);

  private static final String ML_HDFS_READERS_DEFAULT = "ml.hdfs.readers.default";
  private static final String ML_HDFS_READERS = "ml.hdfs.readers";
  private static final String ML_HDFS_MODEL_DIR = "ml.hdfs.modelDir";
  private static final String ML_PIPELINE_CLASS = "ml.pipeline.class";
  private static final String ML_MODEL_SAVER_CLASS = "ml.modelSaver.class";
  private static final String CLASS = "class";
  private static final String PATH = "path";
  private static final String ROOT = "ml";
  private static final String SEPARATOR = ".";
  private static final String ML_BLACK_BOX_INPUTS = "ml.blackbox.inputs";
  private static final String ML_BLACK_BOX_OUTPUT = "ml.blackbox.output";
  private static final String ML_BLACK_BOX_MODEL_PATH = "ml.blackbox.modelPath";
  private static final String ML_BLACK_BOX_FOLDER = "ml.blackbox.folder";
  private static final String ML_BLACK_BOX_PYTHON_SCRIPT_PATH = "ml.blackbox.python.scriptPath";
  private static final String ML_BLACK_BOX_CLASS = "ml.blackbox.class";
  private static final String ML_BLACK_BOX_PYTHON_LIBRARIES = "ml.blackbox.python.libraries";
  private static final String ML_BLACK_BOX_PYTHON_EXTRA_SCRIPTS = "ml.blackbox.python.extraScripts";
  private static final String ML_BLACK_BOX_PYTHON_REQUIREMENTS_PATH =
      "ml.blackbox.python.requirementsPath";
  private static final String ML_BLACK_BOX_PYTHON_EXTRA_ARGUMENTS =
      "ml.blackbox.python.extraArguments";

  private static MLConfigurationUtils configuration;

  private MLConfigurationUtils() throws ConfigurationException {
    super();
    this.setConfRoot(ROOT.concat(SEPARATOR));

    logger.debug("MLConfigurationUtils created");
  }

  /**
   * Static method to get a MlConfigurationUtils instance.
   *
   * <p>This method will create a new instance of MlConfigurationUtils if the configuration is null,
   * otherwise it will return the existing instance.
   *
   * @return MlConfigurationUtils instance.
   * @throws ConfigurationException if any error occurs while creating the MlConfigurationUtils
   *     instance.
   */
  public static MLConfigurationUtils getMlConf() throws ConfigurationException {
    if (configuration == null) {
      logger.warn("No previous ml configuration found, loading new configurations.");
      configuration = new MLConfigurationUtils();
    }

    return configuration;
  }

  /**
   * Method to get HDFS path based on provided class name.
   *
   * @param className String representing class name.
   * @return String representing HDFS path.
   */
  public String getHDFSPath(String className) {
    logger.debug("Getting HDFS path for class: {}", className);
    String defaultValue = config.getString(ML_HDFS_READERS_DEFAULT, "");
    logger.debug("Default HDFS path: {}", defaultValue);

    Iterator<String> keys = config.getKeys(ML_HDFS_READERS);

    logger.debug("Reading configurations that starts with \"{}\"", ML_HDFS_READERS);

    int suffixLength = 6;
    String fullKey;
    String readerPrefix;
    String classConfigValue;
    String pathKey;

    while (keys.hasNext()) {
      fullKey = keys.next();

      if (fullKey.endsWith(SEPARATOR.concat(CLASS))) {
        readerPrefix = fullKey.substring(0, fullKey.length() - suffixLength);
        classConfigValue = config.getString(fullKey);

        if (className.equals(classConfigValue)) {
          pathKey = readerPrefix.concat(SEPARATOR.concat(PATH));
          logger.debug("Reader class {} found in configurations", className);
          logger.info("HDFS path successfully retrieved");
          return config.getString(pathKey, defaultValue);
        }
      }
    }

    logger.warn("Reader class {} not found in configuration file", className);
    logger.debug(
        "No specific HDFS path found for class {}, returning default: {}", className, defaultValue);
    return defaultValue;
  }

  /**
   * Returns a list of {@link HdfsReader} instances based on the configuration in the YAML file.
   *
   * <p>At least one reader must be defined in the configuration file.
   *
   * @return a list of {@link HdfsReader} instances
   * @throws ConfigurationException if no valid {@link HdfsReader} binding exists or if at least one
   *     reader is not defined
   */
  public List<HdfsReader> getHdfsReaders() throws ConfigurationException {

    Iterator<String> keys = config.getKeys(ML_HDFS_READERS);

    logger.debug("Reading configurations that starts with \"{}\"", ML_HDFS_READERS);

    List<HdfsReader> readerList = new ArrayList<>();
    String fullKey;
    String readerClassName;
    while (keys.hasNext()) {
      fullKey = keys.next();

      if (fullKey.endsWith(SEPARATOR.concat(CLASS))) {

        readerClassName = config.getString(fullKey);

        if (readerClassName == null || readerClassName.trim().isEmpty()) {
          throw new ConfigurationException(
              "At least a reader must be defined in ml.hdfs.readers config");
        }

        logger.debug("Reader class {} found in configurations", readerClassName);

        try {
          readerList.add(loadInstanceOf(readerClassName, HdfsReader.class));
        } catch (ConfigurationException e) {
          throw new ConfigurationException(
              "Could not instantiate " + HdfsReader.class + " due to com.smartshaped.chameleon.batch.exception", e);
        }
      }
    }

    return readerList;
  }

  /**
   * Method that returns the model directory from configuration file.
   *
   * @return String representing the model directory.
   */
  public String getModelDir() {

    return config.getString(ML_HDFS_MODEL_DIR, "");
  }

  /**
   * Returns an instance of the configured {@link Pipeline} class or null if the class name is
   * empty.
   *
   * <p>The class name is read from the configuration key {@link #ML_PIPELINE_CLASS}. If the class
   * is defined but cannot be instantiated, a {@link ConfigurationException} is thrown.
   *
   * @return an instance of the configured {@link Pipeline} class
   * @throws ConfigurationException if any error occurs while loading the configuration, or if the
   *     class cannot be instantiated
   */
  public Pipeline getPipeline() throws ConfigurationException {
    String pipelineClassName = config.getString(ML_PIPELINE_CLASS, "");

    if (pipelineClassName.trim().isEmpty()) {
      logger.warn("Missing or empty configuration for key: {}", ML_PIPELINE_CLASS);
      return null;
    }

    logger.debug("Pipeline class: {}", pipelineClassName);

    try {
      return loadInstanceOf(pipelineClassName, Pipeline.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + Pipeline.class + " due to com.smartshaped.chameleon.batch.exception", e);
    }
  }

  /**
   * Returns an instance of the configured {@link ModelSaver} class.
   *
   * <p>The class name is read from the configuration key {@link #ML_MODEL_SAVER_CLASS}. If the key
   * is not defined or if the class cannot be instantiated, a {@link ConfigurationException} is
   * thrown.
   *
   * @return an instance of the configured {@link ModelSaver} class
   * @throws ConfigurationException if any error occurs while loading the configuration, or if the
   *     class cannot be instantiated
   */
  public ModelSaver getModelSaver() throws ConfigurationException {
    String modelSaverClassName = config.getString(ML_MODEL_SAVER_CLASS, "");

    if (modelSaverClassName.trim().isEmpty()) {
      throw new ConfigurationException(
          "Missing or empty configuration for key: " + ML_MODEL_SAVER_CLASS);
    }

    logger.debug("Model saver class: {}", modelSaverClassName);

    try {
      return loadInstanceOf(modelSaverClassName, ModelSaver.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + ModelSaver.class + " due to com.smartshaped.chameleon.batch.exception", e);
    }
  }

  /**
   * Returns an instance of the configured {@link Blackbox} class or null if the class name is
   * empty.
   *
   * <p>The class name is read from the configuration key {@link #ML_BLACK_BOX_CLASS}. If the class
   * is defined but cannot be instantiated, a {@link ConfigurationException} is thrown.
   *
   * @return an instance of the configured {@link Blackbox} class
   * @throws ConfigurationException if any error occurs while loading the configuration, or if the
   *     class cannot be instantiated
   */
  public Blackbox getBlackbox() throws ConfigurationException {
    String blackboxClassName = config.getString(ML_BLACK_BOX_CLASS, "");

    if (blackboxClassName.trim().isEmpty()) {
      logger.debug("Missing or empty configuration for key: " + ML_BLACK_BOX_CLASS);
      return null;
    }

    logger.debug("Blackbox class: {}", blackboxClassName);

    try {
      return loadInstanceOf(blackboxClassName, Blackbox.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + Blackbox.class + " due to com.smartshaped.chameleon.batch.exception", e);
    }
  }

  /**
   * Returns the comma-separated list of input paths for the Blackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_INPUTS}. If the key is not
   * defined, an empty string is returned.
   *
   * @return the input paths for the Blackbox
   */
  public String getBlackboxInputs() {
    return config.getString(ML_BLACK_BOX_INPUTS, "");
  }

  /**
   * Returns the output path for the Blackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_OUTPUT}. If the key is not
   * defined, an empty string is returned.
   *
   * @return the output path for the Blackbox
   */
  public String getBlackboxOutput() {
    return config.getString(ML_BLACK_BOX_OUTPUT, "");
  }

  /**
   * Returns the model path for the Blackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_MODEL_PATH}. If the key is
   * not defined, an empty string is returned.
   *
   * @return the model path for the Blackbox
   */
  public String getBlackboxModelPath() {
    return config.getString(ML_BLACK_BOX_MODEL_PATH, "");
  }

  /**
   * Returns the Python script path for the PythonBlackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_PYTHON_SCRIPT_PATH}. If
   * the key is not defined, an empty string is returned.
   *
   * @return the Python script path for the Blackbox
   */
  public String getBlackboxPythonScriptPath() {
    return config.getString(ML_BLACK_BOX_PYTHON_SCRIPT_PATH, "");
  }

  /**
   * Returns the comma-separated list of Python libraries required by the PythonBlackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_PYTHON_LIBRARIES}. If the
   * key is not defined, an empty string is returned.
   *
   * @return the comma-separated list of Python libraries required by the Blackbox
   */
  public String getBlackboxPythonLibraries() {
    return config.getString(ML_BLACK_BOX_PYTHON_LIBRARIES, "");
  }

  /**
   * Returns the comma-separated list of extra Python scripts required by the PythonBlackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_PYTHON_EXTRA_SCRIPTS}. If
   * the key is not defined, an empty string is returned.
   *
   * @return the comma-separated list of extra Python scripts required by the Blackbox
   */
  public String getBlackboxPythonExtraScripts() {
    return config.getString(ML_BLACK_BOX_PYTHON_EXTRA_SCRIPTS, "");
  }

  /**
   * Returns the path to the Python requirements file for the PythonBlackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_PYTHON_REQUIREMENTS_PATH}.
   * If the key is not defined, an empty string is returned.
   *
   * @return the path to the Python requirements file for the Blackbox
   */
  public String getBlackboxPythonRequirementsPath() {
    return config.getString(ML_BLACK_BOX_PYTHON_REQUIREMENTS_PATH, "");
  }

  /**
   * Returns the folder path for the Blackbox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_FOLDER}. If the key is not
   * defined, an empty string is returned.
   *
   * @return the folder path for the Blackbox
   */
  public String getBlackboxFolder() {
    return config.getString(ML_BLACK_BOX_FOLDER, "");
  }

  /**
   * Retrieves a map of extra arguments for the PythonBlackbox.
   *
   * <p>This method iterates over the configuration keys under the base path defined by {@link
   * #ML_BLACK_BOX_PYTHON_EXTRA_ARGUMENTS} and constructs a map where each key is the segment after
   * the last period in the original key, and the value is the corresponding configuration value.
   *
   * @return a map where keys are argument names and values are the corresponding argument values.
   * @throws ConfigurationException if there is an error retrieving any argument value.
   */
  public Map<String, String> getBlackboxPythonExtraArguments() throws ConfigurationException {
    Map<String, String> params = new LinkedHashMap<>();

    Iterator<String> iterator = config.getKeys(ML_BLACK_BOX_PYTHON_EXTRA_ARGUMENTS);
    if (!iterator.hasNext()) {
      logger.warn("No keys found under base path: {}", ML_BLACK_BOX_PYTHON_EXTRA_ARGUMENTS);
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
}
