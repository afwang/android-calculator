package com.example.calculator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.calculator.CalculatorHistoryContract.CalculatorHistory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {
	private enum theOperation {ADD, SUBTRACT, MULT, DIV};
	
	private theOperation op;
	private final int CALC_NOTI_ID = 1;
	
	private CalculatorHistoryHelper dbHelper;
	
	private ArrayList<String> history;
	private ArrayAdapter<String> historyListAdapter;
	private LinkedList<CalcOperation> newoperations;
	
	private AsyncTask<CalculatorHistoryHelper, Void, ArrayList<String>> loadHistory;
	
	private class LoadHistoryTask extends AsyncTask<CalculatorHistoryHelper, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(CalculatorHistoryHelper... dbHelper) {
			String[] columns = {
					CalculatorHistory.COLUMN_NAME_OPER1,
					CalculatorHistory.COLUMN_NAME_OPERATION,
					CalculatorHistory.COLUMN_NAME_OPER2,
					CalculatorHistory.COLUMN_NAME_RESULT };
			Cursor c;
			StringBuilder sb = new StringBuilder();
			ArrayList<String> loadedHistory = new ArrayList<String>();
			int column;
			
			try {
				CalculatorHistoryHelper.dbLock.lockInterruptibly();
				SQLiteDatabase db = dbHelper[0].getReadableDatabase();
				c = db.query(CalculatorHistory.TABLE_NAME, columns, null, null, null, null, null);
				while(c.moveToNext() && !isCancelled()) {
					column = c.getColumnIndex(CalculatorHistory.COLUMN_NAME_OPER1);
					sb.append(c.getDouble(column));
					column = c.getColumnIndex(CalculatorHistory.COLUMN_NAME_OPERATION);
					sb.append(c.getString(column));
					column = c.getColumnIndex(CalculatorHistory.COLUMN_NAME_OPER2);
					sb.append(c.getDouble(column));
					column = c.getColumnIndex(CalculatorHistory.COLUMN_NAME_RESULT);
					sb.append('=');
					sb.append(c.getDouble(column));
					loadedHistory.add(0, sb.toString());
					//clean the StringBuilder
					sb.delete(0, sb.length());
				}
				db.close();
				CalculatorHistoryHelper.dbLock.unlock();
			}
			catch (InterruptedException e) {
				CalculatorHistoryHelper.dbLock.unlock();
//				Log.v("AsyncTask", "Caught an InterruptedException: " + e.getMessage());
				return null;
			}
			catch (Exception e) {
				CalculatorHistoryHelper.dbLock.unlock();
//				Log.v("AsyncTask", "Caught an Exception: " + e.getMessage());
				return null;
			}
			
			if(isCancelled())
				return null;
			return loadedHistory;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> loadedHistory) {
			if(loadedHistory == null)
				return;
			
			//No locking needed to access ArrayList history.
			//onPostExecute runs on the UI thread.
			history.addAll(0, loadedHistory);
			historyListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Explicitly set default operation.
		op = theOperation.ADD;
		
		Spinner operSpinner =
			(Spinner)findViewById(R.id.operation_spinner);
		ListView lv = (ListView)findViewById(R.id.listView1);
		
		ArrayAdapter<CharSequence> aa =
			ArrayAdapter.createFromResource(this,
				R.array.operations, android.R.layout.simple_spinner_item);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		operSpinner.setOnItemSelectedListener(this);
		operSpinner.setAdapter(aa);
		
		dbHelper = new CalculatorHistoryHelper(this);
		history = new ArrayList<String>();
		historyListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, history);
		newoperations = new LinkedList<CalcOperation>();
		
		lv.setAdapter(historyListAdapter);
		
		//Start background task
		loadHistory = new LoadHistoryTask();
		loadHistory.execute(dbHelper);
	}

	@Override
	protected void onPause() {
		super.onPause();
		loadHistory.cancel(true);
		
		CalcOperation[] myData = new CalcOperation[1];
		CalcOperation[] otherArray = newoperations.toArray(myData);
		if(myData[myData.length-1] == null)
			myData = otherArray;
		//Only need to start intent if we have data to store:
		if(myData[0] != null)
		{
			Intent saveHistory = new Intent(this, com.example.calculator.SaveHistoryService.class);
			saveHistory.putExtra(getResources().getString(R.string.ADD_TO_DB_KEY), myData);
			startService(saveHistory);	
			//Clear LinkedList
			newoperations = new LinkedList<CalcOperation>();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuitem) {
		super.onOptionsItemSelected(menuitem);
		switch(menuitem.getItemId()) {
		case R.id.answerMenuItem:
			helpCalc();
			break;
		case R.id.clearMenuItem:
			helpClear();
			break;
		default:
			return false;
		}
		return true;
	}
	
	public void calculate(View v) {
		helpCalc();
	}
	
	private void helpCalc() {
		EditText oper1EditText;
		EditText oper2EditText;
		
		oper1EditText = (EditText)findViewById(R.id.operand1);
		oper2EditText = (EditText)findViewById(R.id.operand2);
		
		String oper1 = oper1EditText.getText().toString();
		String oper2 = oper2EditText.getText().toString();
		
		TextView resTextView = (TextView)findViewById(R.id.resultTextView);
		
		double result = 0;
		double op1;
		double op2;
		char operation = '+';
		
		try {
//			Log.v("helpCalc", "The operation is " + op);
			op1 = Double.parseDouble(oper1);
			op2 = Double.parseDouble(oper2);
			switch(op) {
			case ADD: result = op1 + op2; operation = '+'; break;
			case SUBTRACT: result = op1 - op2; operation = '-'; break;
			case MULT: result = op1 * op2; operation = '*'; break;
			case DIV: result = (op2 != 0) ? op1 / op2 : 0; operation = '/'; break;
			}
		}
		catch (NumberFormatException e) {
			resTextView.setText("One of the operands is not a number!");
			return;
		}
		resTextView.setText("" + result);
		
		//Toast notification
		Toast.makeText(getApplicationContext(),
			"The result is: " + result, Toast.LENGTH_SHORT).show();
			
		//Status bar notification
		NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
		notiBuilder.setSmallIcon(R.drawable.ic_launcher);
		notiBuilder.setContentTitle("Calculator has a result!");
		notiBuilder.setContentText("The result is: " + result);
		
		NotificationManager notiMan = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notiMan.notify(CALC_NOTI_ID, notiBuilder.build());
		
		newoperations.add(new CalcOperation(op1, op2, operation, result));
		history.add(0, "" + op1 + operation + op2 + '=' + result);
		
		historyListAdapter.notifyDataSetChanged();
		
		Intent i = new Intent(this, ResultActivity.class);
//		Log.v("Main activity", "Intent successfully created");
		i.putExtra("result", result);
		startActivity(i);
	} 
	
	public void clear(View v) {	
		helpClear();
	}
	
	private void helpClear() {
		EditText oper1EditText = (EditText)findViewById(R.id.operand1);
		EditText oper2EditText = (EditText)findViewById(R.id.operand2);
//		TextView operTextView = (TextView)findViewById(R.id.operTextView);
		TextView resTextView = (TextView)findViewById(R.id.resultTextView);
		
		oper1EditText.setText("");
		oper2EditText.setText("");
//		operTextView.setText(R.string.operation);
		resTextView.setText(R.string.result);
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View spinnerItem,
			int pos,
			long id) {
//		Log.v("onItemSelected", "Value of pos: " + pos);
		switch(pos) {
		case 0: op = theOperation.ADD; break;
		case 1: op = theOperation.SUBTRACT; break;
		case 2: op = theOperation.MULT; break;
		case 3: op = theOperation.DIV; break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
//		Log.v("onNothingSelected", "onNothingSelected - should not be called");
	}
}
