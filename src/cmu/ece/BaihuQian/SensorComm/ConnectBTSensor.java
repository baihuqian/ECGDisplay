package cmu.ece.BaihuQian.SensorComm;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
// all BT connection goes here
public class ConnectBTSensor extends Thread {
	private BluetoothDevice sensor;
	private BluetoothSocket incomingSocket;
	private ProcessingAdapter adapter;
	private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public ConnectBTSensor(BluetoothDevice sensor) {
		BluetoothSocket tmp = null;
		this.sensor = sensor;
		
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
				incomingSocket.close();
			} catch (Exception ee) {
				
			}
			return;
		}
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
	
}
