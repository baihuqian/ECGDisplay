package cmu.ece.BaihuQian.SensorComm;

public class BTBuffer implements SensorCommConstants{
	private static final int BUFLEN = 5000;

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
	private double[] rawData; //raw data
	private double[] rawDataImpFil; //raw data plus notch
	private double[] filData; //hpf for display

	private double[] last4Samples;
	private double[] impData;

	private int[] packetCounter;
	private int[] triggerData;

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
		rawData = new double[BUFLEN];
		filData = new double[BUFLEN];
		last4Samples = new double[4];
		impData = new double[BUFLEN];
		rawDataImpFil = new double[BUFLEN];

		//inital indicies
		readIndex = 0;
		writeIndex = 0;

		packetCounter = new int[BUFLEN];
		triggerData = new int[BUFLEN];

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

	public synchronized void writeSamples(double newData, int pCounter, int trigger)
	{
		/*
        //reorder if 64 channel headset
        if(cogCHS == 64)
        {
                double[] tempSample = new double[64];
                System.arraycopy(newData, 0, tempSample, 0, cogCHS);

                for(int ch=0; ch<cogCHS; ch++)
                {
                    newData[ch] = tempSample[remap[ch]-1];
                }
        }
		 */

		//write packet counter and trigger
		packetCounter[writeIndex] = pCounter;
		triggerData[writeIndex] = trigger;



		//save the raw data
		rawData[writeIndex] = (newData*ADC_TO_VOLTS);

		//remove impedance stimuli
		//push old data out and add latest data point to the 4 point buffer
		last4Samples[0] = last4Samples[1];
		last4Samples[1] = last4Samples[2];
		last4Samples[2] = last4Samples[3];
		last4Samples[3] = rawData[writeIndex];

		//save data with impedance stimuli gone
		//two stage process, IIR notch at fs/4, and FIR average two point   
		rawDataImpFil[writeIndex] = lpf(notchf(rawData[writeIndex]));

		//compute impedance and save into impedance buffer
		double diff1, diff2;
		diff1 = Math.abs(last4Samples[3]-last4Samples[1]);
		diff2 = Math.abs(last4Samples[2]-last4Samples[0]);
		if(diff2>diff1)
		{diff1 = diff2;}
		impData[writeIndex] = diff1*TO_Z;

		//perform high-pass filtering
		if(impCheckOn)
		{
			filData[writeIndex] = hpf(rawDataImpFil[writeIndex]);
		}
		else
		{
			filData[writeIndex] = hpf(rawData[writeIndex]);
		}


		writeIndex++;
		if(writeIndex>=BUFLEN)
		{
			writeIndex = 0;
		}
	}
	
	public synchronized void readSamples(double rawDataOut, double filDataOut, double impDataOut, int[] triggerDataOut, int[] packetDataOut)
	{



		rawDataOut = rawData[readIndex];



		filDataOut = filData[readIndex];


		impDataOut = impData[readIndex];



		if(triggerDataOut!=null)
		{triggerDataOut[0] = triggerData[readIndex];}

		if(packetDataOut!=null)
		{packetDataOut[0] = packetCounter[readIndex];}

		readIndex++;
		if(readIndex>=BUFLEN)
		{
			readIndex=0;
		}

	}
	public synchronized double readSamples() {
		double filDataOut = filData[readIndex];
		readIndex++;
		if(readIndex >= BUFLEN) {
			readIndex = 0;
		}
		return filDataOut;
	}
    
    /*
     * Reads out impedance values, averaged by the specified number of samples
     */
	public synchronized void readImp(double impDataOut, int samplesToAverage)
	{
		double impDataTemp = 0;

		int tempIndex = readIndex;

		for(int j=0; j<samplesToAverage; j++)
		{

			impDataTemp += impData[tempIndex]/((double)samplesToAverage);


			tempIndex--;
			if(tempIndex<0)
			{
				tempIndex = BUFLEN-1;
			}
		}
		impDataOut = impDataTemp;
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
    
    
    private double notch_depth = 0.7;
    
    private double b0 = (1+notch_depth)/2;
    private double b1 = 0;
    private double b2 = (1+notch_depth)/2;
    
    private double a0 = 1;
    private double a1 = 0;
    private double a2 = notch_depth;
    
    
    private double notchf(double newSample)
    {
        //perform notch
        double yout = b0*newSample + b1*xp1_notch + b2*xp2_notch - a1*yp1_notch - a2*yp2_notch;
        
        //update state variables
        xp2_notch = xp1_notch;
        xp1_notch = newSample;
        
        
        
        yp2_notch = yp1_notch;
        yp1_notch = yout;
        
        return yout;
    }
    
    
    //high quality LPF at Fs/2
    private double lpf_depth = 0.6;
    private double d0 = (1+lpf_depth)/2;
    private double d1 = (1+lpf_depth)/2;
    
    private double c0 = 1;
    private double c1 = lpf_depth;
    
    private double lpf(double newSample)
    {
        //perform lpf
        double yout = d0*newSample + d1*xp1_lpf  - c1*yp1_lpf;
        
        //update state variables
        xp1_lpf = newSample;
       
        yp1_lpf = yout;
        
        return yout;
    }
}
