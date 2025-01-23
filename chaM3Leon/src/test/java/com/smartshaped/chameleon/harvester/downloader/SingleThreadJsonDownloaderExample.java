package com.smartshaped.chameleon.harvester.downloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartshaped.chameleon.common.exception.ConfigurationException;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SingleThreadJsonDownloaderExample extends SingleThreadJsonDownloader {

    public SingleThreadJsonDownloaderExample() throws ConfigurationException {
        super();
    }

    @Override
    protected List<String> createUriList(List<String> paramList) {
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        return list;
    }

    @Override
    protected JsonNode joinResponses(List<HttpResponse<String>> responses) {
        return null;
    }

}
