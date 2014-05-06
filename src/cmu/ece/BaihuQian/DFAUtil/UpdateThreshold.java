package cmu.ece.BaihuQian.DFAUtil;

import cmu.ece.BaihuQian.Util.KernelDensityFunction;
import cmu.ece.BaihuQian.Util.Mathematics;

public class UpdateThreshold implements Runnable {
	private TDFAData [] data;
	private int iter;
	private int numWindow;
	private final int numSamplingPoints = 500;
	private double cutoff = 0.8;

	private double [] threshold;
	private boolean isResultReady;

	public UpdateThreshold() { 	}
	public UpdateThreshold(TDFAData [] data, int numScale, double cutoff) {
		this.data = data;
		this.numWindow = numScale;
		threshold = new double [numScale];
		this.cutoff = cutoff;
		isResultReady = false;
	}

	public double[] getThreshold() {
		while(!isResultReady) {	}
		return threshold;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int iScale = 0; iScale < numWindow; iScale++) {
			double [] F = new double [data.length];
			for(int i = 0; i < data.length; i++) {
				F[i] = data[i].getF(iScale);
			}
			KernelDensityFunction memory = new KernelDensityFunction(F, numSamplingPoints);

			threshold[iScale] = 
					getThreshold(memory.getxValue(), memory.getPDFyValue(), memory.getCDFyValue(), cutoff);
		}
		isResultReady = true;
	}

	private double getThreshold(double [] xValue, double [] PDF, double []CDF, double cutoff) {
		double [] d = Mathematics.diff(PDF); // 1st order derivative
		double [] dd = Mathematics.diff(d); // 2nd order derivative
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
			int indexTemp = Mathematics.find(metricCum, true); 
			if(indexTemp != -1) {
				index = indexTemp;
			}
		}
		return xValue[index];
	}

}
