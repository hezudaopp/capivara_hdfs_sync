/*
 * capivara - Java File Synchronization
 *
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
 * $Id: SyncActionTree.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.util.ArrayList;
import java.util.List;

public class SyncActionTree {

  private final SyncAction root;

  public SyncActionTree(final SyncAction root) {
    this.root = root;
  }

  public SyncAction[] getActionList() {
    final List<SyncAction> actionList = new ArrayList<SyncAction>();
    collectActionNodes(root, actionList);
    return actionList.toArray(new SyncAction[actionList.size()]);
  }

  protected void collectActionNodes(SyncAction actionNode, List<SyncAction> list) {
    if (actionNode != null) {
      list.add(actionNode);
      SyncAction[] childs = actionNode.getChildren();
      for (int i = 0; i < childs.length; i++) {
        collectActionNodes(childs[i], list);
      }
    }
  }

}
