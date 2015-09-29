/*
 * BotFlightBehavior.java
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;


public class BotFlightBehavior extends Behavior
{
  private Vector m_listeners = new Vector();
  private WakeupOnElapsedTime  m_timer;
  private boolean m_stopped = true;
  private Dem m_dem = null;

  private TransformGroup m_translator = new TransformGroup(); // temporary, until setJoint() is issued
  private TransformGroup m_rotator = new TransformGroup();    // temporary, until setJoint() is issued
  private Transform3D m_currXformT = new Transform3D();
  private Transform3D m_currXformR = new Transform3D();
  private BranchGroupRT m_plane = new BranchGroupRT();

  // flight parameters
  private double  m_roll  = 0.0;    // radians per second, aelerons
  private double  m_pitch = 0.0;    // radians per second, elevator
  private double  m_yaw   = 0.0;    // radians per second, rudder
  private double  m_speed = 0.0;    // meters per second, throttle

  private double  m_reverse = 1.0;
  
  private int m_x, m_y, m_z, m_speedX, m_speedY, m_speedZ = 0;
  private boolean firstTime = true;
  
  /** Creates new FlightBehavior */
  public BotFlightBehavior()
  {
    super();
  }

  public BotFlightBehavior(int x, int y, int z, int speedX, int speedY, int speedZ)
  {
  	super();
  	if(x == 0)
  	{
  		do{ m_x = (int)(Dem.DEM_WIDTH_METERS * java.lang.Math.random()); } while(m_x < 800);
  	}
  	else m_x = x;
  	if(y == 0)
  	{
  		do{ m_y = (int)(Dem.DEM_WIDTH_METERS * java.lang.Math.random()); } while(m_y < 800);
  	}
  	else m_y = y;
  	if(z == 0)
  	{
  		do{ m_z = (int)(18000 * java.lang.Math.random()); } while(m_z < 1000);
  	}
  	else m_z = z;
  	m_speedX = speedX;
  	m_speedY = speedY;
  	m_speedZ = speedZ;
  	m_plane.moveAbsolute(new Point3d(m_x, m_y, m_z));
  	//System.out.println(m_x + " " + m_y + " " + m_z + " " + m_speedX + " " + m_speedY + " " + m_speedZ);
  }

  public void initialize()
  {
    setSpeed(0.0);
    m_timer = new WakeupOnElapsedTime(1000 / ProgramConfig.getFramesPerSecond());
    this.wakeupOn(m_timer);
  }

  public void setFramesPerSecond()
  {
    m_timer = new WakeupOnElapsedTime(1000 / ProgramConfig.getFramesPerSecond());
    this.wakeupOn(m_timer);
  }
  
  public void setReverse(boolean rev)
  {
    m_reverse = rev ? -1.0 : 1.0;
  }
  
  public void setSpeed(double speed)
  {
    //System.out.println("set speed=" + speed);
    m_speed = speed * m_reverse;
  }

  public void setJoint(BranchGroupRT joint)
  {
    //m_rotator = joint.getRotation();
    m_translator = joint.getPosition();
    m_plane = joint;

    m_translator.addChild(this);  // rotator is already child of translator
  }
  
  public void processStimulus (Enumeration criteria)
  {
    WakeupCriterion wakeup = null;

    // Process all pending wakeups
    while( criteria.hasMoreElements( ) ) {
      wakeup = (WakeupCriterion)criteria.nextElement( );
      if ( wakeup instanceof WakeupOnElapsedTime ) {
        processTimerTick();
      }
    }
    this.wakeupOn( m_timer );
  }

  public void stopMovement()
  {
    m_roll  = 0.0;
    m_pitch = 0.0;
    m_yaw   = 0.0;
    m_speed = 0.0;
  }

  private void processTimerTick()
  {
    processMovement();
  }
  
  private void processMovement()
  {
    //int framesPerSecond = ProgramConfig.getFramesPerSecond();
    if(firstTime)
    {
    	m_plane.moveAbsolute(new Point3d(m_x, m_y, m_z));
    	firstTime = false;
    }
    else
    {    
    	// process the forward movement
			Point3d cur = m_plane.getPos();
			//Point3d cur = new Point3d(m_x, m_y, m_z);
			//Point3d move = new Point3d(0,10,0);
			Point3d move = new Point3d(m_speedX, m_speedY, m_speedZ);
			move.add(cur);
    	m_plane.moveAbsolute(move);
    	double[] t = new double[3];
    	cur.get(t);
    	if(t[0]>Dem.DEM_WIDTH_METERS || t[0]<0) firstTime=true;
    	if(t[1]>Dem.DEM_WIDTH_METERS || t[1]<0) firstTime=true;
    	if(t[2]>60000 || t[2]<0) firstTime=true;
    }
  }
}
