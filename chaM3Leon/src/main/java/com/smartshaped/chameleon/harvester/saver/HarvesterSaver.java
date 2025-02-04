package com.smartshaped.chameleon.harvester.saver;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/** A utility class for saving a Spark Dataset to a specified HDFS path in Parquet format. */
public class HarvesterSaver {

  /**
   * Saves a Spark Dataset to a specified HDFS path in Parquet format.
   *
   * <p>This method writes the given Dataset<Row> to the specified path in Parquet format using the
   * append mode. This allows new data to be appended to the existing data at the specified
   * location.
   *
   * @param df The Dataset<Row> to be saved.
   * @param parquetPath The HDFS path where the data will be stored in Parquet format.
   */
  public static void save(Dataset<Row> df, String parquetPath) {
    df.write().mode("append").parquet(parquetPath);
  }
}
