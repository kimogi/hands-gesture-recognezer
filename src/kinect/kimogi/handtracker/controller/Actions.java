package kinect.kimogi.handtracker.controller;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Actions {

	private static Robot robot;
	
	public static void init() {
		try {
			if(robot == null) {
				robot = new Robot();
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}			
	}
	
	public static void mouseMove(Point point) {
		robot.mouseMove(point.x, point.y);					
	}
	
	public static void mouseLost() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();
		robot.mouseMove((int) screenWidth/2, (int) screenHeight/2);
	}
	
	public static void mouseClick() {
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void mouseScroll(int wheelAmt) {
		robot.mouseWheel(wheelAmt);
	}
	
	public static void desktopRight() {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
	}
	
	public static void desktopLeft() {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_LEFT);
	}
}
