package cmu.ece.BaihuQian.ECGDisplayUI;

import java.util.Set;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import cmu.ece.BaihuQian.SensorComm.ConnectBTSensor;
import cmu.ece.BaihuQian.SensorComm.ProcessingAdapter;

public class BluetoothActivity extends Activity {
	// Bluetooth related variables
	private BluetoothAdapter btAdapter;
	private BluetoothDevice sensor;
	private static final String SENSOR_NAME = "Cog-Dev-3 160";
	private ProcessingAdapter adapter = null;
	private ConnectBTSensor conn;

	// GraphView related variables
	private GraphViewSeries graphViewSeries;
	private GraphView graphView;
	private GraphViewData [] graphViewData;

	// real-time update
	private final static int ORIGINAL_SAMPLE_RATE = 500; // original sampling rate
	private final static int DOWN_SAMPLE = 5; // down sample factor
	private final static int WINDOW_WIDTH = 10; // display window width in seconds
	private final static double UPDATE_FREQ = 1; // update frequency in seconds
	private int sample_rate; // actual sample rate
	private int dataLength; // length of data displayed
	private int newDataLength; // length data appended in each update epoch
	
	
	private Runnable add_data;
	private Handler mHandler;
	private boolean updateFlag = true;
	private double current_time = 10;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		// handle bluetooth connection
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter != null) {
			if(!btAdapter.isEnabled()) {
				btAdapter.enable();
				//Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
			if(pairedDevices.size() > 0) {
				for(BluetoothDevice device : pairedDevices) {
					if(device.getName().equalsIgnoreCase(SENSOR_NAME)){
						sensor = device;
						break;
					}
				}
			}
			if(pairedDevices.size() <= 0 || sensor == null) {

			}

			// set up display related variables
			mHandler = new Handler();
			sample_rate = ORIGINAL_SAMPLE_RATE / DOWN_SAMPLE;
			dataLength = sample_rate * WINDOW_WIDTH;
			newDataLength = (int)(sample_rate * UPDATE_FREQ);
			
			
			graphViewData = new GraphViewData[dataLength];
			for(int i = 0; i < graphViewData.length; i++) {
				graphViewData[i] = new GraphViewData((double)(i)/(double)(sample_rate), 0);
			}

			graphViewSeries = new GraphViewSeries(graphViewData);
			graphView = new LineGraphView(this, "ECG");
			graphView.addSeries(graphViewSeries);
			graphView.setViewPort(0, WINDOW_WIDTH);
			graphView.setScrollable(true);
			graphView.setScalable(true);
			graphView.setManualYAxisBounds(5, -1);
			graphView.scrollToEnd();
			LinearLayout graph = (LinearLayout) findViewById(R.id.BTgraph);
			graph.addView(graphView);

			//start connection
			conn = new ConnectBTSensor(sensor);
			conn.start();

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		add_data = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// connection still pending, wait
				while(!conn.isConnectionStarted() && !conn.isConnectionFailed()) {}
				if(conn.isConnectionFailed()) {
					mHandler.removeCallbacks(add_data);
					updateFlag = false;
					Toast.makeText(BluetoothActivity.this, "Cannot start Bluetooth Connection!", Toast.LENGTH_LONG).show(); 
					Intent intent = new Intent(BluetoothActivity.this, WelcomeActivity.class);
					BluetoothActivity.this.startActivity(intent);
					return;
				}
				if(conn.isConnectionStarted()) {
					//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					if(adapter == null) { // first time, initialize adapter
						adapter = conn.getAdapter();
					}
					if(adapter != null) {
						// fetch new data
						double [] displayValue = adapter.readDataBuffer((int)(ORIGINAL_SAMPLE_RATE * UPDATE_FREQ));
						if(displayValue != null) {

							GraphViewData [] displayData = new GraphViewData [dataLength];
							int offset = dataLength - newDataLength;
							// copy old data
							for(int i = 0; i < offset; i++) {
								displayData[i] = graphViewData[i + newDataLength];
							}

							double time = current_time;
							double update_interval = 1.0 / (double)sample_rate;
							// add new data, down sample if necessary
							for(int i = 0; i < newDataLength; i++) {
								displayData[i + offset] = new GraphViewData(time, displayValue[i * DOWN_SAMPLE] * 1000);
								time += update_interval;
							}
							graphViewSeries.resetData(displayData); // feed new data to the data series
							graphView.scrollToEnd(); // scroll to end to animate updating
							
							current_time += UPDATE_FREQ; // update time
							graphViewData = displayData;
						}
						//Toast.makeText(BluetoothActivity.this, "Round " + current_time, Toast.LENGTH_SHORT).show(); // test purposes

					}
					mHandler.postDelayed(this, (long)(UPDATE_FREQ * 1000));
				}
			}

		};

		if(updateFlag) {
			mHandler.postDelayed(add_data, (long)(UPDATE_FREQ * 1000));
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth, menu);
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
