package cmu.ece.BaihuQian.DFAUtil;

import java.util.LinkedList;

import cmu.ece.BaihuQian.Util.Mathematics;

/**
 * Calculate F value
 * @author bqian
 *
 */
public class TDFA implements TDFAConstants{
	//private int [] windows;
	private LinkedList<TDFAData> dataBuffer;
	private int [] index;
	private static int loc = 0;
	private TDFADetection detector;
	private static double offset = 0;
	private double last = 0;
<<<<<<< HEAD:src/cmu/ece/BaihuQian/DFAUtil/TDFA.java
	private boolean isFirst;
	public TDFA() {	}
	public TDFA(double [] signal, TDFADetection detector) {
		isFirst = true;
		//this.windows = windows;
		this.dataBuffer = new LinkedList<TDFAData>();
		index = new int [numWindow];
=======
	public MPDFA() {	}
	public MPDFA(double [] signal, int [] idx, int [] windows, MPDFADetection detector) {
		this.windows = windows;
		this.dataBuffer = new LinkedList<MPDFAData>();
		index = new int [windows.length];
>>>>>>> demo_version:src/cmu/ece/BaihuQian/DFAUtil/MPDFA.java
		for(int i = 0; i < index.length; i++) {
			index[i] = 0;
		}
		setDetector(detector);
		addData(signal, idx);
	}
	
	public void setDetector(TDFADetection detector) {
		this.detector = detector;
	}
	public void addData(double [] signal, int [] idx) {
		EventDetectedInterface.eventFlag = false;
		for(int i = 0; i < signal.length; i++) {
<<<<<<< HEAD:src/cmu/ece/BaihuQian/DFAUtil/TDFA.java
			TDFAData tmpData = new TDFAData(signal[i], i + loc);
=======
			MPDFAData tmpData = new MPDFAData(signal[i], idx[i]);
>>>>>>> demo_version:src/cmu/ece/BaihuQian/DFAUtil/MPDFA.java
			dataBuffer.add(tmpData);
		}
		loc += signal.length;
		processData();
	}
	
	private void processData() {
		for(int i = 0; i < numWindow; i++) {
			int startIndex = index[i];
			// dataBuffer.size() - startIndex: length of unprocessed data
			int numIter = (dataBuffer.size() - startIndex) / windows[i];
			for(int j = 0; j < numIter; j++) {
				calculate(i, startIndex + windows[i] * j);
			}
			index[i] += windows[i] * numIter; //startIndex + window * number-of-window-forwarded
		}
		removeData();
	}
	
	private void calculate(int windowIndex, int idx) {
		//ListIterator<Double> iter = dataBuffer.listIterator(idx);
		int windowSize= windows[windowIndex];
		double [] windowData = new double [windowSize];
		for(int i = 0; i < windowSize; i++) {
			if(i == 0) { // cumulative sum
				windowData[i] = dataBuffer.get(idx + i).getSignal() + last - offset;
			}
			else {
				windowData[i] = dataBuffer.get(idx + i).getSignal() + windowData[i - 1] - offset;
			}
		}
		double [] x = Mathematics.getIncrementalArray(1, (double) windowSize, 1);
		
		LinearRegression lr = new LinearRegression(x, windowData);
		double [] tempFit = lr.linearValue();
		
		double F = 0;
		for(int i = 0; i < windowSize; i++) {
			F += Math.pow((windowData[i] - tempFit[i]), 2);
		}
		for(int i = 0; i < windowSize; i++) {
			dataBuffer.get(idx + i).setF(F, windowIndex);
			//dataBuffer.get(idx + i).setFlag();
		}
		
	}
	
	private void removeData() {
		int minIndex = Mathematics.min(index); // index of earliest unprocessed number, aka number of data removed
		TDFAData [] MPDFAPayload = new TDFAData[minIndex];
		for(int i = 0; i < minIndex; i++) {
			MPDFAPayload[i] = dataBuffer.pop();
		}
		if(detector != null) {
			if(isFirst) {
				detector.initializeData(MPDFAPayload);
				isFirst = false;
			}
			else {
				detector.addData(MPDFAPayload);
			}
		}
		for(int i = 0; i < index.length; i++) {
			index[i] -= minIndex;
		}
	}
}
