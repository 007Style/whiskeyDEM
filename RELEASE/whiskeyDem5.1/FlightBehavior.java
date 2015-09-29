/*
 * FlightBehavior.java
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;


public class FlightBehavior extends Behavior
{
  private Vector m_listeners = new Vector();
  private WakeupOnElapsedTime  m_timer;
  private boolean m_stopped = true;
  private Dem m_dem = null;

  private TransformGroup m_translator = new TransformGroup(); // temporary, until setJoint() is issued
  private TransformGroup m_rotator = new TransformGroup();    // temporary, until setJoint() is issued
  private Transform3D m_currXformT = new Transform3D();
  private Transform3D m_currXformR = new Transform3D();

  // flight parameters
  private double  m_roll  = 0.0;    // radians per second, aelerons
  private double  m_pitch = 0.0;    // radians per second, elevator
  private double  m_yaw   = 0.0;    // radians per second, rudder
  private double  m_speed = 0.0;    // meters per second, throttle

  private double  m_reverse = 1.0;
  
  /** Creates new FlightBehavior */
  public FlightBehavior()
  {
    super();
  }

  public void initialize()
  {
    setRoll(0.0);
    setPitch(0.0);
    setYaw(0.0);
    setSpeed(0.0);
    m_stopped = true;
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
  
  public void setRoll(double roll)
  {
    //System.out.println("set roll=" + roll);
    m_roll = roll * m_reverse;
  }

  public void setPitch(double pitch)
  {
    //System.out.println("set pitch=" + pitch);
    m_pitch = pitch; // * m_reverse;
  }

  public void setYaw(double yaw)
  {
    //System.out.println("set yaw=" + yaw);
    m_yaw = yaw * m_reverse;
  }

  public void setSpeed(double speed)
  {
    //System.out.println("set speed=" + speed);
    m_speed = speed * m_reverse;
  }

  public void setJoint(BranchGroupRT joint)
  {
    m_rotator = joint.getRotation();
    m_translator = joint.getPosition();

    m_translator.addChild(this);  // rotator is already child of translator
  }

  public void setDem(Dem dem)
  {
    m_dem = dem;
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

  private void setStopped(boolean stopped)
  {
    if(m_stopped != stopped) {
      m_stopped = stopped;
      if(stopped) {
        // report "stopped" event
        //System.out.println("report \"stopped\" event");
        if(m_dem != null) {
          m_dem.renderFast(false);
        }
      } else {
        // report "started" event
        //System.out.println("report \"started\" event");
        if(m_dem != null) {
          m_dem.renderFast(true);
        }
      }
    }
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
    if((Math.abs(m_roll) < 0.01) && (Math.abs(m_pitch) < 0.01)
            && (Math.abs(m_yaw) < 0.01) && (Math.abs(m_speed) < 0.01)) {
      stopMovement();
      setStopped(true);   // send events if needed
      return;
    }
    setStopped(false);    // send events if needed
    
    processMovement();
  }
  
  private void processMovement()
  {
    Matrix3d noseRotation = new Matrix3d();     // rotational component of the rotator
    Transform3D m_currXformR = new Transform3D();
    m_rotator.getTransform(m_currXformR);
    m_currXformR.get(noseRotation);

    //System.out.println("noseRotation=" + noseRotation);
    
    int framesPerSecond = ProgramConfig.getFramesPerSecond();

    // process pitch (elevator function):
    AxisAngle4d pitchDirection = new AxisAngle4d(1.0, 0.0, 0.0, m_pitch * ProgramConfig.getPitchFactor() / framesPerSecond);
    Matrix3d pitchRotation = new Matrix3d();     // rotational component set by pitch
    pitchRotation.set(pitchDirection);
    noseRotation.mul(pitchRotation);

    // process yaw (rudder function):
    AxisAngle4d yawDirection = new AxisAngle4d(0.0, 1.0, 0.0, m_yaw * ProgramConfig.getYawFactor() / framesPerSecond);
    Matrix3d yawRotation = new Matrix3d();     // rotational component set by yaw
    yawRotation.set(yawDirection);
    noseRotation.mul(yawRotation);

    // process roll (aelerons function):
    AxisAngle4d rollDirection = new AxisAngle4d(0.0, 0.0, 1.0, m_roll * ProgramConfig.getRollFactor() / framesPerSecond);
    Matrix3d rollRotation = new Matrix3d();     // rotational component set by roll
    rollRotation.set(rollDirection);
    noseRotation.mul(rollRotation);

    // we are done with rotations, set the rotator:
    m_currXformR.set(noseRotation);

    //System.out.println("noseRotation=" + noseRotation);

    m_rotator.setTransform(m_currXformR);

    // process the forward movement, using nose direction and speed:

    Vector3d noseDirection = new Vector3d(0, 0, -1.0);  // where the nose is directed
    noseRotation.transform(noseDirection);              // apply rotational matrix
    double distanceTraveled = m_speed * ProgramConfig.getSpeedFactor() / framesPerSecond;
    noseDirection.scale(distanceTraveled);   // now holds displacement adjusted for frame rate

    //System.out.println("noseDirection=" + noseDirection);

    Transform3D m_currXformT = new Transform3D();
    m_translator.getTransform(m_currXformT);
    Vector3d tmp = new Vector3d();
    m_currXformT.get(tmp);
    tmp.add(noseDirection);

    //System.out.println("new position=" + tmp);
    notifyListeners(new Point3d(tmp.x, tmp.y, tmp.z), new Vector3d(tmp), Math.abs(distanceTraveled));
    
    m_currXformT.set(tmp);
    m_translator.setTransform(m_currXformT);
  }
  
  public void addPositionListener(PositionListener listener)
  {
    if(!m_listeners.contains(listener)) {
      m_listeners.addElement(listener);
    }
  }
  
  public void removePositionListener(PositionListener listener)
  {
    m_listeners.removeElement(listener);
  }
  
  private void notifyListeners(Point3d newPos, Vector3d travel, double distanceTraveled)
  {
    Vector copyOfListeners = (Vector)(m_listeners.clone());
    PositionEvent posEvent = new PositionEvent(this, newPos, travel, distanceTraveled);
    Enumeration enume = copyOfListeners.elements();
    while(enume.hasMoreElements()) {
      PositionListener listener = (PositionListener)enume.nextElement();
      listener.positionChanged(posEvent);
    }
  }
}
