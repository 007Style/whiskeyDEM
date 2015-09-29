/*
 * MouseFlyView.java
 */
 
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;


public class MouseFlyView extends MouseBehavior implements Runnable
{
  private TransformGroup m_translator = null;
  private TransformGroup m_rotator = null;
  private Transform3D m_currXformT = new Transform3D();
  private Transform3D m_currXformR = new Transform3D();

  // flight parameters
  private double  m_roll = 0.0;
  private double  m_pitch = 0.0;
  private double  m_yaw = 0.0;
  private double  m_speed = 0.0;

  private double  x_angle;
  private double  y_angle;
  private double  x_shift;
  private double  y_shift;
  private double  z_shift;
  
  private double  x_rot_factor;
  private double  y_rot_factor;
  private double  x_shift_factor;
  private double  y_shift_factor;
  private double  z_shift_factor;
  
  private int     m_dx;
  private int     m_dy;
  private int     x_first;  // where mouse started or stopped temporarily, before dragging
  private int     y_first;  // where mouse started or stopped temporarily, before dragging
  private Thread  m_thread;
  private boolean mouseDown = false;

  /** Creates new MouseFlyView */
  public MouseFlyView() {
    super(0);
  }
  
  public void initialize() {
    super.initialize();
    x_angle = 0.0;
    y_angle = 0.0;
    x_shift = 0.0;
    y_shift = 0.0;
    z_shift = 0.0;
    x_rot_factor = .001;
    y_rot_factor = .001;
    x_shift_factor = .001;
    y_shift_factor = .001;
    z_shift_factor = .05;
    m_dx = 0;
    m_dy = 0;
    x_first = -1;
    y_first = -1;
    mouseDown = false;
  }

  public void setJoint(BranchGroupRT joint)
  {
    m_rotator = joint.getRotation();
    m_translator = joint.getPosition();
    
    setTransformGroup(null);
    m_translator.addChild(this);  // rotator is already child of translator
  }
  
  public void processStimulus (Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] event;
    int id;
    int  dx;
    int  dy;

    while (criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion) criteria.nextElement();
      if (wakeup instanceof WakeupOnAWTEvent) {
        event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
        for (int i=0; i < event.length; i++) {
          processMouseEvent((MouseEvent) event[i]);

          if (((flags & MANUAL_WAKEUP) == 0) || ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))) {

            id = event[i].getID();
            if ((id == MouseEvent.MOUSE_DRAGGED) &&
                      !((MouseEvent)event[i]).isMetaDown() &&
                      !((MouseEvent)event[i]).isAltDown()) {

              x = ((MouseEvent)event[i]).getX();
              y = ((MouseEvent)event[i]).getY();

              dx = x - x_last;
              dy = y - y_last;

              if (!reset){
                x_angle = y_angle = x_shift = y_shift = z_shift = 0.0;
                if(((MouseEvent)event[i]).isShiftDown()) {
                  x_shift = dx * x_shift_factor;
                  y_shift = dy * y_shift_factor;
                } else if(((MouseEvent)event[i]).isControlDown()) {
                  z_shift = dy * z_shift_factor;
                } else {
                  x_angle = dy * x_rot_factor;
                  y_angle = dx * y_rot_factor;
                }                
                move(x_angle, y_angle, x_shift, y_shift, z_shift);
              }
              else {
                reset = false;
              }

              x_last = x;
              y_last = y;
              m_dx = x - x_first;
              if(Math.abs(dx) <= 2) {
                x_first = x;
              }
              m_dy = y - y_first;
              if(Math.abs(dy) <= 2) {
                y_first = y;
              }
            }
            else if (id == MouseEvent.MOUSE_PRESSED) {
              mouseDown = true;
              x_last = ((MouseEvent)event[i]).getX();
              y_last = ((MouseEvent)event[i]).getY();
              x_first = x;
              y_first = y;
              m_dx = 0;
              m_dy = 0;
              m_thread = new Thread(this);
              m_thread.start();
            } else if (id == MouseEvent.MOUSE_RELEASED) {
              //System.out.println("MOUSE_RELEASED");
              mouseDown = false;
            }
          }
        }
      }
    }
    wakeupOn (mouseCriterion);
  }

  public void run() {
    while(mouseDown) {
      try {
        Thread.sleep(100);
        //x_angle = m_dy * x_rot_factor;
        //y_angle = m_dx * y_rot_factor;
        move(x_angle, y_angle, x_shift, y_shift, z_shift);
      } catch(Exception e) {
        System.out.println("Exception in run(): " + e + "\n");
      }
    }
  }
  
  private void move(double x_angle, double y_angle, double dx, double dy, double dz)
  {
    transformX.rotX(x_angle);
    transformY.rotY(y_angle);

    m_rotator.getTransform(m_currXformR);
    m_currXformR.mul(transformX, m_currXformR);
    m_currXformR.mul(transformY, m_currXformR);
    m_rotator.setTransform(m_currXformR);
    
    Vector3d dv = new Vector3d(dx, dy, dz);
    m_translator.getTransform(m_currXformT);
    Vector3d tmp = new Vector3d();
    m_currXformT.get(tmp);
    tmp.add(dv);
    m_currXformT.set(tmp);
    m_translator.setTransform(m_currXformT);
  }
}
