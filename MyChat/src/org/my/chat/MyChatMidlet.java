package org.my.chat;

import java.io.ByteArrayOutputStream;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class MyChatMidlet extends MIDlet implements CommandListener {

	private Alert alarm;
	public static Display display;
	public Form fopt, fwait, fchat, ffailed;
	public TextField it;
	private StringItem si;
	public TextField adres, nick;
	public ChoiceGroup tryb;
	private ChoiceGroup opcje;
	boolean newline = true;
	private ServerCon watek;
	private String rozmowca = "??";

	private VideoRecord videoRecord;
	StringBuffer sb;
	public Command exitCommand = new Command("Exit", Command.EXIT, 1);
	private Command connectCommand = new Command("Connect", Command.SCREEN, 0);
	private Command sendCommand = new Command("Send", Command.ITEM, 0);
	private Command abortCommand = new Command("Abort", Command.CANCEL, 1);
	private Command changeCommand = new Command("Voice", Command.SCREEN, 1);
	private Command videoCommand = new Command("Video", Command.SCREEN, 1);

	public MyChatMidlet() {
		videoRecord = new VideoRecord(this);
		sb = new StringBuffer();
		display = Display.getDisplay(this);
		fopt = new Form("Time Demo");
		//fopt.addCommand(sendCommand);
		fopt.addCommand(exitCommand);
		fopt.addCommand(connectCommand);
		
		fopt.setCommandListener(this);
		adres = new TextField("Adres", "192.168.0.1", 100, 0);
		nick = new TextField("nick", "atay", 15, 0);
		tryb = new ChoiceGroup("Tryb połączenia", Choice.EXCLUSIVE,
				new String[] { "klient", "serwer" }, null);
		opcje = new ChoiceGroup("Opcje", Choice.MULTIPLE, new String[] {
				"alarm wibracyjny", "podświetl ekran" }, null);

		fopt.append(adres);
		fopt.append(nick);
		fopt.append(tryb);
		fopt.append(opcje);
		display.setCurrent(fopt);

	}

	public void startApp() {

	}

	public void wyslijTekst(String tekst) {
		watek.wyslijTekst(tekst);

	}

	public void wyslijDzwiek(byte[] tab) {
		watek.wyslijDzwiek(tab);

	}

	public void pauseApp() {
	}

	public void oczekujePolaczenie(String adres, int i) {
		fwait = new Form("Chat client");
		si = new StringItem("Serwer gotowy", "Oczekuje: " + adres + ":" + i);
		fwait.append(si);
		display.setCurrent(fwait);

	}

	public void odebranoLinie(StringBuffer str) {
		ChatStringItem newsi=null;		
		String tekst = str.toString();
		if (tekst.startsWith("login:")) {
			String nick = tekst.substring("login:".length());
			tekst = rozmowca + " zmienil nick na " + nick;
			rozmowca = nick;
		}
		if (tekst.startsWith("audio:")) {
			int dlugosc = Integer.parseInt(tekst.substring("audio:".length()));
			watek.wylaczOdbieranie();
			ByteArrayOutputStream audioFile=watek.pobierzDzwiek(dlugosc);
			watek.wlaczOdbieranie();
			tekst = "Odebrano plik audio (" + dlugosc + ")";
			newsi = new AudioStringItem(tekst, audioFile);


		}

		else if (tekst.startsWith("/me ")) {
			String corobi = tekst.substring("/me ".length());
			tekst = rozmowca + " " + corobi;
		} else {
			tekst = "<" + rozmowca + ">" + tekst;

		}
		if (newsi==null){
			newsi = new TextStringItem(tekst);
		}
		//newsi.setLayout(StringItem.LAYOUT_EXPAND);
		if (opcje.isSelected(0))
			display.vibrate(100);
		if (opcje.isSelected(1))
			display.flashBacklight(100);
		fchat.insert(fchat.size()-1, newsi);
		

	}
	public void echoMojejWypowiedzi(String wypowiedz) {
		wypowiedz = "<" + nick.getString() + ">" + wypowiedz;
		ChatStringItem newsi = new TextStringItem(wypowiedz);
		fchat.insert(fchat.size()-1, newsi);

	}

	public void destroyApp(boolean unconditional) {
	}

	public void pokazKomunikat(String jaki) {
		alarm = new Alert(jaki);
		alarm.setTimeout(3000);
		display.setCurrent(alarm);
	}

	public void commandAction(Command c, Displayable s) {

		if (c == exitCommand) {
			destroyApp(true);
			notifyDestroyed();
		}

		if (c == sendCommand) {
			String tekst = it.getString();
			if (tekst.length() == 0)
				return;

			Thread send = new Thread(new Runnable() {
				public void run() {
					watek.wyslijTekst(it.getString() + "\n");
					it.setString("");
				}
			});
			send.start();
			echoMojejWypowiedzi(it.getString());

		}

		if (c == connectCommand) {

			watek = new ServerCon(this, adres.getString());
			watek.start();
			fwait = new Form("Chat client");
			si = new StringItem("", "Łączenie w trakcie");
			fwait.addCommand(abortCommand);
			fwait.append(si);
			fwait.setCommandListener(this);
			display.setCurrent(fwait);

		}

		if (c == abortCommand) {

			pokazKomunikat("Przerwano probe polaczenia");
			display.setCurrent(fopt);

		}

		if (c == changeCommand) {
			VoiceRecord vr= new VoiceRecord(this);
			vr.MainForm();
		}
		if (c == videoCommand){
			videoRecord.showCamera();
		}

	}

	public void budujChat() {

		fchat = new Form("Chat client");
		si = new StringItem("Chat:", " ");
		it = new TextField("", "", 200, 0);
		fchat.append(si);
		fchat.append(it);
		fchat.addCommand(changeCommand);
		fchat.addCommand(sendCommand);
		fchat.addCommand(videoCommand);
		fchat.setCommandListener(this);
		display.setCurrent(fchat);

	}
}