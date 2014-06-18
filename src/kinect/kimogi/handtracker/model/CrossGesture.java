package kinect.kimogi.handtracker.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kinect.kimogi.handtracker.controller.PointUtils;
import kinect.kimogi.handtracker.controller.SegmentUtils;

public class CrossGesture extends SingleHandGesture{
	private final int MIN_POINTS = 12;
	private final double MIN_RESEMBLANCE_VALUE = 0.7;
	
	private int A = 1;

	private int input_min_x;
	private int input_max_y;
	private int input_min_y;
	
	public Gesture name() {
		return Gesture.CROSS;
	}
	
	public boolean matching(ArrayList<Point> points) {
		boolean detected = false;

		PointUtils.removeRepetitions(points);
		ArrayList<Point> selfCrossings = SegmentUtils.getSelfCrossings(PointUtils.convertToSegments(points));
		if(isCrossBySelfCrossing(selfCrossings)) {
			Point selfCrossingPoint = selfCrossings.get(0);			
			ArrayList<Point> translated = PointUtils.translate(points, selfCrossingPoint.x, selfCrossingPoint.y);
			
			defineExtremeValues(translated);
			if (isCrossByExtremeValues()) {
				defineCrossPatternParams();
				if (translated.size() >= MIN_POINTS) {

					HashMap<Integer, Integer> comapring = getComparingValues(translated);
					Integer[] curve1 = comapring.keySet().toArray(new Integer[comapring.size()]);
					Integer[] curve2 = comapring.values().toArray(new Integer[comapring.size()]);

					double resemblance = evalResemblance(curve1, curve2);

					if (resemblance >= MIN_RESEMBLANCE_VALUE) {
						detected = true;
					}
				}
			} 
		}
		return detected;
	}

	private boolean isCrossBySelfCrossing(ArrayList<Point> selfCrossings) {
		return selfCrossings.size() == 1;
	}
	
	private double evalResemblance(Integer[] curve1, Integer[] curve2) {
		return Math.abs(dotProduct(curve1, curve2) / (norm(curve1) * norm(curve2)));
	}

	private double norm(Integer[] vector) {
		long sum = 0;
		for (int i = 0; i < vector.length; i++) {
			sum += vector[i] * vector[i];
		}
		return Math.sqrt(sum);
	}

	private long dotProduct(Integer[] vector1, Integer[] vector2) {
		long dotProduct = 0;
		if (vector1.length != vector2.length) {
			System.out.println("Not equal vectors lengths : " + vector1.length + " " + vector2.length);
		} else {
			for (int i = 0; i < vector1.length; i++) {
				dotProduct += vector1[i] * vector2[i];
			}
		}
		return dotProduct;
	}

	private HashMap<Integer, Integer> getComparingValues(final ArrayList<Point> givenPoints) {
		HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
		Iterator<Point> iterator = givenPoints.iterator();
		while (iterator.hasNext()) {
			Point givenPoint = iterator.next();
			int patternY = evalCrossPatternY(givenPoint.x, givenPoint.y);
			values.put(givenPoint.y, patternY);
		}
		return values;
	}

	private int evalCrossPatternY(int x, int y) {
		int patternYAbs;
		if (x > A/2) {
			patternYAbs = (int) (Math.sin(Math.PI/3)*x);
		} else {
			double square = (Math.pow(A*x, 2) - Math.pow(x, 4)) / Math.pow(A, 2);
			patternYAbs = (int) Math.sqrt(Math.abs(square));
		}
		return (int)Math.signum(y)*patternYAbs;
	}

	private boolean isCrossByExtremeValues() {
		int absYMin = Math.abs(input_min_x)/4 ;
		int absYMax = 3*Math.abs(input_min_x)/4 ;
		
		int y1 = Math.abs(input_min_y);
		int y2 = Math.abs(input_max_y);
		
		return absYMin < y1 && absYMax > y1 && absYMin < y2 && absYMax > y2 ? true : false;
	}
	
	private void defineCrossPatternParams() {
		A = Math.abs(input_min_x);
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
		input_min_x = leftest;
		input_min_y = lowest;
		input_max_y = heighest;
	}	
}
