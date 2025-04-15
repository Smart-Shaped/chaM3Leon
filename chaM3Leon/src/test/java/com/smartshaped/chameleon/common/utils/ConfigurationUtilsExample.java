package com.smartshaped.chameleon.common.utils;

import com.smartshaped.chameleon.common.exception.ConfigurationException;

public class ConfigurationUtilsExample extends ConfigurationUtils {

  protected ConfigurationUtilsExample() throws ConfigurationException {
    super();

    setConfRoot("test.");
  }
}
