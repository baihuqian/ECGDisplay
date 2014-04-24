package cmu.ece.BaihuQian.SensorComm;

public class ProcessingAdapter extends Thread{
	private BTBuffer buffer;
	private double [] dataBuffer;
	private static final int DATABUFFERSIZE = 10000;
	private boolean needToStop;
	private int writeIndex, readIndex;
	
	public ProcessingAdapter(BTBuffer buffer) {
		this.buffer = buffer;
		needToStop = false;
		dataBuffer = new double [DATABUFFERSIZE];
		writeIndex = 0;
		readIndex = 0;
	}
	
	public synchronized void run() {
		while(!Thread.interrupted() && !needToStop) {
			int numSampleReady;
			double filDataOut;
			while((numSampleReady = buffer.numSamplesReady()) <= 0) {} // wait until samples are ready
			for(int i = 0; i < numSampleReady; i++) { // read samples
				filDataOut = buffer.readSamples();
				dataBuffer[writeIndex++] = filDataOut;
				if(writeIndex >= DATABUFFERSIZE) {
					writeIndex = 0;
				}
			}
			
			
		}
	}
	
	public synchronized int numSamplesReady()
	{
		if( (writeIndex - readIndex) >= 0)
		{
			return writeIndex - readIndex;
		}
		else
		{
			return (DATABUFFERSIZE - readIndex + writeIndex);
		}
	}

	public synchronized double[] readDataBuffer() {
		int numSamplesReady = numSamplesReady();
		if(numSamplesReady > 0) {
			double [] returnValue = new double [numSamplesReady];
			if(readIndex + numSamplesReady <= DATABUFFERSIZE) {
				System.arraycopy(dataBuffer, readIndex, returnValue, 0, numSamplesReady);
				
			}
			else {
				int firstPart = DATABUFFERSIZE - readIndex;
				System.arraycopy(dataBuffer, readIndex, returnValue, 0, firstPart);
				System.arraycopy(dataBuffer, 0, returnValue, firstPart, numSamplesReady - firstPart);
			}
			readIndex += numSamplesReady;
			if(readIndex >= DATABUFFERSIZE) {
				readIndex -= DATABUFFERSIZE;
			}
			return returnValue;
		}
		else {
			return null;
		}
	}
	
	public synchronized double[] readDataBuffer(int size) {
		int numSamplesReady = numSamplesReady();
		if(numSamplesReady >= size) {
			double [] returnValue = new double [size];
			if(readIndex + size <= DATABUFFERSIZE) {
				System.arraycopy(dataBuffer, readIndex, returnValue, 0, size);
				
			}
			else {
				int firstPart = DATABUFFERSIZE - readIndex;
				System.arraycopy(dataBuffer, readIndex, returnValue, 0, firstPart);
				System.arraycopy(dataBuffer, 0, returnValue, firstPart, size - firstPart);
			}
			readIndex += size;
			if(readIndex >= DATABUFFERSIZE) {
				readIndex -= DATABUFFERSIZE;
			}
			return returnValue;
		}
		else {
			return null;
		}
	}
}
