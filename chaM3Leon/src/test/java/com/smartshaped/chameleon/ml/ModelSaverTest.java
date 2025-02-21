package com.smartshaped.chameleon.ml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import com.smartshaped.chameleon.ml.blackbox.BlackBox;
import com.smartshaped.chameleon.ml.exception.ModelSaverException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.util.MLWritable;
import org.apache.spark.ml.util.MLWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelSaverTest {

  @Mock MLConfigurationUtils mlConfigurationUtils;
  @Mock TableModel tableModel;
  @Mock Pipeline pipeline;
  @Mock BlackBox blackBox;
  Model model;
  @Mock MLWriter mlWriter;
  @Mock Dataset<Row> predictions;
  @Mock CassandraUtils cassandraUtils;
  ModelSaver modelSaver;
  String modelDir;
  String modelName;

  @BeforeEach
  void setUp() {
    modelDir = "ModelSaverExample";
    modelName = "ModelSaverExample";
  }

  @Test
  void testConstructor() throws ConfigurationException {

    try (MockedStatic<MLConfigurationUtils> mockedStatic = mockStatic(MLConfigurationUtils.class);
        MockedStatic<CassandraUtils> mockedStaticModel = mockStatic(CassandraUtils.class)) {

      mockedStatic.when(MLConfigurationUtils::getMlConf).thenReturn(mlConfigurationUtils);

      mockedStaticModel
          .when(() -> CassandraUtils.getCassandraUtils(any()))
          .thenReturn(cassandraUtils);

      when(mlConfigurationUtils.getModelDir()).thenReturn(modelDir);
      when(mlConfigurationUtils.getModelClassName()).thenReturn(modelName);
      when(mlConfigurationUtils.createTableModel(modelName)).thenReturn(tableModel);

      assertDoesNotThrow(ModelSaverExample::new);
    }
  }

  @Test
  void testSaveModelModelSaverException() {
    modelSaver = mock(ModelSaver.class, Mockito.CALLS_REAL_METHODS);

    assertThrows(ModelSaverException.class, () -> modelSaver.saveModel(pipeline));
  }

  @Test
  void testSaveModelSuccess() throws Exception {

    model = mock(Model.class, Mockito.withSettings().extraInterfaces(MLWritable.class));

    try (MockedStatic<MLConfigurationUtils> mockedStatic = mockStatic(MLConfigurationUtils.class)) {

      mockedStatic.when(MLConfigurationUtils::getMlConf).thenReturn(mlConfigurationUtils);

      when(mlConfigurationUtils.getModelDir()).thenReturn(modelDir);
      when(mlConfigurationUtils.getModelClassName()).thenReturn(modelName);
      when(mlConfigurationUtils.createTableModel(modelName)).thenReturn(tableModel);

      when(pipeline.getModel()).thenReturn(model);
      when(pipeline.getPredictions()).thenReturn(predictions);

      MLWritable mlWritable = (MLWritable) model;

      when(mlWritable.write()).thenReturn(mlWriter);
      when(mlWriter.overwrite()).thenReturn(mlWriter);

      try (MockedStatic<CassandraUtils> mockedStaticModel = mockStatic(CassandraUtils.class)) {

        mockedStaticModel
            .when(() -> CassandraUtils.getCassandraUtils(any()))
            .thenReturn(cassandraUtils);

        ModelSaver modelSaver = new ModelSaverExample();

        assertDoesNotThrow(() -> modelSaver.saveModel(pipeline));
      }
    }
  }

  @Test
  void testSaveModelBlackBoxSuccess() throws ConfigurationException, CassandraException {
    when(blackBox.getPredictions()).thenReturn(predictions);

    try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

      mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);
      ModelSaverExample modelSaver = new ModelSaverExample();
      assertDoesNotThrow(() -> modelSaver.saveModel(blackBox));
    }
  }

  @Test
  void testSaveModelBlackBoxFailure() throws ConfigurationException, CassandraException {
    when(blackBox.getPredictions()).thenReturn(predictions);

    try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

      mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);
      doThrow(CassandraException.class).when(cassandraUtils).saveDF(any(), any());

      ModelSaverExample modelSaver = new ModelSaverExample();
      assertThrows(ModelSaverException.class, (() -> modelSaver.saveModel(blackBox)));
    }
  }
}
