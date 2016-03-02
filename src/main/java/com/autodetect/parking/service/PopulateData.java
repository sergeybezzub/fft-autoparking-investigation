package com.autodetect.parking.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.autodetect.parking.ImageType;
import com.fft.Complex;

public interface PopulateData {

	BufferedImage populateGrayscaleImageData(String path, int id);
	BufferedImage populateRGB24ImageData(String path, int id);
	BufferedImage populateFFTImageData(int h, int w, int id, ImageType type);
	BufferedImage loadImage(String uri) throws IOException;
	Complex[][] getFFTData(int id);
	Complex[][] getGrayScaleData(int id);
	Complex[][] convertRGBtoHSV(BufferedImage img);
	BufferedImage populateHSVImageData(String path, int id);

}
