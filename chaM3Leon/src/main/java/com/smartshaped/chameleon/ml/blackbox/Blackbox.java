package com.smartshaped.chameleon.ml.blackbox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackboxException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lombok.Getter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/** Java abstract class representing a black box for machine learning. */
public abstract class Blackbox {

  private static final Logger logger = LogManager.getLogger(Blackbox.class);

  protected MLConfigurationUtils mlConfigurationUtils;
  protected String inputs;
  protected String output;
  protected String modelPath;
  protected String blackboxFolder;

  @Getter private Dataset<Row> predictions;

  protected Blackbox() throws ConfigurationException {

    this.mlConfigurationUtils = MLConfigurationUtils.getMlConf();
    logger.info("ML configurations loaded correctly");

    this.inputs = mlConfigurationUtils.getBlackBoxInputs();
    logger.debug("Retrieved input paths: {}", inputs);
    this.output = mlConfigurationUtils.getBlackBoxOutput();
    logger.debug("Retrieved output path: {}", output);
    this.modelPath = mlConfigurationUtils.getBlackBoxModelPath();
    logger.debug("Retrieved model path: {}", modelPath);
    this.blackboxFolder = mlConfigurationUtils.getBlackBoxFolder();
    logger.debug("Retrieved blackbox folder: {}", blackboxFolder);

    logger.debug("BlackBox initialized");
  }

  public void start(List<Dataset<Row>> datasets) throws BlackboxException {

    logger.info("Starting BlackBox...");
    writeInputs(datasets);

    extraPreparation();
    runML();
    postRunning();

    logger.info("Reading predictions...");
    this.predictions = readOutput(output);

    cleanBlackBoxFolder();

    logger.info("BlackBox process completed successfully");
  }

  /**
   * Deletes the specified folder from the filesystem.
   *
   * <p>This method attempts to delete the folder located at the given path. If the folder exists,
   * it is deleted, and a success message is logged. If the folder does not exist or deletion fails,
   * an appropriate message is logged.
   *
   * <p>
   *
   * @param folderPath the path of the folder to be deleted
   * @throws BlackboxException if an error occurs during the deletion process
   */
  protected void deleteHdfsFolder(String folderPath) throws BlackboxException {

    Configuration configuration = new Configuration();

    try {
      FileSystem fileSystem = FileSystem.get(configuration);
      Path path = new Path(folderPath);

      if (fileSystem.exists(path)) {

        boolean deleted = fileSystem.delete(path, true);
        if (deleted) {
          logger.info("Folder deleted successfully: {}", folderPath);
        } else {
          logger.warn("Failed to delete folder: {}", folderPath);
        }
      } else {
        logger.warn("Folder does not exist: {}", folderPath);
      }

      fileSystem.close();
    } catch (IOException e) {
      throw new BlackboxException("Error deleting folder " + folderPath, e);
    }
  }

  /**
   * Runs the given command and logs its output and exit code.
   *
   * <p>This method starts the given command and logs its output line by line. After the command has
   * finished running, it logs the exit code of the command. If the command exited with a non-zero
   * exit code, this method throws a {@link BlackboxException}.
   *
   * @param processBuilder the process builder containing the command to run
   * @throws BlackboxException if the command exited with a non-zero exit code
   */
  protected void runCommand(ProcessBuilder processBuilder) throws BlackboxException {
    try {
      logger.info("Running command {}", processBuilder.command());
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        logger.debug(line);
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new BlackboxException("Error running command, exit code: " + exitCode);
      } else {
        logger.info("Command completed successfully");
      }

    } catch (IOException e) {
      throw new BlackboxException("Error running command", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new BlackboxException("Error running command", e);
    }
  }

  private void writeInputs(List<Dataset<Row>> datasets) throws BlackboxException {

    String[] inputsSplit = this.inputs.split(",");
    datasets = mergeDatasetsIfNecessary(datasets);

    if (inputsSplit.length != datasets.size()) {
      throw new BlackboxException("Number of input paths does not match number of datasets");
    }
    for (int i = 0; i < inputsSplit.length; i++) {
      makeDatasetAccessible(datasets.get(i), inputsSplit[i]);
    }
  }

  /**
   * Runs the black box machine learning process.
   *
   * <p>This method must be implemented by the subclasses and should perform the actual machine
   * learning process using the provided input paths, output path, and model path.
   *
   * @throws BlackboxException if any error occurs during the black box process
   */
  protected abstract void runML() throws BlackboxException;

  /**
   * Performs any necessary preparation before executing the machine learning script.
   *
   * <p>This method is intended to be overridden by subclasses to implement any additional setup or
   * preparation required before the ML script is run. This could include operations such as
   * installing python libraries.
   */
  protected abstract void extraPreparation() throws BlackboxException;

  /**
   * Merges the datasets if necessary.
   *
   * <p>It is intended to be implemented in the subclasses of this class.
   *
   * <p>The method should take the list of datasets as input and return a new list of datasets that
   * will be written to the input paths for the black box. The aim is to make sure that the number
   * of datasets to write is equal to the number of input paths in the configuration.
   *
   * <p>The default implementation of this method in this class is to return the input list of
   * datasets as is.
   *
   * @param datasets the list of datasets
   * @return the merged list of datasets
   * @throws BlackboxException if any error occurs during the merging
   */
  protected abstract List<Dataset<Row>> mergeDatasetsIfNecessary(List<Dataset<Row>> datasets)
      throws BlackboxException;

  /**
   * Post-processing step after the machine learning script has finished running.
   *
   * <p>Subclasses can override this method to perform any additional post-processing steps.
   */
  protected abstract void postRunning() throws BlackboxException;

  /**
   * Reads the output of the machine learning script and returns it as a Spark Dataset<Row>.
   *
   * <p>This method must be implemented by the subclasses and should read the output of the machine
   * learning script and return it as a Spark Dataset. The output path is given as a string
   * argument.
   *
   * <p>The method should return a Spark Dataset containing the output of the machine learning
   * script. The schema of the returned Dataset should match the schema of the output Dataset as
   * specified in the configuration.
   *
   * <p>The method should throw a BlackBoxException if any error occurs while reading the output.
   */
  protected abstract Dataset<Row> readOutput(String output) throws BlackboxException;

  /**
   * Makes the given dataset accessible to the machine learning script.
   *
   * <p>This method must be implemented by the subclasses and should make the given dataset
   * accessible to the machine learning script. This could involve operations such as writing the
   * dataset to a file or exposing it through a network service.
   *
   * @param dataset the dataset to be made accessible
   * @param inputInfo the information about the input (e.g. the path where the dataset will be
   *     written)
   * @throws BlackboxException if any error occurs during the process
   */
  protected abstract void makeDatasetAccessible(Dataset<Row> dataset, String inputInfo)
      throws BlackboxException;

  /**
   * Cleans up the black box folder.
   *
   * <p>This method is intended to be implemented by subclasses to perform cleanup operations
   * specific to the black box implementation. It should remove any temporary or intermediate files
   * created during the machine learning process.
   *
   * <p>If any error occurs during the cleanup process, a {@link BlackboxException} is thrown.
   *
   * @throws BlackboxException if an error occurs during the cleanup process
   */
  protected abstract void cleanBlackBoxFolder() throws BlackboxException;

  /**
   * Validates the parameters of the black box.
   *
   * <p>This method is intended to be implemented by subclasses to validate the parameters of the
   * black box. It should check the parameters set by the configuration and throw a {@link
   * BlackboxException} if any of the parameters is invalid.
   *
   * @throws BlackboxException if any of the parameters is invalid
   */
  protected abstract void validateParams() throws BlackboxException;
}
