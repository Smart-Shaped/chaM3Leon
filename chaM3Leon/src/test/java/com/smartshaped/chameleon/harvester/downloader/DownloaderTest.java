package com.smartshaped.chameleon.harvester.downloader;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.request.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DownloaderTest {

    @Mock
    private List paramList;
    @Mock
    private Request request;


    @Test
    void testDownloaderSuccess () throws ConfigurationException {
        Downloader downloader = new DownloaderClassTest();
        assertDoesNotThrow(() -> downloader.download(paramList, request));
    }


}
