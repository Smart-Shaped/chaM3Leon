package com.smartshaped.chameleon.speed;

import com.smartshaped.chameleon.common.utils.exception.CassandraException;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import com.smartshaped.chameleon.speed.exception.SpeedUpdaterException;
import com.smartshaped.chameleon.speed.utils.SpeedConfigurationUtils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@Getter
public abstract class SpeedUpdater {

  private static final Logger logger = LogManager.getLogger(SpeedUpdater.class);

  private String modelName;
  private SpeedConfigurationUtils configurationUtils;
  private TableModel tableModel;
  private CassandraUtils cassandraUtils;

  /**
   * SpeedUpdater constructor.
   *
   * <p>This constructor loads the SpeedConfigurationUtils that are used to get the CassandraUtils
   * and the TableModel.
   *
   * @throws ConfigurationException if there is an error while loading the configuration
   * @throws CassandraException if there is an error while validating the table model
   */
  protected SpeedUpdater() throws ConfigurationException, CassandraException {

    this.configurationUtils = SpeedConfigurationUtils.getSpeedConf();
    logger.info("Speed configurations loaded correctly");
    this.modelName = configurationUtils.getModelClassName();
    logger.info("Model class name loaded correctly");
    this.tableModel = configurationUtils.createTableModel(modelName);
    logger.info("Table from model created correctly");
    this.cassandraUtils = CassandraUtils.getCassandraUtils(configurationUtils);
    logger.info("Cassandra com.smartshaped.chameleon.batch.utils loaded correctly");
    cassandraUtils.validateTableModel(tableModel);
    logger.info("Table model validated correctly");

    cassandraUtils.close();
  }

  /**
   * Starts the Speed update process.
   *
   * <p>This method takes a streaming Dataset<Row> and an interval in milliseconds as arguments. It
   * calls the updateSpeed method to update the Dataset and then calls the saveSpeed method to save
   * the updated Dataset to Cassandra.
   *
   * @param df the streaming Dataset<Row> to update
   * @param intervalMs the interval in milliseconds
   * @throws SpeedUpdaterException if an error occurs while executing the update
   * @throws CassandraException if an error occurs while saving to Cassandra
   * @throws ConfigurationException if an error occurs while loading the configuration
   */
  public void startUpdate(Dataset<Row> df, Long intervalMs)
      throws SpeedUpdaterException, CassandraException, ConfigurationException {

    logger.info("Start Speed update...");
    Dataset<Row> updatedDF = updateSpeed(df);

    saveSpeed(updatedDF, intervalMs);

    cassandraUtils.close();
  }

  /**
   * Abstract method that updates a streaming Dataset<Row>
   *
   * @param df the streaming Dataset<Row> to update
   * @return the updated Dataset<Row>
   * @throws SpeedUpdaterException if an error occurs while executing the update
   */
  public abstract Dataset<Row> updateSpeed(Dataset<Row> df) throws SpeedUpdaterException;

  public void saveSpeed(Dataset<Row> df, Long intervalMs)
      throws CassandraException, ConfigurationException {

    logger.info("Save updated dataframe");
    cassandraUtils.saveStreamDF(df, tableModel, intervalMs);
  }
}
