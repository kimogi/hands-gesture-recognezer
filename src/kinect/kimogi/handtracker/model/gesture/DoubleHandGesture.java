package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;

public abstract class DoubleHandGesture {
	public static final float MAX_DEPTH_DISTANCE_BETWEEN_HANDS = 100;
	
	public abstract Gesture name();
	public abstract boolean matching(ArrayList<Point> hand1, ArrayList<Point> hand2);
}
