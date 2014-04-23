package cmu.ece.BaihuQian.DFAUtil;

public class LinearLine {
	/**
	 * Store results of linear regression and provide polyval method
	 * @author Baihu Qian
	 */
	private double slope;
	private double intercept;
	
	LinearLine() {	}
	LinearLine(double slope, double intercept) {
		this.slope = slope;
		this.intercept = intercept;
	}
	
	public double [] linearValue(double [] x) {
		double [] y = new double[x.length];
		for(int i = 0; i < x.length; i++) {
			y[i] = slope * x[i] + intercept;
		}
		return y;
	}
	public double getSlope() {
		return slope;
	}
	
	
	
}
