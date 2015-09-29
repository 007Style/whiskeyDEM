/*
 * PlaneFactory.java
 */
import java.io.*; 


class ModelsFilenameFilter implements FilenameFilter
{
      public boolean accept(java.io.File file, java.lang.String str) {
          if(str.toLowerCase().endsWith(".wrl")) {
              return true;
          }
          return false;
      }
}

public class PlaneFactory
{
    private static String m_planeNames[] = null;

    private static String m_planeName = null;
    
    private static String m_sModelsDir = null;

    private static String fileNameB1 = "b1.wrl";
    private static final double SCALE_B1 = 1.5;
    private static final double TURNX_B1 = Math.PI / 2;

    private static String fileNameF14 = "f14.wrl";
    private static final double SCALE_F14 = 5.045;
    private static final double TURNX_F14 = Math.PI/2;

    private static String fileNameF16 = "f16.wrl";
    private static final double SCALE_F16 = 1.0;
    private static final double TURNX_F16 = Math.PI/2;

    private static String fileNameF18 = "f18.wrl";
    private static final double SCALE_F18 = 50.0; //0.000005;
    private static final double TURNX_F18 = Math.PI/2;

    private static String fileNameF15Agg = "f15dj_agg4.wrl";
    private static final double SCALE_F15Agg = 2.0;
    private static final double TURNX_F15Agg = Math.PI;
    
    private static String fileNameA10 = "a10.wrl";
    private static final double SCALE_A10 = 2.0;
    private static final double TURNX_A10 = Math.PI/2;

    private static String fileNameMirage = "mirageA.wrl";
    private static final double SCALE_Mirage = 1.0;
    private static final double TURNX_Mirage = Math.PI/2;

    private static String fileNameSU27 = "su27.wrl";
    private static final double SCALE_SU27 = 1.0;
    private static final double TURNX_SU27 = Math.PI/2;
    

    /** Configures PlaneFactory */
    public PlaneFactory(String modelsDirName)
    {
        File modelsDir = new File(modelsDirName);
        try {
          m_sModelsDir = modelsDir.getCanonicalPath();
          System.out.println("Models path=" + m_sModelsDir);
          if(!modelsDir.exists()) {
              System.err.println("Error: Models folder not found: " + m_sModelsDir);
              System.exit(1);
          }
          m_sModelsDir = modelsDir.getCanonicalPath();
          System.out.println("Models path=" + m_sModelsDir);
          if(!modelsDir.exists()) {
              System.err.println("Error: Models folder not found: " + m_sModelsDir);
              System.exit(1);
          }
          FilenameFilter filter = new ModelsFilenameFilter();
          String modelsFiles[] = modelsDir.list(filter);
          int modelsFilesCount = modelsFiles.length;
          if(modelsFilesCount == 0) {
              System.err.println("Error: no Model files in folder: " + m_sModelsDir);
              System.exit(1);
          }
          m_planeNames = new String[modelsFilesCount];
          for(int i=0; i < modelsFilesCount ;i++) {
              String name = modelsFiles[i];
              System.out.print("    " + name);
              m_planeNames[i] = name.substring(0, name.length() - 4);
              System.out.println(" --->  " + m_planeNames[i]);
          }
        } catch(Exception e) {
          e.printStackTrace();
          System.exit(1);
        }
    }
    
    public static String[] getPlaneNames()
    {
        return m_planeNames;
    }

    public static String getPlaneName()
    {
      return m_planeName;
    }
    
    public static int planeHangar(String thePlane)
    {
    	int plane = 0;
    	if(thePlane.equals("a10.wrl")) plane = 5;
    	else if(thePlane.equals("b1.wrl")) plane = 1;
    	else if(thePlane.equals("f15djagg4.wrl")) plane = 0;
    	else if(thePlane.equals("f14.wrl")) plane = 2;
    	else if(thePlane.equals("f16.wrl")) plane = 3;
    	else if(thePlane.equals("f18.wrl")) plane = 4;
    	else if(thePlane.equals("mirageA.wrl")) plane = 6;
    	else if(thePlane.equals("su27.wrl")) plane = 7;
    	else plane = 0;
    	return plane;
    }

    public static Plane produce(int type)
    {
        Plane plane = null;
        String sPlaneName = "none";
        switch (type) {
        default:
            sPlaneName = fileNameF15Agg;
            plane = new Plane("file:///" + m_sModelsDir + "/" + sPlaneName, SCALE_F15Agg, TURNX_F15Agg, 0.0d, 0.0d);
            break;
        case 1:
            sPlaneName = fileNameB1;
            plane = new Plane("file:///" + m_sModelsDir + "/" + sPlaneName, SCALE_B1, TURNX_B1, 0.0d, 0.0d);
            break;
        case 2:
            sPlaneName = fileNameF14;
            plane = new Plane("file:///" + m_sModelsDir + "/" + sPlaneName, SCALE_F14, TURNX_F14, 0.0d, 0.0d);
            break;
        case 3:
            sPlaneName = fileNameF16;
            plane = new Plane("file:///" + m_sModelsDir + "/" + sPlaneName, SCALE_F16, TURNX_F16, 0.0d, 0.0d);
            break;
        case 4:
            sPlaneName = fileNameF18;
            plane = new Plane("file:///" + m_sModelsDir + "/" + sPlaneName, SCALE_F18, TURNX_F18, 0.0d, 0.0d);
            break;
        case 5:
            sPlaneName = fileNameA10;
            plane = new Plane("File:///" + m_sModelsDir + "/" + sPlaneName, SCALE_A10, TURNX_A10, 0.0d, 0.0d);
            break;
        case 6:
            sPlaneName = fileNameMirage;
            plane = new Plane("File:///" + m_sModelsDir + "/" + sPlaneName, SCALE_Mirage, TURNX_Mirage, 0.0d, 0.0d);
            break;    
        case 7:
            sPlaneName = fileNameSU27;
            plane = new Plane("File:///" + m_sModelsDir + "/" + sPlaneName, SCALE_SU27, TURNX_SU27, 0.0d, 0.0d);
            break;    
        }
        //ProgramConfig.setPlaneName(sPlaneName);
        return plane;
    }
}
