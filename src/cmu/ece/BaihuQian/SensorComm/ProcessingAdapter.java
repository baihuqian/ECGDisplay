package cmu.ece.BaihuQian.SensorComm;

public class ProcessingAdapter{
	private BTBuffer buffer;
	//private double [] dataBuffer;
	//private static final int DATABUFFERSIZE = 10000;
	//private boolean needToStop;
	//private int writeIndex, readIndex;
	
	public ProcessingAdapter(BTBuffer buffer) {
		this.buffer = buffer;
		//needToStop = false;
		//dataBuffer = new double [DATABUFFERSIZE];
		//writeIndex = 0;
		//readIndex = 0;
	}
	/*
	public void run() {
		while(!Thread.interrupted() && !needToStop) {
			int numSampleReady;
			double filDataOut;
			if((numSampleReady = buffer.numSamplesReady()) > 0) { // wait until samples are ready
				for(int i = 0; i < numSampleReady; i++) { // read samples
					filDataOut = buffer.readSamples();
					dataBuffer[writeIndex++] = filDataOut;
					if(writeIndex >= DATABUFFERSIZE) {
						writeIndex = 0;
					}
				}
			}
			
			
		}
	}
	*/
	public int numSamplesReady()
	{
		//return 1000;
		return buffer.numSamplesReady();
	}

	public double[] readDataBuffer() {
		int numSamplesReady = numSamplesReady();
		if(numSamplesReady > 0) {
			double [] returnValue = new double [numSamplesReady];
			for(int i = 0; i < numSamplesReady; i++) {
				returnValue[i] = buffer.readSamples();
			}
			return returnValue;
		}
		else {
			return null;
		}
	}
	
	public double[] readDataBuffer(int size) {
		int numSamplesReady = numSamplesReady();
		if(numSamplesReady >= size) {
			double [] returnValue = new double [size];
			for(int i = 0; i < size; i++) {
				returnValue[i] = buffer.readSamples();
			}
			return returnValue;
		}
		else {
			return null;
		}
	}
}
