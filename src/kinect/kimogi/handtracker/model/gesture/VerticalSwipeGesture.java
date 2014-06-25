package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;

public class VerticalSwipeGesture extends SingleHandGesture {

	public static final int WHEEL_AMT = 100;
	
	
	@Override
	public Gesture name() {
		return Gesture.V_SWIPE;
	}

	@Override
	public boolean matching(ArrayList<Point> points) {
		return false;
	}
}
