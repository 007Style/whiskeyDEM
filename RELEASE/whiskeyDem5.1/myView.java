/*
 * myView.java
 */
 
import javax.media.j3d.*;
import javax.vecmath.*;


public class myView extends BranchGroupRT implements PositionListener
{

  /** Creates new myView */
  public myView(Canvas3D canvas3d, Locale locale)
  {
    resetPosition();
    
    View view = new View();
    
    view.setPhysicalEnvironment(new PhysicalEnvironment());
    view.setPhysicalBody(new PhysicalBody());
    view.addCanvas3D(canvas3d);
    ViewPlatform platform = new ViewPlatform();
    view.attachViewPlatform(platform);

    //view.setFrontClipPolicy(View.VIRTUAL_EYE);
    view.setFrontClipDistance(3.0);
    //view.setBackClipPolicy(View.VIRTUAL_EYE);
    view.setBackClipDistance(3000000.0);
    //view.setScreenScalePolicy(View.SCALE_EXPLICIT);
    //view.setScreenScale(0.4d);
    //view.setWindowResizePolicy(View.VIRTUAL_WORLD);
    //view.setWindowResizePolicy(View.PHYSICAL_WORLD);
    //view.setWindowMovementPolicy(View.VIRTUAL_WORLD);
    view.setFieldOfView(1.5d);
    
    getAttachPoint().addChild(platform);

    //Behavior bhv = new OrbitBehavior(tg);
    //Bounds bounds = new BoundingSphere(new Point3d(), 10000);
    //bhv.setSchedulingBounds(bounds);
    //bg.addChild(bhv);
    //setBounds(bounds);
  }
  
  public void resetPosition()
  {
    moveAbsolute(new Point3d(-1000.0d, -1000.0d, 60000.0d));
    //moveAbsolute(new Point3d(-100.0d, -100.0d, 150.0d));
    //moveRelative(new Vector3d(-2.0d,-2.0d,2.0d));
    //moveAbsolute(new Point3d(0d,0d,3.0d));

    //rotReset();
    lookAt(new Point3d(25000, 25000, 0), true);
    /*
    rotX(0.68);    // increase this to look "steeper" into the xy plane
    rotY(-0.57);   // adjusts horizontal pozitioning of zero point
    rotZ(-0.585);  // increase this to look "steeper" into the xy plane (decrease absolute)
    */
  }
 
  public void positionChanged(PositionEvent posEvent)
  {
    Point3d pos = posEvent.getNewPos();
    double distanceTraveled = posEvent.getDistanceTraveled();
    if(!pos.equals(getPos())) {
      lookAt(pos, true);
      Vector3d viewDirection = new Vector3d(pos);
      viewDirection.sub(new Vector3d(getPos()));
      double distance = viewDirection.length();
      if(distance > 40) {
        viewDirection.scale(((distance-40) / distance) * 0.7);
        moveRelative(viewDirection);
      }
    }
  }
}
