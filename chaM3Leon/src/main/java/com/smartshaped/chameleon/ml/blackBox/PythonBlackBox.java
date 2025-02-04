package com.smartshaped.chameleon.ml.blackBox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackBox.exception.BlackBoxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.deploy.PythonRunner;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/** Specialization of the {@link BlackBox} class for Python-based black boxes. */
public abstract class PythonBlackBox extends BlackBox {

  private static final Logger logger = LogManager.getLogger(PythonBlackBox.class);

  protected String pythonScriptPath;
  protected String pythonLibraries;
  public static JavaSparkContext javaSparkContext;

  protected PythonBlackBox() throws ConfigurationException {

    super();

    this.pythonScriptPath = mlConfigurationUtils.getBlackBoxPythonScriptPath();
    this.pythonLibraries = mlConfigurationUtils.getBlackBoxPythonLibraries();
  }

  /**
   * Perform any additional preparation required for Python-based black boxes.
   *
   * <p>This includes installing required Python libraries and making the Python script executable.
   *
   * @throws BlackBoxException if any error occurs during preparation
   */
  @Override
  protected void extraPreparation() throws BlackBoxException {

    // install required python libraries
    installLibraries();

    // copy python script to make it executable
    copyResourceToDestination(pythonScriptPath);

    // prepare SparkSession to be accessed by python
    javaSparkContext = new JavaSparkContext(SparkSession.getActiveSession().get().sparkContext());
  }

  /**
   * Installs the Python libraries specified in the configuration file, if any.
   *
   * <p>If the configuration contains a list of Python libraries, this method will install them
   * using pip3.
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
   *
   * <p>The resource to be copied is determined by the last element of the given destination path.
   * The resource is copied to the same destination path. If the destination file already exists, it
   * is overwritten.
   *
   * <p>If the resource is not found in the classpath or if the copy operation fails, a {@link
   * BlackBoxException} is thrown.
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
   *
   * <p>It checks if the python script path is not empty and throws a {@link BlackBoxException} if
   * it is.
   *
   * @throws BlackBoxException if the python script path is empty
   */
  @Override
  protected void validateParams() throws BlackBoxException {

    if (pythonScriptPath.trim().isEmpty()) {
      throw new BlackBoxException("The python script path is empty");
    }
  }

  /**
   * Cleans up the Python script used in the black box.
   *
   * <p>This method deletes the Python script from the filesystem if it exists. It is intended to be
   * used as part of the cleanup process for Python-based black boxes. If the deletion process
   * encounters any issues, a {@link BlackBoxException} is thrown.
   *
   * @throws BlackBoxException if an error occurs while deleting the Python script
   */
  @Override
  protected void cleanBlackBoxFolder() throws BlackBoxException {

    // delete python script
    try {
      Files.deleteIfExists(new File(pythonScriptPath).toPath());
    } catch (IOException e) {
      throw new BlackBoxException("Error deleting python script", e);
    }
  }

  /**
   * Runs the machine learning script.
   *
   * <p>This method overrides the base class implementation to execute the Python-based machine
   * learning script. It uses the Apache Spark's {@link PythonRunner} to execute the script with the
   * input paths, output path, and model path as command-line arguments.
   *
   * <p>If the script execution fails due to any exception, a {@link BlackBoxException} is thrown
   * with the error details.
   *
   * @throws BlackBoxException if an error occurs during the script execution
   */
  @Override
  protected void runML() throws BlackBoxException {

    try {
      logger.info("Running ML script...");
      PythonRunner.main(
          new String[] {pythonScriptPath, pythonScriptPath, inputs, output, modelPath});
    } catch (Exception e) {
      throw new BlackBoxException("Error running ML script", e);
    }
  }

  /**
   * Reads the output of the machine learning script and returns it as a Spark Dataset<Row>.
   *
   * <p>This method reads the output of the machine learning script from a temporary view and
   * returns it as a Spark Dataset<Row>. The output view name is given as a string argument.
   *
   * <p>The method should return a Spark Dataset containing the output of the machine learning
   * script. The schema of the returned Dataset should match the schema of the output Dataset as
   * specified in the configuration.
   *
   * <p>The method should throw a BlackBoxException if any error occurs while reading the output.
   *
   * @param outputInfo the name of the output view
   * @return the output of the machine learning script as a Spark Dataset
   * @throws BlackBoxException if an error occurs while reading the output
   */
  @Override
  protected Dataset<Row> readOutput(String outputInfo) throws BlackBoxException {

    logger.info("Reading output from view: {}", outputInfo);
    SparkSession sparkSession = SparkSession.getActiveSession().get();
    String query = "SELECT * FROM " + outputInfo;
    return sparkSession.sql(query);
  }

  /**
   * Saves the given dataset as a temporary view.
   *
   * <p>This method makes the given dataset accessible to the machine learning script by saving it
   * as a temporary view. The view name is given as a string argument.
   *
   * @param dataset the dataset to be saved as a view
   * @param inputInfo the name of the view
   * @throws BlackBoxException if an error occurs during the saving process
   */
  @Override
  protected void makeDatasetAccessible(Dataset<Row> dataset, String inputInfo)
      throws BlackBoxException {

    logger.info("Saving dataset to view: {}", inputInfo);
    dataset.createOrReplaceTempView(inputInfo);
  }
}
