package cmu.ece.BaihuQian.DFAUtil;

import cmu.ece.BaihuQian.Util.KernelDensityFunction;
import cmu.ece.BaihuQian.Util.Mathematics;

public class UpdateThreshold implements Runnable {
	private TDFAData [] data;
	private int iter;
	private int numWindow;
	private final int numSamplingPoints = 500;
	private double cutoff = 0.8;
	private double lowThreshold = 0.01, highThreshold = 0.1;

	private double [] threshold;
	private double lowRRThreshold, highRRThreshold;
	private boolean isThresholdReady, isRRThresholdReady;

	public UpdateThreshold() { 	}
	public UpdateThreshold(TDFAData [] data, int numScale, double cutoff) {
		this.data = data;
		this.numWindow = numScale;
		threshold = new double [numScale];
		this.cutoff = cutoff;
		isThresholdReady = false;

	}

	public synchronized double getLowRRThreshold() {
		return lowRRThreshold;
	}
	public synchronized double getHighRRThreshold() {
		return highRRThreshold;
	}
	public synchronized boolean isThresholdReady() {
		return isThresholdReady;
	}
	public synchronized boolean isRRThresholdReady() {
		return isRRThresholdReady;
	}
	public synchronized double[] getThreshold() {
		while(!isThresholdReady) {	}
		return threshold;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

		// set threshold for F
		for(int iScale = 0; iScale < numWindow; iScale++) {
			double [] F = new double [data.length];
			for(int i = 0; i < data.length; i++) {
				F[i] = data[i].getF(iScale);
			}
			KernelDensityFunction memory = new KernelDensityFunction(F, numSamplingPoints);

			threshold[iScale] = 
					getThreshold(memory.getxValue(), memory.getPDFyValue(), memory.getCDFyValue(), cutoff);
		}
		synchronized(this) {
			isThresholdReady = true;
		}

		double [] RR = new double [data.length];
		for(int i = 0; i < data.length; i++) {
			RR[i] = data[i].getSignal();
		}
		KernelDensityFunction RRDistri = new KernelDensityFunction(RR, numSamplingPoints);
		getRRThreshold(RRDistri.getPDFyValue(), RRDistri.getCDFyValue(), RRDistri.getxValue());
		synchronized(this) {
			isRRThresholdReady = true;
		}
	}

	private double getThreshold(double [] xValue, double [] PDF, double []CDF, double cutoff) {
		double [] d = Mathematics.diff(PDF, xValue); // 1st order derivative
		double [] dd = Mathematics.diff(d, xValue); // 2nd order derivative
		boolean [] metric = new boolean[d.length];
		boolean [] metricCum = new boolean[d.length];
		// set up metrics for find function
		for(int i = 0; i < metric.length; i++) {
			metric[i] = Math.abs(d[i]) < 1e-5 && dd[i] > 0;
			metricCum[i] = CDF[i] > cutoff;
		}
		int [] idx = Mathematics.find(metric, -1, true); // find all indices that match the diff metric
		int index = Mathematics.find(metricCum, true); // find the index at cutoff value
		if(idx != null) { // find something
			boolean [] metricIdx = new boolean[idx.length];
			for(int i = 0; i < metricIdx.length; i++) {
				metricIdx[i] = idx[i] > index; // find first index that satisfies both criteria
			}
			int indexTemp = Mathematics.find(metricIdx, true); 
			if(indexTemp != -1) {
				index = idx[indexTemp];
			}
		}
		return xValue[index];
	}

	private void getRRThreshold(double [] PDF, double [] CDF, double [] xValue) {

		double [] d = Mathematics.diff(PDF, xValue);
		double [] dd = Mathematics.diff(d, xValue);
		int maxIndex = Mathematics.maxIndex(PDF);
		int offset = maxIndex + 1;
		boolean [] lmetric = new boolean[maxIndex]; // 0 -> maxIndex - 1
		boolean [] rmetric = new boolean[d.length - offset]; // maxIndex + 1 -> length - 1(end)
		boolean [] metricCumLow = new boolean[d.length];
		boolean [] metricCumHigh = new boolean[d.length];
		for(int i = 0; i < lmetric.length; i++) {
			lmetric[i] = (Math.abs(d[i]) < 1e-5) && dd[i] > 0;
		}
		for(int i = 0; i < rmetric.length; i ++) {
			rmetric[i] = (Math.abs(d[i + offset]) < 1e-5) && dd[i + offset] < 0;
		}

		
		// low threshold
		for(int i = 0; i < metricCumLow.length; i++) {
			metricCumLow[i] = CDF[i] >= lowThreshold;
			metricCumHigh[i] = CDF[i] >= highThreshold;
		}
		int [] lindexSet = Mathematics.find(lmetric, -1, false);
		int lindex = Mathematics.find(metricCumLow, false);
		int boundary = Mathematics.find(metricCumHigh, true); // smallest index that contains at least highThreshold prob.

		if(lindexSet != null) {// find something
			boolean [] metriclindex = new boolean[lindexSet.length];
			for(int i = 0; i < lindexSet.length; i++) {
				metriclindex[i] = lindexSet[i] >= boundary; // exceed boundary
			}
			int indexTemp = Mathematics.find(metriclindex, false); 
			if(indexTemp != -1) {
				lindex = lindexSet[indexTemp];
			}
		}
		synchronized(this) {
			lowRRThreshold = xValue[lindex];
		}
		//high threshold
		for(int i = 0; i < metricCumLow.length; i++) {
			metricCumLow[i] = CDF[i] <= 1 - lowThreshold;
			metricCumHigh[i] = CDF[i] <= 1 - highThreshold;
		}
		int [] rindexSet = Mathematics.find(rmetric, -1, true);
		int rindex = Mathematics.find(metricCumHigh, true);
		boundary = Mathematics.find(metricCumHigh, true); // smallest index that contains at least 1 - highThreshold prob.
		
		if(rindexSet != null) {
			boolean [] metricrindex = new boolean[rindexSet.length];
			for(int i = 0; i < rindexSet.length; i++) {
				metricrindex[i] = rindexSet[i] <= boundary; // exceed boundary
			}
			int indexTemp = Mathematics.find(metricrindex, true);
			if(indexTemp != -1) {
				rindex = rindexSet[indexTemp] + offset;
			}
		}
		synchronized(this) {
			highRRThreshold = xValue[rindex];

		}
	}
}
