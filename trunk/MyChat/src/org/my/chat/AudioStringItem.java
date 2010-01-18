package org.my.chat;

import java.io.ByteArrayOutputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

public class AudioStringItem extends ChatStringItem implements CommandListener {
	private ByteArrayOutputStream audioFile;
	private Command playCommand = new Command("Play", Command.ITEM, 0);

	public AudioStringItem(String text, ByteArrayOutputStream audioFile) {
		super(null, text);
		this.audioFile=audioFile;
		this.addCommand(playCommand);
	}

	public void commandAction(Command c, Displayable d) {
		if (c==playCommand){
			
		}
		
	}


}
