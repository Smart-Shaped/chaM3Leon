package com.smartshaped.chameleon.harvester.exception;

public class HarvesterLayerException extends Exception {

    public HarvesterLayerException(String message) {
        super("Exception in the Harvester Layer. Caused by: \n" + message);
    }

    public HarvesterLayerException(Throwable err) {
        super("Exception in the Harvester Layer. Caused by : \n" + err.getMessage(), err);
    }

    public HarvesterLayerException(String errMessage, Throwable err) {
        super(errMessage + "\n" + err.getMessage(), err);
    }

}
