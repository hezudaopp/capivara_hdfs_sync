/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

import java.util.HashMap;

import net.sf.gnocchi.GWorker;

public class GWorkerStore {

  private final HashMap<GWorker, String> workerMap = new HashMap<GWorker, String>();

  public GWorkerStore() {
  }

  public void registerWorker(final GWorker worker, final String taskTag) {
    if (worker == null) {
      throw new IllegalArgumentException("worker is null");
    }
    if (taskTag == null) {
      throw new IllegalArgumentException("taskTag is null");
    }
    workerMap.put(worker, taskTag);
  }

  public void unregisterWorker(final GWorker worker) {
    workerMap.remove(worker);
  }

  public String getTaskTag(final GWorker worker) {
    return workerMap.get(worker);
  }

  public boolean contains(final GWorker worker) {
    return workerMap.containsKey(worker);
  }

}
