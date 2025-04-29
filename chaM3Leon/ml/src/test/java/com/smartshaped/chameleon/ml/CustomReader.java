package com.smartshaped.chameleon.ml;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class CustomReader extends HdfsReader {
  public CustomReader() throws ConfigurationException {
    /* document why this constructor is empty */
  }

  @Override
  protected Dataset<Row> processRawData() throws HdfsReaderException {
    return super.dataframe;
  }
}
