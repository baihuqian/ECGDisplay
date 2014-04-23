/**
 * 
 */
package cmu.ece.BaihuQian.DFAUtil;

/**
 * @author Baihu Qian
 *
 */
public final class DFAUtility {
	public static int [] constructWindow(int window_min, int window_max, int window_num) {
		if(window_num > (window_max - window_min)) {
			window_num = window_max - window_min;
		}
		int [] window = new int[window_num];
		double logWindowMin = java.lang.Math.log(window_min);
		double logWindowMax = java.lang.Math.log(window_max);
		for(int i = 0; i < window.length; i++) {
			window[i] = (int) java.lang.Math.round(
					java.lang.Math.exp((logWindowMax - logWindowMin) / (window_num - 1) * i + logWindowMin));
		}
		return window;
	}
}
