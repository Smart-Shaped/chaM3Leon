package com.smartshaped.chameleon.harvester.downloader;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.util.List;

public class TextualDownloaderTestClass extends TextualDownloader {
  protected TextualDownloaderTestClass() throws ConfigurationException {
    super();
  }

  /**
   * @param paramList The list of parameters to be used in the URI.
   * @param request
   * @return
   * @throws DownloaderException
   */
  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of();
  }
}
