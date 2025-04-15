package com.smartshaped.chameleon.batch;

import com.smartshaped.chameleon.batch.exception.BatchUpdaterException;
import com.smartshaped.chameleon.batch.utils.BatchConfigurationUtils;
import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeoutException;

@Getter
public abstract class BatchUpdater {

  private static final Logger logger = LogManager.getLogger(BatchUpdater.class);

  private String modelName;
  private BatchConfigurationUtils configurationUtils;
  private TableModel tableModel;
  private CassandraUtils cassandraUtils;

  /**
   * Creates a new instance of BatchUpdater.
   *
   * <p>This constructor will load the batch configurations, create a TableModel instance from the
   * model class name, get an instance of CassandraUtils, validate the TableModel, and then close
   * the Cassandra connection.
   *
   * @throws ConfigurationException if any error occurs while loading the configuration
   * @throws CassandraException if any error occurs while connecting to Cassandra
   */
  protected BatchUpdater() throws ConfigurationException, CassandraException {

    this.configurationUtils = BatchConfigurationUtils.getBatchConf();
    logger.info("Batch configurations loaded correctly");
    this.modelName = configurationUtils.getModelClassName();
    logger.info("Model class name loaded correctly");
    this.tableModel = configurationUtils.createTableModel(modelName);
    logger.info("Table from model created correctly");
    this.cassandraUtils = CassandraUtils.getCassandraUtils(configurationUtils);
    logger.info("Cassandra utils loaded correctly");
    cassandraUtils.validateTableModel(tableModel);
    logger.info("Table model validated correctly");

    cassandraUtils.close();
  }

  /**
   * Starts the batch update process.
   *
   * <p>This method takes a streaming Dataset<Row> and an interval in milliseconds as arguments. It
   * writes the streaming Dataset to a Cassandra table at the specified interval. The updateBatch
   * method is called with each new batch of data.
   *
   * @param df the streaming Dataset<Row>
   * @param intervalMs the interval in milliseconds
   * @throws BatchUpdaterException if an error occurs while executing the update
   */
  public void startUpdate(Dataset<Row> df, Long intervalMs) throws BatchUpdaterException {

    SparkSession sparkSession = SparkSession.getActiveSession().get();

    try {
      df.writeStream()
          .foreachBatch(
              (streamingBatch, batchId) -> {
                logger.info("Starting batch update...");
                Dataset<Row> updatedDF = updateBatch(streamingBatch);
                saveBatch(updatedDF);
              })
          .trigger(Trigger.ProcessingTime(intervalMs))
          .start();
    } catch (TimeoutException e) {
      throw new BatchUpdaterException("Error executing update", e);
    }

    try {
      sparkSession.streams().awaitAnyTermination();
    } catch (StreamingQueryException e) {
      throw new BatchUpdaterException(e);
    }

    cassandraUtils.close();
  }

  /**
   * Updates a batch of data in Cassandra.
   *
   * <p>This method takes a Dataset<Row> as arguments. It should execute any necessary computations
   * on the Dataset and then return the updated Dataset. The updated Dataset is then saved to
   * Cassandra by the BatchUpdater.
   *
   * @param df the Dataset<Row> to update
   * @return the updated Dataset
   * @throws BatchUpdaterException if an error occurs while executing the update
   */
  public abstract Dataset<Row> updateBatch(Dataset<Row> df) throws BatchUpdaterException;

  /**
   * Saves a batch of data to Cassandra.
   *
   * <p>This method takes a Dataset<Row> as an argument and saves it to Cassandra using the {@link
   * CassandraUtils#saveDF(Dataset, TableModel)} method.
   *
   * @param df The Dataset<Row> to save
   * @throws CassandraException if an error occurs during saving
   */
  public void saveBatch(Dataset<Row> df) throws CassandraException {

    logger.info("Save updated dataframe");
    cassandraUtils.saveDF(df, tableModel);
  }
}
