package org.my.chat;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;

public class TextStringItem extends ChatStringItem implements ItemCommandListener{
	private Command playCommand = new Command("Play", Command.ITEM, 0);
	

	public TextStringItem(String text) {
		super(null, text);
	}


	public void commandAction(Command c, Item item) {

		
	}


}
