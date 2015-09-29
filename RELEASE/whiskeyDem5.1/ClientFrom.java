/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */

/*
This simply prints the output from the server to the screen.
*/

import java.io.*;
import java.net.*;

//This class recieves input and prints them...

public class ClientFrom extends Thread
{
  BufferedReader is;    // intput stream

  public ClientFrom(BufferedReader in)
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
        //System.out.print("?");  //My attempt at a prompt :)
        System.out.println(cmd);
      }
    }
    catch (java.io.IOException e)
    {
      //System.out.println(e.getMessage());
      //e.printStackTrace();
    }
  }
}