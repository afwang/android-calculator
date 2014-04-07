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
		int operation = bun.getInt(r.getString(R.string.DB_OPERATION));
		
		switch(operation) {
		case CalculatorHistoryHelper.SAVE_TO_DB:
			saveData(bun.getParcelableArray(r.getString(R.string.ADD_TO_DB_KEY)));
			break;
		case CalculatorHistoryHelper.CLEAR_ALL:
			clearAll();
			break;
		default:
			Log.v("HistoryService.onHandleIntent()",
				"An illegal value was passed with Bundle!");
		}
	}
	
	private void saveData(Parcelable[] data) {
		CalculatorHistoryHelper dbHelper = new CalculatorHistoryHelper(this);
		SQLiteDatabase db = null;
		
		try {
			CalculatorHistoryHelper.dbLock.lockInterruptibly();
			db = dbHelper.getWritableDatabase();
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
		catch(InterruptedException e) {
			//Do not unlock the Lock, because we never acquired the lock
			//if an InterruptedException occurs
		}
		catch(ClassCastException e) {
			//Throw away remaining data. Something weird happened.
			if(db != null)
				db.close();
			CalculatorHistoryHelper.dbLock.unlock();
		}
		catch(SQLiteException e) {
			//Unable to open database. Discard data.
			CalculatorHistoryHelper.dbLock.unlock();
		}
		catch(Exception e) {
			//Something unexpected occur. Just discard remaining data
			//Try unlocking the lock:
			try {
				CalculatorHistoryHelper.dbLock.unlock();
			}
			catch(IllegalMonitorStateException nolock) {
				//We never had the lock in the first place! No worries about
				//anything then.
			}
		}
	}
	
	private void clearAll() {
		CalculatorHistoryHelper dbHelper = new CalculatorHistoryHelper(this);
		dbHelper.clearTable();
	}
}
