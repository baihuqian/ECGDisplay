/**
 * 
 */
package cmu.ece.BaihuQian.DFAUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import cmu.ece.BaihuQian.Util.KernelDensityFunction;

/**
 * @author Baihu Qian
 *
 */
public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		// TODO Auto-generated method stub
		try {
			// read file
			int leng = 90600;
			double [] data = new double[leng];
			int [] windows = {7, 13, 19};
			int i = 0;
			BufferedReader br = new BufferedReader(new FileReader("data.txt"));
			String line = br.readLine();
			while(line != null) {

				double incoming = Double.parseDouble(line);
				data[i++] = incoming;
				if(i == leng) {
					break;
				}
				
				line = br.readLine();
			}
			br.close();
			/*
			double [] initialData = new double [20];
			for(int j = 0; j < initialData.length; j++) {
				initialData[j] = data[j];
			}
			
			MPDFADetection detector = new MPDFADetection(windows.length);
			MPDFA mpdfa = new MPDFA(initialData, windows, detector);
			
			for(int k = 0; k < data.length - initialData.length; k++) {
				mpdfa.addData(new double [] {data[k + initialData.length]});
			}
			*/
			KernelDensityFunction k = new KernelDensityFunction(data, 500);
			KernelDensityFunction.ValuePair [] v = k.getProbabilityDensityFunction();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output.txt")));
			for(int j = 0; j < v.length; j++) {
				writer.write(v[j].getX() + "\t" + v[j].getY() + "\n");
			}
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
	}

}
