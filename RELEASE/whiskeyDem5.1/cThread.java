/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
 /*
 This class listens for incoming audio packets.  When it recieves one it decodes it
 and plays it through the audio system.  It runs until the connection is droped.
 */

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.Vector;
import javax.sound.sampled.*;
import java.util.zip.*;


//This class recieves clips and plays them

public class cThread extends Thread
{
        byte[] audioBytes = null;
        Vector vec = new Vector();
        int waiting = 0;
        int toPlay = 1;
        int playing = 0;
        FormatControls formatControls = new FormatControls();
        Playback playback = new Playback();
        Monitor monitor = new Monitor();
        AudioInputStream audioInputStream;
        ObjectInputStream p;
        BufferedReader br;
        InputStream is;
        
        String fileName = "untitled.wav";
        String errStr;
        File file;
        Socket s = null;
        double duration, seconds;
        final int bufSize = 16384;
                

        public cThread(Socket s)  //Sets up the Object Stream for listening
        {
                try
                {
                        is = s.getInputStream();
                        p = new ObjectInputStream(is);
                        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                }
                catch (java.io.IOException e) 
                {
                        ipKtalk.tf2.setText(e.getMessage());
                        //System.out.println(e.getMessage());
                        //e.printStackTrace();
                }
        }
    

        public void run()
        {
                vec.add(0, "0");
                monitor.start();
                try
                {
                        while(true)  //The main loop, it recieves the audio
                        {  //  decodes it, formats it, and sends it to the Playback class
                                int slength = Integer.parseInt(br.readLine());
                                if(slength == -1)
                                {
                                        ipKtalk.reset();
                                }
                                byte[] UNCaudioBytes = (byte[]) p.readObject();
                                byte[] buffer = null;
                                if(UNCaudioBytes == null)
                                {
                                        ipKtalk.DisconnecT();
                                        break;
                                }
                                if(ipKtalk.CurCon == 0)
                                {
                                        break;
                                }                          
      
      
                                ByteArrayInputStream baisc = new ByteArrayInputStream(UNCaudioBytes);
                                GZIPInputStream gis = new GZIPInputStream(baisc);
                                DataInputStream dis = new DataInputStream(gis);
                                buffer = new byte[ slength ];
                                dis.readFully(buffer);
                                System.out.println("");
                                System.out.println("Recieved Clip: ");
                                System.out.println( "  Compressed size: " + new String(UNCaudioBytes).trim().length() );
                                System.out.println( "  Inflated size: " + new String(buffer).trim().length() );
                                
                                
                                vec.add(buffer);
                                waiting = waiting + 1;
                                
                                System.out.println("");
                                System.out.println("Clips Waiting: " + waiting);
                                System.out.println("");
                        }//It will loop until a disconnect happens
                }
                catch (java.io.IOException e)
                {
                        //ipKtalk.tf2.setText(e.getMessage());
                        //e.printStackTrace();
                        //System.out.println(e.getMessage());
                }
                catch(ClassNotFoundException c)
                {
                        //ipKtalk.tf2.setText(c.getMessage());
                        //System.out.println(c.getMessage());
                        //c.printStackTrace();
                }
        }
        
  
        private void reportStatus(String msg)  //Reports error messages from the Playback class
        {
                if ((errStr = msg) != null) 
                {
                        ipKtalk.tf2.setText(errStr);
                        System.out.println(errStr);
                }
        }
        
  
        /**
        * Controls for the AudioFormat.
        */
        class FormatControls //Returns the audio format 
        {// 8bin, 8khz, mono, sighned pcm
                public FormatControls() {}
                
                public AudioFormat getFormat() 
                {
                        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
                        return new AudioFormat(encoding, 8000, 8, 1, 1, 8000, true);
                }
        
                public void setFormat(AudioFormat format) 
                {
                        AudioFormat.Encoding type = format.getEncoding();
                }
        } // End class FormatControls


        public class Monitor extends Thread  //Monitors the buffer and plays the sounds...
        {
                public void run()
                {
                        while(true)
                        {
                                try  
                                {
                                        java.lang.Thread.currentThread().sleep(200);
                                }
                                catch(java.lang.InterruptedException u) {}
                                if(waiting > 0 && playing == 0 && ipKtalk.downup == 0)
                                {
                                        playing = 1;
                                        audioBytes = (byte[]) vec.remove(toPlay);
                                        waiting = waiting - 1;
                                        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes); 
                                        AudioFormat format = formatControls.getFormat();
                                        int frameSizeInBytes = format.getFrameSize();
                                        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
                                        playback.start();
                                }
                                if(ipKtalk.CurCon == 0)
                                {
                                        break;
                                } 
                        }
                }
        }       
        

        /*
         *Class Playback
         */
        public class Playback implements Runnable  //Plays the audio clip
        {
                SourceDataLine line;
                Thread thread;
        
                public void start() 
                {
                        errStr = null;
                        thread = new Thread(this);
                        thread.setName("Playback");
                        thread.start();
                }
        
                public void stop() 
                {
                        thread = null;
                }
        
                private void shutDown(String message) 
                {
                        if ((errStr = message) != null) 
                        {
                                ipKtalk.tf2.setText(errStr);
                                //System.err.println(errStr);
                        }
                        if (thread != null) 
                        {
                                thread = null;
                        } 
                }

                public void run() 
                {
                // make sure we have something to play
                if (audioInputStream == null) 
                {
                        shutDown("No loaded audio to play back");
                        playing = 0;
                        return;
                }
                // reset to the beginnning of the stream
                try 
                {
                        audioInputStream.reset();
                }
                catch (Exception e) 
                {
                        shutDown("Unable to reset the stream\n" + e);
                        playing = 0;
                        return;
                }
                // get an AudioInputStream of the desired format for playback
                AudioFormat format = formatControls.getFormat();
                AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                if (playbackInputStream == null) 
                {
                        shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
                        playing = 0;
                        return;
                }
                // define the required attributes for our line, 
                // and make sure a compatible line is supported.
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                if (!AudioSystem.isLineSupported(info)) 
                {
                        shutDown("Line matching " + info + " not supported.");
                        playing = 0;
                        return;
                }
                // get and open the source data line for playback.
                try 
                {
                	if(ipKtalk.cMode.getState() == true)
                	{
                        	while(true)
                        	{
	                                if(ipKtalk.lock == 0)
                                	{
	                                        ipKtalk.lock = 1;
                                        	line = (SourceDataLine) AudioSystem.getLine(info);
                                        	line.open(format, bufSize);
                                        	ipKtalk.lock = 0;
                                        	break;
                                	}
                                	else
                                	{
	                                        try  
                                        	{  
	                                                java.lang.Thread.currentThread().sleep(100);
                                        	}
                                        	catch(java.lang.InterruptedException u) {System.out.println(u.getMessage());}
                                	}
                        	}
                        }
                        else
                        {
                        	line = (SourceDataLine) AudioSystem.getLine(info);
                                line.open(format, bufSize);	
                        }
                }
                catch (LineUnavailableException ex) 
                { 
                        shutDown("Could not Play Audio...   Unable to open the line: " + ex);
                        playing = 0;
                        return;
                }
                // play back the captured audio data
                int frameSizeInBytes = format.getFrameSize();
                int bufferLengthInFrames = line.getBufferSize() / 8;
                int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
                byte[] data = new byte[bufferLengthInBytes];
                int numBytesRead = 0;
                // start the source data line
                line.start();
                while (thread != null) 
                {
                        try 
                        {
                                if ((numBytesRead = playbackInputStream.read(data)) == -1) 
                                {
                                        break;
                                }
                                int numBytesRemaining = numBytesRead;
                                while (numBytesRemaining > 0 ) 
                                {
                                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                                }
                        } 
                        catch (Exception e) 
                        {
                                shutDown("Error during playback: " + e);
                                break;
                        }
                }
                // we reached the end of the stream.  let the data play out, then
                // stop and close the line.
                if (thread != null) 
                {
                        line.drain();
                }
                line.stop();
                line.close();
                line = null;
                shutDown(null);
                playing = 0;
        }  // End class Playback
} 
}

//End class cThread



/*
 * @(#)CapturePlayback.java     1.11    99/12/03
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */