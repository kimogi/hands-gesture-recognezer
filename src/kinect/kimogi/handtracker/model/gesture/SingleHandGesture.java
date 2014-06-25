package kinect.kimogi.handtracker.model.gesture;

import java.awt.Point;
import java.util.ArrayList;

public abstract class SingleHandGesture {
	protected static int MIN_POINTS;
	protected static double MIN_RESEMBLANCE_VALUE = 0.7;

	public abstract boolean matching(ArrayList<Point> points);
	public abstract Gesture name();
}
