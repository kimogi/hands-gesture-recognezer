package kinect.kimogi.handtracker;
// HandsTracker.java
// Andrew Davison, November 2011, ad@fivedots.psu.ac.th

/* Track multiple hands, displaying a disappearing trail of 
 colour behind each hand.

 For multiple hand detection, the Nite.ini file must be edited
 in C:\Program Files\PrimeSense\NITE\Hands_1_4_0\Data (or whatever
 is the latest version of the Hands directory). Remove the ";"s
 from the start of the two property assignment lines:

 [HandTrackerManager]
 AllowMultipleHands=1  
 TrackAdditionalHands=1
 ^^

 With multiple hand tracking, there is no focus gesture 
 for your second hand.

 Once you have gained focus for your first hand, tracking is started
 on your other hand by bringing it near to the first hand, then
 spreading both hands apart a little.

 Usage:
 > java HandsTracker
 */

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import kinect.kimogi.handtracker.controller.VirtualScreen;

public class HandsTrackerApp extends JFrame {
	private static final long serialVersionUID = -1891195877753310514L;
	private TrackerPanel trackerPanel;
	
	public HandsTrackerApp() {
		super("Hands Tracker");
		
		trackerPanel = new TrackerPanel(this);
		add(trackerPanel);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				trackerPanel.closeDown();
			}
		});

		pack();
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		int screenWidth = (int) rect.getMaxX();
		int screenHeight = (int) rect.getMaxY();
		
		setLocation(screenWidth - getWidth(), screenHeight - getHeight());
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(true);
	}

	public static void main(String args[]) {
		VirtualScreen.init();
		new HandsTrackerApp();
	}
} 
