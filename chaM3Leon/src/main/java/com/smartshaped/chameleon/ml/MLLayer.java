package com.smartshaped.chameleon.ml;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.ml.blackbox.Blackbox;
import com.smartshaped.chameleon.ml.blackbox.exception.BlackboxException;
import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import com.smartshaped.chameleon.ml.exception.MLLayerException;
import com.smartshaped.chameleon.ml.exception.ModelSaverException;
import com.smartshaped.chameleon.ml.exception.PipelineException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class for the entry point of the machine learning layer; all ml related components will
 * be started from here and for this reason, extending this class is mandatory.
 */
@Getter
@Setter
public abstract class MLLayer {

  private static final Logger logger = LogManager.getLogger(MLLayer.class);

  private List<HdfsReader> readerList;
  private Pipeline pipeline;
  private ModelSaver modelSaver;
  private SparkSession sparkSession;
  private MLConfigurationUtils configurationUtils;
  private CassandraUtils cassandraUtils;
  private Blackbox blackBox;

  protected MLLayer() throws ConfigurationException, MLLayerException, CassandraException {

    this.setConfigurationUtils(MLConfigurationUtils.getMlConf());
    logger.info("ML configurations loaded correctly");
    this.cassandraUtils = CassandraUtils.getCassandraUtils(configurationUtils);
    logger.info("Cassandra utils loaded correctly");
    this.setReaderList(this.configurationUtils.getHdfsReaders());
    logger.info("HdfsReaders list loaded correctly");
    this.setPipeline(this.configurationUtils.getPipeline());
    logger.info("Pipeline loaded correctly");
    this.setModelSaver(this.configurationUtils.getModelSaver());
    logger.info("Model saver loaded correctly");
    this.setBlackBox(this.configurationUtils.getBlackBox());
    logger.info("BlackBox loaded correctly");

    String pipelineLog = (pipeline == null ? "Pipeline is null" : "Pipeline is not null");
    logger.debug(pipelineLog);
    String blackBoxLog = (blackBox == null ? "BlackBox is null" : "BlackBox is not null");
    logger.debug(blackBoxLog);

    // one between pipeline and blackbox must be not null
    if (this.pipeline == null && this.blackBox == null) {
      throw new MLLayerException("Pipeline and BlackBox cannot be null at the same time");
    } else if (this.pipeline != null && this.blackBox != null) {
      throw new MLLayerException("Pipeline and BlackBox cannot be not null at the same time");
    }

    SparkConf sedonaConf = this.configurationUtils.getSparkConf();

    try {
      logger.info("Instantiating Spark Session");

      SparkSession config = SedonaContext.builder().config(sedonaConf).getOrCreate();
      this.setSparkSession(SedonaContext.create(config));

      logger.info("Spark Session with Sedona created");

    } catch (Exception e) {
      throw new MLLayerException("Error getting or creating Sedona SparkSession", e);
    }
  }

  /**
   * Starts the machine learning layer process by executing the configured HDFS readers, pipelines,
   * and black box components.
   *
   * <p>This method performs the following steps:
   *
   * <ul>
   *   <li>Executes each HDFS reader to read and process data, storing the results in datasets.
   *   <li>If a pipeline is configured, sets the datasets and starts the pipeline process, saving
   *       the model and predictions if a ModelSaver is available.
   *   <li>If a black box is configured, starts the black box process, saving the predictions if a
   *       ModelSaver is available.
   *   <li>Closes the CassandraUtils connection and stops the Spark session.
   * </ul>
   *
   * @throws MLLayerException If an error occurs in the ML layer process.
   * @throws HdfsReaderException If an error occurs while reading from HDFS.
   * @throws ModelSaverException If an error occurs while saving the model or predictions.
   * @throws ConfigurationException If an error occurs during configuration retrieval.
   * @throws CassandraException If an error occurs while interacting with Cassandra.
   * @throws PipelineException If an error occurs during pipeline execution.
   * @throws BlackboxException If an error occurs during black box execution.
   */
  public void start()
      throws MLLayerException,
          HdfsReaderException,
          ModelSaverException,
          ConfigurationException,
          CassandraException,
          PipelineException,
          BlackboxException {

    List<Dataset<Row>> datasets = new ArrayList<>();

    for (HdfsReader reader : this.readerList) {
      logger.info("Starting {}", reader.getClass().getName());

      reader.start();
      datasets.add(reader.getDataframe());
    }

    logger.debug("Datasets size: {}", datasets.size());

    if (this.pipeline != null) {

      logger.info("Starting {}", pipeline.getClass().getName());

      pipeline.setDatasets(datasets);
      pipeline.start();

      if (this.modelSaver != null && pipeline.getPredictions() != null) {
        modelSaver.saveModel(pipeline);
      } else {
        logger.warn("Model Saver skipped");
      }
    } else {
      logger.warn("Pipeline skipped");
    }

    if (this.blackBox != null) {

      logger.info("Starting {}", blackBox.getClass().getName());

      blackBox.start(datasets);

      if (this.modelSaver != null && blackBox.getPredictions() != null) {
        modelSaver.saveModel(blackBox);
      } else {
        logger.warn("Model Saver skipped");
      }
    } else {
      logger.warn("BlackBox skipped");
    }

    this.cassandraUtils.close();
    sparkSession.stop();
  }
}
