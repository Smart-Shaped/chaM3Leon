package com.smartshaped.chameleon.harvester.downloader;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.util.List;

public class DownloaderClassTest extends Downloader {

  public DownloaderClassTest() throws ConfigurationException {
    super();
  }

  @Override
  public Object download(List reqParams, Request req)
      throws DownloaderException, ConfigurationException {
    return null;
  }

  @Override
  protected List<String> createUriList(List paramList) {
    return List.of();
  }
}
