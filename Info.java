/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */

/*
The information about each connected client is stored here.  This includes
name, socket, connection time, talk time, chat room connected to and person
connected to.  This will also print out the information when it is requested
by this or another user.
*/

import java.net.Socket;
import java.util.Date;

//This class stores all the info needed for each connection

public class Info
{
  String name = null;  //Stores the string
  Socket s = null;  //Stores the socket
  long connStamp = System.currentTimeMillis();  //Stores the time of connection

  Info connected = null;  //This is the "Info" object of the current connection
  long talkStamp;  //Stores when the talk began
  
  int room = 0;

  public Info(String name, Socket s)
  {  //Constructor for seting up object
        this.name = name;
        this.s = s;
  }
  
  public String toString()  //Overriden meathod to print %u info
  {
        String s = "<" + name + "\t";
        if (connected != null | room != 0)
        {
                s += connected.name + "\t" + new Date(talkStamp) + "\t" + new Date(connStamp) + ">";
        }
        else
        {
                s += "-\t-";
                s += "\t" + new Date(connStamp) + ">";
        }
        return s;
  }
}
