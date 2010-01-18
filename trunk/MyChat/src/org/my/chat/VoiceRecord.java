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
	
	 private Command recordCommand, playCommand, sendCommand,backCommand;

	   private MyChatMidlet parent;
	   
	   private StringItem messageItem, errorItem;
	   
	   private Player p;
	   public byte[] recordedSoundArray = null;
	
	public VoiceRecord(MyChatMidlet mid) {
		parent = mid;
	}
	
	public void MainForm()
	{  	  
	  fwalkie = new Form("Walkie talkie");
	  messageItem = new StringItem("Record", "Click record to start recording.");
      fwalkie.append(messageItem);
      errorItem = new StringItem("Actual filesize", "");
      
      fwalkie.append(errorItem);
      recordCommand = new Command("Record", Command.ITEM, 1);
      fwalkie.addCommand(recordCommand);
      sendCommand = new Command("Send", Command.ITEM, 3);
      fwalkie.addCommand(sendCommand);
      playCommand = new Command("Play", Command.ITEM, 3);
      fwalkie.addCommand(playCommand);
      backCommand = new Command("Back to chat", Command.ITEM, 4);
      fwalkie.addCommand(backCommand);
      fwalkie.setCommandListener(this);
      parent.display.setCurrent(fwalkie);

	}
	
	   public void commandAction(Command c, Displayable s) {
		   
		      if(c == recordCommand){
		          try{                
		              p = Manager.createPlayer("capture://audio?encoding=gsm");
		              p.realize();                
		              RecordControl rc = (RecordControl)p.getControl("RecordControl");
		              ByteArrayOutputStream output = new ByteArrayOutputStream();
		              rc.setRecordStream(output);                
		              rc.startRecord();
		              p.start();
		              messageItem.setText("recording...");
		              Thread.currentThread().sleep(5000);
		              rc.commit();               
		              recordedSoundArray = output.toByteArray();    
		              messageItem.setText("done!" + recordedSoundArray.length);
		              p.close();
		          } catch (IOException ioe) {
		              errorItem.setLabel("Error");
		              errorItem.setText(ioe.toString());
		          } catch (MediaException me) {
		              errorItem.setLabel("Error");
		              errorItem.setText(me.toString());
		          } catch (InterruptedException ie) {
		              errorItem.setLabel("Error");
		              errorItem.setText(ie.toString());
		          }
		      }
		      if(c == playCommand) {
		          try {
		              ByteArrayInputStream recordedInputStream = new ByteArrayInputStream
		                    (recordedSoundArray);
		              Player p2 = Manager.createPlayer(recordedInputStream,"audio/gsm");
		              p2.prefetch();
		              p2.start();
		          }  catch (IOException ioe) {
		              errorItem.setLabel("Error");
		              errorItem.setText(ioe.toString());
		          } catch (MediaException me) {
		              errorItem.setLabel("Error");
		              errorItem.setText(me.toString());
		          }
		      }
		      if(c == sendCommand) {
		    	  
		    	  parent.wyslijDzwiek(recordedSoundArray);
		          
		      }
		      
		      
		      if(c == backCommand) {
		          parent.display.setCurrent(parent.fchat);
		      }
		   
	   }

}
