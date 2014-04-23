package cmu.ece.BaihuQian.ECGDisplayUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	private Button buttonSDFile;
	private Button buttonBluetooth;
	private Button buttonDemo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		buttonSDFile = (Button) findViewById(R.id.buttonShowSDFile);
		buttonBluetooth = (Button) findViewById(R.id.buttonShowSensor);
		buttonSDFile.setOnClickListener(buttonSDFileListener);
		buttonBluetooth.setOnClickListener(buttonBluetoothListener);
		buttonDemo = (Button) findViewById(R.id.buttonDemo);
		buttonDemo.setOnClickListener(buttonDemoListener);
		
	}
	private OnClickListener buttonSDFileListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(WelcomeActivity.this, DisplaySDFileActivity.class);
			startActivity(intent);
		}
		
	};
	
	private OnClickListener buttonBluetoothListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(WelcomeActivity.this, BluetoothActivity.class);
			startActivity(intent);
		}
		
	};
	
	private OnClickListener buttonDemoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(WelcomeActivity.this, MPDFADemoActivity.class);
			startActivity(intent);
		}
		
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



}
