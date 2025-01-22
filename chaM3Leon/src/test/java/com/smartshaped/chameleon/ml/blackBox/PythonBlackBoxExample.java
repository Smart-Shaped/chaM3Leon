package com.smartshaped.chameleon.ml.blackBox;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.ml.blackBox.exception.BlackBoxException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PythonBlackBoxExample extends PythonBlackBox {

    public PythonBlackBoxExample() throws ConfigurationException {
        super();
    }

    @Override
    protected List<Dataset<Row>> mergeDatasetsIfNecessary(List<Dataset<Row>> datasets) throws BlackBoxException {
        return datasets;
    }

    @Override
    protected void postRunning() throws BlackBoxException {

    }

    @Override
    protected void runCommand(ProcessBuilder processBuilder) throws BlackBoxException {

    }
}
