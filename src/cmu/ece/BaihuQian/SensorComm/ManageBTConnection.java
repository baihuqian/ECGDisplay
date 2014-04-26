package cmu.ece.BaihuQian.SensorComm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

// all BT processing goes here
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

		try {
			tmpIn = this.socket.getInputStream();
			tmpOut = this.socket.getOutputStream();
		} catch (IOException e) {

		}
		inStream = tmpIn;
		outStream = tmpOut;
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}

	public int readByte() throws IOException{
		//byte [] buffer = new byte[1024];
		//int bytes;
		if(byteBufferIndex == numBytesToRead) {
			int read = 0;
			while (read < byteBuffer.length) {
				read = inStream.read(byteBuffer, read, byteBuffer.length - read);
			}
			byteBufferIndex = 0;
		}
		return (byteBuffer[byteBufferIndex++]&0xFF); // convert to unsigned int
	}

	public int writeByte(byte[] data) throws IOException {
		outStream.write(data);
		return data.length;
	}

	public void run() {
		try {
			for(int c=0; c<4096; c++) { //flush buffers
				readByte();
			}

			while (!Thread.interrupted() && !needToClose) {
				//find sync byte
				while (readByte() != 255) { }

				int packetCount = readByte();

				//read data
				int msb, lsb2, lsb1;

				msb = readByte();
				lsb2 =readByte();
				lsb1 = readByte();

				int temp = ((msb << 24) | (lsb2 << 17) | (lsb1 << 10));

				double newSamples = (double) temp;

				for(int i = 0; i < 10; i++) { // ignore two other channels and 4 tail bytes (2 * 3 + 4 = 10)
					readByte();
				}
				//write to buffer
				buffer.writeSamples(newSamples, packetCount, 0);
			} 
		}catch(IOException e) {

		}

	}
	
	public BTBuffer getBuffer() {
		return buffer;
	}
}
