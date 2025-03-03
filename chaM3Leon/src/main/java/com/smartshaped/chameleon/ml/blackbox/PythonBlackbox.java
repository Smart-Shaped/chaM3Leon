package com.smartshaped.chameleon.ml.blackbox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackboxException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.deploy.PythonRunner;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/** Specialization of the {@link Blackbox} class for Python-based black boxes. */
public abstract class PythonBlackbox extends Blackbox {

  private static final Logger logger = LogManager.getLogger(PythonBlackbox.class);

  protected String pythonScriptPath;
  protected String pythonExtraScripts;
  protected String pythonLibraries;
  protected String requirementsPath;
  protected boolean requirementsDefined;
  private boolean extraScriptsDefined;

  public static JavaSparkContext javaSparkContext;

  protected PythonBlackbox() throws ConfigurationException {

    super();

    this.pythonScriptPath = mlConfigurationUtils.getBlackBoxPythonScriptPath();
    logger.debug("Python script path: {}", pythonScriptPath);
    this.pythonExtraScripts = mlConfigurationUtils.getBlackBoxPythonExtraScripts();
    logger.debug("Python extra scripts: {}", pythonExtraScripts);
    this.pythonLibraries = mlConfigurationUtils.getBlackBoxPythonLibraries();
    logger.debug("Python libraries: {}", pythonLibraries);
    this.requirementsPath = mlConfigurationUtils.getBlackBoxPythonRequirementsPath();
    logger.debug("Python requirements path: {}", requirementsPath);

    this.requirementsDefined = false;
    this.extraScriptsDefined = false;

    logger.debug("PythonBlackBox initialized");
  }

  /**
   * Perform any additional preparation required for Python-based black boxes.
   *
   * <p>This includes installing required Python libraries and making the Python script executable.
   *
   * @throws BlackboxException if any error occurs during preparation
   */
  @Override
  protected void extraPreparation() throws BlackboxException {

    // copy python scripts to make them executable
    copyResourceToDestination(pythonScriptPath);
    if (extraScriptsDefined) {
      for (String script : pythonExtraScripts.split(",")) {
        copyResourceToDestination(script);
      }
    }
    // copy requirements file if it exists
    if (requirementsDefined) {
      copyResourceToDestination(requirementsPath);
    }

    // install required python libraries
    installLibraries();

    // prepare SparkSession to be accessed by python
    javaSparkContext = new JavaSparkContext(SparkSession.getActiveSession().get().sparkContext());

    logger.info("PythonBlackBox extra preparation completed");
  }

  /**
   * Installs the Python libraries specified in the configuration file, if any.
   *
   * <p>If the configuration contains a list of Python libraries, this method will install them
   * using pip3.
   *
   * <p>If a requirements file is specified in the configuration, it will be used to install the
   * required libraries.
   *
   * @throws BlackboxException if there is an error during the libraries installation
   */
  private void installLibraries() throws BlackboxException {

    if (!pythonLibraries.trim().isEmpty()) {

      String[] libraries = pythonLibraries.split(",");
      for (String library : libraries) {

        logger.info("Installing Python library: {}", library);
        ProcessBuilder processBuilder = new ProcessBuilder("pip3", "install", library);
        runCommand(processBuilder);
      }
      logger.info("Python libraries installed successfully");
    } else {
      logger.warn("No Python libraries specified in the configuration");
    }

    if (requirementsDefined) {
      ProcessBuilder processBuilder =
          new ProcessBuilder("pip3", "install", "-r", requirementsPath);
      runCommand(processBuilder);
      logger.info("Python requirements installed successfully");
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
   * BlackboxException} is thrown.
   *
   * @param destinationPath the path where the resource will be copied
   * @throws BlackboxException if the resource is not found or the copy operation fails
   */
  private void copyResourceToDestination(String destinationPath) throws BlackboxException {

    String[] pathElements = destinationPath.split("/");
    String resourcePath = pathElements[pathElements.length - 1];

    logger.info("Copying resource: {}", resourcePath);

    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new BlackboxException("Resource not found: " + resourcePath);
      }
      File destination = new File(destinationPath).getParentFile();
      Files.createDirectories(destination.toPath());
      Files.copy(is, new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

      logger.info("Resource copied successfully");
    } catch (IOException e) {
      throw new BlackboxException("Error copying resource", e);
    }
  }

  /**
   * Validates the parameters of this BlackBox.
   *
   * <p>This method checks if the Python script path is not empty and if the requirements file path
   * is empty or has a .txt extension. If any of these checks fail, a {@link BlackboxException} is
   * thrown with the error details.
   *
   * @throws BlackboxException if the parameters are invalid
   */
  @Override
  protected void validateParams() throws BlackboxException {

    if (pythonScriptPath.trim().isEmpty() || !pythonScriptPath.endsWith(".py")) {
      throw new BlackboxException("The python script path is empty or must end with .py");
    }
    this.pythonScriptPath = this.blackboxFolder + "/" + pythonScriptPath;

    this.extraScriptsDefined = !pythonExtraScripts.trim().isEmpty();
    if (extraScriptsDefined) {
      String[] scripts = pythonExtraScripts.split(",");
      for (int i = 0; i < scripts.length; i++) {
        String script = scripts[i].trim();
        if (!script.isEmpty()) {
          if (!script.endsWith(".py")) {
            throw new BlackboxException("Every script in pythonExtraScripts must end with .py");
          }
          scripts[i] = this.blackboxFolder + "/" + script;
        }
      }
      this.pythonExtraScripts = String.join(",", scripts);
    }

    this.requirementsDefined = !requirementsPath.trim().isEmpty();
    if (requirementsDefined) {
      if (!requirementsPath.endsWith(".txt")) {
        throw new BlackboxException("The requirements file must be a .txt file");
      }
      this.requirementsPath = this.blackboxFolder + "/" + requirementsPath;
    }

    logger.debug("PythonBlackBox validation completed");
  }

  /**
   * Cleans up the black box folder.
   *
   * <p>This method is implemented to remove the Python script used in the black box from the
   * filesystem. It takes the path of the script as a parameter and deletes it. If an {@link
   * IOException} is thrown during the deletion process, a {@link BlackboxException} is thrown.
   *
   * @throws BlackboxException if an error occurs during the cleanup process
   */
  @Override
  protected void cleanBlackBoxFolder() throws BlackboxException {

    deleteResourceFromFS(pythonScriptPath);
    if (extraScriptsDefined) {
      for (String script : pythonExtraScripts.split(",")) {
        if (!script.trim().isEmpty()) {
          deleteResourceFromFS(script);
        }
      }
    }
    if (requirementsDefined) {
      deleteResourceFromFS(requirementsPath);
    }

    logger.info("PythonBlackBox cleanup completed");
  }

  /**
   * Deletes a resource from the filesystem.
   *
   * <p>This method is intended to be used to delete the Python script used in the black box. It
   * takes the path of the resource to be deleted as a parameter and deletes it.If an {@link
   * IOException} is thrown during the deletion process, a {@link BlackboxException} is thrown with
   * the error details.
   *
   * @param resourcePath the path of the resource to be deleted
   * @throws BlackboxException if an error occurs while deleting the resource
   */
  private void deleteResourceFromFS(String resourcePath) throws BlackboxException {
    // delete python script
    try {
      Files.deleteIfExists(new File(resourcePath).toPath());
      logger.debug("Python script deleted successfully at: {}", resourcePath);
    } catch (IOException e) {
      throw new BlackboxException("Error deleting python script at: " + resourcePath, e);
    }
  }

  /**
   * Runs the machine learning script.
   *
   * <p>This method runs the machine learning script using the PythonRunner class. It takes the
   * python script path, the extra scripts to be used, the inputs, the output path and the model
   * path as parameters. If any error occurs during the execution of the script, a BlackBoxException
   * is thrown.
   *
   * @throws BlackboxException if an error occurs while running the ML script
   */
  @Override
  protected void runML() throws BlackboxException {

    try {
      logger.info("Running ML script...");
      PythonRunner.main(
          new String[] {pythonScriptPath, pythonExtraScripts, inputs, output, modelPath});
    } catch (Exception e) {
      throw new BlackboxException("Error running ML script", e);
    }

    logger.info("ML script completed successfully");
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
   * @throws BlackboxException if an error occurs while reading the output
   */
  @Override
  protected Dataset<Row> readOutput(String outputInfo) throws BlackboxException {

    logger.debug("Reading output from view: {}", outputInfo);
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
   * @throws BlackboxException if an error occurs during the saving process
   */
  @Override
  protected void makeDatasetAccessible(Dataset<Row> dataset, String inputInfo)
      throws BlackboxException {

    logger.debug("Saving dataset to view: {}", inputInfo);
    dataset.createOrReplaceTempView(inputInfo);
  }
}
