/*
 * whiskeyDem.java
 */
 
/*
This file is the splash screen when you start up
*/


import java.awt.*;
import java.awt.event.*;


public class whiskeyDem extends Frame 
{       
        Image image;
        
        static PicturePerfect pp;
        
        public whiskeyDem()
        {
        	      Image icon = getToolkit().getImage("whiskeysplash.jpg");
                this.setIconImage(icon);
                double val = java.lang.Math.random();
                String pic = "";
                if(val >= .5)
                {
                        pic = "whiskeysplash.jpg";
                }
                else
                {
                        pic = "whiskeysplash.jpg";
                }
                 
                Font    font = new Font ("Times", Font.BOLD, 14);
                setFont (font); 
                Color   numberColor = new Color (46, 124, 191);
            
                setTitle ("whiskeyDem - version: 5.1");
            
                //Image icon = getToolkit().getImage(pic);
                //this.setIconImage(icon);
                
                image = getToolkit().getImage(pic);

                pp = new PicturePerfect();


                WindowListener1 lWindow = new WindowListener1();
                addWindowListener(lWindow);

                
                setBackground(Color.white);
                
                setResizable(false);
                
                this.setState(Frame.NORMAL);
                this.setLocation(100,100);
                add(pp);
                pack();
                show ();
        }


        public static void main (String argv[])
        {
                whiskeyDem i = new whiskeyDem();
                try
                {
                        java.lang.Thread.currentThread().sleep(8000);
                }
                catch(java.lang.InterruptedException u) {}
                try 
                {
                	      
                        Runtime.getRuntime().exec("run.bat");
                } 
                catch (Exception e) {}  
                System.exit(0);
        }
   
  
        


        class WindowListener1 extends WindowAdapter
        {
                public void windowClosing(WindowEvent event)
                {       
                        Window win = event.getWindow();
                        win.setVisible(false);
                        win.dispose();
                        System.exit(0);
                }
        }


        class PicturePerfect extends Component
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
                        return new Dimension(720, 540);
                }
        }
}
