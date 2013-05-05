/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TFileDataPresenter.java,v 1.8 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui.viewmodel;

/**
 * This class takes file data and represents the presentation layer. The
 * TableModel will ask for the required data here rather than accessing the file
 * data.
 */

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.util.TMiscTool;

public class TFileDataPresenter implements TEventListener {

  private static BigInteger
      kByte = new BigInteger("1024"),
      one   = new BigInteger("1"),
      zero  = new BigInteger("0");

  private boolean windows = (TMiscTool.getOSId() == TMiscTool.OS_WINDOWS);
  private boolean fileSizeInBytes = false;
  private final static Logger LOGGER = Logger
      .getLogger(TFileDataPresenter.class.getPackage().getName());
  
  public TFileDataPresenter() {
    loadConfig();
    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.SETTINGS_CHANGED_MESSAGE);
  }

  public String getModTime(TFileProperties file) {
    
    // for friends of Windows Explorer
    if( windows && file.getFileModTime() == 0 ) {
      return "";
    }
    
    /*
    // links cannot be dealt with
    // so dont show wrong mod times either
    if( file.isLink() ) {
      return "";
    }
    */

    // we need to find a common format for dates of all PropertyConverters
    final SimpleDateFormat dateFormat = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss,z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(new Date(file.getFileModTime()));
  }

  public String getSize(TFileProperties file) {
    BigInteger fSize = file.getFileSize();

    // for friends of Windows
    if( windows && file.isDirectory() && fSize.compareTo(zero) == 0 ) {
      return "";
    }

    String fileSizeLabel = fSize.toString() + " B";
    
    if( ! fileSizeInBytes ) {
      if( fSize.divide(kByte).compareTo(one) > 0 ) {
        BigInteger sizeKbyte = fSize.divide(kByte);
        // we could add mega-byte too
        fileSizeLabel = sizeKbyte.toString() + " KB";
      }
    }
    
    return fileSizeLabel;
  }

  public String getName(TFileProperties file) {
    return file.getFileName();
  }
  
  protected void loadConfig() {
    try {
      fileSizeInBytes = MainWin.config.getProgramSettings().getBooleanOption(
          TProgramSettings.OPTION_FILE_SIZE_BYTES);
    } catch (SettingsTypeException ste) {
      LOGGER.warning(ste.getMessage());
    }
  }

  public void processEvent(TEvent e) {
    TMessage.ID mtype = e.getMessage().getMessageType();
    if( mtype == TMessage.ID.SETTINGS_CHANGED_MESSAGE ) {
      loadConfig();
    }
  }
  
}
