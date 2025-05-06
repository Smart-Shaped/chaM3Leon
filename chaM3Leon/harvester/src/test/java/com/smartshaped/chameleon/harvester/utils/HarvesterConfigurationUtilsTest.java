package com.smartshaped.chameleon.harvester.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;

import java.util.List;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.utils.exception.CassandraException;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;

@ExtendWith(MockitoExtension.class)
class HarvesterConfigurationUtilsTest {

  @Mock private CassandraUtils cassandraUtils;
  @Mock private YAMLConfiguration yamlConfig;

  @InjectMocks private HarvesterConfigurationUtils configurationUtils;

  @Test
  void getHarvesterConf() {
    assertDoesNotThrow(HarvesterConfigurationUtils::getHarvesterConf);
  }

  @Test
  void getHarvesterIdSuccess() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(
        () ->
            harvesterConfigurationUtils.getHarvesterId(
                "com.smartshaped.chameleon.harvester.HarvesterExample"));
  }

  @Test
  void getHarvesterIdFailure() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertThrows(
        ConfigurationException.class,
        () -> harvesterConfigurationUtils.getHarvesterId("testFailure"));
  }

  @Test
  void getHarvestersSuccess() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(harvesterConfigurationUtils::getHarvesters);
  }

  @Test
  void getHarvestersFailure() {
    List<String> keys = List.of("test.harvesters.test.class");
    doReturn(keys.iterator()).when(yamlConfig).getKeys(any());
    doReturn(null).when(yamlConfig).getString(any());

    assertThrows(ConfigurationException.class, configurationUtils::getHarvesters);
  }

  @Test
  void getHarvestersInstatiationFailure() {
    List<String> keys = List.of("test.harvesters.test.class");
    doReturn(keys.iterator()).when(yamlConfig).getKeys(any());
    doReturn("test").when(yamlConfig).getString(any());

    assertThrows(ConfigurationException.class, configurationUtils::getHarvesters);
  }

  @Test
  void getRequestHandlerSuccessOne() throws ConfigurationException, CassandraException {
    try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
      mockedStaticCassandra
          .when(() -> CassandraUtils.getCassandraUtils(any()))
          .thenReturn(cassandraUtils);

      doNothing().when(cassandraUtils).validateTableModel(any());

      HarvesterConfigurationUtils harvesterConfigurationUtils =
          HarvesterConfigurationUtils.getHarvesterConf();
      assertDoesNotThrow(harvesterConfigurationUtils::getRequestHandler);
    }
  }

  @Test
  void getRequestHandlerNull() throws ConfigurationException {

    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        HarvesterConfigurationUtils harvesterConfigurationUtils =
            HarvesterConfigurationUtils.getHarvesterConf();
        doReturn(null).when(yamlConfig).getString("harvester.RequestHandler");
        assertThrows(ConfigurationException.class, harvesterConfigurationUtils::getRequestHandler);
      }
    }
  }

  @Test
  void getRequestHandlerFailure() throws ConfigurationException {

    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        HarvesterConfigurationUtils harvesterConfigurationUtils =
            HarvesterConfigurationUtils.getHarvesterConf();
        doReturn("wrongRequestHandler").when(yamlConfig).getString("harvester.RequestHandler");
        assertThrows(ConfigurationException.class, harvesterConfigurationUtils::getRequestHandler);
      }
    }
  }

  @Test
  void getPreprocessorParams() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(() -> harvesterConfigurationUtils.getPreprocessorParams("HarvesterExample"));
  }

  @Test
  void getPreprocessorParamsNotFound() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(() -> harvesterConfigurationUtils.getPreprocessorParams(""));
  }

  @Test
  void getParamsNotFound() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(() -> harvesterConfigurationUtils.getQueryParam(""));
  }

  @Test
  void getPreprocessorSuccess() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(
        () -> harvesterConfigurationUtils.getPreprocessor("harvester.harvesters.harvester1"));
  }

  @Test
  void getPreprocessorNotFound() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(
        () -> harvesterConfigurationUtils.getPreprocessor("harvester.harvesters.harvester2"));
  }

  @Test
  void getPreprocessorFailure() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      HarvesterConfigurationUtils harvesterConfigurationUtils =
          HarvesterConfigurationUtils.getHarvesterConf();
      doReturn("wrongPreprocessorClass").when(yamlConfig).getString("harvesterId.preprocessor", "");
      assertThrows(
          ConfigurationException.class,
          () -> harvesterConfigurationUtils.getPreprocessor("harvesterId"));
    }
  }

  @Test
  void getUrlParamsSuccess() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(() -> harvesterConfigurationUtils.getUrlParams("DownloaderExample"));
  }

  @Test
  void getQueryParamSuccess() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(() -> harvesterConfigurationUtils.getQueryParam("DownloaderExample"));
  }

  @Test
  void getDownloader() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertDoesNotThrow(
        () -> harvesterConfigurationUtils.getDownloader("harvester.harvesters.harvester1"));
  }

  @Test
  void getDownloaderEmpty() throws ConfigurationException {
    HarvesterConfigurationUtils harvesterConfigurationUtils =
        HarvesterConfigurationUtils.getHarvesterConf();
    assertThrows(
        ConfigurationException.class,
        (() -> harvesterConfigurationUtils.getDownloader("harvester.harvesters.harvester2")));
  }

  @Test
  void getDownloaderFailure() throws ConfigurationException {
    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      HarvesterConfigurationUtils harvesterConfigurationUtils =
          HarvesterConfigurationUtils.getHarvesterConf();
      doReturn("wrongPreprocessorClass").when(yamlConfig).getString("harvesterId.downloader.class");
      assertThrows(
          ConfigurationException.class,
          () -> harvesterConfigurationUtils.getDownloader("harvesterId"));
    }
  }
}
