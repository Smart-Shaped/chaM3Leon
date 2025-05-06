package com.smartshaped.chameleon.batch;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.preprocessing.Preprocessor;
import com.smartshaped.chameleon.common.preprocessing.exception.PreprocessorException;

public class PreprocessorTestClass extends Preprocessor {

  @Override
  public Dataset<Row> preprocess(Dataset<Row> df) throws PreprocessorException {
    return df;
  }

  @Override
  public void closeConnections() throws PreprocessorException {}
}
