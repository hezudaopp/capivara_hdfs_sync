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
 */
package net.sf.jfilesync.engine;

import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.types.JvmStatsMessage;
import net.sf.jfilesync.util.EncodingUtils;

public class CapivaraSystemTracker implements Runnable, CapivaraService {

  private boolean isRunning;
  private boolean shutdown;

  private final long FETCHING_STATS_TIMEOUT = 1000;

  public CapivaraSystemTracker() {
  }

  public synchronized void startService() {
    if( isRunning == false ) {
      Thread t = new Thread(this);
      t.start();
      isRunning = true;
    }
  }

  public synchronized void stopService() {
    if( isRunning ) {
      shutdown = true;
      notify();
    }
  }

  public void run() {
    updateMainStats();
    while( ! shutdown ) {
      //System.out.println("update stats");
      updateJvmStatistics();
      synchronized(this) {
        try {
          wait(FETCHING_STATS_TIMEOUT);
        } catch(InterruptedException e) {}
      }
    }
  }

  private void updateJvmStatistics() {

    TEventMulticaster.getInstance().fireTEvent(
        this,
        TEvent.ANY_REGISTERED_RECEIPIENT,
        new JvmStatsMessage("freemem", Long.toString(Runtime.getRuntime()
            .freeMemory())));

    TEventMulticaster.getInstance().fireTEvent(
        this,
        TEvent.ANY_REGISTERED_RECEIPIENT,
        new JvmStatsMessage("totalmem", Long.toString(Runtime.getRuntime()
            .maxMemory())));

  }

  private void updateMainStats() {

    final String encoding = EncodingUtils.getJVMEnconding();
    TEventMulticaster.getInstance().fireTEvent(this,
        TEvent.ANY_REGISTERED_RECEIPIENT,
        new JvmStatsMessage("encoding", encoding));

    final String locale = System.getProperty("user.language");
    TEventMulticaster.getInstance().fireTEvent(this,
        TEvent.ANY_REGISTERED_RECEIPIENT,
        new JvmStatsMessage("locale", locale));
  }

}
