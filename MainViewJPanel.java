 /*
 * MapJPanel.java
 */

import java.awt.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.awt.event.*;
import java.awt.event.WindowListener;
import java.io.*;

 
public class MainViewJPanel extends javax.swing.JPanel implements MouseMotionListener, MouseListener, KeyListener
{
  private Dem m_dem = null;
  private FlightBehavior m_behavior = null;
  private BotFlightBehavior bot_behavior14 = null;
  private BotFlightBehavior bot_behavior18 = null;
  public  MovementModel m_movementModel = null;
  private myView m_view;
  private Plane m_plane;
  private Plane m_plane14;
  private Plane m_plane18;
  private ControlJPanel m_controlPanel = null;
  private BoundingSphere m_bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 300000.0);
  private TransformGroup m_objTrans = new TransformGroup();
  private TransformGroup m_objPlane = new TransformGroup();
  private GraphicsConfigTemplate3D gct3d = new GraphicsConfigTemplate3D();
  private GraphicsDevice dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); 
  //private GraphicsConfiguration config = gct3d.getBestConfiguration();
                                                //null;  //GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); - doesn't work,
                                                // causes "Canvas3D: GraphicsConfiguration is not compatible with Canvas3D" and possibly fatal exception
  private Canvas3D m_canvas = new myCanvas3D(dev.getDefaultConfiguration());  //config);
  private VirtualUniverse m_universe = new VirtualUniverse();
  private Locale m_locale = new Locale(m_universe);
  private BranchGroup m_objRoot = new BranchGroup();


  /** Creates new form MapJPanel */
  public MainViewJPanel() 
  {
  	//WindowListener1 lWindow = new WindowListener1();
  	//java.awt.Window.addWindowListener(lWindow);
  	  /*addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                //closeDialog(evt);
            }
        });*/
  	
    initComponents ();

    /*
        // uncomment to test compatibility of GraphicsConfiguration with Canvas3D:
        System.out.println(" default config: " + GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
 	GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        System.out.println("GraphicDevices: " + gs.length);
	for (int j = 0; j < gs.length; j++) { 
	   GraphicsDevice gd = gs[j];
	   GraphicsConfiguration[] gc = gd.getConfigurations();
           System.out.println("GraphicDevices " + j + " configs: " + gc.length);
	   for (int i=0; i  < gc.length; i++) {
               config = gc[i];
               System.out.println(" -- " + i + " config: " + gc[i]);
               m_canvas = new myCanvas3D(config);       // every one produces "Canvas3D: GraphicsConfiguration is not compatible with Canvas3D"
	   }
        }
    */
        
    m_view = new myView(m_canvas, m_locale);   // added to locale
    
    m_behavior = new FlightBehavior();
    if(ProgramConfig.getBotsOn())
    {
    	bot_behavior14 = new BotFlightBehavior(0,111000,6000,0,-100,0);
    	bot_behavior18 = new BotFlightBehavior(1, 0, 7000, 80, 0, 0);
    }
    m_behavior.setSchedulingBounds(m_bounds);
    if(ProgramConfig.getBotsOn())
    {
    	bot_behavior14.setSchedulingBounds(m_bounds);
    	bot_behavior18.setSchedulingBounds(m_bounds);
    }
    //m_behavior.setJoint(m_view);
    m_behavior.addPositionListener(m_view);
    
    m_movementModel = new MovementModel(m_behavior);
    
    m_view.compile();
    m_locale.addBranchGraph(m_view);

    add(m_canvas, BorderLayout.CENTER);

    m_objTrans.addChild(new myBackground(m_bounds));
    m_objRoot.addChild(new Lights(m_bounds));

    // Create the transform group node and initialize it to the
    // identity.  Enable the TRANSFORM_WRITE capability so that
    // our behavior code can modify it at runtime.  Add it to the
    // root of the subgraph.
    m_objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    m_objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    m_objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    m_objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    m_objTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    m_objRoot.addChild(m_objTrans);

    // Create a simple scene and attach it to the virtual universe
    m_objRoot.addChild(new Axes());
    //Earth earth = new Earth(m_objTrans, m_bounds, this);    m_objTrans.addChild(earth);

    m_dem = DemFactory.produce(m_bounds, -1);
    MainFrame.setFrameTitle(DemFactory.getAreaName());
    m_dem.setCapability(BranchGroup.ALLOW_DETACH);
    m_objTrans.addChild(m_dem);
    m_behavior.setDem(m_dem);   // we need to process LOD events

    // create aircraft avatar:
    m_plane = PlaneFactory.produce(PlaneFactory.planeHangar(ProgramConfig.getPlaneName()));
    if(ProgramConfig.getBotsOn())
    {
    	m_plane14 = PlaneFactory.produce(PlaneFactory.planeHangar("f14.wrl"));
    	m_plane18 = PlaneFactory.produce(PlaneFactory.planeHangar("f18.wrl"));
    }
    m_objPlane.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    m_objPlane.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    m_objPlane.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    m_objPlane.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    m_objPlane.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    m_objRoot.addChild(m_objPlane);
    
    m_plane.setCapability(BranchGroup.ALLOW_DETACH);
    if(ProgramConfig.getBotsOn())
    {
    	m_plane14.setCapability(BranchGroup.ALLOW_DETACH);
    	m_plane18.setCapability(BranchGroup.ALLOW_DETACH);
    }
    m_objPlane.addChild(m_plane);
    if(ProgramConfig.getBotsOn())
    {
    	m_objPlane.addChild(m_plane14);
    	m_objPlane.addChild(m_plane18);
    	//m_objRoot.addChild(m_plane14);
    	//m_objRoot.addChild(m_plane18);
    }
    
    //m_objRoot.addChild(m_plane);
    m_behavior.setJoint(m_plane);
    if(ProgramConfig.getBotsOn())
    {
    	bot_behavior14.setJoint(m_plane14);
    	bot_behavior18.setJoint(m_plane18);
    }
    //m_behavior.setJoint(m_plane14);
    
    m_behavior.setReverse(true);  // for non-view joint control

    // Have Java 3D perform optimizations on this scene graph.
    m_objRoot.compile();

    m_locale.addBranchGraph(m_objRoot);
    
    m_canvas.addMouseMotionListener(this);
    m_canvas.addMouseListener(this);
    m_canvas.addKeyListener(this);

  }
  
  public void setFramesPerSecond()
  {
      m_behavior.setFramesPerSecond();
      if(ProgramConfig.getBotsOn())
      {
      	bot_behavior14.setFramesPerSecond();
      	bot_behavior18.setFramesPerSecond();
      }
  }
  
  public void anotherDem(int index) 
  {
      int i = 0;
      int nChildren = m_objTrans.numChildren();
      while (i < nChildren) {
          if(m_objTrans.getChild(i) == m_dem) {
              System.out.println("FYI: dem index=" + i + "  out of " + nChildren);
              m_objTrans.removeChild(i);
              m_dem = null;
              System.gc();
              m_dem = DemFactory.produce(m_bounds, index);
              MainFrame.setFrameTitle(DemFactory.getAreaName());
              m_dem.setCapability(BranchGroup.ALLOW_DETACH);
              m_objTrans.addChild(m_dem);
              m_behavior.setDem(m_dem);   // we need to process LOD events
              m_controlPanel.setDem(m_dem);
              ProgramConfig.setAreaName(DemFactory.getAreaName());
              break;
          }
          i++;
      }
  }
  
  /*
  public void anotherPlane(int index) 
  {
      int i = 0;
      int nChildren = m_objPlane.numChildren();
      while (i < nChildren) {
          if(m_objPlane.getChild(i) == m_plane) {
              System.out.println("FYI: Plane index=" + i + "  out of " + nChildren);
              m_objPlane.removeChild(i);
              resetAll();
              m_behavior = new FlightBehavior();
              m_behavior.setSchedulingBounds(m_bounds);
              //m_behavior.setJoint(m_view);
              m_behavior.addPositionListener(m_view);
              m_movementModel = new MovementModel(m_behavior);
              m_plane = null;
              System.gc();
              m_plane = PlaneFactory.produce(index);
              m_plane.setCapability(BranchGroup.ALLOW_DETACH);
              m_objPlane.addChild(m_plane);
              //m_behavior.setJoint(m_plane);
              //m_behavior.setReverse(true);  // for non-view joint control
              // Have Java 3D perform optimizations on this scene graph.
              //m_objRoot.compile();
              //m_controlPanel.setPlane(m_plane);
              break;
          }
          i++;
      }
  }
  */
  
  public void setControlPanel(ControlJPanel controlPanel)
  {
    m_controlPanel = controlPanel;
    m_behavior.addPositionListener(controlPanel);
    m_controlPanel.setDem(m_dem);
    m_controlPanel.setMainView(this);
  }

  public void resetAll()
  {
    m_movementModel.stopMovement();
    m_view.resetPosition();
    m_plane.resetPosition();
  }
  
  public void reverseRightStick()
  {
    m_reverse = m_reverse ? false : true;
  }
  
  private void initComponents() {//GEN-BEGIN:initComponents
      setLayout(new java.awt.BorderLayout());
      
  }

  private int     m_x;
  private int     m_y;
  private int     m_dx;
  private int     m_dy;
  private int     x_first;  // where mouse started or stopped temporarily, before dragging
  private int     y_first;  // where mouse started or stopped temporarily, before dragging
  private int     x_last; 
  private int     y_last;
  private boolean mouseDown = false;
  private boolean m_reverse = false;
  
  public void mouseDragged(final MouseEvent event)
  {
    //System.out.println("mouseDragged()\n");

    Dimension cSize = m_canvas.getSize();
    int midX = cSize.width / 2;
    int midY = cSize.height / 2;
    
    m_x = event.getX();
    m_y = event.getY();
    boolean leftStick = event.isControlDown();
    //m_dx = m_x - x_last;
    m_dx = m_x - midX;
    //if(Math.abs(m_dx) <= 2) {
      if(leftStick) {
        m_movementModel.setYawStimulus((double)(m_dx));
      } else {
        m_movementModel.setRollStimulus((double)(m_dx) * (m_reverse ? -1 : 1));
      }
    //  x_first = m_x;
    //}
    //m_dy = m_y - y_last;
    m_dy = -m_y + midY;
    //if(Math.abs(m_dy) <= 2) {
      if(leftStick) {
        m_movementModel.setSpeedStimulus((double)(m_dy + midY/2));
      } else {
        m_movementModel.setPitchStimulus((double)(m_dy) * (m_reverse ? -1 : 1));
      }
    //  y_first = m_y;
    //}
    x_last = m_x;
    y_last = m_y;
  }

  public void mouseMoved(final MouseEvent event)
  {
    //System.out.println("mouseMoved()\n");
    //myMouseTouched();
  }

  public void mouseReleased(final java.awt.event.MouseEvent event) 
  {
  	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    m_movementModel.stopMovement();
  }
  
  public void mouseClicked(final java.awt.event.MouseEvent event) 
  {
  }
  
  public void mouseEntered(final java.awt.event.MouseEvent event) 
  {
    m_canvas.requestFocus();
  }
  
  public void mousePressed(final java.awt.event.MouseEvent event) 
  {
    java.awt.image.BufferedImage bim = new java.awt.image.BufferedImage(1,1,java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
    setCursor(getToolkit().createCustomCursor(bim,(new Point(0,0)),"HiddenM"));
  }
  
  public void mouseExited(final java.awt.event.MouseEvent event)
  {
    m_canvas.transferFocus();
  }
  
  class WindowListener1 extends WindowAdapter  //Listens to the window
  {  //  if it's closed or minimized or moved or etc
    public void windowClosing(WindowEvent event)
    {
    	System.out.println("Disconnecting from simulation...");
    	m_controlPanel.flightClient.DisconnecT();                   
    	Window win = event.getWindow();
    	win.setVisible(false);
    	win.dispose();
    	System.exit(0);
    }
  }
  

    /** Handle the key typed event: */
    public void keyTyped(final KeyEvent e) {
	//processKey(e, "KEY TYPED: ");
    }

    /** Handle the key pressed event: */
    public void keyPressed(final KeyEvent e) {
	processKey(e, "KEY PRESSED: ");
    }

    /** Handle the key released event: */
    public void keyReleased(final KeyEvent e) {
	//processKey(e, "KEY RELEASED: ");
    }

    protected void processKey(KeyEvent e, String s)
    {
	char c = e.getKeyChar();
	int keyCode = e.getKeyCode();
	int modifiers = e.getModifiers();
	String tmpString = KeyEvent.getKeyModifiersText(modifiers);
        //System.out.println(s + " char=" + c + " keyCode=" + keyCode + " modifiers=" + modifiers + "  " + tmpString);
        switch(keyCode) {
        case KeyEvent.VK_UP:
          //System.out.println("VK_UP");
          m_movementModel.adjustPitchStimulus(false);
          break;
        case KeyEvent.VK_DOWN:
          //System.out.println("VK_DOWN");
          m_movementModel.adjustPitchStimulus(true);
          break;
        case KeyEvent.VK_LEFT:
          //System.out.println("VK_LEFT");
          m_movementModel.adjustRollStimulus(true);
          break;
        case KeyEvent.VK_RIGHT:
          //System.out.println("VK_RIGHT");
          m_movementModel.adjustRollStimulus(false);
          break;
        //case KeyEvent.VK_Q:
        //case KeyEvent.VK_E:
        case KeyEvent.VK_W:
          //System.out.println("VK_Q-W-E");
          m_movementModel.adjustSpeedStimulus(true);
          break;
        case KeyEvent.VK_Z:
        case KeyEvent.VK_S:
          //System.out.println("VK_Z-X");
          m_movementModel.adjustSpeedStimulus(false);
          break;
        case KeyEvent.VK_A:
          //System.out.println("VK_A");
          m_movementModel.adjustYawStimulus(true);
          break;
        case KeyEvent.VK_D:
          //System.out.println("VK_S");
          m_movementModel.adjustYawStimulus(false);
          break;
        case KeyEvent.VK_SPACE:
          //System.out.println("VK_SPACE");
          m_movementModel.stopMovement();
          break;
        case KeyEvent.VK_R:
        case KeyEvent.VK_ESCAPE:
          //System.out.println("VK_ESCAPE-R");
          resetAll();
          break;
        case KeyEvent.VK_ENTER:
          //System.out.println("enter");
          //if(m_controlPanel.btnCommands.isEnabled()==false)  m_controlPanel.flightClient.syncUp();
          if(m_controlPanel.isClient == 1)  
          {
          	if(ProgramConfig.getSimType() == 0) m_controlPanel.flightClient.syncUp();
          	if(ProgramConfig.getSimType() == 1) m_controlPanel.nioFlightClient.syncUp();
          }
          else if(m_controlPanel.isServer == 1)  
          {
          	//System.out.println("SENDING TRUE to client -------------------------------");
          	//MovementModel.setFPS(ProgramConfig.getSyncAfterFrame());
          	ControlJPanel.forceSync = true;
          }
          break;
        }
    }
}
