package cmu.ece.BaihuQian.DFAUtil;


public class TDFAData implements TDFAConstants{
	//public static int [] windows = {7, 13, 19};

	private double signal;
	private double [] F;
	private boolean detectionResult;
	private int index;
	
	public TDFAData(double signal, int index) {
		this.signal = signal;
		this.index = index;
		F = new double [numWindow];
		detectionResult = false;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getSignal() {
		return signal;
	}

	public void setF(double f, int index) {
		F[index] = f;
	}
	
	public double getF(int index) {
		return F[index];
	}

	public void eventDetected() {
		detectionResult = true;
	}
	public boolean getDetectionResult() {
		return detectionResult;
	}
	
	
}
