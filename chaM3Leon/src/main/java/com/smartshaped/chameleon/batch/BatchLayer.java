package com.smartshaped.chameleon.batch;

import com.smartshaped.chameleon.batch.exception.BatchLayerException;
import com.smartshaped.chameleon.batch.exception.BatchUpdaterException;
import com.smartshaped.chameleon.batch.exception.HdfsSaverException;
import com.smartshaped.chameleon.batch.utils.BatchConfigurationUtils;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.exception.KafkaConsumerException;
import com.smartshaped.chameleon.common.utils.KafkaConsumer;
import com.smartshaped.chameleon.preprocessing.Preprocessor;
import com.smartshaped.chameleon.preprocessing.exception.PreprocessorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.Map;
import java.util.Objects;

public abstract class BatchLayer {

    private static final Logger logger = LogManager.getLogger(BatchLayer.class);

    private BatchConfigurationUtils configurationUtils;
    private SparkSession sparkSession;
    private Map<String, Preprocessor> preprocessors;
    private BatchUpdater batchUpdater;
    private Map<String, String> kafkaConfig;


    /**
     * Constructor for the BatchLayer.
     *
     * @throws ConfigurationException if there is an error loading the configuration
     * @throws BatchLayerException    if there is an error with the creation of the
     *                                SparkSession
     */
    protected BatchLayer() throws ConfigurationException, BatchLayerException {

        this.configurationUtils = BatchConfigurationUtils.getBatchConf();
        logger.info("Batch configurations loaded correctly");

        SparkConf sparkConf = configurationUtils.getSparkConf();
        logger.info("Spark configurations loaded correctly");

        try {
            logger.info("Instantiating Spark Session");
            sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
            logger.info("Spark Session created");
        } catch (Exception e) {
            throw new BatchLayerException("Error getting or creating SparkSession", e);
        }

        this.kafkaConfig = configurationUtils.getKafkaConfig();
        logger.info("Kafka configurations loaded correctly");
        this.preprocessors = configurationUtils.getPreprocessors();
        logger.info("Preprocessors loaded correctly");
        this.batchUpdater = configurationUtils.getBatchUpdater();
        logger.info("Batch Updater loaded correctly");
    }

    /**
     * Starts the BatchLayer process by reading data from Kafka, preprocessing it,
     * saving it to HDFS, and executing batch updates if a BatchUpdater is configured.
     *
     * <p>
     * This method performs the following steps:
     * <ul>
     * <li>Reads a streaming dataset from Kafka using the provided configuration.</li>
     * <li>Preprocesses the dataset and saves it to HDFS.</li>
     * <li>If a BatchUpdater is configured, it initiates the batch update process.</li>
     * <li>Waits for the termination of all active Spark streaming queries.</li>
     * <li>Stops the Spark session upon completion.</li>
     * </ul>
     *
     * @throws KafkaConsumerException If an error occurs during Kafka data reading.
     * @throws BatchUpdaterException  If an error occurs during the batch update process.
     * @throws PreprocessorException  If an error occurs during data preprocessing.
     * @throws HdfsSaverException     If an error occurs while saving data to HDFS.
     * @throws BatchLayerException    If an error occurs in the BatchLayer process.
     */
    public void start() throws KafkaConsumerException, BatchUpdaterException, PreprocessorException, HdfsSaverException, BatchLayerException {
        logger.info("Starting BatchLayer...");

        Dataset<Row> df = KafkaConsumer.kafkaRead(kafkaConfig, sparkSession);

        logger.info("Starting preprocessing and save to HDFS...");
        preprocessAndSave(df);

        if (Objects.isNull(batchUpdater)) {
            logger.info("No batchUpdater found in configuration,skipping it...");
        } else {
            batchUpdater.startUpdate(df, sparkSession, Long.parseLong(kafkaConfig.get("intervalMs")));
            logger.info("BatchUpdater completed.");
        }

        try {
            sparkSession.streams().awaitAnyTermination();
        } catch (StreamingQueryException e) {
            throw new BatchLayerException("Error awayting all Spark Streaming queries termination", e);
        }

        sparkSession.stop();


        logger.info("BatchLayer completed successfully");
    }

    /**
     * This method is responsible for managing streaming datasets retrieved from Kafka and saving them in
     * the corrisponding parquetFile. It also applies preprocessing to the data before
     * saving it, if a preprocessor is defined for the topic.
     *
     * @param df The dataset of rows retrieved from Kafka.
     * @throws PreprocessorException If an error occurs during data preprocessing.
     * @throws HdfsSaverException    If an error occurs while saving data to HDFS.
     */
    private void preprocessAndSave(Dataset<Row> df) throws PreprocessorException, HdfsSaverException {

        String topics = kafkaConfig.get("topics");
        Long intervalMs = Long.parseLong(kafkaConfig.get("intervalMs"));

        logger.info("Dataset subscribed to kafka.");

        String[] topicArray = topics.split(",");
        Dataset<Row> tmpDf;

        logger.info("Start preprocessing data:");

        for (String topic : topicArray) {

            tmpDf = df.filter(df.col("topic").equalTo(topic));

            logger.info("Loading preprocessor for topic:{}", topic);

            if (!preprocessors.isEmpty()) {
                if (preprocessors.get(topic) != null) {
                    tmpDf = preprocessors.get(topic).preprocess(tmpDf);
                } else {
                    logger.info("No preprocessor defined for topic: {}", topic);
                }
            }

            logger.info("Save data for topic:{}", topic);

            HdfsSaver.save(tmpDf, kafkaConfig.get(topic.concat(".path")), kafkaConfig.get(topic.concat(".checkpoint")), intervalMs);


        }

    }

}
