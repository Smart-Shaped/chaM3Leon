package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.preprocessing.Preprocessor;
import com.smartshaped.chameleon.preprocessing.exception.PreprocessorException;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class PreprocessorExample extends Preprocessor {

  public PreprocessorExample() throws ConfigurationException {
    super();
  }

  @Override
  public Dataset<Row> preprocess(Dataset<Row> ds) throws PreprocessorException {
    SparkSession sparkSession = SparkSession.getActiveSession().get();
    Dataset<Row> df = sparkSession.createDataFrame(List.of(), Row.class);
    return df;
  }
}
