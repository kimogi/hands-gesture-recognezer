package kinect.kimogi.handtracker.model;

// HandTrail.java
// Andrew Davison, November 2011, ad@fivedots.psu.ac.th

/* Store hand coordinates in a list (up to a maximum of MAX_POINTS)
 and draw them as a trail (actually a polyline).

 When MAX_POINTS is reached, the oldest point (at position 0) is
 discarded, causing the rendering to show a trail of previous hand
 points.
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import kinect.kimogi.handtracker.TrackerPanel;

import org.OpenNI.DepthGenerator;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;

public class ProjectiveHandTrail {
	private static final int MAX_POINTS = 30;

	private static final int CIRCLE_SIZE = 25;
	private static final int STROKE_SIZE = 10;

	private static final Color POINT_COLORS[] = { Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.PINK, Color.YELLOW };

	private Font msgFont; // for the hand ID string

	private int handID;
	private DepthGenerator depthGen;
	private ArrayList<Point> coords; // the points that form the trail

	public ProjectiveHandTrail(int id, DepthGenerator dg) {
		handID = id;
		depthGen = dg;
		msgFont = new Font("SansSerif", Font.BOLD, 24);

		coords = new ArrayList<Point>();
	}

	public synchronized void addPoint(Point3D realPt) {
		try {
			Point3D pt = depthGen.convertRealWorldToProjective(realPt);
			if (pt == null)
				return;
			coords.add(new Point((int) pt.getX(), (int) pt.getY())); // discard
																		// z
																		// coord
			if (coords.size() > MAX_POINTS) // get rid of the oldest point
				coords.remove(0);
		} catch (StatusException e) {
			System.out.println("Problem converting point");
		}
	}

	public synchronized void draw(Graphics2D g2) {
		int numPoints = coords.size();
		if (numPoints == 0)
			return;

		if (TrackerPanel.drawTrails) {
			drawTrail(g2, coords, numPoints);
		}

		Point pt = coords.get(numPoints - 1);
		if (TrackerPanel.drawPoints) {
			pt = coords.get(numPoints - 1);
			g2.setColor(POINT_COLORS[(handID + 1) % POINT_COLORS.length]);
			g2.fillOval(pt.x - CIRCLE_SIZE / 2, pt.y - CIRCLE_SIZE / 2, CIRCLE_SIZE, CIRCLE_SIZE);
		}
		if (TrackerPanel.drawIds) {
			g2.setColor(Color.WHITE);
			g2.setFont(msgFont);
			g2.drawString("" + handID, pt.x - 6, pt.y + 6); // roughly centered
		}
	}

	private void drawTrail(Graphics2D g2, ArrayList<Point> coords, int numPoints) {
		int[] xCoords = new int[numPoints];
		int[] yCoords = new int[numPoints];

		Point pt;
		for (int i = 0; i < numPoints; i++) {
			pt = coords.get(i);
			xCoords[i] = pt.x;
			yCoords[i] = pt.y;
		}

		g2.setColor(POINT_COLORS[handID % POINT_COLORS.length]);
		g2.setStroke(new BasicStroke(STROKE_SIZE));

		g2.drawPolyline(xCoords, yCoords, numPoints);
	}
}