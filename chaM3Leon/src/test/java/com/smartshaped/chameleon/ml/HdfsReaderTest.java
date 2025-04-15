package com.smartshaped.chameleon.ml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class HdfsReaderTest {

  HdfsReader hdfsReader;
  @Mock MLConfigurationUtils configurationUtils;
  @Mock SparkSession sedona;
  @Mock Dataset<Row> dataFrame;
  @Mock DataFrameReader dataFrameReader;
  @Mock scala.Option<SparkSession> option;

  private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {

    Field instance = MLConfigurationUtils.class.getDeclaredField("configuration");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void testConstructor() throws NoSuchFieldException, IllegalAccessException {

    resetSingleton();

    assertDoesNotThrow(CustomReader::new);
  }

  @Test
  void testConstructorFailure() throws NoSuchFieldException, IllegalAccessException {

    resetSingleton();

    try (MockedStatic<MLConfigurationUtils> mockedStatic = mockStatic(MLConfigurationUtils.class)) {
      mockedStatic.when(MLConfigurationUtils::getMlConf).thenReturn(configurationUtils);
      when(configurationUtils.getHDFSPath(anyString())).thenReturn("");

      assertThrows(ConfigurationException.class, CustomReader::new);
    }
  }

  @Test
  void testStartSuccess() throws HdfsReaderException {

    try (MockedStatic<SparkSession> mockedStatic = mockStatic(SparkSession.class)) {
      mockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      when(option.get()).thenReturn(sedona);

      when(sedona.read()).thenReturn(dataFrameReader);
      when(sedona.read().parquet(anyString())).thenReturn(dataFrame);

      hdfsReader = mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);
      hdfsReader.setHdfsPath(anyString());

      hdfsReader.start();

      assertNotNull(hdfsReader);
    }
  }

  @Test
  void testStartFailureRead() {

    try (MockedStatic<SparkSession> mockedStatic = mockStatic(SparkSession.class)) {
      mockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      when(option.get()).thenReturn(sedona);

      when(sedona.read()).thenReturn(dataFrameReader);
      doThrow(RuntimeException.class).when(dataFrameReader).parquet(anyString());

      hdfsReader = mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);

      assertThrows(HdfsReaderException.class, () -> hdfsReader.start());
    }
  }

  @Test
  void testStartFailureProcess() throws HdfsReaderException {

    hdfsReader = Mockito.mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);

    doNothing().when(hdfsReader).readRawData();
    doThrow(HdfsReaderException.class).when(hdfsReader).processRawData();

    assertThrows(
        HdfsReaderException.class,
        () -> {
          hdfsReader.start();
        });
  }

  @Test
  void testGettersAndSetters() {

    hdfsReader = mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);

    String hdfsPath = "test";

    hdfsReader.setConfigurationUtils(configurationUtils);
    hdfsReader.setDataframe(dataFrame);
    hdfsReader.setHdfsPath(hdfsPath);

    assertEquals(configurationUtils, hdfsReader.getConfigurationUtils());
    assertEquals(dataFrame, hdfsReader.getDataframe());
    assertEquals(hdfsPath, hdfsReader.getHdfsPath());
  }
}
