package com.smartshaped.chameleon.harvester.request;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.common.utils.CassandraUtils;
import com.smartshaped.chameleon.common.utils.TableModel;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Class for handling requests: retrieving, updating and validating them. */
public class RequestHandler {

  private static final Logger logger = LogManager.getLogger(RequestHandler.class);

  private CassandraUtils cassandraUtils;
  private HarvesterConfigurationUtils configurationUtils;
  private TableModel requestModel;

  /**
   * Class for handling requests: retrieving, updating and validating them.
   */
  public RequestHandler() throws ConfigurationException, CassandraException {
    configurationUtils = HarvesterConfigurationUtils.getHarvesterConf();
    logger.info("Harvester configurations loaded correctly");
    cassandraUtils = CassandraUtils.getCassandraUtils(configurationUtils);
    logger.info("Cassandra utils loaded correctly");
    requestModel = configurationUtils.createTableModel(Request.class.getName());
    logger.info("Table from model created correctly");
    cassandraUtils.validateTableModel(requestModel);
    logger.info("Table model validated correctly");
  }

  /**
   * Retrieves all the requests with state set to 'false'.
   *
   * <p>The method executes a select query on the "request" table and retrieves all the rows with
   * state set to 'false'. It then loops through the rows and populates a list of {@link Request}
   * objects. The objects are populated with the values from the row and then validated with a
   * {@link RequestValidator}. If the request is not valid, it is discarded. The method then returns
   * an array of valid requests.
   *
   * @return an array of valid requests
   */
  public Request[] getRequest() {
    logger.info("Retrieving requests not already processed");

    try {
      ResultSet resultSet = cassandraUtils.executeSelect("request", Optional.of("state = 'false'"));

      List<Row> rows = resultSet.all();
      logger.debug("Number of rows retrieved: {}", rows.size());

      List<Request> requests = new ArrayList<>();

      for (Row row : rows) {
        logger.debug("Populating request");
        Request request = new Request();

        request.setId(row.getUuid("id"));
        request.setContent(row.getString("content"));
        request.setHarvesterIds(row.getString("harvesterids"));
        request.setState(row.getString("state"));

        logger.debug("Request populated: {}", request);

        RequestValidator validator = new RequestValidator();

        if (!validator.isRequestValid(request)) {
          logger.warn("Invalid request: {}", request);
          continue;
        }
        requests.add(request);
      }
      return requests.toArray(new Request[0]);

    } catch (Exception e) {
      logger.warn("Error retrieving requests with false state", e);
      return new Request[0];
    }
  }

  /**
   * Updates the state of a given request in the database.
   *
   * <p>This method constructs an update query to change the state of the request identified by its
   * UUID in the database. The new state is provided as a parameter.
   *
   * @param request The request whose state is to be updated.
   * @param state The new state of the request.
   * @throws CassandraException If an error occurs during the update operation.
   */
  public void updateRequestState(Request request, String state) throws CassandraException {
    Map<String, Object> values = new HashMap<>();
    values.put("state", state);
    UUID uuid = UUID.fromString(String.valueOf(request.getId()));

    String whereClause = "id = " + uuid;

    cassandraUtils.executeUpdate(requestModel.getTableName(), values, Optional.of(whereClause));
  }

  /**
   * Closes the connection to the Cassandra database.
   *
   * <p>This method will close the underlying connection to the Cassandra database. The method is
   * idempotent, so it is safe to call it multiple times.
   */
  public void closeConnection() {
    logger.debug("Closing Cassandra connection");
    cassandraUtils.close();
  }
}
