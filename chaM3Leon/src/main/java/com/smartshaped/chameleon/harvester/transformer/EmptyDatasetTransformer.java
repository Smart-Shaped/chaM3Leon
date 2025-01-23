package com.smartshaped.chameleon.harvester.transformer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * An implementation of {@link DatasetTransformer} that does not transform the dataset.
 */
public class EmptyDatasetTransformer extends DatasetTransformer<Dataset<Row>> {
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation does not do any transformation on the dataset and simply returns the input as is.
	 */
	@Override
	public Dataset<Row> transform(Dataset<Row> t) {
		return t;
	}

}
