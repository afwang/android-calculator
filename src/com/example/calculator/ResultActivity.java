package com.example.calculator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Log.v("Result activity", "Able to switch activities");
		super.onCreate(savedInstanceState);
		
		Intent in = getIntent();
		double result = in.getExtras().getDouble("result");

		setContentView(R.layout.activity_result);
		TextView resText = (TextView)findViewById(R.id.resultActivityTextView);
//		Log.v("Result Activity", "TextView successfully retrieved");
		resText.setText("Hello. The result is " + result);
//		Log.v("Result Activity", "Setting the text was not at all a problem!");
//		Log.v("Result Activity", "Creation of activity was not a problem");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}
	
	public void goToMain(View v) {
		Intent out = new Intent(this, MainActivity.class);
		startActivity(out);
	}
}
