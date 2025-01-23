package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.request.Request;

import java.util.List;

public class HarvesterTestClass extends Harvester {

    public HarvesterTestClass() throws ConfigurationException {
        super();
    }

    @Override
    protected List<String> extractParams(Request req) {
        return List.of();
    }

}
