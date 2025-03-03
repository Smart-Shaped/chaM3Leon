package com.smartshaped.chameleon.harvester.downloader;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import org.apache.spark.sql.Dataset;

import java.util.List;

public class DownloaderClassTest extends Downloader {


  protected DownloaderClassTest() throws ConfigurationException {
    super();
  }

  /**
*
 * @param paramList The list of parameters to be used in the URI.
 * @param request
 * @return
 * @throws DownloaderException
*/
  @Override
  protected List<String> createUriList(List paramList, Request request) throws DownloaderException {
    return List.of();
  }

/**
 * @param paramList The list of parameters needed for the download.
 * @param request   The request that contains the parameters needed for the download.
 * @return
 * @throws DownloaderException
 * @throws ConfigurationException
 */
  @Override
  public Dataset download(List paramList, Request request) throws DownloaderException, ConfigurationException {
    return null;
  }
}
