package com.smartshaped.chameleon.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.batch.exception.BatchLayerException;
import com.smartshaped.chameleon.batch.exception.BatchUpdaterException;
import com.smartshaped.chameleon.batch.utils.BatchConfigurationUtils;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.exception.KafkaConsumerException;
import com.smartshaped.chameleon.common.utils.KafkaConsumer;
import com.smartshaped.chameleon.preprocessing.Preprocessor;
import com.smartshaped.chameleon.preprocessing.exception.PreprocessorException;

@ExtendWith(MockitoExtension.class)
class BatchLayerTest {

  @Mock private BatchConfigurationUtils configurationUtils;
  @Mock private SparkSession sparkSession;
  @Mock private SparkSession.Builder builder;
  @Mock private BatchUpdater batchUpdater;
  @Mock private Dataset<Row> dataFrame;
  @Mock private Map<String, String> kafkaConfig;
  @Mock private SparkConf sparkConf;
  @Mock private Column column;
  @Mock private Map<String, Preprocessor> preprocessors;
  @Mock private PreprocessorTestClass testPreprocessor;
  @Mock private StreamingQueryManager streamingQueryManager;

  @BeforeEach
  void setUp() {
    kafkaConfig = new HashMap<>();
    kafkaConfig.put("topics", "testTopic1,testTopic2");
    kafkaConfig.put("servers", "localhost:9092");
    kafkaConfig.put("intervalMs", "30000");

    testPreprocessor = new PreprocessorTestClass();
    preprocessors = new HashMap<>();
    preprocessors.put("testTopic1", testPreprocessor);
  }

  @Test
  void testConstructorSuccess() throws ConfigurationException {

    when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
    when(configurationUtils.getKafkaConfig()).thenReturn(kafkaConfig);
    when(configurationUtils.getPreprocessors()).thenReturn(preprocessors);
    when(configurationUtils.getBatchUpdater()).thenReturn(batchUpdater);

    try (MockedStatic<BatchConfigurationUtils> mockedStatic =
        mockStatic(BatchConfigurationUtils.class)) {
      mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
      try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class)) {

        when(builder.config(any(SparkConf.class))).thenReturn(builder);
        when(builder.getOrCreate()).thenReturn(sparkSession);

        mockedStaticSpark.when(SparkSession::builder).thenReturn(builder);

        assertDoesNotThrow(BatchLayerTestClass::new);
      }
    }
  }

  @Test
  void testStartSuccess() throws ConfigurationException, BatchLayerException {

    when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
    when(configurationUtils.getKafkaConfig()).thenReturn(kafkaConfig);
    when(configurationUtils.getPreprocessors()).thenReturn(preprocessors);
    when(configurationUtils.getBatchUpdater()).thenReturn(batchUpdater);
    when(column.equalTo(any())).thenReturn(column);
    when(dataFrame.col(anyString())).thenReturn(column);
    when(dataFrame.filter(any(Column.class))).thenReturn(dataFrame);

    try (MockedStatic<BatchConfigurationUtils> mockedStatic =
        mockStatic(BatchConfigurationUtils.class)) {
      mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
      try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class)) {

        when(builder.config(any(SparkConf.class))).thenReturn(builder);
        when(builder.getOrCreate()).thenReturn(sparkSession);

        mockedStaticSpark.when(SparkSession::builder).thenReturn(builder);

        try (MockedStatic<KafkaConsumer> mockedStaticKafka = mockStatic(KafkaConsumer.class)) {
          mockedStaticKafka.when(() -> KafkaConsumer.kafkaRead(kafkaConfig)).thenReturn(dataFrame);

          try (MockedStatic<HdfsSaver> mockedStaticSaver = mockStatic(HdfsSaver.class)) {

            mockedStaticSaver
                .when(() -> HdfsSaver.save(any(), anyString(), anyString(), anyLong()))
                .thenAnswer(invocation -> null);

            when(sparkSession.streams()).thenReturn(streamingQueryManager);

            BatchLayerTestClass testBatch = new BatchLayerTestClass();
            assertDoesNotThrow(testBatch::start);
          }
        }
      }
    }
  }
}
