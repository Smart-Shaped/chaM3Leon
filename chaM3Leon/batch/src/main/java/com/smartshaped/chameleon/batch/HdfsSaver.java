package com.smartshaped.chameleon.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.Trigger;

import com.smartshaped.chameleon.batch.exception.HdfsSaverException;

public class HdfsSaver {

  private static final Logger logger = LogManager.getLogger(HdfsSaver.class);

  /**
   * Utility class to save Spark Dataframe to HDFS in a streaming mode.
   *
   * <p>This class is not intended to be instantiated and its methods must be accessed in a static
   * way.
   */
  private HdfsSaver() throws HdfsSaverException {
    throw new HdfsSaverException(
        "com.smartshaped.chameleon.batch.HdfsSaver is an utility class and you can access to its methods in a static way");
  }

  /**
   * Saves a Spark Dataset to HDFS in streaming mode using the Parquet format.
   *
   * <p>This method writes the given Dataset<Row> to HDFS at the specified path, using a checkpoint
   * directory for fault tolerance and triggering the write operation at the specified interval. The
   * output mode is set to append.
   *
   * @param ds The Dataset<Row> to be saved.
   * @param parquetPath The HDFS path where the data will be stored in Parquet format.
   * @param checkpointLocation The directory path for saving checkpoint information.
   * @param intervalMs The interval in milliseconds for triggering the write operation.
   * @throws HdfsSaverException If an error occurs during the writeStream operation.
   */
  public static void save(
      Dataset<Row> ds, String parquetPath, String checkpointLocation, Long intervalMs)
      throws HdfsSaverException {

    logger.info("Starting writeStream operation...");

    try {
      ds.writeStream()
          .format("parquet")
          .outputMode(OutputMode.Append())
          .option("path", parquetPath)
          .option("checkpointLocation", checkpointLocation)
          .trigger(Trigger.ProcessingTime(intervalMs))
          .start();
    } catch (Exception e) {
      throw new HdfsSaverException(e);
    }

    logger.debug("WriteStream operation started successfully");
  }
}
