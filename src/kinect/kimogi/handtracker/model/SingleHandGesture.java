package kinect.kimogi.handtracker.model;

import java.awt.Point;
import java.util.ArrayList;

public abstract class SingleHandGesture {
	
	public abstract boolean matching(ArrayList<Point> points);
	public abstract Gesture name();
}
