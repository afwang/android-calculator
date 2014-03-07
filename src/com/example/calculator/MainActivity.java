package com.example.calculator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	enum theOperation {ADD, SUBTRACT, MULT, DIV};
	
	private theOperation op;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Explicitly set default operation.
		op = theOperation.ADD;
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
	
	public void setOperation(View v) {
		TextView operTextView = (TextView)findViewById(R.id.operTextView);
		Button but = (Button)v;
		char operation = but.getText().toString().charAt(0);
		
		operTextView.setText("" + operation);
		switch(operation)
		{
		case '+': op = theOperation.ADD; break;
		case '-': op = theOperation.SUBTRACT; break;
		case '*': op = theOperation.MULT; break;
		case '/': op = theOperation.DIV; break;
		}
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
		
		try {
			double op1 = Double.parseDouble(oper1);
			double op2 = Double.parseDouble(oper2);
			switch(op) {
			case ADD: result = op1 + op2; break;
			case SUBTRACT: result = op1 - op2; break;
			case MULT: result = op1 * op2; break;
			case DIV: result = (op2 != 0) ? op1 / op2 : 0; break;
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
		TextView operTextView = (TextView)findViewById(R.id.operTextView);
		TextView resTextView = (TextView)findViewById(R.id.resultTextView);
		
		oper1EditText.setText("");
		oper2EditText.setText("");
		operTextView.setText(R.string.operation);
		resTextView.setText(R.string.result);
	}
}
