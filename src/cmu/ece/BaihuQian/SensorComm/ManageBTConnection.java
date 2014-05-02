package cmu.ece.BaihuQian.SensorComm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * all BT processing goes here
 * @author Baihu Qian
 *
 */
public class ManageBTConnection extends Thread {


	private final BluetoothSocket socket;
	private final InputStream inStream;
	private final OutputStream outStream;
	private BTBuffer buffer;

	private int numBytesToRead = 500;
	private byte [] byteBuffer;
	private int byteBufferIndex;

	public boolean needToClose = false;

	public ManageBTConnection(BluetoothSocket socket) {
		this.socket = socket;
		byteBuffer = new byte[numBytesToRead];
		byteBufferIndex = numBytesToRead;
		this.buffer = new BTBuffer();
		
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try { // instantiate IOStream
			tmpIn = this.socket.getInputStream();
			tmpOut = this.socket.getOutputStream();
		} catch (IOException e) {

		}
		inStream = tmpIn;
		outStream = tmpOut;
		//Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}

	public int readByte() throws IOException{

		if(byteBufferIndex == numBytesToRead) {
			int alreadyRead = 0; // number of bytes already been read
			int actualRead = 0; // actual number of bytes from last read
			while (alreadyRead < byteBuffer.length) { // fill byteBuffer
				// read data
				actualRead = inStream.read(byteBuffer, alreadyRead, byteBuffer.length - alreadyRead);
				
				if(actualRead == -1) { // reach the end
					Log.i("MBTC", "readByte failed");
					throw new IOException();
				}
				alreadyRead += actualRead; // add newly read data to existing data
			}
			byteBufferIndex = 0;
			//Log.i("MBTC", "readByte successful");
		}
		return (byteBuffer[byteBufferIndex++]&0xFF); // convert to unsigned int
	}

	public int writeByte(byte[] data) throws IOException {
		outStream.write(data);
		return data.length;
	}

	public void run() {
		try {
			//Log.i("MBTC", "start");
			
			for(int c=0; c<4096; c++) { //flush buffers
				readByte();
			}
	
			while (true) {
				//find sync byte
				while (readByte() != 255) { }

				readByte(); //read packet count byte

				//read data
				int msb, lsb2, lsb1;

				msb = readByte();
				lsb2 =readByte();
				lsb1 = readByte();

				int temp = ((msb << 24) | (lsb2 << 17) | (lsb1 << 10)); //assemble the data

				double newSamples = (double) temp; // convert to double

				for(int i = 0; i < 10; i++) { // ignore two other channels and 4 tail bytes (2 * 3 + 4 = 10)
					readByte();
				}
				//write to buffer
				buffer.writeSamples(newSamples);
				//Log.i("MBTC", "buffer write " + newSamples);
			} 
		}catch(IOException e) {
			//Log.i("MBTC", "interruptted");
		}
		//Log.i("MBTC", "end");
	}
	
	public BTBuffer getBuffer() {
		return buffer;
	}
}
