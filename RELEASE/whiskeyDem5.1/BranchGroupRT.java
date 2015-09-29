/*
 * BranchGroupRT.java
 */
 
import javax.media.j3d.*;
import javax.vecmath.*;


public class BranchGroupRT extends BranchGroup
{
  TransformGroup position = new TransformGroup();
  TransformGroup rotation = new TransformGroup();

  /** Creates new BranchGroupRT */
  public BranchGroupRT() {
    position.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    position.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    rotation.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    position.addChild(rotation);
    this.addChild(position);
  }
  
  public TransformGroup getPosition()
  {
    return position;
  }
  
  public TransformGroup getRotation()
  {
    return rotation;
  }
  
  /**
   * where to attach children
   */
  public TransformGroup getAttachPoint()
  {
    return rotation;
  }
  
  public void rotX(double angle)
  {
    Transform3D tmp = new Transform3D();
    rotation.getTransform(tmp);
    Transform3D rot = new Transform3D();
    rot.rotX(angle);
    tmp.mul(rot);
    rotation.setTransform(tmp);
  }
  
  public void rotY(double angle)
  {
    Transform3D tmp = new Transform3D();
    rotation.getTransform(tmp);
    Transform3D rot = new Transform3D();
    rot.rotY(angle);
    tmp.mul(rot);
    rotation.setTransform(tmp);
  }
  
  public void rotZ(double angle)
  {
    Transform3D tmp = new Transform3D();
    rotation.getTransform(tmp);
    Transform3D rot = new Transform3D();
    rot.rotZ(angle);
    tmp.mul(rot);
    rotation.setTransform(tmp);
  }
  
  public void rotReset()
  {
    Transform3D tmp = new Transform3D();
    rotation.setTransform(tmp);
  }
  
  public void moveAbsolute(Point3d where)
  {
    Transform3D tmp = new Transform3D();
    tmp.set(new Vector3d(where));
    position.setTransform(tmp);
  }
  
  public void moveRelative(Vector3d shift)
  {
    Transform3D tmp = new Transform3D();
    position.getTransform(tmp);
    Vector3d tr = new Vector3d();
    tmp.get(tr);
    tr.add(shift);
    tmp.setTranslation(tr);
    position.setTransform(tmp);
  }
  
  public Point3d getPos()
  {
    // get the joint position:
    Transform3D tmp = new Transform3D();
    position.getTransform(tmp);
    Vector3d vv = new Vector3d();
    tmp.get(vv);
    Point3d pos = new Point3d(vv.x, vv.y, vv.z);
    return pos;
  }

  public void lookAt(Point3d pos, boolean flip)
  {
    // get the joint position:
    Point3d jointPos = getPos();

    //System.out.print("looking at: pos=" + pos);
    //System.out.println("  from: jointPos=" + jointPos);

    Transform3D ttmp = new Transform3D();
    Vector3d up;
    if(flip) {    // may be we can do something with the matrix instead
      up = new Vector3d(0.0, 0.0, 1.0);
      ttmp.lookAt(jointPos, pos, up);
    } else {
      up = new Vector3d(0.0, 0.0, -1.0);
      ttmp.lookAt(pos, jointPos, up);
    }
    Matrix3d mtmp = new Matrix3d();   // to hold rotational component
    ttmp.get(mtmp);
    mtmp.invert();    // some kind of 90 grad rotation that puts things in place...
    mtmp.normalize();

    Transform3D xFormR = new Transform3D();
    rotation.getTransform(xFormR);
    xFormR.setRotation(mtmp);
    rotation.setTransform(xFormR);
  }
}