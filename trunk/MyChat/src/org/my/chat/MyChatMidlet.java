package org.my.chat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class MyChatMidlet extends MIDlet implements CommandListener {

	private Alert alarm;
	public static Display display;
	public Form fopt, fwait, fchat, ffailed;
	public TextField it;
	public StringItem si, errorItem ;
	public TextField adres, nick;
	public ChoiceGroup tryb;
	private ChoiceGroup opcje;
	boolean newline = true;
	private boolean bTextOn=false;
	private ServerCon watek;
	private String rozmowca = "??";

	private VideoRecord videoRecord;
	StringBuffer sb;
	public Command exitCommand = new Command("Exit", Command.EXIT, 1);
	private Command connectCommand = new Command("Connect", Command.ITEM, 0);
	private Command sendCommand = new Command("Send", Command.ITEM, 0);
	private Command abortCommand = new Command("Abort", Command.CANCEL, 1);
	private Command voiceCommand = new Command("Voice", Command.SCREEN, 1);
	private Command videoCommand = new Command("Video", Command.SCREEN, 1);
	private Command reconnectCommand = new Command("New connection", Command.SCREEN, 1);
	private Command disconnectCommand = new Command("Disconnect", Command.SCREEN, 4);

	public MyChatMidlet() {
		
		
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

		opcje.setSelectedIndex(0, true);


		fopt.append(tryb);
		fopt.append(adres);
		fopt.append(nick);
		
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
	
	public void wyslijZdjecie(byte[] zdjecie)
	{
		watek.wyslijZdjecie(zdjecie);
	}

	public void pauseApp() {
	}

	
	public void odebranoLinie(String tekst) {
		ChatStringItem newsi=null;		
		if (tekst.startsWith("nick:")) {
			String nick = tekst.substring("nick:".length());
			tekst = rozmowca + " zmienil nick na " + nick;
			rozmowca = nick;
		}
		else if (tekst.startsWith("audio:")) {
			int dlugosc = Integer.parseInt(tekst.substring("audio:".length()));
			watek.wylaczOdbieranie();
			ByteArrayOutputStream audioFile=watek.pobierzDane(dlugosc);
			watek.wlaczOdbieranie();
			tekst = "Odebrano plik audio (" + dlugosc + ")";
			newsi = new AudioStringItem(tekst, audioFile);
		}
		else if (tekst.startsWith("photo:")) {
			int dlugosc = Integer.parseInt(tekst.substring("photo:".length()));
			odebranoLinie("info: pobieranie pliku graficznego o dlugosci "+dlugosc);
			watek.wylaczOdbieranie();
			ByteArrayOutputStream photoFile=watek.pobierzDane(dlugosc);
			odebranoLinie("info: odebrano plik graficzny o dlugosci "+dlugosc);
			watek.wlaczOdbieranie();
			
			tekst = "Odebrano plik audio (" + dlugosc + ")";
			newsi = new PhotoStringItem(tekst, photoFile, this);
		}
		else if (tekst.startsWith("error:"))
		{
			String blad = tekst.substring("error:".length());
			tekst="Wystąpił błąd: "+blad;
			
		}
		else if (tekst.startsWith("info:"))
		{
			tekst = tekst.substring("info:".length());

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
		if (opcje.isSelected(0))
			display.vibrate(100);
		if (opcje.isSelected(1))
			display.flashBacklight(100);
		if (bTextOn)
			fchat.insert(fchat.size()-1, newsi);
		else
			fchat.append(newsi);
		
		
	}

	public void odebranoLinie(StringBuffer str) {
		odebranoLinie(str.toString());
		

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

		else if (c == sendCommand) {
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

		else if (c == connectCommand) {

			watek = new ServerCon(this, adres.getString());
			watek.start();

		}

		else if (c == abortCommand) {

			pokazKomunikat("Przerwano probe polaczenia");
			display.setCurrent(fopt);

		}

		else if (c == voiceCommand) {
			VoiceRecord vr= new VoiceRecord(this);
			vr.MainForm();
		}
		else if (c == videoCommand){
			VideoRecord videoRecord = new VideoRecord(this);
			videoRecord.showCamera();
		}
		else if (c == reconnectCommand){
			MyChatMidlet.display.setCurrent(fopt);
		}
		else if (c == disconnectCommand){
			watek.stop();
		}

	}

	public void budujChat() {

		fchat = new Form("Chat client");
		si = new StringItem("Chat:", " ");
		it = new TextField("", "", 200, 0);
		fchat.append(si);
		
		fchat.addCommand(voiceCommand);
		fchat.addCommand(sendCommand);
		fchat.addCommand(videoCommand);
		fchat.addCommand(disconnectCommand);
		fchat.addCommand(exitCommand);
		fchat.setCommandListener(this);
		display.setCurrent(fchat);

	}

	public void wyrzucBlad(IOException e) {
		odebranoLinie(new StringBuffer("error:"+e.getMessage()+ " z klasy " + e.getClass().getName()));
		fchat.removeCommand(sendCommand);
		fchat.removeCommand(videoCommand);
		fchat.removeCommand(voiceCommand);
		fchat.removeCommand(disconnectCommand);
		fchat.addCommand(reconnectCommand);
		this.textOff();
	}
	public void textOn(){
		if (!bTextOn){
			fchat.append(it);
			bTextOn=true;
		}
	}
	public void textOff(){
		if (bTextOn){
			fchat.delete(fchat.size()-1);
			bTextOn=false;
		}
	}


}
