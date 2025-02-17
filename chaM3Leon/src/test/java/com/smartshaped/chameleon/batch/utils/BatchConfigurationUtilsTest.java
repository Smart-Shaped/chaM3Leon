package com.smartshaped.chameleon.batch.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.batch.BatchUpdater;
import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import com.smartshaped.chameleon.preprocessing.Preprocessor;

@ExtendWith(MockitoExtension.class)
class BatchConfigurationUtilsTest {

  private String modelName;
  @Mock private BatchConfigurationUtils configurationUtils;
  @Mock private CassandraUtils cassandraUtils;
  @Mock private TableModel tableModel;

  @BeforeEach
  public void resetSingleton()
      throws SecurityException,
          NoSuchFieldException,
          IllegalArgumentException,
          IllegalAccessException {
    Field instance = BatchConfigurationUtils.class.getDeclaredField("configuration");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void testGetBatchConfSuccess() throws ConfigurationException {

    BatchConfigurationUtils config = BatchConfigurationUtils.getBatchConf();
    BatchConfigurationUtils config2 = BatchConfigurationUtils.getBatchConf();
    assertEquals(config, config2);
    BatchConfigurationUtils config3 = mock(BatchConfigurationUtils.class);
    assertNotEquals(config, config3);
  }

  @Test
  void testGetKafkaConfigSuccess() throws ConfigurationException {
    BatchConfigurationUtils config = BatchConfigurationUtils.getBatchConf();
    Map<String, String> kafkaConfig = config.getKafkaConfig();

    assertAll(
        () -> assertEquals("kafkaservers", kafkaConfig.get("servers")),
        () -> assertEquals("topic1,topicTif", kafkaConfig.get("topics")),
        () -> assertEquals("/topic1/data.parquet", kafkaConfig.get("topic1.path")),
        () -> assertEquals("/checkpoint/topic1", kafkaConfig.get("topic1.checkpoint")));
  }

  @Test
  void testGetPreprocessorsSuccess() throws ConfigurationException {
    BatchConfigurationUtils config = BatchConfigurationUtils.getBatchConf();
    Map<String, Preprocessor> preprocessors = config.getPreprocessors();

    assertAll(
        () -> assertNotEquals(null, preprocessors),
        () -> assertNotEquals(null, preprocessors.get("topicTif")),
        () -> assertNotEquals(null, preprocessors.get("topic1")));
  }

  @Test
  void testGetBatchUpdaterSuccess() throws ConfigurationException, CassandraException {
    BatchConfigurationUtils config = BatchConfigurationUtils.getBatchConf();

    when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    doNothing().when(cassandraUtils).validateTableModel(tableModel);

    try (MockedStatic<BatchConfigurationUtils> mockedStatic =
        mockStatic(BatchConfigurationUtils.class)) {
      mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);
        BatchUpdater batchUpdater = config.getBatchUpdater();

        assertAll(() -> assertNotEquals(null, batchUpdater));
      }
    }
  }
}
