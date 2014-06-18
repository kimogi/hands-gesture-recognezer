package nite.openni.handtracker.controller.sweepline;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;

import nite.openni.handtracker.model.sweepline.Constants;
import nite.openni.handtracker.model.sweepline.Event;
import nite.openni.handtracker.model.sweepline.Segment;
import nite.openni.handtracker.model.sweepline.Pair;

public class SweepLineAlgorithm {
	private static ConcurrentSkipListSet<Segment> segments = new ConcurrentSkipListSet<Segment>(Segment.LineComparator);
	private static HashMap<Integer, Segment> where = new HashMap<Integer, Segment>();
	
	public static Pair<Segment, Segment> getIntercestion(ArrayList<Segment> lines) {
		if(lines == null || lines.isEmpty()) {
			return null;
		}
		Vector<Event> events = new Vector<Event>();
		for (int i=0; i<lines.size(); ++i) {
			events.add(new Event (Math.min(lines.get(i).point1.x, lines.get(i).point2.x), +1, i));
			events.add(new Event(Math.max(lines.get(i).point1.x, lines.get(i).point2.x), -1, i));
		}
		Collections.sort(events, Event.EventComparator);
		
		segments.clear();
		
		for(int i=0; i<events.size(); ++i) {
			int id = events.get(i).id;
			if (events.get(i).tp == +1) {
				if(!segments.isEmpty()) {
					Segment nxt = lowerBound(segments, lines.get(id));
					Segment prv = prev(segments, nxt);
					if (!nxt.equals(segments.last()) && intersect(nxt, lines.get(id))) {
						return new Pair<Segment, Segment>(nxt, lines.get(id));
					}
					if (!prv.equals(segments.last()) && intersect(prv, lines.get(id))) {
						return new Pair<Segment, Segment>(prv, lines.get(id));
					}
				}
				if(segments.add(lines.get(id))) {
					where.put(id, lines.get(id));
				} else {
					where.put(id, segments.ceiling(lines.get(id)));
				}
			} else {
				Segment nxt = next(segments, where.get(id));
				Segment prv = prev(segments, where.get(id));

				if (nxt != null && !nxt.equals(segments.last()) && !prv.equals(segments.last()) && intersect(nxt, prv)) {
					return new Pair<Segment, Segment>(prv, nxt);
				}
				segments.remove(where.get(id));
			}
		}
		return null;
	}
	
	private static Segment lowerBound(ConcurrentSkipListSet<Segment> lines, Segment line) {
		return lines.ceiling(line) != null ? lines.ceiling(line) : lines.last();
	}
	
	private static boolean intersect(Segment a, Segment b) {
		return intersect1d (a.point1.x, a.point2.x, b.point1.x, b.point2.x)
				&& intersect1d (a.point1.y, a.point2.y, b.point1.y, b.point2.y)
				&& vec (a.point1, a.point2, b.point1) * vec (a.point1, a.point2, b.point2) <= 0
				&& vec (b.point1, b.point2, a.point1) * vec (b.point1, b.point2, a.point2) <= 0;
	}
	
	private static Segment prev(ConcurrentSkipListSet<Segment> lines,  Segment line) {
		if(lines.first().equals(line)) {
			return lines.last();
		}
		return lines.lower(line) != null ? lines.lower(line) : lines.first();
	}
	
	private static Segment next(ConcurrentSkipListSet<Segment> lines, Segment line) {
		return lines.higher(line) != null ? lines.higher(line) : lines.last();
	}
	
	private static boolean intersect1d (double l1, double r1, double l2, double r2) {
		if (l1 > r1)  {
			double temp = l1;
			l1 = r1;
			r1 = temp;	
		}
		if (l2 > r2) {
			double temp = l2;
			l2 = r2;
			r2 = temp;	
		} 
		return Math.max(l1, l2) <= Math.min(r1, r2) + Constants.EPS;
	}
	 
	private static int vec(final Point a, final Point b, final Point c) {
		double s = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
		return Math.abs(s) < Constants.EPS ? 0 : s > 0 ? +1 : -1;
	}
}
