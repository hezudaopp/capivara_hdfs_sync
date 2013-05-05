/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TextViewManager.java,v 1.5 2006/08/22 21:29:09 hunold Exp $
 */
package net.sf.jfilesync.gui;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;

public class TextViewManager {

  private final Component parent;
  private final static Logger LOGGER = Logger.getLogger(TextViewManager.class
      .getName());
   
  public TextViewManager(final Component parent) {
    this.parent = parent;
  }
    
  public synchronized void startViewerFor(final AbstractConnectionProxy con, 
      final TFileProperties file) {
    
    if( con == null || file == null ) {
      return;
    }
    
    if( file.isDirectory() ) {
      return;
    }
    
    if( con.isLocalConnection() ) {
      
      try {
        String content = readContent(file.getAbsoluteFileName());
        new TextViewFrame(content).setVisible(true);

      } catch(IOException e) {
        LOGGER.warning(e.getMessage());
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL);
      }
      
    } else {

      try {
        int max_file_size = MainWin.config.getProgramSettings()
            .getIntegerOption(TProgramSettings.OPTION_CONFIRM_EDITOR_SIZE);

        boolean confirm_file_open = MainWin.config.getProgramSettings()
            .getBooleanOption(TProgramSettings.OPTION_CONFIRM_EDITOR_OPEN);

        BigInteger fileSize = file.getFileSize().divide(new BigInteger("1024"));
        BigInteger maxSize = new BigInteger(new Integer(max_file_size)
            .toString());

        if (confirm_file_open && fileSize.compareTo(maxSize) > 0) {
          int opt = JOptionPane.showConfirmDialog(parent, LanguageBundle
              .getInstance().getMessage("dialog.confirm.remotefileedit.apply"));
          if (opt != JOptionPane.OK_OPTION) {
            return;
          }
        }

        RemoteFileTextViewer remoteViewer = new RemoteFileTextViewer(con, file
            .getAbsoluteFileName());
        remoteViewer.start();
                
      } catch (SettingsTypeException e) {
        LOGGER.warning(e.getMessage());
      }
    }
    
  }
  
  public static String readContent(final String fileName) throws IOException {
    final BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
    final StringBuffer buf = new StringBuffer();
    String line;
    while ( (line = reader.readLine()) != null) {
      buf.append(line);
      buf.append("\n");
    }
    reader.close();

    return buf.toString();
  }
    
  class RemoteFileTextViewer implements Runnable {
    
    private final AbstractConnectionProxy con;
    private final String fileName;
    
    public RemoteFileTextViewer(final AbstractConnectionProxy con, 
        final String fileName) {
      this.con = con;
      this.fileName = fileName;
    }
    
    public void start() {
      new Thread(this).start(); 
    }
    
    public void run() {
      
      String content = "";
      
      if( ! con.isConnected() ) {
        content = "Not connected. @todo@";
      } else {

        try {
          File tmpFile = File.createTempFile("capi_viewer", null);
          //LOGGER.info("tmp file : " + tmpFile.getAbsolutePath());
          con.get(fileName, new FileOutputStream(tmpFile));
          content = readContent(tmpFile.getAbsolutePath());
          tmpFile.delete();
          
          if( content == null ) {
            content = "Read error @todo@";
          }
          new TextViewFrame(content).setVisible(true);
                    
        } catch(IOException e) {
          LOGGER.warning(e.getMessage());
          TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, e.getMessage());
        }
        
      }
      
    }
    
  }
    
}
