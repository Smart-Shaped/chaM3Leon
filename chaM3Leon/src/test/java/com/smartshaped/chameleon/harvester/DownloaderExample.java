package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.downloader.Downloader;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;

public class DownloaderExample extends Downloader {

    public DownloaderExample() throws ConfigurationException {
        super();
    }

    @Override
    public Object download(List reqParams, Request req) throws DownloaderException, ConfigurationException {
        SparkSession sparkSession = SparkSession.getActiveSession().get();
        Dataset<Row> df = sparkSession.createDataFrame(List.of(), Row.class);
        return df;
    }

    @Override
    protected List<String> createUriList(List paramList) {
        return List.of();
    }

}
