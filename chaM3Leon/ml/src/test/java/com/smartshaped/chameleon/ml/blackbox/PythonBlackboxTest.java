package com.smartshaped.chameleon.ml.blackbox;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.deploy.PythonRunner;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import scala.Option;

@ExtendWith(MockitoExtension.class)
class PythonBlackboxTest {

  ArrayList<Dataset<Row>> datasets = new ArrayList<>();
  @Mock Dataset<Row> dataset;
  @Mock Option<SparkSession> option;
  @Mock SparkSession sparkSession;
  @Mock SparkContext sparkContext;
  @Mock MLConfigurationUtils mlConfigurationUtils;

  @BeforeEach
  void setUp() {
    datasets.add(dataset);
  }

  private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {

    Field instance = MLConfigurationUtils.class.getDeclaredField("configuration");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void testStart() throws ConfigurationException, NoSuchFieldException, IllegalAccessException {

    resetSingleton();

    try (MockedStatic<SparkSession> mockedStaticSpark = mockStatic(SparkSession.class);
        MockedStatic<PythonRunner> mockedStaticPythonRunner = mockStatic(PythonRunner.class);
        MockedConstruction<JavaSparkContext> ignored = mockConstruction(JavaSparkContext.class)) {

      mockedStaticSpark.when(SparkSession::getActiveSession).thenReturn(option);
      when(option.get()).thenReturn(sparkSession);
      when(sparkSession.sparkContext()).thenReturn(sparkContext);

      PythonBlackboxExample blackBox = new PythonBlackboxExample();
      assertDoesNotThrow(() -> blackBox.start(datasets));
    }
  }
}
