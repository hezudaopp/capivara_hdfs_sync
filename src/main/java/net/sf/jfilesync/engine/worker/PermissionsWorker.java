/*
 * capivara - Java File Synchronization
 *
 * Created on 10-Jun-2005
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
 * $Id: PermissionsWorker.java,v 1.10 2006/08/02 20:25:46 hunold Exp $
 */
package net.sf.jfilesync.engine.worker;

import java.awt.Component;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.worker.events.DisplayFileNameMessage;
import net.sf.jfilesync.engine.worker.events.ProgressMessage;
import net.sf.jfilesync.engine.worker.events.TFileListEvent;
import net.sf.jfilesync.engine.worker.events.TSimpleWorkerDoneEvent;
import net.sf.jfilesync.gui.dialog.CollectFilesDialog;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;

public class PermissionsWorker extends GWorker implements GWorkerListener,
    TAbstractGUIElementListener {

  private final Component parent;
  private final AbstractConnectionProxy con;
  private final TFileProperties[] files;
  private final int dir_perm, file_perm;
  private final boolean recursive;
  private final ConnectionDetails details;

  private TAbstractWorkerGUIElement abstractGuiElement;
  private GWorkerObserver workerObserver = new GWorkerObserver();

  private final static Logger LOGGER = Logger.getLogger(PermissionsWorker.class
      .getPackage().getName());

  public PermissionsWorker(final Component parent,
      final AbstractConnectionProxy connection, final ConnectionDetails details,
      final TFileProperties[] files, final int dir_perm, final int file_perm,
      final boolean recursive) {
    this.parent = parent;
    con = connection;
    this.details = details;
    this.files = files;
    this.dir_perm = dir_perm;
    this.file_perm = file_perm;
    this.recursive = recursive;
  }


  public void task() throws Exception {
    if (con != null && con.isConnected()) {

      if (recursive) {

        abstractGuiElement = new CollectFilesDialog(parent, details);
        GWorker worker = new DataCollectWorker(con, files, true, true);
        worker.addObserver(workerObserver);
        workerObserver.addWorkerListener(this);
        abstractGuiElement.addTAbstractGUIElementListener(this);

        abstractGuiElement.enableGUIElement(true);
        workerObserver.executeAndWait(worker);
        abstractGuiElement.enableGUIElement(false);

        if (worker.getWorkerState() == GWorker.STATE_DONE) {
          GWorkerEvent res = worker.construct();
          if (res instanceof TFileListEvent) {
            TFileListEvent ev = (TFileListEvent) res;
            TFileProperties[] colFiles = ev.getFileData()
                .getFilePropertiesArray();
            changePermissions(colFiles);
          }
        } else {
          if (worker.getWorkerState() == GWorker.STATE_DIED) {
            throw worker.getException();
          } else {
            cancel();
          }
        }
      } else {
        changePermissions(files);
      }

    } else {
      throw new IOException("not connected");
    }
  }

  protected void changePermissions(TFileProperties files[]) {
    GWorkerObserver observer = getObserver();
    for (int i = 0; files != null && i < files.length && !isCancelled(); i++) {
      TFileProperties file = files[i];
      int perm = (file.isDirectory()) ? dir_perm : file_perm;
      if (observer != null) {
        observer.updateGUI(new DisplayFileNameMessage(this, file
            .getAbsoluteFileName()));
        int progress = i * 100 / files.length;
        observer.updateGUI(new ProgressMessage(this, progress));
      }
      try {
        con.setPermissions(file.getAbsoluteFileName(), perm);
        //con.setPermissions(file.getFileName(), perm);
      } catch (IOException e) {
        LOGGER.warning(e.getMessage());
        cancel();
      }
    }
  }

  public GWorkerEvent construct() {
    return new TSimpleWorkerDoneEvent(this);
  }

  /*
   * dont need those since executeAndWait requires
   * the callers to check the worker state themselves
   */
  public void workerDone(GWorkerEvent e) {}
  public void workerCancelled(GWorkerEvent e) {}
  public void workerDied(GWorkerEvent e) {}


  public void updateModel(GWorkerEvent e) {
    abstractGuiElement.displayWorkerData(e);
  }


  public void cancelClicked(TAbstractDialogEvent e) {
    cancel();
  }

}
