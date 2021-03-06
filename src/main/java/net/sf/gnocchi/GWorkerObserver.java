/*
 * gnocchi - worker thread framework
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: GWorkerObserver.java,v 1.5 2006/03/08 22:00:12 hunold Exp $
 */

package net.sf.gnocchi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GWorkerObserver {

  private final List<GWorkerListener> listeners = new ArrayList<GWorkerListener>();
  private final List<GWorker> workerList = new ArrayList<GWorker>();

  private boolean executeAndWait;
  private boolean finished;

  public GWorkerObserver() {
  }

  public synchronized void execute(GWorker worker) {
    workerList.add(worker);
    worker.start();
  }

  public synchronized void cancel(GWorker worker) {
    worker.cancel();
    workerList.remove(worker);
  }

  public synchronized void updateGUI(GWorkerEvent e) {
    fireGuiUpdate(e);
  }

  public synchronized void finished(GWorker worker) {
    workerList.remove(worker);
    if (executeAndWait) {
      finished = true;
      notifyAll();
    } else {
      handleWorkerState(worker);
    }
  }

  public synchronized void executeAndWait(GWorker worker) {
    workerList.add(worker);
    executeAndWait = true;
    finished = false;
    worker.start();
    while (!finished) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
    executeAndWait = false;
  }

  public synchronized boolean isWorkerDone(GWorker worker) {
    if (workerList.contains(worker)) {
      return worker.isFinished();
    } else {
      return true;
    }
  }

  public synchronized void addWorkerListener(GWorkerListener l) {
    listeners.add(l);
  }

  public synchronized void removeWorkerListener(GWorkerListener l) {
    listeners.remove(l);
  }

  private void handleWorkerState(GWorker worker) {
    switch( worker.getWorkerState() ) {
    case GWorker.STATE_DONE:
      fireWorkerDone(worker.construct());
      break;
    case GWorker.STATE_INTERRUPTED:
      fireWorkerCancel(worker.construct());
      break;
    case  GWorker.STATE_DIED:
      fireWorkerDied(worker.construct());
      break;
    }
  }


  private void fireGuiUpdate(GWorkerEvent e) {
    final Iterator<GWorkerListener> lit = listeners.iterator();
     while(lit.hasNext()) {
       ((GWorkerListener)lit.next()).updateModel(e);
     }
  }

  private void fireWorkerDone(GWorkerEvent e) {
      final Iterator<GWorkerListener>  lit = listeners.iterator();
     while(lit.hasNext()) {
       ((GWorkerListener)lit.next()).workerDone(e);
     }
  }

  private void fireWorkerCancel(GWorkerEvent e) {
      final Iterator<GWorkerListener>  lit = listeners.iterator();
     while(lit.hasNext()) {
       ((GWorkerListener)lit.next()).workerCancelled(e);
     }
  }

  private void fireWorkerDied(GWorkerEvent e) {
      final Iterator<GWorkerListener>  lit = listeners.iterator();
     while(lit.hasNext()) {
       ((GWorkerListener)lit.next()).workerDied(e);
     }
  }
  
  public synchronized int getNumberOfCurrentWorkers() {
      return workerList.size();
  }

  public synchronized void cancelWorkers() {
     for (GWorker worker : workerList) {
        worker.cancel();
     }
  }

}