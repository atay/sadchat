package org.my.chat;

import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.media.control.VideoControl;

public class VideoRecord implements CommandListener {
	private Display display;
	private Form form;
	private MyChatMidlet parent;
	private Command exit, back, capture, camera;
	private Player player;
	private VideoControl videoControl;
	private Video video;

	public VideoRecord(MyChatMidlet mid) {
		this.parent=mid;
		display = Display.getDisplay(this.parent);
		form = new Form("Capture Video");
		exit = new Command("Exit", Command.EXIT, 0);
		camera = new Command("Camera", Command.SCREEN, 1);
		back = new Command("Back", Command.BACK, 2);
		capture = new Command("Capture", Command.SCREEN, 3);
		form.addCommand(camera);
		form.addCommand(back);
		form.setCommandListener(this);
		display.setCurrent(form);
	}

	public void commandAction(Command c, Displayable s) {
		String label = c.getLabel();
		if (label.equals("Exit")) {
			parent.display.setCurrent(parent.fchat);
		} else if (label.equals("Camera")) {
			showCamera();
		} else if (label.equals("Back"))
			parent.display.setCurrent(parent.fchat);
		else if (label.equals("Capture")) {
			video = new Video(this);
			video.start();
		}
	}

	public void showCamera() {
		try {
			player = Manager.createPlayer("capture://video");
			player.realize();
			videoControl = (VideoControl) player.getControl("VideoControl");
			Canvas canvas = new VideoCanvas(this, videoControl);
			canvas.addCommand(back);
			canvas.addCommand(capture);
			canvas.setCommandListener(this);
			display.setCurrent(canvas);
			player.start();
		} catch (IOException ioe) {
		} catch (MediaException me) {
		}
	}

	class Video extends Thread {
		VideoRecord midlet;

		public Video(VideoRecord midlet) {
			this.midlet = midlet;
		}

		public void run() {
			captureVideo();
		}

		public void captureVideo() {
			try {
				byte[] photo = videoControl.getSnapshot(null);
				Image image = Image.createImage(photo, 0, photo.length);
				form.append(image);
				display.setCurrent(form);
				player.close();
				player = null;
				videoControl = null;
			} catch (MediaException me) {
			}
		}
	};
}

class VideoCanvas extends Canvas {
	private VideoRecord midlet;

	public VideoCanvas(VideoRecord midlet, VideoControl videoControl) {
		int width = getWidth();
		int height = getHeight();
		this.midlet = midlet;

		videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this);
		try {
			videoControl.setDisplayLocation(2, 2);
			videoControl.setDisplaySize(width - 4, height - 4);
		} catch (MediaException me) {
		}
		videoControl.setVisible(true);
	}

	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		g.setColor(255, 0, 0);
		g.drawRect(0, 0, width - 1, height - 1);
		g.drawRect(1, 1, width - 3, height - 3);
	}
}