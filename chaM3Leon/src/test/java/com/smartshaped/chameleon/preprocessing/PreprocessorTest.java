package com.smartshaped.chameleon.preprocessing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreprocessorTest {

  @Mock private Dataset<Row> dataFrame;

  @Test
  void testSimpleBinaryPreprocessSuccess() {

    when(dataFrame.selectExpr(anyString(), anyString(), anyString())).thenReturn(dataFrame);

    SimpleBinaryPreprocessor preprocessor = new SimpleBinaryPreprocessor();

    assertDoesNotThrow(() -> preprocessor.preprocess(dataFrame));
  }

  @Test
  void testSimpleNonBinaryPreprocessSuccess() {

    when(dataFrame.selectExpr(anyString(), anyString(), anyString())).thenReturn(dataFrame);

    SimpleNonBinaryPreprocessor preprocessor = new SimpleNonBinaryPreprocessor();

    assertDoesNotThrow(() -> preprocessor.preprocess(dataFrame));
  }
}
