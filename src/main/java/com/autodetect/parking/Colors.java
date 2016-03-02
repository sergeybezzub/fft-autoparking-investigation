package com.autodetect.parking;

import java.awt.Color;

public enum Colors {
	RED(Color.red), GREEN(Color.green), BLUE(Color.blue), ORANGE(Color.orange), CYAN(Color.cyan), MAGENTA(Color.magenta),PINK(Color.pink),YELLOW(Color.YELLOW), GREY(Color.gray), WHITE(Color.white), BLACK(Color.black);
	
	private Color color;
	
	private Colors(Color c) {
		this.color=c;
	}
	
	public static Color getColorValue( int index) {
		for( Colors c : Colors.values()) {
			if(c.ordinal() == index) {
				return c.getColor();
			}
		}
		return Colors.WHITE.getColor();
	}
	
	public Color getColor() {
		return color;
	}
}
