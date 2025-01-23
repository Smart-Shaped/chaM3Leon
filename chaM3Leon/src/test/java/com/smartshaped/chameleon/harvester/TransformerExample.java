package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.harvester.transformer.DatasetTransformer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;

public class TransformerExample extends DatasetTransformer {

    public TransformerExample() {
        super();
    }

    @Override
    public Dataset<Row> transform(Object o) {
        SparkSession sparkSession = SparkSession.getActiveSession().get();
        Dataset<Row> df = sparkSession.createDataFrame(List.of(), Row.class);
        return df;
    }

}
