/**
 * 
 */
package cmu.ece.BaihuQian.Util;

import java.util.Arrays;

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

	public static double std(int [] in) {
		double sum = 0;
		double squareSum = 0;
		for(int i : in) {
			sum += i;
			squareSum += i * i;
		}
		sum /= in.length;
		squareSum /= in.length;
		return Math.sqrt(squareSum - Math.pow(sum, 2));
	}



	public static double std(double [] in) {
		double sum = 0;
		double squareSum = 0;
		for(double i : in) {
			sum += i;
			squareSum += i * i;
		}
		sum /= in.length;
		squareSum /= in.length;
		return Math.sqrt(squareSum - Math.pow(sum, 2));
	}

	public static double [] derivative(double [] in) {
		double [] out = new double [in.length];

		out[0] = in[0];
		for(int i = 1; i < in.length; i++) {
			out[i] = in[i] - in[i - 1]; 
		}
		return out;
	}

	public static double [] cumsum(double [] in) {
		double [] out = new double [in.length];

		out[0] = in[0];
		for(int i = 1; i < in.length; i++) {
			out[i] = in[i] + out[i - 1]; 
		}
		return out;
	}

	public static int [] find(boolean [] metric, int num, boolean first) {
		
		int index = first ? 0 : metric.length - 1;
		int end = first ? metric.length :  -1;
		int increment = first? 1 : -1;
		if(num == -1) { // find as many as possible
			int [] out = new int [metric.length];
			int count = 0;
			for(; index != end; index += increment) {
				if(metric[index]) {
					out[count++] = index;
				}
			}
			return Arrays.copyOf(out, count);
		}
		else {
			int [] out = new int [num];
			int count = 0;
		while(count < num) {
			if(metric[index]) {
				out[count++] = index;
			}
			index += increment;
			if(index == end) {
				break;
			}
		}
		if(count == num) {
			return out;
		}
		else {
			return null;
		}
		}
	}

	public static int find(boolean [] metric, boolean first) {
		int [] result = find(metric, 1, first);
		if(result == null) {
			return -1;
		}
		else {
			return result[0];
		}
	}

	public static double median(double [] in) {
		double [] data = Arrays.copyOf(in, in.length);
		Arrays.sort(data);

		if(data.length % 2 == 1) {
			return data[(data.length - 1) / 2];
		}
		else {
			return (data[data.length / 2] + data[data.length / 2 - 1]) / 2;
		}
	}

	public static double [] abs(double [] in) {
		double [] out = new double [in.length];
		for(int i = 0; i < in.length; i++) {
			out[i] = Math.abs(in[i]);
		}
		return out;
	}
	
	public static double [] diff(double [] in) {
		double [] out = new double [in.length];
		out[0] = in[0];
		for(int i = 1; i < in.length; i++) {
			out[i] = in[i] - in[i - 1];
		}
		return out;
	}
}
