package cmu.ece.BaihuQian.ECGDisplayUI;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cmu.ece.BaihuQian.SensorComm.ConnectBTSensor;

public class BluetoothActivity extends Activity {
	private BluetoothAdapter btAdapter;
	private BluetoothDevice sensor;
	private static final String SENSOR_NAME = "Cog-Dev-3 160";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
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
			ConnectBTSensor conn = new ConnectBTSensor(sensor);
			conn.start();
			
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
