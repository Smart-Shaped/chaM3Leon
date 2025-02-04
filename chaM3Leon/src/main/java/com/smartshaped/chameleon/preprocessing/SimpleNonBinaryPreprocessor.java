package com.smartshaped.chameleon.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class SimpleNonBinaryPreprocessor extends Preprocessor {

  private static final Logger logger = LogManager.getLogger(SimpleNonBinaryPreprocessor.class);

  /**
   * Method to process non-binary stream.
   *
   * <p>This method takes a DataFrame with non-binary data and processes it. The processing involves
   * casting the key of the DataFrame to String and keeping only the value and timestamp columns.
   *
   * @param df the DataFrame to be processed
   * @return the processed DataFrame
   */
  @Override
  public Dataset<Row> preprocess(Dataset<Row> df) {

    logger.info("Processing non-binary stream");
    df = df.selectExpr("CAST(key AS STRING)", "CAST(value as STRING)", "timestamp");
    df.printSchema();

    return df;
  }
}
