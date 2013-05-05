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
package net.sf.jfilesync.sync2.projects.nodes;

import net.sf.jfilesync.sync2.list.ExpressionList;
import net.sf.jfilesync.sync2.projects.ISyncProjectSavable2;
import net.sf.jfilesync.sync2.projects.AbstractSyncProjectNode;

public class SyncSettingsNode extends AbstractSyncProjectNode {

  public SyncSettingsNode() {
    super("SyncSettings");
  }

  public void setIsFatLeft(boolean isFatLeft) {
    storeValue("FatLeft", Boolean.toString(isFatLeft));
  }

  public void setIsFatRight(boolean isFatRight) {
    storeValue("FatRight", Boolean.toString(isFatRight));
  }

  public void setIsCaseSensitive(boolean caseSensitive) {
    storeValue("CaseSensitive", Boolean.toString(caseSensitive));
  }

  public void setIncludeList(ExpressionList includeList) {
    if( includeList != null ) {
      AbstractSyncProjectNode elementNode = new SyncElementNode("includelist");
      elementNode.addChild(includeList.save());
      addChild(elementNode);
    }
  }

  public void setExcludeList(ExpressionList excludeList) {
    if( excludeList != null ) {
      AbstractSyncProjectNode elementNode = new SyncElementNode("excludelist");
      elementNode.addChild(excludeList.save());
      addChild(elementNode);
    }
  }

  @Override
  public ISyncProjectSavable2 load() {
    // TODO Auto-generated method stub
    return null;
  }

}
