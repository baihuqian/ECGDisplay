package cmu.ece.BaihuQian.DFAUtil;

import java.util.Arrays;

import cmu.ece.BaihuQian.Util.Mathematics;

public class DetrendedFluctuationAnalysis {
	/**
	 * Detrended Fluctuation Analysis
	 * @author Baihu Qian
	 */
	private double [] data;
	private int [] window;
	
	private double HurstExponent;
	
	public DetrendedFluctuationAnalysis() 	{	}
	
	public DetrendedFluctuationAnalysis(double [] data, int window_min, int window_max, int window_num) {
		this.data = data;
		
		// initialize window size array
		this.window = DFAUtility.constructWindow(window_min, window_max, window_num);
		// calculate mean
		double dataMean = Mathematics.mean(data);
		
		// get cumulative sum of input, store in the same array
		for(int i = 0; i < this.data.length; i++) {
			if(i == 0) {
				data[i] = data[i] - dataMean;
			}
			else {
				data[i] = data[i - 1] + data[i] - dataMean;
			}
		}
		// initialize F(n)
		double [] Fn = new double[this.window.length];
		
		// doing DFA
		for(int i = 0; i < this.window.length; i++) { // for each window size
			// parameters
			int numWindow = this.data.length / this.window[i]; // Only the whole number is stored, same as using floor method
			int numData = numWindow * this.window[i];
			double [] x = Mathematics.getIncrementalArray(1, (double) this.window[i], 1);
			
			double [] y_fit   = new double[numData]; // to store fitting data
			double [] y_fit_r = new double[numData]; 
			
			for(int j = 0; j < numWindow; j++) {
				// forward
				int idxStart = j * window[i];
				int idxEnd = (j + 1) * window[i]; // idxEnd is exclusive, so minus by 1 is not necessary
				double [] y = Arrays.copyOfRange(data, idxStart, idxEnd);
				LinearRegression lr = new LinearRegression(x, y);
				double [] tempFit = lr.linearValue();
				for(int k = 0; k < tempFit.length; k++) {
					y_fit[idxStart + k] = tempFit[k];
				}
				// backward
				idxEnd = data.length - j * window[i];
				idxStart = data.length - (j + 1) * window[i];
				y =  Arrays.copyOfRange(data, idxStart, idxEnd);
				lr = new LinearRegression(x, y);
				tempFit = lr.linearValue();
				for(int k = 0; k < tempFit.length; k++) {
					y_fit_r[(numWindow - j - 1) * window[i] + k] = tempFit[k];
				}
			}
			Fn[i] = Mathematics.meanSquareError(Arrays.copyOfRange(data, 0, numData), y_fit) + 
					Mathematics.meanSquareError(Arrays.copyOfRange(data, data.length - numData, data.length), y_fit_r);
		}
		LinearRegression Hlr = new LinearRegression(Mathematics.log2(this.window), Mathematics.log2(Fn));
		this.HurstExponent = Hlr.getSlope();
		
	}

	public double getHurstExponent() {
		return HurstExponent;
	}
	
	
	
}
