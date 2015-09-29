/*
 * MovementModel.java
 */
 
import javax.vecmath.*;


public class MovementModel extends Object implements Runnable
{
  private int TICKS_PER_SECOND = 10;
  
  static private int fps = 0;
  
  protected FlightBehavior m_bhv;
  private Thread m_thread;
  
  // current control parameters:
  private double  m_rollStimulus  = 0.0;    // radians per second, aelerons
  private double  m_pitchStimulus = 0.0;    // radians per second, elevator
  private double  m_yawStimulus   = 0.0;    // radians per second, rudder
  private double  m_speedStimulus = 0.0;    // meters per second, throttle

  // flight parameters:
  private double  m_roll  = 0.0;            // units, aelerons
  private double  m_pitch = 0.0;            // units, elevator
  private double  m_yaw   = 0.0;            // units, rudder
  private double  m_speed = 0.0;            // units, throttle

  /** Creates new MovementModel */
  public MovementModel(FlightBehavior bhv)
  {
    m_bhv = bhv;

    m_thread = new Thread(this);
    m_thread.start();
  }
  
  static public int getFPS()
  {
  	return fps;
  }
  
  static public void setFPS(int f)
  {
  	fps = f;
  }
  
  public void setRollStimulus(double rollStimulus)
  {
    m_rollStimulus = rollStimulus;
  }

  public void setPitchStimulus(double pitchStimulus)
  {
    m_pitchStimulus = pitchStimulus;
  }

  public void setYawStimulus(double yawStimulus)
  {
    m_yawStimulus = yawStimulus;
  }

  public void setSpeedStimulus(double speedStimulus)
  {
    m_speedStimulus = speedStimulus;
  }

  public void adjustRollStimulus(boolean toLeft)
  {
    m_rollStimulus += toLeft ? -20.0 : 20.0;
  }

  public void adjustPitchStimulus(boolean toUp)
  {
    m_pitchStimulus += toUp ? -20.0 : 20.0;
  }

  public void adjustYawStimulus(boolean toLeft)
  {
    m_yawStimulus += toLeft ? -20.0 : 20.0;
  }

  public void adjustSpeedStimulus(boolean toIncrease)
  {
    m_speedStimulus += toIncrease ? 20.0 : -20.0;
    if(m_speedStimulus < 0.0) {
      m_speedStimulus = 0.0;
    }
    //System.out.println("adjustSpeedStimulus()   " + m_speedStimulus);
  }

  public void stopMovement()
  {
    m_roll  = 0.0;
    m_pitch = 0.0;
    m_yaw   = 0.0;
    m_speed = 0.0;
    
    m_rollStimulus  = 0.0;
    m_pitchStimulus = 0.0;
    m_yawStimulus   = 0.0;
    m_speedStimulus = 0.0;
    
    m_bhv.setRoll(m_roll);
    m_bhv.setPitch(m_pitch);
    m_bhv.setYaw(m_yaw);
    m_bhv.setSpeed(m_speed);
  }

  protected void processControlsPositions()
  {
    m_roll  = -m_rollStimulus * 0.4;
    m_pitch = -m_pitchStimulus * 0.4;
    m_yaw   = -m_yawStimulus * 0.4;
    m_speed = m_speedStimulus * 1.0;

    // make sure rotational components melt down in time, because both 
    // sticks are not available simultaneously, and having yaw stuck is not fun:
    m_roll  *= 0.9;
    m_pitch *= 0.9;
    m_yaw   *= 0.9;
    /*
    if(m_speed > 10.0) {
      m_speed *= 0.9;
    }
    */

    // stumulae should also fade in time:
    m_rollStimulus  *= 0.8;
    m_pitchStimulus *= 0.8;
    m_yawStimulus   *= 0.8;
  }
        
  public void run()
  {
    while(true) {
      try {
        Thread.sleep(1000 / TICKS_PER_SECOND);
        fps++;
        // calculate flight parameters based on controls positions:
        processControlsPositions();
        
        // set flight parameters, if they are not zero:
        if((Math.abs(m_roll) > 0.01) || (Math.abs(m_pitch) > 0.01)
                || (Math.abs(m_yaw) > 0.01) || (Math.abs(m_speed) > 0.01)) {
          m_bhv.setRoll(m_roll);
          m_bhv.setPitch(m_pitch);
          m_bhv.setYaw(m_yaw);
          m_bhv.setSpeed(m_speed);
        }
        
      } catch(Exception e) {
        System.out.println("Exception in run(): " + e);
        e.printStackTrace();
      }
    }
  }
}