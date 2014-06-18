package kinect.kimogi.handtracker.controller;

import java.awt.Point;
import java.util.ArrayList;

import kinect.kimogi.handtracker.controller.sweepline.SweepLineAlgorithm;
import kinect.kimogi.handtracker.model.sweepline.Pair;
import kinect.kimogi.handtracker.model.sweepline.Segment;

public class SegmentUtils {
	public static Point intersection(Segment line1, Segment line2) {
		int A1 = line1.point2.y - line1.point1.y;
		int B1 = line1.point1.x - line1.point2.x;
		int C1 = A1*line1.point1.x + B1*line1.point1.y;
		
		int A2 = line2.point2.y - line2.point1.y;
		int B2 = line2.point1.x - line2.point2.x;
		int C2 = A2*line2.point1.x + B2*line2.point1.y;
		
		int delta = A1*B2 - A2*B1;
		if(delta == 0) {
			return null;
		} else {
			int x = (B2*C1 - B1*C2)/delta;
			int y = (A1*C2 - A2*C1)/delta;
			return new Point(x, y);
		}
	}

	public static ArrayList<Point> getSelfCrossings(final ArrayList<Segment> lines) {
		ArrayList<Pair<Segment, Segment>> intersections = new ArrayList<Pair<Segment, Segment>>();
		while (true) {
			Pair<Segment, Segment> intersection = SweepLineAlgorithm.getIntercestion(lines);
			
			if(intersection == null) {
				break;
			} else {
				if(!intersection.getFirst().haveCommonPoints(intersection.getSecond())) {
					intersections.add(intersection);
				}
				lines.remove(intersection.getFirst());
				lines.remove(intersection.getSecond());
			}
		} 
		
		ArrayList<Point> selfCrossings = new ArrayList<Point>();
		for(Pair<Segment, Segment> intersection : intersections) {
			Segment line1 = intersection.getFirst();
			Segment line2 = intersection.getSecond();
			selfCrossings.add(SegmentUtils.intersection(line1, line2));
		}
		return selfCrossings;
	}
}
