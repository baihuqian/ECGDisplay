package cmu.ece.BaihuQian.DFAUtil;

public class LinearRegression {
	/**
	 * This class handles linear regression
	 * It takes a pair of x & y array and produce a linear fit for them
	 * @author Baihu Qian
	 * @version 1
	 */
	private double [] x; // x value
	private double [] y; // y value
	private LinearLine fit; // fitting value
	private boolean dataFlag; // mark if data x & y is set
	private boolean fitFlag; // mark if data is fitted
	
	
	LinearRegression() {	}
	LinearRegression(double [] x, double [] y) {
		/**
		 * Initialize objects with x, y pair
		 * Always use this constructor
		 */
		this.x = x;
		this.y = y;
		this.dataFlag = true; // data is imported
		linearFit();
	}
	
	public void linearFit() {
		/**
		 * Do linear regression after data are imported
		 */
		if(x.length != y.length || !dataFlag) {
			System.err.println("Input x, y is not initialized or of different lengths!");
			//error
		}
		else if(!fitFlag) {
			double x_sum = 0;
			double y_sum = 0;
			double xy_sum = 0;
			double x2_sum = 0;
			for(int i = 0; i < x.length; i++) {
				x_sum += x[i];
				y_sum += y[i];
				xy_sum += x[i] * y[i];
				x2_sum += x[i] * x[i];
			}
			double slope = (xy_sum - x_sum * y_sum / x.length) / (x2_sum - x_sum * x_sum / x.length);
			double intercept = y_sum/y.length - slope * x_sum/x.length;
			fit = new LinearLine(slope, intercept);
			fitFlag = true; // fitting is done
		}
	}
	
	public double [] linearValue() {
		/**
		 * polyval method, call it only after execution of linearFit method
		 * check return value
		 */
		if(fitFlag) {
			return fit.linearValue(x);
		}
		else {
			return null;
		}
	}
	
	public double getSlope() {
		return fit.getSlope();
	}
}
