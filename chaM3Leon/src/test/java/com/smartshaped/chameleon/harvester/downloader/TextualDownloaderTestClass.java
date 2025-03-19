package com.smartshaped.chameleon.harvester.downloader;

import java.util.List;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

public class TextualDownloaderTestClass extends TextualDownloader {
  protected TextualDownloaderTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of();
  }

  @Override
  public void closeConnections() throws DownloaderException {}
}
