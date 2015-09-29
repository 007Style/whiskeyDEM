
import java.io.*;
import java.net.*;
import java.util.*;
import javax.vecmath.*;
import java.nio.channels.*;

public class nioServer
{
	public static void main(String[] argv)
  {
  	Point3d   m_newPos = new Point3d(0,0,0);
  	Vector3d  m_travel = new Vector3d(0,0,0);
  	double    m_distanceTraveled = 0;
  	boolean   sync = false;
  	int       fps = 0;
  	System.out.println(m_newPos);
  	System.out.println(m_travel);
  	System.out.println(m_distanceTraveled);
  	System.out.println(sync);
  	System.out.println(fps);
  	String me = "/*" + m_newPos + "" + m_travel + m_distanceTraveled + "," + sync + "," + fps + "*/";
  	System.out.println(me);
  	String sInt = "77";
  	int iInt = Integer.parseInt(sInt);
  	System.out.println(iInt + 1);
  	String sDouble ="77.1";
  	double dDouble = Double.parseDouble(sDouble);
  	System.out.println(dDouble+1);
  	String sPoint3d = "(1.1, 2.1, 3.1)";
  	Point3d thePoint3d = new Point3d(1,1,1);	
  	//Point3d pPoint3d = Point3d.parsePoint3d(sPoint3d);
  	//System.out.println(pPoint3d+thePoint3d);
  	
  	try
  	{
  	  int port = 2222;
  	  String input = "Piper is up late tonight...";
  	  BufferedReader inhere = new BufferedReader(new InputStreamReader(System.in));
  	  ServerSocket ss = new ServerSocket(port);
  	  System.out.println("waiting for a connection on port " + port);
  	  Socket s = ss.accept();    // wait for a connection
      System.out.println("got a connection from " + s.getInetAddress());
    	BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
    	OutputStream os = s.getOutputStream();    // output to socket goes here
    	//byte[] outbytes = (input).getBytes();  //This is the main sending loop
    	int intput = 77;
    	//byte[] outbytes = (intput+"").toString().getBytes();
    	//byte [] outbytes = me.getBytes();
    	byte [] outbytes = null;
    	System.out.println("Sending protocol started");
    	for(int i=0; i<=7; i++)
    	{
    		input = "Piper is up late tonight..." + i;
    		outbytes = input.getBytes();
    		System.out.println("Sending string: " + input);
	      os.write(outbytes, 0, outbytes.length);
      	//os.write("\n".getBytes());  
      	os.flush();  
    	}
    	try{String pause = inhere.readLine();} catch(IOException e){System.out.println(e);}
    	for(int i=0; i<=7; i++)
    	{
    		System.out.println("Sending string " + i);
	      os.write(outbytes, 0, outbytes.length);
      	os.write("\n".getBytes());  //Reads from terminal then sends
      	os.flush();  //  through socket
    	}
    	try{String pause = inhere.readLine();} catch(IOException e){System.out.println(e);}
    	for(int i=0; i<=1023; i++)
    	{
    		System.out.println("Sending string " + i);
	      os.write(outbytes, 0, outbytes.length);
      	os.write("\n".getBytes());  //Reads from terminal then sends
      	os.flush();  //  through socket
    	}
    	try{String pause = inhere.readLine();} catch(IOException e){System.out.println(e);}
    }
    catch (java.io.IOException e)
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }	
}