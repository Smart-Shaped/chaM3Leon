package com.smartshaped.chameleon.harvester.transformer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class EmptyDatasetTransformerTest {

    @Mock
    private Dataset<Row> dataset;


    @Test
    void transformSuccess() {
        EmptyDatasetTransformer transformer = new EmptyDatasetTransformer();
        assertDoesNotThrow(() -> transformer.transform(dataset));
    }

}
