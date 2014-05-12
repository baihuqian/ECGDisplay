package cmu.ece.BaihuQian.ECGDisplayUI;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cmu.ece.BaihuQian.DFAUtil.EventDetectedInterface;
import cmu.ece.BaihuQian.DFAUtil.TDFA;
import cmu.ece.BaihuQian.DFAUtil.TDFADetection;

public class MPDFADemoActivity extends Activity {
	
	private final Handler mHandler = new Handler();
	private Runnable addData;
	private double [] data;
	private TDFADetection detector;
	private TDFA mpdfa;
	private int index;
	private final int [] windows = {7, 13, 19};
	private static int start_size = 2000;
	private int countDown = 0;
	private int eventCount = 0;
	private final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
	
	private TextView textViewIter, textViewEvent, textViewF7, textViewF13, textViewF19, 
		textViewRR, textViewIndex, textViewEventAmp, textViewEventIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpdfademo);

		textViewIter = (TextView) findViewById(R.id.textViewNumIter);
		textViewEvent = (TextView) findViewById(R.id.textViewNumEvent);
		textViewF7 = (TextView) findViewById(R.id.textViewF7);
		textViewF13 = (TextView) findViewById(R.id.textViewF13);
		textViewF19 = (TextView) findViewById(R.id.textViewF19);
		textViewRR = (TextView) findViewById(R.id.textViewSignal);
		textViewIndex = (TextView) findViewById(R.id.textViewIndex);
		textViewEventAmp = (TextView) findViewById(R.id.textViewEventAmplitude);
		textViewEventIndex = (TextView) findViewById(R.id.textViewEventIndex);
		
		textViewEvent.setText(Integer.toString(eventCount));
		
		data = new double[90600];
		
		
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard,"data.txt"); // initialize the file
		try {
			DataInputStream data_in = new DataInputStream(
			        new BufferedInputStream(
			            new FileInputStream(file)));
			for(int i = 0; i < data.length; i++) {
				String line = data_in.readLine();
				data[i] = Double.parseDouble(line);
			}
			data_in.close();
		} catch(Exception e) {
			
		}
		double [] initialData = new double [start_size];
		for(int j = 0; j < initialData.length; j++) {
			initialData[j] = data[j];
		}
		index = start_size;
		
<<<<<<< HEAD
		detector = new TDFADetection();
		mpdfa = new TDFA(initialData, detector);
=======
		detector = new MPDFADetection(windows.length);
		//mpdfa = new MPDFA(initialData, windows, detector);
>>>>>>> demo_version
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(addData);
	}
/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		addData = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mpdfa.addData(new double [] {data[index++]});
				textViewIter.setText(Integer.toString(index));
				if(EventDetectedInterface.eventFlag) {
					// set alarm
					setAlert(true);
					textViewEventAmp.setText(String.format("%.4f", EventDetectedInterface.eventData.getSignal()));
					textViewEventIndex.setText(Integer.toString(EventDetectedInterface.eventData.getIndex()));
					EventDetectedInterface.eventFlag = false;
					countDown = 3;
				}
				else {
					if(countDown == 1) {
						setAlert(false);
					}
					countDown--;
				}
				if(EventDetectedInterface.data != null) {
					textViewF7.setText(String.format("%.4f", EventDetectedInterface.data.getF(0)));
					textViewF13.setText(String.format("%.4f", EventDetectedInterface.data.getF(1)));
					textViewF19.setText(String.format("%.4f", EventDetectedInterface.data.getF(2)));
					textViewRR.setText(String.format("%.3f", EventDetectedInterface.data.getSignal()));
					textViewIndex.setText(Integer.toString(EventDetectedInterface.data.getIndex()));
				}
				if(index < data.length) {
					mHandler.postDelayed(this, (long)(1000));
				}
				
			}
			
		};
		if(index < data.length) {
			mHandler.postDelayed(addData, (long)(1000));
		} else {
			mHandler.removeCallbacks(addData);
		}
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mpdfademo, menu);
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

	private void setAlert(boolean flag) {
		if(flag) {
			eventCount++;
			textViewEvent.setText(Integer.toString(eventCount));
			View someView = findViewById(R.id.container);
			//alert.setText("Event");
			someView.getRootView().setBackgroundColor(Color.argb(255, 255, 0, 0));
			tg.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000);
		}
		else {
			View someView = findViewById(R.id.container);
			//alert.setText("");
			someView.getRootView().setBackgroundColor(Color.argb(255, 255, 255, 255));
		}
	}

}
