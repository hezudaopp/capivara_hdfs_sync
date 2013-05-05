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

import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.sync2.projects.AbstractSyncProjectNode;
import net.sf.jfilesync.sync2.projects.ISyncProjectSavable2;

public class ConnectionDataSaveNode extends AbstractSyncProjectNode {

  public ConnectionDataSaveNode(String host, String user, int port,
      int protocolId) {
    super("ConnectionData");

    storeValue("host", host);
    storeValue("user", user);
    storeValue("port", Integer.toString(port));
    storeValue("protocolId", Integer.toString(protocolId));

  }

  @Override
  public ISyncProjectSavable2 load() {

    final String user = getValue("user");
    final String host = getValue("host");
    final int port = Integer.parseInt(getValue("port"));
    final int protocolId = Integer.parseInt(getValue("protocolId"));

    final TConnectionData conData = new TConnectionData(null, host, port, user,
        null, protocolId);

    if (getValue("caseInsensitive") != null) {
      final boolean caseInsensitive = Boolean
          .parseBoolean(getValue("caseInsensitive"));
      conData.setCaseInsensitive(caseInsensitive);
    }

    if (getValue("encoding") != null) {
      conData.setEncoding(getValue("encoding"));
    }

    return conData;
  }

  public void setCaseInsensitive(boolean caseInsensitive) {
    storeValue("caseInsensitive", Boolean.toString(caseInsensitive));
  }

  public void setEncoding(String encoding) {
    storeValue("encoding", encoding);
  }
}
