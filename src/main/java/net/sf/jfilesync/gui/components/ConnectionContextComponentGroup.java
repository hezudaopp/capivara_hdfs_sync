/*
 * capivara - Java File Synchronization
 *
 * Created on 11-Jun-2005
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
 * $Id: ConnectionContextComponentGroup.java,v 1.3 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ConnectionContextComponentGroup {

  private HashMap<Component, List<Integer>> compHash = new HashMap<Component, List<Integer>>();

  public ConnectionContextComponentGroup() {

  }

  public synchronized void addComponent(final Component comp, final int pluginID) {
    if (compHash.containsKey(comp)) {
      List<Integer> pluginList = compHash.get(comp);
      if (pluginList != null) {
        pluginList.add(new Integer(pluginID));
      }
    } else {
      List<Integer> pluginList = new ArrayList<Integer>();
      pluginList.add(new Integer(pluginID));
      compHash.put(comp, pluginList);
    }
  }

  public boolean isComponentEnabledForPluginID(final Component comp,
      final int pluginID) {
    boolean ret = false;
    if (compHash.containsKey(comp)) {
      List<Integer> pluginList = compHash.get(comp);
      if (pluginList != null) {
        Iterator<Integer> it = pluginList.iterator();
        while (it.hasNext()) {
          int pid = it.next();
          if (pid == pluginID) {
            ret = true;
            break;
          }
        }
      }
    }
    return ret;
  }

  public void enableComponentsForPluginID(final boolean enable,
      final int pluginID) {
    final Iterator<Component> it = compHash.keySet().iterator();
    while (it.hasNext()) {
      final Component c = it.next();
      if (isComponentEnabledForPluginID(c, pluginID)) {
        c.setEnabled(enable);
      } else {
        c.setEnabled(false);
      }
    }
  }

  public void disableAllComponents() {
    final Iterator<Component> it = compHash.keySet().iterator();
    while (it.hasNext()) {
      final Component c = it.next();
      c.setEnabled(false);
    }
  }

}
