package cmu.ece.BaihuQian.SensorComm;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
/**
 * start BT connection
 * @author Baihu Qian
 *
 */
public class ConnectBTSensor extends Thread {
	private BluetoothDevice sensor;
	private BluetoothSocket incomingSocket;
	private ProcessingAdapter adapter;
	private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private boolean connectionStarted, connectionFailed;
	public ConnectBTSensor(BluetoothDevice sensor) {
		BluetoothSocket tmp = null;
		this.sensor = sensor;
		connectionStarted = false;
		connectionFailed = false;
		try {
			tmp = this.sensor.createInsecureRfcommSocketToServiceRecord(mUUID);
		} catch(Exception e) {

		}
		incomingSocket = tmp;
	}
	public void run() {
		try {
			incomingSocket.connect();
		} catch(Exception e) {
			try {
				//Log.i("Connect BT", "failed");
				connectionFailed = true;
				incomingSocket.close();
			} catch (Exception ee) {

			}
			return;
		}
		//Log.i("Connect BT", "successful");
		connectionStarted = true;
		ManageBTConnection manager = new ManageBTConnection(incomingSocket);
		adapter = new ProcessingAdapter(manager.getBuffer());
		manager.start();
	}

	public void cancel() {
		try {
			incomingSocket.close();
		} catch(Exception e) {

		}
	}
	public ProcessingAdapter getAdapter() {
		return adapter;
	}
	public boolean isConnectionStarted() {
		return connectionStarted;
	}
	public boolean isConnectionFailed() {
		return connectionFailed;
	}


}
