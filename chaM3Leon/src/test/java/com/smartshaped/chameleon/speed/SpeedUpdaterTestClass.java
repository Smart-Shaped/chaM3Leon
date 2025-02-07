package com.smartshaped.chameleon.speed;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.speed.exception.SpeedUpdaterException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class SpeedUpdaterTestClass extends SpeedUpdater {
  public SpeedUpdaterTestClass() throws ConfigurationException, CassandraException {
    super();
  }

  @Override
  public Dataset<Row> updateSpeed(Dataset<Row> df) throws SpeedUpdaterException {
    return null;
  }
}
