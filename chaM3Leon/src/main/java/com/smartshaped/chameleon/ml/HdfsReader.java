package com.smartshaped.chameleon.ml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

import lombok.Getter;
import lombok.Setter;

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
  }

  /**
   * Method to read raw data from HDFS and store it in a DataFrame
   *
   * @param sedona Spark session
   * @throws HdfsReaderException if any error occurs while reading from HDFS
   */
  public void readRawData(SparkSession sedona) throws HdfsReaderException {
    String filePath = this.hdfsPath;

    Dataset<Row> rawDF;

    try {
      rawDF = sedona.read().parquet(filePath);
      logger.info("Files in {} were read succesfully", filePath);
    } catch (Exception e) {
      throw new HdfsReaderException("Error while reading from HDFS", e);
    }

    this.dataframe = rawDF;
  }

  /**
   * This method starts the HDFSReader, reading the data from HDFS and processing it.
   *
   * @param sedona Spark session
   * @throws HdfsReaderException if any error occurs while reading or processing the data
   */
  public void start(SparkSession sedona) throws HdfsReaderException {

    try {
      this.readRawData(sedona);
    } catch (HdfsReaderException e) {
      throw new HdfsReaderException(e);
    }

    try {
      dataframe = this.processRawData(sedona);
    } catch (HdfsReaderException e) {
      throw new HdfsReaderException("Error processing Data", e);
    }
  }

  /**
   * Method to process data and make them suitable for machine learning.
   *
   * @param sedona Spark session.
   * @return Processed DataFrame.
   * @throws HdfsReaderException if any error occurs during data processing.
   */
  public Dataset<Row> processRawData(SparkSession sedona) throws HdfsReaderException {
    return this.dataframe;
  }
}
