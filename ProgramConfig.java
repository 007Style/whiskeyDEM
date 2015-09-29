/*
 * ProgramConfig.java
 */
import java.util.*;
import java.io.*;


public class ProgramConfig extends Properties
{
    public static ProgramConfig m_this;
    public static String m_configFileName;
    
    /** Creates new ProgramConfig */
    public ProgramConfig(String configFileName)
    {
        m_configFileName = configFileName;
        
        File configFile = new File(configFileName);
        if(!configFile.exists()) {
            System.err.println("Error: config file missing: " + configFileName);
            System.exit(1);
        }
        
        try {
            InputStream is = new FileInputStream(configFile);
            load(is);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        String sprop;

        sprop = getProperty("mainWindowWidth");
        if(sprop == null) {
            setProperty("mainWindowWidth", "" + m_mainWindowWidth);
        } else {
            try { int w = Integer.parseInt(sprop); if(w >= 300) { m_mainWindowWidth = w; } } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("mainWindowHeight");
        if(sprop == null) {
            setProperty("mainWindowHeight", "" + m_mainWindowHeight);
        } else {
            try { int h = Integer.parseInt(sprop); if(h >= 300) { m_mainWindowHeight = h; } } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("mainWindowX");
        if(sprop == null) {
            setProperty("mainWindowX", "" + m_mainWindowX);
        } else {
            try { m_mainWindowX = Integer.parseInt(sprop); } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("mainWindowY");
        if(sprop == null) {
            setProperty("mainWindowY", "" + m_mainWindowY);
        } else {
            try { m_mainWindowY = Integer.parseInt(sprop); } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("areaName");
        if(sprop == null) {
            setProperty("areaName", "" + m_areaName);
        } else {
            m_areaName = sprop;
            System.out.println("m_areaName " + m_areaName);
        }
        
        sprop = getProperty("planeName");
        if(sprop == null) {
            setProperty("planeName", "" + m_planeName);
        } else {
            m_planeName = sprop;
            System.out.println("m_planeName " + m_planeName);
        }
        
        sprop = getProperty("allowWireframe");
        if(sprop != null && sprop.equalsIgnoreCase("true")) {
            m_allowWireframe = true;
        }

        sprop = getProperty("rollFactor");
        if(sprop == null) {
            setProperty("rollFactor", "" + m_rollFactor);
        } else {
            try { m_rollFactor = Double.parseDouble(sprop); } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("pitchFactor");
        if(sprop == null) {
            setProperty("pitchFactor", "" + m_pitchFactor);
        } else {
            try { m_pitchFactor = Double.parseDouble(sprop); } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("yawFactor");
        if(sprop == null) {
            setProperty("yawFactor", "" + m_yawFactor);
        } else {
            try { m_yawFactor = Double.parseDouble(sprop); } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("speedFactor");
        if(sprop == null) {
            setProperty("speedFactor", "" + m_speedFactor);
        } else {
            try { m_speedFactor = Double.parseDouble(sprop); } catch (Exception e) { e.printStackTrace(); }
        }

        sprop = getProperty("framesPerSecond");
        if(sprop == null) {
            setProperty("framesPerSecond", "" + m_framesPerSecond);
        } else {
            try { m_framesPerSecond = Integer.parseInt(sprop); } catch (Exception e) { e.printStackTrace(); }
        }
        
        sprop = getProperty("serverPort");
        if(sprop == null) {
            setProperty("serverPort", "" + m_serverPort);
        } else {
            try { m_serverPort = Integer.parseInt(sprop); } catch (Exception e) { e.printStackTrace(); }
        }
        
        sprop = getProperty("serverName");
        if(sprop == null) {
            setProperty("serverName", "" + m_serverName);
        } else {
            m_serverName = sprop;
        } 
        
        sprop = getProperty("botsOn");
        if(sprop != null && sprop.equalsIgnoreCase("false")) {
            m_botsOn = false;
        }
        
        sprop = getProperty("simType");
        if(sprop == null) {
            setProperty("simType", "" + m_simType);
        } else {
            try { m_simType = Integer.parseInt(sprop); } catch (Exception e) { e.printStackTrace(); }
        }
        
        sprop = getProperty("syncAfterFrame");
        if(sprop == null) {
            setProperty("syncAfterFrame", "" + m_syncAfterFrame);
        } else {
            try { m_syncAfterFrame = Integer.parseInt(sprop); } catch (Exception e) { e.printStackTrace(); }
        }
        
        m_this = this;
    }

    public static void store()
    {
        File configFile = new File(m_configFileName);
        try {
            OutputStream os = new FileOutputStream(configFile);
            m_this.store(os, "whiskeyDem properties file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static int m_syncAfterFrame = -1;
    public static int getSyncAfterFrame() { return m_syncAfterFrame; }
    public static void setSyncAfterFrame(int syncF) { m_syncAfterFrame = syncF; m_this.setProperty("syncAfterFrame", "" + syncF); }
    
    private static int m_simType = 0;
    public static int getSimType() { return m_simType; }
    public static void setSimType(int simT) { m_simType = simT; m_this.setProperty("simType", "" + simT); }
    
    private static boolean m_botsOn = true;
    public static boolean getBotsOn() { return m_botsOn; }
    public static void setBotsOn(boolean b) { m_botsOn = b; m_this.setProperty("botsOn", "" + b); }
    
    private static int m_serverPort = 2222;
    public static int getServerPort() { return m_serverPort; }
    public static void setServerPort(int num) { m_serverPort = num; m_this.setProperty("serverPort", "" + num); }
    
    private static String m_serverName = "kdog";
    public static String getServerName() { return m_serverName; }
    public static void setServerName(String name) { m_serverName = name; m_this.setProperty("serverName", "" + name); }
    
    private static String  m_areaName  = "santa_w";
    public static String getAreaName() { return m_areaName; }
    public static void setAreaName(String s) { m_areaName = s; m_this.setProperty("areaName", "" + s); }

    private static String  m_planeName  = "";
    public static String getPlaneName() { return m_planeName; }
    public static void setPlaneName(String s) { m_planeName = s + "" + ".wrl"; m_this.setProperty("planeName", "" + m_planeName); }

    private static boolean m_allowWireframe = false;
    public static boolean isAllowWireframe() { return m_allowWireframe; }
    public static void setAllowWireframe(boolean b) { m_allowWireframe = b; m_this.setProperty("allowWireframe", "" + b); }
    
    private static double  m_rollFactor  = 0.04;
    public static double getRollFactor() { return m_rollFactor; }
    public static void setRollFactor(double d) { m_rollFactor = d; m_this.setProperty("rollFactor", "" + d); }

    private static double  m_pitchFactor = 0.04;
    public static double getPitchFactor() { return m_pitchFactor; }
    public static void setPitchFactor(double d) { m_pitchFactor = d; m_this.setProperty("pitchFactor", "" + d); }

    private static double  m_yawFactor   = 0.04;
    public static double getYawFactor() { return m_yawFactor; }
    public static void setYawFactor(double d) { m_yawFactor = d; m_this.setProperty("yawFactor", "" + d); }

    private static double  m_speedFactor = 1.0;
    public static double getSpeedFactor() { return m_speedFactor; }
    public static void setSpeedFactor(double d) { m_speedFactor = d; m_this.setProperty("speedFactor", "" + d); }
    
    private static int m_framesPerSecond = 20;
    public static int getFramesPerSecond() { return m_framesPerSecond; }
    public static void setFramesPerSecond(int fps) { m_framesPerSecond = fps; m_this.setProperty("framesPerSecond", "" + fps); }
    
    private static int m_mainWindowWidth = 400;
    public static int getMainWindowWidth() { return m_mainWindowWidth; }
    public static void setMainWindowWidth(int w) { if (w >= 300) { m_mainWindowWidth = w; m_this.setProperty("mainWindowWidth", "" + w); } }

    private static int m_mainWindowHeight = 460;
    public static int getMainWindowHeight() { return m_mainWindowHeight; }
    public static void setMainWindowHeight(int h) { if (h >= 300) { m_mainWindowHeight = h; m_this.setProperty("mainWindowHeight", "" + h); } }

    private static int m_mainWindowX = 20;
    public static int getMainWindowX() { return m_mainWindowX; }
    public static void setMainWindowX(int x) { m_mainWindowX = x; m_this.setProperty("mainWindowX", "" + x); }

    private static int m_mainWindowY = 20;
    public static int getMainWindowY() { return m_mainWindowY; }
    public static void setMainWindowY(int y) { m_mainWindowY = y; m_this.setProperty("mainWindowY", "" + y); }

}
