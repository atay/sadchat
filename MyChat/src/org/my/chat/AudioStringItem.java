package org.my.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

public class AudioStringItem extends ChatStringItem implements
		ItemCommandListener {
	private ByteArrayOutputStream audioFile;
	private Command playCommand = new Command("Play", Command.ITEM, 0);

	public AudioStringItem(String text, ByteArrayOutputStream audioFile) {
		super(null, text);
		this.audioFile = audioFile;
		this.addCommand(playCommand);
		this.setItemCommandListener(this);
		this.commandAction(playCommand, this);
	}

	public void commandAction(Command c, Item item) {
		if (c == playCommand) {
			Player p2;
			try {
				ByteArrayInputStream is = new ByteArrayInputStream(audioFile
						.toByteArray());
				System.out.println(audioFile.toByteArray().length);
				p2 = Manager.createPlayer(is, "audio/gsm");
				p2.prefetch();
				p2.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MediaException e) {
				e.printStackTrace();
			}
		}
	}

}
