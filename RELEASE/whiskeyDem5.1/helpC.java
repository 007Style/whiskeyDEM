/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
This class displays the 'Help' dialogue for ipKchat.
*/

import java.lang.*;
import java.lang.String;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;


public class helpC extends Frame implements ActionListener
{
  Panel paneltxtfld = new Panel(new GridBagLayout());
  public helpC()
  {
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    
    Panel panelbut = new Panel();

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    
    add(panelbut, gbc);
    
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1.0;
    
    add(paneltxtfld, gbc);

    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;

    TextArea ta1;
   
    ta1 = new TextArea("Welcome to ipKchat!\n\nipKchat is a Client/Server based text chat program.  The user can either talk to a single other user or join one of two chat rooms to talk to an unlimited number of users.  The server can support extremely high volumes, the limit is only set by the capabilities of the server hardware.  In addition local servers can also be set up via the 'Sever' menu.\n\nThe commands are as follows:\n\n%c <name> - Connect to name\n%t - Terminate talk or set Available\n%j - Join J-ChatRoom\n%k - Join K-ChatRoom\n%u - List users\n%s - Set Unavailable\n%i <name> - Change name\n\n", 30, 44,1);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    paneltxtfld.add(ta1, gbc);
    ta1.setEditable(false);

    Button close = new Button(" - Close - ");

    Font    font = new Font ("Times", Font.BOLD, 14);
    Color   numberColor = new Color (50, 200, 255);
    Color   equalsColor = new Color (120, 160, 170);
    setFont (font);
    
    panelbut.add(close);
    close.setBackground(numberColor);
    close.addActionListener(this);
    
    WindowListener2 lWindow = new WindowListener2();
    addWindowListener(lWindow);
    
    setTitle("ipKchat Help");
    setBackground(Color.white);
    setSize (400, 200);
    pack();
    setResizable(false);
    this.setLocation(0,0);
    show ();
  }
  
  public void actionPerformed(ActionEvent event)
  { 
    if(event.getActionCommand() == " - Close - ")
      {
        this.dispose();
      }
  }
  
  class WindowListener2 extends WindowAdapter
  {
    public void windowClosing(WindowEvent event)
    {
      Window win = event.getWindow();
      win.setVisible(false);
      win.dispose();
      //System.exit(0);
    }
  }
  
  public static void main (String args[])
  {  
    helpC hellc = new helpC();
  }
}









