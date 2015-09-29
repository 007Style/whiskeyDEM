/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */

/*
This class displays the 'Help' dialogue box for ipKtalk.
*/


import java.lang.*;
import java.lang.String;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;


public class help extends Frame implements ActionListener
{
  Panel paneltxtfld = new Panel(new GridBagLayout());
  public help()
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
   
    ta1 = new TextArea("Welcome to ipKtalk!\n\n  To use this program either set the <server> checkbox on or off depending if you will be the server or not.  Then press connect.  You will connect to the other person who has been waiting in <server> mode or vice versa.  To disconnect simply hit the 'Disconnect' button.  The 'Compatability Mode' should only be toggled if you are experiencing dificulty.\n\nOnce you have connect press 'Talk' once to start recording.  Press 'Talk' again to stop recording and send the audio clip.  Anytime during the session you may hear the person you are connected to through your speakers.\n\nTo launch ipKchat, click on the ipK menu then click on 'Launch ipKchat' and it will launch it for you!\n\n\nThank you for using ipK!", 30, 40,1);
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
    
    setTitle("Help");
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
    help helli = new help();
  }
}









