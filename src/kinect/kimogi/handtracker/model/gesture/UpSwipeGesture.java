package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;

public class UpSwipeGesture extends SingleHandGesture {

	@Override
	public Gesture name() {
		return Gesture.U_SWIPE;
	}

	@Override
	public boolean matching(ArrayList<Point> points) {
		return false;
	}
}
