/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.gui.viewmodel;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.util.TMiscTool;

public class BasicFilePresenter implements TEventListener {

  private final static BigInteger KBYTE = new BigInteger("1024");
  private final static BigInteger ONE = new BigInteger("1");
  private final static BigInteger ZERO = new BigInteger("0");

  private final boolean isWindows = (TMiscTool.getOSId() == TMiscTool.OS_WINDOWS);
  private boolean fileSizeInBytes = false;
  private final static Logger LOGGER = Logger
      .getLogger(TFileDataPresenter.class.getPackage().getName());

  private final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss,z");

  public BasicFilePresenter() {
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    loadConfig();
    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.SETTINGS_CHANGED_MESSAGE);
  }

  public String getModTime(final BasicFile file) {
    // for friends of Windows Explorer
    if (isWindows && file.getFileModTime() == 0) {
      return "";
    }
    // we need to find a common format for dates of all PropertyConverters
    return dateFormat.format(new Date(file.getFileModTime()));
  }

  public String getSize(final BasicFile file) {
    BigInteger fSize = file.getFileSize();

    // for friends of Windows
    if (isWindows && file.isDirectory() && fSize.compareTo(ZERO) == 0) {
      return "";
    }

    String fileSizeLabel = fSize.toString() + " B";

    if (!fileSizeInBytes) {
      if (fSize.divide(KBYTE).compareTo(ONE) > 0) {
        BigInteger sizeKbyte = fSize.divide(KBYTE);
        // we could add mega-byte too
        fileSizeLabel = sizeKbyte.toString() + " KB";
      }
    }

    return fileSizeLabel;
  }

  public String getName(final BasicFile file) {
    return file.getFileName();
  }

  protected void loadConfig() {
    try {
      fileSizeInBytes = MainWin.config.getProgramSettings().getBooleanOption(
          TProgramSettings.OPTION_FILE_SIZE_BYTES);
    } catch (SettingsTypeException e) {
      LOGGER.warning(e.getMessage());
    }
  }

  public void processEvent(TEvent e) {
    final TMessage.ID mtype = e.getMessage().getMessageType();
    if (mtype == TMessage.ID.SETTINGS_CHANGED_MESSAGE) {
      loadConfig();
    }
  }

}
