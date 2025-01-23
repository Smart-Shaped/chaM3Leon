package com.smartshaped.chameleon.harvester.transformer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * An abstract class representing a transformer that transforms input data into a Dataset of Rows.
 */
public abstract class DatasetTransformer <T> {
	
    /**
     * Transforms the given input of type T into a Dataset of Rows.
     *
     * @param t the input data to be transformed
     * @return a Dataset of Rows resulting from the transformation
     */
	public abstract Dataset<Row> transform(T t);

}
