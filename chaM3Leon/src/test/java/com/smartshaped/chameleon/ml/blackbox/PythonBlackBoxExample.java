package com.smartshaped.chameleon.ml.blackbox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackBoxException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public class PythonBlackBoxExample extends PythonBlackBox {

  public PythonBlackBoxExample() throws ConfigurationException {
    super();
  }

  @Override
  protected List<Dataset<Row>> mergeDatasetsIfNecessary(List<Dataset<Row>> datasets)
      throws BlackBoxException {
    return datasets;
  }

  @Override
  protected void postRunning() throws BlackBoxException {
    /* document why this method is empty */
  }

  @Override
  protected void runCommand(ProcessBuilder processBuilder) throws BlackBoxException {
    /* document why this method is empty */
  }
}
