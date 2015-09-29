/*
 * DlgHelp.java
 */
import java.awt.*;
import java.io.*;


public class DlgHelp extends javax.swing.JDialog  implements Runnable
{
    private Thread m_thread = null;
    private String m_topic;
    private String m_helpFileName = "misc/Help.txt";

    /** Creates new form DlgHelp */
    public DlgHelp(String topic)
    {
        super(MainFrame.m_this, true);
	m_topic = topic;

        initComponents();
        setBounds(200, 200, 500, 700);

        closeJButton.requestFocus();

        start();
    }

    public void run()
    {
            getHelpText(m_topic, helpList);
            stop();
    }

    public void start()
    {
        if (m_thread == null) {
            m_thread = new Thread(this);
            m_thread.start();
        }
    }

    public void stop()
    {
        if (m_thread != null) {
            Thread t = m_thread; 
            m_thread = null;
            t.stop();
            // no control flow here.
        }
    }

    protected void getHelpText(String label, List list)
    {
            boolean seeking = true;

            // read the section of the help file:
            InputStream is = null;
            try {
                   is = new FileInputStream(m_helpFileName);
            } catch (Exception e) {
                    System.err.println(m_helpFileName + " " + e);
                    list.addItem("Help subsystem error: " + e);
                    return;
            }

            DataInputStream dis = new DataInputStream(is);
            String str;
            int lineno = 1;

            String labelStart = "HELPSTART=" + label;

            try {
                    for(; (str=dis.readLine()) != null ;lineno++) {
                            if(str.equalsIgnoreCase(labelStart)) {
                                    seeking = false;
                                    continue;
                            }
                            if(!seeking && str.startsWith("HELPSTART=")) {
                                    break;
                            }
                            if(!seeking) {
                                    String tmp = str.startsWith("\t") ? "    " : "";
                                    tmp += str.replace('\t', ' ');
                                    list.addItem(tmp);
                            }
                    }
            } catch (Exception e) {
                    System.err.println("line " + lineno + " of " 
                                            + m_helpFileName + " " + e);
            }
            try { is.close(); } catch (Exception e) { }

            if(list.countItems() == 0) {
                    list.addItem("No help is available on this topic (" + label + ")");
            }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        bottomJPanel = new javax.swing.JPanel();
        closeJButton = new javax.swing.JButton();
        helpList = new java.awt.List();
        
        getContentPane().setLayout(new java.awt.BorderLayout(10, 10));
        
        setTitle("whiskeyDem Help");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        bottomJPanel.setPreferredSize(new java.awt.Dimension(0, 60));
        bottomJPanel.setMinimumSize(new java.awt.Dimension(0, 20));
        closeJButton.setText("Close");
        closeJButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });
        
        bottomJPanel.add(closeJButton);
        
        getContentPane().add(bottomJPanel, java.awt.BorderLayout.SOUTH);
        
        getContentPane().add(helpList, java.awt.BorderLayout.CENTER);
        
        pack();
    }//GEN-END:initComponents

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed
        // Add your handling code here:
        cleanup();
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        cleanup();
    }//GEN-LAST:event_closeDialog

    private void cleanup()
    {
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomJPanel;
    private javax.swing.JButton closeJButton;
    private java.awt.List helpList;
    // End of variables declaration//GEN-END:variables

}
