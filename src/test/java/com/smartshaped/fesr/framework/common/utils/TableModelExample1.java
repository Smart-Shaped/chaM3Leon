package com.smartshaped.fesr.framework.common.utils;

public class TableModelExample1 extends TableModel {

    String test1;
    String test2;

    @Override
    protected String choosePrimaryKey() {
        return "test1";
    }
}
