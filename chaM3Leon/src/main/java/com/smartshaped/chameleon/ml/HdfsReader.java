package com.smartshaped.chameleon.ml;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Abstract class representing a HDFS reader.
 *
 * <p>This class provides a standard interface for all the HDFS readers. It defines the methods to
 * read raw data from HDFS and process it.
 *
 * <p>All the HDFSReaders must extend this class and implement the methods.
 */
@Setter
@Getter
public abstract class HdfsReader {

  private static final Logger logger = LogManager.getLogger(HdfsReader.class);

  protected String hdfsPath;
  protected Dataset<Row> dataframe;
  protected MLConfigurationUtils configurationUtils;

  protected HdfsReader() throws ConfigurationException {

    this.configurationUtils = MLConfigurationUtils.getMlConf();
    String className = this.getClass().getName();
    this.hdfsPath = configurationUtils.getHDFSPath(className);

    if (hdfsPath.trim().isEmpty()) {
      throw new ConfigurationException("Missing HDFS path");
    }

    logger.debug("HdfsReader initialized");
  }

  /**
   * Method to read raw data from HDFS and store it in a DataFrame
   *
   * @throws HdfsReaderException if any error occurs while reading from HDFS
   */
  protected void readRawData() throws HdfsReaderException {

    logger.info("Reading data from HDFS...");

    SparkSession sparkSession = SparkSession.getActiveSession().get();
    String filePath = this.hdfsPath;

    Dataset<Row> rawDF;

    try {
      rawDF = sparkSession.read().parquet(filePath);
      logger.debug("Files in {} were read successfully", filePath);
    } catch (Exception e) {
      throw new HdfsReaderException("Error while reading from HDFS", e);
    }

    this.dataframe = rawDF;

    logger.info("Data read from HDFS");
  }

  /**
   * This method starts the HDFSReader, reading the data from HDFS and processing it.
   *
   * @throws HdfsReaderException if any error occurs while reading or processing the data
   */
  public void start() throws HdfsReaderException {

    try {
      this.readRawData();
    } catch (HdfsReaderException e) {
      throw new HdfsReaderException(e);
    }

    try {
      dataframe = this.processRawData();
    } catch (HdfsReaderException e) {
      throw new HdfsReaderException("Error processing Data", e);
    }

    logger.info("HdfsReader process completed");
  }

  /**
   * This method must be implemented by all the HDFSReaders. It processes the raw data
   * read from HDFS and returns a new DataFrame.
   *
   * @return a DataFrame containing the processed data
   * @throws HdfsReaderException if any error occurs while processing the data
   */
  protected abstract Dataset<Row> processRawData() throws HdfsReaderException;
}
