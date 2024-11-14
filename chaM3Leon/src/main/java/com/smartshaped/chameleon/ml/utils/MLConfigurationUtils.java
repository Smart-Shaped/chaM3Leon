package com.smartshaped.chameleon.ml.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.ml.HdfsReader;
import com.smartshaped.chameleon.ml.ModelSaver;
import com.smartshaped.chameleon.ml.Pipeline;

/**
 * Utility class that extends {@link ConfigurationUtils} for reading configuration files related to ML layer.
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

    private static MLConfigurationUtils configuration;

    private MLConfigurationUtils() throws ConfigurationException {
        super();
        this.setConfRoot(ROOT.concat(SEPARATOR));
    }

    /**
     * Static method to get a MlConfigurationUtils instance.
     * <p>
     * This method will create a new instance of MlConfigurationUtils if the
     * configuration is null, otherwise it will return the existing instance.
     *
     * @return MlConfigurationUtils instance.
     * @throws ConfigurationException if any error occurs while creating the
     *                                MlConfigurationUtils instance.
     */
    public static MLConfigurationUtils getMlConf() throws ConfigurationException {
        if (configuration == null) {
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
        logger.info("Getting HDFS path for class: {}", className);
        String defaultValue = config.getString(ML_HDFS_READERS_DEFAULT, "");
        logger.info("Default HDFS path: {}", defaultValue);

        Iterator<String> keys = config.getKeys(ML_HDFS_READERS);

        logger.info("Reading configurations that starts with \"{}\"", ML_HDFS_READERS);

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
                    logger.info("Reader class {} found in configurations", className);
                    return config.getString(pathKey, defaultValue);
                }
            }
        }

        logger.info("Reader class {} not found in configuration file", className);
        logger.info("No specific HDFS path found for class {}, returning default: {}", className, defaultValue);
        return defaultValue;
    }

    /**
     * Returns a list of {@link HdfsReader} instances based on the configuration in the YAML file.
     * <p>
     * At least one reader must be defined in the configuration file.
     *
     * @return a list of {@link HdfsReader} instances
     * @throws ConfigurationException if no valid {@link HdfsReader} binding exists or if at least one reader is not defined
     */
    public List<HdfsReader> getHdfsReaders() throws ConfigurationException {

        Iterator<String> keys = config.getKeys(ML_HDFS_READERS);

        logger.info("Reading configurations that starts with \"{}\"", ML_HDFS_READERS);

        List<HdfsReader> readerList = new ArrayList<>();
        String fullKey;
        String readerClassName;
        while (keys.hasNext()) {
            fullKey = keys.next();

            if (fullKey.endsWith(SEPARATOR.concat(CLASS))) {

                readerClassName = config.getString(fullKey);

                if (readerClassName == null || readerClassName.trim().isEmpty()) {
                    throw new ConfigurationException("At least a reader must be defined in ml.hdfs.readers config");
                }

                try {
                    readerList.add(loadInstanceOf(readerClassName, HdfsReader.class));
                } catch (Exception e) {
                    throw new ConfigurationException("Could not instantiate " + HdfsReader.class + " due to exception", e);
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
     * Returns an instance of the configured {@link Pipeline} class.
     *
     * <p>
     * The class name is read from the configuration key {@link #ML_PIPELINE_CLASS}. If
     * the key is not defined, or if the class cannot be instantiated, a
     * {@link ConfigurationException} is thrown.
     *
     * @return an instance of the configured {@link Pipeline} class
     * @throws ConfigurationException if any error occurs while loading the
     *                                configuration, or if the class cannot be
     *                                instantiated
     */
    public Pipeline getPipeline() throws ConfigurationException {
        String pipelineClassName = config.getString(ML_PIPELINE_CLASS, "");

        if (pipelineClassName.trim().isEmpty()) {
            throw new ConfigurationException("Missing or empty configuration for key: " + ML_PIPELINE_CLASS);
        }

        try {
            return loadInstanceOf(pipelineClassName, Pipeline.class);
        } catch (ConfigurationException e) {
            throw new ConfigurationException("Could not instantiate " + Pipeline.class + " due to exception", e);
        }
    }

    /**
     * Returns an instance of the configured {@link ModelSaver} class.
     *
     * <p>
     * The class name is read from the configuration key {@link #ML_MODEL_SAVER_CLASS}.
     * If the key is not defined or if the class cannot be instantiated, a
     * {@link ConfigurationException} is thrown.
     *
     * @return an instance of the configured {@link ModelSaver} class
     * @throws ConfigurationException if any error occurs while loading the
     *                                configuration, or if the class cannot be
     *                                instantiated
     */
    public ModelSaver getModelSaver() throws ConfigurationException {
        String modelSaverClassName = config.getString(ML_MODEL_SAVER_CLASS, "");

        if (modelSaverClassName.trim().isEmpty()) {
            throw new ConfigurationException("Missing or empty configuration for key: " + ML_MODEL_SAVER_CLASS);
        }

        try {
            return loadInstanceOf(modelSaverClassName, ModelSaver.class);
        } catch (ConfigurationException e) {
            throw new ConfigurationException("Could not instantiate " + ModelSaver.class + " due to exception", e);
        }
    }
}
