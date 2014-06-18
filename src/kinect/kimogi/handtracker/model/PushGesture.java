package kinect.kimogi.handtracker.model;

import java.awt.Point;
import java.util.ArrayList;

public class PushGesture extends SingleHandGesture {
	
	public boolean matching(ArrayList<Point> points) {
		return false;
	}
	
	public Gesture name() {
		return Gesture.PUSH;
	}
}
