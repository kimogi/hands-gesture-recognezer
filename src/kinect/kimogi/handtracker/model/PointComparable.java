package kinect.kimogi.handtracker.model;

public class PointComparable {
	
	public int x;
	public int y;
	
	public PointComparable(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		PointComparable point = (PointComparable)obj;
		return point.x == this.x && point.y == this.y;
	}
}
