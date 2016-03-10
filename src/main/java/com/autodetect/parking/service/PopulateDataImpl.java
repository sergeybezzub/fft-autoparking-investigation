package com.autodetect.parking.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.autodetect.parking.ImageType;
import com.fft.Complex;

public class PopulateDataImpl implements PopulateData {
	
	private final static Map<String,Complex[][]> fftData = new HashMap<String,Complex[][]>();
	private final static Map<String,Complex[][]> grayScaleData = new HashMap<String,Complex[][]>();
	private final static Map<String,Complex[][]> color24Data = new HashMap<String,Complex[][]>();
	private final static Map<String,Complex[][]> colorHSVData = new HashMap<String,Complex[][]>();
	private final static Map<String,Integer[]> graphData = new HashMap<String,Integer[]>();

	FFTService fftService = new FFTServiceImpl();
	
	@Override
	public BufferedImage populateImageData(String path, int id, ImageType type) {
		switch(type) {
			case GRAYSCALE:
				return populateGrayscaleImageData(path,id);
			case RGB24:
				return populateRGB24ImageData(path,id);
			case HSV:
				return populateHSVImageData(path,id);
			default:
				return populateGrayscaleImageData(path,id);
		}
	}

	public BufferedImage populateGrayscaleImageData(String path, int id) {
		BufferedImage img=null;
		try {
			img = loadImage(path);
			Complex[][] data = fftService.createGreyScaleImageData(img);
			grayScaleData.put(""+id, data);
//			fftService.printData(data,  img.getHeight(), img.getWidth(), "IMG"+id);
			int w= img.getWidth();
			int h = img.getHeight();
			img = fftService.createImage(data, h, w);

		} catch (IOException e) {
			System.err.println("Populate image data has failed! "+e.toString());
		}
		return img;
	}

	public BufferedImage populateRGB24ImageData(String path, int id) {
		BufferedImage img=null;
		try {
			img = loadImage(path);
			
		   	int h = img.getHeight();
	    	int w = img.getWidth();
	    	
					
			Complex[][] data = new Complex[h][w];
			
	    	for(int j =0; j<h; j++) {       	
	    		for(int i=0; i<w; i++) {
	    			data[j][i]=new Complex((img.getRGB(i, j) & 0xffffff), 0);
	    		}
	    	}
			
	    	color24Data.put(""+id, data);
		} catch (IOException e) {
			System.err.println("Populate RGB24 image data has failed! "+e.toString());
		}
		return img;
	}
	
	public BufferedImage populateHSVImageData(String path, int id) {
		BufferedImage img=null;
		try {
			img = loadImage(path);
			
			Complex[][] data = convertRGBtoHSV(img);	
			
	    	colorHSVData.put(""+id, data);
		} catch (IOException e) {
			System.err.println("Populate HSV image data has failed! "+e.toString());
		}
		return img;
	}

	
	public BufferedImage loadImage(String uri) throws IOException {
		InputStream in = getClass().getResourceAsStream(uri);
		return ImageIO.read(in);
	}

	public BufferedImage populateFFTImageData(int h, int w, int id, ImageType type) {

		Complex[][] data = null;
		switch(type) {
			case GRAYSCALE:
				data = grayScaleData.get(""+id);
				if(data == null) {
					throw new IllegalArgumentException("Grayscale Image Data has not found!");
				}
			break;
			case RGB24:
				data = color24Data.get(""+id);
				if(data == null) {
					throw new IllegalArgumentException("RGB24 Image Data has not found by id="+id);
				}
			break;
			case HSV:
				data = colorHSVData.get(""+id);
				if(data == null) {
					throw new IllegalArgumentException("HSV Image Data has not found by id="+id);
				}
			break;
		default:
			throw new IllegalArgumentException("Unknown image format selected!");
		}
		
		Complex[][] fftdata = fftService.performFFT(data, h,w );				
		fftData.put(""+id, fftdata);
		int[] intData = fftService.prepareGraph(  fftdata,h,w, type);
		Integer[] integerData = Arrays.stream( intData ).boxed().toArray( Integer[]::new );
		graphData.put(""+id, integerData);
		
		double max = fftService.getMax(fftdata, h, w );
		double min = fftService.getMin(fftdata, h, w );
		double max1 = fftService.getMaxIm(fftdata, h, w );
		double min1 = fftService.getMinIm(fftdata, h, w );
    	
		fftService.log("\nMAX:"+max);
		fftService.log("MIN:"+min);
		fftService.log("\nMAX-IM:"+max1);
		fftService.log("MIN-IM:"+min1);

		fftService.printData(fftdata,  h, w, "FFT-IMG"+id);
//		BufferedImage fftimg = fftService.createAdoptedImage(fftdata,  h, w);
//		return fftimg;
		return null;
	}	

	public Complex[][] getFFTData(int id) {
		return fftData.get(""+id);
	}
	
	public Complex[][] getGrayScaleData(int id) {
		return grayScaleData.get(""+id);
	}

	public static Map<String, Complex[][]> getColor24data() {
		return color24Data;
	}

	public double getMathExpected(Complex[] data) {
		double m = 0;
		double p = 1/data.length;
		
		for(int i=0; i < data.length; i++) {
			m+=data[i].re()*p;
		}
		
		return m;
	}
	
	public Complex[][] convertRGBtoHSV(BufferedImage img) {
	   	int h = img.getHeight();
    	int w = img.getWidth();
    	
    	Complex[][] data = new Complex[h][w];
    	/**
    	 * Get image data
    	 */
    	for(int j =0; j<h; j++) {       	
    		for(int i=0; i<w; i++) {
    			Color c = new Color(img.getRGB(i, j));
    			int r = c.getRed();
    			int g = c.getGreen(); 
    			int b = c.getBlue();
    			float[] hsv = new float[3];
    			Color.RGBtoHSB(r, g, b, hsv);
    			data[j][i]=new Complex(hsv[0], 0);    			
    		}
    	}
    	
    	return data;
	}

	@Override
	public Integer[] getGraphData(int id) {
		return graphData.get(""+id);
	}

	@Override
	public double calculateCorrelation(Integer[] data, Integer[] etaloneData) {
		
		if( etaloneData == null || etaloneData.length == 0) {
			return -1;
		}

		if( data == null || data.length == 0) {
			return 0;
		}

		double avgX = calculateAVG(data);
		double avgY = calculateAVG(etaloneData);
		
		double sumTopXY=0;
		for(int i=0; i< data.length; i++) {
			sumTopXY+=(data[i]-avgX)*(etaloneData[i]-avgY);
		}
		
		double sumBottomX=0;
		for(int i=0; i< data.length; i++) {
			sumBottomX+=(data[i]-avgX)*(data[i]-avgX);
		}

		double sumBottomY=0;
		for(int i=0; i< data.length; i++) {
			sumBottomY+=(etaloneData[i]-avgY)*(etaloneData[i]-avgY);
		}
		
		double tmp = Math.sqrt(sumBottomX) * Math.sqrt(sumBottomY);
		return sumTopXY / tmp;
	}

	private double calculateAVG(Integer[] data) {
		double sum=0;

		if( data == null || data.length == 0) {
			return sum;
		}
		
		for( Integer v : data) {
			sum+=v;
		}
		return sum/data.length;
	}

}
