package com.smartshaped.chameleon.harvester.request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.smartshaped.chameleon.common.utils.exception.CassandraException;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class RequestHandlerTest {

  @Mock private HarvesterConfigurationUtils configurationUtils;
  @Mock private CassandraUtils cassandraUtils;
  @Mock private TableModel tableModel;
  @Mock private ResultSet resultSet;

  private List<Row> rows = new ArrayList<Row>();
  @Mock private Row row;

  @Mock private Request request;

  private String state;

  @BeforeEach
  void setUp() throws ConfigurationException, CassandraException {
    when(configurationUtils.createTableModel(Request.class.getName())).thenReturn(tableModel);
    doNothing().when(cassandraUtils).validateTableModel(tableModel);

    rows.add(row);
  }

  @Test
  void getRequestSuccess() throws ConfigurationException, CassandraException {

    when(cassandraUtils.executeSelect("request", Optional.of("state = 'false'")))
        .thenReturn(resultSet);
    when(resultSet.all()).thenReturn(rows);
    when(row.getUuid(anyString())).thenReturn(UUID.randomUUID());
    when(row.getString(anyString())).thenReturn("test");

    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);
        try (MockedConstruction<RequestValidator> mockedConstruction =
            mockConstruction(
                RequestValidator.class,
                (mock, context) -> {
                  when(mock.isRequestValid(any(Request.class))).thenReturn(true);
                })) {
          RequestHandler requestHandler = new RequestHandler();
          assertDoesNotThrow(requestHandler::getRequest);
        }
      }
    }
  }

  @Test
  void getRequestNotValid() throws ConfigurationException, CassandraException {

    when(cassandraUtils.executeSelect("request", Optional.of("state = 'false'")))
        .thenReturn(resultSet);
    when(resultSet.all()).thenReturn(rows);
    when(row.getUuid(anyString())).thenReturn(UUID.randomUUID());
    when(row.getString(anyString())).thenReturn("test");

    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        RequestHandler requestHandler = new RequestHandler();
        assertDoesNotThrow(requestHandler::getRequest);
      }
    }
  }

  @Test
  void getRequestFailure() throws ConfigurationException, CassandraException {

    when(cassandraUtils.executeSelect("request", Optional.of("state = 'false'")))
        .thenThrow(CassandraException.class);

    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        RequestHandler requestHandler = new RequestHandler();
        assertDoesNotThrow(requestHandler::getRequest);
      }
    }
  }

  @Test
  void updateRequestStateSuccess() throws ConfigurationException, CassandraException {

    when(request.getId()).thenReturn(UUID.randomUUID());

    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        RequestHandler requestHandler = new RequestHandler();
        assertDoesNotThrow(() -> requestHandler.updateRequestState(request, state));
      }
    }
  }

  @Test
  void closeConnectionTest() throws ConfigurationException, CassandraException {
    try (MockedStatic<HarvesterConfigurationUtils> mockedStatic =
        mockStatic(HarvesterConfigurationUtils.class)) {
      mockedStatic
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      try (MockedStatic<CassandraUtils> mockedStaticCassandra = mockStatic(CassandraUtils.class)) {
        mockedStaticCassandra
            .when(() -> CassandraUtils.getCassandraUtils(configurationUtils))
            .thenReturn(cassandraUtils);

        RequestHandler requestHandler = new RequestHandler();
        assertDoesNotThrow(requestHandler::closeConnection);
      }
    }
  }
}
