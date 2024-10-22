package com.smartshaped.fesr.framework.common.utils;

import com.smartshaped.fesr.framework.common.exception.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationUtilsTest {

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
        assertDoesNotThrow(() -> configurationUtils.getSparkConf());
    }

    @Test
    void testGetCassandraKeyspaceName() {
        assertDoesNotThrow(() -> configurationUtils.getCassandraKeySpaceName());
    }

    @Test
    void testGetCassandraReplicationFactor() {
        assertDoesNotThrow(() -> configurationUtils.getCassandraReplicationFactor());
    }

    @Test
    void testGetCassandraNode() {
        assertDoesNotThrow(() -> configurationUtils.getCassandraNode());
    }

    @Test
    void testGetCassandraPort() {
        assertDoesNotThrow(() -> configurationUtils.getCassandraPort());
    }

    @Test
    void testGetCassandraDataCenter() {
        assertDoesNotThrow(() -> configurationUtils.getCassandraDataCenter());
    }

    @Test
    void testGetModelClassName() {
        assertDoesNotThrow(() -> configurationUtils.getModelClassName());
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
        assertThrows(ConfigurationException.class, () -> configurationUtils.createTableModel(
                ConfigurationUtils.class.getName()));
    }

    @Test
    void testCreateTableModelSuccess() {
        assertDoesNotThrow(() -> configurationUtils.createTableModel(TableModelExample.class.getName()));
    }
}