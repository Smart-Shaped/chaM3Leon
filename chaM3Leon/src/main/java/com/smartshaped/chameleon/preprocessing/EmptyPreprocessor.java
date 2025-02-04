package com.smartshaped.chameleon.preprocessing;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class EmptyPreprocessor extends Preprocessor {

  /**
   * Preprocesses the given DataFrame. This particular implementation does not modify the input
   * DataFrame.
   *
   * @param df the DataFrame to preprocess
   * @return the same DataFrame
   */
  @Override
  public Dataset<Row> preprocess(Dataset<Row> df) {
    return df;
  }
}
