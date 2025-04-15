package com.smartshaped.chameleon.harvester;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class HarvesterLayerTest {

  @Mock private HarvesterConfigurationUtils configurationUtils;
  @Mock private RequestHandler requestHandler;
  @Mock private SparkConf sparkConf;
  @Mock private SparkSession sparkSession;
  @Mock private SparkSession.Builder builder;

  private Request[] requestList = new Request[1];
  private List<Harvester> harvesterlist = new ArrayList<>();

  @Mock private Request request;
  @Mock private Harvester harvester;

  @BeforeEach
  void setUp() {
    requestList[0] = request;
    harvesterlist.add(harvester);
  }

  @Test
  void HarvesterlayerSuccess() throws ConfigurationException, CassandraException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<CassandraUtils> harvSaver = mockStatic(CassandraUtils.class);
        MockedStatic<SedonaContext> sedonaContext = mockStatic(SedonaContext.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      sedonaContext.when(SedonaContext::builder).thenReturn(builder);
      when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);
      when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
      when(builder.config(sparkConf)).thenReturn(builder);
      when(builder.getOrCreate()).thenReturn(sparkSession);

      assertDoesNotThrow(HarvesterLayer::new);
    }
  }

  @Test
  void HarvesterLayerFailure() throws ConfigurationException, CassandraException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<CassandraUtils> harvSaver = mockStatic(CassandraUtils.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);

      assertThrows(ConfigurationException.class, HarvesterLayer::new);
    }
  }

  @Test
  void startSuccess() throws ConfigurationException, CassandraException {
    try (MockedStatic<HarvesterConfigurationUtils> confUtils =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedStatic<CassandraUtils> harvSaver = mockStatic(CassandraUtils.class);
        MockedStatic<SedonaContext> sedonaContext = mockStatic(SedonaContext.class)) {
      confUtils.when(HarvesterConfigurationUtils::getHarvesterConf).thenReturn(configurationUtils);
      sedonaContext.when(SedonaContext::builder).thenReturn(builder);
      when(configurationUtils.getRequestHandler()).thenReturn(requestHandler);
      when(configurationUtils.getSparkConf()).thenReturn(sparkConf);
      when(configurationUtils.getHarvesters()).thenReturn(harvesterlist);
      when(builder.config(sparkConf)).thenReturn(builder);
      when(builder.getOrCreate()).thenReturn(sparkSession);
      sedonaContext.when(() -> SedonaContext.create(sparkSession)).thenReturn(sparkSession);

      when(requestHandler.getRequest()).thenReturn(requestList);
      when(request.getHarvesterIds()).thenReturn("harvester1");
      when(harvester.getHarvesterId()).thenReturn("harvester.harvesters.harvester1");

      HarvesterLayer harvetserLayer = new HarvesterLayer();
      assertDoesNotThrow(harvetserLayer::start);
    }
  }
}
