package cmu.ece.BaihuQian.SensorComm;

/**
 * Connect buffer to display or post-processing
 * @author Baihu Qian
 *
 */
public class ProcessingAdapter{
	private BTBuffer buffer;

	
	public ProcessingAdapter(BTBuffer buffer) {
		this.buffer = buffer;
	}
	/**
	 * encapsulate buffer methods
	 * @return
	 */
	public int numSamplesReady()
	{
		return buffer.numSamplesReady();
	}

	/**
	 * read as much data as possible
	 * @return
	 */
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
	
	/**
	 * read a segment of data from buffer
	 * @param size length of data to read
	 * @return return an array of data if data are available, null if failed
	 */
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
