package org.my.chat;

import javax.microedition.lcdui.StringItem;


public class ChatStringItem extends StringItem{

	public ChatStringItem(String label, String text) {
		super(text,null);
		this.setLayout(LAYOUT_EXPAND);

	}

	
}
