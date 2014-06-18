package nite.openni.handtracker.model.sweepline;

import java.util.Comparator;

public class Event {
	public double x;
	public int tp;
	public int id;
	
	public Event(double x, int tp, int id) {
		this.x = x;
		this.tp = tp;
		this.id = id;
	}

	public static Comparator<Event> EventComparator = new Comparator<Event>() {
		@Override
		public int compare(Event e1, Event e2) {
			if (Math.abs(e1.x - e2.x) > Constants.EPS) {
				return Double.compare(e1.x, e2.x);
			} 
			return Integer.compare(e2.tp, e1.tp);
		}
	};
}