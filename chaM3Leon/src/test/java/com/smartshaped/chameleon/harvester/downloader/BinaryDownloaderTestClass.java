package com.smartshaped.chameleon.harvester.downloader;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class BinaryDownloaderTestClass extends BinaryDownloader {

  protected BinaryDownloaderTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected String createStructuredFileName(String url) {
    return "";
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of();
  }

  /**
   * @param dataset The downloaded data.
   * @param request The request that contains the parameters needed for the extra processing.
   * @return
   */
  @Override
  protected Dataset<Row> extraProcessing(Dataset<Row> dataset, Request request) {
    return null;
  }
}
