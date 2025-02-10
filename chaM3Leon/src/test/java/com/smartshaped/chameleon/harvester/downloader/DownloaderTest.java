package com.smartshaped.chameleon.harvester.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class DownloaderTest {

  @Mock private List paramList;
  @Mock private Request request;

  @Test
  void testDownloaderSuccess() throws ConfigurationException {
    Downloader downloader = new DownloaderClassTest();
    assertDoesNotThrow(() -> downloader.download(paramList, request));
  }
}
