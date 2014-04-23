package cmu.ece.BaihuQian.SensorComm;

@SuppressWarnings("serial")
public class InsufficientDataFromSDFileException extends Exception {
	private int size;
	
	InsufficientDataFromSDFileException() {	}
	
	InsufficientDataFromSDFileException(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	
}
