package com.smartshaped.chameleon.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;

/** This class is the base class for all classes that needs to interact with Cassandra database. */
public abstract class TableModel {

  private static final Logger logger = LogManager.getLogger(TableModel.class);
  private static final String MAPPING_PREFIX = "typeMapping.";

  private String tableName;
  private String primaryKey;
  private boolean multipleKeys = false;
  private boolean generateUuid = false;

  private YAMLConfiguration config;

  /**
   * This method reads the type mapping configuration file.
   *
   * <p>If the file cannot be loaded, a {@link ConfigurationException} is thrown.
   *
   * @throws ConfigurationException if the file cannot be loaded
   */
  private void loadTypeMappingFile() throws ConfigurationException {
    String mainConfigFile = "typeMapping.yml";
    YAMLConfiguration ymlConfig = new YAMLConfiguration();
    FileHandler fileHandler = new FileHandler(ymlConfig);

    try {
      fileHandler.load(mainConfigFile);
      this.config = ymlConfig;
      logger.info("Type mapping file loaded successfully");
    } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
      throw new ConfigurationException("Unable to load types mapping file", e);
    }
  }

  /**
   * This method validates the current model by checking the following:
   *
   * <ul>
   *   <li>loads the type mapping configuration file
   *   <li>chooses the primary key field
   *   <li>gets the name of the Cassandra table
   *   <li>checks that the primary key field is correctly annotated
   * </ul>
   *
   * <p>If any of the above steps fails, a {@link ConfigurationException} or a {@link
   * CassandraException} is thrown.
   *
   * @throws ConfigurationException if any of the above steps fails
   * @throws CassandraException if any of the above steps fails
   */
  public void validateModel() throws CassandraException, ConfigurationException {

    loadTypeMappingFile();

    primaryKey = choosePrimaryKey();
    tableName = getTableName();

    Field[] fields = this.getClass().getDeclaredFields();

    checkPrimaryKey(fields);

    logger.debug("TableModel initialized");
  }

  /**
   * Generates a Cassandra query to create the table associated to the current model.
   *
   * <p>The query is generated based on the configuration provided by the current model.
   *
   * <p>If any error occurs during the generation of the query, a {@link ConfigurationException} is
   * thrown.
   *
   * @return a Cassandra query to create the table associated to the current model
   * @throws ConfigurationException if any error occurs during the generation of the query
   */
  public String getCreationQuery() throws ConfigurationException {

    logger.debug("Generating query to create table {}...", tableName);
    String fieldsSchema = getSchema();

    return "CREATE TABLE IF NOT EXISTS KEYSPACE." + tableName + " (" + fieldsSchema + ");";
  }

  /**
   * Checks if the primary key is valid.
   *
   * <p>If the primary key is empty, a UUID is generated as the primary key. If the primary key is
   * not empty, it checks if there are multiple keys and if the primary key is valid.
   *
   * @param fields the fields of the class
   * @throws CassandraException if the primary key is not valid
   */
  private void checkPrimaryKey(Field[] fields) throws CassandraException {

    logger.debug("Checking primary key...");

    primaryKey = choosePrimaryKey();

    if (primaryKey.trim().isEmpty()) {
      logger.warn("Primary key is empty, a UUID will be generated as the primary key");
      generateUuid = true;
    } else {
      String[] primaryKeys = primaryKey.replaceAll("\\s", "").split(",");

      if (primaryKeys.length > 1) {
        logger.debug("Multiple primary keys found");
        multipleKeys = true;
      }

      checkValidPrimaryKey(fields, primaryKeys);
    }
    logger.debug("Primary key checked");
  }

  /**
   * Checks if all the given primary keys are valid for the given class.
   *
   * <p>This method will check if all the given primary keys are valid for the given class. If any
   * of the primary keys are not valid, a {@link CassandraException} is thrown.
   *
   * @param fields the fields of the class
   * @param primaryKeys the primary keys to check
   * @throws CassandraException if any of the primary keys are not valid
   */
  private void checkValidPrimaryKey(Field[] fields, String[] primaryKeys)
      throws CassandraException {

    for (String x : primaryKeys) {
      logger.debug("Checking primary key {}...", x);
      checkValidPrimaryKey(fields, x);
    }
    logger.debug("All primary keys are valid");
  }

  /**
   * Checks if a given primary key is valid for the given class.
   *
   * <p>This method will check if the given primary key is valid for the given class. If the primary
   * key is not valid, a {@link CassandraException} is thrown.
   *
   * @param fields the fields of the class
   * @param x the primary key to check
   * @throws CassandraException if the primary key is not valid
   */
  private void checkValidPrimaryKey(Field[] fields, String x) throws CassandraException {

    List<String> fieldsList = new ArrayList<>();
    for (Field field : fields) {
      fieldsList.add(field.getName());
    }
    if (!fieldsList.contains(x)) {
      throw new CassandraException("Primary key " + x + " not found");
    }
    logger.debug("Primary key {} is valid", x);
  }

  /**
   * Builds a Cassandra schema based on the class fields.
   *
   * <p>This method will build a Cassandra schema based on the class fields. If the primary key is
   * empty, a UUID is generated as the primary key. If there are multiple keys, a compound primary
   * key is generated.
   *
   * <p>This method will throw a {@link ConfigurationException} if any error occurs during the
   * execution.
   *
   * @return the Cassandra schema as a string
   * @throws ConfigurationException if any error occurs during the execution
   */
  private String getSchema() throws ConfigurationException {

    StringBuilder fieldsSchema = new StringBuilder();

    Field field;
    String fieldName = "";
    String fieldType = "";

    if (generateUuid) {
      logger.debug("Adding UUID as primary key");
      fieldsSchema.append("id uuid PRIMARY KEY, ");
    }

    Field[] fields = this.getClass().getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      field = fields[i];

      fieldName = field.getName();

      fieldType = field.getType().getSimpleName();
      fieldType = convertFieldType(fieldType);
      fieldsSchema.append(fieldName).append(" ").append(fieldType);

      if (primaryKey.equals(fieldName)) {
        fieldsSchema.append(" PRIMARY KEY");
      }

      if (i < fields.length - 1) {
        fieldsSchema.append(", ");
      }
    }

    if (multipleKeys) {
      logger.debug("Adding compound primary key");
      fieldsSchema.append(", PRIMARY KEY (").append(primaryKey).append(")");
    }

    logger.debug("Generated schema: {}", fieldsSchema);
    return fieldsSchema.toString();
  }

  /**
   * Converts a Java type into a Cassandra type, using the mapping defined in the configuration
   * file.
   *
   * <p>The mapping is defined in the configuration file under the key "typeMapping." followed by
   * the Java type name.
   *
   * <p>If the mapping is not found, a {@link ConfigurationException} is thrown.
   *
   * @param javaType the Java type to convert
   * @return the corresponding Cassandra type
   * @throws ConfigurationException if the mapping is not found
   */
  private String convertFieldType(String javaType) throws ConfigurationException {

    logger.debug("Converting Java type {} to Cassandra type...", javaType);
    String cqlType = config.getString(MAPPING_PREFIX + javaType, "");

    if (cqlType.trim().isEmpty()) {
      throw new ConfigurationException("Missing mapping for " + javaType);
    }

    logger.debug("Converted Java type {} to Cassandra type: {}", javaType, cqlType);

    return cqlType;
  }

  /**
   * Returns the name of the table to be created in Cassandra.
   *
   * @return the name of the table
   */
  public String getTableName() {
    return this.getClass().getSimpleName().toLowerCase();
  }

  /**
   * Checks if a UUID should be generated as the primary key for the table.
   *
   * @return true if a UUID should be generated as the primary key, false otherwise
   */
  public boolean isGenerateUuid() {
    return generateUuid;
  }

  protected abstract String choosePrimaryKey();
}
