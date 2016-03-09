package com.autodetect.parking.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.autodetect.parking.ExtrimValues;
import com.autodetect.parking.ImageType;
import com.fft.Complex;
import com.fft.FFT;

public class FFTServiceImpl implements FFTService{

	private static final int AMP_PIECE = 5;
	private static final String OUT_FF_TDATA_TXT = "outFFTdata.txt";
	private PrintStream ps=null;

	public FFTServiceImpl() {
		try {
	        File outputFile = new File(OUT_FF_TDATA_TXT);
	        if (outputFile.exists()) {
	            outputFile.delete();
	        }
			ps = new PrintStream(new FileOutputStream(outputFile));
		} catch(Exception e) {
			log("PrintStream has not created! "+e);
		}
	}
	
	public Complex[][] createGreyScaleImageData(BufferedImage img) {
    	int h = img.getHeight();
    	int w = img.getWidth();
    	
    	Complex[][] data = new Complex[h][w];
    	/**
    	 * Get image data
    	 */
    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			Color c = new Color(img.getRGB(i, j));
    	        int red = (int)(c.getRed() * 0.21);
    	        int green = (int)(c.getGreen() * 0.72);
    	        int blue = (int)(c.getBlue() *0.07);
    	        int sum = red + green + blue;
    	        data[j][i]=new Complex((double)sum,0);
    		}
    	}		

    	return data;
	}

	public int[] prepareGraphRe(Complex[][] data, int h, int w, ImageType type) {
		int[] graph = new int[GRAPH_LENGTH];

		double max = ExtrimValues.getMaxByImageType(type);
		double min = ExtrimValues.getMinByImageType(type);

    	for(int j =0; j<h; j++) {
    		for(int i=0; i<w; i++) {
    			double d = data[j][i].re();
    			int index= (int)adoptFFTValue(d, max, min).re();
    			int value = graph[index];
    			graph[index]=value + AMP_PIECE;
    		}
    	}
		
		return graph;
	}

	public int[] prepareGraphIm(Complex[][] data, int h, int w, ImageType type ) {
		int[] graph = new int[GRAPH_LENGTH];

		double max = ExtrimValues.getMaxImByImageType(type);
		double min = ExtrimValues.getMinImByImageType(type);

    	for(int j =0; j<h; j++) {
    		for(int i=0; i<w; i++) {
    			double d = data[j][i].im();
    			int index= (int)adoptFFTValue(d, max, min).im();
    			int value = graph[index];
    			graph[index]=value + AMP_PIECE;
    		}
    	}		
		return graph;
	}

	public int[] prepareGraph(Complex[][] data, int h, int w, ImageType type) {
		int[] graphRe = new int[GRAPH_LENGTH];
		int[] graphIm = new int[GRAPH_LENGTH];
		int[] graph = new int[GRAPH_LENGTH];

		double max = ExtrimValues.getMaxByImageType(type);
		double min = ExtrimValues.getMinByImageType(type);
		double max_im = ExtrimValues.getMaxImByImageType(type);
		double min_im = ExtrimValues.getMinImByImageType(type);

    	for(int j =0; j<h; j++) {
    		for(int i=0; i<w; i++) {
    			double d = data[j][i].im();
    			int index= (int)adoptFFTValue(d, max_im, min_im).im();
    			int value = graphIm[index];
    			graphIm[index]=value + AMP_PIECE;

    			d = data[j][i].re();
    			index= (int)adoptFFTValue(d, max, min).re();
    			value = graphRe[index];
    			graphRe[index]=value + AMP_PIECE;    		
    		}
    	}
    	
    	for(int i=0; i<GRAPH_LENGTH; i++) {			
			graph[i] = (int)Math.sqrt(graphRe[i]*graphRe[i] + graphIm[i]*graphIm[i]);
    	}
		return graph;
	}

	public Complex[][] performFFT(BufferedImage img) {   	   	
    	Complex[][] data = createGreyScaleImageData(img);
       	return performFFT(data, img.getHeight(), img.getWidth());
    }

	public Complex[][] performFFT(Complex[][] data, int h, int w) {
    	
    	Complex[] row = new Complex[w];
    	Complex[] column = new Complex[h];

    	/**
    	 * Two-dimensional Fourier Transform 
    	 */
    	//Step 1 - rows processing
    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			row[i]=data[j][i];
    			/**
    			 * Locate the low frequencies to the image center
    			 */
//################
//    			row[i]=new Complex(row[i].re() * Math.pow(-1, (i+j)),0);
//################    			
    		}

       		row = FFT.fft(row);
       		
    		for(int i=0; i<w; i++) {
    			data[j][i]=row[i];
    		}
        }

    	//Step2 - column processing
    	for(int i =0; i<w; i++) {       	
    		for(int j=0; j<h; j++) {
    			column[j]=data[j][i];
    		}
    		
       		column = FFT.fft(column);
       		
    		for(int j=0; j<h; j++) {
    			data[j][i]=column[j];
    		}
    	}

       	return data;
    }

	public Complex[][] performIFFT(Complex[][] fftData, int height, int width ) {
    	
    	int h = height;
    	int w = width;
    	
    	Complex[] row = new Complex[w];
    	Complex[] column = new Complex[h];

    	/**
    	 * Two-dimensional Back Fourier Transform 
    	 */
    	//Step1 - column processing
    	for(int i =0; i<w; i++) {       	

    		for(int j=0; j<h; j++) {
    			column[j]=fftData[j][i];
    		}
    		column = FFT.ifft(column);
    		for(int j=0; j<h; j++) {
    			fftData[j][i]=column[j];
    		}
    	}

    	//Step 2 - rows processing
    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			row[i]=fftData[j][i];
    		}
       		row = FFT.ifft(row);
    		for(int i=0; i<w; i++) {
    			fftData[j][i]=row[i];
    		}
        }

       	return fftData;
    }

	public Complex[][] performConvolve(Complex[][] fftData1, Complex[][] fftData2, int h, int w) {
		Complex[][] data = new Complex[h][w];
		Complex[] row1 = new Complex[w];
		Complex[] row2 = new Complex[w];
		Complex[] row3 = new Complex[w];

    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			row1[i]=fftData1[j][i];
    			row2[i]=fftData2[j][i];
    		}
       		row3 = FFT.convolve(row1,row2);
    		for(int i=0; i<w; i++) {
    			data[j][i]=row3[i];
    		}
        }

		return performIFFT(data, h, w );
	}

	public BufferedImage createImage(Complex[][] data, int height, int weidth) {
		return createImage(data, height, weidth, true);
	}

	public BufferedImage createImage(Complex[][] data, int height, int weidth, boolean isReal) {
		
		BufferedImage img = new BufferedImage(weidth, height, BufferedImage.TYPE_INT_ARGB);
    	for(int j =0; j<height; j++) {       	
    		for(int i=0; i<weidth; i++) {
    			int color = (int) data[j][i].re();
    			if(!isReal) {
    				color*=8;
    			}
    			Color newColor=new Color(255, 255, 255);
    			try {
    				newColor = new Color(color, color, color);
    			} catch (IllegalArgumentException e) {
    				log("Wrong color value=["+color+"]."+e );
    			}
    			img.setRGB(i, j, newColor.getRGB());
    		}
    	}		
		return img;
	}

	public double getMax(Complex[][] fftData, int height, int width) {
		return getMinOrMax(fftData, height, width, true);
	}

	public double getMin(Complex[][] fftData, int height, int width) {
		return getMinOrMax(fftData, height, width, false);
	}

	public double getMaxIm(Complex[][] fftData, int height, int width) {
		return getMinOrMaxIm(fftData, height, width, true);
	}

	public double getMinIm(Complex[][] fftData, int height, int width) {
		return getMinOrMaxIm(fftData, height, width, false);
	}
	
	public void prepareData(Complex[][] data, int h, int w) {
    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			data[j][i]=new Complex(data[j][i].re() * Math.pow(-1, (i+j)),0);
    		}
    	}
	}

	/**
	 * Retrieve Max or Min Value of array
	 * @param fftData input array
	 * @param height 
	 * @param width
	 * @param flag dedine returned value - true (max value will be returned), false (min value will be returned)
	 * @return min or max value of array
	 */
	private double getMinOrMax(Complex[][] data, int height, int width, boolean flag) {
		double value=0;
		
		int h = height;
    	int w = width;
    	
    	Double[] row = new Double[w];

    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			row[i]=data[j][i].re();
    		}

			Arrays.sort(row);

    		if(flag) {
    			if(value < row[w-1]) {
    				value=row[w-1];
    			}
    		} else {
    			if(value >= row[0]) {
    				value=row[0];
    			}    			
    		}
    	}

		return value;
	}

	private double getMinOrMaxIm(Complex[][] data, int height, int width, boolean flag) {
		double value=0;
		
		int h = height;
    	int w = width;
    	
    	Double[] row = new Double[w];

    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			row[i]=data[j][i].im();
    		}

			Arrays.sort(row);

    		if(flag) {
    			if(value < row[w-1]) {
    				value=row[w-1];
    			}
    		} else {
    			if(value >= row[0]) {
    				value=row[0];
    			}    			
    		}
    	}

		return value;
	}

	/**
	 * Create image from fft data
	 */
	public BufferedImage createAdoptedImage(Complex[][] data, int h, int w, ImageType type) {
		Complex[][] newData = new Complex[h][w];

		double max = ExtrimValues.getMaxByImageType(type);
		double min = ExtrimValues.getMinByImageType(type);

    	for(int j =0; j<h; j++) {
    		for(int i=0; i<w; i++) {
    			double d = data[j][i].re();
    			newData[j][i]=adoptFFTValue(d, max, min);
    		}
    	}

		max = getMax(newData, h, w );
		min = getMin(newData, h, w );
		double max1 = getMaxIm(newData, h, w );
		double min1 = getMinIm(newData, h, w );
    	  	
    	log("\nAfter adoptation:");
		log("\nMAX:"+max);
		log("MIN:"+min);
		log("\nMAX-IM:"+max1);
		log("MIN-IM:"+min1);
    	
    	printData(newData, h, w);

    	return createImage(newData, h, w, false);
	}

	private Complex adoptFFTValue(double d, double max, double min ) {	

		if(min < 0) {
			d+=Math.abs(min);
			d=d/(Math.abs(min) + max);
		} else {
			d=d/max;
		}
		
		d=d*(GRAPH_LENGTH-1);
		return new Complex(d,d);
	}

	private double adoptFFTValue(Complex v, double max, double min ) {	

		double d=Math.sqrt((v.re()*v.re()) + (v.im()*v.im()) );

		if(min < 0) {
			d+=Math.abs(min);
			d=d/(Math.abs(min) + max);
		} else {
			d=d/max;
		}
		
		d=d*(GRAPH_LENGTH-1);
		return d;
	}

	public void printData(Complex[][] data, int h, int w) {
		for(int j =0; j<h; j++) {
    		log("\n.::ROW-"+j);
    		for(int i=0; i<w; i++) {
    			printMessage(" Re: "+data[j][i].re());
    			printMessage(" Im: "+data[j][i].im());
    		}
    	}			
	}
	
	public void printData(Complex[][] data, int h, int w, String title) {
		if(title != null && !title.isEmpty()) {
			log("\n"+title);
		}
		printData(data, h, w);
	}

	
	public void log(String message) {
		PrintStream stream=null;
		if(ps == null) {
			stream=System.out;
		} else {
			stream = ps;
		}		
		stream.println(message);
	}

	private void printMessage(String message) {
		PrintStream stream=null;
		if(ps == null) {
			stream=System.out;
		} else {
			stream = ps;
		}		
		stream.print(message);
	}

}
