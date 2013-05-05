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
package net.sf.jfilesync.service.unify.worker;

import java.util.List;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.worker.BasicFileDeleteWorker;
import net.sf.jfilesync.engine.worker.events.DeleteStatusMessage;
import net.sf.jfilesync.engine.worker.events.FileProgressWorkerEvent;
import net.sf.jfilesync.engine.worker.events.TSimpleWorkerDoneEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.service.unify.action.UnifyDeleteOption;

public class UnifyDeletePerformer extends GWorker implements GWorkerListener {

  private final UnifyDeleteOption option;
  private final AbstractConnectionProxy con;
  private final GWorkerObserver observer = new GWorkerObserver();

  private int actionNum;
  private int actionsDone;

  public UnifyDeletePerformer(final AbstractConnectionProxy con,
      final UnifyDeleteOption option) {
    this.con = con;
    this.option = option;
  }

  @Override
  public GWorkerEvent construct() {
    return new TSimpleWorkerDoneEvent(this);
  }

  @Override
  public void task() throws Exception {
    final List<BasicFile> fileList = option.getFilesToDelete();

    actionsDone = 0;
    actionNum   = fileList.size();

    final BasicFileDeleteWorker deleteWorker = new BasicFileDeleteWorker(con,
        fileList);

    deleteWorker.addObserver(observer);
    observer.addWorkerListener(this);
    observer.executeAndWait(deleteWorker);
    observer.removeWorkerListener(this);
  }

  public void updateModel(GWorkerEvent e) {
    if( e instanceof DeleteStatusMessage ) {
      actionsDone++;
      DeleteStatusMessage msg = (DeleteStatusMessage)e;
      getObserver().updateGUI(
          new FileProgressWorkerEvent(this, msg.getFileName(),
              getProgressPercentage()));
    }
  }

  private int getProgressPercentage() {
    int percentage = 0;
    if( actionNum > 0 ) {
       percentage = actionsDone*100/actionNum;
    }
    return percentage;
  }


  public void workerCancelled(GWorkerEvent e) {
    cancel();
  }

  public void workerDied(GWorkerEvent e) {
    cancel();
  }

  public void workerDone(GWorkerEvent e) {
  }

}
