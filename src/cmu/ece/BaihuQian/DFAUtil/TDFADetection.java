package cmu.ece.BaihuQian.DFAUtil;

import java.util.LinkedList;

public class TDFADetection implements TDFAConstants{
	//private int numWindow;
	private TDFAData [] dataBuffer;
	private LinkedList<TDFAData> buffer;
	private int thresholdUpdateCount;
	
	private double [] shortMemoryThreshold, longMemoryThreshold;
	private static double shortMemoryCutoff = 0.85, longMemoryCutoff = 0.9;
	private static double votingThreshold = 0.8;
	private static double shortMemoryWeight = 0.5;
	private static int updateInterval = 100;
	private static double lowRRThreshold = 0.6366, highRRThreshold = 0.8692;
	
	public TDFADetection () {
		//this.numWindow = numWindow;
		thresholdUpdateCount = 0;
		//shortMemoryThreshold = new double [numWindow];
		//longMemoryThreshold = new double [numWindow];
		//initializeData(setupData);
	}
	
	public void initializeData(TDFAData [] data) {
		UpdateThreshold shortTime = new UpdateThreshold(data, numWindow, shortMemoryCutoff);
		UpdateThreshold longTime = new UpdateThreshold(data, numWindow, longMemoryCutoff);
		new Thread(shortTime).start();
		new Thread(longTime).start();
		
		shortMemoryThreshold = shortTime.getThreshold();
		longMemoryThreshold = longTime.getThreshold();
		for(int i = updateInterval; i < data.length; i++) {
			buffer.add(data[i]);
		}
	}
	
	public void addData(TDFAData [] data){
		for(TDFAData d: data) {
			buffer.add(d);
		}
		dataBuffer = data;
		thresholdUpdateCount += data.length;
		if(thresholdUpdateCount >= updateInterval) {
			TDFAData [] storedData = (TDFAData []) buffer.toArray();
			UpdateThreshold shortTime = new UpdateThreshold(storedData, numWindow, shortMemoryCutoff);
			UpdateThreshold longTime = new UpdateThreshold(storedData, numWindow, longMemoryCutoff);
			new Thread(shortTime).start();
			new Thread(longTime).start();
			
			shortMemoryThreshold = shortTime.getThreshold();
			longMemoryThreshold = longTime.getThreshold();
		}
		processData();
	}
	
	private void processData(){
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
				EventDetectedInterface.eventData = dataBuffer[i];
			}
			EventDetectedInterface.data = dataBuffer[i];
		}
	}
}
