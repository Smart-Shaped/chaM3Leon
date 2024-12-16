package com.smartshaped.chameleon.ml.blackBox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackBox.exception.BlackBoxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Specialization of the {@link BlackBox} class for Python-based black boxes.
 */
public abstract class PythonBlackBox extends BlackBox {

    private static final Logger logger = LogManager.getLogger(PythonBlackBox.class);

    private final String pythonScriptPath;
    private final String pythonLibraries;
    private final boolean pysparkApp;
    private SparkConf sparkConf;

    protected PythonBlackBox() throws ConfigurationException {

        super();

        this.pythonScriptPath = mlConfigurationUtils.getBlackBoxPythonScriptPath();
        this.pythonLibraries = mlConfigurationUtils.getBlackBoxPythonLibraries();
        this.pysparkApp = mlConfigurationUtils.isPysparkApp();
    }

    /**
     * Installs the Python libraries specified in the configuration file, if any.
     *
     * @throws BlackBoxException if there is an error during the libraries installation
     */
    @Override
    protected void extraPreparation() throws BlackBoxException {

        // install required python libraries
        installLibraries();

        // copy python script to make it executable
        copyResourceToDestination(pythonScriptPath);

        closeSession();
    }

    /**
     * Closes the active Spark session and saves its configuration.
     * <p>
     * This method is used to close the Spark session after it has been used in the black box process.
     * The configuration of the active session is saved in the {@link #sparkConf} field.
     */
    private void closeSession() {

        if (SparkSession.getActiveSession().isDefined()) {
            SparkSession session = SparkSession.getActiveSession().get();
            this.sparkConf = session.sparkContext().conf();
            session.stop();
        }
    }

    /**
     * Installs the Python libraries specified in the configuration file, if any.
     * <p>
     * If the configuration contains a list of Python libraries, this method will
     * install them using pip3.
     *
     * @throws BlackBoxException if there is an error during the libraries installation
     */
    private void installLibraries() throws BlackBoxException {

        if (!pythonLibraries.trim().isEmpty()) {

            String[] libraries = pythonLibraries.split(",");
            for (String library : libraries) {

                logger.info("Installing Python library: {}", library);
                ProcessBuilder processBuilder = new ProcessBuilder("pip3", "install", library);
                runCommand(processBuilder);
            }
        }
    }

    /**
     * Copies a resource from the classpath to a destination path in the filesystem.
     * <p>
     * The resource to be copied is determined by the last element of the given
     * destination path. The resource is copied to the same destination path. If
     * the destination file already exists, it is overwritten.
     * <p>
     * If the resource is not found in the classpath or if the copy operation
     * fails, a {@link BlackBoxException} is thrown.
     *
     * @param destinationPath the path where the resource will be copied
     * @throws BlackBoxException if the resource is not found or the copy operation fails
     */
    private void copyResourceToDestination(String destinationPath) throws BlackBoxException {

        String[] pathElements = destinationPath.split("/");
        String resourcePath = pathElements[pathElements.length - 1];

        logger.info("Copying resource: {}", resourcePath);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new BlackBoxException("Resource not found: " + resourcePath);
            }
            File destination = new File(destinationPath).getParentFile();
            if (!destination.exists()) {
                boolean created = destination.mkdirs();
                if (created) {
                    logger.info("Destination folder created: {}", destinationPath);
                }
            }
            Files.copy(is, new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BlackBoxException("Error copying resource", e);
        }
    }

    /**
     * Validates the parameters of this BlackBox.
     * <p>
     * It checks if the python script path is not empty and throws a
     * {@link BlackBoxException} if it is.
     *
     * @throws BlackBoxException if the python script path is empty
     */
    @Override
    protected void validateParams() throws BlackBoxException {

        super.validateParams();

        if (pythonScriptPath.trim().isEmpty()) {
            throw new BlackBoxException("The python script path is empty");
        }
    }

    /**
     * Cleans the black box folder by deleting the input, output, and Python script files.
     * <p>
     * This method overrides the base class implementation to include the deletion of
     * the Python script file used in the Python-based black box. It first calls the
     * superclass method to handle the standard input and output folder cleanup, then
     * attempts to delete the Python script file.
     * <p>
     * If the Python script file cannot be deleted due to an I/O error, a
     * {@link BlackBoxException} is thrown with the error details.
     *
     * @throws BlackBoxException if any error occurs during the deletion process
     */
    @Override
    protected void cleanBlackBoxFolder() throws BlackBoxException {
        super.cleanBlackBoxFolder();

        // delete python script
        try {
            Files.deleteIfExists(new File(pythonScriptPath).toPath());
        } catch (IOException e) {
            throw new BlackBoxException("Error deleting python script", e);
        }
    }

    /**
     * Executes the machine learning script for the Python-based black box.
     * <p>
     * This method constructs a command to either run the script using Spark or Python,
     * depending on whether the black box is configured as a PySpark application.
     * The input paths, output path, and model path are passed as arguments to the script.
     * <p>
     * If the script execution fails, a {@link BlackBoxException} is thrown with the error details.
     *
     * @throws BlackBoxException if an error occurs while running the machine learning script
     */
    @Override
    protected void runML() throws BlackBoxException {
        // command building
        ProcessBuilder processBuilder;
        if (pysparkApp) {
            processBuilder = new ProcessBuilder("spark-submit", pythonScriptPath,
                    inputPaths, outputPath, modelPath);
        } else {
            processBuilder = new ProcessBuilder("python3", pythonScriptPath,
                    inputPaths, outputPath, modelPath);
        }

        // command execution
        try {
            logger.info("Running ML script...");
            runCommand(processBuilder);
        } catch (BlackBoxException e) {
            throw new BlackBoxException("Error running ML script", e);
        }
    }

    /**
     * Post-processing step to be executed after the machine learning script has completed.
     * <p>
     * This implementation opens a Spark session by invoking the {@link #openSession()} method.
     * Subclasses can override this method to perform any additional post-processing tasks.
     */
    @Override
    protected void postRunning() {
        openSession();
    }

    /**
     * Opens a Spark session.
     * <p>
     * This method creates a Spark session using the configuration defined in
     * {@link #sparkConf} and stores it in a local variable.
     */
    private void openSession() {
        SparkSession.builder().config(sparkConf).getOrCreate();
    }
}
