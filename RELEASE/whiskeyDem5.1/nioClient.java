
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;

public class nioClient
{
	private static CharsetEncoder encoder;
  private static CharsetDecoder decoder;
  private static LinkedList syncQ = null;
	public static void main(String[] argv)
  {
  	syncQ = new LinkedList();
  	String host = "localhost";
  	int port = 2222;
  	int status = 0;
  	Charset charset = Charset.forName("ISO-8859-1");
  	decoder = charset.newDecoder();
  	encoder = charset.newEncoder();
  	try
    {
    	System.out.println("Trying to connect to" + host + ":" + port);
      /*s = new Socket(host, port);*/
      InetSocketAddress ad = new InetSocketAddress(host,port);
      SocketChannel sc = SocketChannel.open();
      sc.configureBlocking(false);
      sc.connect(ad);
      sc.finishConnect();
      Socket s = sc.socket();
      OutputStream os = s.getOutputStream();
      BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
      /*in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));*/
      //String cmd;
      ByteBuffer dst = ByteBuffer.allocateDirect(1024);
      if(dst!=null) System.out.println("dst not null");
  		else System.out.println("dst null");
      //while((cmd = is.readLine()) != null)  //Reads from socket and prints
      System.out.println("..ocoooooooooooooooooo..");
      //while(true)
      for(int ii=0; ii<7; ii++)
      {   
      	//System.out.println("....");
      	dst.clear();
      	status = sc.read(dst);
      	//if(status != 0) System.out.println(status);
      	if(status >0)
      	{
      		dst.flip();
      		CharBuffer buf = null;
      		buf = decoder.decode(dst);
      		//syncQ.addLast(buf.toString());
      		
      		//System.out.println(buf.toString());
      		
      		//byte [] ba = dst.array();
      		//String sure = new String(ba,0,status);
      		//System.out.println(sure);
        }
      	//System.out.println(dst.toString());
      	//System.out.println(dst.getChar());
      	//cmd = is.readLine();                                  
        //System.out.println(cmd);
        //try{String pause = inhere.readLine();} catch(IOException e){System.out.println(e);}
      }
    }
    catch (UnknownHostException e){System.err.println("unknown host: " + host);}
    catch (IOException e){System.out.println(e.getMessage());}
    for(int ii=0; ii<=10; ii++)
    {
    	syncQ.addLast(""+ii);
    }
    
    for(int q=0; q<syncQ.size(); q++)
  	{
  		String me = (String)syncQ.get(q);
  		System.out.println("syncQ check m_newPos " + q + " ==== " + me);
  	}
    
    while(syncQ.size() !=0)
    {
    	System.out.println((String)syncQ.removeFirst());
    }
	}
}