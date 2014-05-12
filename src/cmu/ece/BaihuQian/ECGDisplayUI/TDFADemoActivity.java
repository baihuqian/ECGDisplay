package cmu.ece.BaihuQian.ECGDisplayUI;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import cmu.ece.BaihuQian.DFAUtil.EventDetectedInterface;
import cmu.ece.BaihuQian.DFAUtil.MPDFA;
import cmu.ece.BaihuQian.DFAUtil.MPDFAData;
import cmu.ece.BaihuQian.DFAUtil.MPDFADetection;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.os.Build;

public class TDFADemoActivity extends Activity {
	private GraphViewSeries intervalSeries, resultSeries;
	private GraphView graphView;
	private GraphViewData [] intervalData, resultData;
	private final Handler mHandler = new Handler();
	private Runnable addData;
	private double [] data;
	private MPDFADetection detector;
	private MPDFA mpdfa;
	private int index;
	private final int [] windows = {5, 7, 13, 19};
	private static int start_size = 200;
	private int dataLength = 200;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tdfademo);

		data = new double[90600];


		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard,"data.txt"); // initialize the file
		try {
			DataInputStream data_in = new DataInputStream(
					new BufferedInputStream(
							new FileInputStream(file)));
			for(int i = 0; i < data.length; i++) {
				@SuppressWarnings("deprecation")
				String line = data_in.readLine();
				data[i] = Double.parseDouble(line);
			}
			data_in.close();
		} catch(Exception e) {

		}
		double [] initialData = new double [start_size];
		int [] initialIndex = new int [start_size];
		for(int j = 0; j < initialData.length; j++) {
			initialData[j] = data[j];
			initialIndex[j] = j + 1;
		}
		index = start_size;

		detector = new MPDFADetection(windows.length);
		mpdfa = new MPDFA(initialData, initialIndex, windows, detector);

		intervalData = new GraphViewData[dataLength];
		for(int i = 0; i < intervalData.length; i++) {
			intervalData[i] = new GraphViewData(i, initialData[i]);
		}

		resultData = new GraphViewData[dataLength];
		for(int i = 0; i < intervalData.length; i++) {
			resultData[i] = new GraphViewData(i, 0);
		}
		GraphViewSeries.GraphViewSeriesStyle interval_style = new GraphViewSeries.GraphViewSeriesStyle(Color.BLUE, 3);
		GraphViewSeries.GraphViewSeriesStyle result_style = new GraphViewSeries.GraphViewSeriesStyle(Color.RED, 2);
		intervalSeries = new GraphViewSeries("RR interval", interval_style, intervalData);
		resultSeries = new GraphViewSeries("Detection Result",result_style, resultData);
		graphView = new LineGraphView(this, "RR Interval");
		graphView.addSeries(intervalSeries);
		graphView.addSeries(resultSeries);
		graphView.setViewPort(0, dataLength);
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setShowLegend(true);
		graphView.setLegendWidth(200);
		//graphView.setManualYAxisBounds(5, -1);
		graphView.scrollToEnd();
		LinearLayout graph = (LinearLayout) findViewById(R.id.container);
		graph.addView(graphView);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addData = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				double newData = data[index++];
				mpdfa.addData(new double [] {newData}, new int [] {index});
				GraphViewData [] newIntervalData = new GraphViewData[dataLength];
				GraphViewData [] newResultData = new GraphViewData[dataLength];
				for(int i = 0; i < dataLength - 1; i++) {
					newIntervalData[i] = intervalData[i + 1];
					newResultData[i] = resultData[i + 1];
				}
				newIntervalData[dataLength - 1] = new GraphViewData(index, newData);
				newResultData[dataLength - 1] = new GraphViewData(index, 0);
				if(EventDetectedInterface.eventFlag) {
					int size = EventDetectedInterface.size;
					MPDFAData [] eventData = EventDetectedInterface.eventData;
					int start = index - dataLength;
					for(int j = 0; j < size; j++) {
						int idx = eventData[j].getIndex() - start;
						newResultData[idx] = new GraphViewData(eventData[j].getIndex(), 1);
					}
					for(int i = 1; i < dataLength - 1; i++) {
						if(newResultData[i - 1].valueY == 1 && newResultData[i + 1].valueY == 1 && newResultData[i].valueY == 0) {
							int idx = (int)newResultData[i].getX();
							newResultData[i] = new GraphViewData(idx, 1);
						}
					}
				}
				intervalSeries.resetData(newIntervalData); // feed new data to the data series
				resultSeries.resetData(newResultData);
				graphView.scrollToEnd(); // scroll to end to animate updating
				intervalData = newIntervalData;
				resultData = newResultData;
				mHandler.postDelayed(this, (long)(0.1 * 1000));
			}

		};

		mHandler.postDelayed(addData, (long)(0.1 * 1000));
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(addData);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tdfademo, menu);
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
