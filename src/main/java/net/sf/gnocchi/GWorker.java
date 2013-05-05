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
 * $Id: GWorker.java,v 1.13 2006/04/22 14:29:34 hunold Exp $
 */

package net.sf.gnocchi;

public abstract class GWorker extends Thread {

  private GWorkerObserver observer;

  public final static int
  	STATE_RUNNING     = 0,
  	STATE_DONE        = 1,
  	STATE_INTERRUPTED = 2,
  	STATE_READY       = 3,
    STATE_DIED        = 4;

  private int state = STATE_READY;
  private boolean cancelled = false;
  private Exception exception;

  public GWorker() {
    observer =null;
  }

  public GWorker(GWorkerObserver observer) {
    this.observer = observer;
  }

  public void addObserver(GWorkerObserver observer) {
    this.observer = observer;
  }

  public GWorkerObserver getObserver() {
    return observer;
  }

  public final void run() {

    synchronized(this) {
      state = STATE_RUNNING;
    }

    try {
      task();
    } catch (InterruptedException ie) {
      synchronized (this) {
        state = STATE_INTERRUPTED;
      }
    } catch (Exception ex) {
      synchronized (this) {
        state = STATE_DIED;
        exception = ex;
        // ex.printStackTrace();
      }
    }

    synchronized(this) {
      if( state != STATE_INTERRUPTED && state != STATE_DIED ) {
        state = STATE_DONE;
      }
      if( observer != null ) {
        observer.finished(this);
      }
    }
  }

  public abstract void task() throws Exception;


  public abstract GWorkerEvent construct();

  public synchronized final void cancel() {
    if( state != STATE_DONE ) {
      state = STATE_INTERRUPTED;
      cancelled = true;
      interrupt();
      customCancel();
    }
  }

  public synchronized boolean isCancelled() {
    return cancelled;
  }

  /**
   * This is only to be used inside worker threads for which
   * sub worker threads have died.
   * @param e
   */
  protected synchronized final void setDied(Exception e) {
    state = STATE_DIED;
    exception = e;
    interrupt();
  }

  public synchronized final boolean hasDied() {
    return (state == STATE_DIED);
  }

  public synchronized final boolean isDone() {
    return (state == STATE_DONE);
  }

  /**
   * callback for customized cancel events
   * e.g. when a sub-thread has to be cancelled as well
   */
  public synchronized void customCancel() {}

  public synchronized final int getWorkerState() {
    return state;
  }

  public synchronized boolean isFinished() {
    return (state == STATE_DONE
        ||  state == STATE_INTERRUPTED
        ||  state == STATE_DIED);
  }

  public synchronized boolean isRunning() {
    return (state == STATE_RUNNING );
  }

  public synchronized Exception getException() {
    return exception;
  }

}
