package org.my.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.midlet.MIDlet;

public class PhotoStringItem extends ChatStringItem implements
		ItemCommandListener {
	private ByteArrayOutputStream photoFile;
	private Command playCommand = new Command("Show", Command.ITEM, 0);
	private MyChatMidlet parent;
	private Command backCommand;

	public PhotoStringItem(String text, ByteArrayOutputStream photoFile,
			MyChatMidlet parent) {
		super(null, text);
		this.parent = parent;

		this.photoFile = photoFile;
		this.addCommand(playCommand);
		this.setItemCommandListener(this);
		this.commandAction(playCommand, this);
		backCommand = new Command("Back to chat", Command.EXIT, 4);
	}

	public void commandAction(Command c, Item item) {
		if (c == playCommand) {
			Form f = new Form("Image preview");
			ByteArrayInputStream is = new ByteArrayInputStream(photoFile.toByteArray());

			try {
				Image image = Image.createImage(is);
				f.append(image); 
				f.addCommand(backCommand);
				parent.display.setCurrent(f);				
			} catch (IOException e) {
				parent.wyrzucBlad(e);
			}
			
		}
		else if (c == backCommand)
		{
			parent.display.setCurrent(parent.fchat);
		}
	}
}
