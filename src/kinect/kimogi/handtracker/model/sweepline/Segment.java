package kinect.kimogi.handtracker.model.sweepline;

import java.awt.Point;
import java.util.Comparator;

public class Segment {
	
	public int id;
	public Point point1;
	public Point point2;
	
	public Segment(int id, Point point1, Point point2) {
		this.id = id;
		this.point1 = point1;
		this.point2 = point2;
	}

	public double getY(double x) {
		if(Math.abs(point1.x - point2.x) < Constants.EPS) {
			return point1.y;
		}
		return point1.y + (point2.y - point1.y) * (x - point1.x) / (point2.x - point1.x);
	}
	
	public boolean haveCommonPoints(Segment line) {
		return same(line.point1, point1) || same(line.point1, point2) || same(line.point2, point1) || same(line.point2, point2);
	}
	
	private boolean same(Point point1, Point point2) {
		return point1.x == point2.x && point1.y == point2.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		Segment line = (Segment) obj;
		return id == line.id;
	}
	
	public static Comparator<Segment> LineComparator = new Comparator<Segment>() {

		@Override
		public int compare(Segment line1, Segment line2) {
			double x = Math.max(Math.min(line1.point1.x, line1.point2.x), Math.min(line2.point1.x, line2.point2.x));
			return line1.getY(x) < line2.getY(x) - Constants.EPS ? -1 : 1;
		}
	};
}