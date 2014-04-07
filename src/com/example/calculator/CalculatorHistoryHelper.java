package com.example.calculator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.calculator.CalculatorHistoryContract.CalculatorHistory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalculatorHistoryHelper extends SQLiteOpenHelper {
	public static final Lock dbLock = new ReentrantLock(); 
	
	//Constants are used to determine what sort of operation is performed
	public static final int SAVE_TO_DB = 1;
	public static final int CLEAR_ALL = 2;
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Calculator.db";
	private static final String CREATE_TABLE =
		"CREATE TABLE " + CalculatorHistory.TABLE_NAME + " ("
		+ CalculatorHistory._ID + " INTEGER PRIMARY KEY,"
		+ CalculatorHistory.COLUMN_NAME_OPER1 + " REAL,"
		+ CalculatorHistory.COLUMN_NAME_OPERATION + " TEXT,"
		+ CalculatorHistory.COLUMN_NAME_OPER2 + " REAL,"
		+ CalculatorHistory.COLUMN_NAME_RESULT + " REAL)";
	private static final String DEL_TABLE =
		"DROP TABLE IF EXISTS " + CalculatorHistory.TABLE_NAME;
	
	public CalculatorHistoryHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(DEL_TABLE);
		onCreate(db);
	}
	
	public void clearTable() {
		SQLiteDatabase db = null;
		try {
			dbLock.lockInterruptibly();
			db = getWritableDatabase();
			db.execSQL(DEL_TABLE);
			onCreate(db);
		}
		catch(Exception e) {
		}
		finally {
			if(db != null)
				db.close();
			dbLock.unlock();
		}
	}
}
