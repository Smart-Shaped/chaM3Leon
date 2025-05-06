package com.smartshaped.chameleon.common.utils;

import org.apache.commons.configuration2.interpol.Lookup;

import java.io.File;

/** Custom lookup for the "include" keyword in the configuration files. */
public class IncludeLookup implements Lookup {

  private final String basePath;

  IncludeLookup(String basePath) {
    this.basePath = basePath;
  }

  /**
   * Looks up the path of the file with the given key relative to the given base path.
   *
   * @param key the name of the file to look up
   * @return the path of the file with the given key
   */
  @Override
  public String lookup(String key) {
    return new File(basePath, key).getPath();
  }
}
