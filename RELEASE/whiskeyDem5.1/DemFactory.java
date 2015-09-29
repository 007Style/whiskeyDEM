/*
 * DemFactory.java
 */
import java.io.*; 
import javax.media.j3d.*;
import javax.vecmath.*;


class DemFilenameFilter implements FilenameFilter
{
      public boolean accept(java.io.File file, java.lang.String str) {
          if(str.toLowerCase().endsWith(".dem")) {
              return true;
          }
          return false;
      }
}
  

public class DemFactory
{
  // Canonical names for directories - to be used for file names:
  private static String m_sCacheDir = null;
  private static String m_sDemDir = null;
  
  private static String m_areaNames[] = null;

  private static String m_areaName = null;
  
   /** Configures DemFactory 
   * computes path names for DEM folder and cache folder, and fills m_areaNames[] array
   * by listing all .dem files in the DEM folder. Requires DemFilenameFilter class.
   */

    public DemFactory(String demDirName, String cacheDirName)
    {
      File demDir = new File(demDirName);
      File cacheDir = new File(cacheDirName);
      try {
          m_sCacheDir = cacheDir.getCanonicalPath();
          System.out.println("CACHE path=" + m_sCacheDir);
          if(!cacheDir.exists()) {
              if(!cacheDir.mkdirs()) {
                  System.err.println("Error: failed to create cache folder: " + m_sCacheDir);
                  System.exit(1);
              }
          }

          m_sDemDir = demDir.getCanonicalPath();
          System.out.println("DEM path=" + m_sDemDir);
          if(!demDir.exists()) {
              System.err.println("Error: DEM folder not found: " + m_sDemDir);
              System.exit(1);
          }
          FilenameFilter filter = new DemFilenameFilter();
          String demFiles[] = demDir.list(filter);
          int demFilesCount = demFiles.length;
          if(demFilesCount == 0) {
              System.err.println("Error: no DEM files in folder: " + m_sDemDir);
              System.exit(1);
          }
          m_areaNames = new String[demFilesCount];
          for(int i=0; i < demFilesCount ;i++) {
              String name = demFiles[i];
              System.out.print("    " + name);
              m_areaNames[i] = name.substring(0, name.length() - 4);
              System.out.println(" --->  " + m_areaNames[i]);
          }

      } catch(Exception e) {
          e.printStackTrace();
          System.exit(1);
      }
    }
    
    public static String[] getAreaNames()
    {
        return m_areaNames;
    }

    public static String getAreaName()
    {
        return m_areaName;
    }

    public static Dem produce(BoundingSphere bounds, int index)
    {
        Dem dem = null;
        if(index < 0) { // use areaName from ProgramConfig
            String preferredAreaName = ProgramConfig.getAreaName();
            if(preferredAreaName == null) {
                index = 0;
            } else {
                boolean found = false;
                for(int i=0; i < m_areaNames.length ;i++) {
                    if(m_areaNames[i].equals(preferredAreaName)) {
                        index = i;
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    index = 0;
                }
            }
        }
        m_areaName = m_areaNames[index];
         
        String demName = m_sDemDir + "/" + m_areaName + ".dem";
        String shpName = m_sCacheDir + "/" + m_areaName + ".shp";
        
        dem = new Dem(bounds, demName, shpName);
        return dem;
    }
}
