package com.smartshaped.chameleon.common.utils;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.DataStreamReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

	@Mock
	SparkSession sparkSession;

	@Mock
	DataStreamReader dsr;

	@Mock
	Dataset<Row> dataset;

	Map<String, String> properties = new HashMap<>();

	@Test
	void testKafkaRead() {

		properties.put("topics", "topic1");
		properties.put("servers", "kafka.bootstrap.servers");

		when(sparkSession.readStream()).thenReturn(dsr);
		when(dsr.format("kafka")).thenReturn(dsr);
		when(dsr.option(anyString(), anyString())).thenReturn(dsr);
		when(dsr.load()).thenReturn(dataset);

		assertDoesNotThrow(() -> KafkaConsumer.kafkaRead(properties, sparkSession));
	}

}
