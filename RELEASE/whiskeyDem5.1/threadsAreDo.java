/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
All the real work goes on here!  A thread is created for each client that connects.
This thread will listen for server command and execute them.  It will set up the 
hash table and allow communication to a chat room or another user.  When the 
connection terminates, the user will be removed from the hash table.  This thread
will also make sure that no two users have the same name.  Note that this class is
inline, function calls in java a too expensive and for a large amount of users the 
server could potentially get bogged down with alot of uneeded function calls.
*/

import java.io.*;
import java.net.*;
import java.util.*;


//The worker thread and the hardest part of it all

public class threadsAreDo extends Thread
{
  static Map users = new Hashtable();  //Hashtable baby, no stinkin' vector's here
  static final String usages = "< %c name - Start talk;  %u - List users;  %t - Stop talk;" +
          "%i - Change name;  %% - %;  %q - Quit;  ...  >" +
          "%j - Enter J-ChatRoom;  %k - Enter K-ChatRoom;  %s - Stop availability >";
  static Info Jinfo = new Info("J-ChatRoom", null);
  static Info Kinfo = new Info("K-ChatRoom", null);
  static Info StartStop = new Info("Unavailable", null);
  Info info;
  int checkExit = 0;  //Useful for when two IDs are tried
        
 
  public threadsAreDo(Socket s, String name) throws IOException
  {
    if (users.containsKey(name))  //Checks to see if that name already exists
    {
      s.getOutputStream().write(("<User " + name + " already connected.  Choose a new name upon restart...>").getBytes());
      s.getOutputStream().write(("\n").getBytes());
      s.getOutputStream().flush();
      s.getOutputStream().write("<Quiting...>\n".getBytes());
      s.getOutputStream().flush();
      OutputStream oss = s.getOutputStream();
      oss.write("QUIT\n".getBytes());
      oss.flush();
      s.close();
      checkExit = 1;  //When the duplicate client exits this is set so it
    }  //  will not bring down the other client with the same name
    if(checkExit == 0)
    {
      info = new Info(name, s);
      users.put(name, info);
    }
  }

  public void run()
  {  //Main loop where commands are parsed and messages are brokered
  if(checkExit == 0)
  {
  try
  {  //You should have told us about PrintWriter in lecture or used it
    BufferedReader in = new BufferedReader(new InputStreamReader(info.s.getInputStream()));
    PrintWriter out = new PrintWriter(info.s.getOutputStream(), true);
     //  in the examples, it's VERY usefull
    String line;
    out.println("");
    out.println("<You are now connected...>");
    out.println("<Welcome to ipKchat Server...>");
    out.println("");
    while ((line = in.readLine()) != null)
    {
      out.println("(" + info.name + ":)  " + line);
      StringTokenizer st = new StringTokenizer(line);
      String command = "";
      if (st.hasMoreTokens())
      command = st.nextToken();
      if (command.equalsIgnoreCase("%c"))  //Connects to another user
      {
        String user = null;
        if (st.hasMoreTokens())  //Expected stuff, next token is name to
        user = st.nextToken();  //  talk to
        if (user == null)
        {
          out.println("<Invalid syntax, usage: %c user>");
        }
        else
        {
          if (!users.containsKey(user))
          {
            out.println("<User is not online...>");
          }
          else
          {
            Info other = (Info) users.get(user);
            if (other.connected != null)
            {
              out.println("<sorry, " + user + " is busy...>");
            }
            else
            {  //If not busy or not online then it connects the two
              info.connected = other;  //here it connects the two
              other.connected = info;  //just linkin' the objects in the hashtable
              info.talkStamp = System.currentTimeMillis();  //Sets timestamp
              other.talkStamp = info.talkStamp;
              out.println("<You are now talking to: " + info.connected.name + ">");
              OutputStream oss = info.connected.s.getOutputStream();
              byte [] outbytess = ("<You are now talking to: " + info.name + ">").getBytes();
              oss.write(outbytess, 0, outbytess.length);  //Writes terminated screen
              oss.write("\n".getBytes());  //  with time talked
              oss.flush();  //As they say, after you go, you always need to flush
              oss.write("ALERT\n".getBytes());
              oss.flush();
            }
          }
        }
      }

        else if (command.equalsIgnoreCase("%u"))  //Displays user info
        {
          //out.println(usages);
          //out.println("");
          out.println("<name                  talking-to                  talktime                  login time>");
          for (Iterator i = users.values().iterator(); i.hasNext();)
          out.println(i.next().toString());
        }  //Searches hashtable and prints out all data with the overriden toString()

        else if(command.equalsIgnoreCase("%t"))  //Terminates connection
        {
          if(info.room != 0 | info.connected == StartStop)
          {
            float l = (float)(System.currentTimeMillis() - info.talkStamp);
            l = l/60000;
            out.println("<Connection terminated...  Talk time: " + l + "min>");
            info.talkStamp = 0;
            info.room = 0;
            info.connected = null;
          }
          else if(info.connected != null)
          {  //Figures time in min, this is variable l
            float l = (float)(System.currentTimeMillis() - info.talkStamp);
            l = l/60000;
            out.println("<Connection terminated...  Talk time: " + l + "min>");
            OutputStream oss = info.connected.s.getOutputStream();
            byte [] outbytess = ("<Connection terminated...  Talk time: " + l + "min>").getBytes();
            oss.write(outbytess, 0, outbytess.length);  //Writes terminated screen
            oss.write("\n".getBytes());  //  with time talked
            oss.flush();  //As they say, after you go, you always need to flush
            info.talkStamp = 0;
            info.connected.talkStamp = 0;
            info.connected.connected = null;  //This is where we hang up the connection
            info.connected = null;  //God, I love object oriented programming
          }
        }

        else if(command.equalsIgnoreCase("%q"))  //This quits
        {
          if(info.room != 0 | info.connected == StartStop)
          {
            float l = (float)(System.currentTimeMillis() - info.talkStamp);
            l = l/60000;
            out.println("<Connection terminated...  Talk time: " + l + "min>");
            info.talkStamp = 0;
            info.room = 0;
            info.connected = null;
          }
          else if(info.connected != null)
          {  //Figures time in min, this is variable l
            float l = (float)(System.currentTimeMillis() - info.talkStamp);
            l = l/60000;
            out.println("<Connection terminated...  Talk time: " + l + "min>");
            OutputStream oss = info.connected.s.getOutputStream();
            byte [] outbytess = ("<Connection terminated...  Talk time: " + l + "min>").getBytes();
            oss.write(outbytess, 0, outbytess.length);  //Writes terminated screen
            oss.write("\n".getBytes());  //  with time talked
            oss.flush();  //As they say, after you go, you always need to flush
            info.talkStamp = 0;
            info.connected.talkStamp = 0;
            info.connected.connected = null;  //This is where we hang up the connection
            info.connected = null;  //God, I love object oriented programming
          }                         //  without this, null pointer would be there
          System.out.println(info.name + " has logged off...");
          users.remove(info.name);  //Here we remove from the hashtable
          info.s.close();  //And finally close the socket
        }

        else if(command.equalsIgnoreCase("%i"))  //Change name command
        {  //Hardest part of the assignment
          if(info.connected == null)
          {
            String newname = null;
            if (st.hasMoreTokens())
              newname = st.nextToken();
            if (newname == null)
            {  //Checks for syntax
              out.println("<Invalid syntax, usage: %i newname>");
            }
            else
            {
              if (users.containsKey(newname))
              {  //Checks for name duplication
                out.println("<Name is in use, try again...>");
              }
              else
              { //Finally removes old key and put in a new one
                users.remove(info.name);
                info.name = newname;
                users.put(newname, info);
              }
            }
          }
          else
          {  //Though this would be a good idea...  It's not necessary, but...
            out.println("<To change name, terminate talk first (%t)...>");
          }  //  it seemed more logical to do this.  None of the other connections
        }  //  are hurt when a name change occurs
        
        else if(command.equalsIgnoreCase("%s"))  //Marks user as unavailable
        {
                if(info.connected == null && info.room == 0)
                {
                        info.connected = StartStop;
                        out.println("<You are now marked 'Unavailable...'>");
                        out.println("<To become 'Available...' type %s>");
                }
                else
                {
                        out.println("<You must terminate current connection first '%t'...'>");
                }
        }

        else if(command.equalsIgnoreCase("%j"))  //Joins J chat room
        {
                if(info.connected == null && info.room == 0)
                {
                        info.room = 1;
                        info.talkStamp = System.currentTimeMillis();
                        info.connected = Jinfo;
                        out.println("<You are now talking in: J-ChatRoom...>");
                }
                else
                {
                        out.println("<You must terminate connection first (%t)...>");
                }
        }
        
        else if(command.equalsIgnoreCase("%k"))  //Joins K chat room
        {
                if(info.connected == null && info.room == 0)
                {
                        info.room = 2;
                        info.talkStamp = System.currentTimeMillis();
                        info.connected = Kinfo;
                        out.println("<You are now talking in: K-ChatRoom...>");
                }
                else
                {
                        out.println("<You must terminate connection first (%t)...>");
                }
        }
                 
        else
        {
          if (line.startsWith("%%"))  //Strips off leading %
            line = line.substring(1);
          if (info.connected == null && info.room == 0)
          {
            out.println("<You are not connected to anyone...>");
          }
          else
          {  //If its not a command, it sends it to a connected client or one of the two chat rooms, K or J
                if(info.room == 1)  //J room
                {
                        Info Jtemp;
                        for (Iterator i = users.values().iterator(); i.hasNext();)
                        {  
                                Jtemp = (Info) i.next();
                                if(Jtemp.room == 1 && Jtemp.name != info.name)
                                {
                                        Jtemp.s.getOutputStream().write(("(" + info.name + ":)  " + line).getBytes());
                                        Jtemp.s.getOutputStream().write("\n".getBytes());
                                        Jtemp.s.getOutputStream().flush();                      
                                }
                        }                       
                }
                else if(info.room == 2)  //K room
                {
                        Info Ktemp;
                        for (Iterator i = users.values().iterator(); i.hasNext();)
                        {  
                                Ktemp = (Info) i.next();
                                if(Ktemp.room == 2 && Ktemp.name != info.name)
                                {
                                        Ktemp.s.getOutputStream().write(("(" + info.name + ":)  " + line).getBytes());
                                        Ktemp.s.getOutputStream().write("\n".getBytes());
                                        Ktemp.s.getOutputStream().flush();                      
                                }
                        }                       
                }
                else if(info.connected == StartStop)  //Reminds user the he/she is marked 'Unavailable'
                {
                        out.println("<Press '%t' to become 'Available'>");
                }
                else  //Transmits text to other connected user
                {
                        info.connected.s.getOutputStream().write(("(" + info.name + ":)  " + line).getBytes());
                        info.connected.s.getOutputStream().write("\n".getBytes());
                        info.connected.s.getOutputStream().flush();
                }
          }  //Tada, that's all there is to it
        }
      }
    }
    catch (java.io.IOException e)
    {
        if(info.connected != null)
        {
          info.connected.connected = null;
        }
        users.remove(info.name);  //  doesn't take the server down.
        try
        {
                info.s.close();
        }
      catch (IOException more) {}
       // System.out.println(e.getMessage());
       // e.printStackTrace();
    }
    if(checkExit == 0)
    {  //This is to make sure that a client with the same name as another
      users.remove(info.name);  //  doesn't take the server down.
      try
      {
        info.s.close();
      }
      catch (IOException e) {}
    }
  }
  }
}

//End class threadsAreDo
