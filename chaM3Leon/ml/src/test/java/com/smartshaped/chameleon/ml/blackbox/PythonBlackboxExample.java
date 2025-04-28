package com.smartshaped.chameleon.ml.blackbox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackbox.PythonBlackbox;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackboxException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public class PythonBlackboxExample extends PythonBlackbox {

  public PythonBlackboxExample() throws ConfigurationException {
    super();
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
  protected void runCommand(ProcessBuilder processBuilder) throws BlackboxException {
    /* document why this method is empty */
  }
}
