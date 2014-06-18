package nite.openni.handtracker.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import Jama.Matrix;
import nite.openni.handtracker.model.sweepline.Segment;

public class PointUtils {
	public static ArrayList<Point> decimate(ArrayList<Point> points, int minDistance) {
		Iterator<Point> iter = points.iterator();
		Point lastSpared = null;
		while (iter.hasNext()) {
			if (lastSpared == null) {
				lastSpared = iter.next();
				continue;
			} else {
				Point point = iter.next();
				if (distance(lastSpared, point) >= minDistance) {
					lastSpared = point;
				} else {
					iter.remove();
				}
			}
		}
		return points;
	}
	
	public static int distance(Point a, Point b) {
		return (int) Math.sqrt((a.x - b.x)*(a.x -b.x) + (a.y - b.y)*(a.y -b.y));
	}
	
	public static int distance(float a, float b) {
		int distance = 0;
		if (Math.signum(a) == Math.signum(b)) {
			distance = (int) Math.abs(a - b);
		} else {
			distance = (int) (Math.abs(a) + Math.abs(b));
		}
		return distance;
	}
	
	public static Iterable<Point> removeRepetitions(ArrayList<Point> points) {
		Iterator<Point> iter = points.iterator();
		Point prev = null;
		while (iter.hasNext()) {
			if (prev == null) {
				prev = iter.next();
				continue;
			}
			Point curr = iter.next();
			if (curr.getX() == prev.getX() && curr.getY() == prev.getY()) {
				iter.remove();
			} else {
				prev = curr;
			}
		}
		return points;
	}

	public static ArrayList<Segment> convertToSegments(ArrayList<Point> points) {
		ArrayList<Segment> lines = new ArrayList<Segment>();
		Iterator<Point> iter = points.iterator();
		Point prev = null;
		while(iter.hasNext()) {
			if(prev == null) {
				prev = iter.next();
			} else {
				Point point = iter.next();
				lines.add(new Segment(points.indexOf(prev), prev, point));
				prev = point;
			}
		}
		return lines;
	}
	
	public static ArrayList<Point> translate(ArrayList<Point> points, int tx, int ty) {
		double[][] translationMatrix = { { 1, 0, 0 }, { 0, 1, 0 }, { -tx, -ty, 1 } };
		Iterator<Point> iter = points.iterator();
		while (iter.hasNext()) {
			Point point = iter.next();
			double[] vector = { point.x, point.y, 1 };
			double[] translatedVector = multiply(vector, translationMatrix);
			Point translatedPoint = new Point((int) translatedVector[0], (int) translatedVector[1]);
			points.set(points.indexOf(point), translatedPoint);
		}
		return points;
	}

	private static double[] multiply(double[] vectorValues, double[][] matrixValues) {
		Matrix matrix = new Matrix(matrixValues);
		Matrix vector = new Matrix(vectorValues, 1);
		Matrix multiplied = vector.times(matrix);
		return multiplied.getArray()[0];
	}
}
