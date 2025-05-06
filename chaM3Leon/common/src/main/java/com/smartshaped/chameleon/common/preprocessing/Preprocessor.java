package com.smartshaped.chameleon.common.preprocessing;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.preprocessing.exception.PreprocessorException;

public abstract class Preprocessor {

  /**
   * This method is the one that should be implemented by all the concrete preprocessors. It takes a
   * DataFrame and applies the specific preprocessing to it, returning a new DataFrame.
   *
   * @param df DataFrame to be preprocessed.
   * @return Preprocessed DataFrame.
   * @throws PreprocessorException If any error occurs during the preprocessing.
   */
  public abstract Dataset<Row> preprocess(Dataset<Row> df) throws PreprocessorException;

  public abstract void closeConnections() throws PreprocessorException;
}
