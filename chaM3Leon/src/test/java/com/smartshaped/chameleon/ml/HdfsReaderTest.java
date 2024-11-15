package com.smartshaped.chameleon.ml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import com.smartshaped.chameleon.ml.utils.MLConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class HdfsReaderTest {

	HdfsReader hdfsReader;
	@Mock
	MLConfigurationUtils configurationUtils;
	@Mock
	SparkSession sedona;
	@Mock
	Dataset<Row> dataFrame;
	@Mock
	DataFrameReader dataFrameReader;

	@Test
	void testConstructor() {

		assertDoesNotThrow(CustomReader::new);
	}

	@Test
    void testStartSuccess() throws HdfsReaderException {

        when(sedona.read()).thenReturn(dataFrameReader);
        when(sedona.read().parquet(anyString())).thenReturn(dataFrame);

        hdfsReader = mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);
        hdfsReader.setHdfsPath(anyString());

        hdfsReader.start(sedona);

        assertNotNull(hdfsReader);
    }

	@Test
    void testStartFailureRead() {

        when(sedona.read()).thenReturn(dataFrameReader);
        doThrow(RuntimeException.class).when(dataFrameReader).parquet(anyString());

        hdfsReader = mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);

        assertThrows(HdfsReaderException.class, () -> {
            hdfsReader.start(sedona);
        });
    }

	@Test
	void testStartFailureProcess() throws HdfsReaderException {

		hdfsReader = Mockito.mock(HdfsReader.class, Mockito.CALLS_REAL_METHODS);

		doNothing().when(hdfsReader).readRawData(sedona);
		doThrow(HdfsReaderException.class).when(hdfsReader).processRawData(sedona);

		assertThrows(HdfsReaderException.class, () -> {
			hdfsReader.start(sedona);
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
