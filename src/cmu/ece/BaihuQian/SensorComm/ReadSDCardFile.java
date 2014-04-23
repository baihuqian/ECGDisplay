/**
 * 
 */
package cmu.ece.BaihuQian.SensorComm;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

import cmu.ece.BaihuQian.Util.SignalProcessing;
/**
 * @author Baihu Qian
 *
 */
public final class ReadSDCardFile implements SensorCommConstants {
	/**
	 * Read .COG file with a given file name
	 * @param fileName
	 * @param downsample number of samples to skip
	 * @param start number of actual samples (after downsample) that is skipped
	 * @param size length of data
	 * @return data sequence in double format
	 */
	
	//public static final int SAMPLE_RATE = 500;
	
	public static double [] loadFile(File file, int downsample, double start_in_time, double length_in_time)
			throws InsufficientDataFromSDFileException {
		final double VREF = 2.5;
		final double GAIN = 3;
		//final double Fs = 540;
		//final double ISTIM = 24e-9;
		final int CHS = 3;
		
		int size = (int) Math.floor(length_in_time * SAMPLE_RATE);
		// read data into file
		double [] data = new double[size];
		try {
			int i = 0;
			DataInputStream data_in = new DataInputStream(
		        new BufferedInputStream(
		            new FileInputStream(file)));
			int start = (int) Math.floor(start_in_time * SAMPLE_RATE);
    		data_in.skip(start * 4 * CHS);
			while(true) {
		    	try {
		    		int t = data_in.readInt();
		    		int t1 = little2big(t);
		    		data[i] = little2big(t);
		    		i++;
		    		if(i >= size) {
		    			break;
		    		}
		    		data_in.skip(8); // skip two other channels
		    		//data_in.skip(4 * CHS * (downsample - 1)); // skip downsamples
				} catch(EOFException eof) {
					if(i < size) {
						throw new InsufficientDataFromSDFileException(i);
					} else {
						break;
					}
					
				} 
		    }
		    data_in.close();
		}catch(InsufficientDataFromSDFileException isdfSDfe) {
			throw isdfSDfe;
		}catch (Exception e) {
			
		}
		
		
		//filter data
		double [] b = {0.85, 0, 0.85};
		double [] a = {1, 0, 0.7};
		data = SignalProcessing.filter(b, a, data);
		
		
		double [] bb = {0.8, 0.8};
		double [] aa = {1, 0.6};
		data = SignalProcessing.filter(bb, aa, data);
		
		// scale and downsample data
		int downsample_size = size / downsample;
		double [] out = new double [downsample_size];
		for(int i = 0; i < downsample_size; i++) {
			out[i] = 2 * VREF *data[i * downsample] / (GAIN * Math.pow(2, 24));
			out[i] *= 1000;
		}
		return SmoothFluctuation.smooth(out, downsample);
	}
	
	/**
	 * Binary file stored by sensor is of little Endian
	 * Java reads int in big Endian
	 * Conversion function
	 * @param i
	 * @return
	 */
	public static int little2big(int i) {
	    return((i&0xff)<<24)+((i&0xff00)<<8)+((i&0xff0000)>>8)+((i>>24)&0xff);
	}
}
