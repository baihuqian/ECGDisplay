package cmu.ece.BaihuQian.ECGDisplayUI;

import java.util.Set;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import cmu.ece.BaihuQian.SensorComm.ConnectBTSensor;
import cmu.ece.BaihuQian.SensorComm.ProcessingAdapter;

public class BluetoothActivity extends Activity {
	private BluetoothAdapter btAdapter;
	private BluetoothDevice sensor;
	private static final String SENSOR_NAME = "Cog-Dev-3 160";
	
	private GraphViewSeries graphViewSeries;
	private GraphView graphView;
	private GraphViewData [] graphViewData;
	
	private final static int ORIGINAL_SAMPLE_RATE = 500; // original sampling rate
	private final static int DOWN_SAMPLE = 5; // down sample factor
	private final static int WINDOW_WIDTH = 10; // display window width in seconds
	private final static double UPDATE_FREQ = 1; // update frequency in seconds
	private Runnable add_data;
	private Handler mHandler;
	private boolean updateFlag = true;
	private double current_time = 0;
	private ProcessingAdapter adapter = null;
	private ConnectBTSensor conn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		mHandler = new Handler();
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
			
			
			
			int sample_rate = ORIGINAL_SAMPLE_RATE / DOWN_SAMPLE;
			graphViewData = new GraphViewData[1];
			graphViewData[0] = new GraphViewData(0, 0);
			graphViewSeries = new GraphViewSeries(graphViewData);
			graphView = new LineGraphView(this, "ECG");
			graphView.addSeries(graphViewSeries);
			graphView.setViewPort(0, WINDOW_WIDTH * sample_rate);
			graphView.setScrollable(true);
			graphView.setScalable(true);
			graphView.setManualYAxisBounds(5, -1);
			graphView.scrollToEnd();
			LinearLayout graph = (LinearLayout) findViewById(R.id.BTgraph);
			graph.addView(graphView);
			
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
				if(updateFlag == false) {
					updateFlag = true;
				}
				else {
					if(adapter == null) {
						adapter = conn.getAdapter();
					}
					if(adapter != null) {
						int dataLength = (int)(ORIGINAL_SAMPLE_RATE * UPDATE_FREQ);
						double [] displayValue = adapter.readDataBuffer(dataLength);
						if(displayValue != null) {
							GraphViewData [] displayData = new GraphViewData [dataLength];
							for(int i = 0; i < dataLength; i++) {
								double time = current_time + (double)i / (double)ORIGINAL_SAMPLE_RATE;
								displayData[i] = new GraphViewData(time, displayValue[i]);
							}
							graphViewSeries.resetData(graphViewData); // feed new data to the data series
							graphView.scrollToEnd(); // scroll to end to animate updating
							current_time += UPDATE_FREQ;
						}
						
					}
					mHandler.postDelayed(this, (long)(UPDATE_FREQ * 1000));
				}
			}
			
		};
		
		if(updateFlag) {
			mHandler.postDelayed(add_data, (long)(UPDATE_FREQ * 1000));
		} else {
			mHandler.removeCallbacks(add_data);
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
