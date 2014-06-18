package kinect.kimogi.handtracker;

// TrackerPanel.java
// Andrew Davison, November 2011, ad@fivedots.psu.ac.th

/* Panel that shows the Kinect camera image, and displays multiple
 hand trails (which are drawn by HandTrail objects).

 Based on OpenNI's HandTracker and NITE's PointViewer examples.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import kinect.kimogi.handtracker.controller.HandsListener;
import kinect.kimogi.handtracker.model.MovesQueue;
import kinect.kimogi.handtracker.model.ProjectiveHandTrail;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.GestureGenerator;
import org.OpenNI.HandsGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.License;
import org.OpenNI.MapOutputMode;
import org.OpenNI.PixelFormat;
import org.OpenNI.StatusException;

import com.primesense.NITE.HandEventArgs;
import com.primesense.NITE.HandPointContext;
import com.primesense.NITE.IdEventArgs;
import com.primesense.NITE.NullEventArgs;
import com.primesense.NITE.PointControl;
import com.primesense.NITE.PointEventArgs;
import com.primesense.NITE.SessionManager;

enum SessionState {
	IN_SESSION, NOT_IN_SESSION, QUICK_REFOCUS
}

public class TrackerPanel extends JPanel implements Runnable {

	public static final boolean drawTrails = true;
	public static final boolean drawPoints = true;
	public static final boolean drawIds = true;

	private static final long serialVersionUID = 692632562674377006L;
	private static final double MIN_CONFIDENCE_VALUE = 0.5;

	private BufferedImage image = null;
	private int imWidth, imHeight;

	private volatile boolean isRunning;
	
	private int imageCount = 0;
	private long totalTime = 0;
	private DecimalFormat df;
	private Font msgFont;

	private Context context;
	private ImageGenerator imageGen;
	private DepthGenerator depthGen;
	private HandsGenerator handsGen;
	private GestureGenerator gestureGen;
	
	private SessionManager sessionMan;
	private SessionState sessionState;

	private HashMap<Integer, ProjectiveHandTrail> projectiveHandTrails;
	private HandsListener handsListener;
	
	public TrackerPanel(JFrame top) {
		setBackground(Color.DARK_GRAY);

		df = new DecimalFormat("0.#"); 
		msgFont = new Font("SansSerif", Font.BOLD, 18);

		projectiveHandTrails = new HashMap<Integer, ProjectiveHandTrail>();
		handsListener = new HandsListener();
		
		configKinect();
		configNITE();
		
		new Thread(this).start();
	}

	private void configNITE() {
		try {
			sessionMan = new SessionManager(context, "Click", "RaiseHand");
			setSessionEvents(sessionMan);
			sessionState = SessionState.NOT_IN_SESSION;
			
			PointControl pointCtrl = initPointControl();
			sessionMan.addListener(pointCtrl);

		} catch (GeneralException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void configKinect() {
		try {			
			context = new Context();

			License licence = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");
			context.addLicense(licence);
			
			imageGen = ImageGenerator.create(context);
			depthGen = DepthGenerator.create(context);

			MapOutputMode mapMode = new MapOutputMode(640, 480, 30);
			
			imageGen.setMapOutputMode(mapMode);
			depthGen.setMapOutputMode(mapMode);

			imageGen.setPixelFormat(PixelFormat.RGB24);

			ImageMetaData imageMD = imageGen.getMetaData();
			imWidth = imageMD.getFullXRes();
			imHeight = imageMD.getFullYRes();
			System.out.println("Image dimensions (" + imWidth + ", " + imHeight + ")");

			handsGen = HandsGenerator.create(context); 
			handsGen.SetSmoothing(0.1f);
			
			gestureGen = GestureGenerator.create(context);
			setGestureEvents(gestureGen);

			context.setGlobalMirror(true);
			context.startGeneratingAll();
			System.out.println("Started context generating...");
		} catch (GeneralException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void setGestureEvents(GestureGenerator gestureGen) {}

	private void setSessionEvents(SessionManager sessionMan) {
		try {
			// session start (S1)
			sessionMan.getSessionStartEvent().addObserver(new IObserver<PointEventArgs>() {
				public void update(IObservable<PointEventArgs> observable, PointEventArgs args) {
					System.out.println("Session started...");
					sessionState = SessionState.IN_SESSION;
				}
			});

			// session end (S2)
			sessionMan.getSessionEndEvent().addObserver(new IObserver<NullEventArgs>() {
				public void update(IObservable<NullEventArgs> observable, NullEventArgs args) {
					System.out.println("Session ended");
					isRunning = false;
					sessionState = SessionState.NOT_IN_SESSION;
				}
			});
		} catch (StatusException e) {
			e.printStackTrace();
		}
	}

	private PointControl initPointControl() {
		PointControl pointCtrl = null;
		try {
			pointCtrl = new PointControl();

			// create new hand point, and hand trail (P1)
			pointCtrl.getPointCreateEvent().addObserver(new IObserver<HandEventArgs>() {
				public void update(IObservable<HandEventArgs> observable, HandEventArgs args) {
					sessionState = SessionState.IN_SESSION;
					HandPointContext handContext = args.getHand();
					int id = handContext.getID();
					System.out.println("  Creating hand trail " + id);
					ProjectiveHandTrail handTrail = new ProjectiveHandTrail(id, depthGen);
					handTrail.addPoint(handContext.getPosition());
					projectiveHandTrails.put(id, handTrail);

					MovesQueue realWorldHandTrail = new MovesQueue(depthGen);
					handsListener.addHandMovesQueue(id, realWorldHandTrail);
					handsListener.addMove(id, handContext.getPosition());
				}
			});

			// hand point has moved; add to its trail (P2)
			pointCtrl.getPointUpdateEvent().addObserver(new IObserver<HandEventArgs>() {
				public void update(IObservable<HandEventArgs> observable, HandEventArgs args) {
					sessionState = SessionState.IN_SESSION;
					HandPointContext handContext = args.getHand();
					int id = handContext.getID();
					ProjectiveHandTrail handTrail = projectiveHandTrails.get(id);
					handTrail.addPoint(handContext.getPosition());

					if (handContext.getConfidence() > MIN_CONFIDENCE_VALUE) {
						handsListener.addMove(id, handContext.getPosition());
					}
				}
			});

			// destroy hand point and its trail (P3)
			pointCtrl.getPointDestroyEvent().addObserver(new IObserver<IdEventArgs>() {
				public void update(IObservable<IdEventArgs> observable, IdEventArgs args) {
					int id = args.getId();
					System.out.println("  Deleting hand trail " + id);
					projectiveHandTrails.remove(id);
					if (projectiveHandTrails.isEmpty()) {
						System.out.println("  No hand trails left...");
					}

					handsListener.removeHandMovesQueue(id);
				}
			});

			// no active hand point, which triggers refocusing (P4)
			pointCtrl.getNoPointsEvent().addObserver(new IObserver<NullEventArgs>() {
				public void update(IObservable<NullEventArgs> observable, NullEventArgs args) {
					if (sessionState != SessionState.NOT_IN_SESSION) {
						System.out.println("  Lost hand point, so refocusing");
						sessionState = SessionState.QUICK_REFOCUS;
					}
				}
			});

		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return pointCtrl;
	}

	public Dimension getPreferredSize() {
		return new Dimension(imWidth, imHeight);
	}

	public void closeDown() {
		isRunning = false;
	}

	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				context.waitAnyUpdateAll();
				sessionMan.update(context);
			} catch (StatusException e) {
				System.out.println(e);
				System.exit(1);
			}
			long startTime = System.currentTimeMillis();
			updateCameraImage();
			totalTime += (System.currentTimeMillis() - startTime);
			repaint();
		}

		try {
			context.stopGeneratingAll();
		} catch (StatusException e) {}

		context.release();
		System.exit(1);
	}

	private void updateCameraImage() {
		try {
			ByteBuffer imageBB = imageGen.getImageMap().createByteBuffer();
			image = bufToImage(imageBB);
			imageCount++;
		} catch (GeneralException e) {
			System.out.println(e);
		}
	}

	private BufferedImage bufToImage(ByteBuffer pixelsRGB) {
		int[] pixelInts = new int[imWidth * imHeight];

		int rowStart = 0;

		int bbIdx; // index into ByteBuffer
		int i = 0; // index into pixels int[]
		int rowLen = imWidth * 3; // number of bytes in each row
		for (int row = 0; row < imHeight; row++) {
			bbIdx = rowStart;
			for (int col = 0; col < imWidth; col++) {
				int pixR = pixelsRGB.get(bbIdx++);
				int pixG = pixelsRGB.get(bbIdx++);
				int pixB = pixelsRGB.get(bbIdx++);
				pixelInts[i++] = 0xFF000000 | ((pixR & 0xFF) << 16) | ((pixG & 0xFF) << 8) | (pixB & 0xFF);
			}
			rowStart += rowLen;
		}

		BufferedImage im = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
		im.setRGB(0, 0, imWidth, imHeight, pixelInts, 0, imWidth);
		return im;
	}

	// -------------------- drawing ---------------------------------

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		if (image != null)
			g2.drawImage(image, 0, 0, this); // draw camera's image

		drawTrails(g2);
		writeMessage(g2);

		writeStats(g2);
	}

	private void drawTrails(Graphics2D g2) {
		
		HashMap<Integer, ProjectiveHandTrail> toDraw = new HashMap<Integer, ProjectiveHandTrail>(projectiveHandTrails);
		for (int id : toDraw.keySet()) {
			ProjectiveHandTrail handTrail = toDraw.get(id);
			handTrail.draw(g2);
		}
	}

	private void writeMessage(Graphics2D g2) {
		g2.setColor(Color.YELLOW);
		g2.setFont(msgFont);

		String msg = null;
		switch (sessionState) {
		case IN_SESSION:
			if (projectiveHandTrails.size() == 1)
				msg = "Bring your second hand close to your first to track it";
			else
				msg = "Tracking " + projectiveHandTrails.size() + " hands...";
			break;
		case NOT_IN_SESSION:
			msg = "Click/Wave to start tracking";
			break;
		case QUICK_REFOCUS:
			msg = "Click/Wave/Raise your hand to resume tracking";
			break;
		}
		if (msg != null)
			g2.drawString(msg, 5, 20);
	}

	private void writeStats(Graphics2D g2) {
		g2.setColor(Color.YELLOW);
		g2.setFont(msgFont);
		int panelHeight = getHeight();
		if (imageCount > 0) {
			double avgGrabTime = (double) totalTime / imageCount;
			g2.drawString("Pic " + imageCount + "  " + df.format(avgGrabTime) + " ms", 5, panelHeight - 10);
		} else
			// no image yet
			g2.drawString("Loading...", 5, panelHeight - 10);
	}
}
