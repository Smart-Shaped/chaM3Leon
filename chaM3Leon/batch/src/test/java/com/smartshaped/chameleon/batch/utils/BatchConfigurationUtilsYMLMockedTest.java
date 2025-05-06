package com.smartshaped.chameleon.batch.utils;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.batch.BatchUpdater;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;

@ExtendWith(MockitoExtension.class)
class BatchConfigurationUtilsYMLMockedTest {

  @Mock YAMLConfiguration configuration;

  @InjectMocks BatchConfigurationUtils batchConfig;

  @Test
  void testGetBatchUpdaterNullOrMissing() throws ConfigurationException {

    doReturn("").when(configuration).getString("batch.updater.class");

    BatchUpdater batchUpdater = batchConfig.getBatchUpdater();
    assertNull(batchUpdater);

    doReturn(null).when(configuration).getString("batch.updater.class");

    batchUpdater = batchConfig.getBatchUpdater();
    assertNull(batchUpdater);
  }

  @Test
  void testGetBatchUpdaterFailure() {

    doReturn("WrongClassName").when(configuration).getString("batch.updater.class");

    assertThrows(ConfigurationException.class, () -> batchConfig.getBatchUpdater());

    doReturn("com.smartshaped.fesr.framework.batch.com.smartshaped.chameleon.batch.utils.BatchConfigurationUtilsYMLMockedTest")
        .when(configuration)
        .getString("batch.updater.class");

    assertThrows(ConfigurationException.class, () -> batchConfig.getBatchUpdater());
  }

  @Test
  void testGetKafkaConfigServerFailure() {

    doReturn(null).when(configuration).getString("batch.kafka.server");

    assertThrows(ConfigurationException.class, () -> batchConfig.getKafkaConfig());
  }

  @Test
  void testGetKafkaConfigIntervalFailure() {

    doReturn("servers").when(configuration).getString("batch.kafka.server");

    doReturn(null).when(configuration).getString("batch.kafka.intervalMs");

    assertThrows(ConfigurationException.class, () -> batchConfig.getKafkaConfig());
  }

  @Test
  void testGetKafkaConfigTopicsFailure() {

    doReturn("servers").when(configuration).getString("batch.kafka.server");

    doReturn("30000").when(configuration).getString("batch.kafka.intervalMs");

    assertThrows(ConfigurationException.class, () -> batchConfig.getKafkaConfig());
  }
}
