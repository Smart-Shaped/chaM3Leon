package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.downloader.Downloader;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DownloaderExample extends Downloader {

  public DownloaderExample() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request) {
    return List.of();
  }

  @Override
  public Dataset<Row> download(List<String> reqParams, Request req)
      throws DownloaderException, ConfigurationException {
    return null;
  }
}
