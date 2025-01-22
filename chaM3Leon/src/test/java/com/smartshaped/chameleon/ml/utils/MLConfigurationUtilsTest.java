package com.smartshaped.chameleon.ml.utils;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.exception.ConfigurationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class MLConfigurationUtilsTest {

	@Mock
	YAMLConfiguration ymlConfig;
	MLConfigurationUtils mlConfigurationUtils;

	@BeforeEach
	void setUp() throws ConfigurationException {
		mlConfigurationUtils = MLConfigurationUtils.getMlConf();
	}

	@Test
	void testGetHDFSPathSuccess() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getHDFSPath("com.smartshaped.fesr.framework.ml.CustomReader"));
	}

	@Test
	void testGetHDFSPathDefaultValue() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getHDFSPath("test"));
	}

	@Test
	void testGetHDFSReaders() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getHdfsReaders());
	}

	@Test
	void testGetModelDir() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getModelDir());
	}

	@Test
	void testGetModelSaver() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getModelSaver());
	}

	@Test
	void testGetPipeline() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getPipeline());
	}

	@Test
	void testGetBlackBox() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getBlackBox());
	}

	@Test
	void testGetBlackBoxInputs() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getBlackBoxInputs());
	}

	@Test
	void testGetBlackBoxOutput() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getBlackBoxOutput());
	}

	@Test
	void testGetBlackBoxModelPath() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getBlackBoxModelPath());
	}

	@Test
	void testGetBlackBoxPythonScriptPath() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getBlackBoxPythonScriptPath());
	}

	@Test
	void testGetBlackBoxPythonLibraries() {
		assertDoesNotThrow(() -> mlConfigurationUtils.getBlackBoxPythonLibraries());
	}
}
