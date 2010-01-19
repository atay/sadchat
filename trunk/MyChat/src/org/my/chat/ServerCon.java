package org.my.chat;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import java.io.*;

public class ServerCon implements Runnable {
	private MyChatMidlet parent;
	private Display display;
	private SocketConnection sc;
	private ServerSocketConnection ssc;

	private InputStream is;
	private OutputStream os;
	private String adres = "atay.pl";
	private boolean enable;

	public ServerCon(MyChatMidlet mid, String s) {
		parent = mid;
		adres = s;
	}

	public void start() {
		enable = true;
		Thread t = new Thread(this);
		t.start();
	}

	public void stop() {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			parent.wyrzucBlad(e);
		}

	}

	public void wylaczOdbieranie() {
		enable = false;
	}

	public void wlaczOdbieranie() {
		enable = true;
	}

	public ByteArrayOutputStream pobierzDane(int dlugosc) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		for (int i = 0; i < dlugosc; i++)
			try {
				output.write(is.read());
			} catch (IOException e) {
				parent.wyrzucBlad(e);
			}
		return output;
	}

	public void run() {

		parent.budujChat();
		try {
			if (parent.tryb.isSelected(1)) {
				ssc = (ServerSocketConnection) Connector
						.open("socket://:12341");
				parent.odebranoLinie("info:Oczekuje polaczenie: "
						+ ssc.getLocalAddress());
				sc = (SocketConnection) ssc.acceptAndOpen();

			} else {
				sc = (SocketConnection) Connector.open("socket://" + adres
						+ ":12341");
			}
			parent.odebranoLinie("info:Connected with : " + sc.getAddress());
			parent.textOn();
			os = sc.openOutputStream();

			is = sc.openInputStream();

			int c = 0;

			wyslijTekst(new String("nick:" + parent.nick.getString() + "\n"));

			StringBuffer bufor = new StringBuffer();

			while ((c = is.read()) != '.' && (c != -1)) {
				if (c != 10)
					bufor.append((char) c);
				else {
					parent.odebranoLinie(bufor);
					bufor.delete(0, bufor.length());
					while (!enable) {
					}
				}
			}

		} catch (IOException e) {

			parent.wyrzucBlad(e);

		} finally {
			parent.textOff();
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}

				if (sc != null) {
					sc.close();
				}
				if (ssc != null) {
					ssc.close();
				}

			} catch (IOException e) {
				parent.wyrzucBlad(e);
			}
		}

	}

	public int wyslijDzwiek(byte[] tab) {
		wyslijTekst("audio:" + tab.length + "\n");

		try {
			for (int i = 0; i < tab.length; i++)
				os.write(tab[i]);

			os.flush();
		} catch (IOException e) {
			parent.wyrzucBlad(e);
		}
		parent.odebranoLinie("info: audio sent " + tab.length + " bytes");
		return 0;
	}

	public void wyslijZdjecie(byte[] tab) {
		
		wyslijTekst("video:" + tab.length + "\n");
		try {
			for (int i = 0; i < tab.length; i++)
				os.write(tab[i]);

			os.flush();
		} catch (IOException e) {
			parent.wyrzucBlad(e);
		}
		parent.odebranoLinie("info: photo sent " + tab.length + " bytes");
		
	}

	public int wyslijTekst(String tekst) {

		for (int i = 0; i < tekst.length(); i++)
			try {
				os.write(tekst.charAt(i));
				os.flush();
			} catch (IOException e) {
				parent.wyrzucBlad(e);

			}

		return 0;
	}

}