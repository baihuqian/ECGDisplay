/**
 * 
 */
package cmu.ece.BaihuQian.DFAUtil;

import java.util.ArrayList;
import java.util.LinkedList;

import cmu.ece.BaihuQian.Util.Mathematics;

/**
 * @author Baihu Qian
 *
 */
public class ProgressiveDFA {
	private int [] windows;
	private double [] P_n;
	private ArrayList<Double> [] diffP_n;
	private LinkedList<Double> dataBuffer;
	private int [] index; // first number unvisited
	
	public ProgressiveDFA() {	}
	public ProgressiveDFA(int [] window) {
		this.windows = window;
		this.P_n = new double [this.windows.length];
		this.index = new int [this.windows.length];
		this.diffP_n = new ArrayList[this.windows.length];
		this.dataBuffer = new LinkedList<Double>();
		for(int i = 0; i < this.index.length; i++) {
			this.index[i] = 0;
			this.diffP_n[i] = new ArrayList<Double>();
		}
	}
	
	public ProgressiveDFA(int [] window, double [] initialData) {
		this(window);
		addData(initialData);
	}
	public ArrayList<Double> getDiffP_n(int index) {
		return diffP_n[index];
	}
	
	public boolean isEmpty() {
		return (dataBuffer.size() == 0);
	}
	
	public void addData(double [] data) {
		for(int i = 0; i < data.length; i++) {
			dataBuffer.add(data[i]);
		}
		processData();
	}
	
	private void processData() {
		for(int i = 0; i < windows.length; i++) {
			int startIndex = index[i];
			// dataBuffer.size() - startIndex: length of unprocessed data
			int numIter = (dataBuffer.size() - startIndex) / windows[i];
			for(int j = 0; j < numIter; j++) {
				calculate(i, startIndex + windows[i] * j);
			}
			index[i] += windows[i] * numIter; //startIndex + window * number-of-window-forwarded
		}
		removeData();
	}
	
	private void calculate(int windowIndex, int idx) {
		//ListIterator<Double> iter = dataBuffer.listIterator(idx);
		int windowSize= windows[windowIndex];
		double [] windowData = new double [windowSize];
		for(int i = 0; i < windowSize; i++, idx++) {
			windowData[i] = dataBuffer.get(idx);
		}
		double [] x = Mathematics.getIncrementalArray(1, (double) windowSize, 1);
		
		LinearRegression lr = new LinearRegression(x, windowData);
		double [] tempFit = lr.linearValue();
		
		double Pn = Math.pow(P_n[windowIndex], 2);
		for(int i = 0; i < windowSize; i++) {
			Pn += Math.pow((windowData[i] - tempFit[i]), 2);
		}
		Pn = Math.sqrt(Pn);
		diffP_n[windowIndex].add(Pn - P_n[windowIndex]);
		P_n[windowIndex] = Pn;
	}
	
	private void removeData() {
		int minIndex = Mathematics.min(index); // index of earliest unprocessed number, aka number of data removed
		for(int i = 0; i < minIndex; i++) {
			dataBuffer.remove();
		}
		for(int i = 0; i < index.length; i++) {
			index[i] -= minIndex;
		}
	}
}
