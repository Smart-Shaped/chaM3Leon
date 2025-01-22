package com.smartshaped.chameleon.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeoutException;

import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.StreamingQueryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.smartshaped.chameleon.batch.exception.BatchUpdaterException;
import com.smartshaped.chameleon.batch.utils.BatchConfigurationUtils;
import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;

@ExtendWith(MockitoExtension.class)
class BatchUpdaterTest {

	@Mock
	private CqlSession session;
	@Mock
	private CqlSessionBuilder cqlSessionBuilder;
	@Mock
	private CqlIdentifier identifier;

	private String modelName;
	@Mock
	private BatchConfigurationUtils configurationUtils;
	@Mock
	private TableModel tableModel;
	@Mock
	private CassandraUtils cassandraUtils;
	@Mock
	private Dataset<Row> dataFrame;
	@Mock
	private SparkSession sparkSession;
	@Mock
	DataStreamWriter<Row> mockDataStreamWriter;
	@Mock
	private StreamingQueryManager streamingQueryManager;

	@Test
    void testStartUpdateSuccess() throws ConfigurationException, CassandraException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);
    	when(dataFrame.writeStream()).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.foreachBatch(any(VoidFunction2.class))).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.trigger(any())).thenReturn(mockDataStreamWriter);

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	when(sparkSession.streams()).thenReturn(streamingQueryManager);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertDoesNotThrow(() -> batchUpdater.startUpdate(dataFrame,sparkSession,30000L));

	        }
	    }

    }

	@Test
    void testStartUpdateTimeoutFailure() throws ConfigurationException, CassandraException, StreamingQueryException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);
    	when(dataFrame.writeStream()).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.foreachBatch(any(VoidFunction2.class))).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.trigger(any())).thenReturn(mockDataStreamWriter);
    	when(sparkSession.streams()).thenReturn(streamingQueryManager);
    	doThrow(StreamingQueryException.class).when(streamingQueryManager).awaitAnyTermination();

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertThrows(BatchUpdaterException.class,() -> batchUpdater.startUpdate(dataFrame,sparkSession,30000L));

	        }
	    }
    }

	@Test
    void testStartUpdateStreamingFailure() throws ConfigurationException, CassandraException, TimeoutException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);
    	when(dataFrame.writeStream()).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.foreachBatch(any(VoidFunction2.class))).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.trigger(any())).thenReturn(mockDataStreamWriter);
    	when(mockDataStreamWriter.start()).thenThrow(TimeoutException.class);

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertThrows(BatchUpdaterException.class,() -> batchUpdater.startUpdate(dataFrame,sparkSession,30000L));
	        }
	    }
    }

	@Test
    void testSaveBatchSuccess() throws ConfigurationException, CassandraException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);

		try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
		    mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
		    try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
		    	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

		    	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
		    	assertDoesNotThrow(() -> batchUpdater.saveBatch(dataFrame));

		    }
		}
    }

	@Test
    void testGetCassandraUtilsSuccess() throws ConfigurationException, CassandraException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertDoesNotThrow(() -> batchUpdater.getCassandraUtils());

	        }
	    }
    }

	@Test
    void testGetConfigurationUtilsSuccess() throws ConfigurationException, CassandraException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertDoesNotThrow(() -> batchUpdater.getConfigurationUtils());

	        }
	    }
    }

	@Test
    void testGetModelNameSuccess() throws ConfigurationException, CassandraException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertDoesNotThrow(() -> batchUpdater.getModelName());

	        }
	    }
    }

	@Test
    void testGetTableModelSuccess() throws ConfigurationException, CassandraException {

    	when(configurationUtils.getModelClassName()).thenReturn(modelName);
    	when(configurationUtils.createTableModel(modelName)).thenReturn(tableModel);
    	doNothing().when(cassandraUtils).validateTableModel(tableModel);

	    try (MockedStatic<BatchConfigurationUtils> mockedStatic = mockStatic(BatchConfigurationUtils.class)) {
	        mockedStatic.when(BatchConfigurationUtils::getBatchConf).thenReturn(configurationUtils);
	        try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
	        	mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils)).thenReturn(cassandraUtils);

	        	BatchUpdaterTestClass batchUpdater = new BatchUpdaterTestClass();
	        	assertDoesNotThrow(() -> batchUpdater.getTableModel());

	        }
	    }
    }

}
