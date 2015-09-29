/*
 * MainFrame.java
 */
import java.awt.*;


public class MainFrame extends javax.swing.JFrame 
{
  private static final String demDirName = "FlightObjects/DEMs";
  private static final String cacheDirName = "FlightObjects/cache";
  private static final String modelsDirName = "FlightObjects/Shapes";
  private static final String configFileName = "config/config.txt";
  
  public static MainFrame m_this = null;

  // Variables declaration
  private javax.swing.JPanel jPanel2;
  private ControlJPanel m_controlJPanel;
  private MainViewJPanel m_mainView;
  // End of variables declaration

  /*
  * @param args the command line arguments
  */
  public static void main (String args[]) 
  {
    System.out.println("Endo Sim");
    new ProgramConfig(configFileName);
    
    new DemFactory(demDirName, cacheDirName);
    new PlaneFactory(modelsDirName);

    MainFrame mf = new MainFrame();
    mf.setBounds(ProgramConfig.getMainWindowX(), ProgramConfig.getMainWindowY(), ProgramConfig.getMainWindowWidth(), ProgramConfig.getMainWindowHeight());
    mf.show();
  }

  /** Creates new form MainFrame */
  public MainFrame() 
  {
  	Image iIcon = getToolkit().getImage("whiskeysplash.jpg");
    this.setIconImage(iIcon);
    
    m_this = this;
    
    initComponents();
    m_mainView.setControlPanel(m_controlJPanel);
    pack();
  }

  /** This method is called from within the constructor to initialize the form.
   */
  private void initComponents () 
  {
    jPanel2 = new javax.swing.JPanel ();
    m_controlJPanel = new ControlJPanel ();
    m_mainView = new MainViewJPanel ();

    /*
    setEnabled (false);
    addComponentListener (new java.awt.event.ComponentAdapter () {
                            public void componentHidden (java.awt.event.ComponentEvent evt) {
                              formComponentHidden (evt);
                            }
                            public void componentShown (java.awt.event.ComponentEvent evt) {
                              formComponentShown (evt);
                            }
                          }
                        );
    */

    addWindowListener (new java.awt.event.WindowAdapter () {
                            public void windowClosing (java.awt.event.WindowEvent evt) {
                              exitForm (evt);
                            }
                          }
                      );

  
    jPanel2.add (m_controlJPanel);

    getContentPane ().add (jPanel2, java.awt.BorderLayout.SOUTH);

    getContentPane ().add (m_mainView, java.awt.BorderLayout.CENTER);

  }

  /*
  private void formComponentHidden (java.awt.event.ComponentEvent evt) {
	// Add your handling code here:
  }

  private void formComponentShown (java.awt.event.ComponentEvent evt) {
	// Add your handling code here:
  }
  */

  /** Exit the Application */
  private void exitForm(java.awt.event.WindowEvent evt) {
    Dimension s = this.getSize();
    int width = (int)s.getWidth();
    int height = (int)s.getHeight();
    ProgramConfig.setMainWindowWidth(width);
    ProgramConfig.setMainWindowHeight(height);
    ProgramConfig.setMainWindowX(this.getX());
    ProgramConfig.setMainWindowY(this.getY());
    ProgramConfig.store();
    System.exit (0);
  }

  public static void setFrameTitle(String newTitle)
  {
    m_this.setTitle(newTitle);
  }

}
