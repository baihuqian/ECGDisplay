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
	
	public void run() {
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
	
	public int numSamplesReady()
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
}
