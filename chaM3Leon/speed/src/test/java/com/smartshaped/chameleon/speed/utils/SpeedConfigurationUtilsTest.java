package com.smartshaped.chameleon.speed.utils;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpeedConfigurationUtilsTest {

  YAMLConfiguration ymlConfig;

  @Mock private ConfigurationInterpolator interpolator;

  @BeforeEach
  public void resetSingleton()
      throws SecurityException,
          NoSuchFieldException,
          IllegalArgumentException,
          IllegalAccessException {
    Field instance = SpeedConfigurationUtils.class.getDeclaredField("configuration");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void getKafkaConfigTestFailureServer() throws ConfigurationException {
    try (MockedConstruction<YAMLConfiguration> mockedConstruction =
        mockConstruction(
            YAMLConfiguration.class,
            (mock, context) -> {
              when(mock.getInterpolator()).thenReturn(interpolator);
            })) {
      SpeedConfigurationUtils speedConfUt = SpeedConfigurationUtils.getSpeedConf();
      assertThrows(ConfigurationException.class, speedConfUt::getKafkaConfig);
    }
  }

  @Test
  void getKafkaConfigTestFailureInterval() throws ConfigurationException {
    try (MockedConstruction<YAMLConfiguration> mockedConstruction =
        mockConstruction(
            YAMLConfiguration.class,
            (mock, context) -> {
              when(mock.getInterpolator()).thenReturn(interpolator);
              when(mock.getString("speed.kafka.server")).thenReturn("localhost:9092");
            })) {
      SpeedConfigurationUtils speedConfUt = SpeedConfigurationUtils.getSpeedConf();
      assertThrows(ConfigurationException.class, speedConfUt::getKafkaConfig);
    }
  }

  @Test
  void speedUpdaterTestFailureClassName() throws ConfigurationException {
    try (MockedConstruction<YAMLConfiguration> mockedConstruction =
        mockConstruction(
            YAMLConfiguration.class,
            (mock, context) -> {
              when(mock.getInterpolator()).thenReturn(interpolator);
            })) {
      SpeedConfigurationUtils speedConfUt = SpeedConfigurationUtils.getSpeedConf();
      assertThrows(ConfigurationException.class, speedConfUt::getSpeedUpdater);
    }
  }
}
