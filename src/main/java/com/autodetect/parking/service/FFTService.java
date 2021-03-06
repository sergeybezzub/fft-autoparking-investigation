package com.autodetect.parking.service;

import java.awt.image.BufferedImage;

import com.autodetect.parking.ImageType;
import com.fft.Complex;

public interface FFTService {
	
	int GRAPH_LENGTH = 360;
	
	Complex[][] performFFT(BufferedImage img);
	Complex[][] performIFFT(Complex[][] fftData, int height, int width);
	Complex[][] performConvolve(Complex[][] fftData1, Complex[][] fftData2, int height, int width);
	Complex[][] performFFT(Complex[][] fftData, int height, int width);
	BufferedImage createImage(Complex[][] data, int height, int weidth);
	void printData(Complex[][] fftData, int height, int width);
	void printData(Complex[][] fftData, int height, int width, String title);
	BufferedImage createAdoptedImage(Complex[][] data, int height, int weidth, ImageType type);
	double getMax(Complex[][] fftData, int height, int width);
	double getMin(Complex[][] fftData, int height, int width);
	double getMaxIm(Complex[][] fftData, int height, int width);
	double getMinIm(Complex[][] fftData, int height, int width);
	Complex[][] createGreyScaleImageData(BufferedImage img);
	void prepareData(Complex[][] data, int h, int w);
	int[] prepareGraph(Complex[][] data, int height, int width, ImageType type);
	int[] prepareGraphRe(Complex[][] data, int height, int width, ImageType type);
	int[] prepareGraphIm(Complex[][] data, int height, int width, ImageType type);
	void log(String message);	
}
