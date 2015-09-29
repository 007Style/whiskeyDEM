/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This simply send the input to the server.
*/

import java.io.*;
import java.net.*;

//This class reads from terminal and sends out to server through socket

public class ClientTo extends Thread
{
  String input = "";
  String id = "";
  OutputStream os;      // output stream
  BufferedReader inhere = new BufferedReader(new InputStreamReader(System.in));

  public ClientTo(OutputStream out, String eyedee)
  {
    os = out;  //I pass it both the output stream and the ID.
    id = eyedee;  //The ID is for the server's use
  }

  public void run()
  {
    byte[] outbytes;
    try
    { //The first thing the server recieves is the ID to set up talking for
      outbytes = (id + "\n").getBytes();  //  this client
      System.out.println("Your username is: " + id);
      System.out.println("------------------------------------------------------------");
      System.out.println(" ");
      os.write(outbytes);
      os.flush();
    }
    catch (java.io.IOException e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

    try
    {
      while(true)
      {
        //System.out.print("?");  //My attempt at a prompt
        try
        {
          input = inhere.readLine();
        }
        catch(IOException e)
        {
          System.out.println(e);
        }
        outbytes = (input).getBytes();  //This is the main sending loop
        os.write(outbytes, 0, outbytes.length);
        os.write("\n".getBytes());  //Reads from terminal then sends
                os.flush();  //  through socket
        if(input.equals("%q"))  //%q exits
        {
          System.out.println("Quiting...");
          System.exit(0);
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
