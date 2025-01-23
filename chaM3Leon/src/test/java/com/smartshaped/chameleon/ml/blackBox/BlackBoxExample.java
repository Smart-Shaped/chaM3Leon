package com.smartshaped.chameleon.ml.blackBox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackBox.exception.BlackBoxException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public class BlackBoxExample extends BlackBox {

    public BlackBoxExample() throws ConfigurationException {
        super();
    }

    @Override
    protected void runML() throws BlackBoxException {

    }

    @Override
    protected void extraPreparation() throws BlackBoxException {

    }

    @Override
    protected List<Dataset<Row>> mergeDatasetsIfNecessary(List<Dataset<Row>> datasets) throws BlackBoxException {
        return datasets;
    }

    @Override
    protected void postRunning() throws BlackBoxException {

    }

    @Override
    protected Dataset<Row> readOutput(String output) throws BlackBoxException {
        return null;
    }

    @Override
    protected void makeDatasetAccessible(Dataset<Row> dataset, String inputInfo) throws BlackBoxException {

    }

    @Override
    protected void cleanBlackBoxFolder() throws BlackBoxException {

    }

    @Override
    protected void validateParams() throws BlackBoxException {

    }
}
