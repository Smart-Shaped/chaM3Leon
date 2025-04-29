package com.smartshaped.chameleon.harvester.downloader;

import java.util.List;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

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

  @Override
  public void closeConnections() throws DownloaderException {}
}
