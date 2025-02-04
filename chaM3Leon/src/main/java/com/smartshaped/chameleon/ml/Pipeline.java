package com.smartshaped.chameleon.ml;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.ml.Model;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.ml.exception.PipelineException;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class representing a machine learning pipeline.
 *
 * <p>This class provides a standard interface for all the machine learning pipelines.
 *
 * <p>All the machine learning pipelines must extend this class and implement the methods.
 */
@Getter
@Setter
public abstract class Pipeline {

  private static final Logger logger = LogManager.getLogger(Pipeline.class);

  private List<Dataset<Row>> datasets;
  private Model<?> model;
  private Dataset<Row> predictions;

  public abstract void start() throws PipelineException;

  public abstract void evaluatePredictions(Dataset<Row> predictions) throws PipelineException;

  public abstract void evaluateModel(Model<?> model) throws PipelineException;

  public abstract Model<?> readModelFromHDFS(String hdfsPath) throws PipelineException;

  /**
   * Method to check if a given HDFS path already exists.
   *
   * @param hdfsPath String representing the path to check.
   * @return boolean indicating whether the path already exists.
   * @throws PipelineException if any error occurs while checking the path.
   */
  public boolean hdfsPathAlreadyExist(String hdfsPath) throws PipelineException {

    Configuration configuration = new Configuration();
    Path path = new Path(hdfsPath);

    try {
      FileSystem fileSystem = FileSystem.get(configuration);
      return fileSystem.exists(path);
    } catch (IOException e) {
      throw new PipelineException("Error checking HDFS path", e);
    }
  }
}
