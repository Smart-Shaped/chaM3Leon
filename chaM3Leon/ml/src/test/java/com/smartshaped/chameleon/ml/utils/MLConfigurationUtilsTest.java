package com.smartshaped.chameleon.ml.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import java.util.Iterator;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MLConfigurationUtilsTest {

  MLConfigurationUtils mlConfigurationUtils;

  @Mock YAMLConfiguration configuration;
  @Mock Iterator<String> iterator;
  @InjectMocks MLConfigurationUtils mlConfigurationUtilsMock;

  @BeforeEach
  void setUp() throws ConfigurationException {
    mlConfigurationUtils = MLConfigurationUtils.getMlConf();
  }

  @Test
  void testGetHDFSPathSuccess() {
    assertDoesNotThrow(
        () -> mlConfigurationUtils.getHDFSPath("com.smartshaped.fesr.framework.ml.CustomReader"));
  }

  @Test
  void testGetHDFSPathDefaultValue() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getHDFSPath("test"));
  }

  @Test
  void testGetHDFSReadersSuccess() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getHdfsReaders());
  }

  @Test
  void testGetHDFSReadersFailure() {

    when(iterator.hasNext()).thenReturn(true);
    when(iterator.next()).thenReturn(".class");
    when(configuration.getKeys("ml.hdfs.readers")).thenReturn(iterator);
    when(configuration.getString(".class")).thenReturn("test");

    assertThrows(ConfigurationException.class, () -> mlConfigurationUtilsMock.getHdfsReaders());
  }

  @Test
  void testGetModelDir() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getModelDir());
  }

  @Test
  void testGetModelSaver() {

    try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

      mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(null);

      assertDoesNotThrow(() -> mlConfigurationUtils.getModelSaver());
    }
  }

  @Test
  void testGetPipeline() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getPipeline());
  }

  @Test
  void testGetBlackbox() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackbox());
  }

  @Test
  void testGetBlackboxInputs() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxInputs());
  }

  @Test
  void testGetBlackboxOutput() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxOutput());
  }

  @Test
  void testGetBlackboxModelPath() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxModelPath());
  }

  @Test
  void testGetBlackboxPythonScriptPath() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxPythonScriptPath());
  }

  @Test
  void testGetBlackboxPythonLibraries() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxPythonLibraries());
  }

  @Test
  void testGetBlackboxPythonExtraScripts() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxPythonExtraScripts());
  }

  @Test
  void testGetBlackboxPythonRequirementsPath() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxPythonRequirementsPath());
  }

  @Test
  void testGetBlackboxFolder() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxFolder());
  }

  @Test
  void testGetBlackboxPythonExtraArguments() {
    assertDoesNotThrow(() -> mlConfigurationUtils.getBlackboxPythonExtraArguments());
  }
}
