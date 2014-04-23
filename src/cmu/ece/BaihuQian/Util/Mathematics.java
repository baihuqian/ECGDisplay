/**
 * 
 */
package cmu.ece.BaihuQian.Util;

//import java.lang.Math;

/**
 * @author Baihu Qian
 * Modified methods based on java.lang.Math
 */
public final class Mathematics {
	public static double log2(double x) {
		return java.lang.Math.log10(x) / java.lang.Math.log10(2);
	}
	
	public static double log2(int x) {
		return (double) (java.lang.Math.log10(x) / java.lang.Math.log10(2));
	}
	
	public static double log2(float x) {
		return (double) (java.lang.Math.log10(x) / java.lang.Math.log10(2));
	}
	
	public static double [] log2(double [] x) {
		double [] returnValue = new double[x.length];
		for(int i = 0; i < x.length; i++) {
			returnValue[i] = log2(x[i]);
		}
		return returnValue;
	}
	
	public static double [] log2(float [] x) {
		double [] returnValue = new double[x.length];
		for(int i = 0; i < x.length; i++) {
			returnValue[i] = log2(x[i]);
		}
		return returnValue;
	}
	
	public static double [] log2(int [] x) {
		double [] returnValue = new double[x.length];
		for(int i = 0; i < x.length; i++) {
			returnValue[i] = log2(x[i]);
		}
		return returnValue;
	}
	
	public static double mean(double [] x) {
		double xSum = 0;
		for(int i = 0; i < x.length; i++) {
			xSum += x[i];
		}
		return xSum / x.length;
	}
	
	public static double mean(float [] x) {
		double xSum = 0;
		for(int i = 0; i < x.length; i++) {
			xSum += x[i];
		}
		return xSum / x.length;
	}
	public static double mean(int [] x) {
		double xSum = 0;
		for(int i = 0; i < x.length; i++) {
			xSum += x[i];
		}
		return xSum / x.length;
	}
	public static double [] getIncrementalArray(double start, double end, double interval) {
		int size = (int) java.lang.Math.floor((end - start) / interval) + 1;
		double [] returnValue = new double[size];
		for(int i = 0; i < size; i++) {
			returnValue[i] = start + i * interval;
		}
		return returnValue;
	}
	public static double [] getIncrementalArray(float start, float end, float interval) {
		int size = (int) java.lang.Math.floor((end - start) / interval) + 1;
		double [] returnValue = new double[size];
		for(int i = 0; i < size; i++) {
			returnValue[i] = start + i * interval;
		}
		return returnValue;
	}
	
	public static int [] getIncrementalArray(int start, int end, int interval) {
		int size = (start - end) / interval;
		int [] returnValue = new int[size];
		for(int i = 0; i < size; i++) {
			returnValue[i] = start + i * interval;
		}
		return returnValue;
	}
	
	public static double meanSquareError(double [] y, double [] y_fit) {
		if(y.length != y_fit.length) {
			// error
			System.err.println("Input vector length mismatch!");
			return -1;
		}
		else {
			double sumSquareError = 0;
			for(int i = 0; i < y.length; i++) {
				sumSquareError += Math.pow((y[i] - y_fit[i]), 2);
			}
			return Math.sqrt(sumSquareError / (double) y.length);
		}
	}
	public static double meanSquareError(float [] y, float [] y_fit) {
		if(y.length != y_fit.length) {
			// error
			System.err.println("Input vector length mismatch!");
			return -1;
		}
		else {
			float sumSquareError = 0;
			for(int i = 0; i < y.length; i++) {
				sumSquareError += Math.pow((y[i] - y_fit[i]), 2);
			}
			return Math.sqrt(sumSquareError / (double) y.length);
		}
	}
	
	public static int max(int [] in) {
		int returnValue = in[0];
		for(int i = 1; i < in.length; i++) {
			if(in[i] > returnValue) {
				returnValue = in[i];
			}
		}
		return returnValue;
	}
	
	public static double max(double [] in) {
		double returnValue = in[0];
		for(int i = 1; i < in.length; i++) {
			if(in[i] > returnValue) {
				returnValue = in[i];
			}
		}
		return returnValue;
	}
	
	public static int min(int [] in) {
		int returnValue = in[0];
		for(int i = 1; i < in.length; i++) {
			if(in[i] < returnValue) {
				returnValue = in[i];
			}
		}
		return returnValue;
	}
	
	public static double min(double [] in) {
		double returnValue = in[0];
		for(int i = 1; i < in.length; i++) {
			if(in[i] < returnValue) {
				returnValue = in[i];
			}
		}
		return returnValue;
	}
}
