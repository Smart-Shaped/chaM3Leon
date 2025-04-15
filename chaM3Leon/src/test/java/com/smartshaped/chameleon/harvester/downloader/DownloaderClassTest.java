package com.smartshaped.chameleon.harvester.downloader;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

public class DownloaderClassTest extends Downloader {

  protected DownloaderClassTest() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of();
  }

  @Override
  public Dataset<Row> download(List<String> paramList, Request request)
      throws DownloaderException, ConfigurationException {
    return null;
  }

  @Override
  public void closeConnections() throws DownloaderException {}
}
