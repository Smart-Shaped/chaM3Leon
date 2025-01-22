package com.smartshaped.chameleon.ml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackBox.BlackBox;
import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.ml.exception.MLLayerException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class MLLayerTest {

	MLLayer mlLayer;
	List<HdfsReader> readerList;
	@Mock
	SparkSession sedona;
	@Mock
	Pipeline pipeline;
	@Mock
	BlackBox blackBox;
	@Mock
	ModelSaver modelSaver;
	@Mock
	MLConfigurationUtils configurationUtils;
	@Mock
	HdfsReader reader;
	@Mock
	SparkSession.Builder builder;
	@Mock
	Dataset<Row> predictions;
	@Mock
	CassandraUtils cassandraUtils;
	@Mock
	SparkConf sparkConf;

	@BeforeEach
	void setUp() {
		mlLayer = mock(MLLayer.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	void testConstructorSuccess() throws ConfigurationException {

		try (MockedStatic<SedonaContext> mockedStatic = mockStatic(SedonaContext.class);
			 MockedStatic<MLConfigurationUtils> mockedMlConfig = mockStatic(MLConfigurationUtils.class);) {

			mockedStatic.when(SedonaContext::builder).thenReturn(builder);
			when(builder.config(any(SparkConf.class))).thenReturn(builder);
			when(builder.getOrCreate()).thenReturn(sedona);

			mockedMlConfig.when(MLConfigurationUtils::getMlConf).thenReturn(configurationUtils);
			when(configurationUtils.getPipeline()).thenReturn(pipeline);
			when(configurationUtils.getSparkConf()).thenReturn(sparkConf);

			mockedStatic.when(() -> SedonaContext.create(sedona)).thenReturn(sedona);

			assertDoesNotThrow(CustomMlLayer::new);
		}
	}

	@Test
	void testConstructorFailureSparkSessionCreation() {

		assertThrows(MLLayerException.class, CustomMlLayer::new);
	}

	@Test
	void testConstructorFailureBothMLNull() throws ConfigurationException {

		try (MockedStatic<MLConfigurationUtils> mockedStatic = mockStatic(MLConfigurationUtils.class)) {

			mockedStatic.when(MLConfigurationUtils::getMlConf).thenReturn(configurationUtils);
			when(configurationUtils.getPipeline()).thenReturn(null);
			when(configurationUtils.getBlackBox()).thenReturn(null);
			assertThrows(MLLayerException.class, CustomMlLayer::new);
		}
	}

	@Test
	void testConstructorFailureBothMLNotNull() throws ConfigurationException {

		try (MockedStatic<MLConfigurationUtils> mockedStatic = mockStatic(MLConfigurationUtils.class)) {

			mockedStatic.when(MLConfigurationUtils::getMlConf).thenReturn(configurationUtils);
			when(configurationUtils.getPipeline()).thenReturn(pipeline);
			when(configurationUtils.getBlackBox()).thenReturn(blackBox);
			assertThrows(MLLayerException.class, CustomMlLayer::new);
		}
	}

	@Test
    void testStartWithPipelineSuccess() {

        when(pipeline.getPredictions()).thenReturn(predictions);

        readerList = new ArrayList<>();
        readerList.add(reader);

        mlLayer.setReaderList(readerList);
        mlLayer.setSedona(sedona);
        mlLayer.setPipeline(pipeline);
        mlLayer.setModelSaver(modelSaver);

        try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

            mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);

            assertDoesNotThrow(() -> mlLayer.start());
        }
    }

	@Test
	void testStartWithPipelineMissingModelSaver() {

		readerList = new ArrayList<>();
		readerList.add(reader);

		mlLayer.setReaderList(readerList);
		mlLayer.setSedona(sedona);
		mlLayer.setPipeline(pipeline);
		mlLayer.setModelSaver(null);

		try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

			mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);

			assertDoesNotThrow(() -> mlLayer.start());
		}
	}

	@Test
	void testStartWithBlackBoxSuccess() {

		when(blackBox.getPredictions()).thenReturn(predictions);

		readerList = new ArrayList<>();
		readerList.add(reader);

		mlLayer.setReaderList(readerList);
		mlLayer.setSedona(sedona);
		mlLayer.setPipeline(null);
		mlLayer.setBlackBox(blackBox);
		mlLayer.setModelSaver(modelSaver);

		try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

			mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);

			assertDoesNotThrow(() -> mlLayer.start());
		}
	}

	@Test
	void testStartWithBlackBoxMissingModelSaver() {

		readerList = new ArrayList<>();
		readerList.add(reader);

		mlLayer.setReaderList(readerList);
		mlLayer.setSedona(sedona);
		mlLayer.setPipeline(null);
		mlLayer.setBlackBox(blackBox);
		mlLayer.setModelSaver(null);

		try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

			mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);

			assertDoesNotThrow(() -> mlLayer.start());
		}
	}

	@Test
	void testStartSuccessNoOptionalParameters() {

		readerList = new ArrayList<>();
		readerList.add(reader);

		mlLayer.setReaderList(readerList);
		mlLayer.setSedona(sedona);

		try (MockedStatic<CassandraUtils> mockedStatic = mockStatic(CassandraUtils.class)) {

			mockedStatic.when(() -> CassandraUtils.getCassandraUtils(any())).thenReturn(cassandraUtils);

			assertDoesNotThrow(() -> mlLayer.start());
		}
	}

	@Test
	void testGettersAndSetters() {

		readerList = mock(List.class);

		mlLayer.setConfigurationUtils(configurationUtils);
		mlLayer.setModelSaver(modelSaver);
		mlLayer.setPipeline(pipeline);
		mlLayer.setReaderList(readerList);
		mlLayer.setSedona(sedona);

		assertEquals(configurationUtils, mlLayer.getConfigurationUtils());
		assertEquals(modelSaver, mlLayer.getModelSaver());
		assertEquals(pipeline, mlLayer.getPipeline());
		assertEquals(readerList, mlLayer.getReaderList());
		assertEquals(sedona, mlLayer.getSedona());
	}
}
