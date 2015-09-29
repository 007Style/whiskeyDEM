/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This class listens on the socket from ipKchat and will print any thing 
that comes in to the main dialogue box in ipKchat.  It will exit when 
the connection is terminated.
*/

import java.io.*;
import java.net.*;
import java.applet.*;

//This class recieves input and prints them...

public class ipKFrom extends Thread
{
  BufferedReader is;    // intput stream

  public ipKFrom(BufferedReader in)
  {
    is = in;
  }

  public void run()
  {
    try
    {
      String cmd;
      while((cmd = is.readLine()) != null)  //Reads from socket and prints
      {                                     //  to terminal
        if(cmd.equals("ALERT") && ipKchat.cboxM.getState() == true)
        {  //Plays the audio alert when some one connects to the client if it is selected in the GUI
          try
          {
            String bufferFile = "alert.wav";
            URL codeBase = new URL("file:" + System.getProperty("user.dir") + "/" + bufferFile);
            AudioClip clip = Applet.newAudioClip(codeBase);
            clip.play();
          }
          catch(Exception e)
          {
            System.out.println(e);
          }
        }
        else if(cmd.equals("ALERT")) {}
        else if(cmd.equals("QUIT"))
        {
          ipKchat.Connect.setEnabled(true);
          ipKchat.Disconnect.setEnabled(false); 
          ipKchat.s.close();
        }
        else
        {
                ipKchat.ta1.append(cmd + "\n");
        }
      }
    }
    catch (java.io.IOException e)
    {
      //System.out.println(e.getMessage());
      //e.printStackTrace();
    }
  }
}