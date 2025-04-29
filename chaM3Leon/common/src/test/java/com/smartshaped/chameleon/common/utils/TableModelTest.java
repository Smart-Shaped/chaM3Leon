package com.smartshaped.chameleon.common.utils;

import org.junit.jupiter.api.Test;

import com.smartshaped.chameleon.common.utils.exception.CassandraException;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableModelTest {

  TableModel tableModel;

  @Test
  void testNoPK() throws CassandraException, ConfigurationException {

    tableModel = new TableModelExample();
    tableModel.validateModel();
    tableModel.isGenerateUuid();

    assertDoesNotThrow(() -> tableModel.getCreationQuery());
  }

  @Test
  void testSinglePK() throws CassandraException, ConfigurationException {

    tableModel = new TableModelExample1();
    tableModel.validateModel();

    assertDoesNotThrow(() -> tableModel.getCreationQuery());
  }

  @Test
  void testMultiplePK() throws CassandraException, ConfigurationException {

    tableModel = new TableModelExample2();
    tableModel.validateModel();

    assertDoesNotThrow(() -> tableModel.getCreationQuery());
  }

  @Test
  void testWrongPK() {

    tableModel = new TableModelExample3();

    assertThrows(CassandraException.class, () -> tableModel.validateModel());
  }

  @Test
  void testUnmappedType() throws ConfigurationException, CassandraException {

    tableModel = new TableModelExample4();
    tableModel.validateModel();

    assertThrows(ConfigurationException.class, () -> tableModel.getCreationQuery());
  }
}
