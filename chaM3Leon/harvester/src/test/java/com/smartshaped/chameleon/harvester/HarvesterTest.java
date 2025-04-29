package com.smartshaped.chameleon.harvester;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.exception.HarvesterException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.harvester.saver.HarvesterSaver;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class HarvesterTest {

  @Mock private Request request;
  @Mock private Dataset<Row> dataset;
  @Mock private HarvesterConfigurationUtils configurationUtils;
  @Mock private RequestHandler requestHandler;

  private List<Harvester> harvestersList = new ArrayList<>();

  private List<String> paramList = new ArrayList<>();

  @Mock private Harvester harvester;
  @Mock private DownloaderExample downloaderExample;
  @Mock private PreprocessorExample preprocessorExample;

  @BeforeEach
  void setup() {
    harvestersList.add(harvester);
    paramList.add("param1");
    paramList.add("param2");
  }

  @Test
  void executeSuccess() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      HarvesterExample harvesterExample = new HarvesterExample();
      assertDoesNotThrow(() -> harvesterExample.execute(request));
    }
  }

  @Test
  void downloadAndTransformSuccess() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      HarvesterExample harvesterExample = new HarvesterExample();
      assertDoesNotThrow(() -> harvesterExample.download(paramList, request));
    }
  }

  @Test
  void downloadAndTransformNull() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      HarvesterExample harvesterExample = new HarvesterExample();
      assertDoesNotThrow(() -> harvesterExample.download(paramList, request));
    }
  }

  @Test
  void downloadAndTransformFailure() throws ConfigurationException, DownloaderException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      when(downloaderExample.download(paramList, request)).thenThrow(ConfigurationException.class);
      HarvesterExample harvesterExample = new HarvesterExample();
      assertThrows(HarvesterException.class, () -> harvesterExample.download(paramList, request));
    }
  }

  @Test
  void processSuccess() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
      when(configurationUtils.getPreprocessor(anyString())).thenReturn(preprocessorExample);

      HarvesterExample harvesterExample = new HarvesterExample();
      assertDoesNotThrow(() -> harvesterExample.process(dataset));
    }
  }

  @Test
  void processNull() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
      when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);

      HarvesterExample harvesterExample = new HarvesterExample();
      assertDoesNotThrow(() -> harvesterExample.process(dataset));
    }
  }
}
