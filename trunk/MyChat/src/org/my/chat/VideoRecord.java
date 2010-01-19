package org.my.chat;

import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.VideoControl;

public class VideoRecord implements CommandListener {
	private Display display;
	private Form form;
	private MyChatMidlet parent;
	private Player player;
	private VideoControl videoControl;
	private Video video;
	private Command captureCommand, sendCommand, cancelCommand, backCommand;
	private byte[] photo;

	public VideoRecord(MyChatMidlet mid) {
		this.parent=mid;
		display = Display.getDisplay(this.parent);
		form = new Form("Capture Video");
		backCommand = new Command("Back to chat", Command.EXIT, 4);
		captureCommand = new Command("Capture", Command.SCREEN, 1);
		sendCommand = new Command("Send", Command.SCREEN, 3);
		cancelCommand = new Command("Cancel", Command.CANCEL, 4);
		form.setCommandListener(this);
		display.setCurrent(form);
		
	}

	public void commandAction(Command c, Displayable s) {
		
		if (c == captureCommand) {
			video = new Video(this);
			video.start();
			
		} else if (c == backCommand) {
			parent.display.setCurrent(parent.fchat);
		
		} else if (c == sendCommand) {
			WyslijZdjecie(photo);
			showCamera();
			
		} else if (c == cancelCommand) {
			showCamera();
		}
	}
	
	public void WyslijZdjecie(byte[] zdjecie)
	{
		parent.wyslijZdjecie(zdjecie);
	}

	public void showCamera() {
		try {
			player = Manager.createPlayer("capture://video");
			player.realize();
			videoControl = (VideoControl) player.getControl("VideoControl");
			Canvas canvas = new VideoCanvas(this, videoControl);
			canvas.addCommand(backCommand);
			canvas.addCommand(captureCommand);
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
				photo = videoControl.getSnapshot(null);
				
				Image image = Image.createImage(photo, 0, photo.length);

				form.deleteAll();
				form.append(image);
				form.addCommand(sendCommand);
				form.addCommand(cancelCommand);
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
			videoControl.setDisplayLocation(0, 0);
			videoControl.setDisplaySize(width, height);
		} catch (MediaException me) {
		}
		videoControl.setVisible(true);
	}

	public void paint(Graphics g) {

	}
}