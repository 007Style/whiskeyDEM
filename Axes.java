/*
 * Axes.java
 */
 
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Cylinder;


public class Axes extends BranchGroup {
  /** Creates new Axes */
  public Axes() {
    final float diameter = 500f; //0.1f; //0.005f;
    final float length = (float)Dem.DEM_WIDTH_METERS;  // 20f // all sizes in meters
    final float lengthZ = 1000f;  // 20f // all sizes in meters
    Matrix3d mtr = new Matrix3d();

    Appearance apX= new Appearance();
    Material mX = new Material();
    mX.setLightingEnable(true);
    mX.setDiffuseColor(1.0f, 0.0f, 0.0f);
    apX.setMaterial(mX);

    TransformGroup trans_x = new TransformGroup();
    Transform3D mat_x = new Transform3D();
    mat_x.rotZ(Math.PI/2);
    mat_x.setTranslation(new Vector3d(length/2, 0, 0));
    trans_x.setTransform(mat_x);
    Cylinder a_x = new Cylinder(diameter, length, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apX);
    trans_x.addChild(a_x);
    addChild(trans_x);
 
    Appearance apY= new Appearance();
    Material mY = new Material();
    mY.setLightingEnable(true);
    mY.setDiffuseColor(0.0f, 1.0f, 0.0f);
    apY.setMaterial(mY);

    TransformGroup trans_y = new TransformGroup();
    Transform3D mat_y = new Transform3D();
    mat_y.set(new Vector3d(0, length/2, 0));
    trans_y.setTransform(mat_y);
    Cylinder a_y = new Cylinder(diameter, length, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apY);
    trans_y.addChild(a_y);
    addChild(trans_y);

    Appearance apZ= new Appearance();
    Material mZ = new Material();
    mZ.setLightingEnable(true);
    mZ.setDiffuseColor(0.0f, 0.0f, 0.0f);
    apZ.setMaterial(mZ);

    TransformGroup trans_z = new TransformGroup();
    Transform3D mat_z = new Transform3D();
    mat_z.rotX(Math.PI/2);
    mat_z.setTranslation(new Vector3d(0, 0, lengthZ/2));
    trans_z.setTransform(mat_z);
    Cylinder a_z = new Cylinder(diameter, lengthZ+80000, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apZ);
    trans_z.addChild(a_z);
    addChild(trans_z);
    
    
    Appearance apX2= new Appearance();
    Material mX2 = new Material();
    mX2.setLightingEnable(true);
    mX2.setDiffuseColor(1.0f, 0.5f, 0.0f);
    apX2.setMaterial(mX2);

    TransformGroup trans_x2 = new TransformGroup();
    Transform3D mat_x2 = new Transform3D();
    mat_x2.rotZ(Math.PI/2);
    mat_x2.setTranslation(new Vector3d(length/2, length, 0));
    trans_x2.setTransform(mat_x2);
    Cylinder a_x2 = new Cylinder(diameter, length, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apX2);
    trans_x2.addChild(a_x2);
    addChild(trans_x2);
    
    Appearance apY2= new Appearance();
    Material mY2 = new Material();
    mY2.setLightingEnable(true);
    mY2.setDiffuseColor(0.5f, 1.0f, 0.0f);
    apY2.setMaterial(mY2);

    TransformGroup trans_y2 = new TransformGroup();
    Transform3D mat_y2 = new Transform3D();
    mat_y2.set(new Vector3d(length, length/2, 0));
    trans_y2.setTransform(mat_y2);
    Cylinder a_y2 = new Cylinder(diameter, length, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apY2);
    trans_y2.addChild(a_y2);
    addChild(trans_y2);
    
    Appearance apZ1= new Appearance();
    Material mZ1 = new Material();
    mZ1.setLightingEnable(true);
    mZ1.setDiffuseColor(0.0f, 0.0f, 1.0f);
    apZ1.setMaterial(mZ1);

    TransformGroup trans_z1 = new TransformGroup();
    Transform3D mat_z1 = new Transform3D();
    mat_z1.rotX(Math.PI/2);
    mat_z1.setTranslation(new Vector3d(length, 0, lengthZ/2));
    trans_z1.setTransform(mat_z1);
    Cylinder a_z1 = new Cylinder(diameter, lengthZ+80000, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apZ1);
    trans_z1.addChild(a_z1);
    addChild(trans_z1);
    
    Appearance apZ2= new Appearance();
    Material mZ2 = new Material();
    mZ2.setLightingEnable(true);
    mZ2.setDiffuseColor(1.0f, 0.0f, 1.0f);
    apZ2.setMaterial(mZ2);

    TransformGroup trans_z2 = new TransformGroup();
    Transform3D mat_z2 = new Transform3D();
    mat_z2.rotX(Math.PI/2);
    mat_z2.setTranslation(new Vector3d(0, length, lengthZ/2));
    trans_z2.setTransform(mat_z2);
    Cylinder a_z2 = new Cylinder(diameter, lengthZ+80000, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apZ2);
    trans_z2.addChild(a_z2);
    addChild(trans_z2);
    
    Appearance apZ3= new Appearance();
    Material mZ3 = new Material();
    mZ3.setLightingEnable(true);
    mZ3.setDiffuseColor(1.0f, 1.0f, 1.0f);
    apZ3.setMaterial(mZ3);

    TransformGroup trans_z3 = new TransformGroup();
    Transform3D mat_z3 = new Transform3D();
    mat_z3.rotX(Math.PI/2);
    mat_z3.setTranslation(new Vector3d(length, length, lengthZ/2));
    trans_z3.setTransform(mat_z3);
    Cylinder a_z3 = new Cylinder(diameter, lengthZ+80000, Cylinder.GENERATE_NORMALS | Cylinder.GENERATE_TEXTURE_COORDS, apZ3);
    trans_z3.addChild(a_z3);
    addChild(trans_z3);
  }
  
}