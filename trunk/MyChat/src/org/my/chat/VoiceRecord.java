package org.my.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;

public class VoiceRecord implements CommandListener {

	public Form fwalkie;

	private Command recordCommand, cancelCommand, sendCommand, backCommand;

	private MyChatMidlet parent;

	private StringItem messageItem, errorItem;

	private Player p;
	private RecordControl rc;
	public byte[] recordedSoundArray = null;
	ByteArrayOutputStream output;

	public VoiceRecord(MyChatMidlet mid) {
		parent = mid;
	}

	public void MainForm() {
		fwalkie = new Form("Walkie talkie");
		messageItem = new StringItem("Record",
				"Click record to start recording.");
		fwalkie.append(messageItem);
		errorItem = new StringItem("", "");

		fwalkie.append(errorItem);
		recordCommand = new Command("Record", Command.SCREEN, 1);
		sendCommand = new Command("Send", Command.SCREEN, 3);
		backCommand = new Command("Back to chat", Command.EXIT, 4);
		cancelCommand = new Command("Cancel", Command.CANCEL, 4);
		fwalkie.addCommand(backCommand);
		fwalkie.addCommand(recordCommand);
		fwalkie.setCommandListener(this);
		parent.display.setCurrent(fwalkie);
	}

	public void commandAction(Command c, Displayable s) {

		if (c == recordCommand) {
			try {
				p = Manager.createPlayer("capture://audio?encoding=gsm");
				p.realize();
				rc = (RecordControl) p.getControl("RecordControl");
				output = new ByteArrayOutputStream();
				rc.setRecordStream(output);
				rc.startRecord();
				p.start();
				messageItem.setText("recording...");
				fwalkie.removeCommand(recordCommand);
				fwalkie.removeCommand(backCommand);
				fwalkie.addCommand(sendCommand);
				fwalkie.addCommand(cancelCommand);
			} catch (IOException e) {
				errorItem.setLabel("Error");
				errorItem.setText(e.toString());
			} catch (MediaException e) {
				errorItem.setLabel("Error");
				errorItem.setText(e.toString());
			}
		}
		if (c==cancelCommand){
			try {
				rc.commit();
				p.close();
				fwalkie.removeCommand(cancelCommand);
				fwalkie.removeCommand(sendCommand);
				fwalkie.addCommand(recordCommand);
				fwalkie.addCommand(backCommand);
				messageItem.setText("recording canceled");
			} catch (IOException e) {
				errorItem.setLabel("Error");
				errorItem.setText(e.toString());
			}
			
		}
		if (c == sendCommand) {
			try {
				rc.commit();
				recordedSoundArray = output.toByteArray();
				messageItem.setText("sent " + recordedSoundArray.length + " bytes");
				p.close();
				fwalkie.removeCommand(sendCommand);
				fwalkie.removeCommand(cancelCommand);
				fwalkie.addCommand(recordCommand);
				fwalkie.addCommand(backCommand);
				parent.wyslijDzwiek(recordedSoundArray);
			} catch (IOException e) {
				errorItem.setLabel("Error");
				errorItem.setText(e.toString());
			}
		}

		if (c == backCommand) {
				parent.display.setCurrent(parent.fchat);
		}

	}

}
