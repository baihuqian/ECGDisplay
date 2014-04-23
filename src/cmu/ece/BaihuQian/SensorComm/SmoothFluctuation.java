package cmu.ece.BaihuQian.SensorComm;

import cmu.ece.BaihuQian.Util.Mathematics;

public final class SmoothFluctuation implements SensorCommConstants{
	
	public static final double [] smooth(double [] data, int downsample_rate) {
		int sample_rate = SAMPLE_RATE / downsample_rate;
		double [] trend = new double[data.length];
		for(int i = sample_rate / 2; i < data.length - sample_rate / 2; i++) {
			double [] avg_data = new double[sample_rate];
			int offset = i - sample_rate / 2;
			for(int j = 0; j < sample_rate; j++) {
				avg_data[j] = data[offset + j];
			}
			trend[i] = Mathematics.mean(avg_data);
		}
		for(int i = 0; i < data.length; i++) {
			if(i < sample_rate / 2) {
				data[i] -= trend[sample_rate / 2];
			}
			else if(i >= data.length - sample_rate / 2) {
				data[i] -= trend[data.length - sample_rate / 2 - 1];
			}
			else {
				data[i] -= trend[i];
			}
		}
		return data;
	}
}
