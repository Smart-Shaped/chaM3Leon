package com.smartshaped.chameleon.ml;

import org.apache.spark.ml.Model;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class PipelineExample extends Pipeline {
  @Override
  public void start() {
    /* document why this method is empty */
  }

  @Override
  public void evaluatePredictions(Dataset<Row> predictions) {
    /* document why this method is empty */
  }

  @Override
  public void evaluateModel(Model<?> model) {
    /* document why this method is empty */
  }

  @Override
  public Model<?> readModelFromHDFS(String hdfsPath) {
    return null;
  }
}
