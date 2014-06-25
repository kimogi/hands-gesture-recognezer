package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kinect.kimogi.handtracker.controller.PointUtils;
import kinect.kimogi.handtracker.controller.SegmentUtils;
import kinect.kimogi.handtracker.controller.VectorUtils;

public class CrossGesture extends SingleHandGesture {
	protected static int MIN_POINTS = 20;
	private static int MIN_HEIGHT = 100;
	
	private int A = 1;

	private int inputMinX;
	private int inputMaxY;
	private int inputMinY;
	
	public Gesture name() {
		return Gesture.CROSS;
	}
	
	public boolean matching(ArrayList<Point> points) {
		boolean detected = false;

		PointUtils.removeRepetitions(points);
		ArrayList<Point> selfCrossings = SegmentUtils.getSelfCrossings(PointUtils.convertToSegments(points));
		if(validSelfCrossing(selfCrossings)) {
			Point selfCrossingPoint = selfCrossings.get(0);			
			ArrayList<Point> translated = PointUtils.translate(points, selfCrossingPoint.x, selfCrossingPoint.y);
			
			defineExtremeValues(translated);
			defineCrossPatternParams();
			if (validExtremeValues()) {
				if (translated.size() >= MIN_POINTS) {

					HashMap<Integer, Integer> comapring = getComparingValues(translated);
					Integer[] curve1 = comapring.keySet().toArray(new Integer[comapring.size()]);
					Integer[] curve2 = comapring.values().toArray(new Integer[comapring.size()]);

					double resemblance = VectorUtils.resemblance(curve1, curve2);

					if (resemblance >= MIN_RESEMBLANCE_VALUE) {
						detected = true;
					}
				}
			} 
		}
		return detected;
	}

	private boolean validSelfCrossing(ArrayList<Point> selfCrossings) {
		return selfCrossings.size() == 1;
	}
	
	private HashMap<Integer, Integer> getComparingValues(final ArrayList<Point> givenPoints) {
		HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
		Iterator<Point> iterator = givenPoints.iterator();
		while (iterator.hasNext()) {
			Point givenPoint = iterator.next();
			int patternY = evalPatternY(givenPoint.x, givenPoint.y);
			values.put(givenPoint.y, patternY);
		}
		return values;
	}

	private int evalPatternY(int x, int y) {
		int patternYAbs;
		if (x > A/2) {
			patternYAbs = (int) (Math.sin(Math.PI/3)*x);
		} else {
			double square = (Math.pow(A*x, 2) - Math.pow(x, 4)) / Math.pow(A, 2);
			patternYAbs = (int) Math.sqrt(Math.abs(square));
		}
		return (int)Math.signum(y)*patternYAbs;
	}

	private boolean validExtremeValues() {
		int patternYMin = -A/2;
		int patternYMax = A/2;
		int deviationY = A/4;
		
		return PointUtils.distance(inputMinY, inputMaxY) >= MIN_HEIGHT &&
				patternYMin - deviationY <= inputMinY &&
				patternYMin + deviationY >= inputMinY &&
				patternYMax + deviationY >= inputMaxY && 
				patternYMax - deviationY <= inputMaxY ? true : false;
	}
	
	private void defineCrossPatternParams() {
		A = Math.abs(inputMinX);
	}

	private void defineExtremeValues(final ArrayList<Point> points) {
		int leftest = Integer.MAX_VALUE;
		int lowest = Integer.MAX_VALUE;
		int heighest = Integer.MIN_VALUE;

		for (Point point : points) {
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
		inputMinX = leftest;
		inputMinY = lowest;
		inputMaxY = heighest;
	}	
}
