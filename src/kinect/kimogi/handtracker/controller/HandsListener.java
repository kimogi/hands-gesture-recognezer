package kinect.kimogi.handtracker.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import kinect.kimogi.handtracker.model.MovesQueue;
import kinect.kimogi.handtracker.model.gesture.CrossGesture;
import kinect.kimogi.handtracker.model.gesture.DownSwipeGesture;
import kinect.kimogi.handtracker.model.gesture.Gesture;
import kinect.kimogi.handtracker.model.gesture.LeftSwipeGesture;
import kinect.kimogi.handtracker.model.gesture.PushGesture;
import kinect.kimogi.handtracker.model.gesture.SingleHandGesture;
import kinect.kimogi.handtracker.model.gesture.UpSwipeGesture;

import org.OpenNI.Point3D;
import org.OpenNI.StatusException;

public class HandsListener {

	private static int navigationQueueId = -1;
	private static int gestureQueueId = -1;

	private HashMap<Integer, MovesQueue> handMovesQueues;
	private VirtualScreen virtualScreen;
	private ArrayList<SingleHandGesture> singleHandGestures;

	public HandsListener() {
		handMovesQueues = new HashMap<Integer, MovesQueue>();
		virtualScreen = VirtualScreen.getInstance();

		singleHandGestures = new ArrayList<SingleHandGesture>();
		setSingleHandGestureList();

		Actions.init();
	}

	private void setSingleHandGestureList() {
		singleHandGestures.add(new PushGesture());
		singleHandGestures.add(new CrossGesture());
		singleHandGestures.add(new UpSwipeGesture());
		singleHandGestures.add(new DownSwipeGesture());
		singleHandGestures.add(new LeftSwipeGesture());
		//singleHandGestures.add(new RightSwipeGesture());
	}

	public void addHandMovesQueue(int id, MovesQueue handTrail) {
		handMovesQueues.put(id, handTrail);
	}

	public void addMove(int id, Point3D point) {
		if (handMovesQueues.containsKey(id)) {
			handMovesQueues.get(id).addMove(point);
			onNewEvent();
		}
	}

	public void removeHandMovesQueue(int id) {
		handMovesQueues.remove(id);
		if (handMovesQueues.isEmpty()) {
			System.out.println("  No hand histories left...");
			onNoHands();
		}
	}

	private void onNoHands() {
		Actions.mouseLost();
	}

	private void onNewEvent() {
		if (handMovesQueues.size() == 2 && isNoneMovesQueueEmpty()) {

			Integer[] ids = handMovesQueues.keySet().toArray(new Integer[handMovesQueues.size()]);

			float hand1Depth = handMovesQueues.get(ids[0]).getEarliestPosition().getZ();
			float hand2Depth = handMovesQueues.get(ids[1]).getEarliestPosition().getZ();

			int newNavigationQueueId;
			int newGestureQueueId;

			if (hand1Depth <= hand2Depth) {
				newNavigationQueueId = ids[0];
				newGestureQueueId = ids[1];
			} else {
				newNavigationQueueId = ids[1];
				newGestureQueueId = ids[0];
			}

			if (newNavigationQueueId != navigationQueueId) {
				if (handMovesQueues.containsKey(navigationQueueId)) {
					handMovesQueues.get(navigationQueueId).clear();
				}
			}

			if (newGestureQueueId != gestureQueueId) {
				if (handMovesQueues.containsKey(gestureQueueId)) {
					handMovesQueues.get(gestureQueueId).clear();
				}
			}

			navigationQueueId = newNavigationQueueId;
			gestureQueueId = newGestureQueueId;

			if (handMovesQueues.containsKey(navigationQueueId) && !handMovesQueues.get(navigationQueueId).isEmpty()) {
				performMouseMove(navigationQueueId);
			}
			if (handMovesQueues.containsKey(gestureQueueId) && !handMovesQueues.get(gestureQueueId).isEmpty()) {
				try {
					performSingleHandGestureIfDetected(gestureQueueId);
				} catch (StatusException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void performMouseMove(int navigationMovesQueueId) {
		Point mousePoint = virtualScreen.projectToScreen(handMovesQueues.get(navigationMovesQueueId).getEarliestPosition());
		Actions.mouseMove(mousePoint);
		handMovesQueues.get(navigationMovesQueueId).removeEarliestPosition();
	}

	private void performSingleHandGestureIfDetected(int gestureQueueId) throws StatusException {
		MovesQueue movesQueue = handMovesQueues.get(gestureQueueId);
		ArrayList<Point> planePoints = movesQueue.getProjectionPoints();
		Gesture detected = matching(planePoints);
		if (detected != null) {
			switch (detected) {
			case PUSH:
				//Actions.mouseClick();
				handMovesQueues.get(gestureQueueId).clear();
				break;
			case CROSS:
				System.out.println("Cross detedcted!!");
				handMovesQueues.get(gestureQueueId).clear();
				break;
			case U_SWIPE:
				System.out.println("Up Swipe detedcted!!");
				//Actions.mouseScroll(-100);
				handMovesQueues.get(gestureQueueId).clear();
				break;
			case D_SWIPE:
				System.out.println("Down Swipe detedcted!!");
				//Actions.mouseScroll(100);
				handMovesQueues.get(gestureQueueId).clear();
				break;
			case R_SWIPE:
				System.out.println("R Swipe detedcted!!");
				//Actions.desktopRight();
				handMovesQueues.get(gestureQueueId).clear();
				break;
			case L_SWIPE:
				System.out.println("L Swipe detedcted!!");
				//Actions.desktopLeft();
				handMovesQueues.get(gestureQueueId).clear();
				break;
			default:
				break;
			}
		}
	}

	private boolean isNoneMovesQueueEmpty() {
		for (int key : handMovesQueues.keySet()) {
			MovesQueue handTrail = handMovesQueues.get(key);

			if (handTrail.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private Gesture matching(ArrayList<Point> points) {
		for (SingleHandGesture gesture : singleHandGestures) {
			if (gesture.matching(points)) {
				return gesture.name();
			}
		}
		return null;
	}
}
