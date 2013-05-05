/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.engine.worker;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.worker.events.DeleteStatusMessage;
import net.sf.jfilesync.engine.worker.events.TSimpleWorkerDoneEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.prop.LanguageBundle;

public class BasicFileDeleteWorker extends GWorker {

  private final static Logger LOGGER = Logger
      .getLogger(BasicFileDeleteWorker.class.getName());

  private final AbstractConnectionProxy con;
  private final List<BasicFile> fileList;

  public BasicFileDeleteWorker(final AbstractConnectionProxy con,
      final List<BasicFile> fileList) {
    this.con = con;
    this.fileList = fileList;
  }

  @Override
  public void task() throws Exception {

    for(BasicFile file : fileList ) {
      if (isCancelled()) {
        break;
      } else {
        removeFile(file);
      }
    }

  }

  protected void removeFile(BasicFile file) throws IOException {

    getObserver().updateGUI(
        new DeleteStatusMessage(this, file.getAbsolutePath()));

    LOGGER.info("delete file : " + file.getAbsolutePath());

    try {
      con.rmdir(file.getAbsolutePath());
    } catch (IOException e) {
      boolean cont = handleIOException(file.getAbsolutePath(), e);
      if (!cont) {
        cancel();
      }
    }

  }

  // continue deleting if it returns true
  private boolean handleIOException(final String fileName, final IOException e) {
    boolean ret = false;

    TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, e.getMessage());

    int res = JOptionPane.showConfirmDialog(null, LanguageBundle.getInstance()
        .getMessage("label.continue"), LanguageBundle.getInstance().getMessage(
        "label.continue"), JOptionPane.YES_NO_OPTION);

    if (res == JOptionPane.YES_OPTION) {
      ret = true;
    }

    return ret;
  }

  @Override
  public GWorkerEvent construct() {
    return new TSimpleWorkerDoneEvent(this);
  }

}
