package kinect.kimogi.handtracker.controller;

import java.util.ArrayList;
import java.util.Iterator;

public class SequenceUtils {
	public static boolean strictlyMonotonicallyIncreasing(ArrayList<Integer> sequence) {
		Iterator<Integer> it = sequence.iterator();
		int prev = Integer.MIN_VALUE;
		while(it.hasNext()) {
			int value = it.next();
			if(value <= prev) {
				return false;
			}
			prev = value;
		}
		return true;
	}
	
	public static boolean strictlyMonotonicallyDecreasing(ArrayList<Integer> sequence) {
		Iterator<Integer> it = sequence.iterator();
		int prev = Integer.MAX_VALUE;
		while(it.hasNext()) {
			int value = it.next();
			if(value >= prev) {
				return false;
			}
			prev = value;
		}
		return true;
	}
}
