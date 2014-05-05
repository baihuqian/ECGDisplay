package cmu.ece.BaihuQian.Util;

import java.util.Arrays;


public class KernelDensityFunction {
	private static final double constant = 1.0 / Math.sqrt(2 * Math.PI);
	private static final double kernelCut = 3.0;

	private ValuePair[] densityFunction;
	private int numPoints;
	private ValuePair[] cumulativeFunction;
	private double [] data;
	private boolean isDensityCalculated, isCumulativeCalculated;
	private double minRange, maxRange, step;

	public KernelDensityFunction() {	}
	public KernelDensityFunction(double [] data, int numPoints) {
		this.data = data;
		this.numPoints = numPoints;
		isDensityCalculated = false;
		isCumulativeCalculated = false;

		densityFunction = new ValuePair[numPoints];
		cumulativeFunction = new ValuePair[numPoints];
		/*
		minRange = Mathematics.min(data) - 3;
		maxRange = Mathematics.max(data) + 3;
		step = (maxRange - minRange) / (numPoints - 1);
		for(int i = 0; i < numPoints; i++) {
			densityFunction[i] = new ValuePair(minRange + step * i, 0);
			cumulativeFunction[i] = new ValuePair(minRange + step * i, 0);
		}
	*/

	}

	public ValuePair[] getProbabilityDensityFunction() {
		if(!isDensityCalculated) {
			double med = Mathematics.median(data);
			double [] data_med = new double [data.length];
			double h = 0;
			for(int i = 0; i < data.length; i++) {
				data_med[i]= data[i] - med;
			}
			double sigma = Mathematics.median(Mathematics.abs(data_med)) / 0.6745;
			if(sigma <= 0) {
				sigma = Mathematics.max(data) - Mathematics.min(data);
			}
			if(sigma > 0) {
				h = sigma * Math.pow(4.0 / (3.0 * data.length), 0.2);
			}
			else {
				h = 1;
			}
			minRange = Mathematics.min(data) - kernelCut * h;
			maxRange = Mathematics.max(data) + kernelCut * h;
			
			step = (maxRange - minRange) / (numPoints - 1);
			for(int i = 0; i < numPoints; i++) {
				densityFunction[i] = new ValuePair(minRange + step * i, 0);
				cumulativeFunction[i] = new ValuePair(minRange + step * i, 0);
			}
			
			/*
			int range = (int) Math.ceil(step / 3);
			for(double dataPoint : data) {
				int leftMostIndex = (int) Math.floor((dataPoint - minRange) / step) - range;
				int rightMostIndex = leftMostIndex + range * 2 + 1;
				if(leftMostIndex < 0) {
					leftMostIndex = 0;
				}
				if(rightMostIndex >= numPoints) {
					rightMostIndex = numPoints - 1;
				}
				for(int i = leftMostIndex; i <= rightMostIndex; i++) {
					densityFunction[i].setY(1.0 / ((double) data.length * h) * kernelFunction((densityFunction[i].getX() - dataPoint) / h));
				}
			}
			double sum = 0;
			for(int i = 0; i < numPoints; i++) {
				sum += densityFunction[i].getY();
			}
			for(int i = 0; i < numPoints; i++) {
				densityFunction[i].setY(densityFunction[i].getY() / sum);
			}
			
			*/
			Arrays.sort(data);
			int jstart = 0, jend = 0;
			double halfwidth = kernelCut * h;
			for(int i = 0; i < numPoints; i++) {
				double x = densityFunction[i].getX();
				double low = x - halfwidth;
				while(data[jstart] < low && jstart < data.length - 1) {
					jstart++;
				}
				double high = x + halfwidth;
				jend = Math.max(jend, jstart);
				while(data[jend] <= high && jend < data.length - 1) {
					jend++;
				}
				double sum = 0;
				for(int k = jstart; k <= jend; k++) {
					double z = (x - data[k]) / h;
					sum += kernelFunction(z);
					
				}
				densityFunction[i].setY(sum / ((double) data.length * h));
			}
			double sum = 0;
			for(int i = 0; i < numPoints; i++) {
				sum += densityFunction[i].getY();
			}
			for(int i = 0; i < numPoints; i++) {
				densityFunction[i].setY(densityFunction[i].getY() / sum);
			}
			isDensityCalculated = true;
		}
		return densityFunction;
	}

	public ValuePair [] getCumulativeDensityFunction() {
		if(!isDensityCalculated) {
			getProbabilityDensityFunction();
		}
		if(!isCumulativeCalculated) {
			cumulativeFunction[0].setY(densityFunction[0].getY());
			for(int i = 1; i < numPoints; i++) {
				cumulativeFunction[i].setY(densityFunction[i].getY() + cumulativeFunction[i - 1].getY());
			}
			isCumulativeCalculated = true;
		}
		return cumulativeFunction;
	}

	private double kernelFunction(double in) {
		return constant * Math.exp(-0.5 * Math.pow(in, 2));
	}
	public class ValuePair{
		private double x;
		private double y;

		ValuePair() {}
		ValuePair(double x, double y) {
			this.x = x;
			this.y = y;
		}
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}

	}
}
