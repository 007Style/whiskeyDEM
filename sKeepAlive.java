/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */
 
/*
When the server is activated in the ipKchat gui, this class prints 
the server to the main dialogue box in the gui.  This is all it 
does, pretty simple, huh?
*/

import java.io.*;
import java.net.*;
import java.util.*;




public class sKeepAlive extends Thread
{
        Server ServiN;
        Process servess;
                
        public sKeepAlive(Process p) 
        {
                servess = p;
        }
        
        public void run()
        {        
          BufferedReader is = new BufferedReader(new InputStreamReader(servess.getInputStream()));
          while(ipKchat.serverr == 1)
          {
                try
                {
                ipKchat.ta1.append(is.readLine() + "\n");
                }
                catch (java.io.IOException e) {}
          }
        }
}