/*
 * capivara - Java File Synchronization
 *
 * Created on 27-Mar-2005
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
 * $Id: ConnectionStateMessage.java,v 1.4 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.event.types;

import net.sf.jfilesync.event.TMessage;

/**
 * 
 * @author sascha
 *
 * notify the outside tabbed pane about the connection status 
 * of a TControlCenter
 */

public class ConnectionStateMessage implements TMessage {

  private final boolean connected;
  private final int ccID;

  public ConnectionStateMessage(final int ccID, final boolean connected) {
    this.ccID = ccID;
    this.connected = connected;
  }

  public int getControlCenterID() {
    return ccID;
  }

  public boolean isConnected() {
    return connected;
  }

  public ID getMessageType() {
    return ID.CONNECTION_STATE_MESSAGE;
  }
  
}
