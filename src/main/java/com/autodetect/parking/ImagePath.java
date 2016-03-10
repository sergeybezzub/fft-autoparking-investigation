package com.autodetect.parking;

public enum ImagePath {
	EMPTY3("/empty3.png"),
	BUSY1("/busy1.png"),
	BUSY2("/busy2.png"),
	BUSY3("/busy3.png"),
	BUSY4("/busy4.png"),
	BUSY5("/busy5.png"),
	BUSY6("/busy6.png"),
	BUSY7("/busy7.png"),
	BUSY8("/busy8.png"),
	EMPTY1("/empty1.png"),
	EMPTY2("/empty2.png"),
	EMPTY4("/empty7.png"),
	EMPTY5("/empty5.png"),
	EMPTY6("/empty6.png"),
	EMPTY7("/empty4.png"),
	EMPTY8("/empty8.png");
	
	private String path;

	private ImagePath(String path) {
		this.path=path;
	}

	public String getPath() {
		return path;
	}
}
