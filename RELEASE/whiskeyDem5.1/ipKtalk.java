/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This is the bread and butter of ipKtalk.  This file handles the gui and all
it's interactions, sets up the environment, controls the sound recording and
finally sends the audio packet.  This is the executable if you will.  This 
class will initiate conversation to a client that is in <Server> mode.  To 
be in <Server> mode a client instantiates the 'sThread' class.  When a 
connection is made, 'sThread' is deallocated.  Also, when a connection is 
made, 'cThread' is instantiated and started.  'cThread' listenes for incoming
audio packets; when it recieves one it decodes it and plays it after storing it
in a small latency buffer.  ipK versions latter than 2.4 support gzip compression
in the transmission of sound bytes.  Note, any code that is commented out is for
debugging purposes only. ipKangaroo is not responsible for any damage this 
program inflicts on the users machine.
*/

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.util.zip.*;




public class ipKtalk extends Frame implements ActionListener
{       
        //The following are variables used in this class and others
        static int lock = 0;
        static int downup = 0;
        static int mode = 0;
        String fileName = "untitled.wav";
        String errStr;
        File file;
        Image image;
        double duration, seconds;
        final int bufSize = 16384;
        
        GridBagConstraints gbc; 
        byte[] audioBytes = null;
        FormatControls formatControls;
        Capture capture;
        AudioInputStream audioInputStream;
        
        //The static variables are used inside as well as outside this 
        //  class by the supporting classes 'sThread' and 'cThread'
        static Checkbox check2;
        static String host = "localhost";
        static int port = 69;
        static int server;
        static int CurCon = 0;
        static int fresh = 0;
        static TextField tf1, tf2;
        static Button Start;
        static Button Disconnect;
        static Button Connect;
        static CheckboxMenuItem cMode;
        static int bannerOnOff = 0;
        static PicturePerfect pp;
        
        static ObjectOutputStream o;  //We send a byte array object
        static Socket s;                                                                
        static ServerSocket ss;
        static OutputStream os;
        
        static sThread b;
        static cThread c;
        
        static Process chat;
        

        private void reportStatus(String msg)   //This prints any sound related error messages
        {                                       //  to the standard error
                if ((errStr = msg) != null) 
                {
                        System.out.println(errStr);
                }
        }

    
        public void New_Action(ActionEvent event)  //Brings up the about window
        {
                //Put something here...  About dialog menu.
        }


        public void Help_Action(ActionEvent event)  //Brings up the help window 
        {       
                help haha = new help();
        }


        public void Quit_Action(ActionEvent event)  //Disconnects from current connection
        {                                           //  then exits the program
                DisconnecT();    
                this.dispose();
                System.exit(0);
        }
        
        public void LipKchat_Action(ActionEvent event)  //Executes ipKchat
        {
                try 
                {
                        chat = Runtime.getRuntime().exec("ipKchat.bat");
                } 
                catch (Exception e) {}  
        }


        public void Banner_Action(ActionEvent event)  //Turns on/off the ipK main banner
        {
                if(bannerOnOff == 1)
                {
                        remove(pp);     
                        pack();
                        show ();
                        bannerOnOff = 0;        
                }
                else
                {
                        add(pp, gbc);
                        pack();
                        show ();
                        bannerOnOff = 1;        
                }
        }
        
        
        public static void reset()
        {
                tf2.setText("RESETING LINE, please wait...");
                if(check2.getState() == false)
                {
                        try
                        {
                                java.lang.Thread.currentThread().sleep(8000);
                        }
                        catch(java.lang.InterruptedException u) {}      
                }
                try
                {
                        if(check2.getState() == true)
                        {
                                if(cMode.getState() == true)
                                {
                                        Runtime.getRuntime().exec("java ipKtalk -m -s -p " + port);
                                }
                                else
                                {
                                        Runtime.getRuntime().exec("java ipKtalk -s -p " + port);
                                }
                        }
                        else
                        {
                                if(cMode.getState() == true)
                                {
                                        host = tf1.getText();
                                        Runtime.getRuntime().exec("java ipKtalk -m -c " + host + " -p " + port);
                                }
                                else
                                {
                                        host = tf1.getText();
                                        Runtime.getRuntime().exec("java ipKtalk -c " + host + " -p " + port);
                                }
                        }       
                }
                catch (Exception e) {}  
                DisconnecT();    
                System.exit(0); 
        }
        
        
        public void Reset_Action(ActionEvent event)  //Reset the connection, Line and Program
        {       
                tf2.setText("RESETING LINE, please wait...");
                PrintWriter outend = null;
                try
                {
                        outend = new PrintWriter(s.getOutputStream(), true);
                }
                catch(java.io.IOException e) {}
                outend.println("-1");
                outend.flush();
                if(check2.getState() == false)
                {
                        try
                        {
                                java.lang.Thread.currentThread().sleep(8000);
                        }
                        catch(java.lang.InterruptedException u) {}      
                }       
                try
                {
                        if(check2.getState() == true)
                        {
                                if(cMode.getState() == true)
                                {
                                        Runtime.getRuntime().exec("java ipKtalk -m -s -p " + port);
                                }
                                else
                                {
                                        Runtime.getRuntime().exec("java ipKtalk -s -p " + port);
                                }
                        }
                        else
                        {
                                if(cMode.getState() == true)
                                {
                                        host = tf1.getText();
                                        Runtime.getRuntime().exec("java ipKtalk -m -c " + host + " -p " + port);
                                }
                                else
                                {
                                        host = tf1.getText();
                                        Runtime.getRuntime().exec("java ipKtalk -c " + host + " -p " + port);
                                }
                        }       
                }
                catch (Exception e) {}  
                DisconnecT();  
                tf2.setText("RESETING LINE, please wait...");  
                this.dispose();
                System.exit(0); 
        }
        
        
        public void Personal_Reset_Action(ActionEvent event)  //Resets line when not connected
        {
                tf2.setText("RESETING LINE, please wait...");
                try
                {
                        if(check2.getState() == true)
                        {
                                if(cMode.getState() == true)
                                {
                                        Runtime.getRuntime().exec("java ipKtalk -m -f -p " + port);
                                }
                                else
                                {
                                        Runtime.getRuntime().exec("java ipKtalk -f -p " + port);
                                }
                        }
                        else
                        {
                                if(cMode.getState() == true)
                                {
                                        host = tf1.getText();
                                        Runtime.getRuntime().exec("java ipKtalk -m -t " + host + " -p " + port);
                                }
                                else
                                {
                                        host = tf1.getText();
                                        Runtime.getRuntime().exec("java ipKtalk -t " + host + " -p " + port);
                                }
                        }       
                }
                catch (Exception e) {}     
                this.dispose();
                System.exit(0); 
        }
        

        public void actionPerformed(ActionEvent event)  //Gives the 'Talk' button it's functionality
        {
                if(event.getActionCommand() == " -         - ---- < < < <  < TALK >  > > > > ---- -         - ")
                {
                        if(downup == 0)  //One click to start recording
                        {
                                startMic();
                        }
                        else  //Another click to stop recording, encode, packetize, and send
                        {
                                stopMic();                      
                        }
                }
                if(event.getActionCommand() == "Connect")  //'Connect' button connects 
                {
                        ConnecT();
                }
                if(event.getActionCommand() == "Disconnect")  //'Disconnect' button drops conneciton
                {
                        DisconnecT();
                }
        }
      
      
        public void startMic()  //Sets status bar and starts mic recording
        {
                tf2.setText("Recording Clip...");
                file = null;
                capture.start();
                downup = 1;
                try  //Minimum of one second recording is required
                {
                        java.lang.Thread.currentThread().sleep(1000);
                }
                catch(java.lang.InterruptedException u) {}
        }
    
    
        public void stopMic()  //Sets status bar, stops mic and sends packet
        {
                downup = 0;     
                capture.stop();
                try  //This sleep is here to allow the recording thread to finish up
                {  //  it writes a byte array that will latter be sent
                        java.lang.Thread.currentThread().sleep(500);
                }
                catch(java.lang.InterruptedException u) {System.out.println(u.getMessage());}
                tf2.setText("Sending Clip...");  //Again, sets status bar
                        /*
                        if(audioBytes == null)
                        {
                                System.out.println("null byter...");
                        }
                        else
                        {
                                System.out.println("good to go...");
                        }
                        */
                try
                {
                        Start.setEnabled(false);  //Set buttons enabled/disabled
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        int l = audioBytes.length;
                        out.println(l);
                                                  
                                                
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        GZIPOutputStream gos = new GZIPOutputStream(baos, 9);
                        gos.write(audioBytes, 0, audioBytes.length );
                        gos.finish();
                        gos.close();
                        byte[] CaudioBytes = baos.toByteArray();
                        System.out.println("");
                        System.out.println("Sent Clip: ");                  
                        System.out.println( "  Inflated size: " + new String(audioBytes).trim().length() );
                        System.out.println( "  Compressed size: " + new String(CaudioBytes).trim().length() );
                
                
                        o.writeObject(CaudioBytes);  //Sends the byte array to the other user
                        o.flush();
                        Start.setEnabled(true);
                        //audioBytes = null;
                }
                catch (java.io.IOException e) 
                {
                        tf2.setText(e.getMessage());     
                        //System.out.println(e.getMessage());
                        //e.printStackTrace();
                }
                /*
                try
                {  //Grey out the 'Talk' button for the duration of the sound clip
                        java.lang.Thread.currentThread().sleep(((int)duration * 1000));  
                }  //  pervents the user from getting over zealous and floding the program with data
                catch(java.lang.InterruptedException u) {}
                Start.setEnabled(true);
                */
                tf2.setText("Status: Ready...");
        }
        
                
        public static void ConnecT()  //Sets up the sockets for connection
        {
                if(CurCon == 0)
                {
                fresh = 1;
                if(check2.getState() == false)
                {  //Client connection
                        server = 0;     
                        try
                        {  //Trys to connect and if it succeeds starts 'cThread' to listen
                                tf2.setText("Connecting w/IP...");
                                host = tf1.getText();
                                s = new Socket(host, port);
                                os = s.getOutputStream();
                                o = new ObjectOutputStream(os);
                                tf2.setText("Connection Accepted...");
                                CurCon = 1;
                                c = new cThread(s);
                                c.start(); 
                                Connect.setEnabled(false);
                                try
                                {
                                        java.lang.Thread.currentThread().sleep(4000);
                                }
                                catch(java.lang.InterruptedException u) {}
                                Start.setEnabled(true);
                                Disconnect.setEnabled(true);
                                check2.setEnabled(false);
                                tf1.setEnabled(false);
                                tf2.setText("Status: Ready...");
                                fresh = 1;
                        }
                        catch (UnknownHostException e)
                        {
                                tf2.setText("unknown host: " + host);
                                //System.err.println("unknown host: " + host);
                        }
                        catch (IOException e)
                        {
                                tf2.setText(e.getMessage());
                                //System.out.println(e.getMessage());
                                //e.printStackTrace();
                        }       
                }
                
                else
                {  //Server  Connection 
                        server = 1;
                        try
                        {  //Start 'sThread' to listen for incoming connections then starts 'cThread' 
                                ss = new ServerSocket(port);
                                b = new sThread();
                                b.start();
                                tf2.setText("Waiting...");
                                Connect.setEnabled(false);
                                Disconnect.setEnabled(true);
                                check2.setEnabled(false);
                                tf1.setEnabled(false);
                        }
                        catch (java.io.IOException e)
                        {
                                tf2.setText(e.getMessage());
                                //System.out.println(e.getMessage());
                                //e.printStackTrace();
                        }
                }
                }
        }
    
    
        public static void DisconnecT()  //Breaks the connection and returns the client
        {  //  to it's start up state
                tf2.setText("Disconnecting...");
                if(CurCon == 1)
                {
                        tf2.setText("Disconnecting...");
                        try
                        {       
                                PrintWriter outend = new PrintWriter(s.getOutputStream(), true);
                                outend.println("0");
                                outend.flush();
                                try
                                {
                                        java.lang.Thread.currentThread().sleep(1000);
                                }
                                catch(java.lang.InterruptedException u) {}
                                Byte[] end = null;
                                o.writeObject(end);
                                o.flush();
                        }
                        catch (java.io.IOException e) 
                        {
                                tf2.setText(e.getMessage());
                                //System.out.println(e);
                        }
                        try
                        {
                                
                                if(server == 0)
                                {
                                        s.close();  //If client, closes the socket
                                }
                                if(server == 1)
                                {
                                        ss.close();  //If server, closes the socket 
                                        s.close();   //  and the server socket
                                }       
                                tf2.setText("Status: Ready...");
                                Start.setEnabled(false);
                                Connect.setEnabled(true);
                                Disconnect.setEnabled(false);
                                check2.setEnabled(true);
                                tf1.setEnabled(true);
                        }
                        catch (IOException e)
                        {
                                tf2.setText(e.getMessage());
                                //System.out.println(e.getMessage());
                                //e.printStackTrace();
                        }
                }
                else
                {
                        if(fresh != 0 && server == 1)
                        {
                                try
                                {
                                        ss.close();
                                }
                                catch(IOException e){}
                        }
                        //tf2.setText("Status: Ready...");
                        Start.setEnabled(false);
                        Connect.setEnabled(true);
                        Disconnect.setEnabled(false);
                        check2.setEnabled(true);
                        tf1.setEnabled(true);
                }
                CurCon = 0;
                tf2.setText("Status: Ready...");
        }
          

        public ipKtalk()  //Sets up the gui and draws it to the screen
        {
                formatControls = new FormatControls();
                capture = new Capture();
                
                Font    font = new Font ("Times", Font.BOLD, 14);
                setFont (font); 
                Color   numberColor = new Color (46, 124, 191);
            
                setTitle ("ipKtalk");
            
                Image icon = getToolkit().getImage("talk.gif");
                this.setIconImage(icon);
                
                image = getToolkit().getImage("ipk.jpg");
                setLayout(new GridBagLayout());
                gbc = new GridBagConstraints();

                Panel panelbut = new Panel();
                Panel paneltxtfld = new Panel();

                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
            
                add(panelbut, gbc);
                add(paneltxtfld, gbc);
                
                gbc.gridwidth = 1;
      
                Label l1 = new Label("IP Address:", Label.RIGHT);
                paneltxtfld.add(l1);

                tf1 = new TextField(23);
                paneltxtfld.add(tf1, gbc);

                Label l2 = new Label(" ", Label.RIGHT);
                paneltxtfld.add(l2);
     
                check2 = new Checkbox("< Server >");
                paneltxtfld.add(check2, gbc);
    
                Label l3 = new Label(" ", Label.RIGHT);
                paneltxtfld.add(l3);

                Connect = new Button ("Connect");
                paneltxtfld.add(Connect);
                Connect.addActionListener(this);

                Disconnect = new Button ("Disconnect");
                paneltxtfld.add(Disconnect);
                Disconnect.addActionListener(this);
                Disconnect.setEnabled(false);

                pp = new PicturePerfect();

                Start = new Button(" -         - ---- < < < <  < TALK >  > > > > ---- -         - ");
                panelbut.add(Start);
                Start.setBackground(numberColor);
                Start.addActionListener(this);
                Start.setEnabled(false);

                tf2 = new TextField(34);
                panelbut.add(tf2,gbc);
                tf2.setEditable(false);
                tf2.setText("Status: Ready...");
                tf1.setText("localhost");

                MenuBar menuBar = new MenuBar();
                Menu fileMenu = new Menu(" File ");
                Menu options = new Menu(" Options ");
                Menu ipk = new Menu(" ipK ");
                cMode = new CheckboxMenuItem("Compatibility Mode");
                options.add(new MenuItem("Banner on/off"));
                options.add(cMode);
                options.add("-");
                options.add(new MenuItem("RESET LINE"));
                //fileMenu.add(new MenuItem("About"));
                fileMenu.add(new MenuItem("Help"));
                fileMenu.add(new MenuItem("-"));
                fileMenu.add(new MenuItem("Exit"));
                ipk.add(new MenuItem("-"));
                ipk.add(new MenuItem("Launch ipKchat"));
                ipk.add(new MenuItem("-"));
                menuBar.add (fileMenu);
                menuBar.add(options);
                menuBar.add(ipk);
                setMenuBar (menuBar);

                WindowListener1 lWindow = new WindowListener1();
                addWindowListener(lWindow);

                ActionListener1 lAction = new ActionListener1();
                fileMenu.addActionListener(lAction);
                options.addActionListener(lAction);
                ipk.addActionListener(lAction);
                
                cMode.setState(false);
               
                this.setLocation(100,100);
                setBackground(Color.white);
                pack();
                setResizable(false);
                show ();   
                add(pp, gbc);
                remove(pp);
                pack();
                show ();
        }


        public static void main (String argv[])  //Parses command line parameters
        {  //  and sets up the gui and the program to run
                try  //Minimum of one second recording is required
                {
                        java.lang.Thread.currentThread().sleep(1000);
                }
                catch(java.lang.InterruptedException u) {}
                ipKtalk M = new ipKtalk();
                try
                {
                        Arguments arguments = new Arguments(argv);  //Start parsing commmand line
                        if (arguments.isSpecified("m"))
                        {
                                cMode.setState(true);   
                        }
                        if (arguments.isSpecified("c"))
                        {
                                host = arguments.getModified("c");
                                tf1.setText(host);
                                ipKtalk.ConnecT();
                        }
                        if (arguments.isSpecified("s"))
                        {       
                                check2.setState(true);
                                ipKtalk.ConnecT();
                        }
                        if (arguments.isSpecified("p"))
                        {
                                port = arguments.getModifiedInt("p");
                        }  
                        if (arguments.isSpecified("t"))
                        {
                                host = arguments.getModified("t");
                                tf1.setText(host);
                        }
                        if (arguments.isSpecified("f"))
                        {       
                                check2.setState(true);
                        }//End parse
                }
                catch (NumberFormatException e)
                {
                System.err.println("bad number format");
                System.exit(1);
                }
        }
  
  
        class ActionListener1 implements ActionListener  //Listens for any menu item events
        {
                public void actionPerformed(ActionEvent event)
                {
                        String str = event.getActionCommand();
                        if (str.equals("About"))
                        {
                                New_Action(event);
                        }
                        else if (str.equals("Help"))
                        {
                                Help_Action(event);
                        }
                        else if (str.equals("Exit"))
                        {
                                Quit_Action(event);
                        }
                        else if (str.equals("Launch ipKchat"))
                        {
                                LipKchat_Action(event);
                        }
                        else if (str.equals("Banner on/off"))
                        {
                                Banner_Action(event);
                        }
                        else if (str.equals("RESET LINE"))
                        {
                                if(CurCon == 1)
                                {
                                        Reset_Action(event);
                                }
                                else
                                {
                                        Personal_Reset_Action(event);
                                }
                        }
                }       
        }


        class WindowListener1 extends WindowAdapter  //Listens to the window
        {  //  if it's closed or minimized or moved or etc
                public void windowClosing(WindowEvent event)
                {
                        DisconnecT();                   
                        Window win = event.getWindow();
                        win.setVisible(false);
                        win.dispose();
                        System.exit(0);
                }
        }


        class PicturePerfect extends Component  //Draws the banner to the screen
        {
                public void paint(Graphics g)
                {
                        g = getGraphics();
                        g.drawImage(image ,0 ,0 , this);
                        Dimension d = getSize();
                        g.drawRect(0 ,0 ,d.width-1, d.height-1);
                }
                public Dimension getPreferredSize()
                {
                        return new Dimension(640, 240);
                }
        }
  
  
        /**
        * Controls for the AudioFormat.
        */
        class FormatControls  //This returns the format of the audio
        {  //8 bit, 8khz, mono, sighned pcm
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


        /*
        * Reads data from the input channel and writes to the output stream
        */
        class Capture implements Runnable  //Captures sound from the microphone
        {
                TargetDataLine line;
                Thread thread;
                
                public void start() 
                {
                        errStr = null;
                        thread = new Thread(this);
                        thread.setName("Capture");
                        thread.start();
                }

                public void stop() 
                {
                        thread = null;
                }
        
                private void shutDown(String message) 
                {
                        if ((errStr = message) != null && thread != null) 
                        {
                                thread = null;
                                tf2.setText(errStr);
                                //System.err.println(errStr);
                        }
                }

                public void run() 
                {
                                //System.out.println("1...");
                        duration = 0;
                        audioInputStream = null;
            
                        // define the required attributes for our line, 
                        // and make sure a compatible line is supported.

                        AudioFormat format = formatControls.getFormat();
                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                                //System.out.println("2...");
                        if (!AudioSystem.isLineSupported(info)) 
                        {
                                shutDown("Line matching " + info + " not supported.");
                                return;
                        }
                                //System.out.println("3...");
                        // get and open the target data line for capture.
                        
                        try 
                        {	
                        	if(cMode.getState() == true)
                        	{
                                	while(true)
                                	{
	                                        if(lock == 0)
                                        	{
	                                                lock = 1;
                                                        	//System.out.println("3.25");
                                                	line = (TargetDataLine) AudioSystem.getLine(info);
	                                                        //System.out.println("3.5");
                                                	line.open(format, line.getBufferSize());
	                                                        //System.out.println("3.75");
                                                	lock = 0; 
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
                        			//System.out.println("3.25");
                                        line = (TargetDataLine) AudioSystem.getLine(info);
	                                	//System.out.println("3.5");
                                        line.open(format, line.getBufferSize());
	                                        //System.out.println("3.75");	
	                        }
                        
                        } 
                        catch (LineUnavailableException ex) 
                        { 
                                //System.out.println(ex.getMessage());
                                shutDown("Could not Record Audio...   Unable to open the line: " + ex);
                                if(downup == 1)
                                {
                                        stopMic();      
                                }
                                return;
                        } 
                        catch (SecurityException ex) 
                        { 
                                //System.out.println(ex.getMessage());
                                shutDown(ex.toString());
                                return;
                        }
                        catch (Exception ex) 
                        { 
                                //System.out.println(ex.getMessage());
                                shutDown(ex.toString());
                                return;
                        }
                                //System.out.println("4...");
                        // play back the captured audio data
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        int frameSizeInBytes = format.getFrameSize();
                        int bufferLengthInFrames = line.getBufferSize() / 8;
                        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
                        byte[] data = new byte[bufferLengthInBytes];
                        int numBytesRead = 0;
                                //System.out.println("5...");
                        line.start();
                                //System.out.println("6...");
                        while (thread != null) 
                        {
                                if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) 
                                {
                                        break;
                                }
                                out.write(data, 0, numBytesRead);
                        }
                                //System.out.println("7...");
                        // we reached the end of the stream.  stop and close the line.
                        line.stop();
                                //System.out.println("7.33");  //waits
                        if(cMode.getState() == true)  //This is for compatibility mode
                        {  //  don't ask why it works on some machines, we don't know
                                line.close();
                        }
                        //System.out.println("7.66");  //waits
                        line = null;
                        //System.out.println("8...");
                        // stop and close the output stream
                        try 
                        {
                                out.flush();
                                out.close();
                        }
                        catch (IOException ex) 
                        {
                                tf2.setText(ex.getMessage());
                                //System.out.println(ex.getMessage());
                                //ex.printStackTrace();
                        }
                                //System.out.println("9...");
                        // load bytes into the audio input stream for transmission
                        audioBytes = out.toByteArray();
                                /*
                                if(audioBytes == null)
                                {
                                        System.out.println("  Capture: null byter..."); 
                                }
                                else
                                {
                                        System.out.println("  Capture:  good to go...");
                                }
                                */      
                        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
                        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
                        long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
                        duration = milliseconds / 1000.0;
                        try
                        {
                                audioInputStream.reset();
                        }
                        catch (Exception ex) 
                        { 
                                tf2.setText(ex.getMessage());
                                //System.out.println(ex.getMessage());
                                //ex.printStackTrace(); 
                                return;
                        }
                                //System.out.println("10...");
                }
        } // End class Capture
}

//End ipKtalk.java


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