/*
 *       @(#)MouseRotateX.java 1.1 98/07/17 16:00:54
 *
 * Copyright (c) 1996-1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.mouse.*;

/**
 * MouseRotateX is a Java3D behavior object that lets users control the
 * rotation of an object via a mouse.
 * <p>
 * To use this utility, first create a transform group that this
 * rotate behavior will operate on. Then,
 *<blockquote><pre>
 *
 *   MouseRotateX behavior = new MouseRotateX();
 *   behavior.setTransformGroup(objTrans);
 *   objTrans.addChild(behavior);
 *   behavior.setSchedulingBounds(bounds);
 *
 *</pre></blockquote>
 * The above code will add the rotate behavior to the transform
 * group. The user can rotate any object attached to the objTrans.
 */

public class MouseRotateX extends MouseBehavior implements Runnable
{
  double  x_angle;
  double  x_factor;
  private Thread m_thread;
  private boolean mouseDown = false;
  private int m_dy = 0;
  private int y_first = -1;  // where mouse started or stopped temporarily, before dragging

  /**
   * Creates a rotate behavior given the transform group.
   * @param transformGroup The transformGroup to operate on.
   */
  public MouseRotateX(TransformGroup transformGroup) {
    super(transformGroup);
  }

  /**
   * Creates a default mouse rotate behavior.
   **/
  public MouseRotateX() {
    super(0);
  }

  /**
   * Creates a rotate behavior.
   * Note that this behavior still needs a transform
   * group to work on (use setTransformGroup(tg)) and
   * the transform group must add this behavior.
   * @param flags interesting flags (wakeup conditions).
   */
  public MouseRotateX(int flags) {
    super(flags);
  }

  public void initialize() {
    super.initialize();
    x_angle = 0;
    x_factor = .001;
    if ((flags & INVERT_INPUT) == INVERT_INPUT) {
      invert = true;
      x_factor *= -1;
    }
  }

  public double getYFactor() {
    return x_factor;
  }

  public void setFactor( double factor) {
    x_factor = factor;
  }

  public void processStimulus (Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] event;
    int id;
    int dy;

    while (criteria.hasMoreElements()) {
      wakeup = (WakeupCriterion) criteria.nextElement();
      if (wakeup instanceof WakeupOnAWTEvent) {
        event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
        for (int i=0; i<event.length; i++) {
          processMouseEvent((MouseEvent) event[i]);

          if (((flags & MANUAL_WAKEUP) == 0) ||
                          ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){

            id = event[i].getID();
            if ((id == MouseEvent.MOUSE_DRAGGED) &&
                !((MouseEvent)event[i]).isMetaDown() &&
                !((MouseEvent)event[i]).isAltDown()){

              y = ((MouseEvent)event[i]).getY();

              dy = y - y_last;

              if (!reset){
                x_angle = dy * x_factor;
                transformX.rotX(x_angle);
                transformGroup.getTransform(currXform);
                currXform.mul(transformX, currXform);
                transformGroup.setTransform(currXform);
              } else {
                reset = false;
              }

              y_last = y;
              m_dy = y - y_first;
              if(Math.abs(dy) <= 2) {
                y_first = y;
              }
            } else if (id == MouseEvent.MOUSE_PRESSED) {
              //System.out.println("MOUSE_PRESSED");
              mouseDown = true;
              y_last = ((MouseEvent)event[i]).getY();
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
  
  public void run()
  {
    while(mouseDown) {
      try {
        Thread.sleep(100);
        x_angle = m_dy * x_factor;

        transformX.rotX(x_angle);

        transformGroup.getTransform(currXform);
        currXform.mul(transformX, currXform);
        transformGroup.setTransform(currXform);
      } catch(Exception e) {
        System.out.println("Exception in run(): " + e + "\n");
      }
    }
  }
}
