package com.smartshaped.chameleon.common.utils;

public class TableModelExample3 extends TableModel {

	String test1;
	String test2;

	@Override
	protected String choosePrimaryKey() {
		return "test1, test3";
	}
}
