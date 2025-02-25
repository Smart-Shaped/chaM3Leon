package com.smartshaped.chameleon.harvester.downloader;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;

/** An abstract class representing a downloader that downloads data from a source. */
public abstract class Downloader<T> {

  protected static final Logger logger = LogManager.getLogger(Downloader.class);
  protected HarvesterConfigurationUtils configurationUtils;
  protected String className;
  protected Map<String, String> queryParams;
  protected Map<String, String> urlParams;

  protected Downloader() throws ConfigurationException {
    logger.info("Initializing Downloader...");

    try {
      configurationUtils = HarvesterConfigurationUtils.getHarvesterConf();
    } catch (ConfigurationException e) {
      throw new ConfigurationException("Unable to retrieve configuration.", e);
    }

    className = this.getClass().getSimpleName();
    logger.debug("Class name set to: {}", className);

    try {
      queryParams = configurationUtils.getQueryParam(className);
      logger.debug("Query parameters retrieved: {}", queryParams);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Failed to retrieve query parameters for class: ".concat(className), e);
    }

    try {
      urlParams = configurationUtils.getUrlParams(className);
      logger.debug("URL parameters retrieved: {}", urlParams);
    } catch (ConfigurationException e) {
      throw new ConfigurationException(
          "Failed to retrieve URL parameters for class: ".concat(className), e);
    }

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
  public abstract T download(List<String> paramList, Request request)
      throws DownloaderException, ConfigurationException;
}
