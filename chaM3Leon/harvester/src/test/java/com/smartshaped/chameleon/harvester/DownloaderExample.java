package com.smartshaped.chameleon.harvester;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.downloader.Downloader;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

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

  @Override
  public void closeConnections() throws DownloaderException {}
}
