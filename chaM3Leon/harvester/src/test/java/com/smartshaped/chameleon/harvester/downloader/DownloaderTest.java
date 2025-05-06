package com.smartshaped.chameleon.harvester.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.request.Request;

class DownloaderTest {

  @Mock private List<String> paramList;
  @Mock private Request request;

  @Test
  void testDownloaderSuccess() throws ConfigurationException {
    Downloader downloader = new DownloaderClassTest();
    assertDoesNotThrow(() -> downloader.download(paramList, request));
  }
}
