/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This is the executable class.  It parses any command line parameters and waits
for connections.  When a connection is established it checks for a user name
then hands the connection off the threads are do for processing.  It then 
returns to it's wait state to wait for more connections.
*/

import java.io.*;
import java.net.*;

//Sets up the server side

public class Server
{
  static int port = 2222;
  static int numClients = 0;

  public Server()
  {
        System.out.println("");
    System.out.println("Port: " + port);
    System.out.println("Server Coming up...  Please Wait... ");
    System.out.println("");

    try
    {
      ServerSocket ss = new ServerSocket(port);
      while(true)  //Sets up incoming connections
      {
        String id = "";
        System.out.println("waiting for a connection on port " + port);
        Socket s = ss.accept();    // wait for a connection
        System.out.println("got a connection from " + s.getInetAddress());
        BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        OutputStream os = s.getOutputStream();    // output to socket goes here
        id = is.readLine();  //Reads in the ID for use in setting up hash table
        System.out.println(id + " has logged on...");
        threadsAreDo p = new threadsAreDo(s, id);  //This is the main worker thread
        p.start();
      }
    }
    catch (java.io.IOException e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
        
        
  public static void main(String[] argv)
  {  //Parsing the usual stuff...
    String usage = "usage: java DemoServer [-p port] (default=" + port +")";
    try
    {
      for (int optind = 0; optind < argv.length; ++optind)
      {
        if (argv[optind].equals("-p"))
        {    // -p port
          if (++optind >= argv.length)
          {
            System.err.println(usage);
            System.exit(1);
          }
          port = (new Integer(argv[optind])).intValue();
        }
        else
        {
          System.err.println(usage);
          System.exit(1);
        }
      }
    }
    catch (NumberFormatException e)
    {
      System.err.println("bad number format");
      System.exit(1);
    }
    System.out.println("");
    System.out.println("Port: " + port);
    System.out.println("Server Coming up...  Please Wait... ");
    System.out.println("");

    try
    {
      ServerSocket ss = new ServerSocket(port);
      while(true)  //Sets up incoming connections
      {
        String id = "";
        System.out.println("waiting for a connection on port " + port);
        Socket s = ss.accept();    // wait for a connection
        System.out.println("got a connection from " + s.getInetAddress());
        BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        OutputStream os = s.getOutputStream();    // output to socket goes here
        id = is.readLine();  //Reads in the ID for use in setting up hash table
        System.out.println(id + " has logged on...");
        threadsAreDo p = new threadsAreDo(s, id);  //This is the main worker thread
        p.start();
      }
    }
    catch (java.io.IOException e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

//End class Server
