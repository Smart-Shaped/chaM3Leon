package com.smartshaped.chameleon.harvester.exception;

public class HarvesterException extends Exception {

    public HarvesterException(String message) {
        super("Exception in the Harvester. Caused by: \n" + message);
    }

    public HarvesterException(Throwable err) {
        super("Exception in the Harvester. Caused by : \n" + err.getMessage(), err);
    }

    public HarvesterException(String errMessage, Throwable err) {
        super(errMessage + "\n" + err.getMessage(), err);
    }

}
