package com.smartshaped.chameleon.ml.blackbox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackboxException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public class BlackboxExample extends Blackbox {

  public BlackboxExample() throws ConfigurationException {
    super();
  }

  @Override
  protected void runML() throws BlackboxException {
    /* document why this method is empty */
  }

  @Override
  protected void extraPreparation() throws BlackboxException {
    /* document why this method is empty */
  }

  @Override
  protected List<Dataset<Row>> mergeDatasetsIfNecessary(List<Dataset<Row>> datasets)
      throws BlackboxException {
    return datasets;
  }

  @Override
  protected void postRunning() throws BlackboxException {
    /* document why this method is empty */
  }

  @Override
  protected Dataset<Row> readOutput(String output) throws BlackboxException {
    return null;
  }

  @Override
  protected void makeDatasetAccessible(Dataset<Row> dataset, String inputInfo)
      throws BlackboxException {
    /* document why this method is empty */
  }

  @Override
  protected void cleanBlackBoxFolder() throws BlackboxException {
    /* document why this method is empty */
  }

  @Override
  protected void validateParams() throws BlackboxException {
    /* document why this method is empty */
  }
}
