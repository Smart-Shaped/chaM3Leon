package com.smartshaped.chameleon.common.utils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.Trigger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to interact with Cassandra database and execute queries.
 *
 * <p>This class provides methods to create a Cassandra session based on the configuration provided
 * by {@link ConfigurationUtils}.
 *
 * <p>This class is a Singleton and can be obtained with the method {@link
 * #getCassandraUtils(ConfigurationUtils)}.
 *
 * <p>This class is thread safe.
 */
public class CassandraUtils {

  private static final Logger logger = LogManager.getLogger(CassandraUtils.class);
  private CqlSession session;
  private final ConfigurationUtils configurationUtils;
  private static CassandraUtils cassandraUtils;
  private final String keyspace;

  /**
   * Create a new instance of CassandraUtils.
   *
   * <p>This class is a Singleton and can be obtained with the method {@link
   * #getCassandraUtils(ConfigurationUtils)}.
   *
   * <p>This class is thread safe.
   *
   * @param configurationUtils the configuration utils to be used to create the Cassandra session
   * @throws ConfigurationException if any error occurs during the creation of the Cassandra session
   * @throws CassandraException if any error occurs during the creation of the Cassandra session
   */
  CassandraUtils(ConfigurationUtils configurationUtils)
      throws ConfigurationException, CassandraException {
    this.configurationUtils = configurationUtils;
    this.session = createCassandraSession();

    keyspace = configurationUtils.getCassandraKeySpaceName();

    if (keyspace.trim().isEmpty()) {
      throw new ConfigurationException("Keyspace not setted");
    }

    logger.debug("Keyspace: {}", keyspace);

    if (!keyspaceExists(keyspace)) {
      logger.warn("Keyspace {} does not exist", keyspace);

      int replicationFactor = configurationUtils.getCassandraReplicationFactor();

      logger.debug("Replication factor: {}", replicationFactor);

      try {
        createKeyspace(replicationFactor);
        logger.info("Keyspace {} created", keyspace);
      } catch (Exception e) {
        throw new CassandraException("Error while creating the keyspace", e);
      }
    }
  }

  /**
   * Check if a keyspace exists in Cassandra.
   *
   * @param keyspace the name of the keyspace to check
   * @return true if the keyspace exists, false otherwise
   * @throws CassandraException if any error occurs during the execution
   */
  private boolean keyspaceExists(String keyspace) throws CassandraException {
    String query = "SELECT keyspace_name FROM system_schema.keyspaces WHERE keyspace_name = ?";
    ResultSet resultSet = executeQuery(query, keyspace);
    com.datastax.oss.driver.api.core.cql.Row row = resultSet.one();

    return row != null;
  }

  /**
   * Create a Cassandra keyspace with the given replication factor.
   *
   * @param replicationFactor the replication factor for the keyspace
   * @throws CassandraException if any error occurs during the execution
   */
  private void createKeyspace(int replicationFactor) throws CassandraException {

    String query =
        "CREATE KEYSPACE IF NOT EXISTS "
            + keyspace
            + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': "
            + replicationFactor
            + "};";
    executeQuery(query);
  }

  /**
   * Get a singleton instance of CassandraUtils.
   *
   * <p>This method will create a new instance of CassandraUtils if it does not already exist.
   * Otherwise, it will return the existing instance.
   *
   * @param configurationUtils the configuration utils to use
   * @return a singleton instance of CassandraUtils
   * @throws ConfigurationException if any error occurs while reading the configuration
   * @throws CassandraException if any error occurs while connecting to Cassandra
   */
  public static CassandraUtils getCassandraUtils(ConfigurationUtils configurationUtils)
      throws ConfigurationException, CassandraException {
    if (cassandraUtils == null) {
      logger.info("Creating CassandraUtils instance");

      cassandraUtils = new CassandraUtils(configurationUtils);
    }

    return cassandraUtils;
  }

  /**
   * Create a Cassandra session based on the configuration provided.
   *
   * <p>This method will read the configuration and create a Cassandra session. If the configuration
   * is invalid, a {@link ConfigurationException} will be thrown. If any error occurs during the
   * creation of the session, a {@link CassandraException} will be thrown.
   *
   * <p>The configuration is expected to contain the following:
   *
   * <ul>
   *   <li>{@code cassandra.node}: the Cassandra node to connect to
   *   <li>{@code cassandra.port}: the Cassandra port to connect to
   *   <li>{@code cassandra.datacenter}: the Cassandra data center to connect to
   * </ul>
   *
   * @return a Cassandra session based on the configuration
   * @throws ConfigurationException if the configuration is invalid
   * @throws CassandraException if any error occurs during the creation of the session
   */
  private CqlSession createCassandraSession() throws ConfigurationException, CassandraException {
    logger.info("Creating Cassandra session from configuration");

    String node = configurationUtils.getCassandraNode();
    int port = configurationUtils.getCassandraPort();
    String dataCenter = configurationUtils.getCassandraDataCenter();

    if (node == null || node.trim().isEmpty()) {
      throw new ConfigurationException("Missing or empty Cassandra node configuration");
    }

    logger.debug("Node: {}", node);

    if (dataCenter == null || dataCenter.trim().isEmpty()) {
      throw new ConfigurationException("Missing or empty Cassandra data center configuration");
    }

    logger.debug("Data center: {}", dataCenter);

    try {
      CqlSessionBuilder builder =
          CqlSession.builder()
              .addContactPoint(new InetSocketAddress(node, port))
              .withLocalDatacenter(dataCenter);

      CqlSession buildSession = builder.build();
      logger.debug(
          "Cassandra session created successfully. Connected to node: {}, port: {}, data center: {}",
          node,
          port,
          dataCenter);
      return buildSession;
    } catch (Exception e) {
      throw new CassandraException("Failed to create Cassandra session", e);
    }
  }

  /**
   * Executes a Cassandra query and returns the result set.
   *
   * <p>This method will prepare the query, bind the given parameters and execute it. If any error
   * occurs during the execution, it will be wrapped in a {@link CassandraException}.
   *
   * @param query the query to be executed
   * @param params the parameters to be bound to the query
   * @return the result set of the query
   * @throws CassandraException if any error occurs during the execution
   */
  private ResultSet executeQuery(String query, Object... params) throws CassandraException {

    ResultSet resultSet;
    try {
      PreparedStatement statement = session.prepare(query);
      BoundStatement bound = statement.bind(params);
      resultSet = session.execute(bound);
    } catch (Exception e) {
      throw new CassandraException("Error while executing the query", e);
    }

    return resultSet;
  }

  /**
   * Executes a SELECT query on a Cassandra table.
   *
   * <p>If the {@code whereClause} is present, it will be appended to the query.
   *
   * <p>The method will return a {@link ResultSet} containing the result of the query.
   *
   * <p>If any error occurs during the execution, a {@link CassandraException} will be thrown.
   *
   * @param tableName the name of the table to select from
   * @param whereClause an optional WHERE clause for the query
   * @return a {@link ResultSet} containing the result of the query
   * @throws CassandraException if any error occurs during the execution
   */
  public ResultSet executeSelect(String tableName, Optional<String> whereClause)
      throws CassandraException {

    logger.debug("Executing SELECT query on table {}", tableName);

    StringBuilder queryBuilder =
        new StringBuilder("SELECT * FROM ").append(keyspace).append(".").append(tableName);

    whereClause.ifPresent(
        clause -> queryBuilder.append(" WHERE ").append(clause).append(" ALLOW FILTERING"));
    SimpleStatement statement = SimpleStatement.builder(queryBuilder.toString()).build();

    try {
      return session.execute(statement);
    } catch (Exception e) {
      throw new CassandraException("Error while executing the query", e);
    }
  }

  /**
   * Executes an INSERT query on a Cassandra table.
   *
   * <p>This method will generate the query string based on the provided column-value map.
   *
   * <p>The method will return a {@link ResultSet} containing the result of the query.
   *
   * <p>If any error occurs during the execution, a {@link CassandraException} will be thrown.
   *
   * @param tableName the name of the table to insert into
   * @param columnValueMap a map of column names to values to be inserted
   * @return a {@link ResultSet} containing the result of the query
   * @throws CassandraException if any error occurs during the execution
   */
  public ResultSet executeInsert(String tableName, Map<String, Object> columnValueMap)
      throws CassandraException {

    logger.debug("Executing INSERT query on table {}", tableName);

    StringBuilder columns = new StringBuilder();
    StringBuilder valuesPlaceholders = new StringBuilder();

    for (String column : columnValueMap.keySet()) {
      if (columns.length() > 0) {
        columns.append(", ");
        valuesPlaceholders.append(", ");
      }
      columns.append(column);
      valuesPlaceholders.append("?");
    }

    String query =
        "INSERT INTO "
            + keyspace
            + "."
            + tableName
            + " ("
            + columns.toString()
            + ") VALUES ("
            + valuesPlaceholders.toString()
            + ")";

    return executeQuery(query, columnValueMap.values().toArray());
  }

  /**
   * Executes an UPDATE query on a Cassandra table.
   *
   * <p>The method will generate the query string based on the provided column-value map.
   *
   * <p>The method will return a {@link ResultSet} containing the result of the query.
   *
   * <p>If any error occurs during the execution, a {@link CassandraException} will be thrown.
   *
   * @param tableName the name of the table to update
   * @param values a map of column names to values to be updated
   * @param whereClause an optional WHERE clause for the query
   * @return a {@link ResultSet} containing the result of the query
   * @throws CassandraException if any error occurs during the execution
   */
  public ResultSet executeUpdate(
      String tableName, Map<String, Object> values, Optional<String> whereClause)
      throws CassandraException {

    logger.debug("Executing UPDATE query on table {}", tableName);

    StringBuilder queryBuilder =
        new StringBuilder("UPDATE ").append(keyspace).append(".").append(tableName).append(" SET ");
    List<Object> params = new ArrayList<>();

    values.forEach(
        (field, value) -> {
          queryBuilder.append(field).append(" = ? ,");
          params.add(value);
        });

    queryBuilder.deleteCharAt(queryBuilder.length() - 1);
    whereClause.ifPresent(clause -> queryBuilder.append(" WHERE ").append(clause));

    return executeQuery(queryBuilder.toString(), params.toArray());
  }

  /**
   * Validates a TableModel by checking if the table exists in Cassandra. If the table does not
   * exist, it creates the table using the given TableModel.
   *
   * <p>This method will throw a {@link ConfigurationException} if the model is not valid and a
   * {@link CassandraException} if any error occurs during the execution.
   *
   * @param tableModel the TableModel to be validated
   * @throws CassandraException if any error occurs during the execution
   */
  public void validateTableModel(TableModel tableModel) throws CassandraException {

    try {
      tableModel.validateModel();
    } catch (ConfigurationException e) {
      throw new CassandraException("Error while validating table model", e);
    }

    if (!tableExists(tableModel.getTableName())) {
      logger.warn("Table {} does not exist in keyspace {}", tableModel.getTableName(), keyspace);

      createTable(tableModel);
    }
  }

  /**
   * Creates a Cassandra table from the given TableModel.
   *
   * <p>This method will validate the TableModel and then create the table using the Cassandra query
   * returned by the TableModel.
   *
   * <p>This method will throw a {@link ConfigurationException} if the model is not valid and a
   * {@link CassandraException} if any error occurs during the execution.
   *
   * @param tableModel the TableModel from which to create the table
   * @throws CassandraException if any error occurs during the execution
   */
  private void createTable(TableModel tableModel) throws CassandraException {

    try {
      logger.debug("Creating table {}", tableModel.getTableName());
      String query = tableModel.getCreationQuery().replace("KEYSPACE", keyspace);
      executeQuery(query);
      logger.info("Table {} created successfully", tableModel.getTableName());
    } catch (Exception e) {
      throw new CassandraException("Failed to create table", e);
    }
  }

  /**
   * Checks if a table exists in the Cassandra keyspace.
   *
   * @param table the name of the table to check
   * @return true if the table exists, false otherwise
   * @throws CassandraException if any error occurs during the execution
   */
  private boolean tableExists(String table) throws CassandraException {
    String query =
        "SELECT table_name FROM system_schema.tables WHERE keyspace_name = ? AND table_name = ?";
    ResultSet resultSet = executeQuery(query, keyspace, table);
    com.datastax.oss.driver.api.core.cql.Row row = resultSet.one();

    return row != null;
  }

  /**
   * Saves a DataFrame to a Cassandra table.
   *
   * <p>This method will check if the TableModel specifies a primary key. If not, it will generate a
   * UUID as a primary key and add it to the DataFrame. Then, it will save the DataFrame to the
   * Cassandra table using the {@link SaveMode#Append} mode.
   *
   * <p>This method will throw a {@link CassandraException} if any error occurs during the
   * execution.
   *
   * @param df the DataFrame to be saved
   * @param tableModel the TableModel from which to get the table name and primary key information
   * @throws CassandraException if any error occurs during the execution
   */
  public void saveDF(Dataset<Row> df, TableModel tableModel) throws CassandraException {

    String table = tableModel.getTableName();
    boolean generateUuid = tableModel.isGenerateUuid();

    if (generateUuid) {
      logger.debug("Adding UUID field as primary key");
      df = df.withColumn("id", functions.expr("uuid()"));
    }

    try {
      df.write()
          .format("org.apache.spark.sql.cassandra")
          .options(Map.of("keyspace", keyspace, "table", table))
          .mode(SaveMode.Append)
          .save();
      logger.info("DataFrame saved to Cassandra table {} successfully", table);
    } catch (Exception e) {
      throw new CassandraException("Failed to save DataFrame to Cassandra", e);
    }
  }

  /**
   * Saves a DataFrame to a Cassandra table in streaming mode.
   *
   * <p>This method will check if the TableModel specifies a primary key. If not, it will generate a
   * UUID as a primary key and add it to the DataFrame. Then, it will save the DataFrame to the
   * Cassandra table using the {@link SaveMode#Append} mode and {@link Trigger#ProcessingTime}
   * trigger.
   *
   * <p>This method will throw a {@link CassandraException} if any error occurs during the
   * execution.
   *
   * @param df the DataFrame to be saved
   * @param tableModel the TableModel from which to get the table name and primary key information
   * @param intervalMs the interval in milliseconds for triggering the write operation
   * @throws CassandraException if any error occurs during the execution
   * @throws ConfigurationException if the Cassandra configuration cannot be loaded
   */
  public void saveStreamDF(Dataset<Row> df, TableModel tableModel, Long intervalMs)
      throws CassandraException, ConfigurationException {

    String table = tableModel.getTableName();
    boolean generateUuid = tableModel.isGenerateUuid();
    String checkpoint = configurationUtils.getCassandraCheckpoint();

    if (generateUuid) {
      logger.debug("Adding UUID field as primary key");
      df = df.withColumn("id", functions.expr("uuid()"));
    }

    df.printSchema();

    try {
      df.writeStream()
          .format("org.apache.spark.sql.cassandra")
          .options(Map.of("keyspace", keyspace, "table", table))
          .option("checkpointLocation", checkpoint)
          .outputMode(OutputMode.Append())
          .trigger(Trigger.ProcessingTime(intervalMs))
          .start();
      logger.info("DataFrame saved to Cassandra table {} in streaming mode successfully", table);
    } catch (Exception e) {
      throw new CassandraException("Failed to save DataFrame to Cassandra", e);
    }
  }

  /**
   * Closes the Cassandra session.
   *
   * <p>This method is idempotent: if the session is already closed, it does nothing.
   */
  public void close() {
    if (session != null) {
      session.close();
      logger.info("Cassandra session closed successfully");
    }
  }
}
