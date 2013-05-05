/*
 * capivara - Java File Synchronization
 *
 * Created on 24-Jun-2005
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
 * $Id: WorkerComponentStore.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.engine.worker;

import java.awt.Component;
import java.util.*;

import net.sf.gnocchi.GWorker;

public class WorkerComponentStore {

  private HashMap<GWorker, TAbstractWorkerGUIElement> worker2comp = new HashMap<GWorker, TAbstractWorkerGUIElement>();

  public synchronized void storeComponent(final GWorker w,
      final TAbstractWorkerGUIElement c) {
    worker2comp.put(w, c);
  }

  public synchronized Component getComponent(final GWorker w) {
    return (Component) worker2comp.get(w);
  }

  public synchronized GWorker getWorker(final Component c) {
    GWorker ret = null;
    final Iterator<GWorker> it = worker2comp.keySet().iterator();
    while (it.hasNext()) {
      final GWorker w = (GWorker) it.next();
      final Component inHash = (Component) worker2comp.get(w);
      if (inHash == c) {
        ret = w;
      }
    }
    return ret;
  }

}
