/*
 * Plane.java
 */

import java.net.URL;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.loaders.*;
import com.sun.j3d.loaders.vrml97.VrmlLoader;
import com.sun.j3d.loaders.vrml97.VrmlScene;
import vrml.BaseNode;
import com.sun.j3d.loaders.vrml97.node.Viewpoint;
import ncsa.j3d.loaders.*;


public class Plane extends BranchGroupRT
{
  /** Creates new Plane */
  public Plane(String fileName, double scale, double turnX, double turnY, double turnZ)
  {
    resetPosition();

    /*
    double dfSize = 0.1;
    ColorCube cube = new ColorCube(dfSize);
    Transform3D tScale = new Transform3D();
    //scale.rotX(Math.PI/2);
    scale.setScale(new Vector3d(1.0, 0.3, 0.05));
    TransformGroup scaleTG = new TransformGroup(tScale);
    scaleTG.addChild(cube);
    getAttachPoint().addChild(scaleTG);
     */
    try {
      BranchGroup pbg = null;
      if(fileName.toLowerCase().endsWith(".wrl")) {
          pbg = loadPlaneVRML(fileName);
      } else if(fileName.toLowerCase().endsWith(".dxf")) {
          pbg = loadPlaneDXF(fileName);
      }
      Transform3D tScale = new Transform3D();
      tScale.rotX(turnX);
      //tScale.rotY(turnY);
      //tScale.rotZ(turnZ);
      tScale.setScale(new Vector3d(scale, scale, scale));
      TransformGroup scaleTG = new TransformGroup(tScale);
      scaleTG.addChild(pbg);
      getAttachPoint().addChild(scaleTG);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      e.printStackTrace();
    }
  }

  public BranchGroup loadPlaneVRML(String fileName)
  {
    BranchGroup ret = null;

    try {
      VrmlScene scene = (VrmlScene)(new VrmlLoader()).load(new URL(fileName));

      System.out.println("FYI: VRML loaded " + fileName + "  scene=" + scene);
      ret = scene.getSceneGroup();

      // Clean the scene to prepare to compile it.  This will keep the
      // scene from being pickable or collidable, but we don't need
      // either of those for this app.
      scene.cleanForCompile(ret);
      ret.compile();
    } catch (java.io.IOException e) {
      System.err.println("Error: IO exception reading URL");
    } catch (vrml.InvalidVRMLSyntaxException e) {
      System.err.println("Error: VRML parse error");
      e.printStackTrace(System.err);
    }
    return ret;
  }

  public BranchGroup loadPlaneDXF(String fileName)
  {
    BranchGroup ret = null;

    try {
      // DXF format requires ncsa loader:
      ModelLoader loader = new ModelLoader();
      Scene scene = loader.load(fileName);
      System.out.println("FYI: DXF loaded " + fileName + "  scene=" + scene);
      ret = scene.getSceneGroup();

      ret.compile();
    } catch (java.io.IOException e) {
      System.err.println("Error: IO exception reading URL");
    } catch (vrml.InvalidVRMLSyntaxException e) {
      System.err.println("Error: VRML parse error");
      e.printStackTrace(System.err);
    }
    return ret;
  }

  public void resetPosition()
  {
    //moveAbsolute(new Point3d(-1.0d, -1.0d, 1.5d));
    moveAbsolute(new Point3d(800d, 800d, 6000.0d));
    rotReset();
    rotX(-Math.PI / 2);   // initial position - horizontal
    //lookAt(new Point3d(0,0,0), false);
  }
}
