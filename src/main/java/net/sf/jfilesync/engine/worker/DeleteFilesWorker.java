package net.sf.jfilesync.engine.worker;

/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: DeleteFilesWorker.java,v 1.5 2006/08/02 20:25:46 hunold Exp $
 */

import java.io.IOException;

import javax.swing.JOptionPane;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.develop.DevelopmentMode;
import net.sf.jfilesync.engine.*;
import net.sf.jfilesync.engine.worker.events.DeleteStatusMessage;
import net.sf.jfilesync.engine.worker.events.TSimpleWorkerDoneEvent;
import net.sf.jfilesync.plugins.net.*;
import net.sf.jfilesync.prop.LanguageBundle;

public class DeleteFilesWorker extends GWorker {

  private final AbstractConnectionProxy con;
  private final TFileProperties[] files;

  public DeleteFilesWorker(final AbstractConnectionProxy con,
      final TFileProperties[] files, final boolean recursive) {
    this.con = con;
    this.files = files;
  }

  public void task() throws Exception {

    // remember where we started
    String startPath = con.getCurrentPath();

    eraseFiles(files, true);

    // change back to starting path
    // otherwise, the system may get confused
    con.chdir(startPath);
  }

  public void eraseFiles(TFileProperties[] files, boolean recursive)
      throws IOException {
    if (isCancelled()) {
      return;
    }

    for (int i = 0; i < files.length && !isCancelled(); i++) {
      if (files[i].isDirectory() && !files[i].isLink()) {
        if (recursive) {
          deleteFilesFromDir(files[i]);

          if (!isCancelled()) {
            getObserver().updateGUI(
                new DeleteStatusMessage(this, files[i].getAbsoluteFileName()));

            // dir should be empty now -> delete it
            try {
              con.rmdir(files[i].getAbsoluteFileName());
            } catch (IOException e) {
              boolean cont = handleIOException(files[i].getAbsoluteFileName(),
                  e);
              if (!cont) {
                cancel();
                break;
              }
            }
          }
        }
      } else {
        getObserver().updateGUI(
            new DeleteStatusMessage(this, files[i].getAbsoluteFileName()));
        try {
          con.remove(files[i].getAbsoluteFileName());
        } catch (IOException e) {
          boolean cont = handleIOException(files[i].getAbsoluteFileName(), e);
          if (!cont) {
            cancel();
            break;
          }
        }
      }

      if (DevelopmentMode.DELETE_WORKER_GUI_DEBUG) {
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
        }
      }
    }
  }

  private void deleteFilesFromDir(TFileProperties dir) throws IOException {
    if (!isCancelled()) {
      con.chdir(dir.getAbsoluteFileName());
      TFileProperties[] dirContent = con.ls().getFilePropertiesArray();
      eraseFiles(dirContent, true);
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

  public GWorkerEvent construct() {
    return new TSimpleWorkerDoneEvent(this);
  }

}
