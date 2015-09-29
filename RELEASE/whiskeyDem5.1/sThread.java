/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */

/*
This class sets up a thread to listen for incoming connections.  When a
Connection is recieved it will set up the sockets in ipKtalk and exit.
*/

import java.io.*;


//This class recieves waves and plays them
public class sThread extends Thread
{
        public sThread() {}

        public void run()
        {
                this.setPriority(MAX_PRIORITY);
                try
                {  //Standard socket stuff, looks for connection and accepts it
                         ipKtalk.s = ipKtalk.ss.accept();
                         ipKtalk.os = ipKtalk.s.getOutputStream();
                         ipKtalk.o = new ObjectOutputStream(ipKtalk.os);
                         ipKtalk.CurCon = 1;
                         ipKtalk.tf2.setText("Connection from: " + ipKtalk.s.getInetAddress());
                         ipKtalk.tf1.setText("" + ipKtalk.s.getInetAddress());
                         ipKtalk.c = new cThread(ipKtalk.s);
                         ipKtalk.c.start();
                         ipKtalk.Start.setEnabled(true);
                         ipKtalk.Disconnect.setEnabled(true);
                }
                catch (java.io.IOException e)
                {
                        ipKtalk.tf2.setText(e.getMessage());
                        //System.out.println(e.getMessage());
                        //e.printStackTrace();
                }
        }
}