package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.ConfigurationUtils;
import com.smartshaped.chameleon.harvester.exception.HarvesterException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.harvester.saver.HarvesterSaver;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
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
public class HarvesterLayerTest {

    @Mock
    private HarvesterConfigurationUtils configurationUtils;
    @Mock
    private RequestHandler requestHandler;
    @Mock
    private SparkConf sparkConf;
    @Mock
    private SparkSession sparkSession;
    @Mock
    private SparkSession.Builder builder;

    private Request[] requestList = new Request[1];
    private List<Harvester> harvesterlist = new ArrayList<>();

    @Mock
    private Request request;
    @Mock
    private Harvester harvester;


    @BeforeEach
    void setUp() {
        requestList[0] = request;
        harvesterlist.add(harvester);
    }

    @Test
    void HarvesterlayerSuccess() throws ConfigurationException, CassandraException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<CassandraUtils> harvSaver = mockStatic(CassandraUtils.class);
             MockedStatic<SedonaContext> sedonaContext = mockStatic(SedonaContext.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            sedonaContext.when(() -> SedonaContext.builder()).thenReturn(builder);
            when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);
            when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
            when(builder.config(sparkConf)).thenReturn(builder);
            when(builder.getOrCreate()).thenReturn(sparkSession);

            assertDoesNotThrow(() -> new HarvesterLayer());
        }
    }

    @Test
    void HarvesterLayerFailure() throws ConfigurationException, CassandraException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<CassandraUtils> harvSaver = mockStatic(CassandraUtils.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);

            assertThrows(ConfigurationException.class, () -> new HarvesterLayer());
        }
    }


    @Test
    void startSuccess() throws ConfigurationException, CassandraException {
        try (MockedStatic<HarvesterConfigurationUtils> confUtils = mockStatic(HarvesterConfigurationUtils.class);
             MockedStatic<CassandraUtils> harvSaver = mockStatic(CassandraUtils.class);
             MockedStatic<SedonaContext> sedonaContext = mockStatic(SedonaContext.class)) {
            confUtils.when(() -> HarvesterConfigurationUtils.getHarvesterConf()).thenReturn(configurationUtils);
            sedonaContext.when(() -> SedonaContext.builder()).thenReturn(builder);
            when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);
            when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
            when(configurationUtils.getHarvesters()).thenReturn(harvesterlist);
            when(builder.config(sparkConf)).thenReturn(builder);
            when(builder.getOrCreate()).thenReturn(sparkSession);

            when(requestHandler.getRequest()).thenReturn(requestList);
            when(request.getHarvesterIds()).thenReturn("harvester1");
            when(harvester.getHarvesterId()).thenReturn("harvester.harvesters.harvester1");


            HarvesterLayer harvetserLayer = new HarvesterLayer();
            assertDoesNotThrow(() -> harvetserLayer.start());
        }
    }


}
