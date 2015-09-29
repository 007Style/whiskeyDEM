/*
 * Dem.java
 */

import java.io.*;
import java.util.Enumeration;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;


//import ncsa.util.ReaderTokenizer;
public class Dem extends BranchGroupRT
{
  private Switch sw = new Switch();
  private float[][] heights = null;
  private int m_width = 0;
  public  static final double DEM_WIDTH_METERS = 111000.0;
  private boolean m_isAllowWireframe;

  public  double minX =  10000000.0;
  public  double maxX = -10000000.0;
  public  double minY =  10000000.0;
  public  double maxY = -10000000.0;
  public  double minZ =  10000000.0;
  public  double maxZ = -10000000.0;
  
  /** Creates new Dem */
  public Dem(BoundingSphere bounds, String demName, String shpName)
  {
    m_isAllowWireframe = ProgramConfig.isAllowWireframe();
    Shape3D shape = null;
    BranchGroup dem = null;
    BranchGroup demFast = null;
    File cache = new File(shpName);
    if(cache.exists()) {
      System.out.println("Loading DEM data from cache " + shpName + "\n");
      Shape3D shapeFast = new Shape3D();
      shapeFast.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape = new Shape3D();
      shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      readShapeArrays(shapeFast, shape, shpName);
      demFast = new BranchGroup();
      demFast.addChild(shapeFast);
      dem = new BranchGroup();
      dem.addChild(shape);
    } else {
      // no cache file found - load DEM file from original DEM format:
      System.out.println("Loading DEM data from original " + demName + "\n");
      try {
        LoaderDEM ldr = new LoaderDEM();
        DemData demData = ldr.loadDEM(demName);
        GeometryInfo geometryInfo = new GeometryInfo(1);
        geometryInfo.setCoordinates(demData.a);
        demData.a = null;
        NormalGenerator normalGenerator = new NormalGenerator();
        normalGenerator.setCreaseAngle(Math.PI);
        normalGenerator.generateNormals(geometryInfo);
        dem = new BranchGroup();
        shape = new Shape3D(geometryInfo.getGeometryArray());
        conditionShape3D(shape);
        setAppearance(shape, false);
        dem.addChild(shape);

        geometryInfo.setCoordinates(demData.b);
        demData.b = null;
        normalGenerator.generateNormals(geometryInfo);
        demFast = new BranchGroup();
        Shape3D shapeFast = new Shape3D(geometryInfo.getGeometryArray());
        conditionShape3D(shapeFast);
        setAppearance(shapeFast, true);
        demFast.addChild(shapeFast);

        saveShapeArrays(shapeFast, shape, shpName);

      } catch (Exception e) {
        System.out.println("Exception: " + e);
        e.printStackTrace();
      }
    } 
    
    // should not happen, but in case DEM is not there, it can be replaced by cube:
    if(dem == null) {
      double dfSize = 600.0;
      dem = new BranchGroup();
      Transform3D mat = new Transform3D();
      TransformGroup trans = new TransformGroup(mat);
      mat.set(new Vector3d(dfSize, dfSize, dfSize));
      //mat.setRotation(new AxisAngle4d(1, 1, 1, 1));
      trans.setTransform(mat);
      trans.addChild(new ColorCube(dfSize));
      dem.addChild(trans);
    }

    // should not happen, but in case fast DEM is not there, it can be replaced by cube too:
    if(demFast == null) {
      double dfSize = 600.0;
      demFast = new BranchGroup();
      Transform3D mat = new Transform3D();
      TransformGroup trans = new TransformGroup(mat);
      mat.set(new Vector3d(dfSize, dfSize, dfSize));
      //mat.setRotation(new AxisAngle4d(1, 1, 1, 1));
      trans.setTransform(mat);
      trans.addChild(new ColorCube(dfSize));
      demFast.addChild(trans);
    }
    
    // get the height information prefilled, so that getHeight(double, double) can work.
    computeHeights(shape);

    sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
    sw.addChild(dem);
    sw.addChild(demFast);
    sw.setWhichChild(Switch.CHILD_ALL);
    
    Transform3D demMat = new Transform3D();
    TransformGroup demTrans = new TransformGroup(demMat);
    //demMat.set(new Vector3d(0, 0, 0));
    //demMat.setRotation(new AxisAngle4d(1, 1, 1, 1));
    double demScaleH = DEM_WIDTH_METERS / (maxX - minX) ;    // must be around 100.0
    double demScaleV = 1.0;
    demMat.setScale(new Vector3d(demScaleH, demScaleH, demScaleV));  // one grad = 110 km near equator
    demTrans.setTransform(demMat);
    demTrans.addChild(sw);
    getAttachPoint().addChild(demTrans);
  }

  public void setAllowWireframe(boolean b)
  {
      m_isAllowWireframe = b;
      if(!m_isAllowWireframe) {
        sw.setWhichChild(0);
      } else {
        sw.setWhichChild(1);
      }
  }
  
  public void renderFast(boolean doFast)
  {
    if(doFast && m_isAllowWireframe) {
      sw.setWhichChild(1);
    } else {
      sw.setWhichChild(0);
    }
  }
  
  /*
  public void printGroup(Group group)
  {
    Enumeration children = group.getAllChildren();

    for (; children.hasMoreElements() ;) {
      Object node = children.nextElement();
      System.out.println(node);
      if(node instanceof Group) {
        printGroup((Group)node);
      }
    }
  }

  public void conditionGroup(Group group)
  {
    Enumeration children = group.getAllChildren();

    // traverse to Shape3D and condition it:
    for (; children.hasMoreElements() ;) {
      Object node = children.nextElement();
      System.out.println(node);
      if(node instanceof Group) {
        conditionGroup((Group)node);
      } else if(node instanceof Shape3D) {
        Shape3D shape = (Shape3D)node;
        conditionShape3D(shape);
        //saveShapeCompressed(shape, shpName);   // causes java.lang.OutOfMemoryError
        saveShapeArray(shape, shpName);
      }
    }
  }

  
  / **
   * using standard compression causes java.lang.OutOfMemoryError in new CompressionStream(shapes) on a 320Mb laptop
   * /
  public void saveShapeCompressed(Shape3D shape, String name)
  {
    Shape3D[] shapes = { shape }; //new Shape3d[1];
    System.out.println("new CompressionStream()\n");
    CompressionStream cs = new CompressionStream(shapes);
    try {
      CompressedGeometryFile cgf = new CompressedGeometryFile(name, true);
      GeometryCompressor compressor = new GeometryCompressor();
      System.out.println("compress()\n");
      compressor.compress(cs, cgf);
    } catch (java.io.IOException e) {
      System.out.println("Exception while compressing:" + e + "\n");
    }
    System.out.println("compressing done\n");
  }
  */
  
  // get the height information prefilled, so that getHeight(double, double) can work.
  public void computeHeights(Shape3D shape)
  {
    if(shape == null) {
      return;
    }
    Geometry geometry = shape.getGeometry();
    if(geometry instanceof TriangleArray) {
      TriangleArray tra = (TriangleArray)geometry;
      int vCount = tra.getVertexCount();
      System.out.println("TriangleArray: " + vCount + " vertices\n");
      double[] coords = new double[vCount * 3];
      tra.getCoordinates(0, coords);
      m_width = (int)Math.sqrt((double)(vCount/6));
      System.out.println("m_width=" + m_width);
      heights = new float[m_width][m_width];
      minX = 10000000.0;
      maxX = -10000000.0;
      minY = 10000000.0;
      maxY = -10000000.0;
      minZ = 10000000.0;
      maxZ = -10000000.0;
      
      for(int i=0; i < m_width ;i++) {
        for(int j=0; j < m_width ;j++) {
          double x = (coords[i*j*18] + coords[i*j*18 + 3] + coords[i*j*18 + 6]
                      + coords[i*j*18 + 9] + coords[i*j*18 + 12] + coords[i*j*18 + 15]) / 6;
          double y = (coords[i*j*18 + 1] + coords[i*j*18 + 4] + coords[i*j*18 + 7]
                      + coords[i*j*18 + 10] + coords[i*j*18 + 13] + coords[i*j*18 + 16]) / 6;
          // we are interested in the most high point here, to avoid scratching belly :-))
          double z = Math.max(coords[i*j*18 + 2], Math.max(coords[i*j*18 + 5], Math.max(coords[i*j*18 + 8],
                              Math.max(coords[i*j*18 + 11], Math.max(coords[i*j*18 + 14], coords[i*j*18 + 17])))));
          heights[i][j] = (float)z;
          minX = x < minX ? x : minX;
          maxX = x > maxX ? x : maxX;
          minY = y < minY ? y : minY;
          maxY = y > maxY ? y : maxY;
          minZ = z < minZ ? z : minZ;
          maxZ = z > maxZ ? z : maxZ;
        }
      }
      coords = null;
      System.gc();
      System.out.println("minX=" + minX + "  maxX=" + maxX);
      System.out.println("minY=" + minY + "  maxY=" + maxY);
      System.out.println("minZ=" + minZ + "  maxZ=" + maxZ);
    } else {
      System.out.println("Error: cannot compute heights with geometry other than TriangleArray\n");
    }
  }
  
  public double getHeight(double x, double y)
  {
    double height = 0.0;
    //System.out.println("getHeight(" + x + "," + y + ")");
    if(heights != null) {
      int ix = (int)(x * m_width / DEM_WIDTH_METERS);
      int iy = (int)(y * m_width / DEM_WIDTH_METERS);
      //System.out.println("getHeight(" + ix + "," + iy + ")");
      if(ix >= 0 && ix < m_width && iy >= 0 && iy < m_width) {
        height = (double)heights[iy][ix];
      }
    }
    return height;
  }
  
  public void saveShapeArrays(Shape3D shapeFast, Shape3D shape, String name)
  {
    System.out.println("saveShapeArrays()\n");
    try {
      FileOutputStream fos = new FileOutputStream(name);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      saveArray(oos, shapeFast);
      saveArray(oos, shape);
      oos.close();
      System.out.println("OK: saved arrays\n");
    } catch (java.io.IOException e) {
      System.out.println("Exception while saving arrays:" + e + "\n");
    }
  }

  private void saveArray(ObjectOutputStream oos, Shape3D shape) throws java.io.IOException
  {
    Geometry geometry = shape.getGeometry();
    if(geometry instanceof TriangleArray) {
      TriangleArray tra = (TriangleArray)geometry;
      int vCount = tra.getVertexCount();
      System.out.println("TriangleArray: " + vCount + " vertices\n");
      float[] colors = new float[vCount * 3];
      tra.getColors(0, colors);
      double[] coords = new double[vCount * 3];
      tra.getCoordinates(0, coords);
      float[] normals = new float[vCount * 3];
      tra.getNormals(0, normals);
      oos.writeObject(new Integer(vCount));
      oos.writeObject(colors);
      oos.writeObject(coords);
      oos.writeObject(normals);
      colors = null;
      coords = null;
      normals = null;
      System.gc();
    } else {
      System.out.println("Error: cannot save shape with geometry other than TriangleArray\n");
    }
  }
    
  public void readShapeArrays(Shape3D shapeFast, Shape3D shape, String name)
  {
    System.out.println("readShapeArray()\n");
    try {
      FileInputStream fis = new FileInputStream(name);
      ObjectInputStream ois = new ObjectInputStream(fis);
      
      TriangleArray traFast = readArray(ois);
      TriangleArray tra = readArray(ois);
      
      ois.close();
      shapeFast.setGeometry(traFast);
      setAppearance(shapeFast, true);

      shape.setGeometry(tra);
      setAppearance(shape, false);

      System.out.println("OK: restored arrays\n");
    } catch (java.lang.ClassNotFoundException e) {
      System.out.println("ClassNotFoundException while reading arrays:" + e + "\n");
    } catch (java.io.IOException e) {
      System.out.println("IOException while reading arrays:" + e + "\n");
    }
  }

  private TriangleArray readArray(ObjectInputStream ois) throws java.lang.ClassNotFoundException, java.io.IOException
  {
    Integer itmp = (Integer)ois.readObject();
    int vCount = itmp.intValue();
    System.out.println("TriangleArray: " + vCount + " vertices\n");
    TriangleArray tra = new TriangleArray(vCount, TriangleArray.COLOR_3 | TriangleArray.COORDINATES | TriangleArray.NORMALS);
    float[] colors = (float[])ois.readObject();
    tra.setColors(0, colors);
    double[] coords = (double[])ois.readObject();
    tra.setCoordinates(0, coords);
    float[] normals = (float[])ois.readObject();
    tra.setNormals(0, normals);
    return tra;
  }

  protected void setAppearance(Shape3D shape, boolean asWireframe)
  {
    Appearance ap = new Appearance();
    Material m = new Material();
    m.setLightingEnable(true);
    ap.setMaterial(m);
    PolygonAttributes pa = new PolygonAttributes();
    if(asWireframe) {
      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
    }
    pa.setCullFace(PolygonAttributes.CULL_NONE);
    ap.setPolygonAttributes(pa);
    shape.setAppearance(ap);
  }

  public void conditionShape3D(Shape3D shape)
  {
    Geometry geometry = shape.getGeometry();

    System.out.println(geometry);
    if(geometry instanceof TriangleArray) {
      TriangleArray tra = (TriangleArray)geometry;
      int vCount = tra.getVertexCount();
      System.out.println("TriangleArray: " + vCount + " vertices\n");
      TriangleArray traNew = new TriangleArray(vCount, TriangleArray.COLOR_3 | TriangleArray.COORDINATES | TriangleArray.NORMALS);

      Point3d point = new Point3d();
      double hMin =  100000000.0d;
      double hMax = -100000000.0d;
      System.out.println("  coloring...\n");
      float[] colors = new float[vCount * 3];
      float[] color = new float[3];
      for(int i=0; i < vCount ;i++) {
        tra.getCoordinate(i, point);
        double[] pcoord = new double[3];
        point.get(pcoord);
        double h = pcoord[2];
        if(h > hMax) {
          hMax = h;
        }
        if(h < hMin) {
          hMin = h;
        }
        colorByHeight(h, color);
        System.arraycopy(color, 0, colors, i*3, 3);
        /*
        colors[i*3] = color[0];
        colors[i*3 + 1] = color[1];
        colors[i*3 + 2] = color[2];
         */
        //System.out.println("  i: " + i + "  " + point + "\n");
      }
      double range = hMax - hMin;
      System.out.println("  min=" + hMin + "  max=" + hMax + "  range=" + range + "\n");
      traNew.setColors(0, colors);
      double[] coords = new double[vCount * 3];
      tra.getCoordinates(0, coords);
      traNew.setCoordinates(0, coords);
      float[] normals = new float[vCount * 3];
      tra.getNormals(0, normals);
      traNew.setNormals(0, normals);
      shape.setGeometry(traNew);
    }
  }

  public void colorByHeight(double h, float[] color)
  {
  float[] MyWater     = { 0.0f, 0.5f, 1.0f };

  float[] MyGray      = { 0.7f, 0.7f, 0.7f };
  float[] MyGreen     = { 0.05f, 0.8f, 0f };
  float[] MyGreen1    = { 0.07f, 0.75f, 0.05f };
  float[] MyGreen2    = { 0.1f, 0.7f, 0.07f };
  float[] MyGreen3    = { 0.12f, 0.63f, 0.1f };
  float[] MyForestGreen = { 0.137255f, 0.556863f, 0.137255f };
  float[] MyDarkForestGreen = { 0.25f, 0.456863f, 0.1f };
  float[] MyKhaki     = { 0.623529f, 0.623529f, 0.372549f };
  float[] MyDarkKhaki = {0.4988f, 0.4988f, 0.2980f };           // MyKhaki*0.8
  float[] MyVeryDarkKhaki =   { 0.3741f, 0.3741f, 0.22353f };   // MyKhaki*0.6
  float[] MyBaseBrown = { 1.0f, 0.74f, 0.65f };
  float[] MyBrown75   = { 0.75f, 0.555f, 0.4875f };     // MyBaseBrown*0.75
  float[] MyBrown65   = { 0.65f, 0.487f, 0.4225f };     // MyBaseBrown*0.65
  float[] MyBrown55   = { 0.55f, 0.407f, 0.3575f };     // MyBaseBrown*0.55
  float[] MyBrown45   = { 0.45f, 0.333f, 0.2925f };     // MyBaseBrown*0.45
  float[] MyGray75    = { 0.75f, 0.75f, 0.75f };        // White*0.75
  float[] MyGray85    = { 0.85f, 0.85f, 0.85f };        // White*0.85
  float[] MyWhite     = { 1.0f, 1.0f, 1.0f };

    h /= 3000.0;

    if(h > 0.830) {
      System.arraycopy((Object)MyWhite, 0, (Object)color, 0, 3);
    } else if(h > 0.730) {
      System.arraycopy((Object)MyGray85, 0, (Object)color, 0, 3);
    } else if(h > 0.630) {
      System.arraycopy((Object)MyGray75, 0, (Object)color, 0, 3);
    } else if(h > 0.530) {
      System.arraycopy((Object)MyBrown75, 0, (Object)color, 0, 3);
    } else if(h > 0.430) {
      System.arraycopy((Object)MyBrown65, 0, (Object)color, 0, 3);
    } else if(h > 0.330) {
      System.arraycopy((Object)MyBrown55, 0, (Object)color, 0, 3);
    } else if(h > 0.230) {
      System.arraycopy((Object)MyBrown45, 0, (Object)color, 0, 3);
    } else if(h > 0.130) {
      System.arraycopy((Object)MyVeryDarkKhaki, 0, (Object)color, 0, 3);
    } else if(h > 0.080) {
      System.arraycopy((Object)MyDarkKhaki, 0, (Object)color, 0, 3);
    } else if(h > 0.060) {
      System.arraycopy((Object)MyKhaki, 0, (Object)color, 0, 3);
    } else if(h > 0.046) {
      System.arraycopy((Object)MyDarkForestGreen, 0, (Object)color, 0, 3);
    } else if(h > 0.030) {
      System.arraycopy((Object)MyForestGreen, 0, (Object)color, 0, 3);
    } else if(h > 0.020) {
      System.arraycopy((Object)MyGreen3, 0, (Object)color, 0, 3);
    } else if(h > 0.012) {
      System.arraycopy((Object)MyGreen2, 0, (Object)color, 0, 3);
    } else if(h > 0.007) {
      System.arraycopy((Object)MyGreen1, 0, (Object)color, 0, 3);
    } else if(h > 0) {
      System.arraycopy((Object)MyGreen, 0, (Object)color, 0, 3);
    } else {
      System.arraycopy((Object)MyWater, 0, (Object)color, 0, 3);
    }
  }

}