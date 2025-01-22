package com.smartshaped.chameleon.harvester.utils;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class HarvesterConfigurationUtilsTest {

    @Mock
    private HarvesterConfigurationUtils configurationUtils;
    @Mock
    private CassandraUtils cassandraUtils;

    @Test
    void getHarvesterConf() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getHarvesterConf());
    }

    @Test
    void getHarvesterIdSuccess() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getHarvesterId("com.smartshaped.chameleon.harvester.HarvesterExample"));
    }

    @Test
    void getHarvesterIdFailure() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertThrows(ConfigurationException.class, () -> harvesterConfigurationUtils.getHarvesterId("testFailure"));
    }

    @Test
    void getHarvestersSuccess() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getHarvesters());
    }


    @Test
    void getRequestHandlerSuccessOne() throws ConfigurationException {

        try (MockedStatic<HarvesterConfigurationUtils> mockedStatic = mockStatic(HarvesterConfigurationUtils.class)) {
            mockedStatic.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
            try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
                mockedStaticCassandra.when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
                        .thenReturn(cassandraUtils);

                HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
                assertDoesNotThrow(() -> harvesterConfigurationUtils.getRequestHandler());
            }
        }

    }


    @Test
    void getPreprocessorParams() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getPreprocessorParams("HarvesterExample"));
    }

    @Test
    void getPreprocessorParamsNotFound() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getPreprocessorParams(""));
    }

    @Test
    void getPreprocessorSuccess() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getPreprocessor("harvester.harvesters.harvester1"));
    }


    @Test
    void getPreprocessorNotFound() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getPreprocessor("harvester.harvesters.harvester2"));
    }


    @Test
    void getUrlParamsSuccess() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getUrlParams("DownloaderExample"));
    }

    @Test
    void getQueryParamSuccess() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getQueryParam("DownloaderExample"));
    }


    @Test
    void getTransformer() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getTransformer("harvester.harvesters.harvester1"));
    }

    @Test
    void getTransformerEmpty() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getTransformer("harvester.harvesters.harvester2"));
    }


    @Test
    void getDownloader() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertDoesNotThrow(() -> harvesterConfigurationUtils.getDownloader("harvester.harvesters.harvester1"));
    }

    @Test
    void getDownloaderEmpty() throws ConfigurationException {
        HarvesterConfigurationUtils harvesterConfigurationUtils = new HarvesterConfigurationUtils();
        assertThrows(ConfigurationException.class, (() -> harvesterConfigurationUtils.getDownloader("harvester.harvesters.harvester2")));
    }


}
