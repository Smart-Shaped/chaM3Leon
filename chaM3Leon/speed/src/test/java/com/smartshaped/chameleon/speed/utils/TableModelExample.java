package com.smartshaped.chameleon.speed.utils;

import com.smartshaped.chameleon.common.utils.TableModel;

public class TableModelExample extends TableModel {

  String test1;
  int test2;

  @Override
  protected String choosePrimaryKey() {
    return "";
  }
}
