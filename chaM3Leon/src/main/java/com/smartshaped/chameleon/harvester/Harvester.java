package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.downloader.Downloader;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.exception.HarvesterException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.saver.HarvesterSaver;
import com.smartshaped.chameleon.harvester.transformer.DatasetTransformer;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import com.smartshaped.chameleon.preprocessing.Preprocessor;
import com.smartshaped.chameleon.preprocessing.exception.PreprocessorException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;

/**
 * The Harvester class is responsible for downloading and transforming data, and then saving it to a file.
 */
public abstract class Harvester {

    private static final Logger logger = LogManager.getLogger(Harvester.class);
    protected String outputPath;
    protected String inputPath;
    protected SparkSession session;
    @Getter
    private String harvesterId;
    private final Preprocessor preprocessor;
    private HarvesterConfigurationUtils configurationUtils;
    private final DatasetTransformer transformer;
    private final Downloader downloader;

    protected Harvester() throws ConfigurationException {
        configurationUtils = HarvesterConfigurationUtils.getHarvesterConf();
        String className = this.getClass().getName();
        harvesterId = configurationUtils.getHarvesterId(className);
        outputPath = configurationUtils.getOutputPath(harvesterId);
        inputPath = configurationUtils.getInputPath(harvesterId);
        preprocessor = configurationUtils.getPreprocessor(harvesterId);
        transformer = configurationUtils.getTransformer(harvesterId);
        downloader = configurationUtils.getDownloader(harvesterId);
    }

    /**
     * Extracts the parameters needed for the harvesting process from the given request.
     *
     * @param req The request from which the parameters should be extracted.
     * @return The extracted parameters.
     */
    protected abstract List<String> extractParams(Request req);


    /**
     * Starts the harvesting process. This method is responsible for calling the
     * correct methods in the correct order to complete the harvesting process.
     *
     * @param req The request that contains the parameters needed for the harvesting process.
     * @throws HarvesterException    If there is an error during the harvesting process.
     * @throws PreprocessorException If there is an error during the preprocessing step.
     */
    public void execute(Request req) throws HarvesterException, PreprocessorException {
        List<String> paramList = extractParams(req);
        logger.info("Extracted params: {}", paramList);

        logger.info("Downloading and transforming data...");
        Dataset<Row> data = downloadAndTransform(paramList, req);
        logger.info("Downloaded and transformed data");

        logger.info("Starting preprocessing...");
        Dataset<Row> df = process(data);
        logger.info("Preprocessed data completed");

        HarvesterSaver.save(df, outputPath);
        logger.info("Saved data to: {}", outputPath);
    }


    /**
     * Downloads the data specified by the request and transforms it according to the harvester's configuration.
     *
     * @param paramList The list of parameters needed for the harvesting process.
     * @param req       The request that contains the parameters needed for the harvesting process.
     * @return The downloaded and transformed data.
     * @throws HarvesterException If there is an error during the downloading or transforming process.
     */
    public Dataset<Row> downloadAndTransform(List<String> paramList, Request req) throws HarvesterException {
        Dataset<Row> df;

        try {
            if (transformer == null) {
                df = (Dataset<Row>) downloader.download(paramList, req);
            } else {
                df = transformer.transform(downloader.download(paramList, req));
            }
        } catch (DownloaderException | ConfigurationException e) {
            throw new HarvesterException("Error downloading or transforming data.", e);
        }

        return df;
    }

    /**
     * Processes the data using the preprocessor associated with this harvester.
     *
     * @param data The data to be processed.
     * @return The processed data.
     * @throws PreprocessorException If there is an error during the preprocessing step.
     */
    public Dataset<Row> process(Dataset<Row> data) throws PreprocessorException {
        if (preprocessor == null) {
            return data;
        } else {
            return preprocessor.preprocess(data);
        }
    }

    /**
     * Returns the name of this harvester.
     *
     * @return The name of this harvester.
     */
    public String getName() {
        return this.getClass().getName();
    }


}
