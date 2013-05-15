/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed by Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
<license>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
</license>
 *
 * $Id: TErrorHandling.java,v 1.18 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.engine;

/**
 *  A class for error handling. It should provide the following actions:
 *  <p>
 *   graphical error message
 *   advices for error correction
 *   user interaction
 *  </p>
 */

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;


public class TErrorHandling {
  

  // ERROR Definition 
  public static final int

  NO_ERROR = 1,
  
  // Connections 
  ERROR_CONNECTION_FAILURE            = 50,
  ERROR_CONNECTION_NO_DATA            = 51,
  ERROR_CONNECTION_AUTHENTICATION     = 52,
  ERROR_CONNECTION_UNKNOWN_HOST       = 53,
  ERROR_CONNECTION_TIMEDOUT           = 54,
  ERROR_CONNECTION_NOT_ACTIVE         = 55,
  ERROR_CONNECTION_LOST               = 56,
  
  // IO errors 
  ERROR_IO_GENERAL                    = 60,
  ERROR_IO_LS                         = 61,
  ERROR_IO_PERMISSIONS                = 62,
  ERROR_IO_PATH_UNKNOWN               = 63,
  
  // SSH related errors 
  ERROR_SSH_CONNECTION_HOSTKEY_VERIFICATION = 70,
  
  // Plugins 
  ERROR_PLUGIN_NOT_FOUND = 101,
  ERROR_UNSUPPORTED_PROTOCOL = 110,
  
  ERROR_GET_SYNC_TREE      = 150,
  ERROR_GET_SYNC_DIFF      = 151,
  ERROR_SYNC_PERFORMER     = 152,
  
  ERROR_UNKNOWN_WORKER_ERROR  = 160,
  
  // Threads 
  ERROR_THREAD_WAIT = 200;

  public static final int  
  ERROR_ADVICE_NON   = 0,
  ERROR_ADVICE_RETRY = 1,
  
  ERROR_ADVICE_ABORT = 10,
  ERROR_ADVICE_ABORT_FUNCTION = 11,
  ERROR_ADVICE_ABORT_PROGRAM  = 12;

  private static final int
  LEVEL_INFO        = 1,
  LEVEL_WARN        = 2,
  LEVEL_ERROR       = 3,
  LEVEL_PANIC       = 4;

  private static int errorLevel;
  private static String errorMsg;
  private static String errorHeader;

  private static int errorDialogType;
  private static Object[] errorPossibilities;

  private static String extraErrorMsg;  // additional error infos 

  // customize the errors - ErrorLevel, Advices.... 
  protected static final void setupError(int errorNumber) {
     errorHeader         = "Error report";
     errorDialogType     = 1;
     errorPossibilities  = new Object[] {"action1", "action2"};

     switch (errorNumber) {
       case NO_ERROR :
         errorWithLangKey(LEVEL_INFO, "error.no_errors_detected");
         break ;
       case ERROR_PLUGIN_NOT_FOUND :
         errorWithLangKey(LEVEL_ERROR, "error.plugin_not_found");
         break ;
       case ERROR_UNSUPPORTED_PROTOCOL:
         errorWithLangKey(LEVEL_ERROR, "error.plugin.unsupported");
         break;
       case ERROR_CONNECTION_AUTHENTICATION:
         errorWithLangKey(LEVEL_ERROR, "error.auth.failed");
         break;
       case ERROR_CONNECTION_NO_DATA:
         errorWithLangKey(LEVEL_INFO, "error.con.no_data");
         break;
       case ERROR_CONNECTION_FAILURE:
         errorWithLangKey(LEVEL_ERROR, "error.login.failed");
         break;
       case ERROR_CONNECTION_TIMEDOUT:
         errorWithLangKey(LEVEL_ERROR, "error.login.timeout");
         break;
       case ERROR_CONNECTION_NOT_ACTIVE:
         errorWithLangKey(LEVEL_ERROR, "error.con.not_active");
         break;
       case ERROR_CONNECTION_LOST:
         errorWithLangKey(LEVEL_ERROR, "error.con.lost");
         break;
       case ERROR_SSH_CONNECTION_HOSTKEY_VERIFICATION:
         errorWithLangKey(LEVEL_ERROR, "error.login.verification.hostkey");
         break;
       case ERROR_IO_GENERAL:
         errorWithLangKey(LEVEL_ERROR, "error.io.general");
         break;
       case ERROR_IO_LS:
         errorWithLangKey(LEVEL_ERROR, "error.io.ls");
         break;
       case ERROR_IO_PERMISSIONS:
         errorWithLangKey(LEVEL_ERROR, "error.io.chmod");
         break;
       case ERROR_IO_PATH_UNKNOWN:
         errorWithLangKey(LEVEL_ERROR, "error.io.path_not_exists");
         break;
       case ERROR_SYNC_PERFORMER:
         errorWithLangKey(LEVEL_ERROR, "error.sync.general");
         break;
       case ERROR_UNKNOWN_WORKER_ERROR:
         errorWithLangKey(LEVEL_ERROR, "error.worker.unkown");
         break;
       default:
         // standard error message 
         errorWithLangKey(LEVEL_ERROR, "error.unkown");
         break;   
     }

     if ( extraErrorMsg != null ) {
       errorMsg = errorMsg + "\n" + extraErrorMsg;
     }
  }

  private static void error(int level, String msg) {
    errorLevel = level;
    errorMsg   = msg;
  }

  /*
   * helper method to wrap error() and to be less redundant
   */
  
  private static void errorWithLangKey(int level, String key) {    
    String msg = LanguageBundle.getInstance().getMessage(key);
    error(level, msg);
  }
  
// -------------------------------------------------------------------------- */


  
  public static final int reportError(int errorNumber,
      String additionalErrorMsg) {
     extraErrorMsg = additionalErrorMsg;
     return reportError( errorNumber );
  }

  
  public static final boolean reportErrorSubsequently(int errorNumber,
      String additionalErrorMsg) {
     extraErrorMsg = additionalErrorMsg;
     boolean ret = reportErrorSubsequently(errorNumber);
     extraErrorMsg = null;
     return ret;
  }

  // shows the error message and returns a user advice 

  public static final int reportError(int errorNumber) {

     setupError(errorNumber);

     switch (errorLevel) {
     case LEVEL_INFO:
       JOptionPane.showMessageDialog(null,
           errorMsg, errorHeader,
           JOptionPane.INFORMATION_MESSAGE );
       break;
     case LEVEL_WARN:
       JOptionPane.showMessageDialog(null,
           errorMsg, errorHeader,
           JOptionPane.WARNING_MESSAGE);
       break;
     case LEVEL_ERROR:
     case LEVEL_PANIC:
       // simple Dialogbox
       if( errorDialogType == 1 ) {   
         JOptionPane.showMessageDialog(null,
             errorMsg, errorHeader,
             JOptionPane.ERROR_MESSAGE);
       } 
       else {
         // String s = (String) JOptionPane...
         JOptionPane.showInputDialog(null, errorMsg, errorHeader,
             JOptionPane.WARNING_MESSAGE, null,
             errorPossibilities, "");
         
       }
       
     }

     extraErrorMsg = null;
     return 0;
  }

  public static boolean reportErrorSubsequently(int errornum) {
    boolean ret = true;
    
    setupError(errornum);

    ErrorReportDialog dialog = new ErrorReportDialog((Frame)null, errorMsg, 
        errorHeader);
    dialog.setVisible(true);
    
    if( dialog.isIgnoreSelected() ) {
      ret = false;
    }
    
    return ret;
  }
  
  //---------------------------------------------------------------------------
  // Returns an ImageIcon, or null if the path was invalid. 
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = TErrorHandling.class.getResource(path);
    if (imgURL != null){
      return new ImageIcon(imgURL);
    } 
    else {
      System.err.println("Couldn't find file: " + path) ;
      return null;
    }
  }
  
  
  static class ErrorReportDialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;	// Jawinton
	private JCheckBox ignoreCheckBox = new JCheckBox(
        LanguageBundle.getInstance().getMessage("error.ignore_subsequent"));
    private final String message;
    
    public ErrorReportDialog(Frame owner, String message, String title) {
      super(owner, title, true);
      this.message = message;
      initUI();
    }
    
    public ErrorReportDialog(Dialog owner, String message, String title) {
      super(owner, title, true);
      this.message = message;
      initUI();
    }
       
    private void initUI() {
      
      JButton okButton = new JButton(LanguageBundle.getInstance()
          .getMessage("label.ok"));
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ErrorReportDialog.this.setVisible(false);
        }
      });
      
      
      JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      checkPanel.add(ignoreCheckBox);
      ignoreCheckBox.setFocusable(false);
      
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      buttonPanel.add(okButton);
      
      ImageIcon icon = TImageIconProvider.getInstance()
      .getImageIcon(TImageIconProvider.ERROR_CRITICAL_ICON, 30, 30);
      
      JLabel msgLabel = new JLabel(message);
      msgLabel.setIcon(icon);
      
      JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      msgPanel.add(msgLabel);

      JPanel contentPane = new JPanel();
      contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
      contentPane.add(msgPanel);
      contentPane.add(checkPanel);
      contentPane.add(buttonPanel);
      contentPane.setBorder(BorderFactory.createEmptyBorder(5,0,5,15));
      
      setContentPane(contentPane);
      pack();
      
      setLocationRelativeTo(getParent());
    }
    
    public boolean isIgnoreSelected() {
      return ignoreCheckBox.isSelected();
    }
    
  }

}
