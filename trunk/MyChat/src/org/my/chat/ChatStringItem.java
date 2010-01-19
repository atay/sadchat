package org.my.chat;

import javax.microedition.lcdui.StringItem;


public class ChatStringItem extends StringItem{

	public ChatStringItem(String label, String text) {
		super(null,text);
		this.setLayout(StringItem.LAYOUT_NEWLINE_AFTER | StringItem.LAYOUT_2);

	}

	
}
