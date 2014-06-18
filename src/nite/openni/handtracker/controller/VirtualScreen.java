package nite.openni.handtracker.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import org.OpenNI.Point3D;

public class VirtualScreen{

	private static VirtualScreen instance;
	
	public static void init() {
		instance = new VirtualScreen();
	}
	
	public static VirtualScreen getInstance() {
		return instance;
	}
	
	public Point projectToScreen(Point3D point) {
		double scale = 2;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();
			
		int xScreen = (int) (screenWidth/2 + scale*point.getX());
		int yScreen = (int) (screenHeight/2 - scale*point.getY());
		
		return new Point(xScreen, yScreen);
	}	
}
