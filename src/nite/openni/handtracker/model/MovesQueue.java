package nite.openni.handtracker.model;

import java.awt.Point;
import java.util.ArrayList;

import org.OpenNI.DepthGenerator;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;

public class MovesQueue {
	
	private static final int MAX_SIZE = 30;
	
	private ArrayList<Point3D> moves;
	private DepthGenerator depthGen;
	
	public MovesQueue(DepthGenerator depthGen) {
		this.moves = new ArrayList<Point3D>();
		this.depthGen = depthGen;
	}
	
	public void addMove(Point3D point) {
		if(moves.size() == MAX_SIZE) {
			removeEarliestPosition();
		}
		moves.add(point);
	}
	
	public ArrayList<Point> getProjectionPoints() throws StatusException {
		ArrayList<Point> planePoints = new ArrayList<Point>();
		for(Point3D point : moves) {
			Point3D projectivePoint = depthGen.convertRealWorldToProjective(point);
			planePoints.add(new Point((int) projectivePoint.getX(), (int) projectivePoint.getY()));
		}
		return planePoints;
	}
	
	public void removeMove(Point3D point) {
		moves.remove(point);
	}
	
	public boolean isEmpty() {
		return moves.isEmpty();
	}
	
	public void clear() {
		moves.clear();
	}
	
	public int size() {
		return moves.size();
	}
	
	public Point3D getEarliestPosition() {
		if(!moves.isEmpty()) {
			return moves.get(0);
		} else {
			return null;
		}
	}
	
	public void removeEarliestPosition() {
		if(!moves.isEmpty()) {
			moves.remove(0);
		} 
	}
}
