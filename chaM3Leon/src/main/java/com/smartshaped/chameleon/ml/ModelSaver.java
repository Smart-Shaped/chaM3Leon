package com.smartshaped.chameleon.ml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.util.MLWritable;
import org.apache.spark.ml.util.MLWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import com.smartshaped.chameleon.ml.blackbox.Blackbox;
import com.smartshaped.chameleon.ml.exception.ModelSaverException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

/**
 * Abstract class representing a saver for ml results.
 *
 * <p>This class provides a standard interface for saving models and predictions. All model savers
 * must extend this class and implement the methods.
 *
 * <p>This class is thread safe.
 */
public abstract class ModelSaver {

  private static final Logger logger = LogManager.getLogger(ModelSaver.class);

  protected String hdfsPath;
  protected TableModel tableModel;
  protected MLConfigurationUtils mlConfigurationUtils;
  protected CassandraUtils cassandraUtils;

  protected ModelSaver() throws ConfigurationException, CassandraException {

    this.mlConfigurationUtils = MLConfigurationUtils.getMlConf();
    logger.info("ML configurations loaded correctly");
    this.cassandraUtils = CassandraUtils.getCassandraUtils(mlConfigurationUtils);
    logger.info("Cassandra utils loaded correctly");
    this.hdfsPath = mlConfigurationUtils.getModelDir();
    logger.debug("Model directory set to: {}", hdfsPath);

    String modelName = mlConfigurationUtils.getModelClassName();
    this.tableModel = mlConfigurationUtils.createTableModel(modelName);
    logger.info("Table from model created correctly");

    logger.info("ModelSaver initialized");
  }

  /**
   * Saves the model and predictions to HDFS and Cassandra respectively.
   *
   * <p>This method saves the model to HDFS using the configured ModelSaver and saves the
   * predictions to Cassandra using the configured TableModel.
   *
   * @param pipeline the pipeline containing the model and predictions
   * @throws ModelSaverException if any error occurs while saving the model
   * @throws ConfigurationException if any error occurs while retrieving the configuration
   * @throws CassandraException if any error occurs while saving the predictions to Cassandra
   */
  public void saveModel(Pipeline pipeline)
      throws ModelSaverException, ConfigurationException, CassandraException {

    Model<?> model = pipeline.getModel();
    logger.debug("Model retrieved");
    Dataset<Row> predictions = pipeline.getPredictions();
    logger.debug("Predictions retrieved");

    saveModelToHDFS(model);

    savePredictionsToCassandra(predictions);

    logger.info("ModelSaver process for Pipeline completed");
  }

  /**
   * Saves the predictions from the given BlackBox to Cassandra.
   *
   * <p>This method retrieves the predictions from the provided BlackBox instance and saves them to
   * Cassandra.
   *
   * @param blackBox the BlackBox containing the predictions to be saved
   * @throws ConfigurationException if there is an error in the configuration
   * @throws CassandraException if an error occurs while saving the predictions to Cassandra
   * @throws ModelSaverException if any other error occurs during the saving process
   */
  public void saveModel(Blackbox blackBox)
      throws ConfigurationException, CassandraException, ModelSaverException {

    Dataset<Row> predictions = blackBox.getPredictions();

    savePredictionsToCassandra(predictions);

    logger.info("ModelSaver process for BlackBox completed");
  }

  /**
   * This method takes an ML model and saves it to HDFS.
   *
   * @param model the model to be saved
   * @throws ModelSaverException if any error occurs while saving the model
   */
  protected void saveModelToHDFS(Model<?> model) throws ModelSaverException {
    try {
      MLWritable writableModel = (MLWritable) model;

      MLWriter mlwriter = writableModel.write();
      mlwriter.overwrite().save(hdfsPath);
    } catch (Exception e) {
      throw new ModelSaverException("Error while saving model to HDFS: " + hdfsPath, e);
    }

    logger.info("Model has been saved to HDFS");
  }

  /**
   * Save the predictions to Cassandra.
   *
   * <p>This method validates the TableModel then it saves the predictions to Cassandra.
   *
   * @param predictions the predictions to be saved
   * @throws ModelSaverException if any error occurs during the saving of the predictions
   * @throws ConfigurationException if any error occurs during the configuration
   * @throws CassandraException if any error occurs while saving the predictions to Cassandra
   */
  protected void savePredictionsToCassandra(Dataset<Row> predictions)
      throws ModelSaverException, ConfigurationException, CassandraException {

    try {
      this.cassandraUtils.validateTableModel(tableModel);
      this.cassandraUtils.saveDF(predictions, tableModel);
    } catch (CassandraException e) {
      throw new ModelSaverException("Error while saving predictions to Cassandra", e);
    }

    logger.info("Predictions have been saved to Cassandra");
  }
}
