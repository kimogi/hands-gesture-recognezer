package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kinect.kimogi.handtracker.controller.PointUtils;
import kinect.kimogi.handtracker.controller.VectorUtils;

public class HorizontalSwipeGesture extends SingleHandGesture {

	protected static int MIN_POINTS = 10;
	protected static double MIN_RESEMBLANCE_VALUE = 0.9;
	
	protected static final int CONSTANT_PATTERN_Y = 2;
	protected static final int MIN_DISTANCE_BETWEEN_NEIGHBORS = 20;
	
	private static final int VERTICAL_HORIZONTAL_DIFERENCE_IN_TIMES = 5;
	
	//TODO : add extreme value, completeness sucks
	
	private int inputMaxY;
	private int inputMinY;
	private int inputMaxX;
	private int inputMinX;
	
	@Override
	public Gesture name() {
		return Gesture.H_SWIPE;
	}

	@Override
	public boolean matching(ArrayList<Point> points) {
		System.out.println("----------------------");
		for(Point point : points) {
			System.out.println(point.x + " " + point.y);
		}
		System.out.println("----------------------");
		boolean detected = false;
		if (points.size() == MIN_POINTS) {
			defineExtremeValues(points);
			if(validExtremeValues()) {
				HashMap<Integer, Integer> comapring = getComparingValues(points);
				Integer[] curve1 = comapring.keySet().toArray(new Integer[comapring.size()]);
				Integer[] curve2 = comapring.values().toArray(new Integer[comapring.size()]);

				double resemblance = VectorUtils.resemblance(curve1, curve2);
				if (resemblance >= MIN_RESEMBLANCE_VALUE) {
					detected = true;
				}
			}
		}
		return detected;
	}
	
	private void defineExtremeValues(ArrayList<Point> points) {
		int lowest = Integer.MAX_VALUE;
		int heighest = Integer.MIN_VALUE;
		int leftest = Integer.MAX_VALUE;
		int rightest = Integer.MIN_VALUE;
		
		for (Point point : points) {
			if (point.getX() > rightest) {
				rightest = point.x;
			}
			if (point.getX() < leftest) {
				leftest = point.x;
			}
			if (point.getY() > heighest) {
				heighest = point.y;
			}
			if (point.getY() < lowest) {
				lowest = point.y;
			}
		}
		inputMinY = lowest;
		inputMaxY = heighest;
		inputMaxX = rightest;
		inputMinX = leftest;
	}
	
	private boolean validExtremeValues() {
		return PointUtils.distance(inputMinY, inputMaxY) * VERTICAL_HORIZONTAL_DIFERENCE_IN_TIMES < PointUtils.distance(inputMinX, inputMaxX);
	}
	
	private HashMap<Integer, Integer> getComparingValues(final ArrayList<Point> givenPoints) {
		HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
		Iterator<Point> iterator = givenPoints.iterator();
		while (iterator.hasNext()) {
			Point givenPoint = iterator.next();
			values.put(givenPoint.y, CONSTANT_PATTERN_Y);
		}
		return values;
	}
}
