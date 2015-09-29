/*
 * myMouseBehavior.java
 */
 
import javax.media.j3d.*;
import com.sun.j3d.utils.behaviors.mouse.*;


public class myMouseBehavior extends Object 
{
  FlightBehavior behaviorView = null;

  /** Creates new myMouseBehavior */
  public myMouseBehavior(BranchGroupRT joint, BoundingSphere bounds)
  {
    //TransformGroup rotator = joint.getRotation();
    //TransformGroup translator = joint.getPosition();

    behaviorView = new FlightBehavior();
    behaviorView.setSchedulingBounds(bounds);
    behaviorView.setJoint(joint);

    /*
    // Create the rotate behavior node
    MouseRotateX behaviorX = new MouseRotateX(); //MouseBehavior.INVERT_INPUT);
    behaviorX.setTransformGroup(rotator);
    behaviorX.setSchedulingBounds(bounds);
    rotator.addChild(behaviorX);

    MouseRotateY behaviorY = new MouseRotateY();
    behaviorY.setTransformGroup(rotator);
    behaviorY.setSchedulingBounds(bounds);
    rotator.addChild(behaviorY);
    
    // Create the zoom behavior node
    MouseZoom behavior2 = new MouseZoom(MouseBehavior.INVERT_INPUT);
    behavior2.setTransformGroup(translator);
    behavior2.setSchedulingBounds(bounds);
    translator.addChild(behavior2);

    // Create the translate behavior node
    MouseTranslate behavior3 = new MouseTranslate(MouseBehavior.INVERT_INPUT);
    behavior3.setTransformGroup(translator);
    behavior3.setSchedulingBounds(bounds);
    translator.addChild(behavior3);
    */
    
    /*
    Alpha alpha = new Alpha(-1, 5000);
    RotationInterpolator ri = new RotationInterpolator(alpha, objTrans);
    ri.setSchedulingBounds(bounds);
    objRoot.addChild(ri);
    */
  }
  
  public void setDem(Dem dem)
  {
    behaviorView.setDem(dem);
  }
  
}