/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This is the executable for the ipKchat text client.  As it stands from
version 1.8 this is an undocumented feature and add on.  In general 
this should not be used.  But, if it has to be used it is the basis
from which the GUI version was constructed.  Very simple, this class
instantiates two threads, 'ClientFrom' to type the input to the screen
and 'ClientTo' to send the output to the server.
*/

import java.io.*;
import java.net.*;


//The Client class

public class Client
{
  static int port = 2222;       // default server port
  static String host = "localhost";      // default server host
  static String id = "DualDog";



  public static void main(String [] argv)
  {
    String usage = "usage: java DemoClient [-h host] [-p port] ID " +
          "\n (default port=" + port +", host=" + host + ")";
    try
    {
      Arguments arguments = new Arguments(argv);  //Start parsing commmand line
      if (arguments.isSpecified("h"))
        host = arguments.getModified("h");
      if (arguments.isSpecified("p"))
        port = arguments.getModifiedInt("p");
      if (arguments.getUnmodified().size() > 0)
        id = (String) arguments.getUnmodified().get(0);  //End parse
    }
    catch (NumberFormatException e)
    {
      System.err.println("bad number format");
      System.exit(1);
    }
    System.out.println("");
    System.out.println("Port: " + port);
    System.out.println("Host: " + host);
    System.out.println("ID: " + id);
    System.out.println("");

    doDaWork();
  }

  protected static void doDaWork()  //Sets up socket communication
  {
    try
    {
      Socket s = new Socket(host, port);
      OutputStream os = s.getOutputStream();
      BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));

      System.out.println("Bringing up Threads...");
      ClientFrom f = new ClientFrom(is);
      f.start();  //Starts recieving thread...
      ClientTo t = new ClientTo(os, id);
      t.start();  //Start sending thread...
    }  //That's it, now look at "ClientFrom.java" and "ClientTo.java"
    catch (UnknownHostException e)
    {
      System.err.println("unknown host: " + host);
      System.exit(1);
    }
    catch (IOException e)
    {
      System.out.println(e.getMessage());
      System.out.println("Quiting...");
      //e.printStackTrace();
    }
  }
}





