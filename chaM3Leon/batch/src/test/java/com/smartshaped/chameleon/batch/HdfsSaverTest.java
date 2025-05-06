package com.smartshaped.chameleon.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeoutException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.OutputMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.batch.exception.HdfsSaverException;

@ExtendWith(MockitoExtension.class)
class HdfsSaverTest {

  @Mock private Dataset<Row> dataFrame;
  @Mock private DataStreamWriter<Row> mockDataStreamWriter;

  @Test
  void testSaveSuccess() {

    when(dataFrame.writeStream()).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.format(anyString())).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.outputMode(any(OutputMode.class))).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.option(anyString(), anyString())).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.trigger(any())).thenReturn(mockDataStreamWriter);

    assertDoesNotThrow(() -> HdfsSaver.save(dataFrame, "", "", 30000L));
  }

  @Test
  void testSaveFailure() throws TimeoutException {

    when(dataFrame.writeStream()).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.format(anyString())).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.outputMode(any(OutputMode.class))).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.option(anyString(), anyString())).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.trigger(any())).thenReturn(mockDataStreamWriter);
    when(mockDataStreamWriter.start()).thenThrow(TimeoutException.class);

    assertThrows(HdfsSaverException.class, () -> HdfsSaver.save(dataFrame, "", "", 30000L));
  }
}
