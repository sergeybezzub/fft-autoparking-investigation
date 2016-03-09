package com.autodetect.parking;

public enum ExtrimValues {

	GRAYSCALE(12000000,-1100000, 1350000, -1350000 ), RGB24(8.2E11,-8.5E10, 8.0E10, -8.0E10), HSV(46500,-5600, 4300, -4300);

	private double max;
	private double min;
	private double max_im;
	private double min_im;
	
	private ExtrimValues(double max, double min, double max_im, double min_im) {
		this.max=max;
		this.min=min;
		this.max_im=max_im;
		this.min_im=min_im;
	}
	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public double getMax_im() {
		return max_im;
	}

	public double getMin_im() {
		return min_im;
	}

	public static double getMaxByImageType(ImageType type) {
		double max=0;
		switch(type) {
			case GRAYSCALE:
				max= ExtrimValues.GRAYSCALE.getMax();
			break;
			case RGB24:
				max= ExtrimValues.RGB24.getMax();
			break;
			case HSV:
				max= ExtrimValues.HSV.getMax();
			break;
		}
		return max;
	}
	
	public static double getMaxImByImageType(ImageType type) {
		double max_im=0;
		switch(type) {
			case GRAYSCALE:
				max_im= ExtrimValues.GRAYSCALE.getMax_im();
			break;
			case RGB24:
				max_im= ExtrimValues.RGB24.getMax_im();
			break;
			case HSV:
				max_im= ExtrimValues.HSV.getMax_im();
			break;
		}
		return max_im;
	}

	public static double getMinByImageType(ImageType type) {
		double min=0;
		switch(type) {
			case GRAYSCALE:
				min= ExtrimValues.GRAYSCALE.getMin();
			break;
			case RGB24:
				min= ExtrimValues.RGB24.getMin();
			break;
			case HSV:
				min= ExtrimValues.HSV.getMin();
			break;
		}
		return min;
	}

	public static double getMinImByImageType(ImageType type) {
		double min_im=0;
		switch(type) {
			case GRAYSCALE:
				min_im= ExtrimValues.GRAYSCALE.getMin_im();
			break;
			case RGB24:
				min_im= ExtrimValues.RGB24.getMin_im();
			break;
			case HSV:
				min_im= ExtrimValues.HSV.getMin_im();
			break;
		}
		return min_im;
	}

}
