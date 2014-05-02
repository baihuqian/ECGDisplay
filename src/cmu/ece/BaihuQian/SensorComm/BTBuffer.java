package cmu.ece.BaihuQian.SensorComm;

/**
 * Buffer that converts and store data from sensor
 * @author Baihu Qian
 *
 */
public class BTBuffer implements SensorCommConstants{
	private static final int BUFLEN = 50000;

	//filter parameters and sample rate
	protected double HPFreq;

	//device gain settings
	protected double GAIN;
	protected double VREF;
	protected double ISTIM;
	protected double ADC_TO_VOLTS;
	protected double TO_Z;

	//impedance check status status
	protected boolean impCheckOn;

	//memory buffers for data

	private double[] filData; //hpf for display




	//pointers for circular buffers
	private volatile int readIndex;
	private volatile int writeIndex;

	//state variables for HPF
	private double xprev_hp;
	private double yprev_hp;

	//state variables for notch
	private double xp1_notch;
	private double xp2_notch;
	private double yp1_notch;
	private double yp2_notch;

	//state variables for LPF
	private double xp1_lpf;
	private double yp1_lpf;

	public BTBuffer() {
		//init buffer arrays

		filData = new double[BUFLEN];

		//inital indicies
		readIndex = 0;
		writeIndex = 0;

		//init device parameters
		HPFreq = 0.5;
		impCheckOn = false;

		GAIN = 3.0;
		VREF = 2.5;
		ISTIM = 0.000000024;

		ADC_TO_VOLTS = (2*VREF/(4294967296.0*GAIN));
		TO_Z = 1.4/(ISTIM*2.0);
	}

	public synchronized int numSamplesReady()
	{
		if( (writeIndex-readIndex) >= 0)
		{
			return writeIndex-readIndex;
		}
		else
		{
			return (BUFLEN-readIndex+writeIndex);
		}
	}


	public void writeSamples(double newData)
	{
		//save the raw data

		double rawData = (newData*ADC_TO_VOLTS);

		//perform high-pass filtering
		synchronized(this) {

			filData[writeIndex] = hpf(rawData);

			writeIndex++;
			if(writeIndex>=BUFLEN)
			{
				writeIndex = 0;
			}
			//Log.i("Buffer", "write index" + writeIndex);
		}
	}


	public double readSamples() {
		synchronized(this) {
			double filDataOut = filData[readIndex];

			readIndex++;
			if(readIndex >= BUFLEN) {
				readIndex = 0;
			}
			//Log.i("Buffer", "read index" + readIndex);
			return filDataOut;
		}
	}


	/*
	 * Updates high pass filter with newSample, updates state variables and returns
	 * filtered data
	 */
	private double hpf(double newSample)
	{
		//calculate coefficients
		double delta_t = 1.0/SAMPLE_RATE; //sample interval is 1/current sample rate
		double RC_HP = 1/(6.28*HPFreq);
		double alpha = RC_HP/(RC_HP+delta_t);

		//perform HPF
		double yout = alpha * (yprev_hp + (double) newSample - xprev_hp);

		//update state variables
		xprev_hp = (double) newSample;
		yprev_hp = yout;

		return yout;
	}



}
