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

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.plugins.net.ConnectionProxy;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.sync2.projects.AbstractSyncProjectNode;
import net.sf.jfilesync.sync2.projects.ISyncProjectSavable2;
import net.sf.jfilesync.sync2.projects.SyncProjectSettings;
import net.sf.jfilesync.sync2.syncer.SyncSettings;

public class SyncProjectNode extends AbstractSyncProjectNode {

  public SyncProjectNode(final SyncSettings settings,
      final AbstractConnectionProxy con1, final String rootPath1,
      final AbstractConnectionProxy con2, final String rootPath2) {
    super("SyncProject");

    addChild(settings.save());

    final AbstractSyncProjectNode leftConNode = new SyncElementNode(
        "LeftConnection");
    leftConNode.addChild(con1.getPlugin().getTConnectionData().save());
    addChild(leftConNode);

    final AbstractSyncProjectNode rightConNode = new SyncElementNode(
        "RightConnection");
    rightConNode.addChild(con2.getPlugin().getTConnectionData().save());
    addChild(rightConNode);

    final AbstractSyncProjectNode leftRootNode = new SyncElementNode("LeftRoot");
    leftRootNode.storeValue("path", rootPath1);
    addChild(leftRootNode);

    final AbstractSyncProjectNode rightRootNode = new SyncElementNode("RightRoot");
    rightRootNode.storeValue("path", rootPath2);
    addChild(rightRootNode);

  }

  @Override
  public ISyncProjectSavable2 load() {
    final SyncSettingsNode settingsNode = (SyncSettingsNode) getChild(new SyncSettingsNode()
        .getId());
    final SyncSettings settings = (SyncSettings) settingsNode.load();

    final SyncElementNode con1Node = (SyncElementNode) getChild("LeftConnection");
    final TConnectionData con1 = (TConnectionData) con1Node.load();

    final SyncElementNode con2Node = (SyncElementNode) getChild("RightConnection");
    final TConnectionData con2 = (TConnectionData) con2Node.load();

    final SyncElementNode path1Node = (SyncElementNode) getChild("LeftRoot");
    final String rootPath1 = path1Node.getValue("path");

    final SyncElementNode path2Node = (SyncElementNode) getChild("RightRoot");
    final String rootPath2 = path2Node.getValue("path");

    final AbstractConnectionProxy con1Proxy = new ConnectionProxy(con1);
    final AbstractConnectionProxy con2Proxy = new ConnectionProxy(con2);

    return new SyncProjectSettings(settings, con1Proxy, rootPath1, con2Proxy,
        rootPath2);
  }

}
