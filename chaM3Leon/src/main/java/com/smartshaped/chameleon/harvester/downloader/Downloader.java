package com.smartshaped.chameleon.harvester.downloader;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;

/** An abstract class representing a downloader that downloads data from a source. */
public abstract class Downloader {

  protected static final Logger logger = LogManager.getLogger(Downloader.class);
  protected HarvesterConfigurationUtils configurationUtils;
  protected String className;
  protected Map<String, String> queryParams;
  protected Map<String, String> urlParams;

  protected Downloader() throws ConfigurationException {
    logger.info("Initializing Downloader...");

    configurationUtils = HarvesterConfigurationUtils.getHarvesterConf();
    logger.info("Harvester configurations loaded correctly");

    className = this.getClass().getSimpleName();
    logger.debug("Class name set to: {}", className);

    queryParams = configurationUtils.getQueryParam(className);
    logger.debug("Query parameters retrieved: {}", queryParams);

    urlParams = configurationUtils.getUrlParams(className);
    logger.debug("URL parameters retrieved: {}", urlParams);

    logger.info("Downloader initialized successfully.");
  }

  /**
   * Creates a list of URI strings from the given list of parameters.
   *
   * @param paramList The list of parameters to be used in the URI.
   * @return A list of URI strings.
   */
  protected abstract List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException;

  /**
   * Downloads the data specified by the request and the given parameters.
   *
   * @param paramList The list of parameters needed for the download.
   * @param request The request that contains the parameters needed for the download.
   * @return The downloaded data.
   * @throws DownloaderException If there is an error during the download process.
   * @throws ConfigurationException If there is an error with the configuration.
   */
  public abstract Dataset<Row> download(List<String> paramList, Request request)
      throws DownloaderException, ConfigurationException;
}
