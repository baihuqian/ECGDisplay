/**
 * 
 */
package cmu.ece.BaihuQian.DFAUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Baihu Qian
 *
 */
public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// read file
			double [] data = new double[500];
			int [] windows = {7, 13, 19};
			int i = 0;
			BufferedReader br = new BufferedReader(new FileReader("data.txt"));
			String line = br.readLine();
			while(line != null) {

				double incoming = Double.parseDouble(line);
				data[i++] = incoming;

				
				line = br.readLine();
			}
			br.close();
			double [] initialData = new double [20];
			for(int j = 0; j < initialData.length; j++) {
				initialData[j] = data[j];
			}
			
			MPDFADetection detector = new MPDFADetection(windows.length);
			MPDFA mpdfa = new MPDFA(initialData, windows, detector);
			
			for(int k = 0; k < data.length - initialData.length; k++) {
				mpdfa.addData(new double [] {data[k + initialData.length]});
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
