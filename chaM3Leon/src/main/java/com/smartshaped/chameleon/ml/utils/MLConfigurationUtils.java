package com.smartshaped.chameleon.ml.utils;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.ml.HdfsReader;
import com.smartshaped.chameleon.ml.ModelSaver;
import com.smartshaped.chameleon.ml.Pipeline;
import com.smartshaped.chameleon.ml.blackbox.BlackBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  private static final String ML_BLACK_BOX_INPUTS = "ml.blackBox.inputs";
  private static final String ML_BLACK_BOX_OUTPUT = "ml.blackBox.output";
  private static final String ML_BLACK_BOX_MODEL_PATH = "ml.blackBox.modelPath";
  private static final String ML_BLACK_BOX_PYTHON_SCRIPT_PATH = "ml.blackBox.pythonScriptPath";
  private static final String ML_BLACK_BOX_CLASS = "ml.blackBox.class";
  private static final String ML_BLACK_BOX_PYTHON_LIBRARIES = "ml.blackBox.pythonLibraries";

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
      configuration = new MLConfigurationUtils();
      logger.debug("MLConfigurationUtils instance created");
    } else {
      logger.debug("MLConfigurationUtils instance already created");
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
              "Could not instantiate " + HdfsReader.class + " due to exception", e);
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
      logger.warn("Missing or empty configuration for key: " + ML_PIPELINE_CLASS);
      return null;
    }

    logger.debug("Pipeline class: {}", pipelineClassName);

    try {
      return loadInstanceOf(pipelineClassName, Pipeline.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + Pipeline.class + " due to exception", e);
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
          "Could not instantiate " + ModelSaver.class + " due to exception", e);
    }
  }

  /**
   * Returns an instance of the configured {@link BlackBox} class or null if the class name is
   * empty.
   *
   * <p>The class name is read from the configuration key {@link #ML_BLACK_BOX_CLASS}. If the class
   * is defined but cannot be instantiated, a {@link ConfigurationException} is thrown.
   *
   * @return an instance of the configured {@link BlackBox} class
   * @throws ConfigurationException if any error occurs while loading the configuration, or if the
   *     class cannot be instantiated
   */
  public BlackBox getBlackBox() throws ConfigurationException {
    String blackBoxClassName = config.getString(ML_BLACK_BOX_CLASS, "");

    if (blackBoxClassName.trim().isEmpty()) {
      logger.debug("Missing or empty configuration for key: " + ML_BLACK_BOX_CLASS);
      return null;
    }

    logger.debug("BlackBox class: {}", blackBoxClassName);

    try {
      return loadInstanceOf(blackBoxClassName, BlackBox.class);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Could not instantiate " + BlackBox.class + " due to exception", e);
    }
  }

  /**
   * Returns the comma-separated list of input paths for the BlackBox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_INPUTS}. If the key is not
   * defined, an empty string is returned.
   *
   * @return the input paths for the BlackBox
   */
  public String getBlackBoxInputs() {
    return config.getString(ML_BLACK_BOX_INPUTS, "");
  }

  /**
   * Returns the output path for the BlackBox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_OUTPUT}. If the key is not
   * defined, an empty string is returned.
   *
   * @return the output path for the BlackBox
   */
  public String getBlackBoxOutput() {
    return config.getString(ML_BLACK_BOX_OUTPUT, "");
  }

  /**
   * Returns the model path for the BlackBox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_MODEL_PATH}. If the key is
   * not defined, an empty string is returned.
   *
   * @return the model path for the BlackBox
   */
  public String getBlackBoxModelPath() {
    return config.getString(ML_BLACK_BOX_MODEL_PATH, "");
  }

  /**
   * Returns the Python script path for the PythonBlackBox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_PYTHON_SCRIPT_PATH}. If
   * the key is not defined, an empty string is returned.
   *
   * @return the Python script path for the BlackBox
   */
  public String getBlackBoxPythonScriptPath() {
    return config.getString(ML_BLACK_BOX_PYTHON_SCRIPT_PATH, "");
  }

  /**
   * Returns the comma-separated list of Python libraries required by the PythonBlackBox.
   *
   * <p>The value is read from the configuration key {@link #ML_BLACK_BOX_PYTHON_LIBRARIES}. If the
   * key is not defined, an empty string is returned.
   *
   * @return the comma-separated list of Python libraries required by the BlackBox
   */
  public String getBlackBoxPythonLibraries() {
    return config.getString(ML_BLACK_BOX_PYTHON_LIBRARIES, "");
  }
}
