package com.smartshaped.chameleon.common.utils;

public class TableModelExample2 extends TableModel {

  String test1;
  String test2;

  @Override
  protected String choosePrimaryKey() {
    return "test1, test2";
  }
}
