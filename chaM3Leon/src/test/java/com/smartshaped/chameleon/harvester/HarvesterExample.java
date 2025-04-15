package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.util.ArrayList;
import java.util.List;

public class HarvesterExample extends Harvester {

  public HarvesterExample() throws ConfigurationException {
    super();
  }

  @Override
  protected List<String> extractParams(Request req) {
    List<String> list = new ArrayList<>();
    list.add("param1");
    list.add("param2");
    return list;
  }
}
