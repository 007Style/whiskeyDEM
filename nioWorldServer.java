/*
 * nioWorldServer.java
 */
 
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

//Sets up the server side

public class nioWorldServer extends Thread
{
  static int port = 2222;
  static int numClients = 0;
  static BufferedReader inhere = new BufferedReader(new InputStreamReader(System.in));
  static String input = "";
  static OutputStream os;      // output stream
  static Socket s = null;
  static private ServerSocketChannel ssc = null;
  static private SocketChannel sc = null; 
  private ControlJPanel cjp = null;
  static ObjectOutputStream[] outS;
  static OutputStream[] osArray; 
  static PrintWriter[] pwArray;
  static SocketChannel[] scArray;
  static int numComm = -1;
  private LinkedList nioSyncQ = null;
  private static ByteBuffer dst;
  private static PrintWriter out;
  
  public static void sSend(flightPacket toSend)
  {
  	String send = "/*" + toSend.m_newPos + toSend.m_travel + toSend.m_distanceTraveled + "," + toSend.sync + "," + toSend.fps + "*/";
  	//System.out.println("Sending Packet: " + send);
  	for(int u=0; u<=numComm; u++)
    {
  	  //try
  	  {		
  	  	//dst.put(send.getBytes());
  	  	//scArray[u].write(dst);
  	  	pwArray[u].println(send);
  	  	//pwArray[u].flush();
  	  	//osArray[u].write(send.getBytes());  
  			//osArray[u].flush();
      }
      //catch (java.io.IOException e)
      {
        //System.out.println(e.getMessage());
        //e.printStackTrace();
      }
    }
  }
  
  public nioWorldServer(ControlJPanel c)
  {
  	dst = ByteBuffer.allocateDirect(1048576);  // A one MegaByte buffer.  Was 8192 Bytes.
    dst.clear();
  	//outS = new ObjectOutputStream[16];
  	osArray = new OutputStream[16];
  	pwArray = new PrintWriter[16];
  	scArray = new SocketChannel[16];
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
      ssc = ServerSocketChannel.open();
      ssc.configureBlocking(true);
      InetAddress lh = InetAddress.getLocalHost();
			InetSocketAddress isa = new InetSocketAddress(lh, port);
			ssc.socket().bind(isa);
      while(true)  //Sets up incoming connections
      {
        String id = "";
        System.out.println("waiting for a connection on port " + port);
        sc = ssc.accept();    // wait for a connection
        sc.configureBlocking(true);
        if(sc != null)
        {
        	s = sc.socket();
        	System.out.println("got a connection from " + s.getInetAddress());
        	//BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        	//OutputStream os = s.getOutputStream();    // output to socket goes here
        	//out = new ObjectOutputStream(s.getOutputStream());
        	out = new PrintWriter(s.getOutputStream(), true);
        	System.out.println("Starting whiskeyDem communication with: " + s.getInetAddress() + ":" + port);
        	numComm = numComm + 1;
        	//outS[numComm] = out;
        	pwArray[numComm] = out;
        	scArray[numComm] = sc;
	        
        	//cjp.btnCommands.setEnabled(false);
        	//cjp.btnNetwork.setEnabled(false);
        	//threadsAreDo p = new threadsAreDo(s, id);  //This is the main worker thread
       		//p.start();
	        //s.getOutputStream().write(("Sonofu beeyatch!\n").getBytes());
        	//s.getOutputStream().flush();
      	}
      }
    }
    catch (java.io.IOException e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
  	
  
  public nioWorldServer()
  {
  	dst = ByteBuffer.allocateDirect(1048576);  // A one MegaByte buffer.  Was 8192 Bytes.
    dst.clear();
  	outS = new ObjectOutputStream[16];
    System.out.println("");
    System.out.println("Port: " + port);
    System.out.println("Server Coming up...  Please Wait... ");
    System.out.println("");
		
    try
    {
      ssc = ServerSocketChannel.open();
      ssc.configureBlocking(true);
      InetAddress lh = InetAddress.getLocalHost();
			InetSocketAddress isa = new InetSocketAddress(lh, port);
			ssc.socket().bind(isa);
      while(true)  //Sets up incoming connections
      {
        System.out.println("waiting for a connection on port " + port);
        sc = ssc.accept();    // wait for a connection
        if(sc != null)
        {
        	s = sc.socket();
        	System.out.println("got a connection from " + s.getInetAddress());
        	//BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        	//OutputStream os = s.getOutputStream();    // output to socket goes here
        	//out = new ObjectOutputStream(s.getOutputStream());
        	out = new PrintWriter(s.getOutputStream(), true);
        	System.out.println("Starting whiskeyDem communication with: " + s.getInetAddress() + ":" + port);
        	numComm = numComm + 1;
        	//outS[numComm] = out;
        	pwArray[numComm] = out;
        }
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
