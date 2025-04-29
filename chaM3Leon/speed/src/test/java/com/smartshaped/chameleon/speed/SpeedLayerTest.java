package com.smartshaped.chameleon.speed;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.KafkaConsumer;
import com.smartshaped.chameleon.speed.exception.SpeedLayerException;
import com.smartshaped.chameleon.speed.utils.SpeedConfigurationUtils;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.StreamingQueryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeedLayerTest {

  @Mock private SpeedConfigurationUtils configurationUtils;
  @Mock private CassandraUtils cassandraUtils;
  @Mock private SparkSession sparkSession;
  @Mock private SparkSession.Builder sparkSessionBuilder;
  @Mock private DataFrameReader dataFrameReader;
  @Mock private Dataset<Row> dataSetRow;
  @Mock private SparkConf sparkConf;
  @Mock private Column column;
  @Mock private StreamingQueryManager streamingQueryManager;

  private Map<String, String> kafkaConfig;

  @BeforeEach
  void setUp() {
    kafkaConfig = new HashMap<>();
    kafkaConfig.put("topics", "testTopic1,testTopic2");
    kafkaConfig.put("servers", "localhost:9092");
  }

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
  void testStartSuccess() throws ConfigurationException, SpeedLayerException {

    try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class)) {
      mockedStaticSpark.when(SparkSession::builder).thenReturn(sparkSessionBuilder);
      when(sparkSessionBuilder.getOrCreate()).thenReturn(sparkSession);
      when(sparkSessionBuilder.config(any(SparkConf.class))).thenReturn(sparkSessionBuilder);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(any(SpeedConfigurationUtils.class)))
            .thenReturn(cassandraUtils);
        try (MockedStatic<KafkaConsumer> mockedStaticKafka = mockStatic(KafkaConsumer.class)) {
          mockedStaticKafka.when(() -> KafkaConsumer.kafkaRead(kafkaConfig)).thenReturn(dataSetRow);
          when(sparkSession.streams()).thenReturn(streamingQueryManager);

          SpeedLayerTestClass speedLayer = new SpeedLayerTestClass();
          assertDoesNotThrow(speedLayer::start);
        }
      }
    }
  }

  @Test
  void testConstructorFailure() {

    try (MockedStatic<SpeedConfigurationUtils> mockedSSpeedConfig =
        mockStatic(SpeedConfigurationUtils.class)) {
      mockedSSpeedConfig.when(SpeedConfigurationUtils::getSpeedConf).thenReturn(configurationUtils);
      when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
      try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class)) {
        mockedStaticSpark.when(SparkSession::builder).thenReturn(sparkSessionBuilder);
        assertThrows(SpeedLayerException.class, SpeedLayerTestClass::new);
      }
    }
  }

  @Test
  void testStartFailure()
      throws ConfigurationException, SpeedLayerException, StreamingQueryException {

    try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class)) {
      mockedStaticSpark.when(SparkSession::builder).thenReturn(sparkSessionBuilder);
      when(sparkSessionBuilder.getOrCreate()).thenReturn(sparkSession);
      when(sparkSessionBuilder.config(any(SparkConf.class))).thenReturn(sparkSessionBuilder);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(any(SpeedConfigurationUtils.class)))
            .thenReturn(cassandraUtils);
        try (MockedStatic<KafkaConsumer> mockedStaticKafka = mockStatic(KafkaConsumer.class)) {
          mockedStaticKafka.when(() -> KafkaConsumer.kafkaRead(kafkaConfig)).thenReturn(dataSetRow);
          when(sparkSession.streams()).thenReturn(streamingQueryManager);
          doThrow(StreamingQueryException.class).when(streamingQueryManager).awaitAnyTermination();

          SpeedLayerTestClass speedLayer = new SpeedLayerTestClass();
          assertThrows(SpeedLayerException.class, speedLayer::start);
        }
      }
    }
  }
}
