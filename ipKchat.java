/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This class is the executable for ipKchat GUI version.  It sets up
the GUI and procedes to write to the socket when needed.  Class
'ipKFrom' is a thread that listenes on the socket and displays 
any message in the main dialogue box.  Notice also that this class
is inline.  There is no need for this, this is a small program with
a small computational load.  The authors were a bit lazy and still 
thinking in the 'fast' mindset from the 'Server' development.  
Adding Functions in the program wouldn't hurt it a bit.  It would 
make it slightly smaller and easier to read.
*/

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.*;
import java.util.*;




public class ipKchat extends Frame implements ActionListener, KeyListener
{
        
    static Socket s;
    static int port = 2222;       // default server port
    //static String host = "localhost";      // default server host
    static String host = "ipkangaroo.no-ip.com";      // default server host
    static String id = "";
    static TextField  tf1, tf2, tf3, tf4, tf5;
    static TextArea ta1;
    static OutputStream os;
    static BufferedReader is;
    static byte[] outbytes;
    static Button star, Start, Disconnect, Connect, getQ;
    static String disp = "";
    static int CurCon = 0;
    static int connectNow = 0;
    static int serverr = 0;
    static CheckboxMenuItem cboxM;
    static MenuItem cboxQ;
    static sKeepAlive keepS;
    static Panel panelbot;
    static GridBagConstraints gbc;
    static int qON = 0;
    static String quoteString = null;
    
    static Process talk;
    
    Process ServeiN;
    

    public void QN_Action(ActionEvent event)  //Activates 'Quote Now!' service
    {
        if(qON == 0 && serverr == 0)
        {
                add(panelbot, gbc);
                pack();
                show ();
                qON = 1;
                ta1.append("\n");
                ta1.append("Quote Now!    Made possible by: Money.com\n");
                ta1.append("\n");
        }
        else
        {
                if(qON == 1)
                {
                        remove(panelbot);
                        pack();
                        show ();
                        qON = 0;
                        ta1.append("\n");
                        ta1.append("Thank you for using    Quote Now!\n");
                        ta1.append("\n");
                }
        }
    }

    public void New_Action(ActionEvent event)  //Displays 'About' dialogue
    {
          //put something here...  About dialog menu.
    }

    public void Help_Action(ActionEvent event)  //Displays 'Help dialogue
    {
           helpC hehe = new helpC();
    }

    public void Quit_Action(ActionEvent event)  //Quits client
    {
                if(serverr == 1)  //If in server mode it stops
                {
                        ServeiN.destroy();
                }
                if(CurCon == 1)  //Otherwise kill connection and exit
                {
                try
                {
                disp = "%q\n";
                ta1.append("(" + id + ":) " + disp + "\n");
                outbytes = (disp + "\n").getBytes();  
                os.write(outbytes);
                os.flush();
                }
                catch(IOException e)
                {
                        ta1.append(e.getMessage() + "\n");
                        try
                        {
                        s.close();
                        ta1.append("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException r)
                        {
                                ta1.setText((r + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                }
                }
      this.dispose();
      System.exit(0);
    }
    
    public void Start_SAction(ActionEvent event)  //Starts server from GUI
    {
        if(qON == 1)
        {
                remove(panelbot);
                pack();
                show ();
                qON = 0;
                ta1.append("\n");
                ta1.append("Thank you for using    Quote Now!\n");
                ta1.append("\n");               
        }
        if(CurCon == 0)
        {
          ta1.append("The Server is Starting...\n");
          Start.setEnabled(false);
          star.setEnabled(false);
          Disconnect.setEnabled(false);
          Connect.setEnabled(false);
          tf1.setEditable(false);
          tf3.setEditable(false);
          tf4.setEditable(false);
          tf4.setText("                        <<<  ***  ipK Server Mode  ***  >>>  ");
          try 
          {
            ServeiN = Runtime.getRuntime().exec("java Server");
            serverr = 1;
            keepS = new sKeepAlive(ServeiN);  //Prints Server's log to the main dialogue box
            keepS.start();
          } 
          catch (Exception e) {ta1.append(e.getMessage() + "\n");}
        }
        else
        {
          ta1.append("Disconnect first to start Server...\n");
        }
    }
    
    public void LipKchat_Action(ActionEvent event)  //Starts another instance of ipKchat
    {
      try 
      {
        Runtime.getRuntime().exec("java ipKchat");
      } 
      catch (Exception e) {ta1.append(e.getMessage() + "\n");}  
    }
    
    public void Stop_SAction(ActionEvent event)  //Stops the server and returns 
    {  //  the GUI to it's start up state
        if(serverr == 1)
        {
          serverr = 0;
          Start.setEnabled(false);
          star.setEnabled(false);
          Disconnect.setEnabled(false);
          Connect.setEnabled(true);
          tf1.setEditable(true);
          tf3.setEditable(true);
          tf4.setEditable(true);
          tf4.setText("");
          ServeiN.destroy();
          ta1.append("The Server has stoped...\n");
        }
        else
        {
          ta1.append("You must start the Server before you can stop it...\n");
        }
    }
    
    
    public void LipKtalk_Action(ActionEvent event)  //Executes ipKchat
    {
      try 
      {
        talk = Runtime.getRuntime().exec("ipKtalk.bat");
      } 
      catch (Exception e) {}  
    }
    
    
    public void keyReleased(KeyEvent key) 
    { 
        //System.out.println("Key Released: " + 
        //e.getKeyText(e.getKeyCode())); 
    } 
    public void keyPressed(KeyEvent key) 
    { 
        //System.out.println("Key Pressed: " 
        //+ e.getKeyText(e.getKeyCode())); 
    } 
    public void keyTyped(KeyEvent key)  //If enter is pressed it send the text in the text field
    { 
        if('\n' == key.getKeyChar())
        {
                if(CurCon == 1)
                {
                try
                {
                disp = tf4.getText();
                if(disp.equals("%q"))
                {
                        try
                        {
                                disp = "%q\n";
                                tf4.setText("");
                                ta1.append("(" + id + ":) " + disp + "\n");
                                outbytes = (disp + "\n").getBytes();  
                                os.write(outbytes);
                                os.flush();
                                s.close();
                                ta1.setText("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException e)
                        {
                                ta1.setText((e + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                        CurCon = 0;
                }
                else
                {       
                        outbytes = (disp + "\n").getBytes();  
                        os.write(outbytes);
                        os.flush();
                        tf4.setText("");
                }
                }
                catch (IOException e)
                {
                        ta1.append(e.getMessage() + "\n");
                        try
                        {
                        s.close();
                        ta1.append("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException r)
                        {
                                ta1.setText((r + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                }                       
                }
        } 
    } 

     

    class ActionListener1 implements ActionListener  //Listenes for menu item actions
    {
            public void actionPerformed(ActionEvent event)
            {
              String str = event.getActionCommand();
              if (str.equals("About"))
                      New_Action(event);
              else if (str.equals("Help"))
                      Help_Action(event);
              else if (str.equals("Exit"))
                      Quit_Action(event);
              else if (str.equals("Start Server"))
                      Start_SAction(event);
              else if (str.equals("Stop Server"))
                      Stop_SAction(event);   
              else if (str.equals("Launch ipKtalk"))
                      LipKtalk_Action(event);
              else if (str.equals("ipKchat"))
                      LipKchat_Action(event);
              else if (str.equals("Quote Now!"))
                      QN_Action(event);        
            }
    }



    public ipKchat()  //Sets up the GUI
    {
        Image icon = getToolkit().getImage("chat.gif");
        this.setIconImage(icon);
        
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        Panel panelbut = new Panel();
        Panel paneltxtfld = new Panel();
        Panel panelmid = new Panel();
        Panel paneltext = new Panel();
        panelbot = new Panel();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
           
        add(paneltxtfld, gbc);
        add(panelmid, gbc);
        add(panelbut, gbc);
        add(paneltext, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        
        Label l1 = new Label("IP Address:", Label.RIGHT);
        paneltxtfld.add(l1);
        tf1 = new TextField(20);
        paneltxtfld.add(tf1, gbc);
        Label l2 = new Label("", Label.RIGHT);
        paneltxtfld.add(l2);
        Label l3 = new Label("", Label.RIGHT);
        paneltxtfld.add(l3);
        Connect = new Button ("Connect");
        paneltxtfld.add(Connect);
        Connect.addActionListener(this);
        Disconnect = new Button ("Disconnect");
        paneltxtfld.add(Disconnect);
        Disconnect.addActionListener(this);
        Disconnect.setEnabled(false);

        Label l4 = new Label("My IP: ", Label.RIGHT);
        panelmid.add(l4);
        tf2 = new TextField(16);
        panelmid.add(tf2, gbc);
        tf2.setEditable(false);
        star = new Button("*");
        star.setEnabled(false);
        panelmid.add(star);
        Label l5 = new Label("", Label.RIGHT);
        panelmid.add(l5);
        Label l6 = new Label("My Name:", Label.RIGHT);
        panelmid.add(l6);
        tf3 = new TextField(16);
        panelmid.add(tf3, gbc);

        tf4 = new TextField(52);
        panelbut.add(tf4, gbc);
        Start = new Button("Send");
        Font    font = new Font ("Times", Font.BOLD, 14);
        Color   numberColor = new Color (46, 124, 191);
        setFont (font);
        panelbut.add(Start);
        Start.setEnabled(false);
        Start.setBackground(numberColor);
        star.setBackground(numberColor);
        Start.addActionListener(this);
        star.addActionListener(this);

            
        ta1 = new TextArea("Welcome to ipKchat!\nBrought to you by, ipKangaroo...\n\n", 20, 60,1);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        paneltext.add(ta1, gbc);
        ta1.setEditable(false);

        Label l9 = new Label("Ticker:", Label.RIGHT);
        panelbot.add(l9);
        tf5 = new TextField(40);
        panelbot.add(tf5, gbc);
        getQ = new Button ("Get Quote");
        panelbot.add(getQ);
        getQ.addActionListener(this);

        setTitle ("ipKchat");
        setBackground(Color.white);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu(" File ");
        Menu server = new Menu(" Server ");
        Menu options = new Menu(" Options ");
        Menu ipK = new Menu(" ipK ");
        ipK.add(new MenuItem("-"));
        ipK.add(new MenuItem("Launch ipKtalk"));
        ipK.add(new MenuItem("-"));
        cboxM = new CheckboxMenuItem("Audio Alert");
        options.add(cboxM);
        cboxQ = new MenuItem("Quote Now!");
        //options.add(cboxQ);
        server.add(new MenuItem("Start Server"));
        server.add(new MenuItem("Stop Server"));
        server.add(new MenuItem("-"));
        server.add(new MenuItem("ipKchat"));
        //fileMenu.add(new MenuItem("About"));
        fileMenu.add(new MenuItem("Help"));
        fileMenu.add(new MenuItem("-"));
        fileMenu.add(new MenuItem("Exit"));
        menuBar.add(fileMenu);
        menuBar.add(options);
        menuBar.add(server);
        menuBar.add(ipK);
        setMenuBar (menuBar);

        WindowListener1 lWindow = new WindowListener1();
        addWindowListener(lWindow);
        ActionListener1 lAction = new ActionListener1();
        fileMenu.addActionListener(lAction);
        options.addActionListener(lAction);
        server.addActionListener(lAction);
        ipK.addActionListener(lAction);
        
        tf4.addKeyListener(this);
        
        cboxM.setState(false);
        
        this.setLocation(160,210);
        setResizable(false);
        pack();
        show ();
        pack();
        show ();
    }


    public void actionPerformed(ActionEvent event)  //Listenes for button events
    {
        
        if(event.getActionCommand() == "*")  //Sends the ip address of the current client
        {
                try
                {
                disp = tf2.getText();
                outbytes = (disp + "\n").getBytes();  
                os.write(outbytes);
                os.flush();
                }
                catch(IOException e)
                {
                        ta1.append(e.getMessage() + "\n");
                        try
                        {
                        s.close();
                        ta1.append("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException r)
                        {
                                ta1.setText((r + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                }
        }

        if(event.getActionCommand() == "Send")  //Sends the text in the text input area, same as enter
        {
                try
                {
                disp = tf4.getText();
                if(disp.equals("%q"))
                {
                        try
                        {
                                disp = "%q\n";
                                tf4.setText("");
                                ta1.append("(" + id + ":) " + disp + "\n");
                                outbytes = (disp + "\n").getBytes();  
                                os.write(outbytes);
                                os.flush();
                                s.close();
                                ta1.setText("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException e)
                        {
                                ta1.setText((e + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                        CurCon = 0;
                }
                else
                {
                outbytes = (disp + "\n").getBytes();  
                os.write(outbytes);
                os.flush();
                tf4.setText("");
                }
                }
                catch (IOException e)
                {
                        ta1.append(e.getMessage() + "\n");
                        try
                        {
                        s.close();
                        ta1.append("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException r)
                        {
                                ta1.setText((r + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                }
        }

        if(event.getActionCommand() == "Connect")  //Initiates a socket connection
        {
                id = tf3.getText();
                if(id.equals(""))
                {
                        ta1.append("\n");
                        ta1.append("Enter a user name first, under 'My Name:' ...\n");
                        ta1.append("\n");
                }
                else
                {
                        ta1.append("\n");
                        ta1.append("\n");
                        ta1.append("Your username is: " + id + "\n");
                        ta1.append("------------------------------------------------------------\n");
                        ta1.append("Please wait while connecting...");
                        ta1.append("\n");
                        host = tf1.getText();
                        try
                        {
                        s = new Socket(host, port);
                        os = s.getOutputStream();
                        is = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        
                        ipKFrom f = new ipKFrom(is);
                        f.start();  //Starts recieving thread...
                        outbytes = (id + "\n").getBytes();  
                        os.write(outbytes);
                        os.flush();
                        
                        Start.setEnabled(true);
                        star.setEnabled(true);
                        Disconnect.setEnabled(true);
                        Connect.setEnabled(false);
                        CurCon = 1;
                        }  
                        catch (UnknownHostException e)
                        {
                        ta1.append("unknown host: " + host + "\n");
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                        CurCon = 0;
                        }
                        catch (IOException e)
                        {
                        ta1.append(e.getMessage() + "\n");
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                        CurCon = 0;
                        }
                }
        }

        if(event.getActionCommand() == "Disconnect")  //Closes the connection
        {               
                try
                {
                        disp = "%q\n";
                        ta1.append("(" + id + ":) " + disp + "\n");
                        outbytes = (disp + "\n").getBytes();  
                        os.write(outbytes);
                        os.flush();
                        s.close();
                        ta1.setText("You have disconnected from the server...\n");
                }
                catch(java.io.IOException e)
                {
                        ta1.setText((e + "\n"));
                }
                Start.setEnabled(false);
                star.setEnabled(false);
                Disconnect.setEnabled(false);
                Connect.setEnabled(true);
                CurCon = 0;
        }
        
        if(event.getActionCommand() == "Get Quote")  //Get the quotes using Quote Now
        {
                quoteString = tf5.getText();
                String token = null;
                
                Quote q = new Quote();
                
                ta1.append("\n\n");
                
                StringTokenizer st = new StringTokenizer(quoteString);
                while (st.hasMoreTokens()) 
                {
                        infoQ info = q.getQ(st.nextToken());
                        ta1.append("Symbol: " + info.symbol + "\n");
                        ta1.append("Name: " + info.name + "\n");
                        ta1.append("Price: " + info.price + "\n");
                        ta1.append("Change: " + info.change + "\n");
                        ta1.append("Volume: " + info.volume + "\n");
                        ta1.append("\n");
                }
                
                ta1.append("\n");
        }
  }


  class WindowListener1 extends WindowAdapter  //Listens for window events, same as quit
  {
          public void windowClosing(WindowEvent event)
          {
                if(serverr == 1)
                {
                        ServeiN.destroy();
                }
                if(CurCon == 1)
                {
                try
                {
                disp = "%q\n";
                outbytes = (disp + "\n").getBytes();  
                os.write(outbytes);
                os.flush();
                }
                catch(IOException e)
                {
                        ta1.append(e.getMessage() + "\n");
                        try
                        {
                        s.close();
                        ta1.append("You have disconnected from the server...\n");
                        }
                        catch(java.io.IOException r)
                        {
                                ta1.setText((r + "\n"));
                        }
                        Start.setEnabled(false);
                        star.setEnabled(false);
                        Disconnect.setEnabled(false);
                        Connect.setEnabled(true);
                }
                }
            Window win = event.getWindow();
            win.setVisible(false);
            win.dispose();
            System.exit(0);
          }
  }


  public static void main (String argv[])  //Parses command line arguments and sets up the Application
  {
        String usage = "usage: java DemoClient [-h host] [-p port] ID " +
          "\n (default port=" + port +", host=" + host + ")";
        try
        {
                Arguments arguments = new Arguments(argv);  //Start parsing commmand line
                if (arguments.isSpecified("h")) 
                {
                        host = arguments.getModified("h");
                }
                if (arguments.isSpecified("p"))
                {
                        port = arguments.getModifiedInt("p");
                }
                if (arguments.getUnmodified().size() > 0)
                {
                        id = (String) arguments.getUnmodified().get(0);  //End parse
                        connectNow = 1;
                }
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
        ipKchat m = new ipKchat ();
        tf3.setText(id);
        tf1.setText(host);
        InetAddress iNet = null;
        try
        {
                iNet = InetAddress.getLocalHost();
                tf2.setText(iNet.toString());
        }
        catch(java.net.UnknownHostException e)
        {
                 ta1.append("\n" + e + "\n");
        }
        if(connectNow == 1)
        {
                host = tf1.getText();
                id = tf3.getText();
                try
                {
                s = new Socket(host, port);
                os = s.getOutputStream();
                is = new BufferedReader(new InputStreamReader(s.getInputStream()));
                
                        ipKFrom f = new ipKFrom(is);
                f.start();  //Starts recieving thread...
                outbytes = (id + "\n").getBytes();  //  this client
                os.write(outbytes);
                os.flush();
                ta1.append("\n");
                ta1.append("\n");
                ta1.append("Your username is: " + id + "\n");
                ta1.append("------------------------------------------------------------\n");
                ta1.append("\n");
                Start.setEnabled(true);
                star.setEnabled(true);
                Disconnect.setEnabled(true);
                Connect.setEnabled(false);
                CurCon = 1;
                }  
                catch (UnknownHostException e)
                {
                ta1.append("unknown host: " + host + "\n");
                Start.setEnabled(false);
                star.setEnabled(false);
                Disconnect.setEnabled(false);
                Connect.setEnabled(true);
                CurCon = 0;
                }
                catch (IOException e)
                {
                ta1.append(e.getMessage() + "\n");
                Start.setEnabled(false);
                star.setEnabled(false);
                Disconnect.setEnabled(false);
                Connect.setEnabled(true);
                CurCon = 0;
                }
        }
  }
  
  
}

//End class ipKchat
