/*
 * capivara - Java File Synchronization
 *
 * Created on 04-Mar-2006
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
 * $Id$
 */
package net.sf.jfilesync.event.types;

import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.event.TMessage;

public class QuickConnectMessage implements TMessage {

  private final ConnectionConfig config;
  private final boolean newTab;
  private final int ccIDSource;
  
  public QuickConnectMessage(final ConnectionConfig config,
      final int ccIDSource, final boolean newTab) {
    if (config == null) {
      throw new NullPointerException("config is null");
    }
    this.config = config;
    this.ccIDSource = ccIDSource;
    this.newTab = newTab;
  }
  
  public ConnectionConfig getConnectionConfig() {
    return config;
  }
  
  public boolean getNewTab() {
    return newTab;
  }
  
  public int getSourceCCId() {
    return ccIDSource;
  }

  public ID getMessageType() {
    return ID.QUICK_CONNECT_MESSAGE;
  }
  
}
