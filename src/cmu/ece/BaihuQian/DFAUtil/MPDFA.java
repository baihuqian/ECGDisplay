package cmu.ece.BaihuQian.DFAUtil;

import java.util.LinkedList;

import cmu.ece.BaihuQian.Util.Mathematics;

public class MPDFA {
	private int [] windows;
	private LinkedList<MPDFAData> dataBuffer;
	private int [] index;
	private static int loc = 0;
	private MPDFADetection detector;
	private static double offset = 0;
	private double last = 0;
	public MPDFA() {	}
	public MPDFA(double [] signal, int [] idx, int [] windows, MPDFADetection detector) {
		this.windows = windows;
		this.dataBuffer = new LinkedList<MPDFAData>();
		index = new int [windows.length];
		for(int i = 0; i < index.length; i++) {
			index[i] = 0;
		}
		setDetector(detector);
		addData(signal, idx);
	}
	
	public void setDetector(MPDFADetection detector) {
		this.detector = detector;
	}
	public void addData(double [] signal, int [] idx) {
		for(int i = 0; i < signal.length; i++) {
			MPDFAData tmpData = new MPDFAData(signal[i], idx[i]);
			dataBuffer.add(tmpData);
		}
		loc += signal.length;
		processData();
	}
	
	private void processData() {
		for(int i = 0; i < windows.length; i++) {
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
		MPDFAData [] MPDFAPayload = new MPDFAData[minIndex];
		for(int i = 0; i < minIndex; i++) {
			MPDFAPayload[i] = dataBuffer.pop();
		}
		if(detector != null) {
			detector.addData(MPDFAPayload);
		}
		for(int i = 0; i < index.length; i++) {
			index[i] -= minIndex;
		}
	}
}
