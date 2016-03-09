package com.autodetect.parking;

public enum ImageType {

	GRAYSCALE, RGB24, HSV;
	
	public static ImageType getImageType(String name) {
		if(name == null || name.isEmpty()) {
			return GRAYSCALE;
		}
		
		String value = name.toLowerCase();
		switch(value) {
			case "rgb24":
				return RGB24;
			case "hsv":
				return HSV;
			default:
				return GRAYSCALE;
		}
	}
}
