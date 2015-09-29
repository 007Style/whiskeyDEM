/*
 * flightPacket.java
 */
 
import java.awt.AWTEvent;
import javax.vecmath.*;
import java.io.Serializable;


public class flightPacket implements Serializable 
{
  public Point3d   m_newPos = new Point3d(0,0,0);
  public Vector3d  m_travel = new Vector3d(0,0,0);
  public double    m_distanceTraveled = 0;
  public boolean   sync = false;
  public int       fps = 0;
  //public String    m_name = null;
  //public String    m_plane = null;
  //public int       m_options = 0;
}