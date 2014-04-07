/**
 * 
 */
package com.example.calculator;

import com.example.calculator.CalculatorHistoryContract.CalculatorHistory;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/**
 * @author afwang
 *
 */
public class HistoryService extends IntentService {

	public HistoryService() {
		super("SaveHistoryService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Resources r = getResources();
		Bundle bun = intent.getExtras();
		int operation = bun.getInt(r.getString(R.string.clear_history));
		
		switch(operation) {
		case CalculatorHistoryHelper.SAVE_TO_DB:
			saveData(bun.getParcelableArray(r.getString(R.string.ADD_TO_DB_KEY)));
			break;
		case CalculatorHistoryHelper.CLEAR_ALL:
			clearAll();
			break;
		//default case is do nothing.
		}
	}
	
	private void saveData(Parcelable[] data) {
		CalculatorHistoryHelper dbHelper = new CalculatorHistoryHelper(this);
		
		try {
			CalculatorHistoryHelper.dbLock.lockInterruptibly();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			CalcOperation co;
			
			for(int i = 0; i < data.length && data[i] != null; i++) {
				co = (CalcOperation)data[i];
				values.put(CalculatorHistory.COLUMN_NAME_OPER1, Double.valueOf(co.operand1));
				values.put(CalculatorHistory.COLUMN_NAME_OPERATION, Character.toString(co.operation));
				values.put(CalculatorHistory.COLUMN_NAME_OPER2, Double.valueOf(co.operand2));
				values.put(CalculatorHistory.COLUMN_NAME_RESULT, Double.valueOf(co.result));
				db.insert(CalculatorHistory.TABLE_NAME, null, values);
			}
			db.close();
			CalculatorHistoryHelper.dbLock.unlock();
		}
		catch(ClassCastException e) {
		}
		catch(SQLiteException e) {
			//Unable to open database. Discard data.
		}
		catch(Exception e) {
			//Something unexpected occur. Just discard remaining data
		}
		finally {
			CalculatorHistoryHelper.dbLock.unlock();
		}
	}
	
	private void clearAll() {
		CalculatorHistoryHelper dbHelper = new CalculatorHistoryHelper(this);
		dbHelper.clearTable();
	}
}
