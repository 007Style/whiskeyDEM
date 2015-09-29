/*
 * LoaderDEM.java
 */
 
import javax.vecmath.*;
import java.io.*;


public class LoaderDEM
{
  public static final int RAKE_ONE = 6; // how to rake original data into apoint3f
  public static final int RAKE_TWO = 2; // how to rake apoint3f even more to make bpoint3f
  protected FileReader rdr = null;

  public LoaderDEM()
  {
  }
  
  /**
   * bullet-proof parseInt - at worst returns 0
   */
  public static int parseInt(String sint)
  {
	int ret = 0;
	try {			// try the easy way first
		ret = Integer.parseInt(sint);
	} catch (Exception e) {
		byte[] bytes = sint.getBytes();
		byte[] bbb = new byte[bytes.length + 1];
		int j = 0;
		bbb[j++] = (byte)'0';
		String digits = "0123456789";
		for(int i=0; i < bytes.length ;i++) {
			char chr = (char)bytes[i];
			if(digits.indexOf(chr) != -1) {
				bbb[j++] = (byte)chr;
			}
		}
		String sret = new String(bbb, 0, 0, j);
		try {
			ret = Integer.parseInt(sret);
		} catch (Exception ee) {
			System.out.println("Can't convert to int: " + sint);
			ret = 0;
		}
	}
	return ret;
  }

  /**
   * DemData is filled the following way:
   * Point3f a[]   - receiver array for full DEM
   * Point3f b[]   - receiver for "fast" (or "raked") DEM, containing much less points
   */
  public DemData loadDEM(String demName) throws java.io.FileNotFoundException
  {
    boolean debug = true;

    rdr = new FileReader(demName);
    StreamTokenizer readerTokenizer = new StreamTokenizer(rdr);

    DEMHeaderData dEMHeaderData = readHeader(readerTokenizer, debug);
    int i1 = -2;
    int j1 = dEMHeaderData.col;

    int b1 = RAKE_ONE;  // how to rake original data into apoint3f
    int b2 = RAKE_ONE;
    
    int bb1 = RAKE_TWO;  // how to rake apoint3f even more to make bpoint3f
    int bb2 = RAKE_TWO;
    
    System.err.println("FYI: read DEM header: col=" + dEMHeaderData.col);
    
    int aan[][] = new int[j1][];
    int k1;
    for (k1 = 0; k1 < j1; k1++)
    {
      if (debug) {
        System.err.print(new StringBuffer(String.valueOf(k1)).append(" ").toString());
        if(k1 % 20 == 0) {
          System.err.println("");
        }
      }
      if (k1 % b1 == 0) {
        aan[k1] = readRow(readerTokenizer, i1);
      } else {
        // just read the row without filling the array:
        readRow(readerTokenizer, 0);
        aan[k1] = null;
      }
    }
    
    Point3f apoint3f[] = fillPointArray(aan, j1, b1, b2, debug);

    if (debug) {
      System.err.println("\nFinished first array...");
    }
    
    b1 *= bb1;
    b2 *= bb2;
    
    // free some of the source array:
    for (k1 = 0; k1 < j1; k1++) {
      if (k1 % b1 != 0) {
        aan[k1] = null;
      }
    }

    // now rake the second array using different b1, b2 into array bpoint3f:
    Point3f bpoint3f[] = fillPointArray(aan, j1, b1, b2, debug);

    if (debug) {
      System.err.println("\nFinished second array...");
    }

    // free the array:
    for (k1 = 0; k1 < j1; k1++) {
      aan[k1] = null;
    }
    DemData demData = new DemData();
    demData.a = apoint3f;
    demData.b = bpoint3f;
    return demData;
  }

  private Point3f[] fillPointArray(int[][] aan, int j1, int b1, int b2, boolean debug)
  {
    int i1 = aan[0].length;
    int pLen = (i1 - 1) / b1 * ((j1 - 1) / b2) * 6;
    if (debug) {
      System.err.println("fillPointArray() i1=" + i1 + " b1=" + b1 + " b2=" + b2 + "   allocating Point3f[" + pLen + "]");
    }
    Point3f apoint3f[] = new Point3f[pLen];
    int i2 = 0;
    float f = aan[0][0];
    int j2;
    for (j2 = 0; j2 < (j1 - 1) / b2; j2++) {
      if (debug) {
        System.err.print(new StringBuffer(String.valueOf(j2)).append(":").toString());
        if(j2 % 40 == 0) {
          System.err.println("");
        }
      }
      for (int k2 = 0; k2 < (i1 - 1) / b1; k2++) {
        apoint3f[i2 + 5] = new Point3f((float)((k2 + 1) * b1), (float)((j2 + 1) * b2), (float)aan[(j2 + 1) * b2][(k2 + 1) * b1]);
        if ((k2 / b1 + j2 / b2) % 2 == 1) {
          if (k2 > 0 && j2 > 0) {
            apoint3f[i2] = apoint3f[i2 - ((i1 - 1) / b1 + 1) * 6 + 5];
            apoint3f[i2 + 1] = apoint3f[i2 - (i1 - 1) / b1 * 6 + 5];
            apoint3f[i2 + 2] = apoint3f[i2 - 6 + 5];
          } else if (k2 > 0) {
            apoint3f[i2] = new Point3f((float)(k2 * b1), (float)(j2 * b2), (float)aan[j2 * b2][k2 * b1]);
            apoint3f[i2 + 1] = new Point3f((float)((k2 + 1) * b1), (float)(j2 * b2), (float)aan[j2 * b2][(k2 + 1) * b1]);
            apoint3f[i2 + 2] = apoint3f[i2 - 6 + 5];
          } else if (j2 > 0) {
            apoint3f[i2] = new Point3f((float)(k2 * b1), (float)(j2 * b2), (float)aan[j2 * b2][k2 * b1]);
            apoint3f[i2 + 1] = apoint3f[i2 - (i1 - 1) / b1 * 6 + 5];
            apoint3f[i2 + 2] = new Point3f((float)(k2 * b1), (float)((j2 + 1) * b2), (float)aan[(j2 + 1) * b2][k2 * b1]);
          } else {
            apoint3f[i2] = new Point3f((float)(k2 * b1), (float)(j2 * b2), (float)aan[j2 * b2][k2 * b1]);
            apoint3f[i2 + 1] = new Point3f((float)((k2 + 1) * b1), (float)(j2 * b2), (float)aan[j2 * b2][(k2 + 1) * b1]);
            apoint3f[i2 + 2] = new Point3f((float)(k2 * b1), (float)((j2 + 1) * b2), (float)aan[(j2 + 1) * b2][k2 * b1]);
          }
          apoint3f[i2 + 3] = apoint3f[i2 + 2];
          apoint3f[i2 + 4] = apoint3f[i2 + 1];
          f = min(f, apoint3f[i2].z, apoint3f[i2 + 1].z, apoint3f[i2 + 2].z, apoint3f[i2 + 5].z);
        } else {
          if (k2 > 0 && j2 > 0) {
            apoint3f[i2] = apoint3f[i2 - ((i1 - 1) / b1 + 1) * 6 + 5];
            apoint3f[i2 + 1] = apoint3f[i2 - (i1 - 1) / b1 * 6 + 5];
            apoint3f[i2 + 3] = apoint3f[i2 - 6 + 5];
          } else if (k2 > 0) {
            apoint3f[i2] = new Point3f((float)(k2 * b1), (float)(j2 * b2), (float)aan[j2 * b2][k2 * b1]);
            apoint3f[i2 + 1] = new Point3f((float)((k2 + 1) * b1), (float)(j2 * b2), (float)aan[j2 * b2][(k2 + 1) * b1]);
            apoint3f[i2 + 3] = apoint3f[i2 - 6 + 5];
          } else if (j2 > 0) {
            apoint3f[i2] = new Point3f((float)(k2 * b1), (float)(j2 * b2), (float)aan[j2 * b2][k2 * b1]);
            apoint3f[i2 + 1] = apoint3f[i2 - (i1 - 1) / b1 * 6 + 5];
            apoint3f[i2 + 3] = new Point3f((float)(k2 * b1), (float)((j2 + 1) * b2), (float)aan[(j2 + 1) * b2][k2 * b1]);
          } else {
            apoint3f[i2] = new Point3f((float)(k2 * b1), (float)(j2 * b2), (float)aan[j2 * b2][k2 * b1]);
            apoint3f[i2 + 1] = new Point3f((float)((k2 + 1) * b1), (float)(j2 * b2), (float)aan[j2 * b2][(k2 + 1) * b1]);
            apoint3f[i2 + 3] = new Point3f((float)(k2 * b1), (float)((j2 + 1) * b2), (float)aan[(j2 + 1) * b2][k2 * b1]);
          }
          apoint3f[i2 + 4] = apoint3f[i2];
          apoint3f[i2 + 2] = apoint3f[i2 + 5];
          f = min(f, apoint3f[i2].z, apoint3f[i2 + 1].z, apoint3f[i2 + 3].z, apoint3f[i2 + 5].z);
        }
        i2 += 6;
      }
    }
    return apoint3f;
  }

  private float min(float f1, float f2)
  {
    return (f2 < f1) ? f2 : f1;
  }

  private float min(float f1, float f2, float f3, float f4, float f5)
  {
    return min(min(min(f1, f2), min(f3, f4)), f5);
  }

  private String readBytes(StreamTokenizer readerTokenizer, int i)
  {
    String string = new String();
    boolean flag = false;
    for (int j = 0; j < i; j++) {
      char ch;
      try {
          ch = (char)rdr.read();
      } catch(IOException e) {
          return string;
      }
      if (ch == 10)
      return string;
      if (ch != 32) {
        if (!flag)
        flag = true;
        string = new StringBuffer(String.valueOf(string)).append(ch).toString();
      }
    }
    if (!flag) {
        return null;
    } else {
        return string;
    }
  }

  private DEMHeaderData readHeader(StreamTokenizer readerTokenizer, boolean flag)
  {
    DEMHeaderData dEMHeaderData = new DEMHeaderData(this);
    readBytes(readerTokenizer, 858);
    dEMHeaderData.col = parseInt(readBytes(readerTokenizer, 6));
    readBytes(readerTokenizer, 160);
    return dEMHeaderData;
  }

  public int[] readRow(StreamTokenizer readerTokenizer, int i1)
  {
    try {
        readerTokenizer.nextToken();
    } catch (IOException e) {
        System.err.println("Error: " + e);
        e.printStackTrace();
    }
    readBytes(readerTokenizer, 6);
    int j1 = parseInt(readBytes(readerTokenizer, 6));
    int k1 = parseInt(readBytes(readerTokenizer, 6));
    readBytes(readerTokenizer, 119);
    // System.err.println("IP: readRow(..., " + i1 + ")  j1=" + j1 + " k1=" + k1);
    int i2 = (i1 < 0) ? j1 : i1;
    int an[] = new int[i2];
    for (int j2 = 0; j2 < j1; j2++) {
      String string = readBytes(readerTokenizer, 6);
      if (string == null) {
        string = readBytes(readerTokenizer, 4);
      }
      if (j2 < i2) {
        int k2;
        if (string != null) {
          k2 = parseInt(string) - 140;
        } else {
          k2 = an[(j2 - 1 < 0) ? 100 : (j2 - 1)];
        }
        an[j2] = k2;
      }
    }
    return an;
  }
}

class DEMHeaderData
{
  int col;

  public DEMHeaderData(LoaderDEM dEM)
  {
  }
}

