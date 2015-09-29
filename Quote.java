/*
 * Copyright (c) 2000-2001 ipKangaroo
 *
 */

/*
This class parses html pages from Money.com and returns the stock quote to 
ipKchat!
*/


import java.net.*;
import java.io.*;
import java.lang.*;


public class Quote 
  {
        infoQ info = new infoQ();
        String quoted;
        int index = 0;
        int endex = 0;
        int Error = 0;

        public Quote() {}

        public infoQ getQ(String m)
        {  //This is responsible for getting the web info and parsing it.
                String where = "http://quote.money.com/money/quote/qc?symbols=" + m;
                //System.out.println("Looking for: " + m);
                try
                {
                        StringBuffer b = new StringBuffer();
                        // Create an URL instance
                        URL url = new URL(where);

                        // Get an input stream for reading
                        InputStream in = url.openStream();
        
                        // Create a buffered input stream for efficency
                        BufferedInputStream bufIn = new BufferedInputStream(in);
                        // Repeat until end of file
                        while(true)
                        {
                                int data = bufIn.read();
                                
                                // Check for EOF
                                if (data == -1)
                                        break;

                                b.append((char) data);

                        }

                        bufIn.close();
                        in.close();
                        
                        quoted = b.toString();
                }
                catch (MalformedURLException mue)
                {
                        ipKchat.ta1.append("Invalid URL, can't find: money.com\n");
                        Error = 1;
                }
                catch (IOException ioe)
                {
                        ipKchat.ta1.append("I/O Error - " + ioe + "\n");
                        Error = 1;
                }
     
                info.symbol = m;
                
                if(Error == 0)
                {
                
                if(quoted.substring(0,13).equals("<!--HEADER-->"))
                {  //if nothing is found it puts this into the info class
                        info.name = "Not Found...";
                        info.price = "";
                        info.change = "";
                        info.volume = "";
                        return info;
                }
                
                //Otherwise it scraps the good crap from the page and returns it in an info class.
                
                index = quoted.indexOf("<b>", 0);
                index = quoted.indexOf("<b>", index);
                endex = quoted.indexOf("</b>", index);
                info.name = quoted.substring(index + 3, endex);
                
                index = quoted.indexOf("<b>", index+3);
                endex = quoted.indexOf("</b>", index);
                info.price = quoted.substring(index + 3, endex);
                
                index = quoted.indexOf("<b>", index+3);
                endex = quoted.indexOf("</b>", index);
                info.change = quoted.substring(index + 3, endex);
                
                index = quoted.indexOf("<b>", 0);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                index = quoted.indexOf("<b>", index+3);
                endex = quoted.indexOf("</b>", index);
                info.volume = quoted.substring(index + 3, endex);
                
                }
                return info;
                
        }
}
