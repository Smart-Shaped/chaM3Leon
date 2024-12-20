package com.smartshaped.chameleon.harvester.exception;

public class DownloaderException extends Exception {

    public DownloaderException(String message) {
        super("Exception related to Downloader. Caused by: \n" + message);
    }

    public DownloaderException(Throwable err) {
        super("Exception related to Downloader. Caused by : \n" + err.getMessage(), err);
    }

    public DownloaderException(String errMessage, Throwable err) {
        super(errMessage + "\n" + err.getMessage(), err);
    }

}
