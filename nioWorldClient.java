/*
 * nioWorldClient.java
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import javax.vecmath.*;


public class nioWorldClient //extends Thread
{
	private static CharsetEncoder encoder;
  private static CharsetDecoder decoder;
  static int port = 2222;       // default server port
  static String host = "kdog";      // default server host
  static String id = "DualDog";
  static Socket s = null;
  static SocketChannel sc = null;
  static OutputStream os = null;
  static BufferedReader is = null; 
  static ObjectInputStream in = null;
  private ControlJPanel cjp = null;
  private static LinkedList nioSyncQ = null;
  private static LinkedList byteSyncQ = null;
  private flightPacket fPacket = new flightPacket();
  //private flightPacket syncer = new flightPacket();
  private Thread world_thread;
  private static ByteBuffer dst;
  private static String incompleteBuffer = null;

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

    //doDaWork();
  }

  public nioWorldClient(ControlJPanel c)
  {
  	byteSyncQ = new LinkedList();
  	nioSyncQ = new LinkedList();
  	nioSyncQ.addFirst(new flightPacket());
  	port = ProgramConfig.getServerPort();
  	host = ProgramConfig.getServerName();
   	cjp = c;
   	Charset charset = Charset.forName("ISO-8859-1");
  	decoder = charset.newDecoder();
  	encoder = charset.newEncoder();
  	//System.out.println("Client Constructed...");
  	//world_thread = new Thread(this);
  	//doDaWork();
  } 
  
  public nioWorldClient()
  {
  	byteSyncQ = new LinkedList();
  	nioSyncQ = new LinkedList();
  	nioSyncQ.addFirst(new flightPacket());
  	Charset charset = Charset.forName("ISO-8859-1");
  	decoder = charset.newDecoder();
  	encoder = charset.newEncoder();
  	//System.out.println("Client Constructed...");
  	//world_thread = new Thread(this);
  	//doDaWork();
  } 
  
  public flightPacket cRecv()
  {
  	flightPacket f = new flightPacket();
  	//String cmd = null;
    try
    {
      f = (flightPacket)in.readObject();
      //System.out.println(f);
    }
    catch (java.io.IOException e)
    {
      //System.out.println(e.getMessage());
      //e.printStackTrace();
    }
    catch (java.lang.ClassNotFoundException e) {}
    return f;	
  }
  
  public void ClientTo(String input)
  {
    try
    {
      byte[] outbytes = (input).getBytes();  //This is the main sending loop
      os.write(outbytes, 0, outbytes.length);
      os.write("\n".getBytes());  //Reads from terminal then sends
      os.flush();  //  through socket
      if(input.equals("%q"))  //%q exits
      {
        System.out.println("Quiting...");
        System.exit(0);
      }
    }
    catch (java.io.IOException e)
    {
      //System.out.println(e.getMessage());
      //e.printStackTrace();
    } 
  }

  public void doDaWork()  //Sets up socket communication
  {
    try
    {
    	dst = ByteBuffer.allocateDirect(1048576);  // A one MegaByte buffer.  Was 8192 Bytes.
      dst.clear();
    	System.out.println("nioWorldClient Trying to connect to " + host + ":" + port);
      /*s = new Socket(host, port);*/
      InetSocketAddress ad = new InetSocketAddress(host,port);
      sc = SocketChannel.open();
      sc.configureBlocking(false);
      sc.connect(ad);
      sc.finishConnect();
      s = sc.socket();
      OutputStream os = s.getOutputStream();
      BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
      /*in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));*/
      //String cmd;
      cjp.btnCommands.setEnabled(false);
      cjp.btnNetwork.setEnabled(false);
      System.out.println("Connection Made");
    } 
    catch (UnknownHostException e)
    {
      System.err.println("unknown host: " + host);
      cjp.btnCommands.setEnabled(true);
      cjp.btnNetwork.setEnabled(true);
      //System.exit(1);
    }
    catch (IOException e)
    {
      System.out.println(e.getMessage());
      cjp.btnCommands.setEnabled(true);
      cjp.btnNetwork.setEnabled(true);
      //e.printStackTrace();
    }
  }
  
  public static void DisconnecT()  //Breaks the connection and returns the client
  {
    try {s.close();}
    catch (IOException e) {}
  }


  public flightPacket sRecv()  //uses queue to recieve packets.
  {
  	//System.out.println("sRecv");
  	//System.out.println("sRecv nioSyncQ.size() " + nioSyncQ.size());
  	if(nioSyncQ.size()==1)  
  	{
  	  fPacket = (flightPacket)nioSyncQ.getFirst();
  	  if(ProgramConfig.getSyncAfterFrame()<-3)  System.out.println("here nioSyncQ.size()==1  " + nioSyncQ.size());  //DEBUG STATEMENT
  	  //System.out.println("fPacket.fps " + fPacket.fps);
  	  return fPacket;
  	}
  	else if(nioSyncQ.size()==0)
  	{
  		if(ProgramConfig.getSyncAfterFrame()<-3)    System.out.println("here nioSyncQ.size()==0  " + nioSyncQ.size());  //DEBUG STATEMENT
  		//System.out.println("fPacket.fps " + fPacket.fps);
  		return fPacket;
  	}
  	else
  	{
  	  fPacket = (flightPacket)nioSyncQ.removeFirst();
  	  if(ProgramConfig.getSyncAfterFrame()<-3)  System.out.println("here nioSyncQ.size()==1++  " + nioSyncQ.size());  //DEBUG STATEMENT
  	  //System.out.println("fPacket.m_newPos " + fPacket.m_newPos);
  	  return fPacket;
  	}
  }
  
  
  public void syncUp()
  {
  	if(ProgramConfig.getSyncAfterFrame()!=0)
  	{
  		System.out.println(" ");
  		System.out.println("***  3d Sync NOW!  ***");
  		System.out.println("Local  Frame      #: " + MovementModel.getFPS());
  		System.out.println("Server Frame Sync #: " + fPacket.fps);
  	}
  	//flightPacket tmp = (flightPacket)nioSyncQ.getLast();
  	//nioSyncQ.clear();
  	//nioSyncQ.addFirst(tmp);
  	nioSyncQ.clear();
  	//nioSyncQ.addFirst(syncer);	
  	MovementModel.setFPS(0);
  }
  
  public void nioRecv()
  {
  	//System.out.println("nioRecv()");
  	try
  	{
  		int status = 0;
  		int index = 0;
  		int endex = 0;
  		int noend = 0;
  		double p1, p2, p3 = 0;
  		
  		status = sc.read(dst);
  		//System.out.println("status " + status);
  		if(status >0)  //Parse out flightPackets and put in 
    	{
    		if(ProgramConfig.getSyncAfterFrame()<-2)  syncUp();    //nioSyncQ.clear();
    		if(ProgramConfig.getSyncAfterFrame()<-1)  nioSyncQ.clear();
      	dst.flip();
      	CharBuffer buf = decoder.decode(dst);
      	//System.out.println("Recv String: " + buf.toString());
      	String sBuffer = buf.toString();
	      //if(sBuffer.substring(0,2).equals("/*"))
	      while(true)
	      {
	      	index = sBuffer.indexOf("/*", endex);
	      	if(index==-1) 
	      	{
	      		dst.clear();
	      		break;
	      	}
	      	endex = sBuffer.indexOf("*/", index);
	      	if(endex==-1) 
	      	{
	      		dst.clear();
	      		dst.put(sBuffer.substring(index, sBuffer.length()).getBytes());  //put end back in buffer.
	      		break;
	      	}
	      	byteSyncQ.addLast(sBuffer.substring(index + 2, endex));
        }
				while(byteSyncQ.size() != 0)
				{
					flightPacket syncer = new flightPacket();
					index = 0;
					endex = 0;
					String parse = (String)byteSyncQ.removeFirst();
					//System.out.println("parse " + parse);
					endex = parse.indexOf(",", index);
					p1 = Double.parseDouble(parse.substring(index+1, endex));
					index = endex+2;
					endex = parse.indexOf(",", index);
					p2 = Double.parseDouble(parse.substring(index, endex));
					index = endex+2;
					endex = parse.indexOf(")", index);
					p3 = Double.parseDouble(parse.substring(index, endex));
					//System.out.println("p1 p2 p3 " + p1 + " " + p2 + " " + p3);
					syncer.m_newPos = new Point3d(p1,p2,p3);
					index = endex + 2;
					endex = parse.indexOf(",", index);
					p1 = Double.parseDouble(parse.substring(index, endex));
					index = endex+2;
					endex = parse.indexOf(",", index);
					p2 = Double.parseDouble(parse.substring(index, endex));
					index = endex+2;
					endex = parse.indexOf(")", index);
					p3 = Double.parseDouble(parse.substring(index, endex));
					//System.out.println("p1 p2 p3 " + p1 + " " + p2 + " " + p3);
					syncer.m_travel = new Vector3d(p1,p2,p3);
					index = endex + 1;
					endex = parse.indexOf(",", index);
					syncer.m_distanceTraveled = Double.parseDouble(parse.substring(index, endex));
					//System.out.println("syncer.m_distanceTraveled " + parse.substring(index, endex));
					index = endex + 1;
					endex = parse.indexOf(",", index);
					syncer.sync = "true".equals(parse.substring(index, endex));
					//System.out.println("syncer.sync " + parse.substring(index, endex));
					index = endex + 1;
					endex = parse.length();
					syncer.fps = Integer.parseInt(parse.substring(index, endex));
					//System.out.println("syncer.fps " + syncer.fps);
					//System.out.println("syncer.m_newPos " + syncer.m_newPos);
					if(syncer.sync==true)  
    			{
    				//System.out.println("******************************************************************** TRUE");
    				fPacket = syncer;
    				syncUp();
    			}
    			else
    			{
    				nioSyncQ.addLast(syncer);
    				//System.out.println("nioSyncQ check fps upon add " + syncer.fps);
    				if(ProgramConfig.getSyncAfterFrame()<-2)  System.out.println("nioRecv nioSyncQ.size() " + nioSyncQ.size());  //DEBUG STATEMENT
    				//System.out.println("nioSyncQ pre check m_newPos " + " ==== " + syncer.m_newPos);
    			}					
			 	}
    	}
  	}
  	catch (IOException e){System.out.println(e.getMessage());}
  	/*while(nioSyncQ.size() != 0)
  	{
  		flightPacket me = (flightPacket)nioSyncQ.removeLast();
  		System.out.println("nioSyncQ check m_newPos " + nioSyncQ.size() + " ==== " + me.m_newPos);
  	}*/
  }
}

