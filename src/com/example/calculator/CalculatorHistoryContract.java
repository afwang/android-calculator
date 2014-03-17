package com.example.calculator;

import android.provider.BaseColumns;

public final class CalculatorHistoryContract {
	public CalculatorHistoryContract() {}
	
	public static abstract class CalculatorHistory implements BaseColumns {
		public static final String TABLE_NAME = "history";
		public static final String COLUMN_NAME_OPER1 = "operand1";
		public static final String COLUMN_NAME_OPERATION = "operation";
		public static final String COLUMN_NAME_OPER2 = "operand2";
		public static final String COLUMN_NAME_RESULT = "result";
	}

}
