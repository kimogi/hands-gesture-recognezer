package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;

public class DownSwipeGesture extends SingleHandGesture {

	@Override
	public Gesture name() {
		return Gesture.D_SWIPE;
	}

	@Override
	public boolean matching(ArrayList<Point> points) {
		return false;
	}
}
