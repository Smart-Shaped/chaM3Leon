package com.smartshaped.chameleon.harvester.downloader;

import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

public class FTPDownloaderTestClass extends FTPDownloader {

  protected FTPDownloaderTestClass() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> createUriList(List<String> paramList, Request request)
      throws DownloaderException {
    return List.of("test");
  }

  public void setFtpClient(FTPClient ftpClient) {
    this.ftpClient = ftpClient;
  }
}
