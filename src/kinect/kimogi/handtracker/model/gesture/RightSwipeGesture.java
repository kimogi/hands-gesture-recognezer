package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;

import kinect.kimogi.handtracker.controller.PointUtils;
import kinect.kimogi.handtracker.controller.SequenceUtils;

public class RightSwipeGesture extends HorizontalSwipeGesture {

	@Override
	public Gesture name() {
		return Gesture.R_SWIPE;
	}

	@Override
	public boolean matching(ArrayList<Point> points) {
		boolean detected = false;
		ArrayList<Point> decimated = PointUtils.decimate(points, (int)(MIN_DISTANCE_BETWEEN_NEIGHBORS));
		ArrayList<Point> minDecimated = new ArrayList<Point>();
		if (decimated.size() > MIN_POINTS) {
			minDecimated.addAll(decimated.subList(0, MIN_POINTS));
		} else {
			minDecimated.addAll(decimated);
		}
		
		if(super.matching(minDecimated)) {
			if(SequenceUtils.strictlyMonotonicallyIncreasing(PointUtils.vectorX(minDecimated))) {
				detected = true;
			}
		}
		return detected;
	}
}
