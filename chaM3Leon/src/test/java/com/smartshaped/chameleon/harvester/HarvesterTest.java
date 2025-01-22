package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.exception.HarvesterException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.harvester.saver.HarvesterSaver;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import org.apache.spark.sql.Dataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HarvesterTest {

    @Mock
    private Request request;
    @Mock
    private Dataset dataset;
    @Mock
    private HarvesterConfigurationUtils configurationUtils;
    @Mock
    private RequestHandler requestHandler;

    private List<Harvester> harvestersList = new ArrayList<>();

    private List<String> paramList = new ArrayList<>();

    @Mock
    private Harvester harvester;
    @Mock
    private DownloaderExample downloaderExample;
    @Mock
    private TransformerExample transformerExample;
    @Mock
    private PreprocessorExample preprocessorExample;

    @BeforeEach
    void setup() {
        harvestersList.add(harvester);
        paramList.add("param1");
        paramList.add("param2");
    }

    @Test
    void executeSuccess() throws ConfigurationException, CassandraException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
            when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
            HarvesterExample harvester = new HarvesterExample();
            assertDoesNotThrow(() -> harvester.execute(request));
        }
    }

    @Test
    void downloadAndTransformSuccess() throws ConfigurationException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
            when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
            when(configurationUtils.getTransformer(anyString())).thenReturn(transformerExample);
            HarvesterExample harvester = new HarvesterExample();
            assertDoesNotThrow(() -> harvester.downloadAndTransform(paramList, request));
        }
    }

    @Test
    void downloadAndTransformNull() throws ConfigurationException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
            when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
            when(configurationUtils.getTransformer(anyString())).thenReturn(null);
            HarvesterExample harvester = new HarvesterExample();
            assertDoesNotThrow(() -> harvester.downloadAndTransform(paramList, request));
        }
    }

    @Test
    void downloadAndTransformFailure() throws ConfigurationException, DownloaderException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
            when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
            when(configurationUtils.getTransformer(anyString())).thenReturn(null);
            when(downloaderExample.download(paramList, request)).thenThrow(ConfigurationException.class);
            HarvesterExample harvester = new HarvesterExample();
            assertThrows(HarvesterException.class, () -> harvester.downloadAndTransform(paramList, request));
        }
    }

    @Test
    void processSuccess() throws ConfigurationException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
            when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
            when(configurationUtils.getTransformer(anyString())).thenReturn(transformerExample);
            when(configurationUtils.getPreprocessor(anyString())).thenReturn(preprocessorExample);

            HarvesterExample harvester = new HarvesterExample();
            assertDoesNotThrow(() -> harvester.process(dataset));
        }
    }

    @Test
    void processNull() throws ConfigurationException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<HarvesterSaver> harvSaver = mockStatic(HarvesterSaver.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getHarvesterId(anyString())).thenReturn("harvester1");
            when(configurationUtils.getDownloader(anyString())).thenReturn(downloaderExample);
            when(configurationUtils.getTransformer(anyString())).thenReturn(null);

            HarvesterExample harvester = new HarvesterExample();
            assertDoesNotThrow(() -> harvester.process(dataset));
        }
    }

    @Test
    void getNameSuccess() throws ConfigurationException {
        HarvesterExample harvester = new HarvesterExample();
        assertDoesNotThrow(() -> harvester.getName());
    }

}
