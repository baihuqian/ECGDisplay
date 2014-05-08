package cmu.ece.BaihuQian.DFAUtil;

import java.util.LinkedList;

public class MPDFADetection {
	private int numWindow;
	private MPDFAData [] dataBuffer;
	private double [] shortMemoryThreshold, longMemoryThreshold;
	private static double votingThreshold = 0.8;
	private static double shortMemoryWeight = 0.5;
	private static double lowRRThreshold = 0.6366, highRRThreshold = 0.8692;
	
	public MPDFADetection (int numWindow) {
		this.numWindow = numWindow;
		shortMemoryThreshold = new double [numWindow];
		longMemoryThreshold = new double [numWindow];
		for(int i = 0; i < numWindow; i++) {
			shortMemoryThreshold[i] = 0.0073;
			longMemoryThreshold[i] = 0.0059;
		}
	}
	
	public void addData(MPDFAData [] data){
		dataBuffer = data;
		
		processData();
	}
	
	private void processData(){
		EventDetectedInterface.size = 0;
		EventDetectedInterface.eventData = new MPDFAData[dataBuffer.length];
		int k = 0;
		for(int i = 0; i < dataBuffer.length; i++) {
			double count = 0;
			for(int j = 0; j < numWindow; j++) {
				double F = dataBuffer[i].getF(j);
				if (F > shortMemoryThreshold[j]) {
					count += shortMemoryWeight;
				}
				if (F > longMemoryThreshold[j]) {
					count += 1 - shortMemoryWeight;
				}
			}
			if(count >= votingThreshold * numWindow && 
					(dataBuffer[i].getSignal() < lowRRThreshold || dataBuffer[i].getSignal() > highRRThreshold)
					) {
			
				dataBuffer[i].eventDetected();
				EventDetectedInterface.eventFlag = true;
				EventDetectedInterface.eventData[k++] = dataBuffer[i];
			}
			
		}
		EventDetectedInterface.size = k;
	}
}
