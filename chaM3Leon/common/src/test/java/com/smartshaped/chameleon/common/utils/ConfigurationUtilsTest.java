package com.smartshaped.chameleon.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.smartshaped.chameleon.common.exception.ConfigurationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationUtilsTest {

  private ConfigurationUtilsExample configurationUtils;

  @BeforeEach
  void setUp() throws ConfigurationException {
    configurationUtils = new ConfigurationUtilsExample();
  }

  @Test
  void testConstructorSuccess() {
    assertDoesNotThrow(ConfigurationUtilsExample::new);
  }

  @Test
  void testGetSparkConf() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getSparkConf());
  }

  @Test
  void testGetCassandraKeyspaceName() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getCassandraKeySpaceName());
  }

  @Test
  void testGetCassandraReplicationFactor() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getCassandraReplicationFactor());
  }

  @Test
  void testGetCassandraNode() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getCassandraNode());
  }

  @Test
  void testGetCassandraPort() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getCassandraPort());
  }

  @Test
  void testGetCassandraDataCenter() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getCassandraDataCenter());
  }

  @Test
  void testGetCassandraCheckpoint() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getCassandraCheckpoint());
  }

  @Test
  void testGetModelClassName() {
    Assertions.assertDoesNotThrow(() -> configurationUtils.getModelClassName());
  }

  @Test
  void testCreateTableModelMissingClassName() {
    assertThrows(ConfigurationException.class, () -> configurationUtils.createTableModel(""));
  }

  @Test
  void testCreateTableModelGenericException() {
    assertThrows(ConfigurationException.class, () -> configurationUtils.createTableModel("test"));
  }

  @Test
  void testCreateTableModelNoValidBinding() {
    assertThrows(
        ConfigurationException.class,
        () -> configurationUtils.createTableModel(ConfigurationUtils.class.getName()));
  }

  @Test
  void testCreateTableModelSuccess() {
    Assertions.assertDoesNotThrow(
        () -> configurationUtils.createTableModel(TableModelExample.class.getName()));
  }
}
