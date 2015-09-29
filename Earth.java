/*
 * Earth.java
 */

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.compression.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;

 
public class Earth extends BranchGroupRT {
private static final String texturesDirName = "FlightObjects/Textures";

  /** Creates new Earth */
  public Earth(TransformGroup objTrans, BoundingSphere bounds, java.awt.Component observer)
  {
    Appearance ap= new Appearance();
    Material mm = new Material();
    mm.setLightingEnable(true);
    ap.setMaterial(mm);

    TextureAttributes texAttr = new TextureAttributes();
    texAttr.setTextureMode(TextureAttributes.MODULATE);
    TextureLoader earthTex = new TextureLoader(new String("./" + texturesDirName + "/clouds.gif"), new String("RGB"), observer);
    if (earthTex != null) ap.setTexture(earthTex.getTexture());
    ap.setTextureAttributes(texAttr);

    // number of divisions - 5 is ugly, 15 OK, 25 pretty good):
    Sphere globe = new Sphere(.5f,Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, 25, ap);

    getAttachPoint().addChild(globe);
 
    Alpha alpha = new Alpha(-1, 500000);
    RotationInterpolator ri = new RotationInterpolator(alpha, getRotation());
    //PositionPathInterpolator ri = new PositionPathInterpolator(alpha, rotation);
    ri.setSchedulingBounds(bounds);
    getRotation().addChild(ri);
  }
  
}