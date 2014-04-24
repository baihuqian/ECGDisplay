package cmu.ece.BaihuQian.ECGDisplayUI;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import cmu.ece.BaihuQian.SensorComm.InsufficientDataFromSDFileException;
import cmu.ece.BaihuQian.SensorComm.ReadSDCardFile;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class DisplaySDFileActivity extends Activity {
	private double [] data;
	private GraphViewSeries graphViewSeries;
	private String fileName = "GetUp.COG";
	private File file;
	private GraphView graphView;
	private Runnable add_data;
	
	private final static int ORIGINAL_SAMPLE_RATE = 500; // original sampling rate
	private final static int DOWN_SAMPLE = 5; // down sample factor
	private final static int WINDOW_WIDTH = 10; // display window width in seconds
	private final static double UPDATE_FREQ = 1; // update frequency in seconds
	private final static int FORWARD = 0; // start point in seconds
	private final static int READ_INTERVAL = 10; // how often does read file calls
	
	private boolean update_flag = true;
	private int update_time = 0; // record number of rounds the update happens
	private int sample_rate; // actual sample_rate after down sampling
	private final Handler mHandler = new Handler(); // handler to call runnable
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_sdfile);
		sample_rate = ORIGINAL_SAMPLE_RATE / DOWN_SAMPLE; // calculate the actual sampling rate
		
		
		File sdcard = Environment.getExternalStorageDirectory();
		file = new File(sdcard,fileName); // initialize the file
		
		//read file from internal storage
		try	{
			data = ReadSDCardFile.loadFile(file, DOWN_SAMPLE, FORWARD, WINDOW_WIDTH + UPDATE_FREQ * READ_INTERVAL); // read file
		} catch(InsufficientDataFromSDFileException e) {
			update_flag = false;
		}
		// convert to graphView data: add time stamp
		if(update_flag) {
			GraphViewData [] graphViewData = new GraphViewData[sample_rate * WINDOW_WIDTH];
			for(int i = 0; i < sample_rate * WINDOW_WIDTH; i++) {
				graphViewData[i] = new GraphViewData((double)i/(double)sample_rate + (double)FORWARD, data[i]);
			}
			
			
			// setup display
			graphViewSeries = new GraphViewSeries(graphViewData);
			graphView = new LineGraphView(this, "ECG");
			graphView.addSeries(graphViewSeries);
			graphView.setViewPort(0, WINDOW_WIDTH * sample_rate);
			graphView.setScrollable(true);
			graphView.setScalable(true);
			graphView.setManualYAxisBounds(5, -1);
			graphView.scrollToEnd();
			LinearLayout graph = (LinearLayout) findViewById(R.id.SDgraph);
			graph.addView(graphView);
		}
		else {
			Toast.makeText(getApplicationContext(), "No enougth data!", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		add_data = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//int num_data_add = (int) (UPDATE_FREQ * SAMPLE_RATE);
				if(update_flag == true) {
					if(update_time != 0) { // no need to update for the first time
						GraphViewData [] graphViewData = new GraphViewData[sample_rate * WINDOW_WIDTH];
						double offset_time = FORWARD + update_time * UPDATE_FREQ; // actual start time in seconds
						int offset_index = (update_time % READ_INTERVAL) * (int) (UPDATE_FREQ * sample_rate); //offset in data[]
						if((update_time % READ_INTERVAL) == 0) { // need to read new data
							try {
								data = ReadSDCardFile.loadFile(file, DOWN_SAMPLE, FORWARD + update_time * UPDATE_FREQ, WINDOW_WIDTH + UPDATE_FREQ * READ_INTERVAL); 
						
							} catch(InsufficientDataFromSDFileException e) {
								update_flag = false;
							}
						}
						if (update_flag == true) {
							for(int i = 0; i < sample_rate * WINDOW_WIDTH; i++) { //populate into graphViewData
								graphViewData[i] = new GraphViewData((double)i/(double)sample_rate + (double)offset_time, data[i + offset_index]);
							}
							graphViewSeries.resetData(graphViewData); // feed new data to the data series
							graphView.scrollToEnd(); // scroll to end to animate updating
						}
						
					}
					// Toast.makeText(DisplayActivity.this, "Round " + update_time, Toast.LENGTH_SHORT).show(); // test purposes
					if(update_flag == true) {
						update_time++;
						mHandler.postDelayed(this, (long)(UPDATE_FREQ * 1000));
					}
				}
			}
			
		};
		if(update_flag) {
			mHandler.postDelayed(add_data, (long)(UPDATE_FREQ * 1000));
		} else {
			mHandler.removeCallbacks(add_data);
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(add_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
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
