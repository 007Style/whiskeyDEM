/*
 * worldServer.java
 */
 
import java.io.*;
import java.net.*;

//Sets up the server side

public class worldServer extends Thread
{
  static int port = 2222;
  static int numClients = 0;
  static BufferedReader inhere = new BufferedReader(new InputStreamReader(System.in));
  static String input = "";
  static OutputStream os;      // output stream
  static ObjectOutputStream out = null;
  static Socket s = null;
  private ControlJPanel cjp = null;
  static ObjectOutputStream[] outS;
  static int numComm = -1;
  
  public static void sSend(flightPacket toSend)
  {
  	//System.out.println("******************************" + toSend);
  	//System.out.println(toSend.m_newPos);
  	for(int u=0; u<=numComm; u++)
    {
  	  try
  	  {		
  		  outS[u].writeObject(toSend);
  			outS[u].flush();
  			//out.writeObject(toSend);
  			//out.flush();
      	//s.getOutputStream().write((toSend+"\n").getBytes());
      	//s.getOutputStream().flush();	
      }
      catch (java.io.IOException e)
      {
        //System.out.println(e.getMessage());
        //e.printStackTrace();
      }
    }
  }
  
  public worldServer(ControlJPanel c)
  {
  	outS = new ObjectOutputStream[16];
  	port = ProgramConfig.getServerPort();
  	//System.out.println(port);
  	cjp = c;
    //System.out.println("Server Thread coming up...");	
  }
  
  public void run()
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
        s = ss.accept();    // wait for a connection
        System.out.println("got a connection from " + s.getInetAddress());
        BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        OutputStream os = s.getOutputStream();    // output to socket goes here
        out = new ObjectOutputStream(s.getOutputStream());
        System.out.println("Starting whiskeyDem communication with: " + s.getInetAddress() + ":" + port);
        numComm = numComm + 1;
        outS[numComm] = out;
        
        //cjp.btnCommands.setEnabled(false);
        //cjp.btnNetwork.setEnabled(false);
        //threadsAreDo p = new threadsAreDo(s, id);  //This is the main worker thread
        //p.start();
        //s.getOutputStream().write(("Sonofu beeyatch!\n").getBytes());
        //s.getOutputStream().flush();
      }
    }
    catch (java.io.IOException e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
  	
  
  public worldServer()
  {
  	outS = new ObjectOutputStream[16];
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
        s = ss.accept();    // wait for a connection
        System.out.println("got a connection from " + s.getInetAddress());
        BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        OutputStream os = s.getOutputStream();    // output to socket goes here
        //threadsAreDo p = new threadsAreDo(s, id);  //This is the main worker thread
        //p.start();
        s.getOutputStream().write(("Sonofu beeyatch!\n").getBytes());
        s.getOutputStream().flush();
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
      String id = "";
      System.out.println("waiting for a connection on port " + port);
      Socket s = ss.accept();    // wait for a connection
      System.out.println("got a connection from " + s.getInetAddress());
      BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
      OutputStream os = s.getOutputStream();    // output to socket goes here
      s.getOutputStream().write(("Mothah Fuqah!\n").getBytes());
      s.getOutputStream().flush();
      while(true)  //Sets up incoming connections
      {
        //threadsAreDo p = new threadsAreDo(s, id);  //This is the main worker thread
        //p.start();
        try
        {
          input = inhere.readLine();
        }
        catch(IOException e)
        {
          System.out.println(e);
        }
        byte[] outbytes = (input).getBytes();  //This is the main sending loop
        os.write(outbytes, 0, outbytes.length);
        os.write("\n".getBytes());  //Reads from terminal then sends
        os.flush();  //  through socket
        
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
