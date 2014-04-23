/**
 * 
 */
package cmu.ece.BaihuQian.Util;

/**
 * @author Baihu Qian
 *
 */
public final class SignalProcessing {
	public static double [] filter(double [] b, double [] a, double [] in) {
		double [] out = new double[in.length];
		int na = a.length;
		int nb = b.length;
		
		for(int i = 0; i < out.length; i++) {
			out[i] = 0;
			for(int j = 0; j < nb; j++) {
				if(i - j >= 0) {
					out[i] += b[j] * in[i - j];
				}
				
			}
			for(int k = 1; k < na; k++) {
				if(i - k >= 0) {
					out[i] -= a[k] * out[i - k];
				}
			}
			out[i] /= a[0];
		}
		
		
		return out;
	}
}
