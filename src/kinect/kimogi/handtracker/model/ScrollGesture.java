package kinect.kimogi.handtracker.model;

import java.awt.Point;
import java.util.ArrayList;

public class ScrollGesture extends DoubleHandGesture {

	@Override
	public Gesture name() {
		return Gesture.SCROLL;
	}

	@Override
	public boolean matching(ArrayList<Point> hand1, ArrayList<Point> hand2) {
		return false;
	}
}
