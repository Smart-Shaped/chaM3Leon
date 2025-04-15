package com.smartshaped.chameleon.common.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class IncludeLookupTest {

  @Test
  void testIncludeLookup() {
    assertDoesNotThrow(() -> new IncludeLookup("framework-config.yml"));
  }

  @Test
  void testLookup() {
    IncludeLookup includeLookup = mock(IncludeLookup.class, Mockito.CALLS_REAL_METHODS);
    assertDoesNotThrow(() -> includeLookup.lookup("test"));
  }
}
