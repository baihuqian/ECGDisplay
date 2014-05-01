package cmu.ece.BaihuQian.Util;


public class KernelDensityFunction {
	private static final double constant = 1.0 / Math.sqrt(2 * Math.PI);

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
		minRange = Mathematics.min(data) - 3;
		maxRange = Mathematics.max(data) + 3;
		step = (maxRange - minRange) / (numPoints - 1);
		for(int i = 0; i < numPoints; i++) {
			densityFunction[i] = new ValuePair(minRange + step * i, 0);
			densityFunction[i] = new ValuePair(minRange + step * i, 0);
		}


	}

	public ValuePair[] getProbabilityDensityFunction() {
		if(!isDensityCalculated) {
			double sigma = Mathematics.std(data);
			double h = 1.06 * sigma * Math.pow(data.length, -0.2);

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
					densityFunction[i].setY(kernelFunction((densityFunction[i].getX() - dataPoint) / h));
				}
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
		if(isCumulativeCalculated) {
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
