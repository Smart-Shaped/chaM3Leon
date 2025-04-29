package com.smartshaped.chameleon.batch;

import com.smartshaped.chameleon.batch.exception.BatchUpdaterException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.utils.exception.CassandraException;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;

public class BatchUpdaterTestClass extends BatchUpdater {

  public BatchUpdaterTestClass() throws ConfigurationException, CassandraException {
    super();
  }

  @Override
  public Dataset<Row> updateBatch(Dataset<Row> df) throws BatchUpdaterException {
    return df;
  }
}
